package de.frag.umlplugin.classcloud;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple coordinate pair in an ordered cell grid.
 */
public class CellCoordinate
{
  private final int x;
  private final int y;

  /**
   * Creates a new coordinate.
   * @param x x-coordinate (0..n from left to right)
   * @param y y-coordinate (0..n from top to bottom)
   */
  public CellCoordinate (int x, int y)
  {
    this.x = x;
    this.y = y;
  }

  /**
   * Gets x-coordinate.
   * @return x-coordinate
   */
  public int getX ()
  {
    return x;
  }

  /**
   * Gets y-coordinate.
   * @return y-coordinate
   */
  public int getY ()
  {
    return y;
  }

  public @NotNull String toString ()
  {
    return "(" + x + ", " + y + ")";
  }

  public boolean equals (@Nullable Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass () != o.getClass ())
    {
      return false;
    }
    CellCoordinate that = (CellCoordinate) o;
    return x == that.x && y == that.y;
  }

  public int hashCode ()
  {
    int result;
    result = x;
    result = 31 * result + y;
    return result;
  }
}
