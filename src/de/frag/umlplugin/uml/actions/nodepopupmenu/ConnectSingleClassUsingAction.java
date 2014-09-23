package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.openapi.graph.base.Node;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.command.ConnectSingleClassUsingCommand;
import de.frag.umlplugin.uml.command.DiagramCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Connects classes to current class that are using current class.
 */
public class ConnectSingleClassUsingAction extends AbstractClassNodeAction
{
  /**
   * Creates command that connects dependent classes.
   * @param node node to connect dependent classes to
   * @return created command
   */
  protected @Nullable DiagramCommand createCommand (@NotNull Node node)
  {
    PsiClass psiClass = UMLDiagram.getPsiClass (node);
    if (psiClass != null)
    {
      String qualifiedClassName = psiClass.getQualifiedName ();
      return new ConnectSingleClassUsingCommand (qualifiedClassName);
    }
    else
    {
      return null;
    }
  }
}