package de.frag.umlplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides project related utility methods.
 */
public class ProjectUtils
{
  private static final Map<Class, Key> keys = new HashMap<Class, Key> ();

  private ProjectUtils () {}

  /**
   * Gets object for given project.
   * @param project project to get object for
   * @param type type of object to get
   * @return object of given type that is associated to specified project
   */
  public static @Nullable <T> T get (@Nullable Project project, @NotNull Class<T> type)
  {
    if (project == null)
    {
      return null;
    }
    Key<T> key = getKey (type);
    return project.getUserData (key);
  }

  /**
   * Sets object for given project.
   * @param project project to set object for
   * @param object object of given type that will be associated to specified project
   */
  @SuppressWarnings ({"unchecked"})
  public static <T> void set (@NotNull  Project project, @NotNull T object)
  {
    set (project, object, (Class<T>) object.getClass ());
  }

  /**
   * Sets object for given project.
   * @param project project to set object for
   * @param object of given type that is associated to specified project
   * @param keyClass class that acts as key for setting/getting value
   */
  public static <T> void set (@NotNull Project project, @Nullable T object, @NotNull Class<T> keyClass)
  {
    Key<T> key = getKey (keyClass);
    project.putUserData (key, object);
  }

  /**
   * Removes object from given project.
   * @param project project to remove object from
   * @param object of given type that is associated to specified project
   */
  @SuppressWarnings ({"unchecked"})
  public static <T> void remove (@NotNull Project project, @NotNull T object)
  {
    Key<T> key = getKey ((Class<T>) object.getClass ());
    project.putUserData (key, null);
  }

  /**
   * Gets key for given type.
   * @param type type to get key for
   * @return key for given type
   */
  @SuppressWarnings ({"unchecked"})
  private static @NotNull <T> Key<T> getKey (@NotNull Class<T> type)
  {
    Key<T> key = keys.get (type);
    if (key == null)
    {
      key = Key.create (type.getName ());
      keys.put (type, key);
    }
    return key;
  }
}
