package de.frag.umlplugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.codenavigator.NavigatorProjectComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Gets a Graph2DView from graphical navigation panel.
 */
public class GraphicalNavigatorGraph2DViewProvider implements Graph2DViewProvider
{
  /**
   * Gets a Graph2DView from graphical navigation panel.
   * @param dataContext data context
   * @return Graph2DView
   */
  public @Nullable Graph2DView getGraph2DView (@NotNull DataContext dataContext)
  {
    Project project = DataKeys.PROJECT.getData (dataContext);
    NavigatorProjectComponent projectComponent = ProjectUtils.get (project, NavigatorProjectComponent.class);
    if (projectComponent != null)
    {
      return projectComponent.getGraph2DView ();
    }
    return null;
  }
}
