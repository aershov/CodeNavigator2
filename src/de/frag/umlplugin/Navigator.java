package de.frag.umlplugin;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.NavigatableFileEditor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Provides navigation to source files.
 */
public class Navigator
{
  private Navigator () {}
  
  /**
   * Navigates to source of given class at specified offset.
   * @param project project to use for navigation
   * @param classToNavigateTo navigate to source of this class
   */
  public static void navigateToSource (@NotNull Project project, @NotNull PsiElement classToNavigateTo)
  {
    PsiFile containingFile = classToNavigateTo.getContainingFile ();
    VirtualFile virtualFile = containingFile.getVirtualFile ();
    if (virtualFile != null)
    {
      FileEditorManager manager = FileEditorManager.getInstance (project);
      FileEditor[] fileEditors = manager.openFile (virtualFile, true);
      if (fileEditors.length > 0)
      {
        FileEditor fileEditor = fileEditors [0];
        if (fileEditor instanceof NavigatableFileEditor)
        {
          NavigatableFileEditor navigatableFileEditor = (NavigatableFileEditor) fileEditor;
          Navigatable descriptor = new OpenFileDescriptor (project, virtualFile, classToNavigateTo.getTextOffset ());
          navigatableFileEditor.navigateTo (descriptor);
        }
      }
    }
  }
}
