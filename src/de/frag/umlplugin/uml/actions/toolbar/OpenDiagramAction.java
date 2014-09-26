package de.frag.umlplugin.uml.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Opens persistent diagram.
 */
public class OpenDiagramAction extends AnAction
{
  /**
   * Shows current element under cursor or mouse in graphical navigator.
   * @param e Carries information on the invocation place
   */
  @SuppressWarnings ({"unchecked"})
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null && project != null)
    {
      FileChooserDescriptor descriptor = createDescriptor ();
//      VirtualFile [] files = FileChooser.chooseFiles(project, descriptor);
      VirtualFile [] files = FileChooser.chooseFiles(descriptor, project, null);
      for (VirtualFile file : files)
      {
        loadDiagram (project, diagrams, file);
      }
    }
  }

  /**
   * Loads diagram from given file.
   * @param project current project
   * @param diagrams diagrams panel to load diagram to
   * @param file file to load as diagram
   */
  @SuppressWarnings ({"unchecked"})
  private void loadDiagram (@NotNull Project project, @NotNull UMLDiagramsPanel diagrams, @NotNull VirtualFile file)
  {
    try
    {
      SAXBuilder builder = new SAXBuilder ();
      Document document = builder.build (file.getPath ());
      Element diagramsElement = document.getRootElement ();
      for (Element diagramElement : (List<Element>) diagramsElement.getChildren ("diagram"))
      {
        UMLDiagram diagram = new UMLDiagram (project, diagramElement);
        addDiagram (project, diagrams, diagram);
      }
    }
    catch (Exception ex)
    {
      Messages.showErrorDialog (project, "Error while loading diagram(s): " + ex.getMessage (), "Error");
    }
  }

  /**
   * Adds given diagram to list of open diagrams.
   * @param project project
   * @param diagrams diagram list to add new diagram to
   * @param diagram diagram to add
   */
  private void addDiagram (@NotNull Project project, @NotNull UMLDiagramsPanel diagrams, @NotNull UMLDiagram diagram)
  {
    String name = diagram.getName ();
    if (diagrams.isExistingDiagram (name))
    {
      String message = "Diagram with name '" + diagram.getName () + "' already exists. Please choose action.";
      int answer = Messages.showDialog (project, message, "Duplicate diagram name",
                                              new String[] {"Replace", "Skip", "Rename"}, 2,
                                              Messages.getQuestionIcon ());
      if (answer == 1) // skip
      {
        return;
      }
      else if (answer == 2) // rename
      {
        int suffix = 2;
        while (diagrams.isExistingDiagram (name + " " + suffix))
        {
          suffix++;
        }
        diagram.setName (name + " " + suffix);
      }
      else // replace
      {
        UMLDiagram existingDiagram = diagrams.getUMLDiagram (diagram.getName ());
        if (existingDiagram != null)
        {
          diagrams.closeDiagram (existingDiagram);
        }
      }
    }
    diagrams.addDiagram (diagram);
    diagram.refreshDiagram ();
    diagram.doLayout (true);
  }

  /**
   * Creates file chooser descriptor for opening diagram xml files.
   * @return created descriptor
   */
  private @NotNull FileChooserDescriptor createDescriptor ()
  {
    FileChooserDescriptor descriptor = new FileChooserDescriptor (true, false, false, false, false, false)
    {
      public boolean isFileVisible (VirtualFile file, boolean showHiddenFiles)
      {
        boolean b = super.isFileVisible (file, showHiddenFiles);
        if (!file.isDirectory())
        {
          b &= StdFileTypes.XML.equals (FileTypeManager.getInstance ().getFileTypeByFile (file));
        }
        return b;
      }
    };
    descriptor.setDescription ("Both files containing single diagram and files containing multiple diagrams can be loaded.");
//    descriptor.setNewFileType (StdFileTypes.XML);
    descriptor.setTitle ("Choose diagram file to open");
    return descriptor;
  }
}