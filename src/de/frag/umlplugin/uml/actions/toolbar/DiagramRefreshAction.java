package de.frag.umlplugin.uml.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;

/**
 * Steps back in diagram creation.
 */
public class DiagramRefreshAction extends AnAction
{
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
   * Shows current element under cursor or mouse in graphical navigator.
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
        umlDiagram.refreshDiagram ();
        umlDiagram.doLayout ();
      }
    }
  }
}