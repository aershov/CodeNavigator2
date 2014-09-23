package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.openapi.graph.base.Node;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.command.AddDependentClassesCommand;
import de.frag.umlplugin.uml.command.DiagramCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adds all dependent classes to diagram.
 */
public class AddDependentClassesAction extends AbstractClassNodeAction
{
  /**
   * Creates command that adds dependent classes.
   * @param node node to add dependent classes for
   * @return created command
   */
  protected @Nullable DiagramCommand createCommand (@NotNull Node node)
  {
    PsiClass psiClass = UMLDiagram.getPsiClass (node);
    if (psiClass != null)
    {
      String qualifiedClassName = psiClass.getQualifiedName ();
      if (qualifiedClassName != null)
      {
        return new AddDependentClassesCommand (qualifiedClassName);
      }
    }
    return null;
  }
}
