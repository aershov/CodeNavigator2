package de.frag.umlplugin.anim;

/**
 * A general interface for objects that perform a sequence of animation steps.
 * Animation objects are executed by an animation player.
 */
public interface AnimationObject
{
  /**
   * Initializes the animation object. This method should be called prior to calling calcFrame.
   */
  public void initAnimation ();

  /**
   * Calculates the animation frame for the specified point in time.
   * The valid time interval for animations is always [0.0, 1.0].
   * @param time a point in [0.0, 1.0]
   */
  public void calcFrame (double time);

  /**
   * Disposes the animation object. This method is invoked by an animation player after the last invocation
   * of calcFrame was performed or AnimationPlayer.stop() has been invoked.
   */
  public void disposeAnimation ();

  /**
   * Returns the preferred animation length in milliseconds that the display of all provided animation
   * frames should last. NOTE: The preferred duration of an animation has to be greater than 0! 
   * @return the preferred duration, which has to be greater than 0
   */
  public long preferredDuration ();
}
