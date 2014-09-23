package de.frag.umlplugin.uml.actions.edgepopupmenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import de.frag.umlplugin.uml.command.DiagramCommand;
import de.frag.umlplugin.uml.command.RemoveAllCreateEdgesCommand;

/**
 * Removes all "create" edgess from diagram.
 */
public class RemoveAllCreateEdgesAction extends AnAction
{
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null)
    {
      UMLDiagram diagram = diagrams.getCurrentDiagram ();
      if (diagram != null)
      {
        DiagramCommand command = new RemoveAllCreateEdgesCommand ();
        diagram.addCommand (command);
        diagram.doLayout ();
      }
    }
  }
}