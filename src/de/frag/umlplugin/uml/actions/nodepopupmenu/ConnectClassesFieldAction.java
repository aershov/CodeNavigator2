package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import de.frag.umlplugin.uml.command.ConnectClassesFieldCommand;
import de.frag.umlplugin.uml.command.DiagramCommand;

/**
 * Connects all classes in diagram that use each other cia fields.
 */
public class ConnectClassesFieldAction extends AnAction
{
  /**
   * Connects all classes in diagram that extend each other.
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
        DiagramCommand command = new ConnectClassesFieldCommand ();
        umlDiagram.addCommand (command);
        umlDiagram.doLayout ();
      }
    }
  }
}