package de.frag.umlplugin.codenavigator.graph;

import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.EdgeMap;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.base.NodeMap;
import com.intellij.openapi.graph.layout.Layouter;
import com.intellij.openapi.graph.view.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiNamedElement;
import de.frag.umlplugin.psi.*;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Builds simple UML graphs for a single central class and several other classes around this central class.
 */
public class GraphBuilder
{
  private static final String UML_ANGLE_ARROW_NAME = "angle";
  private static final Color  LABEL_BG_COLOR       = new Color (0x77FFFFFF, true);

  private Graph2D graph2D;
  private NodeMap nodeMap;
  private EdgeMap edgeMap;

  /**
   * Creates a new graph for the given class.
   * @param psiClass new central class
   * @return created graph
   */
  public @NotNull Graph2D createGraph (@NotNull PsiClass psiClass)
  {
    GraphManager graphManager = GraphManager.getGraphManager ();
    graph2D = graphManager.createGraph2D ();
    nodeMap = graph2D.createNodeMap ();
    edgeMap = graph2D.createEdgeMap ();
    graph2D.addDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY, nodeMap);
    graph2D.addDataProvider (DataProviderKeys.DEPENDENCY_INFO_EDGE_KEY, edgeMap);

    DependencyAnalyzer analyzer = new DependencyAnalyzer (psiClass, Settings.getSettings ());

    DependencyCollection usedClasses      = analyzer.getUsedClasses ();
    DependencyCollection usingClasses     = analyzer.getUsingClasses ();
    DependencyCollection extendedClasses  = analyzer.getExtendedClasses ();
    DependencyCollection extendingClasses = analyzer.getExtendingClasses ();

    Node classNode = createClassNode (psiClass, DependencyType.SUBJECT);
    NodeRealizer realizer = graph2D.getRealizer (classNode);
    realizer.setSize (realizer.getWidth () + 50, realizer.getHeight () + 100);

    Set<PsiClass> cyclicDependendClasses = computeCyclicDependencies (usedClasses,  usingClasses);

    addDependencies (classNode, usedClasses,      DependencyType.USED,      true,  cyclicDependendClasses);
    addDependencies (classNode, usingClasses,     DependencyType.USING,     false, cyclicDependendClasses);
    addDependencies (classNode, extendedClasses,  DependencyType.EXTENDED,  true,  cyclicDependendClasses);
    addDependencies (classNode, extendingClasses, DependencyType.EXTENDING, false, cyclicDependendClasses);
    
    Layouter layouter = new GraphicalNavigationLayouter ();
    layouter.doLayout (graph2D);

    return graph2D;
  }

  /**
   * Computes cyclic dependend classes.
   * @param usedClasses used classes
   * @param usingClasses using classes
   * @return set of classes that are both used by subject class and are using the subject class
   */
  private @NotNull Set<PsiClass> computeCyclicDependencies (@NotNull DependencyCollection usedClasses,
                                                            @NotNull DependencyCollection usingClasses)
  {
    Set<PsiClass> usingClassesSet = new HashSet<PsiClass> ();
    for (PsiClass usingClass : usingClasses)
    {
      usingClassesSet.add (usingClass);
    }
    Set<PsiClass> result = new HashSet<PsiClass> ();
    for (PsiClass usedClass : usedClasses)
    {
      if (usingClassesSet.contains (usedClass))
      {
        result.add (usedClass);
      }
    }
    return result;
  }

  /**
   * Adds nodes and edges for given dependencies.
   * @param subjectNode subject node
   * @param collection collection of dependencies
   * @param dependencyType type of dependencies
   * @param subjectIsSourceNode true, if subject is source node; false otherwise
   * @param cyclicDependendClasses set of classes that both use subject class and are used by subject class
   */
  private void addDependencies (@NotNull Node subjectNode, @NotNull DependencyCollection collection,
                                @NotNull DependencyType dependencyType,
                                boolean subjectIsSourceNode, @NotNull Set<PsiClass> cyclicDependendClasses)
  {
    NodeInfo info = (NodeInfo) nodeMap.get (subjectNode);
    PsiClass subjectClass = info.getPsiClass ();
    for (PsiClass dependendClass : collection)
    {
      if (!dependendClass.equals (subjectClass))
      {
        Node dependendNode = createClassNode (dependendClass, dependencyType);
        List<DependencyReason> filteredReasons = ClassFinder.filterReasons (collection.getDependencyReasons (dependendClass));
        for (DependencyReason reason : filteredReasons)
        {
          boolean cycle = (dependencyType == DependencyType.USED || dependencyType == DependencyType.USING) &&
                          cyclicDependendClasses.contains (dependendClass) &&
                          Settings.getSettings ().isHightlightCyclicEdges ();
          if (subjectIsSourceNode)
          {
            createEdge (subjectNode, dependendNode, dependendClass, reason.getOffset (), reason.getUsageType (),
                        dependencyType, cycle);
          }
          else
          {
            createEdge (dependendNode, subjectNode, dependendClass, reason.getOffset (), reason.getUsageType (),
                        dependencyType, cycle);
          }
        }
      }
    }
  }

  /**
   * Creates a new node for given class
   * @param psiClass class to create node for
   * @param dependencyType dependency type
   * @return created node
   */
  private @NotNull Node createClassNode (@NotNull PsiClass psiClass, @NotNull DependencyType dependencyType)
  {
    GraphManager graphManager = GraphManager.getGraphManager ();
    ShapeNodeRealizer nodeRealizer = graphManager.createShapeNodeRealizer (ShapeNodeRealizer.ROUND_RECT);
    Node classNode = graph2D.createNode (nodeRealizer);
    NodeInfo info = new NodeInfo (psiClass, dependencyType);
    nodeMap.set (classNode, info);
    ClassType classType = DependencyAnalyzer.computeClassType (psiClass);
    nodeRealizer.setFillColor (Settings.getSettings ().getColorForClassType (classType));
    nodeRealizer.setLabelText (createNodeText (psiClass, classType));
    NodeLabel label = nodeRealizer.getLabel ();
    nodeRealizer.setLocation (100, 100);
    nodeRealizer.setSize (label.getWidth () + 10, label.getHeight () + 5);
    return classNode;
  }

  /**
   * Creates a node label for given class.
   * @param psiClass class to create label for
   * @param classType class type
   * @return created label text
   */
  private @NotNull String createNodeText (@NotNull PsiNamedElement psiClass, @NotNull ClassType classType)
  {
    String name = psiClass.getName ();
    switch (classType)
    {
      case CLASS:
        return name;
      case ABSTRACT_CLASS:
        return "<html><div style='font-style:italic'>" + name + "</div></html>";
      case INTERFACE:
        return "<html><center><font size='-2'>&laquo;interface&raquo;</font><div>" + name + "</div></center</html>";
      case ENUM:
        return "<html><center><font size='-2'>&laquo;enum&raquo;</font><div>" + name + "</div></center</html>";
      default:
        throw new IllegalStateException ("unknown class type: " + classType);
    }
  }

  /**
   * Creates a new edge.
   * @param sourceNode source node
   * @param targetNode target node
   * @param psiClass associated class
   * @param offset offset within source
   * @param usageType usage type for edge
   * @param dependencyType dependency type
   * @param cyclic true, if edge should be highlighted as cyclic edge; false otherwise
   */
  private void createEdge (@NotNull Node sourceNode, @NotNull Node targetNode, @NotNull PsiClass psiClass,
                                    int offset, @NotNull UsageType usageType, @NotNull DependencyType dependencyType,
                                    boolean cyclic)
  {
    Edge edge = graph2D.createEdge (sourceNode, targetNode);
    EdgeInfo info = new EdgeInfo (psiClass, offset, dependencyType);
    edgeMap.set (edge, info);
    GraphManager graphManager = GraphManager.getGraphManager ();
    PolyLineEdgeRealizer edgeRealizer = graphManager.createPolyLineEdgeRealizer ();
    Arrow customArrow = getUMLAngleArrow ();
    switch (usageType)
    {
      case EXTENDS:
        edgeRealizer.setTargetArrow (Arrow.WHITE_DELTA);
        break;
      case IMPLEMENTS:
        edgeRealizer.setLineType (LineType.DASHED_1);
        edgeRealizer.setTargetArrow (Arrow.WHITE_DELTA);
        break;
      case NEW_EXPRESSION:
        edgeRealizer.setLineType (LineType.DASHED_1);
        edgeRealizer.setTargetArrow (customArrow);
        EdgeLabel newEdgeLabel = graphManager.createEdgeLabel ("<html>&laquo;create&raquo;</html>");
        newEdgeLabel.setModel           (EdgeLabel.THREE_CENTER);
        newEdgeLabel.setPosition        (EdgeLabel.CENTER);
        newEdgeLabel.setDistance        (0);
        newEdgeLabel.setBackgroundColor (LABEL_BG_COLOR);
        edgeRealizer.addLabel (newEdgeLabel);
        break;
      case REFERENCE:
        edgeRealizer.setLineType (LineType.DASHED_1);
        edgeRealizer.setTargetArrow (customArrow);
        break;
      case STATIC_REFERENCE:
        edgeRealizer.setLineType (LineType.DASHED_1);
        edgeRealizer.setTargetArrow (customArrow);
        break;
      case FIELD_TYPE_MANY:
        edgeRealizer.setSourceArrow (Arrow.DIAMOND);
        edgeRealizer.setTargetArrow (customArrow);
        EdgeLabel manyEdgeLabel = graphManager.createEdgeLabel ("*");
        manyEdgeLabel.setModel           (EdgeLabel.THREE_CENTER);
        manyEdgeLabel.setPosition        (EdgeLabel.TCENTR);
        manyEdgeLabel.setDistance        (0);
        manyEdgeLabel.setBackgroundColor (LABEL_BG_COLOR);
        edgeRealizer.addLabel (manyEdgeLabel);
        break;
      case FIELD_TYPE_ONE:
        edgeRealizer.setSourceArrow (Arrow.DIAMOND);
        edgeRealizer.setTargetArrow (customArrow);
        EdgeLabel oneEdgeLabel = graphManager.createEdgeLabel ("1");
        oneEdgeLabel.setModel           (EdgeLabel.THREE_CENTER);
        oneEdgeLabel.setPosition        (EdgeLabel.TCENTR);
        oneEdgeLabel.setDistance        (0);
        oneEdgeLabel.setBackgroundColor (LABEL_BG_COLOR);
        edgeRealizer.addLabel (oneEdgeLabel);
        break;
    }
    edgeRealizer.setLineColor (cyclic ? Color.RED: Color.BLACK);
    graph2D.setRealizer (edge, edgeRealizer);
  }


  /**
   * Gets a UML angle arrow. If it does not exist already, it will be created and registered; otherwise
   * it will simply be returned.
   * @return cached or created arrow
   */
  private @NotNull Arrow getUMLAngleArrow ()
  {
    Arrow customArrow = Arrow.Statics.getCustomArrow (UML_ANGLE_ARROW_NAME);
    if (customArrow == null)
    {
      Path2D.Float arrowShape = new GeneralPath ();
      arrowShape.moveTo (-8, -5);
      arrowShape.lineTo (0, 0);
      arrowShape.lineTo (-8, 5);
      customArrow = Arrow.Statics.addCustomArrow (UML_ANGLE_ARROW_NAME, arrowShape, new Color (255, 255, 255, 0));
    }
    return customArrow;
  }

  /**
   * Stores additional node information.
   */
  public static class NodeInfo
  {
    private final PsiClass       psiClass;
    private final DependencyType dependencyType;

    public NodeInfo (@NotNull PsiClass psiClass, @NotNull DependencyType dependencyType)
    {
      this.psiClass = psiClass;
      this.dependencyType = dependencyType;
    }

    public @NotNull PsiClass getPsiClass ()
    {
      return psiClass;
    }

    public @NotNull DependencyType getDependencyType ()
    {
      return dependencyType;
    }
  }

  /**
   * Stores additional edge information.
   */
  public static class EdgeInfo
  {
    private final PsiClass       psiClass;
    private final int            offset;
    private final DependencyType dependencyType;

    public EdgeInfo (@NotNull PsiClass psiClass, int offset, @NotNull DependencyType dependencyType)
    {
      this.psiClass = psiClass;
      this.offset = offset;
      this.dependencyType = dependencyType;
    }

    public @NotNull PsiClass getPsiClass ()
    {
      return psiClass;
    }

    public int getOffset ()
    {
      return offset;
    }

    public @NotNull DependencyType getDependencyType ()
    {
      return dependencyType;
    }
  }
}
