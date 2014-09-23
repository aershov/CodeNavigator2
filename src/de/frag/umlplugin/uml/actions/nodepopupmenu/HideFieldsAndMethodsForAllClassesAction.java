package de.frag.umlplugin.uml.actions.nodepopupmenu;

import com.intellij.psi.PsiClass;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

/**
 * Disables field and method visibility for a all classes.
 */
public class HideFieldsAndMethodsForAllClassesAction extends AbstractToggleVisibilityAction
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
      diagram.getCompartmentVisibility ().setFieldsVisible  (className, false);
      diagram.getCompartmentVisibility ().setMethodsVisible (className, false);
    }
  }
}