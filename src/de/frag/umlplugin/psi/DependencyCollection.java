package de.frag.umlplugin.psi;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Contains several dependencies from one class to another.
 */
public class DependencyCollection implements Iterable<PsiClass>
{
  private final Map<PsiClass, List<DependencyReason>> dependencies = new HashMap<PsiClass, List<DependencyReason>> ();

  /**
   * Adds a dependency.
   * @param psiClass PSI class
   * @param usageType type of usage for given PSI class
   * @param offset source offset
   */
  public void addDependency (@NotNull PsiClass psiClass, @NotNull UsageType usageType, int offset)
  {
    List<DependencyReason> dependencyReasonList = dependencies.get (psiClass);
    if (dependencyReasonList == null)
    {
      dependencyReasonList = new ArrayList<DependencyReason> ();
      dependencies.put (psiClass, dependencyReasonList);
    }
    // usage type not found => add new entry
    dependencyReasonList.add (new DependencyReason (usageType, offset));
  }

  /**
   * Gets an iterator that iterates over all dependencies.
   * @return iterator that iterates over all dependencies
   */
  public @NotNull Iterator<PsiClass> iterator ()
  {
    List<PsiClass> classes = new ArrayList<PsiClass> (dependencies.keySet ());
    Collections.sort (classes, new Comparator<PsiClass> () {
      public int compare (PsiClass class1, PsiClass class2)
      {
        String name1 = class1.getName ();
        String name2 = class2.getName ();
        name1 = name1 == null ? "" : name1;
        name2 = name2 == null ? "" : name2;
        return name1.compareTo (name2);
      }
    });
    return classes.iterator ();
  }

  /**
   * Gets a list of dependency reasons for given PSI class.
   * @param psiClass PSI class to get dependency reasons for
   * @return list of dependency reasons (may be empty)
   */
  public @NotNull List<DependencyReason> getDependencyReasons (@NotNull PsiClass psiClass)
  {
    List<DependencyReason> dependencyReasonList = dependencies.get (psiClass);
    if (dependencyReasonList == null)
    {
      dependencyReasonList = Collections.emptyList ();
    }
    return dependencyReasonList;
  }

  /**
   * Filter for restricting dependency collection contents.
   */
  public interface Filter
  {
    /**
     * Accept dependency reason.
     * @param reason reason to check
     * @return true, if reasn is accepted; false if it should be filtered
     */
    public boolean accepts (@NotNull DependencyReason reason);
  }

  /**
   * Filter that accepts only fields.
   */
  public static final Filter FIELD_FILTER = new Filter ()
  {
    public boolean accepts (@NotNull DependencyReason reason)
    {
      UsageType usageType = reason.getUsageType ();
      return usageType == UsageType.FIELD_TYPE_MANY || usageType == UsageType.FIELD_TYPE_ONE;
    }
  };

  /**
   * Filter contents of this dependency collection and return new restricted collection.
   * @param filter filter to use for filtering this collection.
   * @return new collection that only contains dependencies accepted by given filter
   */
  public @NotNull DependencyCollection filter (@NotNull Filter filter)
  {
    DependencyCollection result = new DependencyCollection ();
    for (Map.Entry<PsiClass, List<DependencyReason>> entry : dependencies.entrySet ())
    {
      List<DependencyReason> reasons = entry.getValue ();
      List<DependencyReason> filteredReasons = new ArrayList<DependencyReason> ();
      for (DependencyReason reason : reasons)
      {
        if (filter.accepts (reason))
        {
          filteredReasons.add (reason);
        }
      }
      if (!filteredReasons.isEmpty ())
      {
        result.dependencies.put (entry.getKey (), filteredReasons);
      }
    }
    return result;
  }

  /**
   * Creates a string representation of this collection.
   * @return string representation
   */
  public @NotNull String toString ()
  {
    StringBuilder builder = new StringBuilder ();
    for (PsiClass psiClass : this)
    {
      String className = computeClassName (psiClass);
      boolean isProjectClass = ClassFinder.isProjectClass (psiClass);
      builder.append ("\n").append (className);
      builder.append (isProjectClass ? " (java)" : " (class)");
      List<DependencyReason> reasons = getDependencyReasons (psiClass);
      for (DependencyReason reason : reasons)
      {
        builder.append ("\n  ").append (reason);
      }
    }
    return builder.toString ();
  }

  /**
   * Computes name of given class.
   * @param psiClass class to compute name for
   * @return computed class name (includes containing class name, if given class is inner class)
   */
  private @Nullable String computeClassName (@NotNull PsiClass psiClass)
  {
    PsiClass containingClass = psiClass.getContainingClass ();
    if (containingClass != null)
    {
      return containingClass.getName () + "." + psiClass.getName ();
    }
    else
    {
      return psiClass.getName ();
    }
  }
}
