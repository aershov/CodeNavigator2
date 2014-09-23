package de.frag.umlplugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Provides access to class under cursor in java editor or class that was source of context sensitive menu.
 */
public class ClassUnderCursorProvider implements PsiClassProvider
{
  /**
   * Gets psi class under cursor.
   * @param dataContext data context
   * @return found class or null, if no class was found
   */
  public @NotNull List<PsiClass> getPsiClass (@NotNull DataContext dataContext)
  {
    // try to find seleted class in project view
    PsiElement [] psiElements = DataKeys.PSI_ELEMENT_ARRAY.getData (dataContext);
    if (psiElements != null)
    {
      List<PsiClass> psiClasses = new ArrayList<PsiClass> ();
      for (PsiElement psiElement : psiElements)
      {
        if (psiElement instanceof PsiClass)
        {
          psiClasses.add ((PsiClass) psiElement);
        }
        else if (psiElement instanceof PsiDirectory)
        {
          PsiDirectory psiDirectory = (PsiDirectory) psiElement;
          PsiPackage psiPackage = psiDirectory.getPackage ();
          if (psiPackage != null)
          {
            PsiClass [] classes = psiPackage.getClasses ();
            psiClasses.addAll (Arrays.asList (classes));
          }
        }
      }
      return psiClasses;
    }
    // not found so far => try to find selected class under cursor
    PsiElement psiElement = DataKeys.PSI_ELEMENT.getData (dataContext);
    if (psiElement != null)
    {
      if (psiElement instanceof PsiClass)
      {
        return Collections.singletonList ((PsiClass) psiElement);
      }
      else if (psiElement instanceof PsiPackage)
      {
        PsiPackage psiPackage = (PsiPackage) psiElement;
        PsiClass [] classes = psiPackage.getClasses ();
        return new ArrayList<PsiClass> (Arrays.asList (classes));
      }
      else if (psiElement instanceof PsiVariable)
      {
        // element under cursor is variable (local variable, field or parameter) => use variable type
        PsiVariable psiVariable = (PsiVariable) psiElement;
        PsiType type = psiVariable.getType ();
        PsiClass psiClass = typeToClass (type);
        if (psiClass != null)
        {
          return Collections.singletonList (psiClass);
        }
      }
      else if (psiElement instanceof PsiMethod)
      {
        // element under cursor is method => check, whether method is constructor
        PsiMethod psiMethod = (PsiMethod) psiElement;
        if (psiMethod.isConstructor ())
        {
          return Collections.singletonList (psiMethod.getContainingClass ());
        }
      }
    }
    // not found so far => use containing class
    Editor editor = DataKeys.EDITOR.getData (dataContext);
    PsiFile psiFile = DataKeys.PSI_FILE.getData (dataContext);
    if (psiFile == null)
    {
      VirtualFile virtualFile = DataKeys.VIRTUAL_FILE.getData (dataContext);
      if (virtualFile != null)
      {
        Project project = DataKeys.PROJECT.getData (dataContext);
        if (project != null)
        {
          PsiManager psiManager = PsiManager.getInstance (project);
          psiFile = psiManager.findFile (virtualFile);
        }
      }
    }
    if (psiFile != null)
    {
      if (editor != null)
      {
        CaretModel caretModel = editor.getCaretModel ();
        int offset = caretModel.getOffset ();
        PsiElement elementAt = psiFile.findElementAt (offset);
        return Collections.singletonList (PsiTreeUtil.getParentOfType (elementAt, PsiClass.class));
      }
      else
      {
        PsiClass psiClass = PsiTreeUtil.getChildOfType (psiFile, PsiClass.class);
        if (psiClass != null)
        {
          return Collections.singletonList (psiClass);
        }
      }
    }
    return Collections.emptyList ();
  }

  /**
   * Finds PSI class for given PSI type.
   * @param psiType PSI type to find class for
   * @return found class or null, if class could not be found
   */
  private static @Nullable PsiClass typeToClass (@NotNull PsiType psiType)
  {
    if (psiType instanceof PsiClassType)
    {
      PsiClassType psiClassType = (PsiClassType) psiType;
      PsiClass psiClass = psiClassType.resolve ();
      if (psiClass != null)
      {
        return psiClass;
      }
    }
    return null;
  }
}
