package de.frag.umlplugin.uml.actions.edgepopupmenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.psi.UsageType;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import de.frag.umlplugin.uml.command.DiagramCommand;
import de.frag.umlplugin.uml.command.RemoveEdgeCommand;

/**
 * Removes edges from diagram.
 */
public class RemoveEdgeAction extends AnAction
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
        Edge edge = ProjectUtils.get (project, Edge.class);
        if (edge != null)
        {
          Node sourceNode = edge.source ();
          Node targetNode = edge.target ();
          PsiClass  sourceClass = UMLDiagram.getPsiClass (sourceNode);
          PsiClass  targetClass = UMLDiagram.getPsiClass (targetNode);
          UsageType usageType   = UMLDiagram.getUsageType (edge);
          if (sourceClass != null && targetClass != null && usageType != null)
          {
            String sourceClassName = sourceClass.getQualifiedName ();
            String targetClassName = targetClass.getQualifiedName ();
            if (sourceClassName != null && targetClassName != null)
            {
              DiagramCommand command = new RemoveEdgeCommand (sourceClassName, targetClassName, usageType);
              umlDiagram.addCommand (command);
              umlDiagram.doLayout ();
            }
          }
        }
      }
    }
  }
}