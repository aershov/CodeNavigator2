package de.frag.umlplugin.anim;

import junit.framework.TestCase;

/**
 * Tests composite animation object.
 */
public class TestCompositeAnimationObject extends TestCase
{
  public void testConcurrency ()
  {
    MockAnimationObject animationObject1 = new MockAnimationObject ( 500);
    MockAnimationObject animationObject2 = new MockAnimationObject (1000);
    MockAnimationObject animationObject3 = new MockAnimationObject ( 500);
    CompositeAnimationObject concurrency = CompositeAnimationFactory.createConcurrency ();
    concurrency.addAnimation (animationObject1);
    concurrency.addAnimation (animationObject2);
    concurrency.addAnimation (animationObject3);

    assertEquals (1000, concurrency.preferredDuration ());
    assertFalse  (animationObject1.isInitialized ());
    assertFalse  (animationObject1.isDisposed ());
    assertFalse  (animationObject2.isInitialized ());
    assertFalse  (animationObject2.isDisposed ());
    assertFalse  (animationObject3.isInitialized ());
    assertFalse  (animationObject3.isDisposed ());

    concurrency.initAnimation ();
    assertTrue   (animationObject1.isInitialized ());
    assertFalse  (animationObject1.isDisposed ());
    assertTrue   (animationObject2.isInitialized ());
    assertFalse  (animationObject2.isDisposed ());
    assertTrue   (animationObject3.isInitialized ());
    assertFalse  (animationObject3.isDisposed ());

    for (double time = 0.0; time <= 1.0; time += 0.1)
    {
      concurrency.calcFrame (time);
      assertEquals (Math.min (1.0, 2 * time), animationObject1.getTime (), 0.001);
      assertEquals ((long) (animationObject1.getTime () * 500), animationObject1.getMillis ());
      assertEquals (Math.min (1.0, time), animationObject2.getTime (), 0.001);
      assertEquals ((long) (animationObject2.getTime () * 1000), animationObject2.getMillis ());
      assertEquals (Math.min (1.0, 2 * time), animationObject3.getTime (), 0.001);
      assertEquals ((long) (animationObject3.getTime () * 500), animationObject3.getMillis ());
    }
    concurrency.disposeAnimation ();
    assertFalse (animationObject1.isInitialized ());
    assertTrue  (animationObject1.isDisposed ());
    assertFalse (animationObject2.isInitialized ());
    assertTrue  (animationObject2.isDisposed ());
    assertFalse (animationObject3.isInitialized ());
    assertTrue  (animationObject3.isDisposed ());
  }

  public void testScalingConcurrency ()
  {
    MockAnimationObject animationObject1 = new MockAnimationObject ( 500);
    MockAnimationObject animationObject2 = new MockAnimationObject (1000);
    MockAnimationObject animationObject3 = new MockAnimationObject ( 500);
    CompositeAnimationObject concurrency = CompositeAnimationFactory.createScalingConcurrency ();
    concurrency.addAnimation (animationObject1);
    concurrency.addAnimation (animationObject2);
    concurrency.addAnimation (animationObject3);

    assertEquals (1000, concurrency.preferredDuration ());
    assertFalse  (animationObject1.isInitialized ());
    assertFalse  (animationObject1.isDisposed ());
    assertFalse  (animationObject2.isInitialized ());
    assertFalse  (animationObject2.isDisposed ());
    assertFalse  (animationObject3.isInitialized ());
    assertFalse  (animationObject3.isDisposed ());

    concurrency.initAnimation ();
    assertTrue   (animationObject1.isInitialized ());
    assertFalse  (animationObject1.isDisposed ());
    assertTrue   (animationObject2.isInitialized ());
    assertFalse  (animationObject2.isDisposed ());
    assertTrue   (animationObject3.isInitialized ());
    assertFalse  (animationObject3.isDisposed ());

    for (double time = 0.0; time <= 1.0; time += 0.1)
    {
      concurrency.calcFrame (time);
      assertEquals (Math.min (1.0, time), animationObject1.getTime (), 0.001);
      assertEquals ((long) (animationObject1.getTime () * 500), animationObject1.getMillis ());
      assertEquals (Math.min (1.0, time), animationObject2.getTime (), 0.001);
      assertEquals ((long) (animationObject2.getTime () * 1000), animationObject2.getMillis ());
      assertEquals (Math.min (1.0, time), animationObject3.getTime (), 0.001);
      assertEquals ((long) (animationObject3.getTime () * 500), animationObject3.getMillis ());
    }
    concurrency.disposeAnimation ();
    assertFalse (animationObject1.isInitialized ());
    assertTrue  (animationObject1.isDisposed ());
    assertFalse (animationObject2.isInitialized ());
    assertTrue  (animationObject2.isDisposed ());
    assertFalse (animationObject3.isInitialized ());
    assertTrue  (animationObject3.isDisposed ());
  }

  public void testSequence ()
  {
    MockAnimationObject animationObject1 = new MockAnimationObject ( 500);
    MockAnimationObject animationObject2 = new MockAnimationObject (1000);
    MockAnimationObject animationObject3 = new MockAnimationObject ( 500);
    CompositeAnimationObject sequence = CompositeAnimationFactory.createSequence ();
    sequence.addAnimation (animationObject1);
    sequence.addAnimation (animationObject2);
    sequence.addAnimation (animationObject3);

    assertEquals (2000, sequence.preferredDuration ());
    assertFalse  (animationObject1.isInitialized ());
    assertFalse  (animationObject1.isDisposed ());
    assertFalse  (animationObject2.isInitialized ());
    assertFalse  (animationObject2.isDisposed ());
    assertFalse  (animationObject3.isInitialized ());
    assertFalse  (animationObject3.isDisposed ());

    sequence.initAnimation ();
    assertTrue   (animationObject1.isInitialized ());
    assertFalse  (animationObject1.isDisposed ());
    assertTrue   (animationObject2.isInitialized ());
    assertFalse  (animationObject2.isDisposed ());
    assertTrue   (animationObject3.isInitialized ());
    assertFalse  (animationObject3.isDisposed ());

    for (double time = 0.0; time <= 1.0; time += 0.1)
    {
      sequence.calcFrame (time);
      if (time <= 0.25)
      {
        assertEquals (4 * time, animationObject1.getTime (), 0.002);
        assertEquals ((long) (animationObject1.getTime () * 500), animationObject1.getMillis ());
        assertEquals (0, animationObject2.getTime (), 0.001);
        assertEquals (0, animationObject3.getTime (), 0.001);
      }
      else if (time <= 0.75)
      {
        assertEquals (2 * (time - 0.25), animationObject2.getTime (), 0.002);
        assertEquals ((long) (animationObject2.getTime () * 1000), animationObject2.getMillis ());
        assertEquals (0, animationObject3.getTime (), 0.001);
      }
      else
      {
        assertEquals (4 * (time - 0.75), animationObject3.getTime (), 0.002);
        assertEquals ((long) (animationObject3.getTime () * 500), animationObject3.getMillis ());
      }
    }
    sequence.disposeAnimation ();
    assertFalse (animationObject1.isInitialized ());
    assertTrue  (animationObject1.isDisposed ());
    assertFalse (animationObject2.isInitialized ());
    assertTrue  (animationObject2.isDisposed ());
    assertFalse (animationObject3.isInitialized ());
    assertTrue  (animationObject3.isDisposed ());
  }
}