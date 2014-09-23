package de.frag.umlplugin.uml.command;

import com.intellij.openapi.graph.base.Edge;
import de.frag.umlplugin.psi.UsageType;
import de.frag.umlplugin.uml.ClassRenamer;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Removes an edge from a diagram.
 */
public class RemoveEdgeCommand extends AbstractDiagramCommand
{
  private       String    sourceClassName;
  private       String    targetClassName;
  private final UsageType usageType;

  public RemoveEdgeCommand (@NotNull String sourceClassName, @NotNull String targetClassName,
                            @NotNull UsageType usageType)
  {
    this.sourceClassName = sourceClassName;
    this.targetClassName = targetClassName;
    this.usageType       = usageType;
  }

  public void execute (@NotNull UMLDiagram diagram)
  {
    Edge edge = diagram.getEdge (sourceClassName, targetClassName, usageType);
    if (edge != null)
    {
      diagram.removeEdge (edge);
    }
  }

  public boolean canExecute (@NotNull UMLDiagram diagram)
  {
    return diagram.getEdge (sourceClassName, targetClassName, usageType) != null;
  }

  protected void addAdditionalInfo (@NotNull Element commandElement)
  {
    commandElement.setAttribute (SOURCE_CLASS, sourceClassName);
    commandElement.setAttribute (TARGET_CLASS, targetClassName);
    commandElement.setAttribute (USAGE_TYPE,   usageType.name ());
  }

  /**
   * Renames all contained class names.
   * @param renamer renamer that will rename classes to keep class names in sync after refactorings were applied.
   */
  public void renameClasses (@NotNull ClassRenamer renamer)
  {
    sourceClassName = renamer.rename (sourceClassName);
    targetClassName = renamer.rename (targetClassName);
  }

  /**
   * Creates a string representation of this command.
   * @return created string representation
   */
  public @NotNull String toString ()
  {
    return "remove edge from " + sourceClassName + " to " + targetClassName;
  }
}
