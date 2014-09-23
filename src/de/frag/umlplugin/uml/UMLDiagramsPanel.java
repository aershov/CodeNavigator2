package de.frag.umlplugin.uml;

import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Swing component that contains several uml diagrams.
 */
public class UMLDiagramsPanel extends JPanel implements Iterable<UMLDiagram>
{
  private final Map<String, UMLDiagram> diagrams = new HashMap<String, UMLDiagram> ();
  private       String                  currentDiagramName;
  private final DiagramComboBoxModel    diagramComboBoxModel;
  private final JComboBox               diagramComboBox;

  /**
   * Creates a new diagrams panel.
   */
  public UMLDiagramsPanel ()
  {
    super (new CardLayout ());
    diagramComboBoxModel = new DiagramComboBoxModel ();
    diagramComboBox      = new JComboBox (diagramComboBoxModel);
  }

  /**
   * Gets combo box for switching between diagrams.
   * @return switcher combo box
   */
  public @NotNull JComboBox getDiagramComboBox ()
  {
    return diagramComboBox;
  }

  /**
   * Adds a new diagram.
   * @param diagram diagram to add
   */
  public void addDiagram (@NotNull UMLDiagram diagram)
  {
    diagrams.put (diagram.getName (), diagram);
    diagramComboBoxModel.diagramsChanged ();
    add (diagram.getView ().getJComponent (), diagram.getName ());
    switchToDiagram (diagram.getName ());
  }

  /**
   * Gets UML diagram with given name.
   * @param diagramName name of diagram
   * @return found diagram or null, if diagram could not be found
   */
  public @Nullable UMLDiagram getUMLDiagram (@Nullable String diagramName)
  {
    return diagrams.get (diagramName);
  }

  /**
   * Creates iterator that iterates over all contained UML diagrams.
   * @return diagram iterator
   */
  public @NotNull Iterator<UMLDiagram> iterator ()
  {
    List<UMLDiagram> diagramCollection = new ArrayList<UMLDiagram> (diagrams.values ());
    Collections.sort (diagramCollection);
    return diagramCollection.iterator ();
  }

  /**
   * Switches to given diagram.
   * @param diagramName name of diagram to switch to
   */
  public void switchToDiagram (@NotNull String diagramName)
  {
    if (currentDiagramName == null || !currentDiagramName.equals (diagramName))
    {
      UMLDiagram umlDiagram = getUMLDiagram (diagramName);
      if (umlDiagram != null)
      {
        currentDiagramName = diagramName;
        diagramComboBoxModel.setSelectedItem (umlDiagram);
        CardLayout cardlayout = (CardLayout) getLayout ();
        cardlayout.show (this, diagramName);
        umlDiagram.getView ().updateView ();
      }
    }
  }

  /**
   * Gets current diagram.
   * @return current diagram
   */
  public @Nullable UMLDiagram getCurrentDiagram ()
  {
    return getUMLDiagram (currentDiagramName);
  }

  /**
   * Checks whether diagram with given name already exists.
   * @param diagramName diagram name
   * @return true, if diagram with given name already exists; false otherwise
   */
  public boolean isExistingDiagram (@NotNull String diagramName)
  {
    return diagrams.containsKey (diagramName);
  }

  /**
   * Renames given diagram.
   * @param diagram diagram to rename
   * @param newName new name for diagram
   */
  public void renameDiagram (@NotNull UMLDiagram diagram, @NotNull String newName)
  {
    CardLayout cardlayout = (CardLayout) getLayout ();
    remove (diagram.getView ().getJComponent ());
    add (diagram.getView ().getJComponent (), newName);
    cardlayout.show (this, newName);

    diagrams.remove (diagram.getName ());
    diagrams.put (newName, diagram);
    if (diagram.getName ().equals (currentDiagramName))
    {
      currentDiagramName = newName;
    }
    diagram.setName (newName);
    diagramComboBoxModel.diagramsChanged ();
  }

  /**
   * Closes given diagram.
   * @param diagram diagram to close.
   */
  public void closeDiagram (@NotNull UMLDiagram diagram)
  {
    diagrams.remove (diagram.getName ());
    remove (diagram.getView ().getJComponent ());
    diagramComboBoxModel.diagramsChanged ();
    if (diagrams.isEmpty ())
    {
      currentDiagramName = null;
    }
    else
    {
      String newCurrentDiagram = diagrams.keySet ().iterator ().next ();
      switchToDiagram (newCurrentDiagram);
    }
  }

  /**
   * Writes all diagrams to given JDOM element.
   * @param element element to write diagrams to
   */
  public void writeExternal (@NotNull Element element)
  {
    for (UMLDiagram diagram : diagrams.values ())
    {
      Element diagramElement = diagram.createElement ();
      element.addContent (diagramElement);
    }
  }

  /**
   * Reads all diagrams from given JDOM element.
   * @param project current project
   * @param element element to read diagrams from
   */
  @SuppressWarnings ({"unchecked"})
  public void readExternal (@NotNull Project project, @NotNull Element element)
  {
    List<Element> diagramElements = element.getChildren (UMLDiagram.DIAGRAM_ELEMENT_NAME);
    for (Element diagramElement : diagramElements)
    {
      UMLDiagram diagram = new UMLDiagram (project, diagramElement);
      addDiagram (diagram);
    }
  }

  /**
   * Combo box model that acts as adapter between diagram collection and combo box.
   */
  private class DiagramComboBoxModel extends AbstractListModel implements ComboBoxModel
  {
    public int getSize ()
    {
      return diagrams.size ();
    }

    public @NotNull Object getElementAt (int index)
    {
      UMLDiagram [] umlDiagrams = diagrams.values ().toArray (new UMLDiagram [diagrams.size ()]);
      Arrays.sort (umlDiagrams);
      return umlDiagrams [index];
    }

    public void setSelectedItem (@NotNull Object anItem)
    {
      UMLDiagram diagram = (UMLDiagram) anItem;
      switchToDiagram (diagram.getName ());
      fireContentsChanged (this, -1, -1);
    }

    public @Nullable Object getSelectedItem ()
    {
      return getUMLDiagram (currentDiagramName);
    }

    public void diagramsChanged ()
    {
      fireContentsChanged (this, 0, getSize ());
    }
  }
}
