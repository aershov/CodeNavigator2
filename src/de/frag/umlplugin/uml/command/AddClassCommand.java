package de.frag.umlplugin.uml.command;

import de.frag.umlplugin.uml.ClassRenamer;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Adds a class to a diagram.
 */
public class AddClassCommand extends AbstractDiagramCommand
{
  private String qualifiedClassName;

  public AddClassCommand (@NotNull String qualifiedClassName)
  {
    this.qualifiedClassName = qualifiedClassName;
  }

  public void execute (@NotNull UMLDiagram diagram)
  {
    if (diagram.getNode (qualifiedClassName) == null)
    {
      diagram.addClassNode (diagram.findPsiClass (qualifiedClassName));
    }
  }

  public boolean canExecute (@NotNull UMLDiagram diagram)
  {
    return diagram.getNode (qualifiedClassName) == null;
  }

  protected void addAdditionalInfo (@NotNull Element commandElement)
  {
    commandElement.setAttribute (PSI_CLASS, qualifiedClassName);
  }

  /**
   * Renames all contained class names.
   * @param renamer renamer that will rename classes to keep class names in sync after refactorings were applied.
   */
  public void renameClasses (@NotNull ClassRenamer renamer)
  {
    qualifiedClassName = renamer.rename (qualifiedClassName);
  }

  /**
   * Creates a string representation of this command.
   * @return created string representation
   */
  public @NotNull String toString ()
  {
    return "add class " + qualifiedClassName;
  }
}
