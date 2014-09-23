package de.frag.umlplugin.graphio;

import com.intellij.openapi.graph.view.Graph2D;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * Diagram file types.
 */
public enum GraphFileType implements FileType
{
  GIF ("GIF image",  "Saves current diagram as GIF image",  ".gif", new ImageGraphWriter ("gif")),
  JPG ("JPEG image", "Saves current diagram as JPEG image", ".jpg", new ImageGraphWriter ("jpg")),
  PNG ("PNG image",  "Saves current diagram as PNG image",  ".png", new ImageGraphWriter ("png")),
  SVG ("SVG image",  "Saves current diagram as SVG image",  ".svg", new SVGGraphWriter ());

  private final String      name;
  private final String      description;
  private final String      extension;
  private final GraphWriter graphWriter;

  /**
   * Creates a new graph file type.
   * @param name name that appears in {@link #toString()} method.
   * @param description description for info messages
   * @param extension file name extension
   * @param graphWriter graph writer that writes graphs to files
   */
  private GraphFileType (@NotNull String name, @NotNull String description, @NotNull String extension,
                         @NotNull GraphWriter graphWriter)
  {
    this.name        = name;
    this.description = description;
    this.extension   = extension;
    this.graphWriter = graphWriter;
  }

  /**
   * Gets file type description.
   * @return file type description
   */
  @NotNull public String getDescription ()
  {
    return description;
  }

  /**
   * Gets file type name.
   * @return file type name
   */
  @NotNull public String toString ()
  {
    return name;
  }

  /**
   * Appends extension, if it is not already present.
   * @param path path to append extension to
   * @return modified path
   */
  public @NotNull String appendExtension (@NotNull String path)
  {
    return path.endsWith (extension) ? path : path + extension;
  }

  /**
   * Writes given graph to specified file path.
   * @param graph graph to write
   * @param path path to target file
   * @param saveThumbnail true, if thumbnail should be written; false otherwise
   * @param thumbnailHeight desired thumbnail height in pixels
   * @throws java.io.IOException on IO error
   */
  public void writeGraph (@NotNull Graph2D graph, @NotNull String path, boolean saveThumbnail,
                                   int thumbnailHeight) throws IOException
  {
    graphWriter.writeGraph (graph, path, saveThumbnail, thumbnailHeight);
  }

  /**
   * Checks whether this file type supports storage of image thumbnails.
   * @return true, if this file type can create thumbnail images; false otherwise
   */
  public boolean canWriteThumbnail ()
  {
    return graphWriter.canWriteThumbnail ();
  }
}