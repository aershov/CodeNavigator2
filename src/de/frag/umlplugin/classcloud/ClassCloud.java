package de.frag.umlplugin.classcloud;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.view.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.peer.PeerFactory;
import com.intellij.psi.PsiClass;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import de.frag.umlplugin.*;
import de.frag.umlplugin.classcloud.actions.ActionNames;
import de.frag.umlplugin.codenavigator.graph.DependencyType;
import de.frag.umlplugin.psi.DependencyAnalyzer;
import de.frag.umlplugin.psi.DependencyCollection;
import de.frag.umlplugin.psi.UsageType;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles creation and presentation of class clouds.
 */
public class ClassCloud
{
  private static final String CLASS_CLOUD             = "Class Cloud";
  private static final int    UNSELECTED_TRANSPARENCY =  50;
  private static final int    OPAQUE                  = 255;

  private final Project                     project;
  private final DelayedExecutor             executor = new DelayedExecutor (400);
  private final Map<String, Node>           classNamesToNodes = new HashMap<String, Node> ();
  private final ProjectDependenciesAnalyzer analyzer = new ProjectDependenciesAnalyzer ();
  private final Graph2DView                 graph2DView;
  private UMLEdgeFactory                    edgeFactory;
  private boolean                           goToSourceOnClick = true;
  private JPanel                            graphViewContainer;

  /**
   * Creates a new class cloud.
   * @param project current project
   */
  public ClassCloud (@NotNull Project project)
  {
    this.project = project;
    this.analyzer.clear ();
    this.classNamesToNodes.clear ();
    this.graph2DView = createGraphView ();
  }

  /**
   * Analyzes given class.
   * @param psiClass class to analyze
   */
  public void analyzeClass (@NotNull PsiClass psiClass)
  {
    analyzer.analyzeClass (psiClass);
  }

  /**
   * Creates and shows graph containg class cloud in class cloud tool window.
   */
  public void show ()
  {
    analyzer.analyzeDependencies ();
    final java.util.List<String> sortedClasses = analyzer.getClasses ();
    if (sortedClasses.isEmpty ())
    {
      Messages.showInfoMessage (project, "Selected scope does not contain any classes.", "Information");
      return;
    }
    final Map<String, ProjectDependenciesAnalyzer.DependencyInfo> infos = analyzer.getInfos ();
    CloudLayouter layouter = new CloudLayouter (sortedClasses, infos);
    Graph2D graph = layouter.createGraph ();

    // add mapping from class names to nodes
    for (Node node : graph.getNodeArray ())
    {
      Cell cell = ClassCloudData.findCell (node);
      if (cell != null)
      {
        classNamesToNodes.put (cell.getClassName (), node);
      }
    }
    graph2DView.setGraph2D (graph);
    edgeFactory = new UMLEdgeFactory (graph);
    ToolWindow cloudToolWindow = createToolWindow (project, graph2DView);
    cloudToolWindow.activate (new Runnable () {
      public void run ()
      {
        graph2DView.fitContent ();
        graph2DView.updateView ();
        graph2DView.getJComponent ().repaint ();
      }
    });
  }

  /**
   * Checks whether dependencies should be highlighted on node-clicks or the source should be shown.
   * @return true, if node-clicks navigate to source; false if node-clicks lead to highlighting dependent classes
   */
  public boolean isGoToSourceOnClick ()
  {
    return goToSourceOnClick;
  }

  /**
   * Sets whether dependencies should be highlighted on node-clicks or the source should be shown.
   * @param goToSourceOnClick true, if node-clicks navigate to source;
   *                          false, if node-clicks lead to highlighting dependent classes
   */
  public void setGoToSourceOnClick (boolean goToSourceOnClick)
  {
    this.goToSourceOnClick = goToSourceOnClick;
  }

  /**
   * Resets view. All classes will be shown in highlighted style so there are no more ghosted classes.
   */
  public void resetView ()
  {
    removeAllEdges ();
    for (String nodeClassName : classNamesToNodes.keySet ())
    {
      Node node = classNamesToNodes.get (nodeClassName);
      setTransparency (node, OPAQUE);
    }
    graph2DView.updateView ();
  }

  /**
   * Gets current graph view
   * @return class cloud graph view
   */
  public @NotNull Graph2DView getGraph2DView ()
  {
    return graph2DView;
  }

  /**
   * Closes and disposes this class cloud.
   */
  public void close ()
  {
    clear ();
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance (project);
    ToolWindow cloudToolWindow = toolWindowManager.getToolWindow (CLASS_CLOUD);
    if (cloudToolWindow != null)
    {
      cloudToolWindow.getContentManager ().removeAllContents (true);
      toolWindowManager.unregisterToolWindow (CLASS_CLOUD);
      cloudToolWindow.setAvailable (false, null);
    }
  }

  /**
   * Clears cached content.
   */
  public void clear ()
  {
    classNamesToNodes.clear ();
    analyzer.clear ();
    goToSourceOnClick = true;
  }
  
  /**
   * Highlights classes dependent to class associated to given node.
   * @param subjectNode subject node
   */
  public void highlightDependencies (@NotNull Node subjectNode)
  {
    removeAllEdges ();
    PsiClass psiClass = ClassCloudData.findPsiClass (project, subjectNode);
    if (psiClass != null)
    {
      DependencyAnalyzer analyzer = new DependencyAnalyzer (psiClass, Settings.getSettings ());
      DependencyCollection usingClasses     = analyzer.getUsingClasses ();
      DependencyCollection usedClasses      = analyzer.getUsedClasses ();
      DependencyCollection extendingClasses = analyzer.getExtendingClasses ();
      DependencyCollection extendedClasses  = analyzer.getExtendedClasses ();

      for (String nodeClassName : classNamesToNodes.keySet ())
      {
        Node node = classNamesToNodes.get (nodeClassName);
        setTransparency (node, UNSELECTED_TRANSPARENCY);
      }
      setTransparency (subjectNode, OPAQUE);
      highlightClasses (subjectNode, usingClasses,     DependencyType.USING);
      highlightClasses (subjectNode, usedClasses,      DependencyType.USED);
      highlightClasses (subjectNode, extendingClasses, DependencyType.EXTENDING);
      highlightClasses (subjectNode, extendedClasses,  DependencyType.EXTENDED);

      graph2DView.updateView ();
    }
  }

  //--------------------------------------------------------------------------------------
  //-------------------------------- internal methods ------------------------------------
  //--------------------------------------------------------------------------------------

  /**
   * Removes all edges from class cloud.
   */
  private void removeAllEdges ()
  {
    Graph2D graph = graph2DView.getGraph2D ();
    for (Edge edge : graph.getEdgeArray ())
    {
      graph.removeEdge (edge);
    }
  }

  /**
   * Highlights all classes contained in given dependency collection and adds corresponding edges.
   * @param subjectNode subject node
   * @param classes classes to highlight
   * @param dependencyType dependency type for classes to be highlighted
   */
  private void highlightClasses (@NotNull Node subjectNode, @NotNull DependencyCollection classes,
                                 @NotNull DependencyType dependencyType)
  {
    for (PsiClass psiClass : classes)
    {
      Node node = findNode (psiClass);
      if (node != null && node != subjectNode)
      {
        setTransparency (node, OPAQUE);
        switch (dependencyType)
        {
          case USING:
            edgeFactory.createEdge (node, subjectNode, UsageType.REFERENCE);
            break;
          case USED:
            edgeFactory.createEdge (subjectNode, node, UsageType.REFERENCE);
            break;
          case EXTENDING:
            edgeFactory.createEdge (node, subjectNode, UsageType.EXTENDS);
            break;
          case EXTENDED:
            edgeFactory.createEdge (subjectNode, node, UsageType.EXTENDS);
            break;
          default:
            throw new IllegalArgumentException ("unknown dependency type: " + dependencyType);
        }
      }
    }
  }

  /**
   * Creates new tool window.
   * @param project current project
   * @param graph2DView graph view panel to display in tool window
   * @return created tool window
   */
  private @NotNull ToolWindow createToolWindow (@NotNull final Project project, @NotNull final Graph2DView graph2DView)
  {
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance (project);
    ToolWindow cloudToolWindow = toolWindowManager.getToolWindow (CLASS_CLOUD);
    if (cloudToolWindow == null)
    {
      prepareActions ();
      cloudToolWindow = toolWindowManager.registerToolWindow (CLASS_CLOUD, true, ToolWindowAnchor.BOTTOM);
      cloudToolWindow.setToHideOnEmptyContent (true);
      cloudToolWindow.setType (ToolWindowType.SLIDING, null);

      graphViewContainer = new JPanel (new BorderLayout ());
      JPanel cloudContentPanel = new JPanel (new BorderLayout ());
      cloudContentPanel.add (createToolBarPanel (), BorderLayout.NORTH);
      cloudContentPanel.add (graphViewContainer,    BorderLayout.CENTER);

      ContentFactory contentFactory = PeerFactory.getInstance ().getContentFactory ();
      Content cloudContent = contentFactory.createContent (cloudContentPanel, "", false);
      cloudToolWindow.getContentManager ().addContent (cloudContent);
    }
    graphViewContainer.removeAll ();
    graphViewContainer.add (graph2DView.getJComponent ());
    return cloudToolWindow;
  }

  /**
   * Creates tool bar panel containing tool bar and further components.
   * @return created panel
   */
  private @NotNull JPanel createToolBarPanel ()
  {
    JPanel toolBarPanel = new JPanel (new BorderLayout ());
    toolBarPanel.add (createSearchField (), BorderLayout.WEST);
    toolBarPanel.add (createToolbar (), BorderLayout.CENTER);
    return toolBarPanel;
  }

  /**
   * Creates and configures a graph view that display given class cloud graph
   * @return created graph view component
   */
  private @NotNull Graph2DView createGraphView ()
  {
    GraphManager graphManager = GraphManager.getGraphManager ();
    final Graph2DView graph2DView = graphManager.createGraph2DView ();
    graph2DView.setAntialiasedPainting (true);
    graph2DView.addViewMode (new ViewMode()
    {
      public void mouseMoved (double x, double y)
      {
        HitInfo hitInfo = getHitInfo (x, y);
        if (hitInfo.hasHitNodes ())
        {
          graph2DView.setViewCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
          Node hitNode = hitInfo.getHitNode ();
          Cell cell = ClassCloudData.findCell (hitNode);
          if (cell != null)
          {
            String toolTipText = "<html><div>" + cell.getClassName () + "</div>" +
                                 "<div>" + cell.getDependencyCount () + " dependencies</div></html>";
            graph2DView.setToolTipText (toolTipText);
          }
        }
        else
        {
          graph2DView.setViewCursor (Cursor.getPredefinedCursor (Cursor.DEFAULT_CURSOR));
          graph2DView.setToolTipText (null);
        }
      }

      public void mousePressedLeft (double x, double y)
      {
        HitInfo hitInfo = getHitInfo (x, y);
        if (hitInfo.hasHitNodes ())
        {
          Node hitNode = hitInfo.getHitNode ();
          PsiClass psiClass = ClassCloudData.findPsiClass (project, hitNode);
          if (goToSourceOnClick)
          {
            if (psiClass != null)
            {
              Navigator.navigateToSource (project, psiClass);
            }
          }
          else
          {
            highlightDependencies (hitNode);
          }
        }
      }
    });
    graph2DView.addViewMode (new PopupMode()
    {
      {
        this.setSelectSubject (false);
      }
      public JPopupMenu getNodePopup (Node node)
      {
        ProjectUtils.set (project, node, Node.class);
        ActionManager actionManager = ActionManager.getInstance ();
        ActionGroup group = (ActionGroup) actionManager.getAction (ActionNames.NODE_POPUP_MENU_GROUP);
        final ActionPopupMenu popupMenu = actionManager.createActionPopupMenu (ActionPlaces.UNKNOWN, group);
        popupMenu.getComponent ().add (new JMenuItem ("workaround"));  // workaround to make items visible
        return popupMenu.getComponent ();
      }
    });
    DefaultGraph2DRenderer renderer = (DefaultGraph2DRenderer) graph2DView.getGraph2DRenderer ();
    renderer.setDrawEdgesFirst (true);

    graph2DView.addViewMode (new MoveViewPortMode ());
    Graph2DViewMouseWheelZoomListener mwzl = graphManager.createGraph2DViewMouseWheelZoomListener ();
    graph2DView.getCanvasComponent ().addMouseWheelListener (mwzl);
    graph2DView.fitContent ();
    return graph2DView;
  }

  /**
   * Creates a search field to search for classes in class cloud.
   * @return created search field
   */
  private @NotNull JComponent createSearchField ()
  {
    final SearchTextField searchTextField = new SearchTextField ();
    searchTextField.getTextEditor ().setColumns (20);
    searchTextField.addDocumentListener (new DocumentAdapter () {
      protected void textChanged (DocumentEvent e)
      {
        searchAndHighlightClasses (searchTextField);
      }
    });
    return searchTextField;
  }


  /**
   * Searches and highlights all classes that contain the class name fragment contained in given search field.
   * @param searchTextField search field that contains fragment of class name to search for
   */
  private void searchAndHighlightClasses (@NotNull final SearchTextField searchTextField)
  {
    Runnable searchTask = new Runnable()
    {
      public void run ()
      {
        String className = searchTextField.getText ();
        if (className != null && className.length () > 0)
        {
          searchTextField.addCurrentTextToHistory ();
          removeAllEdges ();
          for (String nodeClassName : classNamesToNodes.keySet ())
          {
            Node node = classNamesToNodes.get (nodeClassName);
            int alpha = (nodeClassName.indexOf (className) >= 0) ? OPAQUE : UNSELECTED_TRANSPARENCY;
            setTransparency (node, alpha);
          }
        }
        else
        {
          for (String nodeClassName : classNamesToNodes.keySet ())
          {
            Node node = classNamesToNodes.get (nodeClassName);
            setTransparency (node, OPAQUE);
          }
        }
        graph2DView.updateView ();
      }
    };
    executor.execute (searchTask);
  }

  /**
   * Finds node for given class.
   * @param psiClass class to find node for
   * @return found node or null, if node could not be found
   */
  public @Nullable Node findNode (@NotNull PsiClass psiClass)
  {
    String qualifiedName = psiClass.getQualifiedName ();
    return qualifiedName != null ? classNamesToNodes.get (qualifiedName) : null;
  }

  /**
   * Sets transparency of given node to specified alpha value.
   * @param node node
   * @param alpha alpha
   */
  private void setTransparency (@NotNull Node node, int alpha)
  {
    Graph2D graph = graph2DView.getGraph2D ();
    NodeRealizer realizer = graph.getRealizer (node);
    if (realizer != null)
    {
      NodeLabel label = realizer.getLabel ();
      Color fillColor = realizer.getFillColor ();
      Color lineColor = realizer.getLineColor ();
      Color textColor = label.getTextColor ();
      realizer.setFillColor (new Color (fillColor.getRed (), fillColor.getGreen (), fillColor.getBlue (), alpha));
      realizer.setLineColor (new Color (lineColor.getRed (), lineColor.getGreen (), lineColor.getBlue (), alpha));
      label.setTextColor    (new Color (textColor.getRed (), textColor.getGreen (), textColor.getBlue (), alpha));
    }
  }

  /**
   * Creates a tool bar with several toolbar buttons.
   * @return created tool bar
   */
  private @NotNull JComponent createToolbar ()
  {
    ActionManager actionManager = ActionManager.getInstance ();
    ActionGroup group = (ActionGroup) actionManager.getAction (ActionNames.TOOLBAR_GROUP);
    ActionToolbar toolbar = actionManager.createActionToolbar (ActionPlaces.UNKNOWN, group, true);
    return toolbar.getComponent ();
  }

  /**
   * Prepares actions by injecting additional information.
   */
  private void prepareActions ()
  {
    Graph2DViewProvider viewProvider               = new ClassCloudGraph2DViewProvider ();
    PsiClassProvider    classCloudPsiClassProvider = new ClassCloudPsiClassProvider ();
    ActionManager actionManager = ActionManager.getInstance ();

    PsiClassConsumer showInGraphicalNavigatorAction =
            (PsiClassConsumer) actionManager.getAction (ActionNames.SHOW_IN_GRAPHICAL_NAVIGATOR);
    showInGraphicalNavigatorAction.setPsiClassProvider (classCloudPsiClassProvider);

    Graph2DViewConsumer zoomInAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.ZOOM_IN);
    zoomInAction.setGraph2DViewProvider (viewProvider);

    Graph2DViewConsumer zoomOutAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.ZOOM_OUT);
    zoomOutAction.setGraph2DViewProvider (viewProvider);

    Graph2DViewConsumer fitContentAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.FIT_CONTENT);
    fitContentAction.setGraph2DViewProvider (viewProvider);

    Graph2DViewConsumer exportAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.EXPORT_TO_FILE);
    exportAction.setGraph2DViewProvider (viewProvider);

    Graph2DViewConsumer printGraphAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.PRINT_GRAPH);
    printGraphAction.setGraph2DViewProvider (viewProvider);

    Graph2DViewConsumer printPreviewAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.PRINT_PREVIEW);
    printPreviewAction.setGraph2DViewProvider (viewProvider);
  }
}
