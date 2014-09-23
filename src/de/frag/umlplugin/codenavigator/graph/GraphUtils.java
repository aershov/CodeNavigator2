package de.frag.umlplugin.codenavigator.graph;

import com.intellij.openapi.graph.base.DataProvider;
import com.intellij.openapi.graph.base.Graph;
import com.intellij.openapi.graph.base.Node;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Graph related utilities.
 */
public class GraphUtils
{
  private GraphUtils () {}

  /**
   * Finds node with matching class and dependency type in given graph.
   * @param graph find node in this graph
   * @param psiClass find node with this class as node info
   * @param dependencyType find node with this dependency type as node info
   * @return node in given graph with matching node info (or null, if node could not be found)
   */
  public static @Nullable Node findNode (@NotNull Graph graph, @NotNull PsiClass psiClass,
                                         @NotNull DependencyType dependencyType)
  {
    DataProvider nodeMap = graph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
    for (Node node : graph.getNodeArray ())
    {
      GraphBuilder.NodeInfo nodeInfo = (GraphBuilder.NodeInfo) nodeMap.get (node);
      if (psiClass.equals (nodeInfo.getPsiClass ()) && nodeInfo.getDependencyType () == dependencyType)
      {
        return node;
      }
    }
    return null;
  }

  /**
   * Finds subject node in given graph.
   * @param graph find node in this graph
   * @return node in given graph with (or null, if node could not be found)
   */
  public static @Nullable Node findSubjectNode (@NotNull Graph graph)
  {
    DataProvider nodeMap = graph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
    for (Node node : graph.getNodeArray ())
    {
      GraphBuilder.NodeInfo nodeInfo = (GraphBuilder.NodeInfo) nodeMap.get (node);
      if (nodeInfo.getDependencyType () == DependencyType.SUBJECT)
      {
        return node;
      }
    }
    return null;
  }

  /**
   * Gets node info for given node.
   * @param node node to get info for
   * @return found node info or null, if no node info could be found
   */
  public static @Nullable GraphBuilder.NodeInfo getNodeInfo (@NotNull Node node)
  {
    Graph graph = node.getGraph ();
    DataProvider nodeMap = graph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
    return (GraphBuilder.NodeInfo) nodeMap.get (node);
  }
}
