package de.frag.umlplugin.uml;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.peer.PeerFactory;
import com.intellij.refactoring.listeners.RefactoringListenerManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.UIUtil;
import de.frag.umlplugin.*;
import de.frag.umlplugin.uml.actions.ActionNames;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Project component for UML diagrams.
 */
@State (name     = UMLDiagramComponent.UML_DIAGRAM_COMPONENT,
        storages = {@Storage (id     = UMLDiagramComponent.UML_DIAGRAM_COMPONENT,
                              file   = "$PROJECT_FILE$")})
public class UMLDiagramComponent implements ProjectComponent, PersistentStateComponent<Element>
{
  public  static final String UML_DIAGRAM_COMPONENT = "UMLDiagramComponent";
  public  static final String TOOL_WINDOW_ID        = "UML Diagrams";
  private static final String CONTENT_DISPLAY_NAME  = "";

  private final Project project;

  /**
   * Creates a new UML diagram component for given project.
   * @param project project to create component for
   */
  public UMLDiagramComponent (@NotNull final Project project)
  {
    this.project   = project;
    UMLDiagramsPanel umlDiagrams = new UMLDiagramsPanel ();
    ProjectUtils.set (project, this);
    ProjectUtils.set (project, umlDiagrams);
  }

  public void initComponent ()
  {
  }

  public void disposeComponent ()
  {
  }

  public @NotNull String getComponentName ()
  {
    return UML_DIAGRAM_COMPONENT;
  }

  public void projectOpened ()
  {
    prepareActions ();

    final UMLDiagramsPanel umlDiagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance (project);
    JPanel contentPanel = new JPanel (new BorderLayout ());
    contentPanel.setBackground (UIUtil.getPanelBackground ());
    Box toolBarPanel = Box.createHorizontalBox ();
    if (umlDiagrams != null)
    {
      toolBarPanel.add (Box.createHorizontalStrut (5));
      toolBarPanel.add (new JLabel ("Diagram: "));
      toolBarPanel.add (umlDiagrams.getDiagramComboBox ());
      toolBarPanel.add (createToolbar ());
    }
    contentPanel.add (toolBarPanel, BorderLayout.NORTH);
    contentPanel.add (umlDiagrams,  BorderLayout.CENTER);

    ToolWindow toolWindow = toolWindowManager.registerToolWindow (TOOL_WINDOW_ID, false, ToolWindowAnchor.RIGHT);
    ContentFactory contentFactory = PeerFactory.getInstance ().getContentFactory ();
    Content content = contentFactory.createContent (contentPanel, CONTENT_DISPLAY_NAME, false);
    toolWindow.getContentManager ().addContent (content);
    toolWindow.setType (ToolWindowType.SLIDING, null);

    RefactoringListenerManager refactoringListenerManager = RefactoringListenerManager.getInstance (project);
    refactoringListenerManager.addListenerProvider (new UMLRefactoringListenerProvider (project));

    StartupManager startupManager = StartupManager.getInstance (project);
    startupManager.runWhenProjectIsInitialized (new Runnable() {
      public void run ()
      {
        for (UMLDiagram diagram : umlDiagrams)
        {
          diagram.refreshDiagram ();
          diagram.doLayout (false);
        }
      }
    });
  }

  public void projectClosed ()
  {
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance (project);
    toolWindowManager.unregisterToolWindow (TOOL_WINDOW_ID);
  }

  /**
   * Prepares actions by injecting additional information.
   */
  private void prepareActions ()
  {
    PsiClassProvider    classUnderCursor    = new ClassUnderCursorProvider ();
    PsiClassProvider    umlPsiClassProvider = new UMLDiagramPsiClassProvider ();
    Graph2DViewProvider viewProvider        = new UMLDiagramGraph2DViewProvider ();
    ActionManager actionManager = ActionManager.getInstance ();

    PsiClassConsumer addToNewDiagramAction =
            (PsiClassConsumer) actionManager.getAction (ActionNames.ADD_TO_NEW_DIAGRAM);
    addToNewDiagramAction.setPsiClassProvider (classUnderCursor);

    PsiClassConsumer umlMenuGroup =
            (PsiClassConsumer) actionManager.getAction (ActionNames.UML_ACTION_GROUP);
    umlMenuGroup.setPsiClassProvider (classUnderCursor);

    PsiClassConsumer showInGraphicalNavigatorAction =
            (PsiClassConsumer) actionManager.getAction (ActionNames.SHOW_DIAGRAM_CLASS_IN_NAVIGATOR);
    showInGraphicalNavigatorAction.setPsiClassProvider (umlPsiClassProvider);

    Graph2DViewConsumer zoomInAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.ZOOM_IN);
    zoomInAction.setGraph2DViewProvider (viewProvider);

    Graph2DViewConsumer zoomOutAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.ZOOM_OUT);
    zoomOutAction.setGraph2DViewProvider (viewProvider);

    Graph2DViewConsumer fitContentAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.FIT_CONTENT);
    fitContentAction.setGraph2DViewProvider (viewProvider);

    Graph2DViewConsumer printGraphAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.PRINT_GRAPH);
    printGraphAction.setGraph2DViewProvider (viewProvider);

    Graph2DViewConsumer printPreviewAction = (Graph2DViewConsumer) actionManager.getAction (ActionNames.PRINT_PREVIEW);
    printPreviewAction.setGraph2DViewProvider (viewProvider);
  }

  /**
   * Creates a tool bar with several toolbar buttons.
   * @return created tool bar
   */
  private @NotNull JComponent createToolbar ()
  {
    ActionManager actionManager = ActionManager.getInstance ();
    ActionGroup group = (ActionGroup) actionManager.getAction (ActionNames.DIAGRAM_TOOLBAR_GROUP);
    ActionToolbar toolbar = actionManager.createActionToolbar (ActionPlaces.UNKNOWN, group, true);
    return toolbar.getComponent ();
  }

  public @Nullable Element getState ()
  {
    UMLDiagramsPanel panel = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (panel != null)
    {
      Element diagramsElement = new Element ("diagrams");
      panel.writeExternal (diagramsElement);
      return diagramsElement;
    }
    return null;
  }

  public void loadState (@NotNull Element diagramsElement)
  {
    UMLDiagramsPanel panel = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (panel != null)
    {
      panel.readExternal (project, diagramsElement);
    }
  }
}

