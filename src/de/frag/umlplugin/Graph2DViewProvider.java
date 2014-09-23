package de.frag.umlplugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.graph.view.Graph2DView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A GraphViewProvider provides access to Graph2DView objects.
 */
public interface Graph2DViewProvider
{
  /**
   * Gets a Graph2DView from the given data context.
   * @param dataContext data context to be used to find view
   * @return Graph2DView
   */
  public @Nullable Graph2DView getGraph2DView (@NotNull DataContext dataContext);
}