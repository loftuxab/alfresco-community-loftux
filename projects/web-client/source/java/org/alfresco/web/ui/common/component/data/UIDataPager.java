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
package org.alfresco.web.ui.common.component.data;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.apache.log4j.Logger;

import org.alfresco.web.app.Application;
import org.alfresco.web.data.IDataContainer;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.WebResources;

/**
 * @author Kevin Roast
 */
public class UIDataPager extends UICommand
{
   private static Logger s_logger = Logger.getLogger(IDataContainer.class);
   
   private static final String LAST_PAGE = "last_page";
   private static final String NEXT_PAGE = "next_page";
   private static final String PREVIOUS_PAGE = "prev_page";
   private static final String FIRST_PAGE = "first_page";
   private static final String MSG_PAGEINFO = "page_info";
   
   
   // ------------------------------------------------------------------------------
   // Construction 
   
   /**
    * Default constructor
    */
   public UIDataPager()
   {
      setRendererType(null);
   }
   
   
   // ------------------------------------------------------------------------------
   // Component implementation
   
   /**
    * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      IDataContainer dataContainer = getDataContainer();
      if (dataContainer == null)
      {
         throw new IllegalStateException("Must nest UISortLink inside component implementing IDataContainer!"); 
      }
      
      // this component will only render itself if the parent DataContainer is setup
      // with a valid "pageSize" property
      if (isRendered() == false || dataContainer.getPageSize() == -1)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      ResourceBundle bundle = Application.getBundle(context);
      
      StringBuilder buf = new StringBuilder(420);
      
      // output HTML links or labels to render the paging controls
      int nCurrentPage = dataContainer.getCurrentPage();
      
      // first page
      if (nCurrentPage != 0)
      {
         buf.append("<a href='#' onclick=\"");
         buf.append(generateEventScript(0));
         buf.append("\">");
         buf.append(Utils.buildImageTag(context, WebResources.IMAGE_FIRSTPAGE, 13, 10, bundle.getString(FIRST_PAGE)));
         buf.append("</a>");
      }
      else
      {
         buf.append(Utils.buildImageTag(context, WebResources.IMAGE_FIRSTPAGE_NONE, 13, 10, null));
      }
      buf.append("&nbsp;");
      
      // previous page
      if (nCurrentPage != 0)
      {
         buf.append("<a href='#' onclick=\"");
         buf.append(generateEventScript(nCurrentPage - 1));
         buf.append("\">");
         buf.append(Utils.buildImageTag(context, WebResources.IMAGE_PREVIOUSPAGE, 9, 10, bundle.getString(PREVIOUS_PAGE)));
         buf.append("</a>");
      }
      else
      {
         buf.append(Utils.buildImageTag(context, WebResources.IMAGE_PREVIOUSPAGE_NONE, 9, 10, null));
      }
      buf.append("&nbsp;");
      
      // handle that the page count can be zero if no data present
      buf.append(MessageFormat.format(bundle.getString(MSG_PAGEINFO), new Object[] {
            Integer.toString(dataContainer.getCurrentPage() + 1),
            Integer.toString(dataContainer.getPageCount())
            }));
      buf.append("&nbsp;");
      
      // next page
      if ((dataContainer.getCurrentPage() < dataContainer.getPageCount() - 1) == true)
      {
         buf.append("<a href='#' onclick=\"");
         buf.append(generateEventScript(nCurrentPage + 1));
         buf.append("\">");
         buf.append(Utils.buildImageTag(context, WebResources.IMAGE_NEXTPAGE, 9, 10, bundle.getString(NEXT_PAGE)));
         buf.append("</a>");
      }
      else
      {
         buf.append(Utils.buildImageTag(context, WebResources.IMAGE_NEXTPAGE_NONE, 9, 10, null));
      }
      buf.append("&nbsp;");
      
      // last page
      if ((dataContainer.getCurrentPage() < dataContainer.getPageCount() - 1) == true)
      {
         buf.append("<a href='#' onclick=\"");
         buf.append(generateEventScript(dataContainer.getPageCount() - 1));
         buf.append("\">");
         buf.append(Utils.buildImageTag(context, WebResources.IMAGE_LASTPAGE, 13, 10, bundle.getString(LAST_PAGE)));
         buf.append("</a>");
      }
      else
      {
         buf.append(Utils.buildImageTag(context, WebResources.IMAGE_LASTPAGE_NONE, 13, 10, null));
      }
      
      out.write(buf.toString());
   }

   /**
    * @see javax.faces.component.UIComponentBase#decode(javax.faces.context.FacesContext)
    */
   public void decode(FacesContext context)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      String fieldId = getHiddenFieldName();
      String value = (String)requestMap.get(fieldId);
      if (value != null && value.length() != 0)
      {
         // we were clicked - queue an event to represent the click
         // cannot handle the event here as other components etc. have not had
         // a chance to decode() - we queue an event to be processed later
         PageEvent actionEvent = new PageEvent(this, Integer.valueOf(value).intValue());
         this.queueEvent(actionEvent);
      }
   }
   
   /**
    * @see javax.faces.component.UICommand#broadcast(javax.faces.event.FacesEvent)
    */
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      if (event instanceof PageEvent == false)
      {
         // let the super class handle events which we know nothing about
         super.broadcast(event);
      }
      else
      {
         // found a sort event for us!
         if (s_logger.isDebugEnabled())
            s_logger.debug("Handling paging event to index: " + ((PageEvent)event).Page);
         getDataContainer().setCurrentPage(((PageEvent)event).Page);
      } 
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   /**
    * Return the parent data container for this component
    */
   private IDataContainer getDataContainer()
   {
      return Utils.getParentDataContainer(getFacesContext(), this);
   }
   
   /**
    * Output the JavaScript event script to jump to a specified page
    * 
    * @param page    page index to generate script to jump too
    */
   private String generateEventScript(int page)
   {
      return Utils.generateFormSubmit(getFacesContext(), this, getHiddenFieldName(), Integer.toString(page));
   }
   
   /**
    * We use a hidden field name based on the parent data container component Id and
    * the string "pager" to give a field name that can be shared by all pager links
    * within a single data container component.
    * 
    * @return hidden field name
    */
   private String getHiddenFieldName()
   {
      UIComponent dataContainer = (UIComponent)Utils.getParentDataContainer(getFacesContext(), this);
      return dataContainer.getClientId(getFacesContext()) + NamingContainer.SEPARATOR_CHAR + "pager";
   }
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class representing the clicking of a sortable column.
    */
   private static class PageEvent extends ActionEvent
   {
      public PageEvent(UIComponent component, int page)
      {
         super(component);
         Page = page;
      }
      
      public int Page = 0;
   }
}
