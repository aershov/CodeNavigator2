package de.frag.umlplugin.uml.command;

import com.intellij.psi.PsiClass;
import de.frag.umlplugin.psi.DependencyAnalyzer;
import de.frag.umlplugin.settings.Settings;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

/**
 * Adds dependent classes to a diagram.
 */
public class AddDependentClassesCommand extends AbstractAddDependentClassesCommand
{
  public AddDependentClassesCommand (@NotNull String qualifiedClassName)
  {
    super (qualifiedClassName);
  }

  /**
   * Adds classes dependent to specified class to given diagram.
   * @param diagram diagram
   * @param psiClass subject class
   */
  protected void addDependentClasses (@NotNull UMLDiagram diagram, @NotNull PsiClass psiClass)
  {
    DependencyAnalyzer analyzer = new DependencyAnalyzer (psiClass, Settings.getSettings ());
    addClasses (diagram, analyzer.getUsingClasses (),     false);
    addClasses (diagram, analyzer.getUsedClasses (),      true);
    addClasses (diagram, analyzer.getExtendingClasses (), false);
    addClasses (diagram, analyzer.getExtendedClasses (),  true);
  }

  /**
   * Creates a string representation of this command.
   * @return created string representation
   */
  public @NotNull String toString ()
  {
    return "add classes with any dependency to " + qualifiedClassName;
  }
}
