package de.frag.umlplugin.uml.command;

import com.intellij.openapi.graph.base.Node;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.psi.ClassFinder;
import de.frag.umlplugin.psi.DependencyCollection;
import de.frag.umlplugin.psi.DependencyReason;
import de.frag.umlplugin.psi.UsageType;
import de.frag.umlplugin.uml.UMLDiagram;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Abstract base class for commands that connect existing nodes.
 */
public abstract class AbstractConnectCommand extends AbstractDiagramCommand
{
  /**
   * Adds edges between given subject node and given classes to diagram.
   * @param diagram diagram to add edges to
   * @param subjectNode subject node
   * @param classes collection of classes
   * @param subjectIsSourceNode true, if subject is source node; false otherwise
   */
  protected void addEdges (@NotNull UMLDiagram diagram, @NotNull Node subjectNode,
                           @NotNull DependencyCollection classes, boolean subjectIsSourceNode)
  {
    for (PsiClass psiClass : classes)
    {
      Node node = diagram.getNode (psiClass);
      if (node != null)
      {
        List<DependencyReason> reasons = classes.getDependencyReasons (psiClass);
        reasons = ClassFinder.filterReasons (reasons);
        for (DependencyReason reason : reasons)
        {
          UsageType usageType = reason.getUsageType ();
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
}
