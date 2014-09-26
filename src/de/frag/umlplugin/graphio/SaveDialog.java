package de.frag.umlplugin.graphio;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import static de.frag.umlplugin.guilayout.BuilderConstants.*;
import de.frag.umlplugin.guilayout.LayoutBuilder;
import de.frag.umlplugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Dialog for selecting save format and target file.
 */
public class SaveDialog
{
  private static final Map<String, Key<String>> pathKeys = new HashMap<String, Key<String>> ();

  private final DialogBuilder dialogBuilder;
  private String              path;
  private FileType            fileType;
  private Runnable            saveAction;
  private final JCheckBox     saveThumbnailsCheckBox = new JCheckBox ("Save thumbnail");
  private final JSpinner      thumbnailHeight        = new JSpinner  (new SpinnerNumberModel (40, 10, 80, 5));
  private final JLabel        thumbnailHeightLabel   = new JLabel    ("Thumbnail height:");

  /**
   * Shows current element under cursor or mouse in graphical navigator.
   * @param project current project
   * @param title dialog title
   * @param browseLabel label for path browse button
   * @param browseDetails details for path browse button and field
   * @param pathKey key for storing selected base path, so when user opens dialog next time, the selected
   *        path is already inserted as base path
   * @param fileTypes file types to choose from
   */
  public SaveDialog (@NotNull final Project project, @NotNull String title, @NotNull String browseLabel,
                     @NotNull String browseDetails, @NotNull final String pathKey, @NotNull FileType ... fileTypes)
  {
    dialogBuilder = new DialogBuilder (project);
    dialogBuilder.setTitle (title);
    DialogBuilder.CustomizableAction okAction = dialogBuilder.addOkAction ();
    okAction.setText ("Save");
    dialogBuilder.addCancelAction ();

    Settings settings = Settings.getSettings ();
    thumbnailHeight.getModel ().setValue (settings.getThumbnailHeight ());
    saveThumbnailsCheckBox.setSelected (settings.isSaveThumbnails ());

    final TextFieldWithBrowseButton chooseFolderField = createTextFieldWithBrowseButton (project, pathKey,
                                                                                         browseLabel, browseDetails);
    final JTextField fileNameField    = createFileNameField ();
    final JLabel     infoLabel        = new JLabel ();
    final JComboBox  fileTypeComboBox = createFileTypeComboBox (infoLabel, fileTypes);
    attachDocumentListener (dialogBuilder, chooseFolderField, fileNameField);

    JPanel centerPanel = createCenterPanel (chooseFolderField, fileNameField, infoLabel, fileTypeComboBox);

    dialogBuilder.setOkActionEnabled (false);
    dialogBuilder.setCenterPanel (centerPanel);
    dialogBuilder.setOkOperation (new Runnable () {
      public void run ()
      {
        String file = fileNameField.getText ();
        path = chooseFolderField.getText () + File.separator + file;
        fileType = (FileType) fileTypeComboBox.getSelectedItem ();
        project.putUserData (createPathKey (pathKey), chooseFolderField.getText ());
        boolean       saveThumbnail   = SaveDialog.this.saveThumbnail ();
        int           thumbnailHeight = SaveDialog.this.getThumbnailHeight ();
        Settings settings = Settings.getSettings ();
        settings.setThumbnailHeight (thumbnailHeight);
        settings.setSaveThumbnails  (saveThumbnail);
        if (saveAction != null)
        {
          saveAction.run ();
        }
      }
    });
    dialogBuilder.setPreferredFocusComponent(fileNameField);
  }

  /**
   * Creates a new path key for storing selection path between calls to the save dialog.
   * @param pathKey path key to create key for
   * @return created or cached key
   */
  private @NotNull Key<String> createPathKey (@NotNull String pathKey)
  {
    Key<String> key = pathKeys.get (pathKey);
    if (key == null)
    {
      key = new Key<String> (pathKey);
      pathKeys.put (pathKey, key);
    }
    return key;
  }

  /**
   * Shows dialog.
   * @param saveAction runnable to execute if user selected save-button
   */
  public void show (@NotNull Runnable saveAction)
  {
    this.saveAction = saveAction;
    dialogBuilder.show ();
  }

  /**
   * Closes this dialog.
   */
  public void close ()
  {
    dialogBuilder.getDialogWrapper ().close (0);
  }

  /**
   * Gets selected target save path.
   * @return target path
   */
  public @NotNull String getPath ()
  {
    return path;
  }

  /**
   * Gets selected file type.
   * @return selected file type
   */
  public @NotNull FileType getFileType ()
  {
    return fileType;
  }

  /**
   * Checks whether a thumbnail image should be saved.
   * @return true, if thumbnail image should be saved; false otherwise
   */
  public boolean saveThumbnail ()
  {
    return saveThumbnailsCheckBox.isSelected ();
  }

  /**
   * Gets thumbnail height to use for thumbnail images.
   * @return thumbnail height in pixels
   */
  public int getThumbnailHeight ()
  {
    return ((SpinnerNumberModel) thumbnailHeight.getModel ()).getNumber ().intValue ();
  }

  /**
   * Creates document listener that checks whether selected folder and file are valid and
   * thus enables or disables the save button and attaches it to folder and file selection fields.
   * @param dialogBuilder dialog builder
   * @param chooseFolderField folder selection field
   * @param fileNameField file selection field
   */
  private void attachDocumentListener (@NotNull final DialogBuilder dialogBuilder,
                                       @NotNull final TextFieldWithBrowseButton chooseFolderField,
                                       @NotNull final JTextField fileNameField)
  {
    DocumentListener documentListener = new DocumentAdapter()
    {
      protected void textChanged (DocumentEvent e)
      {
        String path = chooseFolderField.getText ();
        String file = fileNameField.getText ();
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance ();
        VirtualFile virtualFile = localFileSystem.findFileByPath (path);
        boolean selectable = virtualFile != null && virtualFile.isDirectory () && !"".equals (file);
        dialogBuilder.setOkActionEnabled (selectable);
      }
    };
    chooseFolderField.getTextField ().getDocument ().addDocumentListener (documentListener);
    fileNameField.getDocument ().addDocumentListener (documentListener);
  }

  /**
   * Creates center panel with layouted fields.
   * @param chooseFolderField folder selection field
   * @param fileNameField file selection field
   * @param infoLabel file type description label
   * @param fileTypeComboBox file type selection combo box
   * @return created panel with layouter components.
   */
  private @NotNull JPanel createCenterPanel (@NotNull TextFieldWithBrowseButton chooseFolderField,
                                             @NotNull JTextField fileNameField,
                                             @NotNull JLabel infoLabel,
                                             @NotNull JComboBox fileTypeComboBox)
  {
    LayoutBuilder builder = new LayoutBuilder ();
    builder.setDefaultInsets (new Insets (5, 5, 5, 5));
    JPanel centerPanel = builder.beginV ();
      builder.beginH (2);
        builder.add (new JLabel ("File Type:"), LABEL);
        builder.add (fileTypeComboBox,          STRETCH_X);
        builder.add (new JLabel ("Folder:"),    LABEL);
        builder.add (chooseFolderField,         STRETCH_X);
        builder.add (new JLabel ("File Name:"), LABEL);
        builder.add (fileNameField,             STRETCH_X);
      builder.end ();
      JPanel thumbnailPanel = builder.beginH ();
        builder.add (saveThumbnailsCheckBox, LABEL);
        builder.add (thumbnailHeightLabel,   LABEL);
        builder.add (thumbnailHeight,        STRETCH_X);
      builder.end ();
      builder.add (infoLabel, STRETCH_XY);
    builder.end ();
    thumbnailPanel.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                                                                "Thumbnails"));
    return centerPanel;
  }

  /**
   * Creates combo box for selection of file type.
   * @param infoLabel label for displaying file type description.
   * @param fileTypes file types to choose from
   * @return created combo box
   */
  private @NotNull JComboBox createFileTypeComboBox (@NotNull final JLabel infoLabel, @NotNull FileType [] fileTypes)
  {
    final JComboBox comboBox = new JComboBox (fileTypes);
    comboBox.addActionListener (new ActionListener()
    {
      public void actionPerformed (ActionEvent e)
      {
        FileType selectedFileType = (FileType) comboBox.getSelectedItem ();
        infoLabel.setText (selectedFileType.getDescription ());
        boolean canSaveThumbnails = selectedFileType.canWriteThumbnail ();
        saveThumbnailsCheckBox.setEnabled (canSaveThumbnails);
        thumbnailHeight.setEnabled        (canSaveThumbnails);
        thumbnailHeightLabel.setEnabled   (canSaveThumbnails);
      }
    });
    comboBox.setSelectedIndex (0);
    return comboBox;
  }

  /**
   * Creates text field for selection of target file.
   * @return created text field
   */
  private @NotNull JTextField createFileNameField ()
  {
    return new JTextField ();
  }

  /**
   * Creates text field with browse button for selecting target folder.
   * @param project project
   * @param pathKey key for storing selected path between calls to save dialogs
   * @param browseLabel label for path field
   * @param browseDetails detail description for path field
   * @return created folder text field
   */
  private @NotNull TextFieldWithBrowseButton createTextFieldWithBrowseButton (@NotNull Project project,
                                                                              @NotNull String pathKey,
                                                                              @NotNull String browseLabel,
                                                                              @NotNull String browseDetails)
  {
    final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor ();
    final TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton ();
    textFieldWithBrowseButton.addBrowseFolderListener (browseLabel, browseDetails, project, descriptor);
    textFieldWithBrowseButton.getTextField ().setColumns (40);
    String path = project.getUserData (createPathKey (pathKey));
    if (path == null)
    {
      VirtualFile baseDir = project.getBaseDir ();
      path = baseDir != null ? baseDir.getPath () : "";
    }
    textFieldWithBrowseButton.setText (path);
    return textFieldWithBrowseButton;
  }

}
