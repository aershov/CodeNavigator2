package de.frag.umlplugin.anim;

/**
 * Mock for animation object tests.
 */
public class MockAnimationObject implements AnimationObject
{
  private final long preferredDuration;
  private boolean initialized = false;
  private boolean disposed    = false;
  private double  time;
  private long    millis;

  public MockAnimationObject (long preferredDuration)
  {
    this.preferredDuration = preferredDuration;
  }

  /**
   * Initializes the animation object. This method should be called prior to calling calcFrame.
   */
  public void initAnimation ()
  {
    initialized = true;
    disposed    = false;
  }

  /**
   * Calculates the animation frame for the specified point in time. The valid time interval for animations is always
   * [0.0, 1.0].
   * @param time a point in [0.0, 1.0]
   */
  public void calcFrame (double time)
  {
    this.time = time;
    this.millis = (long) (preferredDuration * time);
  }

  /**
   * Disposes the animation object. This method is invoked by an animation player after the last invocation of calcFrame
   * was performed or AnimationPlayer.stop() has been invoked.
   */
  public void disposeAnimation ()
  {
    disposed    = true;
    initialized = false;
  }

  /**
   * Returns the preferred animation length in milliseconds that the display of all provided animation frames should
   * last. NOTE: The preferred duration of an animation has to be greater than 0!
   * @return the preferred duration, which has to be greater than 0
   */
  public long preferredDuration ()
  {
    return preferredDuration;
  }

  public boolean isInitialized ()
  {
    return initialized;
  }

  public boolean isDisposed ()
  {
    return disposed;
  }

  public double getTime ()
  {
    return time;
  }

  public long getMillis ()
  {
    return millis;
  }
}
