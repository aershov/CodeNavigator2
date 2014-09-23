package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.psi.PsiClass;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

/**
 * Toggles field visibility for a all classes.
 */
public class ToggleFieldsForAllClassesAction extends AbstractToggleVisibilityAction
{
  /**
   * Changes visibility of compartments for given diagram and class.
   * @param diagram  diagram
   * @param psiClass class
   */
  protected void changeVisibility (@NotNull UMLDiagram diagram, @NotNull PsiClass psiClass)
  {
    for (String className : diagram)
    {
      diagram.getCompartmentVisibility ().toggleFieldsVisible (className);
    }
  }
}