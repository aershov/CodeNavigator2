package de.frag.umlplugin.guilayout;

import static de.frag.umlplugin.guilayout.BuilderConstants.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

/**
  * The LayoutBuilder is a convenience class for building hierarchical
  * FlexLayout GUI layouts.
  * The following example should illustrate
  * the basic functionality:
  * <p>
  * <img src="FlexDemo.gif" alt="FlexDemo application window snapshot">
  * <p>
  * To create this GUI the following code does all the layout after the
  * GUI elements itself are created.
  * <pre>
  *   LayoutBuilder builder = new LayoutBuilder ();
  *   builder.setDefaultInsets (new Insets (2, 2, 2, 2));
  *   JPanel root = builder.beginV ();
  *     builder.beginH ();    // selection area
  *       builder.beginV ();  // box with available computers
  *         builder.add (availableLabel,    CENTER);
  *         builder.add (availableScroller, STRETCH_XY);
  *       builder.end ();
  *       builder.beginV ();  // buttons to add/remove computers
  *         builder.add (add,    FILL_X);
  *         builder.add (remove, FILL_X);
  *       builder.end ();
  *       builder.beginV ();  // box with selected computers
  *         builder.add (selectedLabel,    CENTER);
  *         builder.add (selectedScroller, STRETCH_XY);
  *       builder.end ();
  *     builder.end ();       // end of selection area
  *     builder.beginH (4);
  *       builder.add (hostLabel, LABEL);
  *       builder.add (hostField, STRETCH_X);
  *       builder.add (ipLabel,   LABEL);
  *       builder.add (ipField,   STRETCH_X);
  *       builder.add (osLabel,   LABEL);
  *       builder.add (osField,   STRETCH_X);
  *       builder.add (roomLabel, LABEL);
  *       builder.add (roomField, STRETCH_X);
  *     builder.end ();
  *     builder.beginH ();
  *       builder.add (okButton,     MAX_X);
  *       builder.addHSpace ();
  *       builder.add (cancelButton, MAX_X);
  *     builder.end ();
  *   builder.end ();
  * </pre>
  * <p>
  * This is a simple example, that uses a border layout:
  * <pre>
  *   JPanel root = new JPanel (new BorderLayout ());
  *   root.add (north,  BorderLayout.NORTH);
  *   root.add (south,  BorderLayout.SOUTH);
  *   root.add (west,   BorderLayout.WEST);
  *   root.add (east,   BorderLayout.EAST);
  *   root.add (center, BorderLayout.CENTER);
  * </pre>
  * The following code simulates this border layout using FlexLayout:
  * <pre>
  *   LayoutBuilder builder = new LayoutBuilder ();
  *   JPanel root = builder.beginV ();
  *     builder.add (north, STRETCH_X);
  *     builder.beginH ();
  *       builder.add (west,   STRETCH_Y);
  *       builder.add (center, STRETCH_XY);
  *       builder.add (east,   STRETCH_Y);
  *     builder.end ();
  *     builder.add (south, STRETCH_X);
  *   builder.end ();
  * </pre>
  * @author Frank Gerberding
  */
public class LayoutBuilder
{
  private final Stack<Container> containerStack;    // stack of parent containers
  private Container              currContainer;     // current container
  private Insets                 componentInsets;   // default component insets
  private Insets                 containerInsets;   // default container insets
  private final Stack<Builder>   builderStack;      // stack of builders
  private Builder                currBuilder;       // current builder

  /**
    * Construct a new LayoutBuilder. The first (root) container should be
    * created using one of the begin-methods, so adding of components is
    * possible only after entering the first hierarchy of containers.
    */
  public LayoutBuilder ()
  {
    containerStack  = new Stack<Container> ();
    builderStack    = new Stack<Builder> ();
    currContainer   = null;
    currBuilder     = null;
    componentInsets = FlexConstraints.NULL_INSETS;
    containerInsets = FlexConstraints.NULL_INSETS;
  }

  /**
    * Create a new LayoutBuilder, that sets a the specified FlexLayout as
    * layout manager for the given container and then uses the given
    * Container as root container.
    * @param root root container
    * @param layout layout manager to use for the root container
    */
  public LayoutBuilder (@NotNull Container root, @NotNull FlexLayout layout)
  {
    this ();
    root.setLayout (layout);
    currContainer = root;
    currBuilder   = new FlexBuilder ();
  }

  /**
    * Set the default insets, that will be used for all components or
    * containers, that are added with <code>null</code> insets.
    * @param insets new default insets
    */
  public void setDefaultInsets (@Nullable Insets insets)
  {
    insets = (insets == null) ? FlexConstraints.NULL_INSETS : insets;
    componentInsets = insets;
    containerInsets = insets;
  }

  /**
    * Set the default insets, that will be used for all components,
    * that are added with <code>null</code> insets.
    * @param insets new default insets
    */
  public void setDefaultComponentInsets (@Nullable Insets insets)
  {
    insets = (insets == null) ? FlexConstraints.NULL_INSETS : insets;
    componentInsets = insets;
  }

  /**
    * Set the default insets, that will be used for all
    * containers, that are added with <code>null</code> insets.
    * @param insets new default insets
    */
  public void setDefaultContainerInsets (@Nullable Insets insets)
  {
    insets = (insets == null) ? FlexConstraints.NULL_INSETS : insets;
    containerInsets = insets;
  }

  /**
    * Get the default insets, that are used for all components,
    * that are added with <code>null</code> insets.
    * @return default insets
    */
  public @NotNull Insets getDefaultComponentInsets ()
  {
    return componentInsets;
  }

  /**
    * Get the default insets, that are used for all containers,
    * that are added with <code>null</code> insets.
    * @return default insets
    */
  public @NotNull Insets getDefaultContainerInsets ()
  {
    return containerInsets;
  }

  /**
    * Begin a new horizontal hierarchy level of components. All components,
    * that are added subsequently via calls to <code>add</code>, are arranged
    * horizontally. So the new component hierarchy will be an irregular
    * raster of components with one row and an arbitrary number of columns.
    * @return the container, that is used as new component hierarchy level
    */
  public @NotNull JPanel beginH ()
  {
    containerStack.push (currContainer);
    builderStack.push   (currBuilder);
    JPanel panel = new JPanel (FlexLayout.createVerticalLayout (1));
    currContainer = panel;
    currBuilder = new FlexBuilder ();
    return panel;
  }

  /**
    * Begin a new horizontal hierarchy level of components. All components,
    * that are added subsequently via calls to <code>add</code>, are arranged
    * horizontally until the specified number of columns is reached and
    * a new row of components is created. So the new component hierarchy
    * will be an irregular raster of components with a fixed number of
    * columns and an arbitrary number of rows.
    * @param columns maximum number of columns
    * @return the container, that is used as new component hierarchy level
    */
  public @NotNull JPanel beginH (int columns)
  {
    containerStack.push (currContainer);
    builderStack.push   (currBuilder);
    JPanel panel = new JPanel (FlexLayout.createHorizontalLayout (columns));
    currContainer = panel;
    currBuilder = new FlexBuilder ();
    return panel;
  }

  /**
    * Begin a new vertical hierarchy level of components. All components,
    * that are added subsequently via calls to <code>add</code>, are arranged
    * vertically. So the new component hierarchy will be an irregular
    * raster of components with one column and an arbitrary number of rows.
    * @return the container, that is used as new component hierarchy level
    */
  public @NotNull JPanel beginV ()
  {
    containerStack.push (currContainer);
    builderStack.push   (currBuilder);
    JPanel panel = new JPanel (FlexLayout.createHorizontalLayout (1));
    currContainer = panel;
    currBuilder = new FlexBuilder ();
    return panel;
  }

  /**
    * Begin a new vertical hierarchy level of components. All components,
    * that are added subsequently via calls to <code>add</code>, are arranged
    * vertically until the specified number of rows is reached and
    * a new column of components is created. So the new component hierarchy
    * will be an irregular raster of components with a fixed number of
    * rows and an arbitrary number of columns.
    * @param rows maximum number of rows
    * @return the container, that is used as new component hierarchy level
    */
  public @NotNull JPanel beginV (int rows)
  {
    containerStack.push (currContainer);
    builderStack.push   (currBuilder);
    JPanel panel = new JPanel (FlexLayout.createVerticalLayout (rows));
    currContainer = panel;
    currBuilder = new FlexBuilder ();
    return panel;
  }

  /**
    * Begin a new border layout hierarchy level of components. All components,
    * that are added subsequently via calls to <code>add</code>, are arranged
    * using a border layout
    * @return the container, that is used as new component hierarchy level
    */
  public @NotNull JPanel beginBorder ()
  {
    containerStack.push (currContainer);
    builderStack.push   (currBuilder);
    JPanel panel = new JPanel (new BorderLayout ());
    currContainer = panel;
    currBuilder = new BorderBuilder ();
    return panel;
  }

  /**
    * Leave the current container hierarchy level. The current container will
    * be added to its parent container and the parent container will
    * become the current container. If this is called without a matching
    * previous call to of the <code>begin</code>-methods, a
    * <code>java.util.EmptyStackException</code> will be raised.
    */
  public void end ()
  {
    end (null);
  }

  /**
    * Leave the current container hierarchy level. The current container will
    * be added to its parent container and the parent container will
    * become the current container. If this is called without a matching
    * previous call to of the <code>begin</code>-methods, a
    * <code>java.util.EmptyStackException</code> will be raised.
    * @param constraints constraints to use for finished panel
    */
  public void end (@Nullable Object constraints)
  {
    currBuilder = builderStack.pop ();
    if (currBuilder != null)
    {
      constraints = currBuilder.getParentConstraints (currContainer, constraints);
    }
    Container child = currContainer;
    currContainer   = containerStack.pop ();
    if (currBuilder != null)
    {
      add (child, constraints);
    }
  }

  /**
    * Add the given component to the current container using the given
    * constraints.
    * @param component component to add
    * @param constraints constraints to use for the given component
    */
  public void add (@NotNull Component component, @Nullable Object constraints)
  {
    currBuilder.add (component, constraints);
  }

  /**
    * Add some horizontal flexible space to create a gap between other
    * components.
    */
  public void addHSpace ()
  {
    currBuilder.addHSpace ();
  }

  /**
    * Add some horizontal fixed space to create a gap between other components.
    * @param pixels width of space in pixels
    */
  public void addHSpace (int pixels)
  {
    currBuilder.addHSpace (pixels);
  }

  /**
    * Add some vertical flexible space to create a gap between other components.
    */
  public void addVSpace ()
  {
    currBuilder.addVSpace ();
  }

  /**
    * Add some vertical fixed space to create a gap between other components.
    * @param pixels height of space in pixels
    */
  public void addVSpace (int pixels)
  {
    currBuilder.addVSpace (pixels);
  }

  //------------------------------------------------------------------------

  private interface Builder
  {
    public void add (Component component, Object constraints);
    public Object getParentConstraints (Container parent, Object constraints);
    public void addHSpace ();
    public void addHSpace (int pixels);
    public void addVSpace ();
    public void addVSpace (int pixels);
  }

  //------------------------------------------------------------------------

  private class FlexBuilder implements Builder
  {
    public void add (@NotNull Component component, @NotNull Object constraints)
    {
      FlexConstraints flexConstraints = (FlexConstraints) constraints;
      if ((flexConstraints.insets == FlexConstraints.NULL_INSETS))
      {
        flexConstraints = new FlexConstraints (flexConstraints);
        flexConstraints.insets = componentInsets;
      }
      if (currContainer != null)
      {
        currContainer.add (component, new FlexConstraints (flexConstraints));
      }
    }

    public @Nullable Object getParentConstraints (@Nullable Container parent, @Nullable Object constraints)
    {
      if (parent != null)
      {
        FlexConstraints flexConstraints = (FlexConstraints) constraints;
        if (flexConstraints == null)
        {
          if (parent.getLayout () instanceof FlexLayout)
          {
            FlexLayout layout = (FlexLayout) parent.getLayout ();
            flexConstraints = new FlexConstraints (layout.getParentConstraints ());
          }
          else
          {
            flexConstraints = STRETCH_XY;
          }
        }
        if (flexConstraints.insets == FlexConstraints.NULL_INSETS)
        {
          flexConstraints.insets = containerInsets;
        }
        constraints = flexConstraints;
      }
      return constraints;
    }

    public void addHSpace ()
    {
      Component space = Box.createHorizontalGlue ();
      currContainer.add (space, new FlexConstraints (STRETCH_X));
    }

    public void addHSpace (int pixels)
    {
      Component space = Box.createHorizontalStrut (pixels);
      currContainer.add (space, CENTER);
    }

    public void addVSpace ()
    {
      Component space = Box.createVerticalGlue ();
      currContainer.add (space, STRETCH_Y);
    }

    public void addVSpace (int pixels)
    {
      Component space = Box.createVerticalStrut (pixels);
      currContainer.add (space, CENTER);
    }
  }

  //--------------------------------------------------------------------

  private class BorderBuilder implements Builder
  {
    public void add (@NotNull Component component, @NotNull Object constraints)
    {
      if (currContainer != null)
      {
        currContainer.add (component, constraints);
      }
    }

    public @Nullable Object getParentConstraints (@Nullable Container parent, @Nullable Object constraints)
    {
      if (parent != null)
      {
        if (constraints == null)
        {
          constraints = BorderLayout.AFTER_LAST_LINE;
        }
      }
      return constraints;
    }

    public void addHSpace ()
    {
    }

    public void addHSpace (int pixels)
    {
    }

    public void addVSpace ()
    {
    }

    public void addVSpace (int pixels)
    {
    }
  }
}
