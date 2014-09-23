package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import de.frag.umlplugin.uml.command.DiagramCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base action for adding dependent classes to diagram.
 */
public abstract class AbstractClassNodeAction extends AnAction
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
          DiagramCommand command = createCommand (node);
          if (command != null)
          {
            umlDiagram.addCommand (command);
            umlDiagram.doLayout ();
            umlDiagram.getView ().updateView ();
          }
        }
      }
    }
  }

  /**
   * Creates command that adds dependent classes.
   * @param node node to add dependent classes for
   * @return created command
   */
  protected abstract @Nullable DiagramCommand createCommand (@NotNull Node node);
}
