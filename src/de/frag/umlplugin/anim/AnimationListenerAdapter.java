package de.frag.umlplugin.anim;

import com.intellij.openapi.graph.view.Graph2DView;
import org.jetbrains.annotations.NotNull;

/**
 * Adapter from animation listener to Graph2DView.
 */
public class AnimationListenerAdapter implements AnimationListener
{
  private final Graph2DView view;

  public AnimationListenerAdapter (@NotNull Graph2DView view)
  {
    this.view = view;
  }

  public void animationPerformed ()
  {
    view.getJComponent ().repaint ();
  }
}
