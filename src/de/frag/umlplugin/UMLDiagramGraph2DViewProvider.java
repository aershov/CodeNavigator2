package de.frag.umlplugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Gets a Graph2DView from UML diagram.
 */
public class UMLDiagramGraph2DViewProvider implements Graph2DViewProvider
{
  /**
   * Gets a Graph2DView from UML diagram.
   * @param dataContext data context
   * @return Graph2DView
   */
  public @Nullable Graph2DView getGraph2DView (@NotNull DataContext dataContext)
  {
    Project project = DataKeys.PROJECT.getData (dataContext);
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null)
    {
      UMLDiagram currentDiagram = diagrams.getCurrentDiagram ();
      if (currentDiagram != null)
      {
        return currentDiagram.getView ();
      }
    }
    return null;
  }
}