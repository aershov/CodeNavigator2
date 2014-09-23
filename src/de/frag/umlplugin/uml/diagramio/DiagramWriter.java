package de.frag.umlplugin.uml.diagramio;

import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * Write diagrams to files.
 */
public interface DiagramWriter
{
  /**
   * Checks whether this writer can write multiple diagrams or not.
   * @return true, if this writer can write multiple diagrams; false, if it can write only one diagram
   */
  public boolean canWriteMultipleDiagrams ();

  /**
   * Writes given diagram to file.
   * @param diagrams array of diagrams to write
   * @param path path to target file
   * @param saveThumbnail true, if thumbnail should be written; false otherwise
   * @param thumbnailHeight desired thumbnail height in pixels
   * @throws IOException on IO error
   */
  public void writeDiagram (@NotNull UMLDiagram[] diagrams, @NotNull String path, boolean saveThumbnail, 
                            int thumbnailHeight) throws IOException;

  /**
   * Checks whether this writer supports storage of image thumbnails.
   * @return true, if this writer can create thumbnail images; false otherwise
   */
  public boolean canWriteThumbnail ();
}
