package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import de.frag.umlplugin.uml.command.ConnectClassesCommand;
import de.frag.umlplugin.uml.command.DiagramCommand;

/**
 * Connects all classes in diagram using all possible dependencies.
 */
public class ConnectClassesAction extends AnAction
{
  /**
   * Connects all classes in diagram using all possible dependencies.
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
        DiagramCommand command = new ConnectClassesCommand ();
        umlDiagram.addCommand (command);
        umlDiagram.doLayout ();
      }
    }
  }
}