package de.frag.umlplugin.uml.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.PsiClassConsumer;
import de.frag.umlplugin.PsiClassProvider;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Action group that contains dynamically computed collection of child menu items.
 */
public class AddToDiagramPopupMenuGroup extends ActionGroup implements PsiClassConsumer
{
  private PsiClassProvider psiClassProvider;

  /**
   * Injects psi class provider. This method should be called to inject a psi class provider at plugin startup.
   * @param psiClassProvider new psi class provider
   */
  public void setPsiClassProvider (@NotNull PsiClassProvider psiClassProvider)
  {
    this.psiClassProvider = psiClassProvider;
  }

  /**
   * Gets popup menu children.
   * @param e action event
   * @return array of child menus
   */
  public @NotNull AnAction [] getChildren (@Nullable AnActionEvent e)
  {
    List<AnAction> actions = new ArrayList<AnAction> ();
    AddToNewDiagramAction addToNewDiagramAction = new AddToNewDiagramAction ();
    addToNewDiagramAction.setPsiClassProvider (psiClassProvider);
    addToNewDiagramAction.getTemplatePresentation ().setText ("New Diagram...");
    actions.add (addToNewDiagramAction);
    if (e != null)
    {
      Project project = DataKeys.PROJECT.getData (e.getDataContext ());
      if (project != null)
      {
        UMLDiagramsPanel umlDiagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
        if (umlDiagrams != null)
        {
          for (UMLDiagram umlDiagram : umlDiagrams)
          {
            String diagramName = umlDiagram.getName ();
            AddToExistingDiagramAction action = new AddToExistingDiagramAction ();
            action.setPsiClassProvider (psiClassProvider);
            action.getTemplatePresentation ().setText (diagramName);
            actions.add (action);
          }
        }
      }
    }
    return actions.toArray (new AnAction [actions.size ()]);
  }
}
