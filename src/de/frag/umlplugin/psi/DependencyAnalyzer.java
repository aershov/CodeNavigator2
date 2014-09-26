package de.frag.umlplugin.psi;

import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Analyzes dependencies from one class to any other classes.
 */
public class DependencyAnalyzer
{
  private final PsiClass             psiClass;
  private final DependencyCollection extendedClasses  = new DependencyCollection ();
  private final DependencyCollection extendingClasses = new DependencyCollection ();
  private final DependencyCollection usedClasses      = new DependencyCollection ();
  private final DependencyCollection usingClasses     = new DependencyCollection ();
  private final Settings             settings;
  private boolean analyzedUsing     = false;
  private boolean analyzedUsed      = false;
  private boolean analyzedExtending = false;
  private boolean analyzedExtended  = false;

  /**
   * Creates a dependency analyzer that analyzes dependencies from given class to any other classes.
   * @param psiClass PSI class to analyze dependencies for
   * @param settings settings
   */
  public DependencyAnalyzer (@NotNull PsiClass psiClass, @NotNull Settings settings)
  {
    this.settings = settings;
    this.psiClass = psiClass;
  }

  /**
   * Gets all classes that are extended or implemented by the analyzed class.
   * @return dependency collection that contains all extended or implemented classes
   */
  public @NotNull DependencyCollection getExtendedClasses ()
  {
    if (!analyzedExtended)
    {
      computeExtendedClasses ();
      analyzedExtended = true;
    }
    return extendedClasses;
  }

  /**
   * Gets all classes that extended or implement the analyzed class.
   * @return dependency collection that contains extending or implementing classes
   */
  public @NotNull DependencyCollection getExtendingClasses ()
  {
    if (!analyzedExtending)
    {
      computeUsingClasses ();
      analyzedExtending = true;
      analyzedUsing = true;
    }
    return extendingClasses;
  }

  /**
   * Gets all classes that are used by the analyzed class.
   * @return dependency collection that contains all used classes
   */
  public @NotNull DependencyCollection getUsedClasses ()
  {
    if (!analyzedUsed)
    {
      computeUsedClasses ();
      analyzedUsed = true;
    }
    return usedClasses;
  }

  /**
   * Gets all classes that use the analyzed class.
   * @return dependency collection that contains all classes that use the analyzed class
   */
  public @NotNull DependencyCollection getUsingClasses ()
  {
    if (!analyzedUsing)
    {
      computeUsingClasses ();
      analyzedUsing = true;
      analyzedExtending = true;
    }
    return usingClasses;
  }

  /**
   * Computes the class type of the given class.
   * @param psiClass class to get class type for
   * @return class type
   */
  public static @NotNull ClassType computeClassType (@NotNull PsiClass psiClass)
  {
    if (psiClass.isEnum ())
    {
      return ClassType.ENUM;
    }
    else if (psiClass.isInterface ())
    {
      return ClassType.INTERFACE;
    }
    else if (psiClass.hasModifierProperty (PsiModifier.ABSTRACT))
    {
      return ClassType.ABSTRACT_CLASS;
    }
    else
    {
      return ClassType.CLASS;
    }
  }

  /**
   * Computes extended classes.
   */
  private void computeExtendedClasses ()
  {
    PsiClass superClass = psiClass.getSuperClass ();
    if (superClass != null)
    {
      String superClassName = superClass.getQualifiedName ();
      if (superClassName != null && !superClassName.equals ("java.lang.Object"))
      {
        addDependency (extendedClasses, superClass, UsageType.EXTENDS, 0);
      }
    }
    for (PsiClass implementedInterface : psiClass.getInterfaces ())
    {
      addDependency (extendedClasses, implementedInterface, UsageType.IMPLEMENTS, 0);
    }
  }

  /**
   * Adds a dependency to the given collection.
   * @param collection collection to add dependency to
   * @param psiClass class to add
   * @param usageType usage type for class
   * @param offset source offset for usage
   */
  private void addDependency (@NotNull DependencyCollection collection, @NotNull PsiClass psiClass,
                              @NotNull UsageType usageType, int offset)
  {
    if ((settings.isIncludeNonProjectClasses () || ClassFinder.isProjectClass (psiClass)) &&
        (settings.isIncludeTestClasses () || !ClassFinder.isTestClass (psiClass)) &&
        (ClassFinder.isProjectClass (this.psiClass) || ClassFinder.isProjectClass (psiClass)))
    {
      collection.addDependency (psiClass, usageType, offset);
    }
  }

  /**
   * Computes used classes for current class.
   */
  private void computeUsedClasses ()
  {
      JavaElementVisitor visitor = new JavaRecursiveElementVisitor() {
      private boolean belowField             = false;
      private boolean belowNew               = false;
      private boolean belowCollection        = false;
      private boolean belowClassObjectAccess = false;
      private boolean belowInnerClass        = false;

      public void visitClass (PsiClass aClass)
      {
        if (belowInnerClass || (aClass instanceof PsiAnonymousClass))
        {
          super.visitClass (aClass);
        }
        else
        {
          belowInnerClass = true;
          super.visitClass(aClass);
          belowInnerClass = false;
        }
      }
      public void visitField (PsiField field)
      {
        if (belowInnerClass)
        {
          return;
        }
        belowField = true;
        super.visitField (field);
        belowField = false;
      }
      public void visitNewExpression (PsiNewExpression expression)
      {
        if (belowInnerClass)
        {
          return;
        }
        belowNew = true;
        PsiType type = expression.getType ();
        if (type != null)
        {
          PsiClass psiClass = findConcreteClass (typeToClass (type));
          if (psiClass != null)
          {
            addDependency (usedClasses, psiClass, UsageType.NEW_EXPRESSION, expression.getTextOffset ());
          }
        }
        super.visitNewExpression (expression);
        belowNew = false;
      }
      public void visitClassObjectAccessExpression (PsiClassObjectAccessExpression expression)
      {
        if (belowInnerClass)
        {
          return;
        }
        belowClassObjectAccess = true;
        super.visitClassObjectAccessExpression (expression);
        belowClassObjectAccess = false;
      }
      public void visitTypeElement (PsiTypeElement typeElement)
      {
        if (belowInnerClass)
        {
          return;
        }
        PsiType type = typeElement.getType ();
        PsiClass psiClass = typeToClass (type);
        if (psiClass != null && !(psiClass instanceof PsiTypeParameter))
        {
          if (belowClassObjectAccess)
          {
            addDependency (usedClasses, psiClass, UsageType.STATIC_REFERENCE, typeElement.getTextOffset ());
          }
          else if (belowField && !belowNew && !belowCollection && !(type instanceof PsiArrayType))
          {
            addDependency (usedClasses, psiClass, UsageType.FIELD_TYPE_ONE, typeElement.getTextOffset ());
          }
          else if (belowField && !belowNew && belowCollection && !(type instanceof PsiArrayType))
          {
            addDependency (usedClasses, psiClass, UsageType.FIELD_TYPE_MANY, typeElement.getTextOffset ());
          }
          else // no field
          {
            addDependency (usedClasses, psiClass, UsageType.REFERENCE, typeElement.getTextOffset ());
          }
        }
        if (belowField && !belowNew && !belowCollection && isCollection (typeElement))
        {
          belowCollection = true;
          super.visitTypeElement (typeElement);
          belowCollection = false;
        }
        else
        {
          super.visitTypeElement (typeElement);
        }
      }
      public void visitReferenceExpression (PsiReferenceExpression referenceExpression)
      {
        if (belowInnerClass)
        {
          return;
        }
        PsiExpression qualifierExpression = referenceExpression.getQualifierExpression ();
        if (qualifierExpression != null)
        {
          PsiReference psiReference = qualifierExpression.getReference ();
          if (psiReference != null)
          {
            PsiElement reference = psiReference.resolve ();
            if (reference instanceof PsiClass)
            {
              addDependency (usedClasses, (PsiClass) reference, UsageType.STATIC_REFERENCE,
                             referenceExpression.getTextOffset ());
            }
          }
        }
        super.visitReferenceExpression (referenceExpression);
      }
    };
    visitor.visitElement (psiClass);
  }

  /**
   * Computes all classes that use the current PSI class.
   */
  private void computeUsingClasses ()
  {
    Query<PsiReference> query = ReferencesSearch.search (psiClass);
    Collection<PsiReference> references = query.findAll ();
    for (PsiReference reference : references)
    {
      PsiElement referencingElement = reference.getElement ();
      PsiClass referencingClass = PsiTreeUtil.getParentOfType (referencingElement, PsiClass.class);
      referencingClass = findConcreteClass (referencingClass);
      if (referencingClass != null)
      {
        PsiElement parentElement = referencingElement.getParent ();
        if (parentElement != null)
        {
          if (parentElement instanceof PsiTypeElement)
          {
            PsiElement typeElement = parentElement;
            PsiClassObjectAccessExpression classObject =
                    PsiTreeUtil.getParentOfType (typeElement, PsiClassObjectAccessExpression.class);
            if (classObject != null)
            {
              addDependency (usingClasses, referencingClass, UsageType.STATIC_REFERENCE,
                             referencingElement.getTextOffset ());
            }
            else
            {
              PsiField field = PsiTreeUtil.getParentOfType (typeElement, PsiField.class);
              if (field != null)
              {
                boolean collection = false;
                parentElement = PsiTreeUtil.getParentOfType (parentElement, PsiTypeElement.class);
                while (parentElement != null && parentElement instanceof PsiTypeElement && !collection)
                {
                  if (isCollection ((PsiTypeElement) parentElement))
                  {
                    collection = true;
                  }
                  parentElement = PsiTreeUtil.getParentOfType (parentElement, PsiTypeElement.class);
                }
                addDependency (usingClasses, referencingClass,
                               collection ? UsageType.FIELD_TYPE_MANY : UsageType.FIELD_TYPE_ONE,
                               referencingElement.getTextOffset ());
              }
              else
              {
                addDependency (usingClasses, referencingClass, UsageType.REFERENCE,
                               referencingElement.getTextOffset ());
              }
            }
          }
          else if (parentElement instanceof PsiReferenceList)
          {
            PsiElement firstChild = parentElement.getFirstChild ();
            if (firstChild != null)
            {
              if (firstChild.getText ().equals ("implements"))
              {
                addDependency (extendingClasses, referencingClass, UsageType.IMPLEMENTS,
                               referencingElement.getTextOffset ());
              }
              if (firstChild.getText ().equals ("extends"))
              {
                addDependency (extendingClasses, referencingClass, UsageType.EXTENDS,
                               referencingElement.getTextOffset ());
              }
            }
          }
          else if (parentElement instanceof PsiNewExpression)
          {
            addDependency (usingClasses, referencingClass, UsageType.NEW_EXPRESSION,
                           referencingElement.getTextOffset ());
          }
          else if (parentElement instanceof PsiReferenceExpression)
          {
            addDependency (usingClasses, referencingClass, UsageType.STATIC_REFERENCE,
                           referencingElement.getTextOffset ());
          }
        }
      }
    }
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
      PsiClass psiClass = ((PsiClassType) psiType).resolve ();
      if (psiClass != null)
      {
        return psiClass;
      }
    }
    else if (psiType instanceof PsiArrayType)
    {
      PsiType deepComponentType = ((PsiArrayType) psiType).getComponentType ().getDeepComponentType ();
      if (deepComponentType instanceof PsiClassType)
      {
        PsiClass psiClass = ((PsiClassType) deepComponentType).resolve ();
        if (psiClass != null)
        {
          return psiClass;
        }
      }
    }
    return null;
  }

  /**
   * Checks whether given type is collection class or array.
   * @param typeElement type element to be checked
   * @return true, if given type element is collection or array; false otherwise
   */
  private boolean isCollection (@NotNull PsiTypeElement typeElement)
  {
    PsiType type = typeElement.getType ();
    if (type instanceof PsiArrayType)
    {
      return true;
    }
    PsiClass psiClass = typeToClass (type);
    if (psiClass != null)
    {
      String className = psiClass.getQualifiedName ();
      //noinspection SuspiciousMethodCalls
      if (className != null && (settings.getCollectionClasses ().contains (className)))
      {
        return true;
      }
      List<PsiClass> interfaces = getAllInterfaces (psiClass);
      for (PsiClass implementedInterface : interfaces)
      {
        String interfaceName = implementedInterface.getQualifiedName ();
        //noinspection SuspiciousMethodCalls
        if (interfaceName != null && settings.getCollectionClasses ().contains (interfaceName))
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Gets all implemented interfaces for given class.
   * @param psiClass class to get all implemented interfaces for
   * @return list of implemented interfaces
   */
  private @NotNull List<PsiClass> getAllInterfaces (@NotNull PsiClass psiClass)
  {
    List<PsiClass> interfaces = new ArrayList<PsiClass> ();
    PsiClass[] implementedInterfaces = psiClass.getInterfaces ();
    for (PsiClass implementedInterface : implementedInterfaces)
    {
      interfaces.add (implementedInterface);
      interfaces.addAll (getAllInterfaces (implementedInterface));
    }
    return interfaces;
  }

  /**
   * Finds concrete class for given class. If given class is an anonymous class the "nearest" containing
   * concrete class is returned. If given class is a concrete class, it is returned directly.
   * @param psiClass psi class to find concrete class for
   * @return given class itself or nearest containing concrete class.
   */
  private @Nullable PsiClass findConcreteClass (@Nullable PsiClass psiClass)
  {
    while (psiClass != null && psiClass instanceof PsiAnonymousClass)
    {
      psiClass = PsiTreeUtil.getParentOfType (psiClass, PsiClass.class);
    }
    return psiClass;
  }
}
