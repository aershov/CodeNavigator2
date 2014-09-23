package de.frag.umlplugin.scopes;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.scope.packageSet.NamedScope;
import com.intellij.psi.search.scope.packageSet.NamedScopeManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collection of named scopes that can be used for scope lookup and scope persistence.
 */
public class NamedSearchScopes
{
  private final Map<String, SearchScope> scopes = new LinkedHashMap<String, SearchScope> ();

  /**
   * Creates a new list of search scopes.
   * @param project project to create list for
   */
  public NamedSearchScopes (@NotNull Project project)
  {
    addScope (GlobalSearchScope.allScope (project));
    addScope (GlobalSearchScope.projectScope (project));
    addScope (GlobalSearchScope.projectProductionScope (project, false));
    addScope (GlobalSearchScope.projectTestScope (project, false));
    addModuleScopes (project);
    addNamedScopes (project);
  }

  /**
   * Adds given scope to this list of search scopes.
   * @param scope scope to add
   */
  private void addScope (@NotNull GlobalSearchScope scope)
  {
    scopes.put (scope.getDisplayName (), scope);
  }

  /**
   * Adds all known named scopes to this list of search scopes.
   * @param project current project
   */
  private void addNamedScopes (@NotNull Project project)
  {
    NamedScopeManager namedScopeManager = NamedScopeManager.getInstance (project);
    for (NamedScope namedScope : namedScopeManager.getScopes ())
    {
      addScope (GlobalSearchScope.filterScope (project, namedScope));
    }
  }

  /**
   * Adds all module scopes to this list of search scopes.
   * @param project current project that contains modules to be added
   */
  private void addModuleScopes (@NotNull Project project)
  {
    ModuleManager moduleManager = ModuleManager.getInstance (project);
    for (Module module : moduleManager.getModules ())
    {
      addScope (GlobalSearchScope.moduleScope (module));
    }
  }

  /**
   * Gets all search scopes contained in this list of search scopes.
   * @return contained search scopes
   */
  public @NotNull Collection<SearchScope> getScopes ()
  {
    return scopes.values ();
  }
}
