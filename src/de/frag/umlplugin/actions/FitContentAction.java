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

import java.awt.*;

/**
 * Action for fitting view contents.
 */
public class FitContentAction extends AnAction implements Graph2DViewConsumer
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
   * Updates the state of the action.
   * @param e Carries information on the invocation place and data available
   */
  public void update (AnActionEvent e)
  {
    Graph2DView graph2DView = graph2DViewProvider.getGraph2DView (e.getDataContext ());
    boolean selectable = graph2DView != null;
    e.getPresentation ().setEnabled (selectable);
  }

  /**
   * Fits contents.
   * @param e event
   */
  public void actionPerformed (AnActionEvent e)
  {
    Graph2DView graph2DView = graph2DViewProvider.getGraph2DView (e.getDataContext ());
    if (graph2DView != null)
    {
      Settings settings = Settings.getSettings ();
      Rectangle rectangle = graph2DView.getGraph2D ().getBoundingBox ();
      if (settings.isAnimateNavigation ())
      {
        AnimationFactory factory = new AnimationFactory ();
        AnimationObject animation = factory.fitRectangle (graph2DView, rectangle, settings.getAnimationDuration () / 3);
        AnimationPlayer player = new AnimationPlayer ();
        player.addAnimationListener (new AnimationListenerAdapter (graph2DView));
        player.animate (animation);
      }
      else
      {
        graph2DView.fitRectangle (rectangle);
        graph2DView.updateView ();
        graph2DView.adjustScrollBarVisibility ();
      }
    }
  }
}