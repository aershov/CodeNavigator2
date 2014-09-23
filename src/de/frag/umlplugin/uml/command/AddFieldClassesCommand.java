package de.frag.umlplugin.uml.command;

import com.intellij.psi.PsiClass;
import de.frag.umlplugin.psi.DependencyAnalyzer;
import de.frag.umlplugin.psi.DependencyCollection;
import de.frag.umlplugin.settings.Settings;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

/**
 * Adds classes to diagram using or used via fields.
 */
public class AddFieldClassesCommand extends AbstractAddDependentClassesCommand
{
  public AddFieldClassesCommand (@NotNull String qualifiedClassName)
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
    DependencyCollection usingFieldClasses = analyzer.getUsingClasses ().filter (DependencyCollection.FIELD_FILTER);
    DependencyCollection usedFieldClasses  = analyzer.getUsedClasses ().filter (DependencyCollection.FIELD_FILTER);
    addClasses (diagram, usingFieldClasses, false);
    addClasses (diagram, usedFieldClasses,  true);
  }

  /**
   * Creates a string representation of this command.
   * @return created string representation
   */
  public @NotNull String toString ()
  {
    return "add classes that are used by " + qualifiedClassName + " as field";
  }
}