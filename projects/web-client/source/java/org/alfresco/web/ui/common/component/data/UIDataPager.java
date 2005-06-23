/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
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

import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.apache.log4j.Logger;

import org.alfresco.web.data.IDataContainer;
import org.alfresco.web.ui.common.Utils;

/**
 * @author Kevin Roast
 */
public class UIDataPager extends UICommand
{
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
      
      StringBuilder buf = new StringBuilder(420);
      
      // output HTML links or labels to render the paging controls
      int nCurrentPage = dataContainer.getCurrentPage();
      
      // first page
      if (nCurrentPage != 0)
      {
         buf.append("<a href='#' onclick=\"");
         buf.append(generateEventScript(0));
         buf.append("\">");
         buf.append(Utils.buildImageTag(context, IMAGE_FIRSTPAGE, 13, 10, "First Page"));
         buf.append("</a>");
      }
      else
      {
         buf.append(Utils.buildImageTag(context, IMAGE_FIRSTPAGE_NONE, 13, 10, null));
      }
      buf.append("&nbsp;");
      
      // previous page
      if (nCurrentPage != 0)
      {
         buf.append("<a href='#' onclick=\"");
         buf.append(generateEventScript(nCurrentPage - 1));
         buf.append("\">");
         buf.append(Utils.buildImageTag(context, IMAGE_PREVIOUSPAGE, 9, 10, "Previous Page"));
         buf.append("</a>");
      }
      else
      {
         buf.append(Utils.buildImageTag(context, IMAGE_PREVIOUSPAGE_NONE, 9, 10, null));
      }
      buf.append("&nbsp;");
      
      // handle that the page count can be zero if no data present
      buf.append(MessageFormat.format(MSG_PAGEINFO, new Object[] {
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
         buf.append(Utils.buildImageTag(context, IMAGE_NEXTPAGE, 9, 10, "Next Page"));
         buf.append("</a>");
      }
      else
      {
         buf.append(Utils.buildImageTag(context, IMAGE_NEXTPAGE_NONE, 9, 10, null));
      }
      buf.append("&nbsp;");
      
      // last page
      if ((dataContainer.getCurrentPage() < dataContainer.getPageCount() - 1) == true)
      {
         buf.append("<a href='#' onclick=\"");
         buf.append(generateEventScript(dataContainer.getPageCount() - 1));
         buf.append("\">");
         buf.append(Utils.buildImageTag(context, IMAGE_LASTPAGE, 13, 10, "Last Page"));
         buf.append("</a>");
      }
      else
      {
         buf.append(Utils.buildImageTag(context, IMAGE_LASTPAGE_NONE, 13, 10, null));
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
         if (s_logger.isDebugEnabled())
            s_logger.debug("Caught pager click using field: " + fieldId + "; with value: " + value);
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
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static final String IMAGE_PREVIOUSPAGE_NONE = "/images/icons/PreviousPage_unavailable.gif";
   private static final String IMAGE_PREVIOUSPAGE = "/images/icons/PreviousPage.gif";
   private static final String IMAGE_FIRSTPAGE_NONE = "/images/icons/FirstPage_unavailable.gif";
   private static final String IMAGE_FIRSTPAGE = "/images/icons/FirstPage.gif";
   private static final String IMAGE_NEXTPAGE_NONE = "/images/icons/NextPage_unavailable.gif";
   private static final String IMAGE_NEXTPAGE = "/images/icons/NextPage.gif";
   private static final String IMAGE_LASTPAGE_NONE = "/images/icons/LastPage_unavailable.gif";
   private static final String IMAGE_LASTPAGE = "/images/icons/LastPage.gif";
   
   private static final String MSG_PAGEINFO = "Page {0} of {1}";
   
   private static Logger s_logger = Logger.getLogger(IDataContainer.class);
}
