package de.frag.umlplugin.anim;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Plays animations.
 */
@SuppressWarnings ({"EmptyCatchBlock"})
public class AnimationPlayer
{
  private boolean playing  = false;
  private final Collection<AnimationListener> listeners = new ArrayList<AnimationListener> ();

  /**
   * Adds an animation listener.
   * @param listener animation listener to add
   */
  public void addAnimationListener (@NotNull AnimationListener listener)
  {
    listeners.add (listener);
  }

  /**
   * Class all registered listeners.
   */
  private void callListeners ()
  {
    for (AnimationListener listener : listeners)
    {
      listener.animationPerformed ();
    }
  }

  /**
   * Plays the animation represented by the given animation object
   * @param animationObject animation object to be played
   */
  public void animate (@NotNull final AnimationObject animationObject)
  {
    Thread thread = new Thread (new Runnable () {
      public void run ()
      {
        try
        {
          playing = true;
          animationObject.initAnimation ();
          double time = 0;
          long preferredDuration = animationObject.preferredDuration ();
          long startTime = System.currentTimeMillis ();
          while (playing && time <= 1.0)
          {
            animationObject.calcFrame (time);
            callListeners ();
            long timeElapsed = System.currentTimeMillis () - startTime;
            time = (double) timeElapsed / (double) preferredDuration;
          }
          animationObject.calcFrame (1.0);
          animationObject.disposeAnimation ();
          callListeners ();
        }
        catch (Exception e)
        {
          // try to catch some of the bugs in idea's graph framework wrappers
          animationObject.calcFrame (1.0);
          animationObject.disposeAnimation ();
          callListeners ();
        }
        finally
        {
          playing = false;
        }
      }
    });
    thread.start ();
  }
}
