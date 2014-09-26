package de.frag.umlplugin.graphio;

import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Writes graphs to SVG documents.
 */
public class SVGGraphWriter implements GraphWriter
{
  /**
   * Writes given graph to file.
   * @param graph graph to write
   * @param path path to target file
   * @param saveThumbnail true, if thumbnail should be written; false otherwise
   * @param thumbnailHeight desired thumbnail height in pixels
   * @throws java.io.IOException on IO error
   */
  public void writeGraph (@NotNull Graph2D graph, @NotNull String path, boolean saveThumbnail,
                          int thumbnailHeight) throws IOException
  {
    DOMImplementationRegistry registry = null;
    try
    {
      registry = DOMImplementationRegistry.newInstance ();
    }
    catch (Exception e)
    {
      e.printStackTrace ();
    }
    if (registry != null)
    {
      DOMImplementation dom = registry.getDOMImplementation ("");
      if (dom != null)
      {
        final int border = 20;
        Rectangle rectangle = graph.getBoundingBox ();
        int x      = (int) rectangle.getX ();
        int y      = (int) rectangle.getY ();
        int width  = (int) rectangle.getWidth  () + Math.abs (x) + 2 * border;
        int height = (int) rectangle.getHeight () + Math.abs (y) + 2 * border;

        String svgNS = "http://www.w3.org/2000/svg";
        Document document = dom.createDocument (svgNS, "svg", null);
        SVGGraphics2D graphics = new SVGGraphics2D (document);
        graphics.setSVGCanvasSize (new Dimension (width, height));

        GraphManager graphManager = GraphManager.getGraphManager ();
        Graph2DView imageView = graphManager.createGraph2DView (graph);
        imageView.setAntialiasedPainting (true);
//        imageView.setSecureDrawingMode   (true); //TODO
        imageView.setBounds              (0, 0, width, height);
        imageView.setViewPoint           (x - border, y - border);
        imageView.paintVisibleContent    (graphics);
        graph.removeView                 (imageView);

        boolean useCSS = true;
        FileOutputStream fos = new FileOutputStream (path);
        Writer writer = new OutputStreamWriter (fos, "UTF-8");
        graphics.stream (writer, useCSS);
        writer.close ();
        fos.close ();
      }
    }
  }

  /**
   * Checks whether this graph writer supports storage image thumbnails.
   * @return false, since this writer does not support thumbnail images
   */
  public boolean canWriteThumbnail ()
  {
    return false;
  }
}
