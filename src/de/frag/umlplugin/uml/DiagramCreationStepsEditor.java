package de.frag.umlplugin.uml;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import static de.frag.umlplugin.guilayout.BuilderConstants.*;
import de.frag.umlplugin.guilayout.LayoutBuilder;
import de.frag.umlplugin.history.HistoryList;
import de.frag.umlplugin.uml.command.DiagramCommand;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dialog for editing diagram creation steps.
 */
public class DiagramCreationStepsEditor
{
  private final DialogBuilder        builder;
  private final List<DiagramCommand> commandListBackup;
  private final UMLDiagram           diagram;
  private JList                      commandList;
  private JCheckBox                  previewCheckBox;
  private boolean                    changed = false;
  private JButton                    cropButton;
  private JButton                    upButton;
  private JButton                    downButton;
  private JButton                    deleteButton;
  private JButton                    undoButton;
  private final HistoryList<List<DiagramCommand>> editHistory = new HistoryList<List<DiagramCommand>> (50);

  /**
   * @param project current project.
   * @param diagram diagram with commands to be edited
   */
  public DiagramCreationStepsEditor (@NotNull Project project, @NotNull UMLDiagram diagram)
  {
    this.diagram = diagram;
    List<DiagramCommand> commands = diagram.getCommands ();
    commandListBackup = new ArrayList<DiagramCommand> (commands);

    builder = new DialogBuilder (project);
    builder.setTitle ("Edit diagram creation steps");
    builder.setCenterPanel (createCenterPanel (commands));
    builder.addOkAction ();
    builder.addCancelAction ();
  }

  /**
   * Shows this dialog.
   * @return true, if editor exited because of OK button; false if exited because of candel button
   */
  public boolean show ()
  {
    return builder.show () == 0;
  }

  /**
   * Checks whether at least one change was applied to list.
   * @return true, if at leats one change was applied; false otherwise
   */
  public boolean isChanged ()
  {
    return changed;
  }

  /**
   * Gets original list of commands.
   * @return list of commands before editing
   */
  public @NotNull List<DiagramCommand> getOriginalCommandList ()
  {
    return commandListBackup;
  }

  /**
   * Gets modified list of commands.
   * @return edited list of commands
   */
  public @NotNull List<DiagramCommand> getEditedCommandList ()
  {
    ListModel listModel = commandList.getModel ();
    List<DiagramCommand> commands = new ArrayList<DiagramCommand> ();
    for (int i = 0; i < listModel.getSize (); i++)
    {
      commands.add ((DiagramCommand) listModel.getElementAt (i));
    }
    return commands;
  }

  /**
   * Refreshed diagram with current list of commands.
   */
  private void refreshDiagram ()
  {
    if (previewCheckBox.isSelected () && changed)
    {
      diagram.setCommands (getEditedCommandList ());
      diagram.refreshDiagram ();
      diagram.doLayout ();
    }
    undoButton.setEnabled (editHistory.canStepBack ());
  }

  /**
   * Creates center panel.
   * @param commands list of commands to be edited
   * @return created panel with all components
   */
  private @NotNull JComponent createCenterPanel (@NotNull List<DiagramCommand> commands)
  {
    final CommandListModel commandListModel = new CommandListModel (commands);
    commandList = new JList (commandListModel);
    commandList.addListSelectionListener (new ListSelectionListener()
    {
      public void valueChanged (ListSelectionEvent e)
      {
        int [] indices = commandList.getSelectedIndices ();
        cropButton.setEnabled   (indices.length == 1);
        deleteButton.setEnabled (indices.length > 0);
        upButton.setEnabled     (indices.length > 0);
        downButton.setEnabled   (indices.length > 0);
      }
    });

    previewCheckBox = new JCheckBox ("Preview", true);
    previewCheckBox.addActionListener (new ActionListener()
    {
      public void actionPerformed (ActionEvent e)
      {
        refreshDiagram ();
      }
    });

    upButton = new JButton ("Up");
    upButton.addActionListener (new ActionListener()
    {
      public void actionPerformed (ActionEvent e)
      {
        int [] selectedIndices = commandList.getSelectedIndices ();
        commandListModel.moveUp (selectedIndices);
        for (int i = 0; i < selectedIndices.length; i++)
        {
          selectedIndices [i]--;
        }
        commandList.setSelectedIndices (selectedIndices);
        changed = true;
        refreshDiagram ();
      }
    });
    upButton.setEnabled (false);

    downButton = new JButton ("Down");
    downButton.addActionListener (new ActionListener()
    {
      public void actionPerformed (ActionEvent e)
      {
        int [] selectedIndices = commandList.getSelectedIndices ();
        commandListModel.moveDown (selectedIndices);
        for (int i = 0; i < selectedIndices.length; i++)
        {
          selectedIndices [i]++;
        }
        commandList.setSelectedIndices (selectedIndices);
        changed = true;
        refreshDiagram ();
      }
    });
    downButton.setEnabled (false);

    deleteButton = new JButton ("Delete");
    deleteButton.addActionListener (new ActionListener()
    {
      public void actionPerformed (ActionEvent e)
      {
        int [] selectedIndices = commandList.getSelectedIndices ();
        commandListModel.delete (selectedIndices);
        commandList.clearSelection ();
        changed = true;
        refreshDiagram ();
      }
    });
    deleteButton.setEnabled (false);

    cropButton = new JButton ("Crop");
    cropButton.addActionListener (new ActionListener()
    {
      public void actionPerformed (ActionEvent e)
      {
        int index = commandList.getSelectedIndex ();
        commandListModel.crop (index);
        changed = true;
        refreshDiagram ();
      }
    });
    cropButton.setEnabled (false);

    undoButton = new JButton ("Undo");
    undoButton.addActionListener (new ActionListener()
    {
      public void actionPerformed (ActionEvent e)
      {
        if (editHistory.canStepBack ())
        {
          List<DiagramCommand> commands = editHistory.stepBack ();
          commandListModel.restoreCommands (commands);
          refreshDiagram ();
        }
      }
    });
    undoButton.setEnabled (false);

    LayoutBuilder layout = new LayoutBuilder ();
    layout.setDefaultInsets (new Insets (4, 4, 4, 4));
    JPanel root = layout.beginV ();
      layout.add (new JLabel ("Diagram creation steps"), CENTER);
      layout.beginH ();
        layout.add (new JScrollPane (commandList), STRETCH_XY);
        layout.beginV ();
          layout.add (upButton,        MAX_X);
          layout.add (downButton,      MAX_X);
          layout.add (deleteButton,    MAX_X);
          layout.add (cropButton,      MAX_X);
          layout.add (undoButton,      MAX_X);
          layout.add (previewCheckBox, MAX_X);
        layout.end ();
      layout.end ();
    layout.end ();
    return root;
  }

  /**
   * Special list model for editing command list.
   */
  private class CommandListModel extends AbstractListModel
  {
    private final List<DiagramCommand> commands;

    /**
     * Creates new command list model that contains given commands.
     * @param commands initial list of contained commands
     */
    private CommandListModel (@NotNull List<DiagramCommand> commands)
    {
      this.commands = commands;
    }

    /**
     * Returns the length of the list.
     * @return the length of the list
     */
    public int getSize ()
    {
      return commands.size ();
    }

    /**
     * Returns the value at the specified index.
     * @param index the requested index
     * @return the value at <code>index</code>
     */
    public @NotNull Object getElementAt (int index)
    {
      return commands.get (index);
    }

    /**
     * Remembers current diagram creation steps for later undo.
     */
    private void rememberCommands ()
    {
      editHistory.add (new ArrayList<DiagramCommand> (commands));
    }

    /**
     * Restores command list.
     * @param commands new command list
     */
    public void restoreCommands (@NotNull List<DiagramCommand> commands)
    {
      this.commands.clear ();
      this.commands.addAll (commands);
      contentsChanged ();
    }

    /**
     * Fires contents changed event.
     */
    private void contentsChanged ()
    {
      fireContentsChanged (this, 0, commands.size () - 1);
    }

    /**
     * Moves commands with given indices one index up.
     * @param selectedIndices indices to move up
     */
    public void moveUp (@NotNull int [] selectedIndices)
    {
      rememberCommands ();
      Arrays.sort (selectedIndices);
      for (int index : selectedIndices)
      {
        if (index > 0)
        {
          DiagramCommand command = commands.remove (index - 1);
          commands.add (index, command);
        }
      }
      contentsChanged ();
    }

    /**
     * Moves commands with given indices one index down.
     * @param selectedIndices indices to move down
     */
    public void moveDown (@NotNull int [] selectedIndices)
    {
      rememberCommands ();
      Arrays.sort (selectedIndices);
      for (int i = selectedIndices.length - 1; i >= 0; i--)
      {
        int index = selectedIndices [i];
        if (index < commands.size () - 1)
        {
          DiagramCommand command = commands.remove (index);
          commands.add (index + 1, command);
        }
      }
      contentsChanged ();
    }

    /**
     * Deletes commands with given indices.
     * @param selectedIndices indices to delete
     */
    public void delete (@NotNull int [] selectedIndices)
    {
      rememberCommands ();
      Arrays.sort (selectedIndices);
      int offset = 0;
      for (int index : selectedIndices)
      {
        commands.remove (index + offset--);
      }
      contentsChanged ();
    }

    /**
     * Deletes all commands below selected index.
     * @param index selected index
     */
    public void crop (int index)
    {
      rememberCommands ();
      while (commands.size () > index + 1)
      {
        commands.remove (commands.size () - 1);
      }
      contentsChanged ();
    }
  }
}
