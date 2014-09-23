package de.frag.umlplugin.anim;

import com.intellij.openapi.graph.base.DataProvider;
import com.intellij.openapi.graph.base.Edge;
import com.intellij.openapi.graph.base.Node;
import com.intellij.openapi.graph.view.EdgeRealizer;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.graph.view.NodeRealizer;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.codenavigator.graph.DataProviderKeys;
import de.frag.umlplugin.codenavigator.graph.DependencyType;
import de.frag.umlplugin.codenavigator.graph.GraphBuilder;
import de.frag.umlplugin.codenavigator.graph.GraphUtils;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Creates animation from one subject class to another class selected by the user.
 */
public class AnimationBuilder
{
  private final Graph2DView graph2DView;
  private final Graph2D     oldGraph;
  private final Graph2D     newGraph;
  private final Node        selectedNodeInOldGraph;
  private final Settings    settings;

  /**
   * Creates an animation builder that can create animations from one graph to another.
   * @param graph2DView view that contains old graph
   * @param oldGraph old graph
   * @param newGraph new graph
   * @param selectedNodeInOldGraph selected node in old graph
   * @param settings settings
   */
  public AnimationBuilder (@NotNull Graph2DView graph2DView, @NotNull Graph2D oldGraph, @NotNull Graph2D newGraph,
                           @NotNull Node selectedNodeInOldGraph, @NotNull Settings settings)
  {
    this.graph2DView            = graph2DView;
    this.oldGraph               = oldGraph;
    this.newGraph               = newGraph;
    this.selectedNodeInOldGraph = selectedNodeInOldGraph;
    this.settings               = settings;
  }

  /**
   * Creates the desired animation.
   * @return create animation
   */
  public @NotNull AnimationObject createNavigationAnimation ()
  {
    AnimationFactory factory = new AnimationFactory ();

    Rectangle rectangle = newGraph.getBoundingBox ();
    CompositeAnimationObject sequence = CompositeAnimationFactory.createSequence ();
    sequence.addAnimation (createFadeOut ());

    CompositeAnimationObject concurrency = CompositeAnimationFactory.createConcurrency ();
    concurrency.addAnimation (createMorph ());
    concurrency.addAnimation (factory.fitRectangle (graph2DView, rectangle, settings.getAnimationDuration () / 3));

    sequence.addAnimation (concurrency);
    sequence.addAnimation (factory.exchangeGraph (graph2DView, newGraph));
    sequence.addAnimation (createFadeIn ());

    return sequence;
  }

  /**
   * Creates an animation object that fades out all nodes and edges but the seubject node and the selected node.
   * @return created animation object
   */
  private @NotNull AnimationObject createFadeOut ()
  {
    CompositeAnimationObject concurrency = CompositeAnimationFactory.createConcurrency ();
    AnimationFactory factory = new AnimationFactory ();
    DataProvider nodeMap = oldGraph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
    for (Node node : oldGraph.getNodeArray ())
    {
      GraphBuilder.NodeInfo nodeInfo = (GraphBuilder.NodeInfo) nodeMap.get (node);
      if (nodeInfo.getDependencyType () != DependencyType.SUBJECT && node != selectedNodeInOldGraph)
      {
        NodeRealizer nodeRealizer = oldGraph.getRealizer (node);
        AnimationObject animation = factory.fadeOut (nodeRealizer, settings.getAnimationDuration () / 3);
        concurrency.addAnimation (animation);
      }
    }
    for (final Edge edge : oldGraph.getEdgeArray ())
    {
      if (edge.source () != selectedNodeInOldGraph && edge.target () != selectedNodeInOldGraph)
      {
        EdgeRealizer edgeRealizer = oldGraph.getRealizer (edge);
        AnimationObject animation = factory.fadeOut (edgeRealizer, settings.getAnimationDuration () / 3);
        concurrency.addAnimation (animation);
      }
    }
    return concurrency;
  }

  /**
   * Creates a morph from old graph to new graph.
   * @return created animation object
   */
  private @NotNull AnimationObject createMorph ()
  {
    DataProvider oldNodeMap = oldGraph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
    Node selectedNodeInNewGraph = GraphUtils.findSubjectNode (newGraph);
    Node subjectNodeInOldGraph  = GraphUtils.findSubjectNode (oldGraph);
    GraphBuilder.NodeInfo subjectNodeInOldGraphInfo  = (GraphBuilder.NodeInfo) oldNodeMap.get (subjectNodeInOldGraph);
    GraphBuilder.NodeInfo selectedNodeInOldGraphInfo = (GraphBuilder.NodeInfo) oldNodeMap.get (selectedNodeInOldGraph);
    PsiClass subjectPsiClass    = subjectNodeInOldGraphInfo.getPsiClass ();
    DependencyType selectedType = selectedNodeInOldGraphInfo.getDependencyType ();
    Node subjectNodeInNewGraph  = GraphUtils.findNode (newGraph, subjectPsiClass, selectedType.opposite ());
    Edge edgeInOldGraph = getEdge (selectedNodeInOldGraph);
    Edge edgeInNewGraph = getEdge (subjectNodeInNewGraph);

    // morph selectedNodeInOldGraph into selectedNodeInNewGraph
    // morph subjectNodeInOldGraph  into subjectNodeInNewGraph
    // morph edgeInOldGraph         into edgeInNewGraph
    AnimationFactory factory = new AnimationFactory ();
    AnimationObject morphSelected = factory.morph (getRealizer (oldGraph, selectedNodeInOldGraph),
                                                   getRealizer (newGraph, selectedNodeInNewGraph), settings.getAnimationDuration () / 3);
    AnimationObject morphSubject  = factory.morph (getRealizer (oldGraph, subjectNodeInOldGraph),
                                                   getRealizer (newGraph, subjectNodeInNewGraph), settings.getAnimationDuration () / 3);
    AnimationObject morphEdge     = factory.morph (getRealizer (oldGraph, edgeInOldGraph),
                                                   getRealizer (newGraph, edgeInNewGraph), settings.getAnimationDuration () / 3);

    CompositeAnimationObject concurrency = CompositeAnimationFactory.createConcurrency ();
    concurrency.addAnimation (morphSelected);
    concurrency.addAnimation (morphSubject);
    concurrency.addAnimation (morphEdge);
    return concurrency;
  }

  /**
   * Creates an animation object that fades out all nodes and edges but the seubject node and the selected node.
   * @return created animation object
   */
  private @NotNull AnimationObject createFadeIn ()
  {
    DataProvider oldNodeMap = oldGraph.getDataProvider (DataProviderKeys.DEPENDENCY_INFO_NODE_KEY);
    Node subjectNodeInOldGraph  = GraphUtils.findSubjectNode (oldGraph);
    GraphBuilder.NodeInfo subjectNodeInOldGraphInfo  = (GraphBuilder.NodeInfo) oldNodeMap.get (subjectNodeInOldGraph);
    GraphBuilder.NodeInfo selectedNodeInOldGraphInfo = (GraphBuilder.NodeInfo) oldNodeMap.get (selectedNodeInOldGraph);
    PsiClass subjectPsiClass    = subjectNodeInOldGraphInfo.getPsiClass ();
    DependencyType selectedType = selectedNodeInOldGraphInfo.getDependencyType ();
    Node selectedNodeInNewGraph = GraphUtils.findSubjectNode (newGraph);
    Node subjectNodeInNewGraph  = GraphUtils.findNode (newGraph, subjectPsiClass, selectedType.opposite ());
    Edge edgeInNewGraph         = getEdge (subjectNodeInNewGraph);

    // set all nodes but selectedNodeInNewGraph and subjectNodeInNewGraph to invisible
    // set all edges but edgeInNewGraph to invisible
    // create fade-in animations for all these nodes and edges
    CompositeAnimationObject concurrency = CompositeAnimationFactory.createConcurrency ();
    AnimationFactory factory = new AnimationFactory ();
    for (Node node : newGraph.getNodeArray ())
    {
      if (node != selectedNodeInNewGraph && node != subjectNodeInNewGraph)
      {
        NodeRealizer nodeRealizer = newGraph.getRealizer (node);
        nodeRealizer.setVisible (false);
        AnimationObject animation = factory.fadeIn (nodeRealizer, settings.getAnimationDuration () / 3);
        concurrency.addAnimation (animation);
      }
    }
    for (final Edge edge : newGraph.getEdgeArray ())
    {
      if (edge != edgeInNewGraph)
      {
        EdgeRealizer edgeRealizer = newGraph.getRealizer (edge);
        edgeRealizer.setVisible (false);
        AnimationObject animation = factory.fadeIn (edgeRealizer, settings.getAnimationDuration () / 3);
        concurrency.addAnimation (animation);
      }
    }
    return concurrency;
  }

  /**
   * NPE Save method to get node realizer.
   * @param graph graph that knows realizer
   * @param node node to get realizer for
   * @return found node realizer or null, if no realizer was found
   */
  private @Nullable NodeRealizer getRealizer (@NotNull Graph2D graph, @Nullable Node node)
  {
    return node != null ? graph.getRealizer (node) : null;
  }

  /**
   * NPE Save method to get edge realizer.
   * @param graph graph that knows realizer
   * @param edge edge to get realizer for
   * @return found edge realizer or null, if no realizer was found
   */
  private @Nullable EdgeRealizer getRealizer (@NotNull Graph2D graph, @Nullable Edge edge)
  {
    return edge != null ? graph.getRealizer (edge) : null;
  }

  /**
   * Gets the single edge that has the given node as target or source.
   * @param node node to get single edge for
   * @return edge that is connected to the given node
   */
  private @Nullable Edge getEdge (@Nullable Node node)
  {
    if (node == null)
    {
      return null;
    }
    Edge edge = node.firstInEdge ();
    return edge != null ? edge : node.firstOutEdge ();
  }
}
