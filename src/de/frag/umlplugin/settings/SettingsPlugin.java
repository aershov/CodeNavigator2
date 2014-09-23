package de.frag.umlplugin.settings;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import de.frag.umlplugin.codenavigator.NavigatorProjectComponent;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Settings plugin application component. This class is responsible for handling plugin settings.
 */
@State (name     = SettingsPlugin.DISPLAY_NAME,
        storages = {@Storage (id   = SettingsPlugin.DISPLAY_NAME,
                              file = "$APP_CONFIG$/code-navigator.xml")})
public class SettingsPlugin implements ApplicationComponent, Configurable, PersistentStateComponent<Element>
{
  public static final String DISPLAY_NAME = "Code Navigator";

  private final Settings     settings;
  private final SettingsForm settingsForm;

  /**
   * Creates a new settings plugin.
   */
  public SettingsPlugin ()
  {
    this.settings     = new Settings ();
    this.settingsForm = new SettingsForm ();
  }

  /**
   * Gets settings.
   * @return settings
   */
  public @NotNull Settings getSettings ()
  {
    return settings;
  }

  public void initComponent ()
  {
  }

  public void disposeComponent ()
  {
  }

  public @NotNull String getComponentName ()
  {
    return "SettingsPlugin";
  }

  //--------------------------------------------------------------------------------------------------------
  //-------------------------------- Methods for implementing Configurable ---------------------------------
  //--------------------------------------------------------------------------------------------------------

  /**
   * Returns the user-visible name of the settings component.
   * @return the visible name of the component.
   */
  public @NotNull String getDisplayName ()
  {
    return DISPLAY_NAME;
  }

  /**
   * Returns the icon representing the settings component. Components shown in the IDEA settings dialog have 32x32
   * icons.
   * @return the icon for the component.
   */
  public @NotNull Icon getIcon ()
  {
    return IconLoader.getIcon ("/de/frag/umlplugin/icons/compass.png");
  }

  /**
   * Returns the topic in the help file which is shown when help for the configurable is requested.
   * @return the help topic, or null if no help is available.
   */
  public @Nullable String getHelpTopic ()
  {
    return null;
  }

  /**
   * Returns the user interface component for editing the configuration.
   * @return the component instance.
   */
  public @NotNull JComponent createComponent ()
  {
    settingsForm.readFromSettings (settings);
    return settingsForm.getRootPanel ();
  }

  /**
   * Checks if the settings in the user interface component were modified by the user and need to be saved.
   * @return true if the settings were modified, false otherwise.
   */
  public boolean isModified ()
  {
    return settingsForm.isModified (settings);
  }

  /**
   * Apply the settings changes from settings form to all dependent objects.
   */
  public void apply () throws ConfigurationException
  {
    ProjectManager projectManager = ProjectManager.getInstance ();
    Project[] projects = projectManager.getOpenProjects ();
    for (Project project : projects)
    {
      NavigatorProjectComponent[] projectComponents = project.getComponents (NavigatorProjectComponent.class);
      for (NavigatorProjectComponent projectComponent : projectComponents)
      {
        boolean reducedClassVisibility  = settingsForm.isReducedClassVisibility (settings);
        boolean modifiedClassVisibility = settingsForm.isModifiedClassVisibility (settings);
        settingsForm.writeToSettings (settings);
        projectComponent.applySettings (reducedClassVisibility, modifiedClassVisibility, settings);
      }
    }
  }

  /**
   * Load settings from other components to configurable.
   */
  public void reset ()
  {
    settingsForm.readFromSettings (settings);
  }

  /**
   * Disposes the Swing components used for displaying the configuration.
   */
  public void disposeUIResources ()
  {
  }

  //--------------------------------------------------------------------------------------------------------
  //--------------------------- Methods for implementing PersistentStateComponent --------------------------
  //--------------------------------------------------------------------------------------------------------

  /**
   * Writes settings from settings object to persistent storage object.
   * @return created JDOM element to be used for persistent storage
   */
  public @NotNull Element getState ()
  {
    Element settingsElement = new Element ("code-navigator-settings");
    settings.write (settingsElement);
    return settingsElement;
  }

  /**
   * Loads settings from given persistent storage JDOM element.
   * @param settingsElement settings element to read from
   */
  public void loadState (@NotNull Element settingsElement)
  {
    settings.read (settingsElement);
  }
}
