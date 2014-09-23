package de.frag.umlplugin.anim;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for composite animation objects.
 */
public abstract class AbstractCompositeAnimationObject implements CompositeAnimationObject
{
  protected final List<AnimationObject> children = new ArrayList<AnimationObject> ();

  /**
   * Adds given animation object to this composite object.
   * @param animationObject animation object to add
   */
  public void addAnimation (@NotNull AnimationObject animationObject)
  {
    children.add (animationObject);
  }

}
