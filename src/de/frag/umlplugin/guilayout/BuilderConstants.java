package de.frag.umlplugin.guilayout;

import static de.frag.umlplugin.guilayout.FlexConstants.*;

import java.awt.*;

/**
  * Some useful constants for layout builder or FlexLayout.
  * @author Frank Gerberding
  */
public class BuilderConstants
{
  private BuilderConstants () {}

  /** Constraints for fixed size left aligned components */
  public static final FlexConstraints LEFT         = new FlexConstraints (ALIGN_LEFT, ALIGN_CENTER, 0.0f, 0.0f, null);
  /** Constraints for fixed size right aligned components */
  public static final FlexConstraints RIGHT        = new FlexConstraints (ALIGN_RIGHT, ALIGN_CENTER, 0.0f, 0.0f, null);
  /** Constraints for fixed size top aligned components */
  public static final FlexConstraints TOP          = new FlexConstraints (ALIGN_CENTER, ALIGN_TOP, 0.0f, 0.0f, null);
  /** Constraints for fixed size bottom aligned components */
  public static final FlexConstraints BOTTOM       = new FlexConstraints (ALIGN_CENTER, ALIGN_BOTTOM, 0.0f, 0.0f, null);
  /** Constraints for fixed size center aligned components */
  public static final FlexConstraints CENTER       = new FlexConstraints (ALIGN_CENTER, ALIGN_CENTER, 0.0f, 0.0f, null);
  /** Constraints for fixed size top left aligned components */
  public static final FlexConstraints TOP_LEFT     = new FlexConstraints (ALIGN_LEFT, ALIGN_TOP, 0.0f, 0.0f, null);
  /** Constraints for fixed size top right aligned components */
  public static final FlexConstraints TOP_RIGHT    = new FlexConstraints (ALIGN_RIGHT, ALIGN_TOP, 0.0f, 0.0f, null);
  /** Constraints for fixed size bottom left aligned components */
  public static final FlexConstraints BOTTOM_LEFT  = new FlexConstraints (ALIGN_LEFT, ALIGN_BOTTOM, 0.0f, 0.0f, null);
  /** Constraints for fixed size bottom right aligned components */
  public static final FlexConstraints BOTTOM_RIGHT = new FlexConstraints (ALIGN_RIGHT, ALIGN_BOTTOM, 0.0f, 0.0f, null);
  /** Constraints for vertically centered, horizontally stretched components.
    * This is useful for text fields, combo boxes and similar components. */
  public static final FlexConstraints STRETCH_X    = new FlexConstraints (ALIGN_STRETCH, ALIGN_CENTER, 1.0f, 0.0f, null);
  /** Constraints for horizontally centered, vertically stretched components */
  public static final FlexConstraints STRETCH_Y    = new FlexConstraints (ALIGN_CENTER, ALIGN_STRETCH, 0.0f, 1.0f, null);
  /** Constraints for horizontally and vertically stretched components.
    * This is useful for tables, trees, lists and similar components. */
  public static final FlexConstraints STRETCH_XY   = new FlexConstraints (ALIGN_STRETCH, ALIGN_STRETCH, 1.0f, 1.0f, null);
  /** Constraints for vertically centered, horizontally filled components.
    * This is useful for buttons and similar components. */
  public static final FlexConstraints FILL_X       = new FlexConstraints (ALIGN_FILL, ALIGN_CENTER, 0.0f, 0.0f, null);
  /** Constraints for horizontally centered, vertically filled components */
  public static final FlexConstraints FILL_Y       = new FlexConstraints (ALIGN_CENTER, ALIGN_FILL, 0.0f, 0.0f, null);
  /** Constraints for horizontally and vertically filled components */
  public static final FlexConstraints FILL_XY      = new FlexConstraints (ALIGN_FILL, ALIGN_FILL, 0.0f, 0.0f, null);
  /** Constraints for right aligned labels with 4-pixel insets left and right */
  public static final FlexConstraints LABEL        = new FlexConstraints (ALIGN_LEFT, ALIGN_CENTER, 0.0f, 0.0f, new Insets (0, 4, 0, 4));
  /** Constraints for vertically centered, horizontally equal-sized components */
  public static final FlexConstraints MAX_X        = new FlexConstraints (ALIGN_MAX, ALIGN_CENTER, 0.0f, 0.0f, null);
  /** Constraints for horizintally centered, vertically equal-sized components */
  public static final FlexConstraints MAX_Y        = new FlexConstraints (ALIGN_CENTER, ALIGN_MAX, 0.0f, 0.0f, null);
  /** Constraints for horizintally and vertically equal-sized components */
  public static final FlexConstraints MAX_XY       = new FlexConstraints (ALIGN_MAX, ALIGN_MAX, 0.0f, 0.0f, null);
}
