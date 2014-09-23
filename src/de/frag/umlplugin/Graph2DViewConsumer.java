package de.frag.umlplugin;

import org.jetbrains.annotations.NotNull;

/**
 * Consumes Graph2DViewProvider. All classes that need a Graph2DViewProvider should implement this interface.
 */
public interface Graph2DViewConsumer
{
  /**
   * Sets Graph2DViewProvider for this object.
   * @param graph2DViewProvider Graph2DViewProvider to use for access to Graph2DView objects
   */
  public void setGraph2DViewProvider (@NotNull Graph2DViewProvider graph2DViewProvider);
}