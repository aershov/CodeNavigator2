package de.frag.umlplugin.classcloud;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Regular grid that is used as intermediate storage for class cloud cells.
 */
public class Grid
{
  private final Cell [][]            cells;
  private final List<CellCoordinate> candidates = new ArrayList<CellCoordinate> ();
  private final int                  width;
  private final int                  height;

  /**
   * Creates a grid that can contain "width" times "height" cells.
   * @param width width of grid in cells
   * @param height height of grid in cells
   */
  public Grid (int width, int height)
  {
    this.width  = width;
    this.height = height;
    cells = new Cell [width][height];
    createCandidates ();
  }

  /**
   * Creates list of candidates. Candidates are all cells starting at the top left corner (coordinates (0,0))
   * with first direction from left to right and progressing in a spiral until the last possible cell is reached
   * in the center of the grid.
   */
  private void createCandidates ()
  {
    int remainingWidth  = width;
    int remainingHeight = height;
    int remaining       = width * height;
    int scanCount       = 0;
    int [] scanToDeltaX = {1, 0, -1,  0};
    int [] scanToDeltaY = {0, 1,  0, -1};
    int x               = 0;
    int y               = 0;
    // start scan in one of 4 possible directions (to right, down, to left, up)
    while (remaining > 0)
    {
      // handle special case: only one central cell left
      if (remaining == 1)
      {
        candidates.add (new CellCoordinate (x, y));
        break;
      }
      int deltaX = scanToDeltaX [scanCount & 3];
      int deltaY = scanToDeltaY [scanCount & 3];
      int scanLength = deltaX != 0 ? remainingWidth - 1 : remainingHeight - 1;
      int count = 0;
      // loop for single scan (to right, down, to left, up)
      while (count < scanLength)
      {
        candidates.add (new CellCoordinate (x, y));
        x += deltaX;
        y += deltaY;
        count++;
      }
      // prepare next loop
      remaining -= scanLength;
      scanCount++;
      if (scanCount % 4 == 0)
      {
        remainingWidth  -= 2;
        remainingHeight -= 2;
        x = scanCount / 4;
        y = scanCount / 4;
      }
    }
  }

  /**
   * Places a single cell by choosing a coordinate from the current list of candidates that is nearest to
   * the preferred coordinate of the cell. The chosen coordinate is removed from the list of candidates.
   * @param cell cell to be placed.
   */
  public void placeCell (@NotNull Cell cell)
  {
    CellCoordinate coordinate = chooseCoordinate (cell);
    candidates.remove (coordinate);
    cells [coordinate.getX ()][coordinate.getY ()] = cell;
  }

  /**
   * Chooses a cell coordinate that has the nearest distance to all available cell candidates.
   * @param cell cell to choose coordinate for
   * @return chosen coordinate
   */
  public @NotNull CellCoordinate chooseCoordinate (@NotNull Cell cell)
  {
    int minDistance = Integer.MAX_VALUE;
    CellCoordinate chosenCoordinate = candidates.get (0);
    for (CellCoordinate candidate : candidates)
    {
      int deltaX = cell.getPreferredX () - candidate.getX ();
      int deltaY = cell.getPreferredY () - candidate.getY ();
      int distance = deltaX * deltaX + deltaY * deltaY;
      if (distance < minDistance)
      {
        minDistance = distance;
        chosenCoordinate = candidate;
      }
    }
    //System.out.println ("cell: " + cell + ", chosen coordinate: " + chosenCoordinate);
    return chosenCoordinate;
  }

  /**
   * Current list of cell candidates.
   * @return list of candidates
   */
  public @NotNull List<CellCoordinate> getCandidates ()
  {
    return candidates;
  }

  /**
   * Gets grid width.
   * @return Gets grid width
   */
  public int getWidth ()
  {
    return width;
  }

  /**
   * Gets grid height.
   * @return Gets grid height
   */
  public int getHeight ()
  {
    return height;
  }

  /**
   * Gets cell at given coordinates.
   * @param x x-coordinate
   * @param y y-coordinate
   * @return found cell or null, if no cell exists at given coordinate
   */
  public @Nullable Cell getCell (int x, int y)
  {
    return cells [x][y];
  }
}
