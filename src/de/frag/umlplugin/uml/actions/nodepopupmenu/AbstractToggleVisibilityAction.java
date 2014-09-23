package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base action for changing visibility of compartments.
 */
public abstract class AbstractToggleVisibilityAction extends AnAction
{
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null)
    {
      UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
      if (umlDiagram != null)
      {
        Node node = ProjectUtils.get (project, Node.class);
        if (node != null)
        {
          PsiClass psiClass = UMLDiagram.getPsiClass (node);
          if (psiClass != null)
          {
            changeVisibility (umlDiagram, psiClass);
            umlDiagram.refreshDiagram ();
            umlDiagram.doLayout ();
            umlDiagram.getView ().updateView ();
          }
        }
      }
    }
  }

  /**
   * Changes visibility of compartments for given diagram and class.
   * @param diagram diagram
   * @param psiClass class
   */
  protected abstract void changeVisibility (@NotNull UMLDiagram diagram, @NotNull PsiClass psiClass);
}