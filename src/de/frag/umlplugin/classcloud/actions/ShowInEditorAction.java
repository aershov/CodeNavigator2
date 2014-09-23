package de.frag.umlplugin.classcloud.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.ClassCloudPsiClassProvider;
import de.frag.umlplugin.Navigator;
import de.frag.umlplugin.PsiClassProvider;

import java.util.List;

/**
 * Shows selected class in class cloud in editor.
 */
public class ShowInEditorAction extends AnAction
{
  /**
   * Shows selected class in class cloud in editor.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    PsiClassProvider provider = new ClassCloudPsiClassProvider ();
    List<PsiClass> psiClasses = provider.getPsiClass (e.getDataContext ());
    if (!psiClasses.isEmpty () && project != null)
    {
      PsiClass psiClass = psiClasses.get (0);
      Navigator.navigateToSource (project, psiClass);
    }
  }
}
