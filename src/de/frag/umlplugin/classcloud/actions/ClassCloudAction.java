package de.frag.umlplugin.classcloud.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.SearchScope;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.classcloud.ClassCloud;
import de.frag.umlplugin.psi.ClassFinder;
import de.frag.umlplugin.scopes.SearchScopeComboBox;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Creates a class cloud that shows all classes of the current project in a single diagram.
 */
public class ClassCloudAction extends AnAction
{
  public void actionPerformed (final AnActionEvent e)
  {
    final Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    if (project == null)
    {
      return;
    }
    final SearchScope searchScope = showScopeDialog (project);
    if (searchScope == null)
    {
      return;
    }
    final ProgressManager progressManager = ProgressManager.getInstance ();
    progressManager.runProcessWithProgressSynchronously (new Runnable () {
      public void run ()
      {
        // start progress that analyzes each class...
        final ClassCloud classCloud = getClassCloud (project);
        final ProgressIndicator progressIndicator = progressManager.getProgressIndicator ();
        List<PsiClass> classes = ClassFinder.findAllClassesForContext (project, searchScope);
        int index = 0;
        for (PsiClass psiClass : classes)
        {
          progressIndicator.setText ("Analyzing " + psiClass.getQualifiedName ());
          classCloud.analyzeClass (psiClass);
          progressIndicator.setFraction ((double) index / classes.size ());
          if (progressIndicator.isCanceled ())
          {
            break;
          }
          index++;
        }
        if (!progressIndicator.isCanceled ())
        {
          // show class cloud as soon as progress indicator is closed...
          SwingUtilities.invokeLater (new Runnable () {
            public void run ()
            {
              // all classes were analyzed, now analyze dependencies between classes and show graph...
              classCloud.show ();
            }
          });
        }
      }
    }, "Analyzing dependencies", true, project);
  }

  /**
   * Gets class cloud or creates a new cloud if there is none.
   * @param project project
   * @return existing or new class cloud
   */
  private @NotNull ClassCloud getClassCloud (@NotNull Project project)
  {
    ClassCloud classCloud = ProjectUtils.get (project, ClassCloud.class);
    if (classCloud == null)
    {
      classCloud = new ClassCloud (project);
      ProjectUtils.set (project, classCloud);
    }
    classCloud.clear ();
    return classCloud;
  }

  /**
   * Shows dialog for choosing search scope.
   * @param project current project
   * @return chosen search scope or null, if user aborted dialog.
   */
  private @Nullable SearchScope showScopeDialog (@NotNull Project project)
  {
    DialogBuilder builder = new DialogBuilder (project);
    builder.setTitle ("Choose search scope for class cloud analysis");
    Settings settings = Settings.getSettings ();

    SearchScopeComboBox searchScopeComboBox = new SearchScopeComboBox (project);
    searchScopeComboBox.setSelectedSearchScopeName (settings.getClassCloudScopeName ());
    JPanel panel = new JPanel (new BorderLayout ());
    panel.add (new JLabel ("Class cloud analysis search scope: "), BorderLayout.WEST);
    panel.add (searchScopeComboBox,                                BorderLayout.CENTER);

    builder.setCenterPanel (panel);
    builder.addOkAction ();
    builder.addCancelAction ();
    int result = builder.show ();
    if (result == 0)
    {
      String cloudScopeName = searchScopeComboBox.getSelectedSearchScopeName ();
      settings.setClassCloudScopeName (cloudScopeName);
      return searchScopeComboBox.getSelectedSearchScope ();
    }
    else
    {
      return null;
    }
  }
}
