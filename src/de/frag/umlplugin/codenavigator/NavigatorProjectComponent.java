package de.frag.umlplugin.codenavigator;

//import com.intellij.codeInsight.TestUtil;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.graph.base.DataProvider;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.UIUtil;
import de.frag.umlplugin.*;
import de.frag.umlplugin.codenavigator.actions.ActionNames;
import de.frag.umlplugin.codenavigator.graph.DataProviderKeys;
import de.frag.umlplugin.codenavigator.graph.Graph2DViewBuilder;
import de.frag.umlplugin.codenavigator.graph.GraphBuilder;
import de.frag.umlplugin.codenavigator.graph.GraphUtils;
import de.frag.umlplugin.history.NavigationHistory;
import de.frag.umlplugin.psi.ClassFinder;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

//import com.intellij.peer.PeerFactory;

/**
 * Tool window project component for graphical navigation between dependent classes.
 */
public class NavigatorProjectComponent implements ProjectComponent
{

    public static final String GRAPHICAL_NAVIGATOR_TOOL_WINDOW_ID   = "Graphical Navigator";
    private static final String CONTENT_DISPLAY_NAME                = "";
    private static final String GRAPHICAL_NAVIGATOR_COMPONENT       = "GraphicalNavigatorComponent";

    private final Project project;
    private final Graph2DView graph2DView;


    /**
     * Creates the navigator component.
     *
     * @param project project to create component for
     */
    public NavigatorProjectComponent(@NotNull Project project) {
        this.project = project;
        this.graph2DView = Graph2DViewBuilder.createGraph2DView(project);
        NavigationHistory navigationHistory = new NavigationHistory(Settings.getSettings().getMaxHistorySize());
        Navigator navigator = new Navigator(project, graph2DView, navigationHistory);
        ProjectUtils.set(project, this);
        ProjectUtils.set(project, navigator);
        ProjectUtils.set(project, navigationHistory);
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------- Getters --------------------------------------------------
    //--------------------------------------------------------------------------------------------------------


    /**
     * Gets graph view.
     *
     * @return graph view
     */
    public Graph2DView getGraph2DView() {
        return graph2DView;
    }

    //--------------------------------------------------------------------------------------------------------
    //------------------------------ Methods for implementing ProjectComponent -------------------------------
    //--------------------------------------------------------------------------------------------------------


    public void initComponent() {
    }


    public void disposeComponent() {
    }


    public
    @NotNull
    String getComponentName() {
        return GRAPHICAL_NAVIGATOR_COMPONENT;
    }


    public void projectOpened() {
        prepareActions();

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtil.getPanelBackground());
        contentPanel.add(createToolbar(), BorderLayout.NORTH);
        contentPanel.add(graph2DView.getJComponent(), BorderLayout.CENTER);

        ToolWindow toolWindow = toolWindowManager.registerToolWindow(GRAPHICAL_NAVIGATOR_TOOL_WINDOW_ID, false, ToolWindowAnchor.RIGHT);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(contentPanel, CONTENT_DISPLAY_NAME, false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.setType(ToolWindowType.SLIDING, null);

        // add special handling: if  tool window is opened before any class was subject of navigation
        // show class in current editor
        toolWindow.getComponent().addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                Graph2D graph = graph2DView.getGraph2D();
                if (graph.isEmpty()) {
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    Editor editor = fileEditorManager.getSelectedTextEditor();
                    if (editor != null) {
                        Document document = editor.getDocument();
                        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
                        PsiFile psiFile = psiDocumentManager.getPsiFile(document);
                        if (psiFile != null) {
                            PsiClass psiClass = PsiTreeUtil.getChildOfType(psiFile, PsiClass.class);
                            if (psiClass != null) {
                                GraphBuilder graphBuilder = new GraphBuilder();
                                Graph2D graph2D = graphBuilder.createGraph(psiClass);
                                graph2DView.setGraph2D(graph2D);
                                NavigationHistory history = ProjectUtils.get(project, NavigationHistory.class);
                                if (history != null) {
                                    history.clear();
                                }
                                graph2DView.adjustScrollBarVisibility();
                                graph2DView.fitContent();
                                graph2DView.updateView();
                            }
                        }
                    }
                }
            }
        });
    }


    public void projectClosed() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.unregisterToolWindow(GRAPHICAL_NAVIGATOR_TOOL_WINDOW_ID);
    }


    /**
     * Prepares actions by injecting additional information.
     */
    private void prepareActions() {
        PsiClassProvider classUnderCursor = new ClassUnderCursorProvider();
        PsiClassProvider navigatorClassProvider = new GraphicalNavigatorPsiClassProvider();
        Graph2DViewProvider viewProvider = new GraphicalNavigatorGraph2DViewProvider();
        ActionManager actionManager = ActionManager.getInstance();

        PsiClassConsumer showInGraphicalNavigatorAction = (PsiClassConsumer) actionManager.getAction(ActionNames.SHOW_IN_GRAPHICAL_NAVIGATOR);
        showInGraphicalNavigatorAction.setPsiClassProvider(classUnderCursor);

        PsiClassConsumer addToNewDiagramAction = (PsiClassConsumer) actionManager.getAction(ActionNames.ADD_TO_NEW_DIAGRAM);
        addToNewDiagramAction.setPsiClassProvider(navigatorClassProvider);

        PsiClassConsumer addToExistingDiagramAction = (PsiClassConsumer) actionManager.getAction(ActionNames.ADD_TO_EXISTING_DIAGRAM);
        addToExistingDiagramAction.setPsiClassProvider(navigatorClassProvider);

        PsiClassConsumer popupMenuGroup = (PsiClassConsumer) actionManager.getAction(ActionNames.POPUP_MENU_GROUP);
        popupMenuGroup.setPsiClassProvider(navigatorClassProvider);

        Graph2DViewConsumer zoomInAction = (Graph2DViewConsumer) actionManager.getAction(ActionNames.ZOOM_IN);
        zoomInAction.setGraph2DViewProvider(viewProvider);

        Graph2DViewConsumer zoomOutAction = (Graph2DViewConsumer) actionManager.getAction(ActionNames.ZOOM_OUT);
        zoomOutAction.setGraph2DViewProvider(viewProvider);

        Graph2DViewConsumer fitContentAction = (Graph2DViewConsumer) actionManager.getAction(ActionNames.FIT_CONTENT);
        fitContentAction.setGraph2DViewProvider(viewProvider);

        Graph2DViewConsumer exportAction = (Graph2DViewConsumer) actionManager.getAction(ActionNames.EXPORT_TO_FILE);
        exportAction.setGraph2DViewProvider(viewProvider);

        Graph2DViewConsumer printGraphAction = (Graph2DViewConsumer) actionManager.getAction(ActionNames.PRINT_GRAPH);
        printGraphAction.setGraph2DViewProvider(viewProvider);

        Graph2DViewConsumer printPreviewAction = (Graph2DViewConsumer) actionManager.getAction(ActionNames.PRINT_PREVIEW);
        printPreviewAction.setGraph2DViewProvider(viewProvider);
    }


    /**
     * Creates a tool bar with several toolbar buttons.
     *
     * @return created tool bar
     */
    private
    @NotNull
    JComponent createToolbar() {
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup group = (ActionGroup) actionManager.getAction(ActionNames.TOOLBAR_GROUP);
        ActionToolbar toolbar = actionManager.createActionToolbar(ActionPlaces.UNKNOWN, group, true);
        return toolbar.getComponent();
    }


    /**
     * Apply given settings to existing graphs.
     *
     * @param reducedClassVisibility  true, if settings changes reduced class visibility
     * @param modifiedClassVisibility true, if settings changes modified class visibility
     * @param settings                settings to apply
     */
    public void applySettings(boolean reducedClassVisibility, boolean modifiedClassVisibility, Settings settings) {
        if (reducedClassVisibility) {
            NavigationHistory history = ProjectUtils.get(project, NavigationHistory.class);
            if (history != null) {
                history.clear();
            }
        }
        if (modifiedClassVisibility) {
            Graph2D graph = graph2DView.getGraph2D();
            Node subjectNode = GraphUtils.findSubjectNode(graph);
            if (subjectNode != null) {
                DataProvider nodeMap = graph.getDataProvider(DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
                GraphBuilder.NodeInfo nodeInfo = (GraphBuilder.NodeInfo) nodeMap.get(subjectNode);
                PsiClass subjectClass = nodeInfo.getPsiClass();
                if ((!ClassFinder.isProjectClass(subjectClass) && !settings.isIncludeNonProjectClasses())
//                    || (TestUtil.isTestClass(subjectClass) && !settings.isIncludeTestClasses())
                    ) {
                    graph.clear();
                } else {
                    GraphBuilder graphBuilder = new GraphBuilder();
                    Graph2D graph2D = graphBuilder.createGraph(subjectClass);
                    graph2DView.setGraph2D(graph2D);
                }
            }
            graph2DView.fitContent();
            graph2DView.updateView();
        }
    }
}
