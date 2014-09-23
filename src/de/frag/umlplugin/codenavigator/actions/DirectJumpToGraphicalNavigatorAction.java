package de.frag.umlplugin.codenavigator.actions;

import com.intellij.ide.util.gotoByName.ChooseByNameFactory;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.codenavigator.Navigator;

/**
 * Action for stepping back in navigation history.
 */
public class DirectJumpToGraphicalNavigatorAction extends AnAction
{
  /**
   * Invoked when an action occurs.
   * @param e action event
   */
  public void actionPerformed (AnActionEvent e)
  {
    final Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    if (project != null)
    {
      final Navigator navigator = ProjectUtils.get (project, Navigator.class);
      ChooseByNameFactory factory = ChooseByNameFactory.getInstance (project);
      ChooseByNameModel model = new GotoClassModel2 (project);
      ChooseByNamePopupComponent popupComponent = factory.createChooseByNamePopupComponent (model);
      popupComponent.invoke (new ChooseByNamePopupComponent.Callback ()
      {
        public void elementChosen (Object element)
        {
          if (element instanceof PsiClass)
          {
            PsiClass psiClass = (PsiClass) element;
            navigator.startNavigation (psiClass);
          }
        }
      }, ModalityState.NON_MODAL, false);
    }
  }
}