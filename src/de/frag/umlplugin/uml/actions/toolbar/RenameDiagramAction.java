package de.frag.umlplugin.uml.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import org.jetbrains.annotations.Nullable;

/**
 * Renames diagram.
 */
public class RenameDiagramAction extends AnAction
{
  /**
   * Renames current diagram.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null)
    {
      UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
      if (umlDiagram != null)
      {
        String oldName = umlDiagram.getName ();
        String newName = requestNewName (project, oldName, diagrams);
        if (newName != null)
        {
          diagrams.renameDiagram (umlDiagram, newName);
        }
      }
    }
  }

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
   * Requests new diagram name.
   * @param project current project
   * @param oldName old diagram name
   * @param diagrams diagrams, needed to check whether new name already exists
   * @return user input
   */
  private @Nullable String requestNewName (Project project, String oldName, final UMLDiagramsPanel diagrams)
  {
    InputValidator inputValidator = new InputValidator()
    {
      public boolean checkInput (String inputString)
      {
        return inputString != null && !"".equals (inputString.trim ()) && !diagrams.isExistingDiagram (inputString);
      }
      public boolean canClose (String inputString)
      {
        return checkInput (inputString);
      }
    };
    return Messages.showInputDialog (project, "Enter new diagram name:", "Rename Diagram",
                                                   Messages.getQuestionIcon (), oldName, inputValidator);

  }
}