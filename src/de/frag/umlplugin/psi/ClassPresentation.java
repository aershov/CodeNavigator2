package de.frag.umlplugin.psi;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import de.frag.umlplugin.uml.graph.EscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * Handles presentation of class, fields and methods.
 */
public class ClassPresentation
{
  public static final String MAGIC_WIDTH = "87";

  /**
   * Renders HTML code that shows information about a class.
   * @param psiClass class to render
   * @param classType class type
   * @param renderFields true, if fields should be contained in output; false otherwise
   * @param renderMethods true, if methods should be contained in output; false otherwise
   * @return created HTML code
   */
  public static @NotNull String renderHtml (@NotNull PsiClass psiClass, @NotNull ClassType classType,
                                            boolean renderFields, boolean renderMethods)
  {
    StringBuilder builder = new StringBuilder ("<html>");
    switch (classType)
    {
      case CLASS:
        builder.append ("<center>");
        appendClass (builder, psiClass).append ("</center>");
        break;
      case ABSTRACT_CLASS:
        builder.append ("<center style='font-style:italic'>");
        appendClass (builder, psiClass).append ("</center>");
        break;
      case INTERFACE:
        builder.append ("<center><font size='-2'>&laquo;interface&raquo;</font><div>");
        appendClass (builder, psiClass).append ("</div></center>");
        break;
      case ENUM:
        builder.append ("<center><font size='-2'>&laquo;enum&raquo;</font><div>");
        appendClass (builder, psiClass).append ("</div></center>");
        break;
      default:
        throw new IllegalStateException ("unknown class type: " + classType);
    }
    URL url = ClassPresentation.class.getResource ("/de/frag/umlplugin/icons/pixel.png");
    if (renderFields)
    {
      builder.append ("<div><img src='").append (url).append ("' alt='' width='").append (MAGIC_WIDTH).append ("'/></div>");
      PsiField[] fields = psiClass.getFields ();
      for (PsiField field : fields)
      {
        builder.append ("<div style='");
        appendStyle (builder, field).append ("'><small>");
        appendField (builder, field).append ("</small></div>");
      }
    }
    if (renderMethods)
    {
      builder.append ("<div><img src='").append (url).append ("' alt='' width='").append (MAGIC_WIDTH).append ("'/></div>");
      PsiMethod[] methods = psiClass.getMethods ();
      for (PsiMethod method : methods)
      {
        if (!method.isConstructor ())
        {
          builder.append ("<div style='");
          appendStyle (builder, method).append ("'><small>");
          appendMethod (builder, method).append ("</small></div>");
        }
      }
    }
    builder.append ("</html>");
    return builder.toString ();
  }

  /**
   * Appends style attributes to given builder.
   * @param builder builder to append attributes to
   * @param owner field or method to append style attributes for
   * @return builder argument
   */
  private static @NotNull StringBuilder appendStyle (@NotNull StringBuilder builder,
                                                     @NotNull PsiModifierListOwner owner)
  {
    if (isAbstract (owner))
    {
      builder.append ("font-style:italic;");
    }
    if (isStatic (owner))
    {
      builder.append ("text-decoration:underline;");
    }
    return builder;
  }

  /**
   * Appends field presentation to builder.
   * @param builder builder to append presentation to
   * @param field field to be appended
   * @return builder argument
   */
  private static @NotNull StringBuilder appendField (@NotNull StringBuilder builder, @NotNull PsiField field)
  {
    String name = field.getName ();
    PsiType type = field.getType ();
    appendAccessMarker (builder, field.getModifierList ());
    builder.append (name).append (": ");
    appendType (builder, type);
    return builder;
  }

  /**
   *
   * @param builder builder to append presentation to
   * @param method method to be appended
   * @return builder argument
   */
  private static @NotNull StringBuilder appendMethod (@NotNull StringBuilder builder, @NotNull PsiMethod method)
  {
    if (!method.isConstructor ())
    {
      PsiType returnType = method.getReturnType ();
      String name = method.getName ();
      appendAccessMarker (builder, method.getModifierList ()).append (name).append (" (");

      PsiParameter[] parameters = method.getParameterList ().getParameters ();
      int index = 0;
      for (PsiParameter parameter : parameters)
      {
        PsiType type = parameter.getType ();
        appendType (builder, type);
        if (index < parameters.length - 1)
        {
          builder.append (", ");
        }
        index++;
      }
      builder.append ("): ");
      appendType (builder, returnType);
    }
    return builder;
  }

  /**
   * Checks whether element is static.
   * @param element element to be checked
   * @return true, if element is static; false otherwise
   */
  private static boolean isStatic (@NotNull PsiModifierListOwner element)
  {
    return element.hasModifierProperty (PsiModifier.STATIC);
  }

  /**
   * Checks whether element is abstract.
   * @param element element to be checked
   * @return true, if element is abstract; false otherwise
   */
  private static boolean isAbstract (@NotNull PsiModifierListOwner element)
  {
    return element.hasModifierProperty (PsiModifier.ABSTRACT);
  }

  /**
   * Appends UML access marker (+/-/#) to given builder.
   * @param builder builder to append marker to
   * @param modifierList modifier list that will be used to compute access marker
   * @return builder argument
   */
  private static @NotNull StringBuilder appendAccessMarker (@NotNull StringBuilder builder,
                                                            @Nullable PsiModifierList modifierList)
  {
    if (modifierList != null)
    {
      int accessLevel = PsiUtil.getAccessLevel (modifierList);
      switch (accessLevel)
      {
        case PsiUtil.ACCESS_LEVEL_PRIVATE:
          builder.append ("- ");
          break;
        case PsiUtil.ACCESS_LEVEL_PROTECTED:
          builder.append ("# ");
          break;
        case PsiUtil.ACCESS_LEVEL_PUBLIC:
          builder.append ("+ ");
          break;
        default:
          builder.append ("  ");
      }
    }
    return builder;
  }

  /**
   * Appends type presentation to given builder.
   * @param builder builder to append presentation to
   * @param psiType type to append presentation for
   */
  private static void appendType (@NotNull StringBuilder builder, @Nullable PsiType psiType)
  {
    if (psiType != null)
    {
      builder.append (EscapeUtils.escape (psiType.getPresentableText ()));
    }
  }

  /**
   * Appends presentation for given class.
   * @param builder builder to append presentation to
   * @param psiClass class to append presenation for
   * @return builder argument
   */
  private static @NotNull StringBuilder appendClass (@NotNull StringBuilder builder, @NotNull PsiClass psiClass)
  {
    builder.append (psiClass.getName ());
    if (psiClass.hasTypeParameters ())
    {
      PsiTypeParameter [] typeParameters = psiClass.getTypeParameters ();
      builder.append ("&lt;");
      int index = 0;
      for (PsiTypeParameter typeParameter : typeParameters)
      {
        appendClass (builder, typeParameter);
        if (index < typeParameters.length - 1)
        {
          builder.append (", ");
        }
        index++;
      }
      builder.append ("&gt;");
    }
    return builder;
  }
}
