package de.frag.umlplugin.anim;

import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.geom.YPoint;
import com.intellij.openapi.graph.view.EdgeRealizer;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.graph.view.NodeRealizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates several types of animation objects.
 */
public class AnimationFactory
{
  /**
   * Creates a fade-in animation for the given node realizer.
   * @param realizer realizer to fade in
   * @param preferredDuration duration in milli seconds
   * @return created animation object
   */
  public @NotNull AnimationObject fadeIn (@Nullable NodeRealizer realizer, long preferredDuration)
  {
    if (realizer == null)
    {
      return new NopAnimation (preferredDuration);
    }
    return new NodeFadeIn (realizer, preferredDuration);
  }

  /**
   * Creates a fade-out animation for the given node realizer.
   * @param realizer realizer to fade out
   * @param preferredDuration duration in milli seconds
   * @return created animation object
   */
  public @NotNull AnimationObject fadeOut (@Nullable NodeRealizer realizer, long preferredDuration)
  {
    if (realizer == null)
    {
      return new NopAnimation (preferredDuration);
    }
    return new NodeFadeOut (realizer, preferredDuration);
  }

  /**
   * Creates a fade-in animation for the given edge realizer.
   * @param realizer realizer to fade in
   * @param preferredDuration duration in milli seconds
   * @return created animation object
   */
  public @NotNull AnimationObject fadeIn (@Nullable EdgeRealizer realizer, long preferredDuration)
  {
    if (realizer == null)
    {
      return new NopAnimation (preferredDuration);
    }
    return new EdgeFadeIn (realizer, preferredDuration);
  }

  /**
   * Creates a fade-out animation for the given edge realizer.
   * @param realizer realizer to fade out
   * @param preferredDuration duration in milli seconds
   * @return created animation object
   */
  public @NotNull AnimationObject fadeOut (@Nullable EdgeRealizer realizer, long preferredDuration)
  {
    if (realizer == null)
    {
      return new NopAnimation (preferredDuration);
    }
    return new EdgeFadeOut (realizer, preferredDuration);
  }

  /**
   * Creates a morph animation from the given source realizer to the specified target realizer.
   * @param source source realizer
   * @param target target realizer
   * @param preferredDuration duration in milli seconds
   * @return created animation object
   */
  public @NotNull AnimationObject morph (@Nullable NodeRealizer source, @Nullable NodeRealizer target,
                                         long preferredDuration)
  {
    if (source == null || target == null)
    {
      return new NopAnimation (preferredDuration);
    }
    return new NodeMorph (source, target, preferredDuration);
  }

  /**
   * Creates a morph animation from the given source realizer to the specified target realizer.
   * @param source source realizer
   * @param target target realizer
   * @param preferredDuration duration in milli seconds
   * @return created animation object
   */
  public @NotNull AnimationObject morph (@Nullable EdgeRealizer source, @Nullable EdgeRealizer target,
                                         long preferredDuration)
  {
    if (source == null || target == null)
    {
      return new NopAnimation (preferredDuration);
    }
    return new EdgeMorph (source, target, preferredDuration);
  }

  /**
   * Creates a an animation that exchanges the current graph.
   * @param view replace graph in this view
   * @param graph2D replace current graph by this graph
   * @return created animation object
   */
  public @NotNull AnimationObject exchangeGraph (@NotNull Graph2DView view, @NotNull Graph2D graph2D)
  {
    return new ExchangeGraph (view, graph2D);
  }

  /**
   * Creates a an animation that fits the given rectangle in the specified view.
   * @param view fit rectangle in this vire
   * @param rectangle rectangle
   * @param preferredDuration duration in milli seconds
   * @return created animation object
   */
  public @NotNull AnimationObject fitRectangle (@NotNull Graph2DView view, @NotNull Rectangle rectangle,
                                                long preferredDuration)
  {
    return new FitRectangle (view, rectangle, preferredDuration);
  }

  /**
   * Creates a an animation that zooms to the specified zoom factor.
   * @param view zoom this view
   * @param zoom target zoom factor
   * @param preferredDuration duration in milli seconds
   * @return created animation object
   */
  public @NotNull AnimationObject zoom (@NotNull Graph2DView view, double zoom, long preferredDuration)
  {
    return new Zoom (view, zoom, preferredDuration);
  }



  //---------------------------------------------------------------------------------------------
  //------------------------------------- helper methods ----------------------------------------
  //---------------------------------------------------------------------------------------------

  /**
   * Creates a color with alpha computed from given transparency.
   * @param color base color
   * @param transparency transparency of new color, 0.0 means opaque, 1.0 means completely transparent.
   * @return created color
   */
  private @NotNull Color createTransparentColor (@NotNull Color color, double transparency)
  {
    return new Color (color.getRed (), color.getGreen (), color.getBlue (), (int) ((1.0 - transparency) * 255));
  }



  //---------------------------------------------------------------------------------------------
  //--------------------------- animation object implementations --------------------------------
  //---------------------------------------------------------------------------------------------

  /**
   * Does exactly nothing.
   */
  private class NopAnimation extends AbstractAnimationObject
  {
    private NopAnimation (long preferredDuration)
    {
      super (preferredDuration);
    }

    /**
     * Calculates the animation frame for the specified point in time. The valid time interval for animations is always
     * [0.0, 1.0].
     * @param time a point in [0.0, 1.0]
     */
    public void calcFrame (double time)
    {
    }
  }



  private class NodeFadeIn extends AbstractAnimationObject
  {
    private final NodeRealizer realizer;

    public NodeFadeIn (@NotNull NodeRealizer realizer, long preferredDuration)
    {
      super (preferredDuration);
      this.realizer = realizer;
    }

    public void initAnimation ()
    {
      for (int i = 0; i < realizer.labelCount (); i++)
      {
        realizer.getLabel (i).setVisible (false);
        realizer.setVisible (true);
      }
    }

    public void calcFrame (double time)
    {
      Color fillColor = realizer.getFillColor ();
      Color lineColor = realizer.getLineColor ();
      realizer.setFillColor (createTransparentColor (fillColor, 1.0 - time));
      realizer.setLineColor (createTransparentColor (lineColor, 1.0 - time));
    }

    public void disposeAnimation ()
    {
      Color fillColor = realizer.getFillColor ();
      Color lineColor = realizer.getLineColor ();
      realizer.setFillColor (createTransparentColor (fillColor, 0.0));
      realizer.setLineColor (createTransparentColor (lineColor, 0.0));
      for (int i = 0; i < realizer.labelCount (); i++)
      {
        realizer.getLabel (i).setVisible (true);
      }
    }
  }



  private class NodeFadeOut extends AbstractAnimationObject
  {
    private final NodeRealizer realizer;

    public NodeFadeOut (@NotNull NodeRealizer realizer, long preferredDuration)
    {
      super (preferredDuration);
      this.realizer = realizer;
      for (int i = 0; i < realizer.labelCount (); i++)
      {
        realizer.getLabel (i).setVisible (false);
      }
    }

    public void calcFrame (double time)
    {
      Color fillColor = realizer.getFillColor ();
      Color lineColor = realizer.getLineColor ();
      realizer.setFillColor (createTransparentColor (fillColor, time));
      realizer.setLineColor (createTransparentColor (lineColor, time));
    }

    public void disposeAnimation ()
    {
      realizer.setVisible (false);
    }
  }



  private class EdgeFadeIn extends AbstractAnimationObject
  {
    private final EdgeRealizer realizer;

    public EdgeFadeIn (@NotNull EdgeRealizer realizer, long preferredDuration)
    {
      super (preferredDuration);
      this.realizer = realizer;
    }

    public void initAnimation ()
    {
      for (int i = 0; i < realizer.labelCount (); i++)
      {
        realizer.getLabel (i).setVisible (false);
      }
      realizer.setVisible (true);
    }

    public void calcFrame (double time)
    {
      Color lineColor = realizer.getLineColor ();
      realizer.setLineColor (createTransparentColor (lineColor, 1.0 - time));
    }

    public void disposeAnimation ()
    {
      Color lineColor = realizer.getLineColor ();
      realizer.setLineColor (createTransparentColor (lineColor, 0.0));
      for (int i = 0; i < realizer.labelCount (); i++)
      {
        realizer.getLabel (i).setVisible (true);
      }
    }
  }



  private class EdgeFadeOut extends AbstractAnimationObject
  {
    private final EdgeRealizer realizer;

    public EdgeFadeOut (@NotNull EdgeRealizer realizer, long preferredDuration)
    {
      super (preferredDuration);
      this.realizer = realizer;
      for (int i = 0; i < realizer.labelCount (); i++)
      {
        realizer.getLabel (i).setVisible (false);
      }
    }

    public void calcFrame (double time)
    {
      Color lineColor = realizer.getLineColor ();
      realizer.setLineColor (createTransparentColor (lineColor, time));
    }

    public void disposeAnimation ()
    {
      realizer.setVisible (false);
    }
  }



  private class NodeMorph extends AbstractAnimationObject
  {
    private final NodeRealizer source;
    private final NodeRealizer target;
    private final double       sourceX;
    private final double       sourceY;
    private final double       sourceWidth;
    private final double       sourceHeight;

    public NodeMorph (@NotNull NodeRealizer source, NodeRealizer target, long preferredDuration)
    {
      super (preferredDuration);
      this.source       = source;
      this.target       = target;
      this.sourceX      = source.getX ();
      this.sourceY      = source.getY ();
      this.sourceWidth  = source.getWidth ();
      this.sourceHeight = source.getHeight ();
    }

    public void calcFrame (double time)
    {
      double targetX      = target.getX ();
      double targetY      = target.getY ();
      double targetWidth  = target.getWidth ();
      double targetHeight = target.getHeight ();
      double x            = sourceX      + time * (targetX      - sourceX);
      double y            = sourceY      + time * (targetY      - sourceY);
      double width        = sourceWidth  + time * (targetWidth  - sourceWidth);
      double height       = sourceHeight + time * (targetHeight - sourceHeight);
      source.setX      (x);
      source.setY      (y);
      source.setWidth  (width);
      source.setHeight (height);
    }
  }



  private class EdgeMorph extends AbstractAnimationObject
  {
    private final EdgeRealizer source;
    private final EdgeRealizer target;
    private final List<YPoint> sourcePoints = new ArrayList<YPoint> ();
    private final List<YPoint> targetPoints = new ArrayList<YPoint> ();
    private final List<Double> sourceLabelAngles = new ArrayList<Double> ();

    public EdgeMorph (@NotNull EdgeRealizer source, @NotNull EdgeRealizer target, long preferredDuration)
    {
      super (preferredDuration);
      this.source = source;
      this.target = target;
      sourcePoints.add (source.getSourcePoint ());
      targetPoints.add (target.getSourcePoint ());
      for (int i = 0; i < source.pointCount (); i++)
      {
        sourcePoints.add (source.getPoint (0));
        targetPoints.add (target.getPoint (0));
      }
      sourcePoints.add (source.getTargetPoint ());
      targetPoints.add (target.getTargetPoint ());

      for (int i = 0; i < source.labelCount (); i++)
      {
        sourceLabelAngles.add (source.getLabel (i).getRotationAngle ());
      }
    }

    public void calcFrame (double time)
    {
      GraphManager graphManager = GraphManager.getGraphManager ();
      for (int i = 0; i < source.pointCount () + 2; i++)
      {
        YPoint sourcePoint = sourcePoints.get (i);
        YPoint targetPoint = targetPoints.get (i);
        double sourceX = sourcePoint.getX ();
        double sourceY = sourcePoint.getY ();
        double targetX = targetPoint.getX ();
        double targetY = targetPoint.getY ();
        double x       = sourceX + time * (targetX - sourceX);
        double y       = sourceY + time * (targetY - sourceY);
        if (i == 0)
        {
          source.setSourcePoint (graphManager.createYPoint (x, y));
        }
        else if (i == sourcePoints.size () - 1)
        {
          source.setTargetPoint (graphManager.createYPoint (x, y));
        }
        else
        {
          source.setPoint (i - 1, x, y);
        }
      }
      for (int i = 0; i < source.labelCount (); i++)
      {
        double sourceAngle = sourceLabelAngles.get (i);
        double targetAngle = target.getLabel (i).getRotationAngle ();
        double diff        = targetAngle - sourceAngle;
        if (diff > 180)
        {
          diff -= 360;
        }
        else if (diff < -180)
        {
          diff += 360;
        }
        source.getLabel (i).setRotationAngle (sourceAngle + time * diff);
      }
    }
  }



  private class ExchangeGraph extends AbstractAnimationObject
  {
    private final Graph2DView view;
    private final Graph2D     graph2D;

    public ExchangeGraph (@NotNull Graph2DView view, @NotNull Graph2D graph2D)
    {
      super (1);
      this.view    = view;
      this.graph2D = graph2D;
    }

    public void calcFrame (double time)
    {
      if (view.getGraph2D () != graph2D)
      {
        view.setGraph2D (graph2D);
      }
    }
  }



  private class FitRectangle extends AbstractAnimationObject
  {
    private final Graph2DView view;
    private final Rectangle   sourceRectangle;
    private final Rectangle   targetRectangle;

    public FitRectangle (@NotNull Graph2DView view, @NotNull Rectangle rectangle, long preferredDuration)
    {
      super (preferredDuration);
      this.view            = view;
      this.sourceRectangle = view.getVisibleRect ();
      this.targetRectangle = rectangle;
    }

    public void calcFrame (double time)
    {
      double x      = sourceRectangle.getX      () + time * (targetRectangle.getX      () - sourceRectangle.getX      ());
      double y      = sourceRectangle.getY      () + time * (targetRectangle.getY      () - sourceRectangle.getY      ());
      double width  = sourceRectangle.getWidth  () + time * (targetRectangle.getWidth  () - sourceRectangle.getWidth  ());
      double height = sourceRectangle.getHeight () + time * (targetRectangle.getHeight () - sourceRectangle.getHeight ());
      view.fitRectangle (new Rectangle ((int) x, (int) y, (int) width, (int) height));
    }

    public void disposeAnimation ()
    {
      view.fitRectangle (targetRectangle);
      view.adjustScrollBarVisibility ();
    }
  }



  private class Zoom extends AbstractAnimationObject
  {
    private final Graph2DView view;
    private final double      sourceZoom;
    private final double      targetZoom;

    public Zoom (@NotNull Graph2DView view, double zoom, long preferredDuration)
    {
      super (preferredDuration);
      this.view       = view;
      this.sourceZoom = view.getZoom ();
      this.targetZoom = zoom;
    }

    public void calcFrame (double time)
    {
      view.setZoom (sourceZoom + time * (targetZoom - sourceZoom));
    }

    public void disposeAnimation ()
    {
      view.setZoom (targetZoom);
      view.adjustScrollBarVisibility ();
    }
  }
}
