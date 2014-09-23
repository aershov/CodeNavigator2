package de.frag.umlplugin.history;

import com.intellij.psi.PsiClass;
import de.frag.umlplugin.codenavigator.graph.DependencyType;
import org.jetbrains.annotations.NotNull;

/**
 * Navigation history.
 */
public class NavigationHistory
{
  private final HistoryList<HistoryEntry> historyList;

  /**
   * Creates a new navigation history with the given maximum history size.
   * @param maxSize maximum history size
   */
  public NavigationHistory (int maxSize)
  {
    historyList = new HistoryList<HistoryEntry> (maxSize);
  }

  /**
   * Clears this history.
   */
  public void clear ()
  {
    historyList.clear ();
  }

  /**
   * Navigates to the given class and dependency type
   * @param subjectClass class to navigate from
   * @param selectedClass class to navigate to
   * @param dependencyType dependency type to navigate to
   */
  public void navigatedTo (@NotNull PsiClass subjectClass, @NotNull PsiClass selectedClass,
                           @NotNull DependencyType dependencyType)
  {
    historyList.add (new HistoryEntry (subjectClass, selectedClass, dependencyType));
  }

  /**
   * Steps back in the history and returns the corresponing history entry.
   * @return previous history entry
   */
  public @NotNull HistoryEntry stepBack ()
  {
    return historyList.stepBack ();
  }

  /**
   * Steps forward in the history and returns the corresponing history entry.
   * @return next history entry
   */
  public @NotNull HistoryEntry stepForward ()
  {
    return historyList.stepForward ();
  }

  /**
   * Checks whether a step back in the history is possible.
   * @return true, if a step back is possible; false, if the current position is already the first list position.
   */
  public boolean canStepBack ()
  {
    return historyList.canStepBack ();
  }

  /**
   * Checks whether a step forward in the history is possible.
   * @return true, if a step forward is possible; false, if the current position is already the last list position.
   */
  public boolean canStepForward ()
  {
    return historyList.canStepForward ();
  }
}
