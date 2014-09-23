package de.frag.umlplugin.uml.command;

import com.intellij.openapi.graph.base.Node;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.psi.ClassFinder;
import de.frag.umlplugin.psi.DependencyCollection;
import de.frag.umlplugin.psi.DependencyReason;
import de.frag.umlplugin.psi.UsageType;
import de.frag.umlplugin.uml.ClassRenamer;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Abstract base class for adding depending classes to diagram.
 */
public abstract class AbstractAddDependentClassesCommand extends AbstractDiagramCommand
{
  protected String qualifiedClassName;

  public AbstractAddDependentClassesCommand (@NotNull String qualifiedClassName)
  {
    this.qualifiedClassName = qualifiedClassName;
  }

  public void execute (@NotNull UMLDiagram diagram)
  {
    PsiClass psiClass = diagram.findPsiClass (qualifiedClassName);
    if (psiClass != null)
    {
      addDependentClasses (diagram, psiClass);
    }
  }

  /**
   * Adds classes dependent to specified class to given diagram.
   * @param diagram diagram
   * @param psiClass subject class
   */
  protected abstract void addDependentClasses (@NotNull UMLDiagram diagram, @NotNull PsiClass psiClass);

  /**
   * Adds given classes to diagram and creates edges to these nodes.
   * @param diagram diagram to add nodes to
   * @param classes collection of classes
   * @param subjectIsSourceNode true, if subject is source node; false otherwise
   */
  protected void addClasses (@NotNull UMLDiagram diagram, @NotNull DependencyCollection classes,
                             boolean subjectIsSourceNode)
  {
    for (PsiClass psiClass : classes)
    {
      Node node = diagram.getNode (psiClass);
      if (node == null)
      {
        node = diagram.addClassNode (psiClass);
      }
      if (node != null)
      {
        List<DependencyReason> reasons = classes.getDependencyReasons (psiClass);
        reasons = ClassFinder.filterReasons (reasons);
        for (DependencyReason reason : reasons)
        {
          UsageType usageType = reason.getUsageType ();
          Node subjectNode = diagram.getNode (qualifiedClassName);
          if (node != subjectNode || usageType == UsageType.FIELD_TYPE_MANY || usageType == UsageType.FIELD_TYPE_ONE)
          {
            if (subjectIsSourceNode)
            {
              diagram.createEdge (subjectNode, node, usageType);
            }
            else
            {
              diagram.createEdge (node, subjectNode, usageType);
            }
          }
        }
      }
    }
  }

  public boolean canExecute (@NotNull UMLDiagram diagram)
  {
    return diagram.getNode (qualifiedClassName) != null;
  }

  protected void addAdditionalInfo (@NotNull Element commandElement)
  {
    commandElement.setAttribute (PSI_CLASS, qualifiedClassName);
  }

  /**
   * Renames all contained class names.
   * @param renamer renamer that will rename classes to keep class names in sync after refactorings were applied.
   */
  public void renameClasses (@NotNull ClassRenamer renamer)
  {
    qualifiedClassName = renamer.rename (qualifiedClassName);
  }
}
