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
import de.frag.umlplugin.uml.command.DiagramCommand;
import de.frag.umlplugin.uml.command.RemoveClassCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adds all used classes to diagram.
 */
public class RemoveClassAction extends AnAction
{
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null)
    {
      UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
      Node node = ProjectUtils.get (project, Node.class);
      if (node != null)
      {
        DiagramCommand command = createCommand (node);
        if (umlDiagram != null && command != null)
        {
          umlDiagram.addCommand (command);
          umlDiagram.getView ().updateView ();
        }
      }
    }
  }

  /**
   * Creates command that removes given class.
   * @param node node to remove
   * @return created command
   */
  protected @Nullable DiagramCommand createCommand (@NotNull Node node)
  {
    PsiClass psiClass = UMLDiagram.getPsiClass (node);
    if (psiClass != null)
    {
      String qualifiedClassName = psiClass.getQualifiedName ();
      if (qualifiedClassName != null)
      {
        return new RemoveClassCommand (qualifiedClassName);
      }
    }
    return null;
  }
}