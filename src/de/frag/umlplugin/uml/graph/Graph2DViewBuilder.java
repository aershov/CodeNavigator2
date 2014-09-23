package de.frag.umlplugin.uml.graph;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.view.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.Navigator;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.actions.ActionNames;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Creates and initializes a {@link com.intellij.openapi.graph.view.Graph2DView} object.
 */
public class Graph2DViewBuilder
{
  private Graph2DViewBuilder () {}

  /**
   * Creates new graph 2D view for given diagram.
   * @param project project associated to the created graph view
   * @return created view
   */
  public static @NotNull Graph2DView createGraph2DView (@NotNull final Project project)
  {
    GraphManager graphManager = GraphManager.getGraphManager ();
    final Graph2DView graph2DView = graphManager.createGraph2DView ();
    graph2DView.setAntialiasedPainting (true);
    graph2DView.setViewPoint (-50, -50);

    graph2DView.addViewMode (new ViewMode()
    {
      public void mouseMoved (double x, double y)
      {
        HitInfo hitInfo = getHitInfo (x, y);
        if (hitInfo.hasHitNodes ())
        {
          graph2DView.setViewCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
          PsiClass psiClass = UMLDiagram.getPsiClass (hitInfo.getHitNode ());
          if (psiClass != null)
          {
            graph2DView.setToolTipText (psiClass.getQualifiedName ());
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
          ProjectUtils.set (project, hitNode, Node.class);
          PsiClass psiClass = UMLDiagram.getPsiClass (hitNode);
          if (psiClass != null)
          {
            Navigator.navigateToSource (project, psiClass);
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

      public JPopupMenu getEdgePopup (Edge edge)
      {
        ProjectUtils.set (project, edge, Edge.class);
        ActionManager actionManager = ActionManager.getInstance ();
        ActionGroup group = (ActionGroup) actionManager.getAction (ActionNames.EDGE_POPUP_MENU_GROUP);
        final ActionPopupMenu popupMenu = actionManager.createActionPopupMenu (ActionPlaces.UNKNOWN, group);
        popupMenu.getComponent ().add (new JMenuItem ("workaround"));  // workaround to make items visible
        return popupMenu.getComponent ();
      }
    });

    graph2DView.addViewMode (new MoveViewPortMode ());
    Graph2DViewMouseWheelZoomListener mwzl = graphManager.createGraph2DViewMouseWheelZoomListener ();
    graph2DView.getCanvasComponent ().addMouseWheelListener (mwzl);
    return graph2DView;
  }
}
