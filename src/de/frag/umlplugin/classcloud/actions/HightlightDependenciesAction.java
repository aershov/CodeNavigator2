package de.frag.umlplugin.classcloud.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.classcloud.ClassCloud;

/**
 * Shows selected class in class cloud in editor.
 */
public class HightlightDependenciesAction extends AnAction
{
  /**
   * Shows selected class in class cloud in editor.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    ClassCloud classCloud = ProjectUtils.get (project, ClassCloud.class);
    Node node = ProjectUtils.get (project, Node.class);
    if (node != null && classCloud != null)
    {
      classCloud.highlightDependencies (node);
    }
  }
}