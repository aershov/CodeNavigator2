package de.frag.umlplugin.uml.command;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for diagram commands.
 */
public abstract class AbstractDiagramCommand implements DiagramCommand
{
  public static final String COMMAND_ELEMENT_NAME = "command";
  public static final String COMMAND_CLASS        = "command-class";
  public static final String PSI_CLASS            = "psi-class";
  public static final String SOURCE_CLASS         = "source-class";
  public static final String TARGET_CLASS         = "target-class";
  public static final String USAGE_TYPE           = "usage-type";

  /**
   * Creates an element for this command. Sub classes can add additional information
   * by overriding method {@link #addAdditionalInfo(org.jdom.Element)}.
   * @return created element
   */
  public @NotNull Element createElement ()
  {
    Element commandElement = new Element (COMMAND_ELEMENT_NAME);
    commandElement.setAttribute (COMMAND_CLASS, getClass ().getName ());
    addAdditionalInfo (commandElement);
    return commandElement;
  }

  /**
   * Adds additional information (attributes or child elements) to given element.
   * @param commandElement command element to add additional information to
   */
  protected void addAdditionalInfo (@NotNull Element commandElement)
  {
    // does nothing - should be overridden in sub classes
  }
}
