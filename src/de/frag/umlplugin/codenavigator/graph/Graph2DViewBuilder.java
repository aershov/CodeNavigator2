package de.frag.umlplugin.codenavigator.graph;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.view.*;
import com.intellij.openapi.project.Project;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.codenavigator.Navigator;
import de.frag.umlplugin.codenavigator.actions.ActionNames;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Creates and initializes a {@link com.intellij.openapi.graph.view.Graph2DView} object.
 */
public class Graph2DViewBuilder
{
  public static @NotNull Graph2DView createGraph2DView (@NotNull final Project project)
  {
    final GraphManager graphManager = GraphManager.getGraphManager ();
    final Graph2DView graph2DView = graphManager.createGraph2DView ();
    graph2DView.setAntialiasedPainting (true);
    graph2DView.setViewPoint (-50, -50);

    graph2DView.addViewMode (new ViewMode() {
      public void mousePressedLeft (double x, double y)
      {
        //HitInfo hitInfo = getHitInfo (x, y);
        HitInfo hitInfo = graphManager.createHitInfo (getGraph2D (), x, y, true);
        if (hitInfo.hasHitNodes ())
        {
          Node hitNode = hitInfo.getHitNode ();
          ProjectUtils.set (project, hitNode, Node.class);
          final Navigator navigator = ProjectUtils.get (project, Navigator.class);
          if (navigator != null)
          {
            navigator.navigate (hitNode);
          }
          graph2DView.getJComponent ().repaint ();
        }
        else if (hitInfo.hasHitEdges ())
        {
          Edge hitEdge = hitInfo.getHitEdge ();
          ProjectUtils.set (project, hitEdge, Edge.class);
          final Navigator navigator = ProjectUtils.get (project, Navigator.class);
          if (navigator != null)
          {
            navigator.navigate (hitEdge, false);
          }
        }
      }
      public void mouseShiftPressedLeft (double x, double y)
      {
        //HitInfo hitInfo = getHitInfo (x, y);
        HitInfo hitInfo = graphManager.createHitInfo (getGraph2D (), x, y, true);
        if (hitInfo.hasHitEdges ())
        {
          Edge hitEdge = hitInfo.getHitEdge ();
          ProjectUtils.set (project, hitEdge, Edge.class);
          final Navigator navigator = ProjectUtils.get (project, Navigator.class);
          if (navigator != null)
          {
            navigator.navigate (hitEdge, true);
          }
        }
      }
      public void mouseMoved (double x, double y)
      {
        //HitInfo hitInfo = getHitInfo (x, y);
        HitInfo hitInfo = graphManager.createHitInfo (getGraph2D (), x, y, true);
        if (hitInfo.hasHitNodes ())
        {
          graph2DView.setViewCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
          GraphBuilder.NodeInfo nodeInfo = GraphUtils.getNodeInfo (hitInfo.getHitNode ());
          if (nodeInfo != null)
          {
            graph2DView.setToolTipText (nodeInfo.getPsiClass ().getQualifiedName ());
          }
        }
        else if (hitInfo.hasHitEdges ())
        {
          graph2DView.setViewCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
          graph2DView.setToolTipText (null);
        }
        else
        {
          graph2DView.setViewCursor (Cursor.getPredefinedCursor (Cursor.DEFAULT_CURSOR));
          graph2DView.setToolTipText (null);
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
        ActionGroup group = (ActionGroup) actionManager.getAction (ActionNames.ROOT_POPUP_MENU_GROUP);
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
