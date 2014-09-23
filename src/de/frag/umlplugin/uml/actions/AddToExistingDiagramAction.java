package de.frag.umlplugin.uml.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Adds selected class to new UML diagram.
 */
public class AddToExistingDiagramAction extends AbstractAddToDiagramAction
{
  /**
   * Gets diagram name for given event.
   * @param e event
   * @param psiClasses psi classes that will be added to diagram
   * @return diagram name or null, if diagram name is unknown
   */
  protected @Nullable String getDiagramName (@NotNull AnActionEvent e, @NotNull List<PsiClass> psiClasses)
  {
    return e.getPresentation ().getText ();
  }
}