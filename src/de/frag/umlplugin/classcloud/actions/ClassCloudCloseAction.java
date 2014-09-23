package de.frag.umlplugin.classcloud.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.classcloud.ClassCloud;

/**
 * Closes class cloud tool window and disposes all computed class cloud data.
 */
public class ClassCloudCloseAction extends AnAction
{
  /**
   * Closes class cloud
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    if (project != null)
    {
      ClassCloud classCloud = ProjectUtils.get (project, ClassCloud.class);
      if (classCloud != null)
      {
        classCloud.close ();
        ProjectUtils.remove (project, classCloud);
      }
    }
  }
}
