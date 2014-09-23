package de.frag.umlplugin.graphio;

import com.intellij.openapi.graph.view.Graph2D;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Write graph to file.
 */
public interface GraphWriter
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
                          int thumbnailHeight) throws IOException;

  /**
   * Checks whether this graph writer supports storage image thumbnails.
   * @return true, if this writer can create thumbnail images; false otherwise
   */
  public boolean canWriteThumbnail ();
}
