
package de.frag.umlplugin.guilayout;

/**
  * Some useful constants for flex layout GUIs.
  * @author Frank Gerberding
  */
public class FlexConstants
{
  private FlexConstants () {}

  /** Constant for horizontal component alignment in flex layout.
    * <code>ALIGN_LEFT</code> aligned components will get their preferred
    * width with empty space to the right of the component to fill up
    * the cell space.
    */
  public static final Alignment ALIGN_LEFT = new Alignment.StartAlignment ();

  /** Constant for horizontal component alignment in flex layout.
    * <code>ALIGN_RIGHT</code> aligned components will get their preferred
    * width with empty space to the left of the component to fill up
    * the cell space.
    */
  public static final Alignment ALIGN_RIGHT = new Alignment.EndAlignment ();

  /** Constant for vertical component alignment in flex layout.
    * <code>ALIGN_TOP</code> aligned components will get their preferred height
    * with empty space to the bottom of the component to fill up
    * the cell space.
    */
  public static final Alignment ALIGN_TOP = new Alignment.StartAlignment ();

  /** Constant for veetical component alignment in flex layout.
    * <code>ALIGN_BOTTOM</code> aligned components will get their preferred
    * height with empty space to the top of the component to fill up
    * the cell space.
    */
  public static final Alignment ALIGN_BOTTOM = new Alignment.EndAlignment ();

  /** Constant for horizontal or vertical component alignment in flex layout.
    * <code>ALIGN_CENTER</code> aligned components will get their preferred
    * width or height with empty space to the left and right or top and height
    * of the component to fill up the cell space.
    */
  public static final Alignment ALIGN_CENTER = new Alignment.CenterAlignment ();

  /** Constant for horizontal or vertical component alignment in flex layout.
    * <code>ALIGN_FILL</code> aligned components will get the cell width
    * or height to fill up the cell space.
    */
  public static final Alignment ALIGN_FILL = new Alignment.FillAlignment ();

  /** Constant for horizontal or vertical component alignment in flex layout.
    * <code>ALIGN_STRETCH</code> aligned components will stretch the cell size
    * in horizontal or vertical direction and the component itself will fill
    * the stretched space.
    */
  public static final Alignment ALIGN_STRETCH = new Alignment.StretchAlignment ();
  
  /** Constant for horizontal or vertical component alignment in flex layout.
    * <code>ALIGN_MAX</code> aligned components will get the maximum
    * preferred width or height of all <code>ALIGN_MAX</code> aligned
    * components of the same container.
    */
  public static final Alignment ALIGN_MAX = new Alignment.MaxAlignment (); 
}
