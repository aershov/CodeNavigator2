package de.frag.umlplugin.anim;

import org.jetbrains.annotations.NotNull;

/**
 * Factory that creates composite animation objects.
 */
public class CompositeAnimationFactory
{
  /**
   * Creates a composite animation object that animates all child animation objects simulaneously.
   * The preferred duration of the created object will be the maximum duration of all child objects.
   * @return created composite animation object
   */
  public static @NotNull CompositeAnimationObject createConcurrency ()
  {
    return new Concurrency ();
  }

  /**
   * Creates a composite animation object that animates all child animation objects simulaneously.
   * The preferred duration of the created object will be the maximum duration of all child objects.
   * The preferred duration of all child objects will be scaled up to the preferred duration of the
   * created composite animation object.
   * @return created composite animation object
   */
  public static @NotNull CompositeAnimationObject createScalingConcurrency ()
  {
    return new ScalingConcurrency ();
  }

  /**
   * Creates a composite animation object that animates all child animation objects in sequence.
   * The initAnimation and disposeAnimation methods of all child objects will be called during
   * the initAnimation and disposeAnimation of the created composite animation object.
   * @return created composite animation object
   */
  public static @NotNull CompositeAnimationObject createSequence ()
  {
    return new Sequence ();
  }

  /**
   * Concurrent composite animation object.
   */
  private static class Concurrency extends AbstractCompositeAnimationObject
  {
    private long duration;

    public void initAnimation ()
    {
      for (AnimationObject child : children)
      {
        child.initAnimation ();
      }
      duration = preferredDuration ();
    }

    public void calcFrame (double time)
    {
      for (AnimationObject child : children)
      {
        long childDuration = child.preferredDuration ();
        double childTime = Math.min (1.0, time * ((double) duration / (double) childDuration));
        child.calcFrame (childTime);
      }
    }

    public void disposeAnimation ()
    {
      for (AnimationObject child : children)
      {
        child.disposeAnimation ();
      }
    }

    public long preferredDuration ()
    {
      long max = 0;
      for (AnimationObject child : children)
      {
        max = Math.max (max, child.preferredDuration ());
      }
      return max;
    }
  }

  /**
   * Concurrent composite animation object.
   */
  private static class ScalingConcurrency extends AbstractCompositeAnimationObject
  {
    public void initAnimation ()
    {
      for (AnimationObject child : children)
      {
        child.initAnimation ();
      }
    }

    public void calcFrame (double time)
    {
      for (AnimationObject child : children)
      {
        child.calcFrame (time);
      }
    }

    public void disposeAnimation ()
    {
      for (AnimationObject child : children)
      {
        child.disposeAnimation ();
      }
    }

    public long preferredDuration ()
    {
      long max = 0;
      for (AnimationObject child : children)
      {
        max = Math.max (max, child.preferredDuration ());
      }
      return max;
    }
  }

  /**
   * Concurrent composite animation object.
   */
  private static class Sequence extends AbstractCompositeAnimationObject
  {
    private long            preferredDuration;
    private int             currentIndex;
    private AnimationObject currentChild;
    private long            currentChildStart;
    private long            currentChildDuration;

    public void initAnimation ()
    {
      for (AnimationObject child : children)
      {
        child.initAnimation ();
      }
      currentIndex = 0;
      preferredDuration = preferredDuration ();
      currentChildStart = 0;
      if (children.isEmpty ())
      {
        currentChildDuration = 0;
      }
      else
      {
        currentChild = children.get (0);
        currentChildDuration = currentChild.preferredDuration ();
      }
    }

    public void calcFrame (double time)
    {
      long millis = (long) (time * preferredDuration);
      double childTime = (double) (millis - currentChildStart) / (double) currentChildDuration;
      if (childTime > 1.0)
      {
        currentChild.calcFrame (1.0);
        currentIndex++;
        if (currentIndex < children.size ())
        {
          currentChild = children.get (currentIndex);
          currentChildStart += currentChildDuration;
          currentChildDuration = currentChild.preferredDuration ();
        }
        childTime = (double) (millis - currentChildStart) / (double) currentChildDuration;
      }
      currentChild.calcFrame (childTime);
    }

    public void disposeAnimation ()
    {
      for (AnimationObject child : children)
      {
        child.disposeAnimation ();
      }
    }

    public long preferredDuration ()
    {
      long sum = 0;
      for (AnimationObject child : children)
      {
        sum += child.preferredDuration ();
      }
      return sum;
    }
  }
}
