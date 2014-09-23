package de.frag.umlplugin.actions;

import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

/**
 * Action for zooming into view.
 */
public class ZoomInAction extends AbstractZoomAction
{
  /**
   * Gets zoom factor.
   * @param settings seetings
   * @return zoom factor
   */
  protected double getZoomFactor (@NotNull Settings settings)
  {
    return 1.0 / settings.getZoomFactor ();
  }
}