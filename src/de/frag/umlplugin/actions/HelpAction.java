package de.frag.umlplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Action for showing help.
 */
public class HelpAction extends AnAction
{
  private static final int    WIDTH               = 1000;
  private static final int    HEIGHT              =  600;
  private static final String CODE_NAVIGATOR_HTML = "/help/CodeNavigator.html";

  /**
   * Show help.
   * @param e event
   */
  public void actionPerformed (AnActionEvent e)
  {
    try
    {
      Project project = DataKeys.PROJECT.getData (e.getDataContext ());
      DialogBuilder dialogBuilder = new DialogBuilder (project);
      dialogBuilder.setTitle ("Code Navigator Help");
      dialogBuilder.addCloseButton ();
      dialogBuilder.setCenterPanel (createCenterPanel ());
      dialogBuilder.show ();
    }
    catch (Exception ex)
    {
      ex.printStackTrace ();
    }
  }

  /**
   * Creates center panel for help dialog.
   * @return created help panel
   * @throws java.io.IOException on IO error
   */
  private @NotNull JComponent createCenterPanel () throws IOException
  {
    URL resource = getClass ().getResource (CODE_NAVIGATOR_HTML);
    JEditorPane editorPane = new JEditorPane (resource);
    editorPane.setEditable (false);
    JScrollPane scrollPane = new JScrollPane (editorPane);
    scrollPane.setPreferredSize (new Dimension (WIDTH, HEIGHT));
    return scrollPane;
  }
}