package de.frag.umlplugin.graphio;

import org.jetbrains.annotations.NotNull;

/**
 * General file type for saving files.
 */
public interface FileType
{
  /**
   * Gets file type description.
   * @return file type description
   */
  public @NotNull String getDescription ();

  /**
   * Gets file type name.
   * @return file type name
   */
  public @NotNull String toString ();

  /**
   * Checks whether this file type supports storage of image thumbnails.
   * @return true, if this file type can create thumbnail images; false otherwise
   */
  public boolean canWriteThumbnail ();
}
