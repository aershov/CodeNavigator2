package de.frag.umlplugin.codenavigator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.codenavigator.Navigator;
import de.frag.umlplugin.history.HistoryEntry;
import de.frag.umlplugin.history.NavigationHistory;

/**
 * Action for stepping forward in navigation history.
 */
public class NavigationHistoryForwardAction extends AnAction
{
  /**
   * Invoked when an action occurs.
   * @param e action event
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project           project   = DataKeys.PROJECT.getData (e.getDataContext ());
    Navigator         navigator = ProjectUtils.get (project, Navigator.class);
    NavigationHistory history   = ProjectUtils.get (project, NavigationHistory.class);
    if (navigator != null && history != null)
    {
      HistoryEntry historyEntry = history.stepForward ();
      navigator.navigate (historyEntry.getSelectedClass (), historyEntry.getDependencyType ());
    }
  }

  /**
   * Update action state.
   * @param e event
   */
  public void update (AnActionEvent e)
  {
    Project           project = DataKeys.PROJECT.getData (e.getDataContext ());
    NavigationHistory history = ProjectUtils.get (project, NavigationHistory.class);
    if (history != null)
    {
      Presentation presentation = e.getPresentation ();
      presentation.setEnabled (history.canStepForward ());
    }
  }
}