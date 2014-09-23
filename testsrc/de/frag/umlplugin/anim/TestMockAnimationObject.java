package de.frag.umlplugin.anim;

import junit.framework.TestCase;

/**
 * Tests mock animation object.
 */
public class TestMockAnimationObject extends TestCase
{
  public void testMock ()
  {
    MockAnimationObject animationObject = new MockAnimationObject (1000);
    assertEquals (1000, animationObject.preferredDuration ());
    assertFalse  (animationObject.isInitialized ());
    assertFalse  (animationObject.isDisposed ());
    animationObject.initAnimation ();
    assertTrue   (animationObject.isInitialized ());
    assertFalse  (animationObject.isDisposed ());
    for (double time = 0.0; time <= 1.0; time += 0.1)
    {
      animationObject.calcFrame (time);
      assertEquals (time, animationObject.getTime (), 0.001);
      assertEquals ((long) (time * 1000), animationObject.getMillis ());
    }
    animationObject.disposeAnimation ();
    assertFalse (animationObject.isInitialized ());
    assertTrue  (animationObject.isDisposed ());
  }
}
