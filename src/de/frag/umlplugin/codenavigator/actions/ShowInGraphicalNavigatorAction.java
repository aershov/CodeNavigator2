package de.frag.umlplugin.codenavigator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.PsiClassConsumer;
import de.frag.umlplugin.PsiClassProvider;
import de.frag.umlplugin.codenavigator.Navigator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 */
public class ShowInGraphicalNavigatorAction extends AnAction implements PsiClassConsumer
{
  private PsiClassProvider psiClassProvider;

  /**
   * Sets psi class provider. This method should be called to inject a psi class provider at plugin startup.
   * @param psiClassProvider new psi class provider
   */
  public void setPsiClassProvider (@NotNull PsiClassProvider psiClassProvider)
  {
    this.psiClassProvider = psiClassProvider;
  }

  /**
   * Updates the state of the action.
   * @param e Carries information on the invocation place and data available
   */
  public void update (AnActionEvent e)
  {
    Presentation presentation = e.getPresentation ();
    if (psiClassProvider == null)
    {
      presentation.setEnabled (false);
    }
    List<PsiClass> psiClasses = psiClassProvider.getPsiClass (e.getDataContext ());
    presentation.setEnabled (!psiClasses.isEmpty ());
  }

  /**
   * Shows current element under cursor or mouse in graphical navigator.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    Navigator navigator = ProjectUtils.get (project, Navigator.class);
    List<PsiClass> psiClasses = psiClassProvider.getPsiClass (e.getDataContext ());
    if (!psiClasses.isEmpty () && navigator != null)
    {
      PsiClass psiClass = psiClasses.get (0);
      navigator.startNavigation (psiClass);
    }
  }
}
