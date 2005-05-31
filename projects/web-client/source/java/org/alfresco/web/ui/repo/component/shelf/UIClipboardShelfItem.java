/*
 * Created on 01-Jun-2005
 */
package org.alfresco.web.ui.repo.component.shelf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.alfresco.web.bean.clipboard.ClipboardItem;
import org.alfresco.web.bean.clipboard.ClipboardStatus;
import org.alfresco.web.ui.common.Utils;

/**
 * @author Kevin Roast
 */
public class UIClipboardShelfItem extends UIShelfItem
{
   // ------------------------------------------------------------------------------
   // Component Impl
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.collections = (List)values[1];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[2];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = this.collections;
      return values;
   }
   
   /**
    * @see javax.faces.component.UIComponentBase#decode(javax.faces.context.FacesContext)
    */
   public void decode(FacesContext context)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      String fieldId = getHiddenFieldName();
      String value = (String)requestMap.get(fieldId);
      
      // we encoded the value to start with our Id
      if (value != null && value.length() != 0)
      {
      }
   }
   
   /**
    * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      if (isRendered() == false)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      List<ClipboardItem> items = getCollections();
      for (int i=0; i<items.size(); i++)
      {
         ClipboardItem item = items.get(i);
         if (item.Mode == ClipboardStatus.COPY)
         {
            out.write(Utils.buildImageTag(context, IMAGE_COPY, "", "absmiddle"));
         }
         else
         {
            out.write(Utils.buildImageTag(context, IMAGE_CUT, "", "absmiddle"));
         }
         out.write("&nbsp;");
         out.write(item.Node.getName());
         out.write("<br>");
      }
   }
   
   
   // ------------------------------------------------------------------------------
   // Strongly typed component property accessors 
   
   /**
    * @param collections   Set the clipboard item collections to use
    */
   public void setCollections(List<ClipboardItem> collections)
   {
      this.collections = collections;
   }
   
   /**
    * @return The clipboard item collections to use
    */
   public List<ClipboardItem> getCollections()
   {
      ValueBinding vb = getValueBinding("collections");
      if (vb != null)
      {
         this.collections = (List<ClipboardItem>)vb.getValue(getFacesContext());
      }
      
      return this.collections;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   /**
    * We use a hidden field name on the assumption that very few clipboard instances will
    * be present on a single page.
    * 
    * @return hidden field name
    */
   private String getHiddenFieldName()
   {
      return getClientId(getFacesContext());
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   private final static String IMAGE_COPY = "/images/icons/copy.gif";
   private final static String IMAGE_CUT  = "/images/icons/cut.gif";
   
   /** the current list of clipboard items */
   private List<ClipboardItem> collections;
}
