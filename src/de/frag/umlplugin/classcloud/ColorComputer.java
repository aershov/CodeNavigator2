package de.frag.umlplugin.classcloud;

import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Computes colors for class cloud nodes.
 */
public class ColorComputer
{
  private static final double SCALE = 0.01;

  /**
   * Computes node color for class cloud node with given preferred coordinates and class cloud size.
   * @param preferredX preferred x-coordinate (0..n means left to right)
   * @param preferredY preferred y-coordinate (0..n means top to bottom)
   * @param width with of class cloud
   * @param height height of class cloud
   * @param settings settings
   * @return computed node color
   */
  public static @NotNull Color computeColor (double preferredX, double preferredY, double width, double height,
                                             @NotNull Settings settings)
  {
    double distanceX = preferredX / width;
    double distanceY = preferredY / height;
    double x = Math.max (Math.abs (distanceX - 0.5) * 2 - SCALE, 0) / (1 - SCALE);
    double y = Math.max (Math.abs (distanceY - 0.5) * 2 - SCALE, 0) / (1 - SCALE);
    return fade2D (x, y, settings);
  }

  /**
   * Compute a 2-dimensional color fade.
   * @param x x-fade-factor (0..1)
   * @param y y-fade-factor (0..1)
   * @param settings settings
   * @return computed color
   */
  private static @NotNull Color fade2D (double x, double y, @NotNull Settings settings)
  {
    Color normal   = settings.getNormalCloudColor ();
    Color used     = settings.getUsedCloudColor ();
    Color extended = settings.getExtendedCloudColor ();
    Color mixed    = new Color ((used.getRed   () + extended.getRed   ()) / 2,
                                (used.getGreen () + extended.getGreen ()) / 2,
                                (used.getBlue  () + extended.getBlue  ()) / 2);
    int r1 = (int) ((1 - x) * normal.getRed   () + x * used.getRed ());
    int g1 = (int) ((1 - x) * normal.getGreen () + x * used.getGreen ());
    int b1 = (int) ((1 - x) * normal.getBlue  () + x * used.getBlue ());

    int r2 = (int) ((1 - x) * extended.getRed   () + x * mixed.getRed ());
    int g2 = (int) ((1 - x) * extended.getGreen () + x * mixed.getGreen ());
    int b2 = (int) ((1 - x) * extended.getBlue  () + x * mixed.getBlue ());

    int r = (int) ((1 - y) * r1 + y * r2);
    int g = (int) ((1 - y) * g1 + y * g2);
    int b = (int) ((1 - y) * b1 + y * b2);
    return new Color (r, g, b);
  }
}
