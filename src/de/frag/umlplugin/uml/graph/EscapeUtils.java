package de.frag.umlplugin.uml.graph;

import org.jetbrains.annotations.NotNull;

/**
 * Escapes HTML or XML special characters.
 */
public class EscapeUtils
{
  /**
   * Escapes all special HTML characters and returns resulting string.
   * @param string string to escape
   * @return escaped string
   */
  public static @NotNull String escape (@NotNull String string)
  {
    StringBuilder escaped = new StringBuilder ();
    for (int i = 0; i < string.length (); i++)
    {
      char c = string.charAt (i);
      switch (c)
      {
        case '<': escaped.append ("&lt;"); break;
        case '>': escaped.append ("&gt;"); break;
        case '&': escaped.append ("&amp;"); break;
        case '"': escaped.append ("&quot;"); break;
        default: escaped.append (c);
      }
    }
    return escaped.toString ();
  }
}
