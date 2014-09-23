package de.frag.umlplugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.actions.printing.PrintGraphAction;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import de.frag.umlplugin.Graph2DViewConsumer;
import de.frag.umlplugin.Graph2DViewProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Prints graph.
 */
public class DirectPrintGraphAction extends PrintGraphAction implements Graph2DViewConsumer
{
  private Graph2DViewProvider graph2DViewProvider;

  /**
   * Injects Graph2DViewProvider.
   * @param graph2DViewProvider Graph2DViewProvider to inject
   */
  public void setGraph2DViewProvider (@NotNull Graph2DViewProvider graph2DViewProvider)
  {
    this.graph2DViewProvider = graph2DViewProvider;
  }

  public void update (AnActionEvent e, Graph2D graph2D)
  {
    super.update (e, graph2D);
    e.getPresentation ().setEnabled (true);
  }

  protected @Nullable Graph2D getGraph (AnActionEvent e)
  {
    Graph2DView graph2DView = graph2DViewProvider.getGraph2DView (e.getDataContext ());
    return (graph2DView != null) ? graph2DView.getGraph2D () : null;
  }
}