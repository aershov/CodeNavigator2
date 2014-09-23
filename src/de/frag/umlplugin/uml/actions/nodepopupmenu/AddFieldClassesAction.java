package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.openapi.graph.base.Node;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.command.AddFieldClassesCommand;
import de.frag.umlplugin.uml.command.DiagramCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adds classes to diagram using or used via fields.
 */
public class AddFieldClassesAction extends AbstractClassNodeAction
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
        return new AddFieldClassesCommand (qualifiedClassName);
      }
    }
    return null;
  }
}