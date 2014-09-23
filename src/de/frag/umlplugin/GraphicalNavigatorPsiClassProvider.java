package de.frag.umlplugin;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.base.DataProvider;
import com.intellij.openapi.graph.base.Graph;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.codenavigator.graph.DataProviderKeys;
import de.frag.umlplugin.codenavigator.graph.GraphBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Gets class that is currently selected in graphical navigator.
 */
public class GraphicalNavigatorPsiClassProvider implements PsiClassProvider
{
  /**
   * Gets class that is currently selected in graphical navigator.
   * @param dataContext data context
   * @return selected class or empty list, if no such class could be found
   */
  public @NotNull List<PsiClass> getPsiClass (@NotNull DataContext dataContext)
  {
    Project project = DataKeys.PROJECT.getData (dataContext);
    Node node = ProjectUtils.get (project, Node.class);
    if (node != null)
    {
      Graph graph = node.getGraph ();
      DataProvider nodeMap = graph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
      GraphBuilder.NodeInfo info = (GraphBuilder.NodeInfo) nodeMap.get (node);
      return Collections.singletonList (info.getPsiClass ());
      
    }
    return Collections.emptyList ();
  }
}