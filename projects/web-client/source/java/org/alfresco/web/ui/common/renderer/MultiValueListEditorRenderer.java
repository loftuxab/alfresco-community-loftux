/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.ui.common.renderer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIMultiValueEditor;
import org.alfresco.web.ui.common.component.UIMultiValueEditor.MultiValueEditorEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Renders the MultiValueEditor component as a list of options that can be
 * removed using a Remove button
 * 
 * @author gavinc
 */
public class MultiValueListEditorRenderer extends BaseRenderer
{
   private static Log logger = LogFactory.getLog(MultiValueListEditorRenderer.class);
   
   /** I18N message strings */
   private final static String MSG_REMOVE = "remove";
   private final static String MSG_SELECT_BUTTON = "select_button";
   private final static String MSG_SELECT = "select_an_item";
   private final static String MSG_ADD_TO_LIST_BUTTON = "add_to_list_button";
   
   // ------------------------------------------------------------------------------
   // Renderer implemenation
   
   /**
    * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void decode(FacesContext context, UIComponent component)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      Map valuesMap = context.getExternalContext().getRequestParameterValuesMap();
      String fieldId = getHiddenFieldName(component);
      String value = (String)requestMap.get(fieldId);
      
      int action = UIMultiValueEditor.ACTION_NONE;
      int removeIndex = -1;
      if (value != null && value.length() != 0)
      {
         // break up the action into it's parts
         int sepIdx = value.indexOf(UIMultiValueEditor.ACTION_SEPARATOR);
         if (sepIdx != -1)
         {
            action = Integer.parseInt(value.substring(0, sepIdx));
            removeIndex = Integer.parseInt(value.substring(sepIdx+1));
         }
         else
         {
            action = Integer.parseInt(value);
         }
      }
      
      if (action != UIMultiValueEditor.ACTION_NONE)
      {
         MultiValueEditorEvent event = new MultiValueEditorEvent(component, action, removeIndex);
         component.queueEvent(event);
      }
      
      super.decode(context, component);
   }

   /**
    * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeBegin(FacesContext context, UIComponent component) throws IOException
   {
      if (component.isRendered() == false)
      {
         return;
      }

      if (component instanceof UIMultiValueEditor)
      {
         ResponseWriter out = context.getResponseWriter();
         Map attrs = component.getAttributes();
         String clientId = component.getClientId(context);
         UIMultiValueEditor editor = (UIMultiValueEditor)component;
         
         // start outer table
         out.write("<table border='0' cellspacing='4' cellpadding='0'");
         this.outputAttribute(out, attrs.get("style"), "style");
         this.outputAttribute(out, attrs.get("styleClass"), "styleClass");
         out.write(">");
         
         // show the select an item message
         out.write("<tr><td>");
         
         // TODO: make this generic
         out.write(Application.getMessage(context, "select_category"));
         out.write("</td></tr>");
            
         // output a padding row
         out.write("<tr><td height='10'></td></tr>");
            
         if (editor.getAddingNewItem())
         {
            // TODO: remove the hard coded style - need to change the space selector to use div and not span
            out.write("<tr><td colspan='2' style='background-color: #eeeeee; border: 1px dashed #cccccc; padding: 6px;'>");
         }
         else
         {
            out.write("<tr><td colspan='2'><input type='submit' value='");
            out.write(Application.getMessage(context, MSG_SELECT_BUTTON));
            out.write("' onclick=\"");
            out.write(generateFormSubmit(context, component, Integer.toString(UIMultiValueEditor.ACTION_SELECT)));
            out.write("\"/></td></tr>");
         
            // output a padding row
            out.write("<tr><td height='10'></td></tr>");
         }
      }
   }
   
   /**
    * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeEnd(FacesContext context, UIComponent component) throws IOException
   {
      if (component instanceof UIMultiValueEditor)
      {
         ResponseWriter out = context.getResponseWriter();
         UIMultiValueEditor editor = (UIMultiValueEditor)component;
         
         // get hold of the node service
         NodeService nodeService = Repository.getServiceRegistry(context).getNodeService();
         
         if (editor.getAddingNewItem())
         {
            out.write("</td></tr>");
            
            // output a padding row
            out.write("<tr><td height='10'></td></tr>");
            
            // show the add to list button but only if something has been selected
            if (editor.getLastItemAdded() != null)
            {
               out.write("<tr><td colspan='2'><input type='submit' value='");
               out.write(Application.getMessage(context, MSG_ADD_TO_LIST_BUTTON));
               out.write("' onclick=\"");
               out.write(generateFormSubmit(context, component, Integer.toString(UIMultiValueEditor.ACTION_ADD)));
               out.write("\"/></td></tr>");
            
               // output a padding row
               out.write("<tr><td height='10'></td></tr>");
            }
         }
         
         // output a padding row
         out.write("<tr><td>");
            
         // TODO: make this generic
         out.write(Application.getMessage(context, "selected_categories"));
         out.write(":</td></tr>");
         
         // show the current items
         List currentItems = (List)editor.getValue();
         if (currentItems != null && currentItems.size() > 0)
         {
            for (int x = 0; x < currentItems.size(); x++)
            {  
               renderExistingItem(context, component, out, nodeService, x, currentItems.get(x));
            }
         }
         else
         {
            // output a padding row
            out.write("<tr><td>&lt;");
            out.write(Application.getMessage(context, "none"));
            out.write("&gt;</td></tr>");
         }
            
         // close table
         out.write("</table>");
      }
   }

   /**
    * Renders an existing item with a remove button
    * 
    * @param context FacesContext
    * @param component The UIComponent
    * @param out Writer to write output to
    * @param nodeService The NodeService
    * @param key The key of the item
    * @param value The item's value
    * @throws IOException
    */
   protected void renderExistingItem(FacesContext context, UIComponent component, ResponseWriter out, 
         NodeService nodeService, int index, Object value) throws IOException
   {
      out.write("<tr><td>");
      
      if (value instanceof NodeRef)
      {
         out.write(Repository.getNameForNode(nodeService, (NodeRef)value));
      }
      else
      {
         out.write(value.toString());
      }

      out.write("&nbsp;&nbsp;");
      out.write("</td><td align='right'><input type='submit' value='");
      out.write(Application.getMessage(context, MSG_REMOVE));
      out.write("' onclick=\"");
      out.write(generateFormSubmit(context, component, UIMultiValueEditor.ACTION_REMOVE + UIMultiValueEditor.ACTION_SEPARATOR + index));
      out.write("\"/></td></tr>");
   }

   /**
    * We use a hidden field per picker instance on the page.
    * 
    * @return hidden field name
    */
   private String getHiddenFieldName(UIComponent component)
   {
      return component.getClientId(FacesContext.getCurrentInstance());
   }
   
   /**
    * Generate FORM submit JavaScript for the specified action
    *  
    * @param context    FacesContext
    * @param component  The UIComponent
    * @param action     Action string
    * 
    * @return FORM submit JavaScript
    */
   private String generateFormSubmit(FacesContext context, UIComponent component, String action)
   {
      return Utils.generateFormSubmit(context, component, getHiddenFieldName(component), action);
   }
}
