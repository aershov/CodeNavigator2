package de.frag.umlplugin.classcloud;

import junit.framework.TestCase;

import java.util.*;

/**
 * Tests grid class.
 */
public class TestGrid extends TestCase
{
  public void testCandidates ()
  {
    System.out.println ("--- (5, 5) ---");
    Grid grid = new Grid (5, 5);
    List<CellCoordinate> candidates = grid.getCandidates ();
    assertEquals (toCellCoordiantes (new int [][] {{0, 0}, {1, 0}, {2, 0}, {3, 0},
                                                   {4, 0}, {4, 1}, {4, 2}, {4, 3},
                                                   {4, 4}, {3, 4}, {2, 4}, {1, 4},
                                                   {0, 4}, {0, 3}, {0, 2}, {0, 1},
                                                   {1, 1}, {2, 1},
                                                   {3, 1}, {3, 2},
                                                   {3, 3}, {2, 3},
                                                   {1, 3}, {1, 2}, 
                                                   {2, 2}}), candidates);
    assertEquals (5 * 5, candidates.size ());
    System.out.println ("--- (4, 4) ---");
    grid = new Grid (4, 4);
    candidates = grid.getCandidates ();
    assertEquals (toCellCoordiantes (new int [][] {{0, 0}, {1, 0}, {2, 0},
                                                   {3, 0}, {3, 1}, {3, 2},
                                                   {3, 3}, {2, 3}, {1, 3},
                                                   {0, 3}, {0, 2}, {0, 1},
                                                   {1, 1},
                                                   {2, 1},
                                                   {2, 2},
                                                   {1, 2}}), candidates);
    assertEquals (4 * 4, candidates.size ());
    System.out.println ("--- (3, 4) ---");
    grid = new Grid (3, 4);
    candidates = grid.getCandidates ();
    assertEquals (toCellCoordiantes (new int [][] {{0, 0}, {1, 0},
                                                   {2, 0}, {2, 1}, {2, 2},
                                                   {2, 3}, {1, 3},
                                                   {0, 3}, {0, 2}, {0, 1},
                                                   {1, 1}, 
                                                   {1, 2}}), candidates);
    assertEquals (3 * 4, candidates.size ());
    System.out.println ("--- (4, 3) ---");
    grid = new Grid (4, 3);
    candidates = grid.getCandidates ();
    assertEquals (toCellCoordiantes (new int [][] {{0, 0}, {1, 0}, {2, 0},
                                                   {3, 0}, {3, 1},
                                                   {3, 2}, {2, 2}, {1, 2},
                                                   {0, 2}, {0, 1},
                                                   {1, 1}, 
                                                   {2, 1}}), candidates);
    assertEquals (4 * 3, candidates.size ());
    System.out.println ("--- (4, 2) ---");
    grid = new Grid (4, 2);
    candidates = grid.getCandidates ();
    assertEquals (toCellCoordiantes (new int [][] {{0, 0}, {1, 0}, {2, 0},
                                                   {3, 0},
                                                   {3, 1}, {2, 1}, {1, 1},
                                                   {0, 1}}), candidates);
    assertEquals (4 * 2, candidates.size ());
    System.out.println ("--- (3, 2) ---");
    grid = new Grid (3, 2);
    candidates = grid.getCandidates ();
    assertEquals (toCellCoordiantes (new int [][] {{0, 0}, {1, 0},
                                                   {2, 0},
                                                   {2, 1}, {1, 1},
                                                   {0, 1}}), candidates);
    assertEquals (3 * 2, candidates.size ());
    System.out.println ("--- (3, 5) ---");
    grid = new Grid (3, 5);
    candidates = grid.getCandidates ();
    assertEquals (toCellCoordiantes (new int [][] {{0, 0}, {1, 0},
                                                   {2, 0}, {2, 1}, {2, 2}, {2, 3},
                                                   {2, 4}, {1, 4},
                                                   {0, 4}, {0, 3}, {0, 2}, {0, 1},
                                                   {1, 1}, {1, 2}, {1, 3}}), candidates);
    assertEquals (3 * 5, candidates.size ());

    Random random = new Random ();
    for (int i = 0; i < 100; i++)
    {
      int width  = random.nextInt (40) + 1;
      int height = random.nextInt (40) + 1;
      System.out.println ("--- (" + width + ", " + height + ") ---");
      grid = new Grid (width, height);
      candidates = grid.getCandidates ();
      assertEquals (width * height, candidates.size ());
      assertEquals (width * height, new HashSet<CellCoordinate> (candidates).size ());
    }
  }

  private List<CellCoordinate> toCellCoordiantes (int[][] pairs)
  {
    List<CellCoordinate> coordinates = new ArrayList<CellCoordinate> ();
    for (int[] pair : pairs)
    {
      coordinates.add (new CellCoordinate (pair [0], pair [1]));
    }
    return coordinates;
  }
}
