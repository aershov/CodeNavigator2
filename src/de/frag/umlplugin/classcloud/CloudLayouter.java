package de.frag.umlplugin.classcloud;

import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.geom.YDimension;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.NodeLabel;
import com.intellij.openapi.graph.view.NodeRealizer;
import de.frag.umlplugin.settings.Settings;

import java.util.*;

/**
 * Layouter for class clouds.
 */
public class CloudLayouter
{
  private static final double GAP           =  4;
  private static final double MIN_FONT_SIZE =  5;
  private static final double MAX_FONT_SIZE = 32;

  private final List<String> sortedClasses;
  private final Map<String,ProjectDependenciesAnalyzer.DependencyInfo> infos;
  private final Grid grid;

  /**
   * Creates a new class cloud layouter.
   * @param sortedClasses list of qualified class names sorted by distance to center of dependency cloud
   * @param infos mapping from qualified class names to dependency infos
   */
  public CloudLayouter (List<String> sortedClasses, Map<String, ProjectDependenciesAnalyzer.DependencyInfo> infos)
  {
    this.sortedClasses = sortedClasses;
    this.infos         = infos;
    int horizontalBorderCount = computeHorizontalBorderClassCount ();
    int verticalBorderCount   = computeVerticalBorderClassCount ();
    double relation = (double) horizontalBorderCount / (double) verticalBorderCount;
    int area = sortedClasses.size ();
    // area = width * height   => width = area / height
    // width = relation * height
    // => relation * height = area / height => relation * height * height = area => height = sqrt (area / relation)
    int gridHeight = (int) Math.ceil (Math.sqrt (area / relation));
    int gridWidth  = (int) Math.ceil ((double) area / (double) gridHeight);
    this.grid = new Grid (gridWidth, gridHeight);

    Collections.reverse (sortedClasses);
    placeClasses (extractCorners ());
    placeClasses (extractBorders ());
    placeClasses (sortedClasses);
  }

  /**
   * Computes number of classes that prefer a position at the top or bottom border of the class cloud
   * @return maximum of top border class count and bottom border class count
   */
  private int computeHorizontalBorderClassCount ()
  {
    int extendingBorderCount = 0;
    int extendedBorderCount  = 0;
    for (String sortedClass : sortedClasses)
    {
      ProjectDependenciesAnalyzer.DependencyInfo info = infos.get (sortedClass);
      double extending = info.getNormalizedExtending ();
      if (extending < 0.05)
      {
        extendingBorderCount++;
      }
      else if (extending > 0.95)
      {
        extendedBorderCount++;
      }
    }
    return Math.max (extendingBorderCount, extendedBorderCount);
  }

  /**
   * Computes number of classes that prefer a position at the left or right border of the class cloud
   * @return maximum of left border class count and right border class count
   */
  private int computeVerticalBorderClassCount ()
  {
    int usingBorderCount = 0;
    int usedBorderCount  = 0;
    for (String sortedClass : sortedClasses)
    {
      ProjectDependenciesAnalyzer.DependencyInfo info = infos.get (sortedClass);
      double using = info.getNormalizedExtending ();
      if (using < 0.05)
      {
        usingBorderCount++;
      }
      else if (using > 0.95)
      {
        usedBorderCount++;
      }
    }
    return Math.max (usingBorderCount, usedBorderCount);
  }

  /**
   * Places a list of classes on the cell grid.
   * @param classes classes to place
   */
  private void placeClasses (List<String> classes)
  {
    for (String className : classes)
    {
      ProjectDependenciesAnalyzer.DependencyInfo info = infos.get (className);
      Cell cell = new Cell (className,
                            (int) (info.getNormalizedUsing () * grid.getWidth ()),
                            (int) ((1.0 - info.getNormalizedExtending ()) * grid.getHeight ()),
                            info.getUsedCount () + info.getUsingCount ());
      grid.placeCell (cell);
    }
  }

  /**
   * Extracts all classes that prefer a position at any corner of the class cloud
   * @return list of "corner"-classes (original list of sorted classes will be modified, too)
   */
  private List<String> extractCorners ()
  {
    List<String> corners = new ArrayList<String> ();
    for (String sortedClass : sortedClasses)
    {
      ProjectDependenciesAnalyzer.DependencyInfo info = infos.get (sortedClass);
      double x = info.getNormalizedUsing ();
      double y = info.getNormalizedExtending ();
      if ((x < 0.05 || x > 0.95) && (y < 0.05 || y > 0.95))
      {
        corners.add (sortedClass);
      }
    }
    sortedClasses.removeAll (corners);
    return corners;
  }

  /**
   * Extracts all classes that prefer a position at any border of the class cloud
   * @return list of "border"-classes (original list of sorted classes will be modified, too)
   */
  private List<String> extractBorders ()
  {
    List<String> borders = new ArrayList<String> ();
    for (String sortedClass : sortedClasses)
    {
      ProjectDependenciesAnalyzer.DependencyInfo info = infos.get (sortedClass);
      double x = info.getNormalizedUsing ();
      double y = info.getNormalizedExtending ();
      if (x < 0.05 || x > 0.95 || y < 0.05 || y > 0.95)
      {
        borders.add (sortedClass);
      }
    }
    sortedClasses.removeAll (borders);
    return borders;
  }

  /**
   * Creates the graph containing all classes.
   * @return created graph
   */
  public Graph2D createGraph ()
  {
    Settings settings = Settings.getSettings ();
    int maxDependencyCount = computeMaxDependencyCount (grid);
    GraphManager graphManager = GraphManager.getGraphManager ();
    Graph2D graph = graphManager.createGraph2D ();
    Map<String, Node> allNodes = new HashMap <String, Node> ();

    // create graph nodes at each grid cell
    for (int y = 0; y < grid.getHeight (); y++)
    {
      for (int x = 0; x < grid.getWidth (); x++)
      {
        Cell cell = grid.getCell (x, y);
        if (cell != null)
        {
          String label = cell.getClassName ();
          int dotPos = label.lastIndexOf ('.');
          if (dotPos >= 0)
          {
            label = label.substring (dotPos + 1);
          }
          Node node = graph.createNode (x, y, label);
          ClassCloudData.attachCell (node, cell);
          allNodes.put (cell.getClassName (), node);
          NodeRealizer realizer = graph.getRealizer (node);
          realizer.setFillColor (ColorComputer.computeColor (cell.getPreferredX (), cell.getPreferredY (),
                                                             grid.getWidth (), grid.getHeight (), settings));
          NodeLabel nodeLabel = realizer.getLabel ();
          nodeLabel.setFontSize (computeFontSize (cell.getDependencyCount (), maxDependencyCount));
        }
      }
    }
    YDimension maxNodeSize = computeMaxNodeSize (graph);
    double maxWidth  = maxNodeSize.getWidth ();
    double maxHeight = maxNodeSize.getHeight ();
    // set first guess for size and position of all nodes
    for (int y = 0; y < grid.getHeight (); y++)
    {
      for (int x = 0; x < grid.getWidth (); x++)
      {
        Cell cell = grid.getCell (x, y);
        if (cell != null)
        {
          Node node = allNodes.get (cell.getClassName ());
          NodeRealizer realizer = graph.getRealizer (node);
          double width  = realizer.getLabel ().getWidth ();
          double height = realizer.getLabel ().getHeight ();
          realizer.setFrame (x * (maxWidth  + 8) + (maxWidth  - width)  / 2,
                             y * (maxHeight + 8) + (maxHeight - height) / 2,
                             width + 4, height + 4);
        }
      }
    }
    // partition all cells into several vertical columns of nodes
    List<List<NodeRealizer>> cellColumns = partitionCells (graph, allNodes);
    // compact all columns by shifting classes down or up to column center
    compactVertically (cellColumns);
    // compact cloud by shifting classes left or right to neighbour classes
    compactHorizontally (cellColumns);
    return graph;
  }

  /**
   * Partitions all cells into several vertical columns of nodes.
   * @param graph graph containing node realizers
   * @param allNodes mapping from qualified class names to graph nodes
   * @return list of node realizer columns
   */
  private List<List<NodeRealizer>> partitionCells (Graph2D graph, Map<String, Node> allNodes)
  {
    List<List<NodeRealizer>> columns = new ArrayList<List<NodeRealizer>> ();
    for (int x = 0; x < grid.getWidth (); x++)
    {
      List<NodeRealizer> column = new ArrayList<NodeRealizer> ();
      for (int y = 0; y < grid.getHeight (); y++)
      {
        Cell cell = grid.getCell (x, y);
        if (cell != null)
        {
          Node node = allNodes.get (cell.getClassName ());
          NodeRealizer realizer = graph.getRealizer (node);
          column.add (realizer);
        }
      }
      if (!column.isEmpty ())
      {
        columns.add (column);
      }
    }
    return columns;
  }

  /**
   * Compacts all columns by shifting classes down or up to column center.
   * @param cellColumns list of cell columns
   */
  private void compactVertically (List<List<NodeRealizer>> cellColumns)
  {
    for (List<NodeRealizer> cellColumn : cellColumns)
    {
      int centerIndex = (cellColumn.size () - 1) / 2;
      NodeRealizer centerRealizer = cellColumn.get (centerIndex);
      double minY = centerRealizer.getY () - GAP;
      double maxY = centerRealizer.getY () + centerRealizer.getHeight () + GAP;
      for (int i = 1; i <= centerIndex; i++)
      {
        NodeRealizer topRealizer = cellColumn.get (centerIndex - i);
        topRealizer.setY (minY - topRealizer.getHeight ());
        minY -= topRealizer.getHeight () + GAP;
        NodeRealizer bottomRealizer = cellColumn.get (centerIndex + i);
        bottomRealizer.setY (maxY);
        maxY += bottomRealizer.getHeight () + GAP;
      }
      if (cellColumn.size () % 2 == 0)
      {
        NodeRealizer bottomRealizer = cellColumn.get (cellColumn.size () - 1);
        bottomRealizer.setY (maxY);
      }
    }
  }

  /**
   * Compacts cloud by shifting classes left or right to neighbour classes
   * @param cellColumns list of cell columns
   */
  private void compactHorizontally (List<List<NodeRealizer>> cellColumns)
  {
    int centerIndex = (cellColumns.size () - 1) / 2;
    List<NodeRealizer> minXList = cellColumns.get (centerIndex);
    List<NodeRealizer> maxXList = cellColumns.get (centerIndex);
    for (int i = 1; i <= centerIndex; i++)
    {
      List<NodeRealizer> leftList = cellColumns.get (centerIndex - i);
      moveToRight (leftList, minXList);
      minXList = leftList;
      List<NodeRealizer> rightList = cellColumns.get (centerIndex + i);
      moveToLeft (rightList, maxXList);
      maxXList = rightList;
    }
    if (cellColumns.size () % 2 == 0)
    {
      List<NodeRealizer> rightList = cellColumns.get (cellColumns.size () - 1);
      moveToLeft (rightList, maxXList);
    }
  }

  /**
   * Moves all classes within given cell column as much right as possible until they almost touch cells in
   * given neighbour column. Neightbour column classes lie right of all classes to be shifted.
   * @param leftList column of classes to be moved right
   * @param minXList already positioned cells, lying right of classes to be moved
   */
  private void moveToRight (List<NodeRealizer> leftList, List<NodeRealizer> minXList)
  {
    for (NodeRealizer nodeRealizer : leftList)
    {
      double minY = nodeRealizer.getY ();
      double maxY = nodeRealizer.getY () + nodeRealizer.getHeight ();
      int minYIndex = findRealizerIndex (minXList, minY);
      int maxYIndex = findRealizerIndex (minXList, maxY);
      double minX = minXList.get (minYIndex).getX ();
      for (int i = minYIndex + 1; i <= maxYIndex; i++)
      {
        minX = Math.min (minX, minXList.get (i).getX ());
      }
      nodeRealizer.setX (minX - nodeRealizer.getWidth () - GAP);
    }
  }

  /**
   * Moves all classes within given cell column as much left as possible until they almost touch cells in
   * given neighbour column. Neightbour column classes lie left of all classes to be shifted.
   * @param rightList column of classes to be moved left
   * @param maxXList already positioned cells, lying left of classes to be moved
   */
  private void moveToLeft (List<NodeRealizer> rightList, List<NodeRealizer> maxXList)
  {
    for (NodeRealizer nodeRealizer : rightList)
    {
      double minY = nodeRealizer.getY ();
      double maxY = nodeRealizer.getY () + nodeRealizer.getHeight ();
      int minYIndex = findRealizerIndex (maxXList, minY);
      int maxYIndex = findRealizerIndex (maxXList, maxY);
      double maxX = maxXList.get (minYIndex).getX () + maxXList.get (minYIndex).getWidth ();
      for (int i = minYIndex + 1; i <= maxYIndex; i++)
      {
        NodeRealizer realizer = maxXList.get (i);
        maxX = Math.max (maxX, realizer.getX () + realizer.getWidth ());
      }
      nodeRealizer.setX (maxX + GAP);
    }
  }

  /**
   * Finds index of cell in given list of realizers that has a y-coordinate most similar to given coordinate.
   * @param realizers search in this list of realizers
   * @param y find index of realizer for this y-coordinate
   * @return index of realizer with most similar y-coordinate
   */
  private int findRealizerIndex (List<NodeRealizer> realizers, double y)
  {
    int index = 0;
    for (NodeRealizer nodeRealizer : realizers)
    {
      if ((nodeRealizer.getY () - GAP <= y) && (nodeRealizer.getY () + nodeRealizer.getHeight () + GAP >= y))
      {
        return index;
      }
      index++;
    }
    // not found => use first or last index
    if (y < realizers.get (0).getY ())
    {
      return 0;
    }
    else
    {
      return realizers.size () - 1;
    }
  }

  /**
   * Computes maximum dependency count of all classes.
   * @param grid grid of cells
   * @return maximum dependency count
   */
  private int computeMaxDependencyCount (Grid grid)
  {
    int maxDependencyCount = 0;
    for (int y = 0; y < grid.getHeight (); y++)
    {
      for (int x = 0; x < grid.getWidth (); x++)
      {
        Cell cell = grid.getCell (x, y);
        if (cell != null)
        {
          int dependencyCount = cell.getDependencyCount ();
          maxDependencyCount = Math.max (maxDependencyCount, dependencyCount);
        }
      }
    }
    return maxDependencyCount;
  }

  /**
   * Computes font size with respect to maximum dependency count.
   * @param dependencyCount dependency count of current class
   * @param maxDependencyCount maximum dependency count in complete class cloud
   * @return computed font size
   */
  private int computeFontSize (double dependencyCount, double maxDependencyCount)
  {
    return (int) (MIN_FONT_SIZE + (dependencyCount / maxDependencyCount) * (MAX_FONT_SIZE - MIN_FONT_SIZE));
  }

  /**
   * Computes size of biggest node in class cloud.
   * @param graph graph containing nodes
   * @return dimension of biggest node
   */
  private YDimension computeMaxNodeSize (Graph2D graph)
  {
    double maxWidth  = 0;
    double maxHeight = 0;
    for (Node node : graph.getNodeArray ())
    {
      NodeRealizer realizer = graph.getRealizer (node);
      double width  = realizer.getLabel ().getWidth ();
      double height = realizer.getLabel ().getHeight ();
      maxWidth  = Math.max (maxWidth,  width);
      maxHeight = Math.max (maxHeight, height);
    }
    GraphManager graphManager = GraphManager.getGraphManager ();
    return graphManager.createYDimension (maxWidth, maxHeight);
  }
}
