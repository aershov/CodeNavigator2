package de.frag.umlplugin.uml.command;

import de.frag.umlplugin.psi.UsageType;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates diagram command objects from JDOM elements.
 */
@SuppressWarnings ({"MismatchedQueryAndUpdateOfCollection", "unchecked"})
public class DiagramCommandFactory
{
  /**
   * Creates command from given JDOM element.
   * @param commandElement element to create command from
   * @return created command or null, if command could not be created
   */
  public static DiagramCommand createCommand (@NotNull Element commandElement)
  {
    try
    {
      String commandClassName = commandElement.getAttributeValue (AbstractDiagramCommand.COMMAND_CLASS);
      Class<DiagramCommand> commandClass = (Class<DiagramCommand>) Class.forName (commandClassName);
      List<Attribute> attributes = (List<Attribute>) commandElement.getAttributes ();
      attributes.remove (0);  // removes command-class attribute

      if (attributes.isEmpty ())
      {
        return commandClass.newInstance ();
      }
      else
      {
        List<Class<?>> parameterTypes = collectParameterTypes (attributes);
        Constructor<DiagramCommand> constructor =
                commandClass.getConstructor (parameterTypes.toArray (new Class<?>[attributes.size () - 1]));

        // collect paramaters
        List<Object> parameters = collectParameters (attributes);
        return constructor.newInstance (parameters.toArray (new Object[attributes.size () - 1]));
      }
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException ("could not create command", e);
    }
  }

  /**
   * Collects parameter types from given JDOM attributes.
   * @param attributes attributes to get parameter types from
   * @return collected parameter types
   */
  private static @NotNull List<Class<?>> collectParameterTypes (@NotNull List<Attribute> attributes)
  {
    List<Class<?>> parameterTypes = new ArrayList<Class<?>> ();
    for (Attribute attribute : attributes)
    {
      String attributeName = attribute.getName ();
      if (attributeName.equals (AbstractDiagramCommand.PSI_CLASS) ||
          attributeName.equals (AbstractDiagramCommand.SOURCE_CLASS) ||
          attributeName.equals (AbstractDiagramCommand.TARGET_CLASS))
      {
        parameterTypes.add (String.class);
      }
      else if (attributeName.equals (AbstractDiagramCommand.USAGE_TYPE))
      {
        parameterTypes.add (UsageType.class);
      }
      else
      {
        throw new IllegalArgumentException ("unknown parameter type for command creation: " + attributeName);
      }
    }
    return parameterTypes;
  }

  /**
   * Collects parameters from given JDOM attributes.
   * @param attributes attributes to get parameters from
   * @return collected parameters
   */
  private static @NotNull List<Object> collectParameters (@NotNull List<Attribute> attributes)
  {
    List<Object> parameters = new ArrayList<Object> ();
    for (Attribute attribute : attributes)
    {
      String attributeName = attribute.getName ();
      if (attributeName.equals (AbstractDiagramCommand.PSI_CLASS) ||
          attributeName.equals (AbstractDiagramCommand.SOURCE_CLASS) ||
          attributeName.equals (AbstractDiagramCommand.TARGET_CLASS))
      {
        parameters.add (attribute.getValue ());
      }
      else if (attributeName.equals (AbstractDiagramCommand.USAGE_TYPE))
      {
        UsageType usageType = UsageType.valueOf (attribute.getValue ());
        parameters.add (usageType);
      }
      else
      {
        throw new IllegalArgumentException ("unknown parameter type for command creation: " + attributeName);
      }
    }
    return parameters;
  }
}
