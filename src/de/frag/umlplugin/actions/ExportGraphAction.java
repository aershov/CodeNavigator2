package de.frag.umlplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.graph.view.Graph2DView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import de.frag.umlplugin.Graph2DViewConsumer;
import de.frag.umlplugin.Graph2DViewProvider;
import de.frag.umlplugin.settings.Settings;
import de.frag.umlplugin.graphio.GraphFileType;
import de.frag.umlplugin.graphio.SaveDialog;
import de.frag.umlplugin.graphio.ImageScaler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * Exports graphs to files (to images for example).
 */
public class ExportGraphAction extends AnAction implements Graph2DViewConsumer
{
  private Graph2DViewProvider graph2DViewProvider;

  /**
   * Sets the graph view provider that provides access to graph views.
   * @param graph2DViewProvider new provider
   */
  public void setGraph2DViewProvider (@NotNull Graph2DViewProvider graph2DViewProvider)
  {
    this.graph2DViewProvider = graph2DViewProvider;
  }

  /**
   * Open dialog to select file format and export graph to selected file.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    final Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    if (graph2DViewProvider != null)
    {
      Graph2DView graph2DView = graph2DViewProvider.getGraph2DView (e.getDataContext ());
      if (graph2DView != null)
      {
        final Graph2D graph = graph2DView.getGraph2D ();
        final SaveDialog saveDialog = new SaveDialog (project, "Save graph to file", "Choose folder.",
                                                      "Select folder for saving file.", "GraphPath",
                                                      GraphFileType.GIF, GraphFileType.JPG,
                                                      GraphFileType.PNG, GraphFileType.SVG);
        saveDialog.show (new Runnable () {
          public void run ()
          {
            String        path            = saveDialog.getPath ();
            GraphFileType fileType        = (GraphFileType) saveDialog.getFileType ();
            boolean       saveThumbnail   = saveDialog.saveThumbnail ();
            int           thumbnailHeight = saveDialog.getThumbnailHeight ();
            if (saveGraph (project, graph, path, fileType, saveThumbnail, thumbnailHeight))
            {
              saveDialog.close ();
            }
          }
        });
      }
    }
  }

  /**
   * Saves graph to file.
   * @param project project
   * @param graph graph to save
   * @param path path to target file
   * @param fileType file type
   * @param saveThumbnail true, if thumbnail should be saved; false otherwise
   * @param thumbnailHeight thumbnail height in pixels
   * @return true, if graph was saved; false if it was aborted
   */
  private boolean saveGraph (@NotNull Project project, @NotNull Graph2D graph, @NotNull String path,
                             @NotNull GraphFileType fileType, boolean saveThumbnail, int thumbnailHeight)
  {
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
      fileType.writeGraph (graph, path, saveThumbnail, thumbnailHeight);
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
