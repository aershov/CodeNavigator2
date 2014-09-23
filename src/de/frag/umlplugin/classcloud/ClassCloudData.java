package de.frag.umlplugin.classcloud;

import com.intellij.openapi.graph.base.Graph;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.base.NodeMap;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.psi.ClassFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages data associated to class cloud node.
 */
public class ClassCloudData
{
  private static final String CLASS_CLOUD_DATA_PROVIDER_KEY = "CLASS_CLOUD_DATA_PROVIDER_KEY";

  /**
   * Attaches cell to given node by using an appropriate node map.
   * @param node node to attach cell to
   * @param cell cell to attach
   */
  public static void attachCell (@NotNull Node node, @NotNull Cell cell)
  {
    NodeMap nodeMap = getNodeMap (node);
    nodeMap.set (node, cell);
  }

  /**
   * Finds psi class for given class cloud node.
   * @param project current project
   * @param node class cloud node
   * @return found psi class or null, if no psi class with given node could be found
   */
  public static @Nullable PsiClass findPsiClass (@NotNull Project project, @NotNull Node node)
  {
    Cell cell = findCell (node);
    if (cell != null)
    {
      String qualifiedClassName = cell.getClassName ();
      return ClassFinder.findPsiClass (project, qualifiedClassName);
    }
    return null;
  }
  
  /**
   * Finds cell containing cell information (class name, dependency count and so on) for given node.
   * @param node node to find cell for
   * @return found cell or null, if no cell could be found
   */
  public static @Nullable Cell findCell (@NotNull Node node)
  {
    NodeMap nodeMap = getNodeMap (node);
    return (Cell) nodeMap.get (node);
  }

  /**
   * Gets node map for given node.
   * @param node node to get node map for
   * @return existing node map or new node map, if no node map was found in graph
   */
  private static @NotNull NodeMap getNodeMap (@NotNull Node node)
  {
    Graph graph = node.getGraph ();
    NodeMap nodeMap = (NodeMap) graph.getDataProvider (CLASS_CLOUD_DATA_PROVIDER_KEY);
    if (nodeMap == null)
    {
      nodeMap = graph.createNodeMap ();
      graph.addDataProvider (CLASS_CLOUD_DATA_PROVIDER_KEY, nodeMap);
    }
    return nodeMap;
  }
}
