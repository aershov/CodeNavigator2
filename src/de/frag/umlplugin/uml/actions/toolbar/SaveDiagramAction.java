package de.frag.umlplugin.uml.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.graphio.SaveDialog;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import de.frag.umlplugin.uml.diagramio.DiagramFileType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

/**
 * Opens persistent diagram.
 */
public class SaveDiagramAction extends AnAction
{
  /**
   * Updates the state of the action.
   * @param e Carries information on the invocation place and data available
   */
  public void update (AnActionEvent e)
  {
    Presentation presentation = e.getPresentation ();
    boolean selectable = false;
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null)
    {
      UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
      selectable = umlDiagram != null;
    }
    presentation.setEnabled (selectable);
  }

  /**
   * Shows current element under cursor or mouse in graphical navigator.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    final Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams != null)
    {
      final UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
      if (umlDiagram != null)
      {
        final SaveDialog saveDialog = new SaveDialog (project, "Save diagram to file", "Choose diagram folder.",
                                                      "Select folder for saving diagram file.", "UMLDiagramPath",
                                                      DiagramFileType.DIAGRAM, DiagramFileType.DIAGRAMS,
                                                      DiagramFileType.GIF,     DiagramFileType.JPG,
                                                      DiagramFileType.PNG,     DiagramFileType.SVG,
                                                      DiagramFileType.GRAPHML);
        saveDialog.show (new Runnable () {
          public void run ()
          {
            String          path            = saveDialog.getPath ();
            DiagramFileType fileType        = (DiagramFileType) saveDialog.getFileType ();
            boolean         saveThumbnail   = saveDialog.saveThumbnail ();
            int             thumbnailHeight = saveDialog.getThumbnailHeight ();
            if (saveDiagram (path, fileType, project, saveThumbnail, thumbnailHeight))
            {
              saveDialog.close ();
            }
          }
        });
      }
    }
  }

  /**
   * Saves diagram to file.
   * @param path path to target file
   * @param fileType file type
   * @param project project
   * @param saveThumbnail true, if thumbnail should be saved; false otherwise
   * @param thumbnailHeight thumbnail height in pixels
   * @return true, if diagram was saved; false if it was aborted
   */
  private boolean saveDiagram (@NotNull String path, @NotNull DiagramFileType fileType, @NotNull Project project,
                               boolean saveThumbnail, int thumbnailHeight)
  {
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams == null)
    {
      return false;
    }
    path = fileType.appendExtension (path);
    if (new File (path).exists ())
    {
      int answer = Messages.showYesNoDialog (project, "File already exists. Overwrite existing file?",
                                             "File exists", Messages.getQuestionIcon ());
      if (answer == 1)
      {
        return false;
      }
    }
    try
    {
      if (fileType.canWriteMultipleDiagrams ())
      {
        List<UMLDiagram> diagramList = new ArrayList<UMLDiagram> ();
        for (UMLDiagram diagram : diagrams)
        {
          diagramList.add (diagram);
        }
        fileType.writeDiagram (diagramList.toArray (new UMLDiagram [diagramList.size ()]), path, saveThumbnail, 
                               thumbnailHeight);
      }
      else
      {
        final UMLDiagram umlDiagram = diagrams.getCurrentDiagram ();
        fileType.writeDiagram (new UMLDiagram [] {umlDiagram}, path, saveThumbnail, thumbnailHeight);
      }
      return true;
    }
    catch (IOException ex)
    {
      Messages.showErrorDialog (project, "Could not write file, error: " + ex.getMessage (),
                                "Error while saving file");
      return false;
    }
  }
}