package de.frag.umlplugin.settings;

import com.intellij.ui.AddDeleteListPanel;
import com.intellij.ui.ColorPanel;
import static de.frag.umlplugin.guilayout.BuilderConstants.*;
import de.frag.umlplugin.guilayout.LayoutBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;

/**
 * Settings form.
 */
public class SettingsForm
{
  private final JPanel        rootPanel;

  private final ColorPanel    normalCloudColorPanel;
  private final ColorPanel    usedCloudColorPanel;
  private final ColorPanel    extendedCloudColorPanel;
  private final JSpinner      minFontSizeCloudSpinner;
  private final JSpinner      maxFontSizeCloudSpinner;

  private final ColorPanel    classColorPanel;
  private final ColorPanel    abstractClassColorPanel;
  private final ColorPanel    interfaceColorPanel;
  private final ColorPanel    enumColorPanel;

  private final JCheckBox     animationCheckBox;
  private final JSpinner      animationDurationSpinner;

  private final AddDeleteList collectionClassesPanel;
  private final JTextField    collectionField;

  private final JCheckBox     nonProjectClassesCheckBox;
  private final JCheckBox     testClassesCheckBox;

  private final JSpinner      gridSizeSpinner;

  private final JCheckBox     highlightEdgesCheckBox;

  /**
   * Creates a new settings form by creating all GUI elements and building a layout.
   */
  public SettingsForm ()
  {
    classColorPanel           = new ColorPanel ();
    abstractClassColorPanel   = new ColorPanel ();
    interfaceColorPanel       = new ColorPanel ();
    enumColorPanel            = new ColorPanel ();

    normalCloudColorPanel     = new ColorPanel ();
    usedCloudColorPanel       = new ColorPanel ();
    extendedCloudColorPanel   = new ColorPanel ();
    minFontSizeCloudSpinner   = new JSpinner (new SpinnerNumberModel ( 5,  4, 12, 1));
    maxFontSizeCloudSpinner   = new JSpinner (new SpinnerNumberModel (32, 12, 60, 1));

    animationCheckBox         = new JCheckBox ("Enable Animations");
    animationDurationSpinner  = new JSpinner (new SpinnerNumberModel (500, 50, 5000, 10));

    collectionField           = new JTextField ();
    collectionClassesPanel    = new AddDeleteList ();

    nonProjectClassesCheckBox = new JCheckBox ("Show non-project classes");
    testClassesCheckBox       = new JCheckBox ("Show test classes");

    highlightEdgesCheckBox    = new JCheckBox ("Highlight edges for cyclic dependencies");

    gridSizeSpinner           = new JSpinner (new SpinnerNumberModel (25, 5, 200, 5));

    animationCheckBox.addChangeListener (new ChangeListener()
    {
      public void stateChanged (ChangeEvent e)
      {
        animationDurationSpinner.setEnabled (animationCheckBox.isSelected ());
      }
    });

    // build layout
    LayoutBuilder builder = new LayoutBuilder ();
    builder.setDefaultInsets (new Insets (4, 4, 4, 4));
    rootPanel = builder.beginV ();
      builder.beginH (2);
        JPanel colorsPanel = builder.beginH (2);
          builder.add (new JLabel ("Class Color:"),                LABEL);
          builder.add (classColorPanel,                            LEFT);
          builder.add (new JLabel ("Abstract Class Color:"),       LABEL);
          builder.add (abstractClassColorPanel,                    LEFT);
          builder.add (new JLabel ("Interface Color:"),            LABEL);
          builder.add (interfaceColorPanel,                        LEFT);
          builder.add (new JLabel ("Enum Color:"),                 LABEL);
          builder.add (enumColorPanel,                             LEFT);
        builder.end ();
        JPanel classCloudPanel = builder.beginV ();
          builder.beginH (2);
            builder.add (new JLabel ("Normal Color:"),             LABEL);
            builder.add (normalCloudColorPanel,                    LEFT);
            builder.add (new JLabel ("Used/Using Color:"),         LABEL);
            builder.add (usedCloudColorPanel,                      LEFT);
            builder.add (new JLabel ("Extended/Extending Color:"), LABEL);
            builder.add (extendedCloudColorPanel,                  LEFT);
          builder.end ();
          builder.beginH ();
            builder.add (new JLabel ("Min. Font Size:"),           LABEL);
            builder.add (minFontSizeCloudSpinner,                  LABEL);
            builder.addHSpace (20);
            builder.add (new JLabel ("Max. Font Size:"),           LABEL);
            builder.add (maxFontSizeCloudSpinner,                  LABEL);
            builder.addHSpace ();
          builder.end ();
        builder.end ();
      builder.end ();

      builder.beginH (2);
        JPanel collectionPanel = builder.beginV ();
          builder.beginH ();
            builder.add (new JLabel ("Collection class:"),         LABEL);
            builder.add (collectionField,                          STRETCH_X);
          builder.end ();
          builder.add (collectionClassesPanel,                     STRETCH_XY);
        builder.end ();
        builder.beginV ();
          JPanel animationPanel = builder.beginH (4);
            builder.add (animationCheckBox,                        LABEL);
            builder.addHSpace ();
            builder.addHSpace ();
            builder.addHSpace ();
            builder.add (new JLabel ("Animation Duration:"),       LABEL);
            builder.add (animationDurationSpinner,                 FILL_X);
            builder.add (new JLabel ("ms"),                        LABEL);
            builder.addHSpace ();
          builder.end ();
          JPanel classFilterPanel = builder.beginH (2);
            builder.add (nonProjectClassesCheckBox,                LABEL);
            builder.addHSpace ();
            builder.add (testClassesCheckBox,                      LABEL);
            builder.addHSpace ();
          builder.end ();
          JPanel dependenciesPanel = builder.beginH (2);
            builder.add (highlightEdgesCheckBox,                   LABEL);
            builder.addHSpace ();
          builder.end ();
          JPanel umlDiagramLayoutPanel = builder.beginH (3);
            builder.add (new JLabel ("Grid Size:"),                LABEL);
            builder.add (gridSizeSpinner,                          LABEL);
            builder.addHSpace ();
          builder.end ();
          builder.addVSpace ();
        builder.end ();
      builder.end ();
    builder.end ();

    colorsPanel.setBorder           (createBorder ("Graphical Navigator and UML diagram colors"));
    classCloudPanel.setBorder       (createBorder ("Class Cloud Settings"));
    animationPanel.setBorder        (createBorder ("Animation"));
    classFilterPanel.setBorder      (createBorder ("Class Filter"));
    dependenciesPanel.setBorder     (createBorder ("Cyclic Dependencies"));
    collectionPanel.setBorder       (createBorder ("Collection Classes and Interfaces"));
    umlDiagramLayoutPanel.setBorder (createBorder ("UML Diagram layout"));
  }

  /**
   * Creates a titled border.
   * @param title border title
   * @return created border
   */
  private @NotNull Border createBorder (@NotNull String title)
  {
    return BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), title);
  }

  /**
   * Gets the root panel that contains all GUI components and finished layout created at constrcution time.
   * @return root panel
   */
  public @NotNull JPanel getRootPanel ()
  {
    return rootPanel;
  }

  /**
   * Reads settings from given settings object and copies contained data to all GUI components of this form.
   * @param data data to read settings from
   */
  public void readFromSettings (@NotNull Settings data)
  {
    classColorPanel.setSelectedColor         (data.getClassColor ());
    abstractClassColorPanel.setSelectedColor (data.getAbstractClassColor ());
    interfaceColorPanel.setSelectedColor     (data.getInterfaceColor ());
    enumColorPanel.setSelectedColor          (data.getEnumColor ());

    normalCloudColorPanel.setSelectedColor   (data.getNormalCloudColor ());
    usedCloudColorPanel.setSelectedColor     (data.getUsedCloudColor ());
    extendedCloudColorPanel.setSelectedColor (data.getExtendedCloudColor ());
    minFontSizeCloudSpinner.setValue         (data.getMinFontSize ());
    maxFontSizeCloudSpinner.setValue         (data.getMaxFontSize ());

    animationCheckBox.setSelected     (data.isAnimateNavigation ());
    animationDurationSpinner.setValue (data.getAnimationDuration ());

    collectionClassesPanel.setListItems (data.getCollectionClasses ());

    nonProjectClassesCheckBox.setSelected (data.isIncludeNonProjectClasses ());
    testClassesCheckBox.setSelected       (data.isIncludeTestClasses ());

    highlightEdgesCheckBox.setSelected (data.isHightlightCyclicEdges ());

    gridSizeSpinner.setValue (data.getGridSize ());
  }

  /**
   * Writes settings from this form to the given settings object.
   * @param data settings object to write to
   */
  public void writeToSettings (@NotNull Settings data)
  {
    data.setClassColor         (classColorPanel.getSelectedColor ());
    data.setAbstractClassColor (abstractClassColorPanel.getSelectedColor ());
    data.setInterfaceColor     (interfaceColorPanel.getSelectedColor ());
    data.setEnumColor          (enumColorPanel.getSelectedColor ());

    data.setNormalCloudColor   (normalCloudColorPanel.getSelectedColor ());
    data.setUsedCloudColor     (usedCloudColorPanel.getSelectedColor ());
    data.setExtendedCloudColor (extendedCloudColorPanel.getSelectedColor ());
    int minFontSize = ((SpinnerNumberModel) minFontSizeCloudSpinner.getModel ()).getNumber ().intValue ();
    int maxFontSize = ((SpinnerNumberModel) maxFontSizeCloudSpinner.getModel ()).getNumber ().intValue ();
    data.setMinFontSize        (minFontSize);
    data.setMaxFontSize        (maxFontSize);

    data.setAnimateNavigation (animationCheckBox.isSelected ());
    int animationDuration = ((SpinnerNumberModel) animationDurationSpinner.getModel ()).getNumber ().intValue ();
    data.setAnimationDuration (animationDuration);

    Object [] collections = collectionClassesPanel.getListItems ();
    Set collectionsSet = new HashSet<Object> (Arrays.asList (collections));
    //noinspection unchecked
    data.setCollectionClasses (collectionsSet);

    data.setIncludeNonProjectClasses (nonProjectClassesCheckBox.isSelected ());
    data.setIncludeTestClasses (testClassesCheckBox.isSelected ());

    data.setHightlightCyclicEdges (highlightEdgesCheckBox.isSelected ());

    int gridSize = ((SpinnerNumberModel) gridSizeSpinner.getModel ()).getNumber ().intValue ();
    data.setGridSize (gridSize);
  }

  /**
   * Checks whether this forms has modified the settings.
   * @param data settings object to compare this forms contents to
   * @return true, if this form contains different settings than given settings object; false otherwise
   */
  public boolean isModified (Settings data)
  {
    Object[] collections = collectionClassesPanel.getListItems ();
    Set<?> collectionsSet = new HashSet<Object> (Arrays.asList (collections));
    return (!classColorPanel.getSelectedColor ().equals (data.getClassColor ()) ||
            !abstractClassColorPanel.getSelectedColor ().equals (data.getAbstractClassColor ()) ||
            !interfaceColorPanel.getSelectedColor ().equals (data.getInterfaceColor ()) ||
            !enumColorPanel.getSelectedColor ().equals (data.getEnumColor ()) ||

            !normalCloudColorPanel.getSelectedColor ().equals (data.getNormalCloudColor ()) ||
            !usedCloudColorPanel.getSelectedColor ().equals (data.getUsedCloudColor ()) ||
            !extendedCloudColorPanel.getSelectedColor ().equals (data.getExtendedCloudColor ()) ||
            !minFontSizeCloudSpinner.getValue ().equals (data.getMinFontSize ()) ||
            !maxFontSizeCloudSpinner.getValue ().equals (data.getMaxFontSize ()) ||

            animationCheckBox.isSelected () != data.isAnimateNavigation ()) ||
            !animationDurationSpinner.getValue ().equals (data.getAnimationDuration ()) ||

            !data.getCollectionClasses ().equals (collectionsSet) ||

            (nonProjectClassesCheckBox.isSelected () != data.isIncludeNonProjectClasses ()) ||
            (testClassesCheckBox.isSelected () != data.isIncludeTestClasses () ||

            !gridSizeSpinner.getValue ().equals (data.getGridSize ()) ||

            (highlightEdgesCheckBox.isSelected () != data.isHightlightCyclicEdges ()));
  }

  /**
   * Checks whether class visibility was reduced.
   * @param data old (unchanged) settings data
   * @return true, if class visibility was reduced; false otherwise
   */
  public boolean isReducedClassVisibility (@NotNull Settings data)
  {
    return (!nonProjectClassesCheckBox.isSelected () && data.isIncludeNonProjectClasses ()) ||
            (!testClassesCheckBox.isSelected () && data.isIncludeTestClasses ());
  }

  /**
   * Checks whether class visibility was modified.
   * @param data old (unchanged) settings data
   * @return true, if class visibility was modified; false otherwise
   */
  public boolean isModifiedClassVisibility (@NotNull Settings data)
  {
    return (nonProjectClassesCheckBox.isSelected () != data.isIncludeNonProjectClasses ()) ||
            (testClassesCheckBox.isSelected () != data.isIncludeTestClasses ());
  }

  /**
   * List for adding/deleting entries in a string list.
   */
  public class AddDeleteList extends AddDeleteListPanel
  {
    public AddDeleteList ()
    {
      super (null, Collections.EMPTY_LIST);
    }

    protected Object findItemToAdd ()
    {
      return collectionField.getText ();
    }

    public void setListItems (Collection items)
    {
      myListModel.clear ();
      for (Object item : items)
      {
        myListModel.addElement (item);
      }
    }
  }
}
