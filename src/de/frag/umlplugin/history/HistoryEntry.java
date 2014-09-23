package de.frag.umlplugin.history;

import com.intellij.psi.PsiClass;
import de.frag.umlplugin.codenavigator.graph.DependencyType;
import org.jetbrains.annotations.NotNull;

/**
 * Single navigation history entry.
 */
public class HistoryEntry
{
  private final PsiClass       subjectClass;
  private final PsiClass       selectedClass;
  private final DependencyType dependencyType;

  /**
   * Creates a new history entry.
   * @param subjectClass subject class
   * @param selectedClass selected class
   * @param dependencyType dependency type
   */
  public HistoryEntry (@NotNull PsiClass subjectClass, @NotNull PsiClass selectedClass,
                       @NotNull DependencyType dependencyType)
  {
    this.subjectClass   = subjectClass;
    this.selectedClass  = selectedClass;
    this.dependencyType = dependencyType;
  }

  public @NotNull PsiClass getSubjectClass ()
  {
    return subjectClass;
  }

  public @NotNull PsiClass getSelectedClass ()
  {
    return selectedClass;
  }

  public @NotNull DependencyType getDependencyType ()
  {
    return dependencyType;
  }
}
