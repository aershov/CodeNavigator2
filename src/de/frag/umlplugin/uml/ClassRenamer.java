package de.frag.umlplugin.uml;

import org.jetbrains.annotations.NotNull;

/**
 * Renames class names in diagram creation commands.
 */
public interface ClassRenamer
{
  /**
   * Renames given class and returns new name.
   * @param classToRename fully qualified name of class to rename
   * @return new name for given class
   */
  public @NotNull String rename (@NotNull String classToRename);
}
