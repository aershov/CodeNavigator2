package de.frag.umlplugin.uml.command;

import com.intellij.openapi.graph.base.Node;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.psi.DependencyAnalyzer;
import de.frag.umlplugin.psi.DependencyCollection;
import de.frag.umlplugin.settings.Settings;
import de.frag.umlplugin.uml.ClassRenamer;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Adds connections between given class node and other class nodes in a diagram that are extending given class.
 */
public class ConnectSingleClassExtendingCommand extends AbstractConnectCommand
{
  private String qualifiedClassName;

  public ConnectSingleClassExtendingCommand (String qualifiedClassName)
  {
    this.qualifiedClassName = qualifiedClassName;
  }

  public void execute (@NotNull UMLDiagram diagram)
  {
    PsiClass psiClass = diagram.findPsiClass (qualifiedClassName);
    if (psiClass != null)
    {
      DependencyAnalyzer analyzer = new DependencyAnalyzer (psiClass, Settings.getSettings ());
      DependencyCollection extendingClasses = analyzer.getExtendingClasses ();
      Node subjectNode = diagram.getNode (qualifiedClassName);
      if (subjectNode != null)
      {
        addEdges (diagram, subjectNode, extendingClasses, false);
      }
    }
  }

  public boolean canExecute (@NotNull UMLDiagram diagram)
  {
    return diagram.getNode (qualifiedClassName) != null;
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
    return "connect contained classes that are extending " + qualifiedClassName;
  }
}