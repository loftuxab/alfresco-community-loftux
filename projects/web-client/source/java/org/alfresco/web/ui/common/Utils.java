/*
 * Created on Mar 15, 2005
 */
package org.alfresco.web.ui.common;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.renderkit.html.HtmlFormRendererBase;

import org.alfresco.web.data.IDataContainer;

/**
 * @author kevinr
 */
public final class Utils
{
   /**
    * Encodes the given string, so that it can be used within an HTML page.
    * 
    * @param string     the String to convert
    */
   public static String encode(String string)
   {
      if (string == null)
      {
         return "";
      }

      StringBuilder sb = null;      //create on demand
      String enc;
      char c;
      for (int i = 0; i < string.length(); i++)
      {
         enc = null;
         c = string.charAt(i);
         switch (c)
         {
            case '"': enc = "&quot;"; break;    //"
            case '&': enc = "&amp;"; break;     //&
            case '<': enc = "&lt;"; break;      //<
            case '>': enc = "&gt;"; break;      //>
             
            //german umlauts
            case '\u00E4' : enc = "&auml;";  break;
            case '\u00C4' : enc = "&Auml;";  break;
            case '\u00F6' : enc = "&ouml;";  break;
            case '\u00D6' : enc = "&Ouml;";  break;
            case '\u00FC' : enc = "&uuml;";  break;
            case '\u00DC' : enc = "&Uuml;";  break;
            case '\u00DF' : enc = "&szlig;"; break;
            
            //misc
            //case 0x80: enc = "&euro;"; break;  sometimes euro symbol is ascii 128, should we suport it?
            case '\u20AC': enc = "&euro;";  break;
            case '\u00AB': enc = "&laquo;"; break;
            case '\u00BB': enc = "&raquo;"; break;
            case '\u00A0': enc = "&nbsp;"; break;
            
            default:
               if (((int)c) >= 0x80)
               {
                  //encode all non basic latin characters
                  enc = "&#" + ((int)c) + ";";
               }
               break;
         }
         
         if (enc != null)
         {
            if (sb == null)
            {
               String soFar = string.substring(0, i);
               sb = new StringBuilder(i + 8);
               sb.append(soFar);
            }
            sb.append(enc);
         }
         else
         {
            if (sb != null)
            {
               sb.append(c);
            }
         }
      }
      
      if (sb == null)
      {
         return string;
      }
      else
      {
         return sb.toString();
      }
   }
   
   /**
    * Crop a label within a SPAN element, using ellipses '...' at the end of label and
    * and encode the result for HTML output. A SPAN will only be generated if the label
    * is beyond the default setting of 32 characters in length.
    * 
    * @param text       to crop and encode
    * 
    * @return encoded and cropped resulting label HTML
    */
   public static String cropEncode(String text)
   {
      return cropEncode(text, 32);
   }
   
   /**
    * Crop a label within a SPAN element, using ellipses '...' at the end of label and
    * and encode the result for HTML output. A SPAN will only be generated if the label
    * is beyond the specified number of characters in length.
    * 
    * @param text       to crop and encode
    * @param length     length of string to crop too
    * 
    * @return encoded and cropped resulting label HTML
    */
   public static String cropEncode(String text, int length)
   {
      if (text.length() > length)
      {
         String label = text.substring(0, length - 3) + "...";
         StringBuilder buf = new StringBuilder(length + 32 + text.length());
         buf.append("<span title=\"")
            .append(Utils.encode(text))
            .append("\">")
            .append(Utils.encode(label))
            .append("</span>");
         return buf.toString();
      }
      else
      {
         return Utils.encode(text);
      }
   }
   
   /**
    * Replace one string instance with another within the specified string
    * 
    * @param str
    * @param repl
    * @param with
    * 
    * @return replaced string
    */
   public static String replace(String str, String repl, String with)
   {
       int lastindex = 0;
       int pos = str.indexOf(repl);

       // If no replacement needed, return the original string
       // and save StringBuffer allocation/char copying
       if (pos < 0)
       {
           return str;
       }
       
       int len = repl.length();
       int lendiff = with.length() - repl.length();
       StringBuilder out = new StringBuilder((lendiff <= 0) ? str.length() : (str.length() + (lendiff << 3)));
       for (; pos >= 0; pos = str.indexOf(repl, lastindex = pos + len))
       {
           out.append(str.substring(lastindex, pos)).append(with);
       }
       
       return out.append(str.substring(lastindex, str.length())).toString();
   }
   
   /**
    * Remove all occurances of a String from a String
    * 
    * @param str     String to remove occurances from
    * @param match   The string to remove
    * 
    * @return new String with occurances of the match removed
    */
   public static String remove(String str, String match)
   {
      int lastindex = 0;
      int pos = str.indexOf(match);

      // If no replacement needed, return the original string
      // and save StringBuffer allocation/char copying
      if (pos < 0)
      {
          return str;
      }
      
      int len = match.length();
      StringBuilder out = new StringBuilder(str.length());
      for (; pos >= 0; pos = str.indexOf(match, lastindex = pos + len))
      {
          out.append(str.substring(lastindex, pos));
      }
      
      return out.append(str.substring(lastindex, str.length())).toString();
   }
   
   /**
    * Helper to output an attribute to the output stream
    * 
    * @param out        ResponseWriter
    * @param attr       attribute value object (cannot be null)
    * @param mapping    mapping to output as e.g. style="..."
    * 
    * @throws IOException
    */
   public static void outputAttribute(ResponseWriter out, Object attr, String mapping)
      throws IOException
   {
      if (attr != null)
      {
         out.write(' ');
         out.write(mapping);
         out.write("=\"");
         out.write(attr.toString());
         out.write('"');
      }
   }
   
   /**
    * Get the hidden field name for any action component.
    * 
    * All components that wish to simply encode a form value with their client ID can reuse the same
    * hidden field within the parent form. NOTE: components which use this method must only encode
    * their client ID as the value and nothing else!
    * 
    * Build a shared field name from the parent form name and the string "act".
    * 
    * @return hidden field name shared by all action components within the Form.
    */
   public static String getActionHiddenFieldName(FacesContext context, UIComponent component)
   {
      return Utils.getParentForm(context, component).getClientId(context) + NamingContainer.SEPARATOR_CHAR + "act";
   }
   
   /**
    * Helper to recursively render a component and it's child components
    * 
    * @param context    FacesContext
    * @param component  UIComponent
    * 
    * @throws IOException
    */
   public static void encodeRecursive(FacesContext context, UIComponent component)
      throws IOException
   {
      if (component.isRendered() == true)
      {
         component.encodeBegin(context);
         
         // follow the spec for components that render their children
         if (component.getRendersChildren() == true)
         {
            component.encodeChildren(context);
         }
         else
         {
            if (component.getChildCount() != 0)
            {
               for (Iterator i=component.getChildren().iterator(); i.hasNext(); /**/)
               {
                  encodeRecursive(context, (UIComponent)i.next());
               }
            }
         }
         
         component.encodeEnd(context);
      }
   }
   
   /**
    * Generate the JavaScript to submit set the specified hidden Form field to the
    * supplied value and submit the parent Form.
    * 
    * NOTE: the supplied hidden field name is added to the Form Renderer map for output.
    * 
    * @param context       FacesContext
    * @param component     UIComponent to generate JavaScript for
    * @param fieldId       Hidden field id to set value for
    * @param fieldValue    Hidden field value to set hidden field too on submit
    * 
    * @return JavaScript event code
    */
   public static String generateFormSubmit(FacesContext context, UIComponent component, String fieldId, String fieldValue)
   {
      return generateFormSubmit(context, component, fieldId, fieldValue, null);
   }
   
   /**
    * Generate the JavaScript to submit set the specified hidden Form field to the
    * supplied value and submit the parent Form.
    * 
    * NOTE: the supplied hidden field name is added to the Form Renderer map for output.
    * 
    * @param context       FacesContext
    * @param component     UIComponent to generate JavaScript for
    * @param fieldId       Hidden field id to set value for
    * @param fieldValue    Hidden field value to set hidden field too on submit
    * @param params        Optional map of param name/values to output
    * 
    * @return JavaScript event code
    */
   public static String generateFormSubmit(FacesContext context, UIComponent component, String fieldId, String fieldValue, Map<String, String> params)
   {
      UIForm form = Utils.getParentForm(context, component);
      if (form == null)
      {
         throw new IllegalStateException("Must nest components inside UIForm to generate form submit!");
      }
      
      String formClientId = form.getClientId(context);
      
      StringBuilder buf = new StringBuilder(200);
      buf.append("document.forms[");
      buf.append("'");
      buf.append(formClientId);
      buf.append("'");
      buf.append("]['");
      buf.append(fieldId);
      buf.append("'].value='");
      buf.append(fieldValue);
      buf.append("';");
      
      if (params != null)
      {
         for (String name : params.keySet())
         {
            buf.append("document.forms[");
            buf.append("'");
            buf.append(formClientId);
            buf.append("'");
            buf.append("]['");
            buf.append(name);
            buf.append("'].value='");
            buf.append(params.get(name));
            buf.append("';");
            
            // weak, but this seems to be the way Sun RI do it...
            //FormRenderer.addNeededHiddenField(context, name);
            HtmlFormRendererBase.addHiddenCommandParameter(form, name);
         }
      }
      
      buf.append("document.forms[");
      buf.append("'");
      buf.append(formClientId);
      buf.append("'");
      buf.append("].submit()");
      
      buf.append(";return false;");
      
      // weak, but this seems to be the way Sun RI do it...
      //FormRenderer.addNeededHiddenField(context, fieldId);
      HtmlFormRendererBase.addHiddenCommandParameter(form, fieldId);
      
      return buf.toString();
   }
   
   /**
    * Generate the JavaScript to submit the parent Form.
    * 
    * @param context       FacesContext
    * @param component     UIComponent to generate JavaScript for
    * 
    * @return JavaScript event code
    */
   public static String generateFormSubmit(FacesContext context, UIComponent component)
   {
      UIForm form = Utils.getParentForm(context, component);
      if (form == null)
      {
         throw new IllegalStateException("Must nest components inside UIForm to generate form submit!");
      }
      
      String formClientId = form.getClientId(context);
      
      StringBuilder buf = new StringBuilder(48);
      
      buf.append("document.forms[");
      buf.append("'");
      buf.append(formClientId);
      buf.append("'");
      buf.append("].submit()");
      
      buf.append(";return false;");
      
      return buf.toString();
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param width         Width in pixels
    * @param height        Height in pixels
    * @param alt           Optional alt/title text
    * @param onclick       JavaScript onclick event handler code
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, int width, int height, String alt, String onclick)
   {
      StringBuilder buf = new StringBuilder(200);
      
      buf.append("<img src=\"")
         .append(context.getExternalContext().getRequestContextPath())
         .append(image)
         .append("\" width=")
         .append(width)
         .append(" height=")
         .append(height)
         .append(" border=0");
      
      if (alt != null)
      {
         alt = Utils.encode(alt);
         buf.append(" alt=\"")
            .append(alt)
            .append("\" title=\"")
            .append(alt)
            .append('"');
      }
      
      if (onclick != null)
      {
         buf.append(" onclick=\"")
            .append(onclick)
            .append("\" style='cursor:pointer'");
      }
      
      buf.append('>');
      
      return buf.toString();
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param width         Width in pixels
    * @param height        Height in pixels
    * @param alt           Optional alt/title text
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, int width, int height, String alt)
   {
      return buildImageTag(context, image, width, height, alt, null);
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param alt           Optional alt/title text
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, String alt)
   {
      return buildImageTag(context, image, alt, null);
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param alt           Optional alt/title text
    * @param align         Optional alignment value
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, String alt, String align)
   {
      StringBuilder buf = new StringBuilder(128);
      
      buf.append("<img src=\"")
         .append(context.getExternalContext().getRequestContextPath())
         .append(image)
         .append("\" border=0");
      
      if (alt != null)
      {
         alt = Utils.encode(alt);
         buf.append(" alt=\"")
            .append(alt)
            .append("\" title=\"")
            .append(alt)
            .append('"');
      }
      if (align != null)
      {
         buf.append(" align=")
            .append(align);
      }
      
      buf.append('>');
      
      return buf.toString();
   }
   
   /**
    * Return the parent UIForm component for the specified UIComponent
    * 
    * @param context       FaceContext
    * @param component     The UIComponent to find parent Form for
    * 
    * @return UIForm parent or null if none found in hiearachy
    */
   public static UIForm getParentForm(FacesContext context, UIComponent component)
   {
      UIComponent parent = component.getParent();
      while (parent != null)
      {
         if (parent instanceof UIForm)
         {
            break;
         }
         parent = parent.getParent();
      }
      return (UIForm)parent;
   }
   
   /**
    * Return the parent UIComponent implementing the NamingContainer interface for
    * the specified UIComponent.
    * 
    * @param context       FaceContext
    * @param component     The UIComponent to find parent Form for
    * 
    * @return NamingContainer parent or null if none found in hiearachy
    */
   public static UIComponent getParentNamingContainer(FacesContext context, UIComponent component)
   {
      UIComponent parent = component.getParent();
      while (parent != null)
      {
         if (parent instanceof NamingContainer)
         {
            break;
         }
         parent = parent.getParent();
      }
      return (UIComponent)parent;
   }
   
   /**
    * Return the parent UIComponent implementing the IDataContainer interface for
    * the specified UIComponent.
    * 
    * @param context       FaceContext
    * @param component     The UIComponent to find parent IDataContainer for
    * 
    * @return IDataContainer parent or null if none found in hiearachy
    */
   public static IDataContainer getParentDataContainer(FacesContext context, UIComponent component)
   {
      UIComponent parent = component.getParent();
      while (parent != null)
      {
         if (parent instanceof IDataContainer)
         {
            break;
         }
         parent = parent.getParent();
      }
      return (IDataContainer)parent;
   }
   
   /**
    * Determines whether the given component is disabled or readonly
    * 
    * @param component The component to test
    * @return true if the component is either disabled or set to readonly
    */
   public static boolean isComponentDisabledOrReadOnly(UIComponent component)
   {
      boolean disabled = false;
      boolean readOnly = false;
      
      Object disabledAttr = component.getAttributes().get("disabled");
      if (disabledAttr != null)
      {
         disabled = disabledAttr.equals(Boolean.TRUE);
      }
      
      if (disabled == false)
      {
         Object readOnlyAttr = component.getAttributes().get("disabled");
         if (readOnlyAttr != null)
         {
            readOnly = readOnlyAttr.equals(Boolean.TRUE);
         }
      }

      return disabled || readOnly;
   }
   
   /**
    * Invoke the method encapsulated by the supplied MethodBinding
    * 
    * @param context    FacesContext
    * @param method     MethodBinding to invoke
    * @param event      ActionEvent to pass to the method of signature:
    *                   public void myMethodName(ActionEvent event)
    */
   public static void processActionMethod(FacesContext context, MethodBinding method, ActionEvent event)
   {
      try
      {
         method.invoke(context, new Object[] {event});
      }
      catch (EvaluationException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof AbortProcessingException)
         {
            throw (AbortProcessingException)cause;
         }
         else
         {
            throw e;
         }
      }   
   }
   
   /**
    * Adds a global error message
    * 
    * @param msg The error message
    */
   public static void addErrorMessage(String msg)
   {
      addErrorMessage(msg, null);
   }
   
   /**
    * Adds a global error message and logs exception details
    * 
    * @param msg     The error message
    * @param err     The exceptio
    */
   public static void addErrorMessage(String msg, Throwable err)
   {
      FacesContext context = FacesContext.getCurrentInstance( );
      FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
      context.addMessage(null, facesMsg);
      if (err != null && logger.isDebugEnabled())
      {
         err.printStackTrace();
      }
   }
   
   private static Logger logger = Logger.getLogger(Utils.class);
}
