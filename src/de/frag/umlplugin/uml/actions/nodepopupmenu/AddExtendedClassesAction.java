package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.openapi.graph.base.Node;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.command.AddExtendedClassesCommand;
import de.frag.umlplugin.uml.command.DiagramCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adds all extended classes to diagram.
 */
public class AddExtendedClassesAction extends AbstractClassNodeAction
{
  /**
   * Creates command that adds extended classes.
   * @param node node to add extended classes for
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
        return new AddExtendedClassesCommand (qualifiedClassName);
      }
    }
    return null;
  }
}