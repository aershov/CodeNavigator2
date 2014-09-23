package de.frag.umlplugin.actions;

import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

/**
 * Action for zooming out of view.
 */
public class ZoomOutAction extends AbstractZoomAction
{
  /**
   * Gets zoom factor.
   * @param settings seetings
   * @return zoom factor
   */
  protected double getZoomFactor (@NotNull Settings settings)
  {
    return settings.getZoomFactor ();
  }
}