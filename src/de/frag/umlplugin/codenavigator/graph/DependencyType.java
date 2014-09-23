package de.frag.umlplugin.codenavigator.graph;

import org.jetbrains.annotations.NotNull;

/**
 * Enumeration of all possible class dependency types.
 */
public enum DependencyType
{
  SUBJECT, USED, USING, EXTENDED, EXTENDING;

  /**
   * Finds opposite of this dependency type.
   * @return opposite type for this type
   */
  public @NotNull DependencyType opposite ()
  {
    switch (this)
    {
      case SUBJECT:   return SUBJECT;
      case USING:     return USED;
      case USED:      return USING;
      case EXTENDING: return EXTENDED;
      case EXTENDED:  return EXTENDING;
      default: throw new IllegalArgumentException ("unknown dependency type: " + this);
    }
  }
}