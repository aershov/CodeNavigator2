package de.frag.umlplugin.uml;

import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.*;
import com.intellij.openapi.graph.layout.BufferedLayouter;
import com.intellij.openapi.graph.layout.GraphLayout;
import com.intellij.openapi.graph.layout.LayoutOrientation;
import com.intellij.openapi.graph.layout.orthogonal.DirectedOrthogonalLayouter;
import com.intellij.openapi.graph.view.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.UMLEdgeFactory;
import de.frag.umlplugin.history.HistoryList;
import de.frag.umlplugin.psi.*;
import de.frag.umlplugin.settings.Settings;
import de.frag.umlplugin.uml.command.AbstractDiagramCommand;
import de.frag.umlplugin.uml.command.DiagramCommand;
import de.frag.umlplugin.uml.command.DiagramCommandFactory;
import de.frag.umlplugin.uml.graph.DataProviderKeys;
import de.frag.umlplugin.uml.graph.Graph2DViewBuilder;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * UML diagram.
 */
public class UMLDiagram implements Comparable<UMLDiagram>, Iterable<String>
{
  private static final int    MIN_NODE_HEIGHT  = 60;
  private static final int    MIN_NODE_WIDTH   = 120;
  private static final int    MAX_HISTORY_SIZE = 100;

  public static final String DIAGRAM_ELEMENT_NAME   = "diagram";
  public static final String DIAGRAM_NAME_ATTRIBUTE = "name";

  private final Project                     project;
  private       String                      name;
  private final Graph2D                     graph;
  private final Graph2DView                 view;
  private final NodeMap                     nodeMap;
  private final HistoryList<DiagramCommand> commands        = new HistoryList<DiagramCommand> (MAX_HISTORY_SIZE);
  private final Map<String, Node>           classNameToNode = new HashMap<String, Node> ();
  private final UMLEdgeFactory              edgeFactory;
  private       CompartmentVisibility       compartmentVisibility;

  /**
   * Creates new UML diagram.
   * @param project associated project
   * @param name diagram name
   */
  public UMLDiagram (@NotNull Project project, @NotNull String name)
  {
    GraphManager graphManager = GraphManager.getGraphManager ();
    this.project               = project;
    this.name                  = name;
    this.graph                 = graphManager.createGraph2D ();
    this.view                  = Graph2DViewBuilder.createGraph2DView (project);
    this.nodeMap               = graph.createNodeMap ();
    this.compartmentVisibility = new CompartmentVisibility ();
    this.edgeFactory           = new UMLEdgeFactory (graph);
    graph.addDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY, nodeMap);
    view.setGraph2D (graph);
  }

  /**
   * Creates a new diagram from given JDOM element
   * @param project current project
   * @param diagramElement JDOM element that contains persistent information about diagram
   */
  @SuppressWarnings ({"unchecked"})
  public UMLDiagram (@NotNull Project project, @NotNull Element diagramElement)
  {
    this (project, diagramElement.getAttributeValue (DIAGRAM_NAME_ATTRIBUTE));
    List<Element> commandElements = diagramElement.getChildren (AbstractDiagramCommand.COMMAND_ELEMENT_NAME);
    for (Element commandElement : commandElements)
    {
      DiagramCommand command = DiagramCommandFactory.createCommand (commandElement);
      if (command != null)
      {
        commands.add (command);
      }
    }
    Element visibilityElement = diagramElement.getChild (CompartmentVisibility.COMPARTMENT_VISIBILITY);
    if (visibilityElement != null)
    {
      compartmentVisibility = new CompartmentVisibility (visibilityElement);
    }
  }

  /**
   * Gets a copy of the diagram creation command list.
   * @return flat copy of diagram creation command list
   */
  public @NotNull List<DiagramCommand> getCommands ()
  {
    return commands.toList (true);
  }

  /**
   * Sets the command history to given command list.
   * @param commands list of commands to use as history
   */
  public void setCommands (@NotNull List<DiagramCommand> commands)
  {
    this.commands.clear ();
    for (DiagramCommand command : commands)
    {
      this.commands.add (command);
    }
  }

  /**
   * Gets diagram name.
   * @return diagram name
   */
  public @NotNull String getName ()
  {
    return name;
  }

  /**
   * Sets name of this diagram.
   * @param name new diagram name
   */
  public void setName (@NotNull String name)
  {
    this.name = name;
  }

  /**
   * Gets contained graph.
   * @return contained graph
   */
  public @NotNull Graph getGraph ()
  {
    return graph;
  }

  /**
   * Gets contained graph view.
   * @return contained graph view
   */
  public @NotNull Graph2DView getView ()
  {
    return view;
  }

  /**
   * Gets compartment visibility handler.
   * @return compartment visibility handler
   */
  public @NotNull CompartmentVisibility getCompartmentVisibility ()
  {
    return compartmentVisibility;
  }

  /**
   * Adds given command to diagram.
   * @param command command to add
   */
  public void addCommand (@NotNull DiagramCommand command)
  {
    if (command.canExecute (this))
    {
      commands.add (command);
      command.execute (this);
    }
  }

  /**
   * Steps back to start of diagram command history.
   */
  public void rewindAllCommands ()
  {
    commands.rewind ();
    refreshDiagram ();
  }

  /**
   * Clears all graph contents.
   */
  private void clearGraph ()
  {
    for (Node node : graph.getNodeArray ())
    {
      graph.removeNode (node);
    }
    classNameToNode.clear ();
  }

  /**
   * Steps back in diagram creation by removing last command.
   */
  public void stepBack ()
  {
    if (canStepBack ())
    {
      commands.stepBack ();
      refreshDiagram ();
    }
  }

  /**
   * Steps forward in diagram creation by reappending last command.
   */
  public void stepForward ()
  {
    if (canStepForward ())
    {
      commands.stepForward ();
      refreshDiagram ();
    }
  }

  /**
   * Checks whether a step back in diagram creation history is possible.
   * @return true, if a step back is possible; false otherwise
   */
  public boolean canStepBack ()
  {
    return commands.canStepBack ();
  }

  /**
   * Checks whether a step forward in diagram creation history is possible.
   * @return true, if a step forward is possible; false otherwise
   */
  public boolean canStepForward ()
  {
    return commands.canStepForward ();
  }

  /**
   * Refreshes the diagram by excuting all commands.
   */
  public void refreshDiagram ()
  {
    clearGraph ();
    for (DiagramCommand command : commands)
    {
      command.execute (this);
    }
  }

  /**
   * Recomputes layout of this diagram.
   */
  public void doLayout ()
  {
    doLayout (true);
  }

  /**
   * Recomputes layout of this diagram.
   * @param animate true, if layout ill be animated; false otherwise
   */
  public void doLayout (boolean animate)
  {
    GraphManager graphManager = GraphManager.getGraphManager ();
    DirectedOrthogonalLayouter layouter = graphManager.createDirectedOrthogonalLayouter ();
    layouter.setGrid (Settings.getSettings ().getGridSize ());
    layouter.setLayoutOrientation (LayoutOrientation.BOTTOM_TO_TOP);

    if (Settings.getSettings ().isAnimateNavigation () && animate)
    {
      BufferedLayouter bufferedLayouter = graphManager.createBufferedLayouter (layouter);
      GraphLayout layoutedGraph = bufferedLayouter.calcLayout (graph);
      LayoutMorpher layoutMorpher = graphManager.createLayoutMorpher (view, layoutedGraph);
      layoutMorpher.execute ();
    }
    else
    {
      layouter.doLayout (graph);
    }
  }

  /**
   * Creates a new node for given class and adds it to the diagram.
   * @param psiClass class to create node for
   * @return created node
   */
  public @Nullable Node addClassNode (@Nullable PsiClass psiClass)
  {
    if (psiClass == null)
    {
      return null;
    }
    GraphManager graphManager = GraphManager.getGraphManager ();
    ShapeNodeRealizer nodeRealizer = graphManager.createShapeNodeRealizer (ShapeNodeRealizer.ROUND_RECT);
    Node classNode = graph.createNode (nodeRealizer);
    classNameToNode.put (psiClass.getQualifiedName (), classNode);
    NodeInfo info = new NodeInfo (psiClass);
    nodeMap.set (classNode, info);
    ClassType classType = DependencyAnalyzer.computeClassType (psiClass);
    nodeRealizer.setFillColor (Settings.getSettings ().getColorForClassType (classType));
    String name = psiClass.getQualifiedName ();
    boolean fieldsVisible  = compartmentVisibility.isFieldsVisible (name);
    boolean methodsVisible = compartmentVisibility.isMethodsVisible (name);
    String nodeText = ClassPresentation.renderHtml (psiClass, classType, fieldsVisible, methodsVisible);
    nodeRealizer.setLabelText (nodeText);
    NodeLabel label = nodeRealizer.getLabel ();
    nodeRealizer.setLocation (MAX_HISTORY_SIZE, MAX_HISTORY_SIZE);
    int width = (int) label.getWidth ();
    nodeText = nodeText.replace ("width='" + ClassPresentation.MAGIC_WIDTH +"'", "width='" + width + "'");
    nodeRealizer.setLabelText (nodeText);
    nodeRealizer.setSize (Math.max (label.getWidth  () + 10, MIN_NODE_WIDTH),
                          Math.max (label.getHeight () + 10, MIN_NODE_HEIGHT));
    return classNode;
  }

  /**
   * Removes given class node from diagram.
   * @param classNode node to remove
   */
  public void removeClassNode (@NotNull Node classNode)
  {
    NodeInfo nodeInfo = (NodeInfo) nodeMap.get (classNode);
    PsiClass psiClass = nodeInfo.getPsiClass ();
    classNameToNode.remove (psiClass.getQualifiedName ());
    nodeMap.set (classNode, null);
    graph.removeNode (classNode);
  }

  /**
   * Creates a new edge.
   * @param sourceNode source node
   * @param targetNode target node
   * @param usageType usage type for edge
   */
  public void createEdge (@Nullable Node sourceNode, @Nullable Node targetNode, @NotNull UsageType usageType)
  {
    if (sourceNode == null || targetNode == null)
    {
      return;
    }
    edgeFactory.createEdge (sourceNode, targetNode, usageType);
  }

  /**
   * Removes given edge from diagram.
   * @param edge edge to remove
   */
  public void removeEdge (@Nullable Edge edge)
  {
    edgeFactory.removeEdge (edge);
  }

  /**
   * Gets node that belongs to given class.
   * @param psiClass class
   * @return node that belongs to given class or null, if no node belongs to class
   */
  public @Nullable Node getNode (@NotNull PsiClass psiClass)
  {
    return classNameToNode.get (psiClass.getQualifiedName ());
  }

  /**
   * Gets node that belongs to given class name.
   * @param qualifiedClassName class name
   * @return node that belongs to given class name or null, if no node belongs to class name
   */
  public @Nullable Node getNode (@NotNull String qualifiedClassName)
  {
    return classNameToNode.get (qualifiedClassName);
  }

  /**
   * Gets edge that belongs to given arguments.
   * @param sourceClassName source class of edge
   * @param targetClassName target class of edge
   * @param usageType usage type of edge
   * @return found edge or null, if edge could not be found
   */
  public @Nullable Edge getEdge (@NotNull String sourceClassName, @NotNull String targetClassName,
                                 @NotNull UsageType usageType)
  {
    for (Edge edge : graph.getEdgeArray ())
    {
      Node sourceNode = edge.source ();
      Node targetNode = edge.target ();
      PsiClass  edgeSourceClass = UMLDiagram.getPsiClass (sourceNode);
      PsiClass  edgeTargetClass = UMLDiagram.getPsiClass (targetNode);
      UsageType edgeUsageType   = UMLDiagram.getUsageType (edge);
      if (edgeSourceClass != null && edgeTargetClass != null &&
          sourceClassName.equals (edgeSourceClass.getQualifiedName ()) &&
          targetClassName.equals (edgeTargetClass.getQualifiedName ()) &&
          usageType == edgeUsageType)
      {
        return edge;
      }
    }
    return null;
  }

  /**
   * Gets all edges.
   * @return list of all edges
   */
  public @NotNull List<Edge> getEdges ()
  {
    return new ArrayList<Edge> (Arrays.asList (graph.getEdgeArray ()));
  }

  public String toString ()
  {
    return name;
  }

  /**
   * Gets psi class for given node.
   * @param node node to get class for
   * @return found class or null, if no class could be found
   */
  public static @Nullable PsiClass getPsiClass (@NotNull Node node)
  {
    Graph graph = node.getGraph ();
    DataProvider nodeMap = graph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
    NodeInfo nodeInfo = (NodeInfo) nodeMap.get (node);
    return nodeInfo != null ? nodeInfo.getPsiClass () : null;
  }

  /**
   * Gets usage type for given edge.
   * @param edge edge to get usage type for
   * @return found usage type or null, if no usage type could be found
   */
  public static @Nullable UsageType getUsageType (@NotNull Edge edge)
  {
    Graph graph = edge.getGraph ();
    DataProvider edgeMap = graph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_EDGE_KEY);
    return (UsageType) edgeMap.get (edge);
  }

  /**
   * Finds psi class for given qualified class name.
   * @param qualifiedClassName qualified class name
   * @return found psi class or null, if no psi class with given qualified name could be found
   */
  public @Nullable PsiClass findPsiClass (@NotNull String qualifiedClassName)
  {
    return ClassFinder.findPsiClass (project, qualifiedClassName);
  }

  public boolean equals (@Nullable Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass () != o.getClass ())
    {
      return false;
    }
    UMLDiagram diagram = (UMLDiagram) o;
    return name.equals (diagram.name);
  }

  public int hashCode ()
  {
    return name.hashCode ();
  }

  public int compareTo (@NotNull UMLDiagram other)
  {
    return this.name.compareTo (other.name);
  }

  /**
   * Creates JDOM element that contains a persistent description of this diagram.
   * @return created JDOM element
   */
  public @NotNull Element createElement ()
  {
    Element diagramElement = new Element (DIAGRAM_ELEMENT_NAME);
    diagramElement.setAttribute (DIAGRAM_NAME_ATTRIBUTE, name);
    for (DiagramCommand command : commands)
    {
      Element commandElement = command.createElement ();
      diagramElement.addContent (commandElement);
    }
    diagramElement.addContent (compartmentVisibility.createElement ());
    return diagramElement;
  }

  /**
   * Returns an iterator over all contained classes.
   * @return iterator that iterates over all contained classes.
   */
  public @NotNull Iterator<String> iterator ()
  {
    return classNameToNode.keySet ().iterator ();
  }

  /**
   * Rename all classes in all commands to keep class names in sync after refactorings were applied.
   * @param renamer renamer to use for renaming class names
   */
  public void renameClasses (@NotNull ClassRenamer renamer)
  {
    for (DiagramCommand command : commands)
    {
      command.renameClasses (renamer);
    }
    compartmentVisibility.renameClasses (renamer);
  }

  /**
   * Stores additional node information.
   */
  public static class NodeInfo
  {
    private final PsiClass psiClass;

    public NodeInfo (@NotNull PsiClass psiClass)
    {
      this.psiClass = psiClass;
    }

    public @NotNull PsiClass getPsiClass ()
    {
      return psiClass;
    }
  }
}
