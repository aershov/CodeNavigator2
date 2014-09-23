package de.frag.umlplugin.classcloud.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.classcloud.ClassCloud;

/**
 * Resets class cloud view, so there are no more ghosted classes.
 */
public class ResetViewAction extends AnAction
{
  /**
   * Implement this method to provide your action handler.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    ClassCloud classCloud = ProjectUtils.get (project, ClassCloud.class);
    if (classCloud != null)
    {
      classCloud.resetView ();
    }
  }
}
