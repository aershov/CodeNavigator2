package de.frag.umlplugin.uml.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Adds selected class to new UML diagram.
 */
public class AddToNewDiagramAction extends AbstractAddToDiagramAction
{
  /**
   * Gets diagram name for given event.
   * @param e event
   * @param psiClasses psi classes that will be added to diagram
   * @return diagram name or null, if diagram name is unknown
   */
  protected @Nullable String getDiagramName (@NotNull AnActionEvent e, @NotNull List<PsiClass> psiClasses)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    final UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams == null)
    {
      return null;
    }
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
    PsiDirectory psiDirectory = psiClasses.get (0).getContainingFile ().getContainingDirectory ();
    String name = psiDirectory != null ? psiDirectory.getName () : "name";
    return Messages.showInputDialog (project, "Enter new diagram name:", "New Diagram",
                                                   Messages.getQuestionIcon (), name, inputValidator);
  }
}
