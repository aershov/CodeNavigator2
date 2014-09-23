package de.frag.umlplugin.anim;

import org.jetbrains.annotations.NotNull;

/**
 * A composite animation object is an animation object that contains several child animation objects.
 */
public interface CompositeAnimationObject extends AnimationObject
{
  /**
   * Adds given animation object to this composite object.
   * @param animationObject animation object to add
   */
  public void addAnimation (@NotNull AnimationObject animationObject);

}
