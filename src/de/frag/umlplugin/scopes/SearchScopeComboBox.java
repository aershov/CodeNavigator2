package de.frag.umlplugin.scopes;

import com.intellij.ide.util.scopeChooser.EditScopesDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.scope.packageSet.NamedScope;
import com.intellij.ui.ComboboxWithBrowseButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Combo box for choosing search scopes.
 */
public class SearchScopeComboBox extends ComboboxWithBrowseButton
{
  /**
   * Creates a new search scope combo box.
   * @param project current project
   */
  public SearchScopeComboBox (final @NotNull Project project)
  {
    getComboBox ().setModel (createComboBoxModel (project));
    addActionListener (new ActionListener()
    {
      public void actionPerformed (ActionEvent e)
      {
        EditScopesDialog editScopesDialog = new EditScopesDialog (project, false);
        editScopesDialog.show ();
        getComboBox ().setModel (createComboBoxModel (project));
        NamedScope selectedScope = editScopesDialog.getSelectedScope ();
        if (selectedScope != null)
        {
          setSelectedSearchScopeName (selectedScope.getName ());
        }
      }
    });
  }

  /**
   * Selects a search scope by name.
   * @param searchScopeName name of search scope to select
   */
  public void setSelectedSearchScopeName (@Nullable String searchScopeName)
  {
    if (searchScopeName != null)
    {
      JComboBox comboBox = getComboBox ();
      ComboBoxModel comboBoxModel = comboBox.getModel ();
      for (int i = 0; i < comboBoxModel.getSize (); i++)
      {
        ScopeWrapper scopeWrapper = (ScopeWrapper) comboBoxModel.getElementAt (i);
        if (scopeWrapper.toString ().equals (searchScopeName))
        {
          comboBox.setSelectedIndex (i);
          break;
        }
      }
    }
  }

  /**
   * Gets name of selected search scope.
   * @return name of search scope that is currently selected
   */
  public @NotNull String getSelectedSearchScopeName ()
  {
    return getComboBox ().getSelectedItem ().toString ();
  }

  /**
   * Gets selected search scope.
   * @return search scope that is currently selected
   */
  public @NotNull SearchScope getSelectedSearchScope ()
  {
    return ((ScopeWrapper) getComboBox ().getSelectedItem ()).searchScope;
  }

  /**
   * Creates combo box model that contains all selectable search scopes.
   * @param project current project
   * @return created model
   */
  private @NotNull ComboBoxModel createComboBoxModel (@NotNull Project project)
  {
    MutableComboBoxModel model = new DefaultComboBoxModel ();
    for (SearchScope searchScope : new NamedSearchScopes (project).getScopes ())
    {
      model.addElement (new ScopeWrapper (searchScope));
    }
    return model;
  }

  /**
   * Simple wrapper that implements toString-method for combo box entry renderer.
   */
  private static class ScopeWrapper
  {
    private final SearchScope searchScope;

    private ScopeWrapper (@NotNull SearchScope searchScope)
    {
      this.searchScope = searchScope;
    }

    public @NotNull String toString ()
    {
      return searchScope.getDisplayName ();
    }
  }
}
