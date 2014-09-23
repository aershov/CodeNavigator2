package de.frag.umlplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.settings.SettingsPlugin;

/**
 * Action for access to settings.
 */
public class SettingsAction extends AnAction
{
  /**
   * Show settings dialog.
   * @param e event
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    Application application = ApplicationManager.getApplication ();
    SettingsPlugin settingsPlugin = application.getComponent (SettingsPlugin.class);
    ShowSettingsUtil showSettingsUtil = ShowSettingsUtil.getInstance ();
    showSettingsUtil.editConfigurable (project, settingsPlugin);
  }
}