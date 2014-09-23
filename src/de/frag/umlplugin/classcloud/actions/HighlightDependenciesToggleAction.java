package de.frag.umlplugin.classcloud.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.classcloud.ClassCloud;

/**
 * Toggle between two states for clicking on a node: navigate to source and highlight dependent classes.
 */
public class HighlightDependenciesToggleAction extends ToggleAction
{
  /**
   * Returns the selected (checked, pressed) state of the action.
   * @param e the action event representing the place and context in which the selected state is queried.
   * @return true if the action is selected, false otherwise
   */
  public boolean isSelected (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    ClassCloud classCloud = ProjectUtils.get (project, ClassCloud.class);
    return classCloud == null || classCloud.isGoToSourceOnClick ();
  }

  /**
   * Sets the selected state of the action to the specified value.
   * @param e     the action event which caused the state change.
   * @param state the new selected state of the action.
   */
  public void setSelected (AnActionEvent e, boolean state)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    ClassCloud classCloud = ProjectUtils.get (project, ClassCloud.class);
    if (classCloud != null)
    {
      classCloud.setGoToSourceOnClick (state);
    }
  }
}
