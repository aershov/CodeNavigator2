package de.frag.umlplugin.uml.diagramio;

import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import de.frag.umlplugin.graphio.GraphWriter;
import de.frag.umlplugin.graphio.SVGGraphWriter;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Writes diagram to an image file.
 */
public class SVGDiagramWriter implements DiagramWriter
{
  private final GraphWriter graphWriter;

  /**
   * Creates a new diagram writer that writes diagrams to vector graphics files.
   */
  public SVGDiagramWriter ()
  {
    this.graphWriter = new SVGGraphWriter ();
  }

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
   * @param diagrams diagrams to write
   * @param path path to target file
   * @param saveThumbnail true, if thumbnail should be written; false otherwise
   * @param thumbnailHeight desired thumbnail height in pixels
   * @throws java.io.IOException on IO error
   */
  public void writeDiagram (@NotNull UMLDiagram[] diagrams, @NotNull String path, boolean saveThumbnail,
                            int thumbnailHeight) throws IOException
  {
    Graph2DView view = diagrams [0].getView ();
    Graph2D graph = view.getGraph2D ();
    graphWriter.writeGraph (graph, path, saveThumbnail, thumbnailHeight);
  }
}