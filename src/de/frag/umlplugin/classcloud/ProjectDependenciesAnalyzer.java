package de.frag.umlplugin.classcloud;

import com.intellij.psi.PsiClass;
import de.frag.umlplugin.codenavigator.graph.DependencyType;
import de.frag.umlplugin.psi.ClassFinder;
import de.frag.umlplugin.psi.DependencyAnalyzer;
import de.frag.umlplugin.psi.DependencyCollection;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Analyzes dependencies between several classes in a project.
 */
public class ProjectDependenciesAnalyzer
{
  private final Set<String>                           classes  = new HashSet<String> ();
  private final BidirectionalMultiMap<String, String> used     = new BidirectionalMultiMap<String, String> ();
  private final BidirectionalMultiMap<String, String> extended = new BidirectionalMultiMap<String, String> ();
  private final Map<String, DependencyInfo>           infos    = new HashMap<String, DependencyInfo> ();

  /**
   * Analyzes dependencies of given class
   * @param psiClass class to analyze
   */
  public void analyzeClass (@NotNull PsiClass psiClass)
  {
    if (!ClassFinder.isProjectClass (psiClass))
    {
      return;
    }
    String qualifiedName = psiClass.getQualifiedName ();
    if (qualifiedName == null || classes.contains (qualifiedName))
    {
      return;
    }
    // add class to analyzed classes and create a dependency analyzer
    classes.add (qualifiedName);
    DependencyAnalyzer dependencyAnalyzer = new DependencyAnalyzer (psiClass, Settings.getSettings ());

    // find all classes that are used by current class
    DependencyCollection usedClasses = dependencyAnalyzer.getUsedClasses ();
    for (PsiClass usedClass : usedClasses)
    {
      if (psiClass != usedClass)
      {
        String usedQualifiedName = usedClass.getQualifiedName ();
        if (usedQualifiedName != null)
        {
          used.add (qualifiedName, usedQualifiedName);
          DependencyInfo.incrementUsingCount (infos, qualifiedName);
          DependencyInfo.incrementUsedCount  (infos, usedQualifiedName);
        }
      }
    }

    // find all classes that are extended by current class
    DependencyCollection extendedClasses = dependencyAnalyzer.getExtendedClasses ();
    for (PsiClass extendedClass : extendedClasses)
    {
      String extendedQualifiedName = extendedClass.getQualifiedName ();
      if (extendedQualifiedName != null)
      {
        extended.add (qualifiedName, extendedQualifiedName);
        DependencyInfo.incrementUsingCount (infos, qualifiedName);
        DependencyInfo.incrementUsedCount  (infos, extendedQualifiedName);
      }
    }

    for (PsiClass innerClass : psiClass.getAllInnerClasses ())
    {
      analyzeClass (innerClass);
    }
  }

  /**
   * Analyzes all dependencies
   */
  public void analyzeDependencies ()
  {
    Set<String> usingClasses     = new HashSet<String> ();
    Set<String> usedClasses      = new HashSet<String> ();
    Set<String> extendingClasses = new HashSet<String> ();
    Set<String> extendedClasses  = new HashSet<String> ();
    for (String usingClass : used.forwardKeys ())
    {
      usingClasses.add (usingClass);
      Set<String> values = used.getForward (usingClass);
      if (values != null)
      {
        for (String usedClass : values)
        {
          usedClasses.add  (usedClass);
        }
      }
    }
    for (String extendingClass : extended.forwardKeys ())
    {
      extendingClasses.add (extendingClass);
      Set<String> values = extended.getForward (extendingClass);
      if (values != null)
      {
        for (String extendedClass : values)
        {
          extendedClasses.add  (extendedClass);
        }
      }
    }
    Set<String> notUsing     = new HashSet<String> (usedClasses);
    Set<String> notUsed      = new HashSet<String> (usingClasses);
    Set<String> notExtending = new HashSet<String> (extendedClasses);
    Set<String> notExtended  = new HashSet<String> (extendingClasses);

    notUsing.removeAll     (usingClasses);
    notUsed.removeAll      (usedClasses);
    notExtending.removeAll (extendingClasses);
    notExtended.removeAll  (extendedClasses);

    traverseDependencies (notUsed,      DependencyType.USING);
    traverseDependencies (notUsing,     DependencyType.USED);
    traverseDependencies (notExtended,  DependencyType.EXTENDING);
    traverseDependencies (notExtending, DependencyType.EXTENDED);

    computeNormalized (DependencyType.USING);
    computeNormalized (DependencyType.EXTENDING);
  }

  /**
   * Gets all classes sorted by distance to center of dependency cloud.
   * @return sorted list of classes
   */
  public @NotNull List<String> getClasses ()
  {
    List<String> sorted = new ArrayList<String> (classes);
    Collections.sort (sorted, new PositionComparator ());
    return sorted;
  }

  /**
   * Gets mapping from qualified class names to dependency infos.
   * @return mapping from qualified class names to dependency infos
   */
  public @NotNull Map<String, DependencyInfo> getInfos ()
  {
    return infos;
  }

  /**
   * Traverses all dependencies from given start classes with respect to given dependency type
   * @param startClasses list of start classes
   * @param dependencyType dependency type
   */
  private void traverseDependencies (@NotNull Collection<String> startClasses, @NotNull DependencyType dependencyType)
  {
    for (String startClass : startClasses)
    {
      DependencyInfo info = DependencyInfo.get (infos, startClass);
      info.index.put (dependencyType, 0);
      traverseDependencies (startClass, 1, dependencyType);
    }
  }

  /**
   * Traverses all dependencies from given start class with respect to given dependency type and computes
   * shortest path to border of class cloud.
   * @param sourceClass start class
   * @param index path length to border starting from given source class
   * @param dependencyType dependency type
   */
  private void traverseDependencies (@NotNull String sourceClass, int index, @NotNull DependencyType dependencyType)
  {
    Set<String> targetClasses;
    switch (dependencyType)
    {
      case USING:     targetClasses = used.getForward      (sourceClass); break;
      case USED:      targetClasses = used.getBackward     (sourceClass); break;
      case EXTENDING: targetClasses = extended.getForward  (sourceClass); break;
      case EXTENDED:  targetClasses = extended.getBackward (sourceClass); break;
      default: throw new IllegalArgumentException ("unknown dependency type: " + dependencyType);
    }
    if (targetClasses != null)
    {
      for (String targetClass : targetClasses)
      {
        DependencyInfo dependencyInfo = DependencyInfo.get (infos, targetClass);
        if (index < dependencyInfo.index.get (dependencyType))
        {
          dependencyInfo.index.put (dependencyType, index);
          traverseDependencies (targetClass, index + 1, dependencyType);
        }
      }
    }
  }

  /**
   * Computes normalized distance to class cloud border for given dependency type.
   * @param dependencyType dependency type
   */
  private void computeNormalized (@NotNull DependencyType dependencyType)
  {
    for (Map.Entry<String, DependencyInfo> infoEntry : infos.entrySet ())
    {
      DependencyInfo info = infoEntry.getValue ();
      int forwardIndex  = info.index.get (dependencyType);
      int backwardIndex = info.index.get (dependencyType.opposite ());
      forwardIndex  = forwardIndex  != Integer.MAX_VALUE ? forwardIndex  : 0;
      backwardIndex = backwardIndex != Integer.MAX_VALUE ? backwardIndex : 0;
      int sum = forwardIndex + backwardIndex;
      if (dependencyType == DependencyType.USING)
      {
        info.normalizedUsing = sum != 0 ? (double) forwardIndex / (double) sum : 0.5;
      }
      else if (dependencyType == DependencyType.EXTENDING)
      {
        info.normalizedExtending = sum != 0 ? (double) forwardIndex / (double) sum : 0.5;
      }
    }
  }

  /**
   * Clears all cached content.
   */
  public void clear ()
  {
    this.classes.clear ();
    this.infos.clear ();
    this.used.clear ();
    this.extended.clear ();
  }

  /**
   * Comparator that can be used to sort classes by distance to center of class cloud starting with center classes.
   */
  private class PositionComparator implements Comparator<String>
  {
    public int compare (@NotNull String className1, @NotNull String className2)
    {
      DependencyInfo info1 = DependencyInfo.get (infos, className1);
      DependencyInfo info2 = DependencyInfo.get (infos, className2);
      double x1 = info1.normalizedUsing     - 0.5;
      double y1 = info1.normalizedExtending - 0.5;
      double distance1 = x1 * x1 + y1 * y1;
      double x2 = info2.normalizedUsing     - 0.5;
      double y2 = info2.normalizedExtending - 0.5;
      double distance2 = x2 * x2 + y2 * y2;
      return (int) (100 * (distance1 - distance2));
    }
  }

  /**
   * Dependency information for single class in class cloud.
   */
  public static class DependencyInfo
  {
    private final Map<DependencyType, Integer> index = new EnumMap<DependencyType, Integer> (DependencyType.class);
    private double normalizedUsing;
    private double normalizedExtending;
    private int usingCount;
    private int usedCount;

    private DependencyInfo ()
    {
      index.put (DependencyType.USING,     Integer.MAX_VALUE);
      index.put (DependencyType.USED,      Integer.MAX_VALUE);
      index.put (DependencyType.EXTENDING, Integer.MAX_VALUE);
      index.put (DependencyType.EXTENDED,  Integer.MAX_VALUE);
    }

    public double getNormalizedUsing ()
    {
      return normalizedUsing;
    }

    public double getNormalizedExtending ()
    {
      return normalizedExtending;
    }

    public int getUsingCount ()
    {
      return usingCount;
    }

    public int getUsedCount ()
    {
      return usedCount;
    }

    public @NotNull String toString ()
    {
      return " -> " + usingCount + " <- " + usedCount +
             " [" + index.get (DependencyType.USING) + ", " + index.get (DependencyType.USED) + ", " +
             index.get (DependencyType.EXTENDING) + ", " + index.get (DependencyType.EXTENDED) + "] " +
             "[" + normalizedUsing + ", " + normalizedExtending + "]";
    }

    private static void incrementUsingCount (@NotNull Map<String, DependencyInfo> infos, @NotNull String qualifiedName)
    {
      get (infos, qualifiedName).usingCount++;
    }

    private static void incrementUsedCount (@NotNull Map<String, DependencyInfo> infos, @NotNull String qualifiedName)
    {
      get (infos, qualifiedName).usedCount++;
    }

    public static DependencyInfo get (@NotNull Map<String, DependencyInfo> infos, @NotNull String qualifiedName)
    {
      DependencyInfo info = infos.get (qualifiedName);
      if (info == null)
      {
        info = new DependencyInfo ();
        infos.put (qualifiedName, info);
      }
      return info;
    }
  }
}
