package de.frag.umlplugin;

import org.jetbrains.annotations.NotNull;

/**
 * Consumes PsiClassProvider. All classes that need a PsiClassProvider should implement this interface.
 */
public interface PsiClassConsumer
{
  /**
   * Sets PsiClassProvider for this object.
   * @param psiClassProvider psi class provider to use for access to psi class objects
   */
  public void setPsiClassProvider (@NotNull PsiClassProvider psiClassProvider);
}
