package de.frag.umlplugin.uml.command;

import com.intellij.openapi.graph.base.Edge;
import de.frag.umlplugin.psi.UsageType;
import de.frag.umlplugin.uml.ClassRenamer;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

/**
 * Removes all "usage" edges from a diagram.
 */
public class RemoveAllUsageEdgesCommand extends AbstractDiagramCommand
{
  public void execute (@NotNull UMLDiagram diagram)
  {
    for (Edge edge : diagram.getEdges ())
    {
      UsageType usageType = UMLDiagram.getUsageType (edge);
      if (usageType == UsageType.REFERENCE || usageType == UsageType.STATIC_REFERENCE)
      {
        diagram.removeEdge (edge);
      }
    }
  }

  public boolean canExecute (@NotNull UMLDiagram diagram)
  {
    return true;
  }

  /**
   * Renames all contained class names.
   * @param renamer renamer that will rename classes to keep class names in sync after refactorings were applied.
   */
  public void renameClasses (@NotNull ClassRenamer renamer)
  {
    // do nothing
  }

  /**
   * Creates a string representation of this command.
   * @return created string representation
   */
  public @NotNull String toString ()
  {
    return "remove all 'uses'-edges from diagram";
  }
}