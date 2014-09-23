package de.frag.umlplugin.uml.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.DiagramCreationStepsEditor;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;

/**
 * Opens a dialog to edit diagram creation command list.
 */
public class EditCommandsAction extends AnAction
{
  /**
   * Open dialog for editing diagram creation command list.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (project != null && diagrams != null)
    {
      UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
      if (umlDiagram != null)
      {
        DiagramCreationStepsEditor editor = new DiagramCreationStepsEditor (project, umlDiagram);
        boolean ok = editor.show ();
        if (ok)
        {
          umlDiagram.setCommands (editor.getEditedCommandList ());
        }
        else
        {
          umlDiagram.setCommands (editor.getOriginalCommandList ());
        }
        if (editor.isChanged ())
        {
          umlDiagram.refreshDiagram ();
          umlDiagram.doLayout ();
        }
      }
    }
  }

  /**
   * Disables action, if no uml diagram is open.
   * @param e event
   */
  public void update (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    Presentation presentation = e.getPresentation ();
    if (diagrams != null)
    {
      UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
      if (umlDiagram != null)
      {
        presentation.setEnabled (umlDiagram.canStepBack ());
      }
    }
    else
    {
      presentation.setEnabled (false);
    }
  }
}
