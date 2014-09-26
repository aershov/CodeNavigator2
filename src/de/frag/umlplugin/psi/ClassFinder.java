package de.frag.umlplugin.psi;

//import com.intellij.codeInsight.TestUtil;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchScopeUtil;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds classes.
 */
public class ClassFinder
{
  /**
   * Finds psi class for given qualified class name.
   * @param project current project
   * @param qualifiedClassName qualified class name
   * @return found psi class or null, if no psi class with given qualified name could be found
   */
  public static @Nullable PsiClass findPsiClass (@NotNull Project project, @NotNull String qualifiedClassName)
  {
//    PsiManager psiManager = PsiManager.getInstance(project);
//    return psiManager.findClass (qualifiedClassName, project.getProjectScope ());
      JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
      return psiFacade.findClass(qualifiedClassName, GlobalSearchScope.projectScope(project));
  }

  /**
   * Finds all classes for given data context.
   * @param project current project
   * @param searchScope search scope that will be used to limit found classes
   * @return list of all found classes
   */
  public static @NotNull List<PsiClass> findAllClassesForContext (@NotNull Project project,
                                                                  @NotNull SearchScope searchScope)
  {
    final ProjectRootManager rootManager = ProjectRootManager.getInstance (project);
    final PsiManager psiManager = PsiManager.getInstance (project);
    final VirtualFile [] directories = rootManager.getContentSourceRoots ();
    return findAllClasses (directories, psiManager, searchScope);
  }

  /**
   * Finds all classes in given directories.
   * @param directories directories to search
   * @param psiManager PSI manager
   * @param searchScope search scope that will be used to limit found classes
   * @return list of found classes
   */
  private static @NotNull List<PsiClass> findAllClasses (@NotNull VirtualFile [] directories,
                                                         @NotNull PsiManager psiManager,
                                                         @NotNull SearchScope searchScope)
  {
    List<PsiClass> result = new ArrayList<PsiClass> ();
    for (VirtualFile directory : directories)
    {
      final PsiDirectory psiDirectory = psiManager.findDirectory (directory);
      if (psiDirectory != null)
      {
        addAllClasses (psiDirectory, result, searchScope);
      }
    }
    return result;
  }

  /**
   * Adds all classes in given directory to specified list of classes.
   * @param directory directory to scan
   * @param classes list of classes to add found classes to
   * @param searchScope search scope that will be used to limit found classes
   */
  private static void addAllClasses (@NotNull PsiDirectory directory, @NotNull List<PsiClass> classes,
                                     @NotNull SearchScope searchScope)
  {

    PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(directory);// directory.getPackage ();
    if (psiPackage != null)
    {
      PsiClass [] containedClasses = psiPackage.getClasses ();
      for (PsiClass psiClass : containedClasses)
      {
        if (PsiSearchScopeUtil.isInScope (searchScope, psiClass))
        {
          classes.add (psiClass);
        }
      }
      PsiDirectory [] subDirectories = directory.getSubdirectories ();
      for (PsiDirectory subDirectory : subDirectories)
      {
        addAllClasses (subDirectory, classes, searchScope);
      }
    }
  }

  /**
   * Checks whether given class is a project class or a reference to an external class.
   * @param psiClass class to be checked
   * @return true, if class is a project class; false it is a reference to an external class
   */
  public static boolean isProjectClass (@NotNull PsiElement psiClass)
  {
    Project project = DataKeys.PROJECT.getData (DataManager.getInstance ().getDataContext ());
    PsiFile containingFile = psiClass.getContainingFile ();
    if (project == null || containingFile == null)
    {
      return false;
    }
    VirtualFile virtualFile = containingFile.getVirtualFile ();
    if (virtualFile == null)
    {
      return false;
    }
    ProjectRootManager projectRootManager = ProjectRootManager.getInstance (project);
    ProjectFileIndex projectFileIndex = projectRootManager.getFileIndex ();
    boolean inLibrarySource = projectFileIndex.isInLibrarySource (virtualFile);
    boolean inLibraryClasses = projectFileIndex.isInLibraryClasses (virtualFile);
    //System.out.println (psiClass + " is in library source: " + inLibrarySource + ", in library classes: " + inLibraryClasses);
    return !inLibrarySource && !inLibraryClasses;
  }

  /**
   * Checks whether given class is a test class or a production class.
   * @param psiClass class to be checked
   * @return true, if class is a test class; false it is a production class
   */
  public static boolean isTestClass (@NotNull PsiClass psiClass)
  {
    Project project = DataKeys.PROJECT.getData (DataManager.getInstance ().getDataContext ());
    PsiFile containingFile = psiClass.getContainingFile ();
    if (project == null || containingFile == null)
    {
      return false;
    }
    VirtualFile virtualFile = containingFile.getVirtualFile ();
    if (virtualFile == null)
    {
      return false;
    }
    ProjectRootManager projectRootManager = ProjectRootManager.getInstance (project);
    ProjectFileIndex projectFileIndex = projectRootManager.getFileIndex ();
    boolean inTestSourceContent = projectFileIndex.isInTestSourceContent (virtualFile);
//    boolean testClass = TestUtil.isTestClass (psiClass);
    //System.out.println (psiClass + " is in test source: " + inTestSourceContent + ", is test class: " + testClass);
    return inTestSourceContent;// || testClass;
  }

  /**
   * Filters reasons so that the number of dependencies is minimized.
   * @param reasons reasons to filter
   * @return filtered reasons
   */
  public static @NotNull List<DependencyReason> filterReasons (@NotNull List<DependencyReason> reasons)
  {
    // first check whether fields or new expressions are contained in given reason list
    boolean oneFieldsContained  = false;
    boolean manyFieldsContained = false;
    boolean newContained        = false;
    for (DependencyReason reason : reasons)
    {
      if (reason.getUsageType () == UsageType.FIELD_TYPE_ONE)
      {
        oneFieldsContained = true;
      }
      if (reason.getUsageType () == UsageType.FIELD_TYPE_MANY)
      {
        manyFieldsContained = true;
      }
      else if (reason.getUsageType () == UsageType.NEW_EXPRESSION)
      {
        newContained = true;
      }
    }
    // now filter given reason list
    List<DependencyReason> filteredReasons = new ArrayList<DependencyReason> ();
    for (DependencyReason reason : reasons)
    {
      boolean isManyField         = reason.getUsageType () == UsageType.FIELD_TYPE_MANY;
      boolean isOneField          = !manyFieldsContained && reason.getUsageType () == UsageType.FIELD_TYPE_ONE;
      boolean isNewAndNotField    = !oneFieldsContained && !manyFieldsContained &&
                                    reason.getUsageType () == UsageType.NEW_EXPRESSION;
      boolean isNotFieldAndNotNew = !oneFieldsContained && !manyFieldsContained && !newContained;
      boolean isEmpty             = filteredReasons.isEmpty ();

      if ((isManyField || isOneField || isNewAndNotField || isNotFieldAndNotNew) && isEmpty)
      {
        filteredReasons.add (reason);
      }
    }
    return filteredReasons;
  }
}
