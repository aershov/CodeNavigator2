package de.frag.umlplugin.uml;

import com.intellij.openapi.command.undo.DocumentReference;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.command.undo.UndoableAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import de.frag.umlplugin.ProjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides access to refactoring listeners to synchronize UML diagram creation steps
 * with rename- and move-refactorings.
 */
public class UMLRefactoringListenerProvider implements RefactoringElementListenerProvider
{
  private final Project project;

  /**
   * Creates new listener provider for given project.
   * @param project current project
   */
  public UMLRefactoringListenerProvider (@NotNull Project project)
  {
    this.project = project;
  }

  /**
   * Creates a new listener for renaming/moving the given element.
   * @param element element that will be renamed or moved
   * @return created listener
   */
  public @Nullable RefactoringElementListener getListener (@NotNull PsiElement element)
  {
    String name = elementToString (element);
    return name == null ? null : new RefactoringListener (project, name);
  }

  /**
   * Converts given element to a string representation.
   * @param element element to convert
   * @return element converted to fully qualified name of element
   */
  private static @Nullable String elementToString (@NotNull PsiElement element)
  {
    if (element instanceof PsiClass)
    {
      return ((PsiClass) element).getQualifiedName ();
    }
    else if (element instanceof PsiPackage)
    {
      return ((PsiPackage) element).getQualifiedName ();
    }
    else
    {
      return null;
    }
  }

  /**
   * Refactring listener for keeping UML diagrams and code in sync.
   */
  private static class RefactoringListener implements RefactoringElementListener
  {
    private final Project project;
    private final String  oldElementName;

    /**
     * Creates a new listener.
     * @param project current project
     * @param oldElementName old (or current) name of element to be renamed or moved
     */
    private RefactoringListener (@NotNull Project project, @NotNull String oldElementName)
    {
      this.project        = project;
      this.oldElementName = oldElementName;
    }

    /**
     * Element associated with this listener was moved.
     * @param newElement new element after moving
     */
    public void elementMoved (@NotNull PsiElement newElement)
    {
      String newElementName = elementToString (newElement);
      RenameAction renameAction = new RenameAction (project, oldElementName, newElementName);
      renameAction.redo ();
      UndoManager undoManager = UndoManager.getInstance (project);
      undoManager.undoableActionPerformed (renameAction);
    }

    /**
     * Element associated with this listener was renamed.
     * @param newElement new element after renaming
     */
    public void elementRenamed (@NotNull PsiElement newElement)
    {
      elementMoved (newElement);
    }
  }

  /**
   * Undoable action that renames all affected fully qualified class names in diagram creation commands.
   */
  private static class RenameAction implements UndoableAction
  {
    private final Project project;
    private final String  oldElementName;
    private final String  newElementName;

    /**
     * Creates a new rename action.
     * @param project current project
     * @param oldElementName old name of renamed element
     * @param newElementName new name of renamed element
     */
    private RenameAction (@NotNull Project project, @NotNull String oldElementName, @Nullable String newElementName)
    {
      this.project        = project;
      this.oldElementName = oldElementName;
      this.newElementName = newElementName;
    }

    /**
     * Undo rename element.
     */
    public void undo ()
    {
      rename (newElementName, oldElementName);
    }

    /**
     * Redo rename element.
     */
    public void redo ()
    {
      rename (oldElementName, newElementName);
    }

    /**
     * Execute renaming element from given old name to specified new name.
     * @param oldName old name of element
     * @param newName new name of element
     */
    private void rename (@NotNull String oldName, @NotNull String newName)
    {
      ClassRenamer renamer = new CommandRenamer (oldName, newName);
      UMLDiagramsPanel diagramsPanel = ProjectUtils.get (project, UMLDiagramsPanel.class);
      if (diagramsPanel != null)
      {
        for (UMLDiagram diagram : diagramsPanel)
        {
          diagram.renameClasses (renamer);
        }
      }
    }

    /**
     * Always returns an empty array since this is not used by this action.
     * @return empty array
     */
    public @NotNull DocumentReference [] getAffectedDocuments ()
    {
      return new DocumentReference [0];
    }

    /**
     * Always returns false since this is not used by this action.
     * @return false
     */
    public boolean isComplex ()
    {
      return false;
    }
  }

  /**
   * Renamer implementation.
   */
  private static class CommandRenamer implements ClassRenamer
  {
    private final String oldName;
    private final String newName;

    /**
     * Creates new renamer.
     * @param oldName old class name
     * @param newName new class name
     */
    private CommandRenamer (@NotNull String oldName, @NotNull String newName)
    {
      this.oldName = oldName;
      this.newName = newName;
    }

    /**
     * Renames given class and returns new name.
     * @param classToRename fully qualified name of class to rename
     * @return new name for given class
     */
    public @NotNull String rename (@NotNull String classToRename)
    {
      if (classToRename.startsWith (oldName) && (classToRename.length () == oldName.length () ||
                                                 classToRename.charAt (oldName.length ()) == '.'))
      {
        return newName + classToRename.substring (oldName.length ());
      }
      return classToRename;
    }
  }
}
