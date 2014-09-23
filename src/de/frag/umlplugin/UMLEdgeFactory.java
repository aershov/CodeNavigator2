package de.frag.umlplugin;

import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.EdgeCursor;
import com.intellij.openapi.graph.base.EdgeMap;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.layout.PortConstraintKeys;
import com.intellij.openapi.graph.layout.orthogonal.DirectedOrthogonalLayouter;
import com.intellij.openapi.graph.view.*;
import de.frag.umlplugin.psi.UsageType;
import de.frag.umlplugin.uml.graph.DataProviderKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

/**
 * Creates graph edges for UML diagrams.
 */
public class UMLEdgeFactory
{
  private static final String UML_ANGLE_ARROW_NAME = "angle";
  private static final Color  LABEL_BG_COLOR       = new Color (0x77FFFFFF, true);
  private static final Color  WHITE                = new Color (255, 255, 255, 0);

  private final Graph2D graph;
  private final EdgeMap edgeMap;
  private final EdgeMap directedEdgeMap;
  private final EdgeMap groupEdgeMap;

  public UMLEdgeFactory (@NotNull Graph2D graph)
  {
    this.graph           = graph;
    this.edgeMap         = graph.createEdgeMap ();
    this.directedEdgeMap = graph.createEdgeMap ();
    this.groupEdgeMap    = graph.createEdgeMap ();
    graph.addDataProvider (DataProviderKeys.DEPENDENCY_INFO_EDGE_KEY,      edgeMap);
    graph.addDataProvider (DirectedOrthogonalLayouter.DIRECTED_EDGE_DPKEY, directedEdgeMap);
    graph.addDataProvider (PortConstraintKeys.TARGET_GROUPID_KEY,          groupEdgeMap);
  }

  /**
   * Creates a new edge.
   * @param sourceNode source node
   * @param targetNode target node
   * @param usageType usage type for edge
   */
  public void createEdge (@NotNull Node sourceNode, @NotNull Node targetNode, @NotNull UsageType usageType)
  {
    for (EdgeCursor edgeCursor = sourceNode.edges (); edgeCursor.ok (); edgeCursor.next ())
    {
      Edge existingEdge = edgeCursor.edge ();
      if (existingEdge.target () == targetNode)
      {
        UsageType existingUsageType = (UsageType) edgeMap.get (existingEdge);
        // compare existing usage type with new usage type
        boolean newUsageTypeIsExtending = usageType == UsageType.EXTENDS ||
                                          usageType == UsageType.IMPLEMENTS;
        boolean oldUsageTypeIsExtending = existingUsageType == UsageType.EXTENDS ||
                                          existingUsageType == UsageType.IMPLEMENTS;
        if (newUsageTypeIsExtending)
        {
          if (oldUsageTypeIsExtending)
          {
            // do not create edge, if new edge is extending edge and extending edge already exists
            return;
          }
        }
        else
        {
          if (usageType.compareTo (existingUsageType) > 0)
          {
            // remove old edge, because new edge is "more important"
            existingEdge.getGraph ().removeEdge (existingEdge);
          }
          else if (!oldUsageTypeIsExtending)
          {
            // do not create edge, if old edge is not extending edge and new edge is "less important"
            return;
          }
        }
      }
    }
    Edge edge = graph.createEdge (sourceNode, targetNode);
    // provide information for layouter to be able to group implements and extends edges
    edgeMap.set (edge, usageType);
    if (usageType == UsageType.EXTENDS || usageType == UsageType.IMPLEMENTS)
    {
      String groupID = graph.getRealizer (targetNode).getLabelText ();
      groupEdgeMap.set (edge, groupID);
    }

    GraphManager graphManager = GraphManager.getGraphManager ();
    PolyLineEdgeRealizer edgeRealizer = graphManager.createPolyLineEdgeRealizer ();
    boolean directedEdge = configureEdge (usageType, edgeRealizer);
    graph.setRealizer (edge, edgeRealizer);
    directedEdgeMap.set (edge, directedEdge);
  }

  /**
   * Removes given edge from diagram.
   * @param edge edge to remove
   */
  public void removeEdge (@Nullable Edge edge)
  {
    edgeMap.set         (edge, null);
    directedEdgeMap.set (edge, null);
    groupEdgeMap.set    (edge, null);
    graph.removeEdge (edge);
  }


  /**
   * Configures given edge realizer.
   * @param usageType usage type to configure edge realizer for
   * @param edgeRealizer edge realizer to configure
   * @return true, if created edge is directed edge (directed edges will be layouted in a different way)
   */
  private boolean configureEdge (@NotNull UsageType usageType, @NotNull EdgeRealizer edgeRealizer)
  {
    GraphManager graphManager = GraphManager.getGraphManager ();
    Arrow customArrow = getUMLAngleArrow ();
    boolean directedEdge = false;
    switch (usageType)
    {
      case EXTENDS:
        edgeRealizer.setTargetArrow (Arrow.WHITE_DELTA);
        directedEdge = true;
        break;
      case IMPLEMENTS:
        edgeRealizer.setLineType (LineType.DASHED_1);
        edgeRealizer.setTargetArrow (Arrow.WHITE_DELTA);
        directedEdge = true;
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
    return directedEdge;
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
      customArrow = Arrow.Statics.addCustomArrow (UML_ANGLE_ARROW_NAME, arrowShape, WHITE);
    }
    return customArrow;
  }
}

