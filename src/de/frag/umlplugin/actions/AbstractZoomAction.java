package de.frag.umlplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.view.Graph2DView;
import de.frag.umlplugin.Graph2DViewConsumer;
import de.frag.umlplugin.Graph2DViewProvider;
import de.frag.umlplugin.anim.AnimationFactory;
import de.frag.umlplugin.anim.AnimationListenerAdapter;
import de.frag.umlplugin.anim.AnimationObject;
import de.frag.umlplugin.anim.AnimationPlayer;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract zoom action.
 */
public abstract class AbstractZoomAction extends AnAction implements Graph2DViewConsumer
{
  private Graph2DViewProvider graph2DViewProvider;

  /**
   * Injects Graph2DViewProvider.
   * @param graph2DViewProvider Graph2DViewProvider to inject
   */
  public void setGraph2DViewProvider (@NotNull Graph2DViewProvider graph2DViewProvider)
  {
    this.graph2DViewProvider = graph2DViewProvider;
  }

  /**
   * Zooms into graph.
   * @param e event
   */
  public void actionPerformed (AnActionEvent e)
  {
    Settings settings = Settings.getSettings ();
    Graph2DView graph2DView = graph2DViewProvider.getGraph2DView (e.getDataContext ());
    if (graph2DView != null)
    {
      double zoom = graph2DView.getZoom () * getZoomFactor (settings);
      if (settings.isAnimateNavigation ())
      {
        AnimationFactory factory = new AnimationFactory ();
        AnimationObject animation = factory.zoom (graph2DView, zoom, settings.getAnimationDuration () / 3);
        AnimationPlayer player = new AnimationPlayer ();
        player.addAnimationListener (new AnimationListenerAdapter (graph2DView));
        player.animate (animation);
      }
      else
      {
        graph2DView.setZoom (zoom);
        graph2DView.updateView ();
        graph2DView.adjustScrollBarVisibility ();
      }
    }
  }

  /**
   * Gets zoom factor.
   * @param settings seetings
   * @return zoom factor
   */
  protected abstract double getZoomFactor (@NotNull Settings settings);
}
