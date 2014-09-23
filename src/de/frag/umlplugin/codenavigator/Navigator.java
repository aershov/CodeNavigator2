package de.frag.umlplugin.codenavigator;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.NavigatableFileEditor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.graph.base.DataProvider;
import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.anim.AnimationBuilder;
import de.frag.umlplugin.anim.AnimationListenerAdapter;
import de.frag.umlplugin.anim.AnimationObject;
import de.frag.umlplugin.anim.AnimationPlayer;
import de.frag.umlplugin.codenavigator.graph.DataProviderKeys;
import de.frag.umlplugin.codenavigator.graph.DependencyType;
import de.frag.umlplugin.codenavigator.graph.GraphBuilder;
import de.frag.umlplugin.codenavigator.graph.GraphUtils;
import de.frag.umlplugin.history.NavigationHistory;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

/**
 * Handles navigation to nodes and source files.
 */
public class Navigator
{
  private final Project           project;
  private final Graph2DView       graph2DView;
  private final NavigationHistory navigationHistory;

  /**
   * Creates new navigator.
   * @param project project
   * @param graph2DView graph view
   * @param navigationHistory navigation history
   */
  public Navigator (@NotNull Project project, @NotNull Graph2DView graph2DView,
                    @NotNull NavigationHistory navigationHistory)
  {
    this.project = project;
    this.graph2DView = graph2DView;
    this.navigationHistory = navigationHistory;
  }

  /**
   * Starts graphical naviagtion using given class as first subject class.
   * @param psiClass navigation start class
   */
  public void startNavigation (@NotNull PsiClass psiClass)
  {
    NavigatorProjectComponent projectComponent = ProjectUtils.get (project, NavigatorProjectComponent.class);
    NavigationHistory         history          = ProjectUtils.get (project, NavigationHistory.class);
    if (projectComponent != null && history != null)
    {
      ToolWindowManager toolWindowManager = ToolWindowManager.getInstance (project);
      ToolWindow toolWindow = toolWindowManager.getToolWindow (NavigatorProjectComponent.GRAPHICAL_NAVIGATOR_TOOL_WINDOW_ID);

      GraphBuilder graphBuilder = new GraphBuilder ();
      Graph2D graph2D = graphBuilder.createGraph (psiClass);
      final Graph2DView graph2DView = projectComponent.getGraph2DView ();
      graph2DView.setGraph2D (graph2D);
      history.clear ();

      toolWindow.activate (new Runnable () {
        public void run ()
        {
          graph2DView.adjustScrollBarVisibility ();
          graph2DView.fitContent ();
          graph2DView.updateView ();
        }
      });
    }
  }

  /**
   * Navigates to given class and dependency type.
   * @param psiClass class to navigate to
   * @param dependencyType dependency type to navigate to
   */
  public void navigate (@NotNull PsiClass psiClass, @NotNull DependencyType dependencyType)
  {
    Graph2D graph = graph2DView.getGraph2D ();
    Node node = GraphUtils.findNode (graph, psiClass, dependencyType);
    if (node != null)
    {
      Graph2D oldGraph = graph2DView.getGraph2D ();
      DataProvider nodeMap = oldGraph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
      GraphBuilder.NodeInfo info = (GraphBuilder.NodeInfo) nodeMap.get (node);
      PsiClass classToNavigateTo = info.getPsiClass ();
      GraphBuilder graphBuilder = new GraphBuilder ();
      Graph2D newGraph = graphBuilder.createGraph (classToNavigateTo);
      navigateToClass (oldGraph, newGraph, node);
    }
  }

  /**
   * Navigates to given node.
   * @param node node to navigate to
   */
  public void navigate (@NotNull Node node)
  {
    Graph2D oldGraph = graph2DView.getGraph2D ();
    DataProvider nodeMap = oldGraph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
    GraphBuilder.NodeInfo info = (GraphBuilder.NodeInfo) nodeMap.get (node);
    PsiClass classToNavigateTo = info.getPsiClass ();
    if (info.getDependencyType () == DependencyType.SUBJECT)
    {
      navigateToSource (classToNavigateTo, classToNavigateTo.getTextOffset ());
    }
    else
    {
      GraphBuilder graphBuilder = new GraphBuilder ();
      Graph2D newGraph = graphBuilder.createGraph (classToNavigateTo);
      Node subjectNode = GraphUtils.findSubjectNode (oldGraph);
      GraphBuilder.NodeInfo selectedNodeInfo = (GraphBuilder.NodeInfo) nodeMap.get (node);
      GraphBuilder.NodeInfo subjectNodeInfo  = (GraphBuilder.NodeInfo) nodeMap.get (subjectNode);
      PsiClass       subjectClass   = subjectNodeInfo.getPsiClass ();
      PsiClass       selectedClass  = selectedNodeInfo.getPsiClass ();
      DependencyType dependencyType = selectedNodeInfo.getDependencyType ();
      navigationHistory.navigatedTo (subjectClass, selectedClass, dependencyType);
      navigateToClass (oldGraph, newGraph, node);
    }
  }

  /**
   * Naviagtes to given edge.
   * @param edge edge to navigate to
   * @param navigateToEdgeSource true, if source of edge is navigation target; false if target of edge is
   *        navigation target
   */
  public void navigate (@NotNull Edge edge, boolean navigateToEdgeSource)
  {
    Graph2D graph2D = graph2DView.getGraph2D ();
    DataProvider edgeMap = graph2D.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_EDGE_KEY);
    GraphBuilder.EdgeInfo info = (GraphBuilder.EdgeInfo) edgeMap.get (edge);
    if (navigateToEdgeSource)
    {
      Node sourceNode = edge.source ();
      DataProvider nodeMap = graph2D.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
      GraphBuilder.NodeInfo nodeInfo = (GraphBuilder.NodeInfo) nodeMap.get (sourceNode);
      PsiClass classToNavigateTo = nodeInfo.getPsiClass ();
      int offset = info.getOffset ();
      navigateToSource (classToNavigateTo, offset);
    }
    else
    {
      PsiClass classToNavigateTo = info.getPsiClass ();
      int offset = (info.getDependencyType () == DependencyType.EXTENDING ||
                    info.getDependencyType () == DependencyType.USING) ? info.getOffset () :
                   classToNavigateTo.getTextOffset ();
      navigateToSource (classToNavigateTo, offset);
    }
  }

  /**
   * Navigates from old graph to new graph after user selected given node.
   * @param oldGraph old graph
   * @param newGraph new graph
   * @param selectedNode selected node
   */
  private void navigateToClass (@NotNull Graph2D oldGraph, @NotNull Graph2D newGraph, @NotNull Node selectedNode)
  {
    Settings settings = Settings.getSettings ();
    if (settings.isAnimateNavigation ())
    {
      AnimationBuilder animBuilder = new AnimationBuilder (graph2DView, oldGraph, newGraph, selectedNode, settings);
      AnimationObject animation = animBuilder.createNavigationAnimation ();
      AnimationPlayer player = new AnimationPlayer ();
      player.addAnimationListener (new AnimationListenerAdapter (graph2DView));
      player.animate (animation);
    }
    else
    {
      graph2DView.setGraph2D (newGraph);
      graph2DView.fitContent ();
      graph2DView.updateView ();
      graph2DView.adjustScrollBarVisibility ();
    }
  }

  /**
   * Navigates to source of given class at specified offset.
   * @param classToNavigateTo navigate to source of this class
   * @param offset navigate to this offset within source
   */
  private void navigateToSource (@NotNull PsiElement classToNavigateTo, int offset)
  {
    PsiFile containingFile = classToNavigateTo.getContainingFile ();
    VirtualFile virtualFile = containingFile.getVirtualFile ();
    if (virtualFile != null)
    {
      FileEditorManager manager = FileEditorManager.getInstance (this.project);
      FileEditor[] fileEditors = manager.openFile (virtualFile, true);
      if (fileEditors.length > 0)
      {
        FileEditor fileEditor = fileEditors [0];
        if (fileEditor instanceof NavigatableFileEditor)
        {
          NavigatableFileEditor navigatableFileEditor = (NavigatableFileEditor) fileEditor;
          Navigatable descriptor = new OpenFileDescriptor (this.project, virtualFile, offset);
          navigatableFileEditor.navigateTo (descriptor);
        }
      }
    }
  }
}
