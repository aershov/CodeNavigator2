package de.frag.umlplugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.classcloud.ClassCloud;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**         
 * Gets a Graph2DView from class cloud.
 */
public class ClassCloudGraph2DViewProvider implements Graph2DViewProvider
{                                      
  /**
   * Gets a Graph2DView from class cloud.
   * @param dataContext data context
   * @return Graph2DView
   */
  public @Nullable Graph2DView getGraph2DView (@NotNull DataContext dataContext)
  {                                       
    Project project = DataKeys.PROJECT.getData (dataContext);
    ClassCloud classCloud = ProjectUtils.get (project, ClassCloud.class);
    if (classCloud != null)
    {
      return classCloud.getGraph2DView ();
    }
    return null;
  }
}