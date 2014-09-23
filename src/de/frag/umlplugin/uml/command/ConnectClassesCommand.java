package de.frag.umlplugin.uml.command;

import com.intellij.openapi.graph.base.Node;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.psi.DependencyAnalyzer;
import de.frag.umlplugin.psi.DependencyCollection;
import de.frag.umlplugin.settings.Settings;
import de.frag.umlplugin.uml.ClassRenamer;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

/**
 * Adds connections between all class nodes in a diagram.
 */
public class ConnectClassesCommand extends AbstractConnectCommand
{
  public void execute (@NotNull UMLDiagram diagram)
  {
    for (Node node : diagram.getGraph ().getNodeArray ())
    {
      PsiClass psiClass = UMLDiagram.getPsiClass (node);
      if (psiClass != null)
      {
        DependencyAnalyzer analyzer = new DependencyAnalyzer (psiClass, Settings.getSettings ());
        DependencyCollection usedClasses     = analyzer.getUsedClasses ();
        DependencyCollection extendedClasses = analyzer.getExtendedClasses ();
        addEdges (diagram, node, usedClasses,     true);
        addEdges (diagram, node, extendedClasses, true);
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
  }

  /**
   * Creates a string representation of this command.
   * @return created string representation
   */
  public @NotNull String toString ()
  {
    return "add connections between all contained classes";
  }
}
