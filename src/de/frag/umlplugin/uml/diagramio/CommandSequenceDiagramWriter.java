package de.frag.umlplugin.uml.diagramio;

import de.frag.umlplugin.uml.UMLDiagram;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes diagram as sequence of diagram creation commands.
 */
public class CommandSequenceDiagramWriter implements DiagramWriter
{
  private final boolean multipleDiagramWriter;

  /**
   * Creates new command writer.
   * @param multipleDiagramWriter true, if this writer should write multiple diagrams; false otherwise
   */
  public CommandSequenceDiagramWriter (boolean multipleDiagramWriter)
  {
    this.multipleDiagramWriter = multipleDiagramWriter;
  }

  /**
   * Checks whether this writer can write multiple diagrams or not.
   * @return true, if this writer can write multiple diagrams; false, if it can write only one diagram
   */
  public boolean canWriteMultipleDiagrams ()
  {
    return multipleDiagramWriter;
  }

  /**
   * Checks whether this writer supports storage of image thumbnails.
   * @return true, if this writer can create thumbnail images; false otherwise
   */
  public boolean canWriteThumbnail ()
  {
    return false;
  }

  /**
   * Writes given diagram to file.
   * @param diagrams diagrams to write
   * @param path path to target file
   * @param saveThumbnail true, if thumbnail should be written; false otherwise
   * @param thumbnailHeight desired thumbnail height in pixels
   * @throws IOException on IO error
   */
  public void writeDiagram (@NotNull UMLDiagram[] diagrams, @NotNull String path, boolean saveThumbnail,
                            int thumbnailHeight) throws IOException
  {
    Element diagramElement = new Element ("diagrams");
    for (UMLDiagram diagram : diagrams)
    {
      diagramElement.addContent (diagram.createElement ());
    }
    Document document = new Document (diagramElement);

    XMLOutputter outputter = new XMLOutputter (Format.getPrettyFormat ());
    FileWriter writer = new FileWriter (path);
    outputter.output (document, writer);
    writer.close ();

  }
}
