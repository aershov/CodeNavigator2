package de.frag.umlplugin.uml.command;

import de.frag.umlplugin.uml.ClassRenamer;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract command that executes some action on a diagram.
 */
public interface DiagramCommand
{
  /**
   * Executes some action on a diagram.
   * @param diagram diagram
   */
  public void execute (@NotNull UMLDiagram diagram);

  /**
   * Checks whether this command can be executed ot not.
   * @param diagram diagram
   * @return true, if command can be executed; false otherwise
   */
  public boolean canExecute (@NotNull UMLDiagram diagram);

  /**
   * Creates JDOM element that contains all data needed to restore this command.
   * @return created JDOM element
   */
  public @NotNull Element createElement ();

  /**
   * Renames all contained class names.
   * @param renamer renamer that will rename classes to keep class names in sync after refactorings were applied.
   */
  public void renameClasses (@NotNull ClassRenamer renamer);
}
