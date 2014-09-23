package de.frag.umlplugin.uml.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;

/**
 * Closes current diagram.
 */
public class DiagramCloseAction extends AnAction
{
  /**
   * Updates the state of the action.
   * @param e Carries information on the invocation place and data available
   */
  public void update (AnActionEvent e)
  {
    Presentation presentation = e.getPresentation ();
    boolean selectable = false;
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null)
    {
      UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
      selectable = umlDiagram != null;
    }
    presentation.setEnabled (selectable);
  }

  /**
   * Closes the current diagrams and disposes all associated data.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null)
    {
      UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
      if (umlDiagram != null && project != null)
      {
        int answer = Messages.showYesNoDialog (project, "Contents will be lost! Really close diagram?",
                                               "Confirm close diagram", Messages.getWarningIcon ());
        if (answer == 0)
        {
          diagrams.closeDiagram (umlDiagram);
        }
      }
    }
  }
}