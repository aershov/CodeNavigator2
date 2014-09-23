package de.frag.umlplugin.graphio;

import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Write graphs to image files (JPG, PNG, GIF).
 */
public class ImageGraphWriter implements GraphWriter
{
  private static final String THUMBNAIL_SUFFIX = "_thumb";

  private final String extension;

  /**
   * Creates new writer that writes graphs to images.
   * @param extension file extension that specified desired image format
   */
  public ImageGraphWriter (@NotNull String extension)
  {
    this.extension = extension;
  }

  /**
   * Creates a thumbnail path.
   * @param path path of normal file with extension already attached.
   * @return created thumbnail path
   */
  private @NotNull String createThumbnailPath (@NotNull String path)
  {
    String suffix = "." + extension;
    int extensionPos = path.lastIndexOf (suffix);
    return path.substring (0, extensionPos) + THUMBNAIL_SUFFIX + suffix;
  }

  /**
   * Writes given graph to file.
   * @param graph graph to write
   * @param path  path to target file
   * @param saveThumbnail true, if thumbnail should be created and written; false otherwise
   * @param thumbnailHeight desired height of thumbnail image in pixels
   * @throws java.io.IOException on IO error
   */
  public void writeGraph (@NotNull Graph2D graph, @NotNull String path, boolean saveThumbnail, int thumbnailHeight)
                          throws IOException
  {
    FileOutputStream stream      = null;
    FileOutputStream thumbStream = null;
    try
    {
      final int border = 20;
      Rectangle rectangle = graph.getBoundingBox ();
      int x      = (int) rectangle.getX ();
      int y      = (int) rectangle.getY ();
      int width  = (int) rectangle.getWidth  () + Math.abs (x) + 2 * border;
      int height = (int) rectangle.getHeight () + Math.abs (y) + 2 * border;
      BufferedImage image = new BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
      Graphics2D graphics = (Graphics2D) image.getGraphics ();

      GraphManager graphManager = GraphManager.getGraphManager ();
      Graph2DView imageView = graphManager.createGraph2DView (graph);
      imageView.setAntialiasedPainting (true);
      imageView.setSecureDrawingMode   (true);
      imageView.setBounds              (0, 0, width, height);
      imageView.setViewPoint           (x - border, y - border);
      imageView.paintVisibleContent    (graphics);
      graph.removeView                 (imageView);
      // save image
      stream = new FileOutputStream (path);
      ImageIO.write (image, extension, stream);

      if (saveThumbnail)
      {
        // create thumbnail
        ImageScaler scaler = new ImageScaler (thumbnailHeight);
        BufferedImage thumbnail = scaler.filter (image);
        // save thumbnail image
        String thumbnailPath = createThumbnailPath (path);
        thumbStream = new FileOutputStream (thumbnailPath);
        ImageIO.write (thumbnail, extension, thumbStream);
      }
    }
    finally
    {
      if (stream != null)
      {
        stream.close ();
      }
      if (thumbStream != null)
      {
        thumbStream.close ();
      }
    }
  }

  /**
   * Checks whether this graph writer supports storage image thumbnails.
   * @return true, since this writer can create thumbnail images
   */
  public boolean canWriteThumbnail ()
  {
    return true;
  }
}
