package de.frag.umlplugin.uml.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiClass;
import de.frag.umlplugin.ProjectUtils;
import de.frag.umlplugin.PsiClassConsumer;
import de.frag.umlplugin.PsiClassProvider;
import de.frag.umlplugin.uml.UMLDiagram;
import de.frag.umlplugin.uml.UMLDiagramComponent;
import de.frag.umlplugin.uml.UMLDiagramsPanel;
import de.frag.umlplugin.uml.command.AddClassCommand;
import de.frag.umlplugin.uml.command.DiagramCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Abstract base class for actions that add selected classes to diagrams.
 */
public abstract class AbstractAddToDiagramAction extends AnAction implements PsiClassConsumer
{
  private PsiClassProvider psiClassProvider;

  /**
   * Sets psi class provider. This method should be called to inject a psi class provider at plugin startup.
   * @param psiClassProvider new psi class provider
   */
  public void setPsiClassProvider (@NotNull PsiClassProvider psiClassProvider)
  {
    this.psiClassProvider = psiClassProvider;
  }

  /**
   * Updates the state of the action.
   * @param e Carries information on the invocation place and data available
   */
  public void update (AnActionEvent e)
  {
    Presentation presentation = e.getPresentation ();
    if (psiClassProvider == null)
    {
      presentation.setEnabled (false);
    }
    List<PsiClass> psiClasses = psiClassProvider.getPsiClass (e.getDataContext ());
    presentation.setEnabled (!psiClasses.isEmpty ());
  }

  /**
   * Shows current element under cursor or mouse in graphical navigator.
   * @param e Carries information on the invocation place
   */
  public void actionPerformed (AnActionEvent e)
  {
    Project project = DataKeys.PROJECT.getData (e.getDataContext ());
    UMLDiagramsPanel diagrams = ProjectUtils.get (project, UMLDiagramsPanel.class);
    if (diagrams == null || project == null)
    {
      return;
    }
    List<PsiClass> psiClasses = psiClassProvider.getPsiClass (e.getDataContext ());
    if (!psiClasses.isEmpty ())
    {
      String diagramName = getDiagramName (e, psiClasses);
      if (diagramName != null)
      {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance (project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow (UMLDiagramComponent.TOOL_WINDOW_ID);
        toolWindow.activate (null);

        UMLDiagram umlDiagram = diagrams.getUMLDiagram (diagramName);
        if (umlDiagram == null)
        {
          umlDiagram = new UMLDiagram (project, diagramName);
          diagrams.addDiagram (umlDiagram);
        }
        for (PsiClass psiClass : psiClasses)
        {
          String className = psiClass.getQualifiedName ();
          if (className != null)
          {
            DiagramCommand command = new AddClassCommand (className);
            umlDiagram.addCommand (command);
          }
        }
        umlDiagram.doLayout ();
        diagrams.switchToDiagram (diagramName);
      }
    }
  }

  /**
   * Gets diagram name for given event.
   * @param e event
   * @param psiClasses psi classes that will be added to diagram
   * @return diagram name or null, if diagram name is unknown
   */
  protected abstract @Nullable String getDiagramName (@NotNull AnActionEvent e, @NotNull List<PsiClass> psiClasses);
}
