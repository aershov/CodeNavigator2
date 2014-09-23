
package de.frag.umlplugin.guilayout;

import static de.frag.umlplugin.guilayout.FlexConstants.ALIGN_CENTER;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
  * FlexConstraints describe layout constraints for FlexLayout.
  * @see FlexLayout
  * @see LayoutBuilder
  */
public class FlexConstraints
{
  public static final Insets NULL_INSETS = new Insets (0, 0, 0, 0);

  /** Horizontal alignment of components. This should be one of 
    * <code>ALIGN_LEFT</code>, <code>ALIGN_RIGHT</code>,
    * <code>ALIGN_CENTER</code>, <code>ALIGN_FILL</code>,
    * <code>ALIGN_MAX</code> or <code>ALIGN_STRETCH</code>.
    */
  public final Alignment alignmentX;

  /** Vertical alignment of components. This should be one of 
    * <code>ALIGN_TOP</code>, <code>ALIGN_BOTTOM</code>,
    * <code>ALIGN_CENTER</code>, <code>ALIGN_FILL</code>,
    * <code>ALIGN_MAX</code> or <code>ALIGN_STRETCH</code>.
    */
  public final Alignment alignmentY;

  /** Horizontal weight of component. If the horizontal alignment is
    * <code>ALIGN_STRETCH</code>, then the weight specifies how much space
    * will be reserved for this component in relation to other stretchable
    * components of the same row, else this value will be ignored.
    */
  public final float weightX;

  /** Vertical weight of component. If the vertical alignment is
    * <code>ALIGN_STRETCH</code>, then the weight specifies how much space
    * will be reserved for this component in relation to other stretchable
    * components of the same column, else this value will be ignored.
    */
  public final float weightY;
  
  /** Insets of a component. Additional space will be reserved to the top,
    * left, bottom and right of the component according to the values
    * of the insets. If this is <code>null</code>, it will behave like
    * <code>Insets (0, 0, 0, 0)</code>.
    */
  public Insets insets;
  
  /**
    * Construct a new FlexConstraints object with default values.
    */
  public FlexConstraints ()
  {
    alignmentX = ALIGN_CENTER;
    alignmentY = ALIGN_CENTER;
    weightX    = 0.0f;
    weightY    = 0.0f;
    insets     = NULL_INSETS;
  }

  /**
    * Construct a new FlexConstraints object by copying the given constraints.
    * @param constraints constraints to copy
    */
  public FlexConstraints (@NotNull  FlexConstraints constraints)
  {
    alignmentX = constraints.alignmentX;
    alignmentY = constraints.alignmentY;
    weightX    = constraints.weightX;
    weightY    = constraints.weightY;
    insets     = constraints.insets;
  }

  /**
    * Construct a new FlexConstraints object with the given values.
    * @param alignmentX Horizontal alignment of components. This should be
    *                   one of <code>ALIGN_LEFT</code>,
    *                   <code>ALIGN_RIGHT</code>, <code>ALIGN_FILL</code>
    *                   <code>ALIGN_CENTER</code>, <code>ALIGN_MAX</code>
    *                   or <code>ALIGN_STRETCH</code>.
    * @param alignmentY Vertical alignment of components. This should be
    *                   one of <code>ALIGN_TOP</code>,
    *                   <code>ALIGN_BOTTOM</code>, <code>ALIGN_FILL</code>
    *                   <code>ALIGN_CENTER</code>, <code>ALIGN_MAX</code>
    *                   or <code>STRETCH</code>.
    * @param weightX    Horizontal weight of component. If the horizontal
    *                   alignment is <code>ALIGN_STRETCH</code>, then the
    *                   weight specifies how much space will be reserved for
    *                   this component in relation to other stretchable
    *                   components of the same row, else this value will be
    *                   ignored.
    * @param weightY    Vertical weight of component. If the vertical
    *                   alignment is <code>ALIGN_STRETCH</code>, then the weight
    *                   specifies how much space will be reserved for this
    *                   component in relation to other stretchable components
    *                   of the same column, else this value will be ignored.
    * @param insets     Insets of a component. Additional space will be
    *                   reserved to the top, left, bottom and right of the
    *                   component according to the values of the insets. If
    *                   this is <code>null</code>, it will behave like
    *                   <code>Insets (0, 0, 0, 0)</code>.
    */
  public FlexConstraints (@NotNull Alignment alignmentX, @NotNull Alignment alignmentY,
                          float weightX, float weightY, @Nullable Insets insets)
  {
    this.alignmentX = alignmentX;
    this.alignmentY = alignmentY;
    this.weightX    = weightX;
    this.weightY    = weightY;
    this.insets     = (insets == null) ? NULL_INSETS : insets;
  }
  
  public String toString ()
  {
    return "FlexConstraints [alignmentX = "+alignmentX+", alignmentY = "+alignmentY+", weightX = "+weightX+", weightY = "+weightY+", insets = "+insets+"]";
  }
}
