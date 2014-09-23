package de.frag.umlplugin.anim;

/**
 * Abstract base class for animation objects.
 */
public abstract class AbstractAnimationObject implements AnimationObject
{
  private final long preferredDuration;

  public AbstractAnimationObject (long preferredDuration)
  {
    this.preferredDuration = preferredDuration;
  }

  public void initAnimation ()
  {
  }

  public void disposeAnimation ()
  {
  }

  public long preferredDuration ()
  {
    return preferredDuration;
  }
}

