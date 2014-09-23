package de.frag.umlplugin.uml.diagramio;

import de.frag.umlplugin.graphio.FileType;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * Diagram file types.
 */
public enum DiagramFileType implements FileType
{
  DIAGRAM  ("Single diagram file",    "Saves current diagram as diagam creation command sequence.",
            ".xml", new CommandSequenceDiagramWriter (false)),
  DIAGRAMS ("Multiple diagrams file", "Saves all diagrams as diagam creation command sequences.",
            ".xml", new CommandSequenceDiagramWriter (true)),
  GIF      ("GIF image",              "Saves current diagram as GIF image",
            ".gif", new ImageDiagramWriter ("gif")),
  JPG      ("JPEG image",             "Saves current diagram as JPEG image",
            ".jpg", new ImageDiagramWriter ("jpg")),
  PNG      ("PNG image",              "Saves current diagram as PNG image",
            ".png", new ImageDiagramWriter ("png")),
  SVG      ("SVG image",              "Saves current diagram as SVG image",
            ".svg", new SVGDiagramWriter ()),
  GRAPHML  ("Graphml file",           "Saves current diagram as Graphml graph (readable by yed)",
            ".graphml", new GraphmlDiagramWriter ());

  private final String        name;
  private final String        description;
  private final String        extension;
  private final DiagramWriter diagramWriter;

  /**
   * Creates a new diagram file type.
   * @param name name that appears in {@link #toString()} method.
   * @param description description for info messages
   * @param extension file name extension
   * @param diagramWriter diagram writer that writes diagrams to files
   */
  private DiagramFileType (@NotNull String name, @NotNull String description, @NotNull String extension,
                           @NotNull DiagramWriter diagramWriter)
  {
    this.name          = name;
    this.description   = description;
    this.extension     = extension;
    this.diagramWriter = diagramWriter;
  }

  /**
   * Gets file type description.
   * @return file type description
   */
  public @NotNull String getDescription ()
  {
    return description;
  }

  /**
   * Gets file type name.
   * @return file type name
   */
  public @NotNull String toString ()
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
   * Creates a thumbnail path.
   * @param path path of normal file with extension already attached.
   * @param thumbnailSuffix thumbnail suffix
   * @return created thumbnail path
   */
  public @NotNull String createThumbnailPath (@NotNull String path, @NotNull String thumbnailSuffix)
  {
    int extensionPos = path.lastIndexOf (extension);
    return path.substring (0, extensionPos) + thumbnailSuffix + extension;
  }

  /**
   * Checks whether this writer can write multiple diagrams or not.
   * @return true, if this writer can write multiple diagrams; false, if it can write only one diagram
   */
  public boolean canWriteMultipleDiagrams ()
  {
    return diagramWriter.canWriteMultipleDiagrams ();
  }

  /**
   * Writes given diagrams to specified file path.
   * @param diagrams diagrams to write
   * @param path path to target file
   * @param saveThumbnail true, if thumbnail should be written; false otherwise
   * @param thumbnailHeight desired thumbnail height in pixels
   * @throws java.io.IOException on IO error
   */
  public void writeDiagram (@NotNull UMLDiagram[] diagrams, @NotNull String path, boolean saveThumbnail,
                            int thumbnailHeight) throws IOException
  {
    diagramWriter.writeDiagram (diagrams, path, saveThumbnail, thumbnailHeight);
  }

  /**
   * Checks whether this file type supports storage of image thumbnails.
   * @return true, if this file type can create thumbnail images; false otherwise
   */
  public boolean canWriteThumbnail ()
  {
    return diagramWriter.canWriteThumbnail ();
  }
}
