package de.frag.umlplugin.classcloud;

import org.jetbrains.annotations.NotNull;

/**
 * Single cell in an ordered grid of class cloud cells.
 */
public class Cell
{
  private final String className;
  private final int    preferredX;
  private final int    preferredY;
  private final int    dependencyCount;

  /**
   * Creates new cell.
   * @param className qualified class name for class cloud cell
   * @param preferredX preferred x-coordinate. 0.0 means left, 1.0 means right
   * @param preferredY preferred y-coordinate. 0.0 means top, 1.0 means bottom
   * @param dependencyCount number of dependencies between this cell and all other cells
   */
  public Cell (@NotNull String className, int preferredX, int preferredY, int dependencyCount)
  {
    this.className       = className;
    this.preferredX      = preferredX;
    this.preferredY      = preferredY;
    this.dependencyCount = dependencyCount;
  }

  /**
   * Gets qualified class name.
   * @return qualified class name
   */
  public @NotNull String getClassName ()
  {
    return className;
  }

  /**
   * Gets preferred x-coordinate.
   * @return preferred x-coordinate
   */
  public int getPreferredX ()
  {
    return preferredX;
  }

  /**
   * Gets preferred y-coordinate.
   * @return preferred y-coordinate
   */
  public int getPreferredY ()
  {
    return preferredY;
  }

  /**
   * Gets number of dependencies between this cell and all other cells in the same class cloud
   * @return number of dependencies between classes
   */
  public int getDependencyCount ()
  {
    return dependencyCount;
  }

  public @NotNull String toString ()
  {
    return className + ", preferred: (" + preferredX + ", " + preferredY + ")";
  }
}
