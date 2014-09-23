package de.frag.umlplugin.codenavigator.graph;

import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.DataProvider;
import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.geom.YDimension;
import com.intellij.openapi.graph.geom.YPoint;
import com.intellij.openapi.graph.layout.EdgeLayout;
import com.intellij.openapi.graph.layout.LayoutGraph;
import com.intellij.openapi.graph.layout.Layouter;
import com.intellij.openapi.graph.view.EdgeLabel;
import com.intellij.openapi.graph.view.EdgeRealizer;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Layouter that computes a layout for UML diagrams with only one class in the middle and several other
 * classes that use the central class or are used by the central class.
 */
public class GraphicalNavigationLayouter implements Layouter
{
  public boolean canLayout (@NotNull LayoutGraph graph)
  {
    return true;
  }

  public void doLayout (@NotNull LayoutGraph graph)
  {
    Settings settings = Settings.getSettings ();

    final DataProvider nodeMap = graph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
    Node[] nodes = graph.getNodeArray ();
    // first categorize nodes into 5 groups (using, used, extending, extended, center)
    Node centerNode = null;
    List<Node> usingNodes     = new ArrayList<Node> ();
    List<Node> usedNodes      = new ArrayList<Node> ();
    List<Node> extendingNodes = new ArrayList<Node> ();
    List<Node> extendedNodes  = new ArrayList<Node> ();
    for (Node node : nodes)
    {
      GraphBuilder.NodeInfo nodeInfo = (GraphBuilder.NodeInfo) nodeMap.get (node);
      DependencyType dependencyType = nodeInfo.getDependencyType ();
      switch (dependencyType)
      {
        case SUBJECT:
          centerNode = node;
          break;
        case USING:
          usingNodes.add (node);
          break;
        case USED:
          usedNodes.add (node);
          break;
        case EXTENDING:
          extendingNodes.add (node);
          break;
        case EXTENDED:
          extendedNodes.add (node);
          break;
        default:
          throw new IllegalStateException ("unknown dependency type " + dependencyType);
      }
    }
    // sort node within groups by name
    sortNodes (nodeMap, usingNodes);
    sortNodes (nodeMap, usedNodes);
    sortNodes (nodeMap, extendingNodes);
    sortNodes (nodeMap, extendedNodes);

    // compute dimensions of left, right, bottom and top node groups
    YDimension usingDimension     = computeDimension (graph, usingNodes,     false);
    YDimension usedDimension      = computeDimension (graph, usedNodes,      false);
    YDimension extendingDimension = computeDimension (graph, extendingNodes, true);
    YDimension extendedDimension  = computeDimension (graph, extendedNodes,  true);

    // compute maximum height of left and right and maximum width of bottom and top node groups
    double verticalSize = Math.max (usingDimension.getHeight (),   usedDimension.getHeight ());
    double centerWidth  = Math.max (settings.getMinNodeWidth (), graph.getWidth (centerNode));
    double centerHeight = Math.max (settings.getMinNodeHeight (), verticalSize - 2 * settings.getCenterGapV ());

    // set sizes of all nodes
    setWidths  (graph, usingNodes,     Math.max (settings.getMinNodeWidth (), usingDimension.getWidth ()));
    setWidths  (graph, usedNodes,      Math.max (settings.getMinNodeWidth (), usedDimension.getWidth ()));
    setHeights (graph, extendingNodes, extendingDimension.getHeight ());
    setHeights (graph, extendedNodes,  extendedDimension.getHeight ());
    graph.setSize (centerNode, centerWidth, centerHeight);

    // recompute dimensions of left, right, bottom and top node groups
    usingDimension     = computeDimension (graph, usingNodes,     false);
    usedDimension      = computeDimension (graph, usedNodes,      false);
    extendingDimension = computeDimension (graph, extendingNodes, true);
    extendedDimension  = computeDimension (graph, extendedNodes,  true);

    // compute size of inner rectangle
    double innerWidth  = 2 * settings.getCenterGapH () + graph.getWidth  (centerNode);
    double innerHeight = 2 * settings.getCenterGapV () + graph.getHeight (centerNode);

    // compute offsets for centering of node groups
    double usingOffset     = (innerHeight - usingDimension.getHeight ()) / 2;
    double usedOffset      = (innerHeight - usedDimension.getHeight  ()) / 2;
    double extendingOffset = extendingDimension.getWidth () / 2;
    double extendedOffset  = extendedDimension.getWidth  () / 2;

    // place nodes
    double usingX     = settings.getSmallGap ();
    double usingY     = settings.getSmallGap () + extendedDimension.getHeight () + settings.getBigGap () + usingOffset;
    double usedX      = settings.getSmallGap () + usingDimension.getWidth () + innerWidth;
    double usedY      = settings.getSmallGap () + extendedDimension.getHeight () + settings.getBigGap () + usedOffset;
    double extendingX = settings.getSmallGap () + usingDimension.getWidth () + innerWidth / 2 - extendingOffset;
    double extendingY = settings.getSmallGap () + extendedDimension.getHeight () + settings.getBigGap () + innerHeight + settings.getBigGap ();
    double extendedX  = settings.getSmallGap () + usingDimension.getWidth () + innerWidth / 2 - extendedOffset;
    double extendedY  = settings.getSmallGap ();
    double centerX    = settings.getSmallGap () + usingDimension.getWidth () + settings.getCenterGapH ();
    double centerY    = settings.getSmallGap () + extendedDimension.getHeight () + settings.getBigGap () + settings.getCenterGapV ();

    /*
    System.out.println ("---------------------------------------------");
    System.out.println ("smallGap:         " + smallGap);
    System.out.println ("bigGap:           " + bigGap);
    System.out.println ("centerGap:        " + centerGap);
    System.out.println ("inner width:      " + innerWidth);
    System.out.println ("inner height:     " + innerHeight);
    System.out.println ("using offset:     " + usingOffset);
    System.out.println ("used offset:      " + usedOffset);
    System.out.println ("extending offset: " + extendingOffset);
    System.out.println ("extended offset:  " + extendedOffset);
    System.out.println ("using width:      " + usingDimension.getWidth ());
    System.out.println ("using height:     " + usingDimension.getHeight ());
    System.out.println ("used width:       " + usedDimension.getWidth ());
    System.out.println ("used height:      " + usedDimension.getHeight ());
    System.out.println ("extending width:  " + extendingDimension.getWidth ());
    System.out.println ("extending height: " + extendingDimension.getHeight ());
    System.out.println ("extended width:   " + extendedDimension.getWidth ());
    System.out.println ("extended height:  " + extendedDimension.getHeight ());
    System.out.println ("center width:     " + centerWidth);
    System.out.println ("center height:    " + centerHeight);
    System.out.println ("usingX:           " + usingX);
    System.out.println ("usingY:           " + usingY);
    System.out.println ("usedX:            " + usedX);
    System.out.println ("usedX:            " + usedX);
    System.out.println ("extendingX:       " + extendingX);
    System.out.println ("extendingX:       " + extendingX);
    System.out.println ("extendedX:        " + extendedX);
    System.out.println ("extendedX:        " + extendedX);
    System.out.println ("centerX:          " + centerX);
    System.out.println ("centerX:          " + centerX);
    */

    placeNodes (graph, usingNodes,     usingX,     usingY,     false);
    placeNodes (graph, usedNodes,      usedX,      usedY,      false);
    placeNodes (graph, extendingNodes, extendingX, extendingY, true);
    placeNodes (graph, extendedNodes,  extendedX, extendedY,   true);
    graph.setLocation (centerNode, centerX, centerY);

    // layout edges
    layoutEdges (graph, centerNode, usingNodes, usingDimension, true);
    layoutEdges (graph, centerNode, usedNodes,  usedDimension,  false);

    layoutForkEdges (graph, centerNode, extendingNodes, true);
    layoutForkEdges (graph, centerNode, extendedNodes,  false);
  }

  /**
   * Computes edge layout.
   * @param graph graph that contains nodes
   * @param centerNode center node
   * @param nodes nodes
   * @param groupDimension bounding box of given nodes
   * @param nodesToCenter true, if edges point from nodes to center; false, if edges point from center to nodes
   */
  private void layoutEdges (@NotNull LayoutGraph graph, @NotNull Node centerNode, @NotNull List<Node> nodes,
                            @NotNull YDimension groupDimension, boolean nodesToCenter)
  {
    GraphManager graphManager = GraphManager.getGraphManager ();
    double centerWidth  = graph.getWidth  (centerNode);
    double centerHeight = graph.getHeight (centerNode);
    double nodeCount = nodes.size ();
    if (groupDimension.getHeight () > centerHeight)
    {
      double step = graph.getHeight (centerNode) / (nodeCount + 1);
      double y = step;
      for (Node node : nodes)
      {
        Edge edge = nodesToCenter ? node.firstOutEdge () : node.firstInEdge ();
        EdgeLayout layout = graph.getLayout (edge);
        layout.clearPoints ();
        if (nodesToCenter)
        {
          layout.setSourcePoint (graphManager.createYPoint (graph.getWidth (node) / 2, 0));
          layout.setTargetPoint (graphManager.createYPoint (-centerWidth / 2, -centerHeight / 2 + y));
        }
        else
        {
          layout.setSourcePoint (graphManager.createYPoint (centerWidth / 2, -centerHeight / 2 + y));
          layout.setTargetPoint (graphManager.createYPoint (-graph.getWidth (node) / 2, 0));
        }
        y += step;
        rotateLabels (graph, edge);
      }
    }
    else
    {
      for (Node node : nodes)
      {
        Edge edge = nodesToCenter ? node.firstOutEdge () : node.firstInEdge ();
        EdgeLayout layout = graph.getLayout (edge);
        layout.clearPoints ();
        double nodeWidth  = graph.getWidth  (node);
        double centerX = graph.getCenterX (centerNode);
        if (nodesToCenter)
        {
          graph.setSourcePointRel (edge, graphManager.createYPoint (nodeWidth / 2, 0));
          YPoint sourcePointAbs = graph.getSourcePointAbs (edge);
          graph.setTargetPointAbs (edge, graphManager.createYPoint (centerX - centerWidth / 2, sourcePointAbs.getY ()));
        }
        else
        {
          graph.setTargetPointRel (edge, graphManager.createYPoint (-nodeWidth / 2, 0));
          YPoint targetPointAbs = graph.getTargetPointAbs (edge);
          graph.setSourcePointAbs (edge, graphManager.createYPoint (centerX + centerWidth / 2, targetPointAbs.getY ()));
        }
      }
    }
  }

  /**
   * Rotates all labels to be aligned to edge.
   * @param graph graph containg edge
   * @param edge edge to rotate labels for
   */
  private void rotateLabels (@NotNull LayoutGraph graph, @NotNull Edge edge)
  {
    EdgeLayout layout = graph.getLayout (edge);
    if (layout instanceof EdgeRealizer)
    {
      EdgeRealizer edgeRealizer = (EdgeRealizer) layout;
      for (int i = 0; i < edgeRealizer.labelCount (); i++)
      {
        EdgeLabel edgeLabel = edgeRealizer.getLabel (i);
        YPoint sourcePoint = graph.getSourcePointAbs (edge);
        YPoint targetPoint = graph.getTargetPointAbs (edge);
        double deltaX = targetPoint.getX () - sourcePoint.getX ();
        double deltaY = targetPoint.getY () - sourcePoint.getY ();
        double angle = Math.toDegrees (Math.asin (deltaY / deltaX));
        edgeLabel.setRotationAngle (angle);
      }
    }
  }

  /**
   * Computes fork layout for nodes that point from below to center node.
   * @param graph graph
   * @param centerNode center node
   * @param nodes nodes
   * @param nodesToCenter true, if nodes point to center; false if center points to nodes
   */
  private void layoutForkEdges (@NotNull LayoutGraph graph, @NotNull Node centerNode, @NotNull List<Node> nodes,
                                boolean nodesToCenter)
  {
    Settings settings = Settings.getSettings ();

    GraphManager graphManager = GraphManager.getGraphManager ();
    YPoint centerPoint = graphManager.createYPoint (0, (nodesToCenter ? 1 : -1) * graph.getHeight (centerNode) / 2);
    for (Node node : nodes)
    {
      Edge edge = nodesToCenter ? node.firstOutEdge () : node.firstInEdge ();
      EdgeLayout edgeLayout = graph.getLayout (edge);
      edgeLayout.clearPoints ();
      double nodeHeight = graph.getHeight (node);
      if (nodesToCenter)
      {
        edgeLayout.setSourcePoint (graphManager.createYPoint (0, -nodeHeight / 2));
        edgeLayout.setTargetPoint (centerPoint);
        YPoint sourcePointAbs = graph.getSourcePointAbs (edge);
        YPoint targetPointAbs = graph.getTargetPointAbs (edge);
        edgeLayout.addPoint (sourcePointAbs.getX (), sourcePointAbs.getY () - settings.getForkLength ());
        edgeLayout.addPoint (targetPointAbs.getX (), sourcePointAbs.getY () - settings.getForkLength ());
      }
      else
      {
        edgeLayout.setSourcePoint (centerPoint);
        edgeLayout.setTargetPoint (graphManager.createYPoint (0, nodeHeight / 2));
        YPoint sourcePointAbs = graph.getSourcePointAbs (edge);
        YPoint targetPointAbs = graph.getTargetPointAbs (edge);
        edgeLayout.addPoint (sourcePointAbs.getX (), targetPointAbs.getY () + settings.getForkLength ());
        edgeLayout.addPoint (targetPointAbs.getX (), targetPointAbs.getY () + settings.getForkLength ());
      }
    }
  }

  /**
   * Sets widths of all given nodes to given width.
   * @param graph graph that contains given nodes
   * @param nodes nodes to set width for
   * @param width new node width
   */
  private void setWidths (@NotNull LayoutGraph graph, @NotNull List<Node> nodes, double width)
  {
    for (Node node : nodes)
    {
      graph.setSize (node, width, graph.getHeight (node));
    }
  }

  /**
   * Sets heights of all given nodes to given height.
   * @param graph graph that contains given nodes
   * @param nodes nodes to set height for
   * @param height new node height
   */
  private void setHeights (@NotNull LayoutGraph graph, @NotNull List<Node> nodes, double height)
  {
    for (Node node : nodes)
    {
      graph.setSize (node, graph.getWidth (node), height);
    }
  }

  /**
   * Places all nodes in given collection
   * @param graph graph that contains given nodes
   * @param nodes nodes to place
   * @param offsetX start offset in x-direction
   * @param offsetY start offset in y-direction
   * @param placeHorizontally true, if nodes will be placed horizontally; false if they will be placed vertically
   */
  private void placeNodes (@NotNull LayoutGraph graph, @NotNull List<Node> nodes, double offsetX, double offsetY,
                           boolean placeHorizontally)
  {
    Settings settings = Settings.getSettings ();
    for (Node node : nodes)
    {
      graph.setLocation (node, offsetX, offsetY);
      if (placeHorizontally)
      {
        offsetX += graph.getWidth (node) + settings.getSmallGap ();
      }
      else
      {
        offsetY += graph.getHeight (node) + settings.getSmallGap ();
      }
    }
  }

  /**
   * Sorts given nodes by class name
   * @param nodeMap node map that contains additional node information
   * @param nodes list of nodes to be sorted
   */
  private void sortNodes (@NotNull final DataProvider nodeMap, @NotNull List<Node> nodes)
  {
    Comparator<Node> nodeComparator = new Comparator<Node> () {
      public int compare (Node node1, Node node2)
      {
        GraphBuilder.NodeInfo info1 = (GraphBuilder.NodeInfo) nodeMap.get (node1);
        GraphBuilder.NodeInfo info2 = (GraphBuilder.NodeInfo) nodeMap.get (node2);
        String name1 = info1.getPsiClass ().getName ();
        String name2 = info2.getPsiClass ().getName ();
        return name1 == null ? -1 : name1.compareTo (name2);
      }
    };
    Collections.sort (nodes, nodeComparator);
  }

  /**
   * Computes 2-dimensional size of given node list.
   * @param graph graph that contains nodes
   * @param nodes nodes to compute size for
   * @param placeHorizontally true, if nodes will be placed horizontally; false if they will be placed vertically
   * @return computed size
   */
  private @NotNull YDimension computeDimension (@NotNull LayoutGraph graph, @NotNull List<Node> nodes,
                                                boolean placeHorizontally)
  {
    Settings settings = Settings.getSettings ();
    double width  = 0;
    double height = 0;
    for (Node node : nodes)
    {
      if (placeHorizontally)
      {
        width += (width > 0 ? settings.getSmallGap () : 0) + graph.getWidth (node);
        height = Math.max (height, graph.getHeight (node));
      }
      else
      {
        width = Math.max (width, graph.getWidth (node));
        height += (height > 0 ? settings.getSmallGap () : 0) + graph.getHeight (node);
      }
    }
    GraphManager graphManager = GraphManager.getGraphManager ();
    return graphManager.createYDimension (width, height);
  }
}
