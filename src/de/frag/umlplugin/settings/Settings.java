package de.frag.umlplugin.settings;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.JDOMExternalizer;
import de.frag.umlplugin.psi.ClassType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;

/**
 * Graphical navigator settings.
 */
public class Settings
{
  /** Color of normal classes in graphical navigator and UML diagrams. */
  private Color       classColor               = new Color (240, 233, 194);
  /** Color of abstract classes in graphical navigator and UML diagrams. */
  private Color       abstractClassColor       = new Color (240, 229, 168);
  /** Color of interfaces in graphical navigator and UML diagrams. */
  private Color       interfaceColor           = new Color (240, 225, 141);
  /** Color of enum classes in graphical navigator and UML diagrams. */
  private Color       enumColor                = new Color (240, 233, 194);

  /** Color of normal classes in class cloud. */
  private Color       normalCloudColor         = new Color (240, 233, 194);
  /** Color of used/suing classes in class cloud. */
  private Color       usedCloudColor           = new Color (152, 164, 184);
  /** Color of extended/extending classes in class cloud. */
  private Color       extendedCloudColor       = new Color (242, 162,  40);
  /** Minimum foint size in class cloud. */
  private int         minFontSize              =  5;
  /** Maximum foint size in class cloud. */
  private int         maxFontSize              = 32;

  /** True, if graphical navigator switches via smooth transitions; false if animations are disabled.  */
  private boolean     animateNavigation        = true;
  /** Duration of smooth transitions in graphical navigator in milli seconds. */
  private long        animationDuration        = 500;

  /** Names of classes that should be treated as collections for detecting one-to-many fields. */
  private Set<String> collectionClasses        = new HashSet<String> (Arrays.asList ("java.lang.Iterable", "java.util.Map"));

  /** True, if non-project classes should be included in graphical navigator. */
  private boolean     includeNonProjectClasses = false;
  /** True, if test-classes should be included in graphical navigator. */
  private boolean     includeTestClasses       = false;
  /** Name of search scope to be used for filtering classes in class cloud. */
  private String      classCloudScopeName      = null;

  /** True, if cyclic dependencies should be highlighted in graphical navigator. */
  private boolean     hightlightCyclicEdges    = false;

  /** Grid size for automatic layout in UML diagrams. */
  private int         gridSize                 =  25;

  /** Standard thumbnail height. */
  private int         thumbnailHeight = 40;
  /** Save thumbnails? */
  private boolean     saveThumbnails  = false;

  /** Small gap for layouting graphical navigator diagrams. */
  private final double      smallGap                 =   5;
  /** Minimum node witdh for layouting graphical navigator diagrams. */
  private final double      minNodeWidth             = 180;
  /** Minimum node height for layouting graphical navigator diagrams. */
  private final double      minNodeHeight            = 100;
  /** Horizontal gap for center node in graphical navigator diagrams. */
  private final double      centerGapH               = 150;
  /** Vertical gap for center node in graphical navigator diagrams. */
  private final double      centerGapV               =  70;
  /** Small gap for layouting graphical navigator diagrams. */
  private final double      bigGap                   =  40;
  /** Length of fork for layout of sub classes in graphical navigator diagrams. */
  private final double      forkLength               =  30;
  /** Maximum history length for graphical navigator. */
  private final int         maxHistorySize           =  40;
  /** Standard zoom factor when zooming diagrams one step. */
  private final double      zoomFactor               = 0.7;

  /**
   * Gets global uml plugin settings.
   * @return settings
   */
  public static @NotNull Settings getSettings ()
  {
    Application application = ApplicationManager.getApplication ();
    SettingsPlugin settingsPlugin = application.getComponent (SettingsPlugin.class);
    return settingsPlugin.getSettings ();
  }

  /**
   * Package visible constructor may only be called from plugin application component.
   */
  Settings ()
  {
  }

  public boolean isAnimateNavigation ()
  {
    return animateNavigation;
  }

  public @NotNull Color getColorForClassType (@NotNull ClassType classType)
  {
    switch (classType)
    {
      case CLASS:          return classColor;
      case ABSTRACT_CLASS: return abstractClassColor;
      case INTERFACE:      return interfaceColor;
      case ENUM:           return enumColor;
      default: throw new IllegalArgumentException ("unknown class type: " + classType);
    }
  }

  void setAnimateNavigation (boolean animateNavigation)
  {
    this.animateNavigation = animateNavigation;
  }

  public long getAnimationDuration ()
  {
    return animationDuration;
  }

  void setAnimationDuration (long animationDuration)
  {
    this.animationDuration = animationDuration;
  }

  public @NotNull Set<?> getCollectionClasses ()
  {
    return collectionClasses;
  }

  void setCollectionClasses (@NotNull Set<String> collectionClasses)
  {
    this.collectionClasses = collectionClasses;
  }

  public boolean isIncludeNonProjectClasses ()
  {
    return includeNonProjectClasses;
  }

  void setIncludeNonProjectClasses (boolean includeNonProjectClasses)
  {
    this.includeNonProjectClasses = includeNonProjectClasses;
  }

  public boolean isIncludeTestClasses ()
  {
    return includeTestClasses;
  }

  void setIncludeTestClasses (boolean includeTestClasses)
  {
    this.includeTestClasses = includeTestClasses;
  }

  public @Nullable String getClassCloudScopeName ()
  {
    return classCloudScopeName;
  }

  public void setClassCloudScopeName (@Nullable String classCloudScopeName)
  {
    this.classCloudScopeName = classCloudScopeName;
  }

  public boolean isHightlightCyclicEdges ()
  {
    return hightlightCyclicEdges;
  }

  public void setHightlightCyclicEdges (boolean hightlightCyclicEdges)
  {
    this.hightlightCyclicEdges = hightlightCyclicEdges;
  }

  public double getSmallGap ()
  {
    return smallGap;
  }

  public double getMinNodeWidth ()
  {
    return minNodeWidth;
  }

  public double getMinNodeHeight ()
  {
    return minNodeHeight;
  }

  public double getCenterGapH ()
  {
    return centerGapH;
  }

  public double getCenterGapV ()
  {
    return centerGapV;
  }

  public double getBigGap ()
  {
    return bigGap;
  }

  public double getForkLength ()
  {
    return forkLength;
  }

  public int getMaxHistorySize ()
  {
    return maxHistorySize;
  }

  public @NotNull Color getClassColor ()
  {
    return classColor;
  }

  void setClassColor (@NotNull Color classColor)
  {
    this.classColor = classColor;
  }

  public @NotNull Color getAbstractClassColor ()
  {
    return abstractClassColor;
  }

  void setAbstractClassColor (@NotNull Color abstractClassColor)
  {
    this.abstractClassColor = abstractClassColor;
  }

  public @NotNull Color getInterfaceColor ()
  {
    return interfaceColor;
  }

  void setInterfaceColor (@NotNull Color interfaceColor)
  {
    this.interfaceColor = interfaceColor;
  }

  public @NotNull Color getEnumColor ()
  {
    return enumColor;
  }

  void setEnumColor (@NotNull Color enumColor)
  {
    this.enumColor = enumColor;
  }

  public double getZoomFactor ()
  {
    return zoomFactor;
  }

  public int getGridSize ()
  {
    return gridSize;
  }

  public void setGridSize (int gridSize)
  {
    this.gridSize = gridSize;
  }

  public @NotNull Color getNormalCloudColor ()
  {
    return normalCloudColor;
  }

  public void setNormalCloudColor (@NotNull Color normalCloudColor)
  {
    this.normalCloudColor = normalCloudColor;
  }

  public @NotNull Color getUsedCloudColor ()
  {
    return usedCloudColor;
  }

  public void setUsedCloudColor (@NotNull Color usedCloudColor)
  {
    this.usedCloudColor = usedCloudColor;
  }

  public @NotNull Color getExtendedCloudColor ()
  {
    return extendedCloudColor;
  }

  public void setExtendedCloudColor (@NotNull Color extendedCloudColor)
  {
    this.extendedCloudColor = extendedCloudColor;
  }

  public int getMinFontSize ()
  {
    return minFontSize;
  }

  public void setMinFontSize (int minFontSize)
  {
    this.minFontSize = minFontSize;
  }

  public int getMaxFontSize ()
  {
    return maxFontSize;
  }

  public void setMaxFontSize (int maxFontSize)
  {
    this.maxFontSize = maxFontSize;
  }

  public int getThumbnailHeight ()
  {
    return thumbnailHeight;
  }

  public void setThumbnailHeight (int thumbnailHeight)
  {
    this.thumbnailHeight = thumbnailHeight;
  }

  public boolean isSaveThumbnails ()
  {
    return saveThumbnails;
  }

  public void setSaveThumbnails (boolean saveThumbnails)
  {
    this.saveThumbnails = saveThumbnails;
  }

  /**
   * Reads settings from given JDOM XML element.
   * @param element element to read settings from
   */
  void read (@NotNull Element element)
  {
    String classColorString         = JDOMExternalizer.readString (element, "classColor");
    String abstractClassColorString = JDOMExternalizer.readString (element, "abstractClassColor");
    String interfaceColorString     = JDOMExternalizer.readString (element, "interfaceColor");
    String enumColorString          = JDOMExternalizer.readString (element, "enumColor");
    setClassColor         (parseColor (classColorString,         classColor));
    setAbstractClassColor (parseColor (abstractClassColorString, abstractClassColor));
    setInterfaceColor     (parseColor (interfaceColorString,     interfaceColor));
    setEnumColor          (parseColor (enumColorString,          enumColor));

    String normalCloudColorString   = JDOMExternalizer.readString (element, "normalCloudColor");
    String usedCloudColorString     = JDOMExternalizer.readString (element, "usedCloudColor");
    String extendedCloudColorString = JDOMExternalizer.readString (element, "extendedCloudColor");
    setNormalCloudColor   (parseColor (normalCloudColorString,   normalCloudColor));
    setUsedCloudColor     (parseColor (usedCloudColorString,     usedCloudColor));
    setExtendedCloudColor (parseColor (extendedCloudColorString, extendedCloudColor));
    setMinFontSize (JDOMExternalizer.readInteger (element, "minFontSize",  5));
    setMaxFontSize (JDOMExternalizer.readInteger (element, "maxFontSize", 32));

    setAnimateNavigation (JDOMExternalizer.readBoolean (element, "animateNavigation"));
    setAnimationDuration (JDOMExternalizer.readInteger (element, "animationDuration", 500));

    setIncludeNonProjectClasses (JDOMExternalizer.readBoolean (element, "includeNonProjectClasses"));
    setIncludeTestClasses       (JDOMExternalizer.readBoolean (element, "includeTestClasses"));
    setClassCloudScopeName      (JDOMExternalizer.readString  (element, "classCloudScopeName"));

    setHightlightCyclicEdges (JDOMExternalizer.readBoolean (element, "hightlightCyclicEdges"));

    setThumbnailHeight (JDOMExternalizer.readInteger (element, "thumbnailHeight", 40));
    setSaveThumbnails  (JDOMExternalizer.readBoolean (element, "saveThumbnails"));

    Map<String,String> map = new HashMap<String,String> ();
    JDOMExternalizer.readMap (element, map, "collectionClasses", "collection");
    collectionClasses.clear ();
    collectionClasses.addAll (map.keySet ());

    setGridSize (JDOMExternalizer.readInteger (element, "gridSize", 25));
  }

  /**
   * Parses a color from a given hex coded rgb-value.
   * @param classColorString color string to be parsed (6-characters RGB-hex-value).
   * @param defaultColor default value, if string could not be parsed
   * @return parsed color
   */
  private @NotNull Color parseColor (@NotNull String classColorString, @NotNull Color defaultColor)
  {
    try
    {
      if (classColorString != null)
      {
        return new Color ((int) Long.parseLong (classColorString, 16));
      }
    }
    catch (NumberFormatException e)
    {
      // ignore
    }
    return defaultColor;
  }

  /**
   * Write settings to given JDOM XML element.
   * @param element store created child elements in this elements
   */
  void write (@NotNull Element element)
  {
    JDOMExternalizer.write (element, "classColor",         Integer.toHexString (classColor.getRGB ()));
    JDOMExternalizer.write (element, "abstractClassColor", Integer.toHexString (abstractClassColor.getRGB ()));
    JDOMExternalizer.write (element, "interfaceColor",     Integer.toHexString (interfaceColor.getRGB ()));
    JDOMExternalizer.write (element, "enumColor",          Integer.toHexString (enumColor.getRGB ()));

    JDOMExternalizer.write (element, "normalCloudColor",   Integer.toHexString (normalCloudColor.getRGB ()));
    JDOMExternalizer.write (element, "usedCloudColor",     Integer.toHexString (usedCloudColor.getRGB ()));
    JDOMExternalizer.write (element, "extendedCloudColor", Integer.toHexString (extendedCloudColor.getRGB ()));
    JDOMExternalizer.write (element, "minFontSize",        minFontSize);
    JDOMExternalizer.write (element, "maxFontSize",        maxFontSize);

    JDOMExternalizer.write (element, "animateNavigation",  animateNavigation);
    JDOMExternalizer.write (element, "animationDuration",  (int) animationDuration);

    JDOMExternalizer.write (element, "includeNonProjectClasses", includeNonProjectClasses);
    JDOMExternalizer.write (element, "includeTestClasses",       includeTestClasses);
    JDOMExternalizer.write (element, "classCloudScopeName",      classCloudScopeName);

    JDOMExternalizer.write (element, "hightlightCyclicEdges",    hightlightCyclicEdges);

    JDOMExternalizer.write (element, "thumbnailHeight",          thumbnailHeight);
    JDOMExternalizer.write (element, "saveThumbnails",           saveThumbnails);

    Map<String, String> map = new HashMap<String, String> ();
    for (Object collectionClass : collectionClasses)
    {
      map.put ((String) collectionClass, "");
    }
    JDOMExternalizer.writeMap (element, map, "collectionClasses", "collection");

    JDOMExternalizer.write (element, "gridSize", gridSize);
  }
}
