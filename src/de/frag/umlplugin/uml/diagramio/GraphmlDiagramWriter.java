package de.frag.umlplugin.uml.diagramio;

import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.view.*;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.graph.EscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA. User: Frank.Gerberding Date: 23.10.2008 Time: 10:13:12 To change this template use File |
 * Settings | File Templates.
 */
public class GraphmlDiagramWriter implements DiagramWriter
{
  private static final String PROLOG =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\" xmlns:y=\"http://www.yworks.com/xml/graphml\" " +
    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
    "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml " +
    "http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\">\n" +
    "  <key id=\"d0\" for=\"node\" yfiles.type=\"nodegraphics\"/>\n" +
    "  <key id=\"d1\" for=\"edge\" yfiles.type=\"edgegraphics\"/>\n" +
    "  <graph id=\"G\" edgedefault=\"directed\">\n";

  private static final String EPILOG =
    "  </graph>\n" +
    "</graphml>";

  /**
   * Checks whether this writer can write multiple diagrams or not.
   * @return true, if this writer can write multiple diagrams; false, if it can write only one diagram
   */
  public boolean canWriteMultipleDiagrams ()
  {
    return false;
  }

  /**
   * Checks whether this writer supports storage of image thumbnails.
   * @return true, if this writer can create thumbnail images; false otherwise
   */
  public boolean canWriteThumbnail ()
  {
    return false;
  }

  /**
   * Writes given diagram to file.
   * @param diagrams array of diagrams to write
   * @param path     path to target file
   * @param saveThumbnail true, if thumbnail should be written; false otherwise
   * @param thumbnailHeight desired thumbnail height in pixels
   * @throws java.io.IOException on IO error
   */
  public void writeDiagram (@NotNull UMLDiagram[] diagrams, @NotNull String path, boolean saveThumbnail,
                            int thumbnailHeight) throws IOException
  {
    Graph2DView view = diagrams [0].getView ();
    Graph2D graph = view.getGraph2D ();

    PrintWriter pw = new PrintWriter (path);
    pw.print (PROLOG);
    for (Node node : graph.getNodeArray ())
    {
      writeNode (pw, graph.getRealizer (node));
    }
    for (Edge edge : graph.getEdgeArray ())
    {
      writeEdge (pw, graph.getRealizer (edge));
    }
    pw.print (EPILOG);
    pw.close ();
  }

  /**
   * Writes a single node to the given stream.
   * @param pw print writer to use for writing
   * @param nodeRealizer node to write
   */
  private void writeNode (@NotNull PrintWriter pw, @NotNull NodeRealizer nodeRealizer)
  {
    Node node = nodeRealizer.getNode ();
    int id = node.index ();
    String fillColor = formatColor (nodeRealizer.getFillColor ());
    double height = nodeRealizer.getHeight ();
    double width  = nodeRealizer.getWidth ();
    double x      = nodeRealizer.getX ();
    double y      = nodeRealizer.getY ();
    String shapeType = computeShapeType (nodeRealizer);

    pw.println ("<node id='" + id + "'>");
    pw.println ("  <data key='d0'>");
    pw.println ("    <y:ShapeNode>");
    pw.println ("      <y:Geometry height='" + height + "' width='" + width + "' x='" + x + "' y='" + y + "'/>");
    pw.println ("      <y:Fill color='#" + fillColor + "' transparent='false'/>");
    pw.print   ("      <y:NodeLabel visible='true' alignment='left' fontFamily='SansSerif' fontSize='12'>");
    pw.print   (EscapeUtils.escape (nodeRealizer.getLabelText ()));
    pw.println ("      </y:NodeLabel>");
    pw.println ("      <y:Shape type='" + shapeType + "'/>");
    pw.println ("    </y:ShapeNode>");
    pw.println ("  </data>");
    pw.println ("</node>");
  }

  /**
   * Writes a single edge to the given stream.
   * @param pw print writer to use for writing
   * @param edgeRealizer edge to write
   */
  private void writeEdge (@NotNull PrintWriter pw, @NotNull EdgeRealizer edgeRealizer)
  {
    Edge edge = edgeRealizer.getEdge ();
    int edgeId   = edge.index ();
    int sourceId = edge.source ().index ();
    int targetId = edge.target ().index ();
    double sx = edgeRealizer.getSourcePoint ().getX ();
    double sy = edgeRealizer.getSourcePoint ().getY ();
    double tx = edgeRealizer.getTargetPoint ().getX ();
    double ty = edgeRealizer.getTargetPoint ().getY ();
    EdgeLabel label = edgeRealizer.getLabel ();
    double distance = label.getDistance ();
    String position = computeModelPosition (label);
    double x = label.getLocation ().getX ();
    double y = label.getLocation ().getY ();
    String sourceArrow = computeArrow (edgeRealizer.getSourceArrow ());
    String targetArrow = computeArrow (edgeRealizer.getTargetArrow ());

    pw.println ("<edge id='e" + edgeId + "' source='" + sourceId + "' target='" + targetId + "'>");
    pw.println ("  <data key='d1'>");
    pw.println ("    <y:PolyLineEdge>");
    pw.println ("    <y:Path sx='" + sx + "' sy='" + sy + "' tx='" + tx + "' ty='" + ty + "'>");
    for (int i = 0; i < edgeRealizer.bendCount (); i++)
    {
      Bend bend = edgeRealizer.getBend (i);
      pw.println ("      <y:Point x='" + bend.getX () + "' y='" + bend.getY () + "'/>");
    }
    pw.println ("    </y:Path>");
    pw.println ("      <y:LineStyle type='line' width='1.0'/>");
    pw.println ("      <y:Arrows source='" + sourceArrow + "' target='" + targetArrow + "'/>");
    pw.println ("      <y:EdgeLabel alignment='center' distance='" + distance + "' hasBackgroundColor='true' " +
                                   "modelName='three_center' " +
                                   "modelPosition='" + position + "' textColor='#000000' " +
                                   "x='" + x + "' y='" + y + "'>" +
                                   EscapeUtils.escape (label.getText ()) + "</y:EdgeLabel>");
    pw.println ("    </y:PolyLineEdge>");
    pw.println ("  </data>");
    pw.println ("</edge>");
  }

  /**
   * Computes shape type for given node realizer.
   * @param nodeRealizer node realizer
   * @return computed graphml shape type
   */
  private @NotNull String computeShapeType (@NotNull NodeRealizer nodeRealizer)
  {
    if (nodeRealizer instanceof ShapeNodeRealizer)
    {
      ShapeNodeRealizer shapeNodeRealizer = (ShapeNodeRealizer) nodeRealizer;
      if (shapeNodeRealizer.getShapeType () == ShapeNodeRealizer.ROUND_RECT)
      {
        return "roundrectangle";
      }
      else
      {
        return "rectangle";
      }
    }
    else
    {
      return "rectangle";
    }
  }

  /**
   * Computes model position for given edge label.
   * @param edgeLabel edge label
   * @return computed graphml model position
   */
  private @NotNull String computeModelPosition (@NotNull YLabel edgeLabel)
  {
    if (edgeLabel.getPosition () == EdgeLabel.TCENTR)
    {
      return "t_centr";
    }
    else
    {
      return "center";
    }
  }

  /**
   * Computes graphml arrow type for given arrow.
   * @param arrow arrow
   * @return computed graphml arrow type
   */
  private @NotNull String computeArrow (@NotNull Arrow arrow)
  {
    if (arrow == Arrow.WHITE_DELTA)
    {
      return "white_delta";
    }
    else if (arrow == Arrow.DIAMOND)
    {
      return "diamond";
    }
    else if (arrow.getCustomName () != null && arrow.getCustomName ().equals ("angle"))
    {
      return "short";
    }
    else
    {
      return "none";
    }
  }

  /**
   * Formats given color to hex code.
   * @param color color to format
   * @return formatted color
   */
  private @NotNull String formatColor (@NotNull Color color)
  {
    return Integer.toHexString (color.getRGB ()).substring (2);
  }
}
