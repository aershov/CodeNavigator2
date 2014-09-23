package de.frag.umlplugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.classcloud.ClassCloudData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Gets class that is currently selected in class cloud.
 */
public class ClassCloudPsiClassProvider implements PsiClassProvider
{
  /**
   * Gets class that is currently selected in class cloud.
   * @param dataContext data context
   * @return selected class or empty list, if no such class could be found
   */
  public @NotNull List<PsiClass> getPsiClass (@NotNull DataContext dataContext)
  {
    Project project = DataKeys.PROJECT.getData (dataContext);
    Node node = ProjectUtils.get (project, Node.class);
    if (node != null && project != null)
    {
      return Collections.singletonList (ClassCloudData.findPsiClass (project, node));
    }
    return Collections.emptyList ();
  }
}