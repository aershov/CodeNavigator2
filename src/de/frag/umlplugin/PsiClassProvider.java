package de.frag.umlplugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A PsiClassProvider gets a psi class from a data context.
 */
public interface PsiClassProvider
{
  /**
   * Gets a list of psi classes from the given data context. Implementations may for example return the class in
   * the current java editor, the class that belongs to the type of the java variable under the cursor,
   * the class that is currently selected in the project view or anything else.
   * @param dataContext data context to be used to find class
   * @return list of psi classes
   */
  public @NotNull List<PsiClass> getPsiClass (@NotNull DataContext dataContext);
}
