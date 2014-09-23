package de.frag.umlplugin.uml;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores compartment visibility information for a single UML diagram.
 */
public class CompartmentVisibility
{
  public  static final String COMPARTMENT_VISIBILITY = "compartment-visibility";
  private static final String SHOW_FIELDS            = "show-fields";
  private static final String SHOW_METHODS           = "show-methods";
  private static final String CLASS                  = "class";

  private final Set<String> classesWithVisibleFields  = new HashSet<String> ();
  private final Set<String> classesWithVisibleMethods = new HashSet<String> ();

  /**
   * Creates new compartment visibility. Initially all field- and method-compartments will be invisible.
   */
  public CompartmentVisibility ()
  {
  }

  /**
   * Creates new compartment visibility. Visibility information will be deserialized from given JDOM element.
   * @param visibilityElement JDOM element with serialized visibility information
   */
  @SuppressWarnings ({"unchecked"})
  public CompartmentVisibility (@NotNull Element visibilityElement)
  {
    List<Element> childElements = visibilityElement.getChildren ();
    for (Element childElement : childElements)
    {
      if (childElement.getName ().equals (SHOW_FIELDS))
      {
        classesWithVisibleFields.add (childElement.getAttributeValue (CLASS));
      }
      else if (childElement.getName ().equals (SHOW_METHODS))
      {
        classesWithVisibleMethods.add (childElement.getAttributeValue (CLASS));
      }
      else
      {
        throw new IllegalArgumentException ("unknown element type: " + childElement);
      }
    }
  }

  /**
   * Sets field compartment visibilty for given class.
   * @param qualifiedClassName qualified name of class
   * @param visible true, if fields should be visible; false otherwise
   */
  public void setFieldsVisible (@Nullable String qualifiedClassName, boolean visible)
  {
    if (qualifiedClassName != null)
    {
      if (visible)
      {
        classesWithVisibleFields.add (qualifiedClassName);
      }
      else
      {
        classesWithVisibleFields.remove (qualifiedClassName);
      }
    }
  }

  /**
   * Sets method compartment visibilty for given class.
   * @param qualifiedClassName qualified name of class
   * @param visible true, if methods should be visible; false otherwise
   */
  public void setMethodsVisible (@Nullable String qualifiedClassName, boolean visible)
  {
    if (qualifiedClassName != null)
    {
      if (visible)
      {
        classesWithVisibleMethods.add (qualifiedClassName);
      }
      else
      {
        classesWithVisibleMethods.remove (qualifiedClassName);
      }
    }
  }

  /**
   * Checks whether fields are visible for given class.
   * @param qualifiedClassName qualified name of class
   * @return true, if fields are visible; false otherwise
   */
  public boolean isFieldsVisible (@Nullable String qualifiedClassName)
  {
    return classesWithVisibleFields.contains (qualifiedClassName);
  }

  /**
   * Checks whether methods are visible for given class.
   * @param qualifiedClassName qualified name of class
   * @return true, if methods are visible; false otherwise
   */
  public boolean isMethodsVisible (@Nullable String qualifiedClassName)
  {
    return classesWithVisibleMethods.contains (qualifiedClassName);
  }

  /**
   * Toggles visibility of fields.
   * @param qualifiedClassName target class
   */
  public void toggleFieldsVisible (@Nullable String qualifiedClassName)
  {
    if (qualifiedClassName != null)
    {
      setFieldsVisible (qualifiedClassName, !isFieldsVisible (qualifiedClassName));
    }
  }

  /**
   * Toggles visibility of methods.
   * @param qualifiedClassName target class
   */
  public void toggleMethodsVisible (@Nullable String qualifiedClassName)
  {
    if (qualifiedClassName != null)
    {
      setMethodsVisible (qualifiedClassName, !isMethodsVisible (qualifiedClassName));
    }
  }

  /**
   * Toggles visibility of fields and methods.
   * @param qualifiedClassName target class
   */
  public void toggleFieldsAndMethodsVisible (@Nullable String qualifiedClassName)
  {
    toggleFieldsVisible (qualifiedClassName);
    toggleMethodsVisible (qualifiedClassName);
  }

  /**
   * Creates JDOM element that contains a serialized description of this compartment visibility object.
   * @return created JDOM element
   */
  public @NotNull Element createElement ()
  {
    Element visibilityElement = new Element (COMPARTMENT_VISIBILITY);
    for (String classWithVisibleField : classesWithVisibleFields)
    {
      Element fieldElement = new Element (SHOW_FIELDS);
      fieldElement.setAttribute (CLASS, classWithVisibleField);
      visibilityElement.addContent (fieldElement);
    }
    for (String classWithVisibleMethod : classesWithVisibleMethods)
    {
      Element methodElement = new Element (SHOW_METHODS);
      methodElement.setAttribute (CLASS, classWithVisibleMethod);
      visibilityElement.addContent (methodElement);
    }
    return visibilityElement;
  }

  /**
   * Rename all classes in all compartment visibility sets to keep class names in sync after
   * refactorings were applied.
   * @param renamer renamer to use for renaming class names
   */
  public void renameClasses (@NotNull ClassRenamer renamer)
  {
    renameClasses (classesWithVisibleFields,  renamer);
    renameClasses (classesWithVisibleMethods, renamer);
  }

  /**
   * Renames all classes in given set.
   * @param classNameSet classes to rename
   * @param renamer renamer that will rename all classes in given set
   */
  private void renameClasses (@NotNull Set<String> classNameSet, @NotNull ClassRenamer renamer)
  {
    List<String> classNames = new ArrayList<String> (classNameSet);
    classNameSet.clear ();
    for (String className : classNames)
    {
      classNameSet.add (renamer.rename (className));
    }
  }
}
