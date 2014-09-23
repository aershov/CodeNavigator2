package de.frag.umlplugin.guilayout;

import static de.frag.umlplugin.guilayout.FlexConstants.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
  * FlexLayout is a layout manager, that behaves similarly to GridBagLayout,
  * but is much simpler to use and a little bit less flexible. Complex
  * GUI designs must not be constructed by using FlexLayout directly, but
  * can be created by using the LayoutBuilder. There are two main types of
  * FlexLayout: horizontal and vertical arrangement of components.
  * A FlexLayout is constructed by calling one of the factory methods
  * <code>createHorizontalLayout</code> and <code>createVerticalLayout</code>.
  * Then components are filled into a unregularly rectangular raster with
  * each component associated to a FlexConstraint object, that describes how
  * to arrange and resize the corresponding component.
  * @see FlexConstraints
  * @see LayoutBuilder
  */
public abstract class FlexLayout implements LayoutManager2
{
  protected final List<Element> components  = new ArrayList<Element> ();  // list of components
  protected int    columns     = 0;              // number of columns
  protected int    rows        = 0;              // number of rows

  private int    maxMaxSize  = 0;    // max size (ALIGN_MAX) (temporary)
  private int    remainSpace = 0;    // remaining space (temporary)
  private float  sumWeight   = 0.0f; // sum of all weights (temporary)

  /**
    * An Element is a component and its associated FlexConstraints object.
    */
  protected class Element
  {
    public final Component       component;
    public final FlexConstraints constraints;

    /**
      * Construct a new Element.
      * @param component component
      * @param constraints FlexConstraints of this component
      */
    public Element (@NotNull Component component, @NotNull FlexConstraints constraints)
    {
      this.component   = component;
      this.constraints = constraints;
    }

    /**
      * Set the bounds of this element with respect to the given available
      * space.
      * @param x x-coordinate of upper left corner of available space
      * @param y y-coordinate of upper left corner of available space
      * @param width width of available space
      * @param height height of available space
      */
    public void sizeComponent (int x, int y, int width, int height)
    {
      Insets insets = constraints.insets;
      x      += insets.left;
      y      += insets.top;
      width  -= (insets.left + insets.right);
      height -= (insets.top  + insets.bottom);
      Dimension size = component.getPreferredSize ();
      component.setBounds (
        constraints.alignmentX.getOffset (x, width,  size.width),
        constraints.alignmentY.getOffset (y, height, size.height),
        constraints.alignmentX.getSize   (width,  size.width),
        constraints.alignmentY.getSize   (height, size.height)
      );
    }
  }

  /**
    * Protected constructor to prevend it from being called directly.
    * FlexLayouts should be constructed by calling one of the factory methods
    * <code>createHorizontalLayout</code> and <code>createVerticalLayout</code>.
    */
  protected FlexLayout ()
  {
  }

  /**
    * Create a new horizontal arranging FlexLayout manager. Components will be
    * added from left to right until the given number of columns is reached
    * and a new row of components is created. A horizontal layout has always
    * the given number of columns and an arbitrary number of rows.
    * @param columns number of columns
    * @return constructed layout manager
    */
  public static FlexLayout createHorizontalLayout (int columns)
  {
    return new FlexLayoutH (columns);
  }

  /**
    * Create a new vertical arranging FlexLayout manager. Components will be
    * added from top to bottom until the given number of rows is reached
    * and a new column of components is created. A vertical layout has always
    * the given number of rows and an arbitrary number of columns.
    * @param rows number of rows
    * @return constructed layout manager
    */
  public static FlexLayout createVerticalLayout (int rows)
  {
    return new FlexLayoutV (rows);
  }

  /**
    * Add one more component to the layouter.
    * @param comp component to add
    * @param constraints layout constraints for the given component. This
    *        must be of type FlexConstraints.
    */
  public void addLayoutComponent (@NotNull Component comp, @NotNull Object constraints)
  {
    components.add (new Element (comp, (FlexConstraints) constraints));
  }

  /**
    * Add a component to the layouter using default FlexConstraints.
    * @param name name of the component (will be ignored)
    * @param comp component to add
    */
  public void addLayoutComponent (@NotNull String name, @NotNull Component comp)
  {
    components.add (new Element (comp, new FlexConstraints ()));
  }

  /**
    * Remove the given component from the layouter.
    * @param comp component to remove
    */
  public void removeLayoutComponent (@NotNull Component comp)
  {
    int index = 0;
    for (Element element : components)
    {
      if (element.component == comp)
      {
        components.remove (index);
        return;
      }
      index++;
    }
  }

  public float getLayoutAlignmentX (@NotNull Container target)
  {
    return 0.5f;
  }

  public float getLayoutAlignmentY (@NotNull Container target)
  {
    return 0.5f;
  }

  public void invalidateLayout (@NotNull Container target)
  {
  }

  /**
    * Get the constraints, that should be used to add a container with
    * this layouter to a parent container, that uses FlexLayout, too.
    * @return constraints to use for adding this container to its parent
    */
  public @NotNull FlexConstraints getParentConstraints ()
  {
    Alignment alignX = ALIGN_FILL;
    Alignment alignY = ALIGN_FILL;
    for (Element element : components)
    {
      FlexConstraints c = element.constraints;
      if (c != null)
      {
        if (c.alignmentX == ALIGN_STRETCH)
        {
          alignX = ALIGN_STRETCH;
        }
        if (c.alignmentY == ALIGN_STRETCH)
        {
          alignY = ALIGN_STRETCH;
        }
      }
    }
    return new FlexConstraints (alignX, alignY, 1.0f, 1.0f, null);
  }

  /**
    * Create a grid of elements with respect to the type of layouter
    * (horizontal or vertical).
   * @return elements on grid
    */
  protected abstract @NotNull Element [][] createGrid ();

  //------------------------------------------------------------------------
  //-------------- minimum, preferred and maximum size ---------------------
  //------------------------------------------------------------------------
  /**
    * A SizeGetter gets size information from a component and is a simple
    * hook to get different size information in the same way (minimum size,
    * maximum size and preferred size).
    */
  protected interface SizeGetter
  {
    public @NotNull Dimension getSize (@NotNull Component component);
    /**
      * a sizeinfo of the given component, assumed that it was  stretch aligned.
      * @return a dimension object with Integr.MAX_VALUE values for the maximum
      * size, with 0 values otherwise
      */
    public @NotNull Dimension getStretchedSize ();
  }

  private class PreferredSizeGetter implements SizeGetter
  {
    public @NotNull Dimension getSize (@NotNull Component component)
    {
      return component.getPreferredSize ();
    }

    public @NotNull Dimension getStretchedSize ()
    {
      return new Dimension(0, 0);
    }
  }

  private class MinSizeGetter implements SizeGetter
  {
    public @NotNull Dimension getSize (@NotNull Component component)
    {
      return component.getMinimumSize ();
    }

    public @NotNull Dimension getStretchedSize ()
    {
      return new Dimension(0, 0);
    }
  }

  private class MaxSizeGetter implements SizeGetter
  {
    public @NotNull Dimension getSize (@NotNull Component component)
    {
      return component.getMaximumSize ();
    }

    public @NotNull Dimension getStretchedSize ()
    {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
  }

  /**
    * Generic layout size calculator to compute values for minimum size,
    * maximum size and preferred size.
    * @param target container to compute layout size for
    * @param getter size getter to get size information of a component
    * @return computed dimension
    */
  protected @NotNull Dimension getLayoutSize (@NotNull Container target, @NotNull SizeGetter getter)
  {
    Element [][] grid = createGrid ();
    int width  = computeWidth(getter, grid);
    int height = computeHeight(getter, grid);

    Insets insets = target.getInsets ();
    if (insets != null)
    {
      width  += insets.left + insets.right;
      height += insets.top  + insets.bottom;
    }

    return new Dimension (width, height);
  }

  /**
    * Get the maximum size of the target container.
    * @param target container to compute maximum size for
    * @return maximum size of the container
    */
  public @NotNull Dimension maximumLayoutSize (@NotNull Container target)
  {
    return getLayoutSize (target, new MaxSizeGetter ());
  }

  /**
    * Get the minimum size of the target container.
    * @param parent container to compute minimum size for
    * @return minimum size of the container
    */
  public @NotNull Dimension minimumLayoutSize (@NotNull Container parent)
  {
    return getLayoutSize (parent, new MinSizeGetter ());
  }

  /**
    * Get the preferred size of the target container.
    * @param parent container to compute preferred size for
    * @return preferred size of the container
    */
  public @NotNull Dimension preferredLayoutSize (@NotNull Container parent)
  {
    return getLayoutSize (parent, new PreferredSizeGetter ());
  }

  //------------------------------------------------------------------------
  //---------------------- layout algorithm --------------------------------
  //------------------------------------------------------------------------

  /**
    * Compute inner bounds of a container.
    * @param container container to compute inner bounds for
    * @return inner bounds (with respect to container's insets)
    */
  private @NotNull Rectangle computeBounds (@NotNull Container container)
  {
    Rectangle bounds = container.getBounds ();
    Insets insets = container.getInsets ();
    if (insets != null)
    {
      return new Rectangle (insets.left,
                            insets.top,
                            bounds.width  - insets.left - insets.right,
                            bounds.height - insets.top  - insets.bottom);
    }
    else
    {
      return new Rectangle (bounds);
    }
  }

  /**
    * Compute sum of all row sizes.
    * @param rowSize row size array
    * @return sum of row sizes
    */
  private int computeRowSum (@NotNull int [] rowSize)
  {
    int sum = 0;
    for (int aRowSize : rowSize)
    {
      sum += aRowSize;
    }
    return sum;
  }

  /**
    * Compute sum of all column sizes.
    * @param colSize column size array
    * @return sum of column sizes
    */
  private int computeColSum (@NotNull int [] colSize)
  {
    int sum = 0;
    for (int aColSize : colSize)
    {
      sum += aColSize;
    }
    return sum;
  }

  /**
    * Adjust bounds, so that used space is horizontally and vertically
    * centered within the available space.
    * @param bounds bounds to adjust
    * @param rowSize array of row sizes
    * @param colSize array of column sizes
   * @return center bounds
    */
  private @NotNull Rectangle centerBounds (Rectangle bounds, @NotNull int [] rowSize, @NotNull int [] colSize)
  {
    bounds.x += (bounds.width  - computeColSum (colSize)) / 2;
    bounds.y += (bounds.height - computeRowSum (rowSize)) / 2;
    return bounds;
  }

  /**
    * SizeInfo computes size information for columns/rows.
    * @author Frank Gerberding
    */
  private abstract class SizeInfo
  {
    protected Element element;           // element to be sized
    protected int     minSize = 0;       // minimum size of column/row
    protected int     layoutMinSize = 0; // minimum layout size of column/row
    protected int     maxSize = 0;       // maximum size of column/row
    protected float   weight  = 0.0f;    // weight of column/row

    protected abstract Alignment getAlignment ();
    protected abstract float     getElementWeight ();
    protected abstract int       getElementSize (SizeGetter getter);
    protected abstract int       getInsetSize ();

    public void addElement (@NotNull SizeGetter getter, @NotNull Element element)
    {
      this.element = element;
      if (element != null)
      {
        if ((element.constraints != null) &&
            (getAlignment () == ALIGN_MAX))
        {
          this.minSize = Math.max (maxMaxSize + getInsetSize (),
                                   this.minSize);
          this.layoutMinSize = this.minSize;
        }
        else if ((element.constraints != null) &&
                 (getAlignment () == ALIGN_STRETCH))
        {
          this.weight  = Math.max (this.weight, getElementWeight ());
          this.minSize = (int)Math.max(this.minSize, getElementSize(getter) * getElementWeight()
              + getInsetSize());
          this.maxSize = Integer.MAX_VALUE;
        }
        else
        {
          this.minSize = Math.max (getElementSize(getter) + getInsetSize (),
                                   this.minSize);
          this.layoutMinSize = this.minSize;
        }
        if (this.maxSize < this.minSize)
        {
          this.maxSize = this.minSize;
        }
      }
    }

    public int getFixedSize ()
    {
      return this.minSize;
    }

  /**
    * get the layoutMinSize, i.e. the fixed size that is used by the layout.
    * This function also adds the weight of the actual element to sumWeight
    * and subtracts the layoutMinSize from the remainSpace.
    * @return the fixed layoutSize
    */
    public int getFixedLayoutSize ()
    {
      sumWeight += weight;
      remainSpace -= this.layoutMinSize;
      return this.layoutMinSize;
    }

  /**
    * get the size that is used for this row/column by the layout, i.e. the
    * fixed size plus - given a stretched row/column - an amount of the
    * remainSpace dependent on the weights.
    * @return the real layoutSize
    */
    public int getStretchedLayoutSize ()
    {
      return this.layoutMinSize + (int) (getFactor() * remainSpace);
    }

  public float getFactor()
  {
    return weight/sumWeight;
  }

  /**
    * get the weight of this row/column
    * @return the weight
    */
  public float getWeight()
  {
    return this.weight;
  }
}

  /**
    * Subclass of SizeInfo that computes size information for columns.
    * @author Frank Gerberding
    */
  private class ColumnInfo extends SizeInfo
  {
    protected @NotNull Alignment getAlignment ()
    {
      return element.constraints.alignmentX;
    }

    /**
      * get the weight of the actual element
      * @return the weight
      */
    protected float getElementWeight ()
    {
      return element.constraints.weightX;
    }

    protected int getElementSize (@NotNull SizeGetter getter)
    {
      int size = 0;
      if (getAlignment() == ALIGN_STRETCH)
      {
        size = getter.getStretchedSize().width;
      }
      return Math.max(size, getter.getSize(element.component).width);
    }

    protected int getInsetSize ()
    {
      Insets insets = element.constraints.insets;
      return insets.left + insets.right;
    }
  }

  /**
    * Subclass of SizeInfo that computes size information for rows.
    * @author Frank Gerberding
    */
  private class RowInfo extends SizeInfo
  {
    protected @NotNull Alignment getAlignment ()
    {
      return element.constraints.alignmentY;
    }

    /**
      * get the weight of the actual element
      * @return the weight
      */
    protected float getElementWeight ()
    {
      return element.constraints.weightY;
    }

    protected int getElementSize (@NotNull SizeGetter getter)
    {
      int size = 0;
      if (getAlignment() == ALIGN_STRETCH)
      {
        size = getter.getStretchedSize().height;
      }
      return Math.max(size, getter.getSize(element.component).height);
    }

    protected int getInsetSize ()
    {
      Insets insets = element.constraints.insets;
      return insets.top + insets.bottom;
    }
  }

  /**
    * Compute the needed width.
    * @param getter the sizeGetter that should be used (->preferred, min, max)
    * @param grid grid of elements
    * @return the widths
    */
  private int computeWidth (@NotNull SizeGetter getter, @NotNull Element [][] grid)
  {
    maxMaxSize = computeMaxMaxWidth(getter);

    // prepare size info
    SizeInfo [] sizeInfo = new SizeInfo [columns];
    for (int col = 0; col < columns; col++)
    {
      sizeInfo [col] = new ColumnInfo();
      for (int row = 0; row < rows; row++)
      {
        sizeInfo [col].addElement(getter, grid[col][row]);
      }
    }

    //compute the fixed colSizes that are used by the layout
    // create column size array and get fixed column sizes
    // compute the sum of all weights
    int [] layoutColSize = new int [columns];
    int [] colSize = new int [columns];
    sumWeight   = 0.0f;
    for (int col = 0; col < columns; col++)
    {
      layoutColSize[col] = sizeInfo[col].getFixedLayoutSize ();
      colSize[col] = sizeInfo[col].getFixedSize ();
    }

    // compute the fixed and stretched needed space
    int fixedSpace = 0;
    float stretchedSpace = 0;

    for (int col=0; col<columns; col++)
    {
      if (sizeInfo[col].getWeight() > 0.0)
      {  // => the column col is stretched
        stretchedSpace = Math.max(stretchedSpace,
                                  (colSize[col] - layoutColSize[col])
                                  /sizeInfo[col].getFactor());
      }
      if (Integer.MAX_VALUE - fixedSpace >= layoutColSize[col])
      {
        fixedSpace += layoutColSize[col];
      }
      else
      {
        fixedSpace = Integer.MAX_VALUE;
      }
    }

    return fixedSpace + (int)stretchedSpace;
  }

  /**
    * Compute the needed height.
    * @param getter the sizeGetter that should be used (->preferred, min, max)
    * @param grid grid of elements
    * @return the height
    */
  private int computeHeight (@NotNull SizeGetter getter, @NotNull Element [][] grid)
  {
    maxMaxSize = computeMaxMaxHeight (getter);

    // prepare size info
    SizeInfo [] sizeInfo = new SizeInfo [rows];
    for (int row = 0; row < rows; row++)
    {
      sizeInfo [row] = new RowInfo();
      for (int col = 0; col < columns; col++)
      {
        sizeInfo [row].addElement(getter, grid[col][row]);
      }
    }

    //compute the rowSizes that are used by the layout
    // create row size array and get fixed row sizes
    // compute the sum of all weights
    int [] layoutRowSize = new int [rows];
    int [] rowSize = new int [rows];
    sumWeight   = 0.0f;
    for (int row = 0; row < rows; row++)
    {
      layoutRowSize[row] = sizeInfo[row].getFixedLayoutSize ();
      rowSize[row] = sizeInfo[row].getFixedSize ();
    }

    // compute the fixed and stretched needed space
    int fixedSpace = 0;
    float stretchedSpace = 0;

    for (int row=0; row<rows; row++)
    {
      if (sizeInfo[row].getWeight() > 0.0)
      {  // => the row row is stretched
        stretchedSpace = Math.max(stretchedSpace,
                                  (rowSize[row] - layoutRowSize[row])
                                  /sizeInfo[row].getFactor());
      }
      if (Integer.MAX_VALUE - fixedSpace >= layoutRowSize[row])
      {
        fixedSpace += layoutRowSize[row];
      }
      else
      {
        fixedSpace = Integer.MAX_VALUE;
      }

    }
    return fixedSpace + (int)stretchedSpace;
  }

  /**
    * Compute max width of all ALIGN_MAX elements
    * @param getter the sizeGetter that should be used (->preferred, min, max)
    * @return the max width
    */
  private int computeMaxMaxWidth (@NotNull SizeGetter getter)
  {
    int max = 0;
    for (Element element : components)
    {
      if (element != null &&
          element.constraints != null &&
          element.constraints.alignmentX == ALIGN_MAX)
      {
        max = Math.max (max,
                        getter.getSize(element.component).width);
      }
    }
    return max;
  }

  /**
    * Compute max height of all ALIGN_MAX elements
    * @param getter the sizeGetter that should be used (->preferred, min, max)
    * @return the max height
    */
  private int computeMaxMaxHeight (@NotNull SizeGetter getter)
  {
    int max = 0;
    for (Element element : components)
    {
      if (element != null &&
          element.constraints != null &&
          element.constraints.alignmentY == ALIGN_MAX)
      {
        max = Math.max (max,
                        getter.getSize(element.component).height);
      }
    }
    return max;
  }

  /**
    * Compute column sizes of all columns.
    * @param bounds bounds of available space
    * @param grid grid of elements
    * @return array of all column widths
    */
  private @NotNull int [] computeColumnSizes (@NotNull Rectangle bounds, @NotNull Element [][] grid)
  {
    SizeGetter getter = new PreferredSizeGetter ();

    // compute max width of all ALIGN_MAX elements
    maxMaxSize = computeMaxMaxWidth(getter);

    // prepare size info
    SizeInfo [] sizeInfo = new SizeInfo [columns];
    for (int col = 0; col < columns; col++)
    {
      sizeInfo [col] = new ColumnInfo ();
      for (int row = 0; row < rows; row++)
      {
        sizeInfo [col].addElement (getter, grid [col][row]);
      }
    }
    // create column size array and get fixed column sizes
    // compute remaining space and sum of all weights
    int [] colSize = new int [columns];
    remainSpace = bounds.width;
    sumWeight   = 0.0f;
    for (int col = 0; col < columns; col++)
    {
      colSize [col] = sizeInfo [col].getFixedLayoutSize ();
    }
    for (int col = 0; col < columns; col++)
    {
    }
    // get stretched column sizes
    for (int col = 0; col < columns; col++)
    {
      colSize [col] = sizeInfo [col].getStretchedLayoutSize ();
    }
    return colSize;
  }

  /**
    * Compute row sizes of all rows.
    * @param bounds bounds of available space
    * @param grid grid of elements
    * @return array of all row heights
    */
  private @NotNull int [] computeRowSizes (@NotNull Rectangle bounds, @NotNull Element [][] grid)
  {
    SizeGetter getter = new PreferredSizeGetter ();

    // compute max height of all ALIGN_MAX elements
    maxMaxSize = computeMaxMaxWidth(getter);

    // prepare size info
    SizeInfo [] sizeInfo = new SizeInfo [rows];
    for (int row = 0; row < rows; row++)
    {
      sizeInfo [row] = new RowInfo ();
      for (int col = 0; col < columns; col++)
      {
        sizeInfo [row].addElement (getter, grid [col][row]);
      }
    }

    // create row size array and get fixed row sizes
    // compute remaining space and sum of all weights
    int [] rowSize = new int [rows];
    remainSpace = bounds.height;
    sumWeight   = 0.0f;
    for (int row = 0; row < rows; row++)
    {
      rowSize [row] = sizeInfo [row].getFixedLayoutSize ();
    }
    for (int row = 0; row < rows; row++)
    {
    }
    // get stretched row sizes
    for (int row = 0; row < rows; row++)
    {
      rowSize [row] = sizeInfo [row].getStretchedLayoutSize ();
    }
    return rowSize;
  }

  /**
    * Compute the layout for all components, that reside within the container
    * with respect to the bounds of the parent container.
    * @param parent parent container information
    */
  public void layoutContainer (@NotNull Container parent)
  {
    // compute available space
    Rectangle bounds = computeBounds (parent);
    // create grid of component-constraint-pairs
    Element [][] grid = createGrid ();
    // compute column sizes
    int [] colSize = computeColumnSizes (bounds, grid);
    // compute row sizes
    int [] rowSize = computeRowSizes (bounds, grid);
    // adjust bounds to center used space
    bounds = centerBounds (bounds, rowSize, colSize);
    // layout components
    int currentY = bounds.y;
    for (int row = 0; row < rows; row++)
    {
      int currentX = bounds.x;
      for (int col = 0; col < columns; col++)
      {
        if (grid [col][row] != null)
        {
          grid [col][row].sizeComponent (currentX, currentY,
                                         colSize [col], rowSize [row]);
        }
        currentX += colSize [col];
      }
      currentY += rowSize [row];
    }
  }
}

//-----------------------------------------------------------
//------- specialized subclasses of FlexLayout --------------
//-----------------------------------------------------------

/**
  * Special implementation of FlexLayout, that fixed number of columns
  * and an arbritrary number of rows.
  * @author Frank Gerberding
  */
class FlexLayoutH extends FlexLayout
{
  /**
    * Construct a new horizontal FlexLayout with the given number of columns.
    * @param columns fixed number of columns to use for this layout
    */
  public FlexLayoutH (int columns)
  {
    this.columns = columns;
    this.rows    = 0;
  }

  /**
    * Create a grid of elements.
    * @return grid of elements
    */
  protected @NotNull Element [][] createGrid ()
  {
    int size = components.size ();
    rows = (int) (Math.ceil (size / (double) columns));
    Element [][] grid = new Element [columns][rows];
    for (int i = 0; i < size; i++)
    {
      grid [i % columns][i / columns] = components.get (i);
    }
    for (int i = size; i < columns * rows; i++)
    {
      grid [i % columns][i / columns] = null;
    }
    return grid;
  }
}

//-----------------------------------------------------------

/**
  * Special implementation of FlexLayout, that fixed number of rows
  * and an arbritrary number of columns.
  * @author Frank Gerberding
  */
class FlexLayoutV extends FlexLayout
{
  /**
    * Construct a new vertical FlexLayout with the given number of rows.
    * @param rows fixed number of rows to use for this layout
    */
  public FlexLayoutV (int rows)
  {
    this.columns = 0;
    this.rows    = rows;
  }

  /**
    * Create a grid of elements.
    * @return grid of elements
    */
  protected @NotNull Element [][] createGrid ()
  {
    int size = components.size ();
    columns = (int) (Math.ceil (size / (double) rows));
    Element [][] grid = new Element [columns][rows];
    for (int i = 0; i < size; i++)
    {
      grid [i / rows][i % rows] = components.get (i);
    }
    for (int i = size; i < columns * rows; i++)
    {
      grid [i / rows][i % rows] = null;
    }
    return grid;
  }
}
