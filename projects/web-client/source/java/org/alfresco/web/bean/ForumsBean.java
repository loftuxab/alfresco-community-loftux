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
package org.alfresco.web.bean;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.config.ConfigService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.app.context.IContextListener;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.config.ClientConfigElement;
import org.alfresco.web.ui.common.component.UIModeList;
import org.alfresco.web.ui.common.component.data.UIRichList;
import org.alfresco.web.ui.common.renderer.data.RichListRenderer;
import org.apache.log4j.Logger;

/**
 * Bean providing properties and behaviour for the forums screens.
 * 
 * @author gavinc
 */
public class ForumsBean implements IContextListener
{
   private static Logger logger = Logger.getLogger(ForumsBean.class);
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** ConfigService bean reference */
   private ConfigService configService;
   
   /** Client configuration object */
   private ClientConfigElement clientConfig = null;
   
   /** Component references */
   private UIRichList forumsRichList;

   /** The current forums view mode - set to a well known IRichListRenderer identifier */
   private String forumsViewMode;
   
   /** The current forums view page size */
   private int forumsPageSize;
   
   // ------------------------------------------------------------------------------
   // Construction 

   /**
    * Default Constructor
    */
   public ForumsBean()
   {
      UIContextService.getInstance(FacesContext.getCurrentInstance()).registerBean(this);
   }
   
   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * @param nodeService The NodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @param configService The ConfigService to set.
    */
   public void setConfigService(ConfigService configService)
   {
      this.configService = configService;
   }
   
   /**
    * @param forumsRichList The forumsRichList to set.
    */
   public void setForumsRichList(UIRichList forumsRichList)
   {
      this.forumsRichList = forumsRichList;
      if (this.forumsRichList != null)
      {
         this.forumsRichList.setInitialSortColumn("name");
      }
   }
   
   /**
    * @return Returns the forumsRichList.
    */
   public UIRichList getForumsRichList()
   {
      return this.forumsRichList;
   }
   
   /**
    * @return Returns the forums View mode. See UIRichList
    */
   public String getForumsViewMode()
   {
      if (this.clientConfig == null)
      {
         initFromClientConfig();
      }
      
      return this.forumsViewMode;
   }
   
   /**
    * @param forumsViewMode      The forums View mode to set. See UIRichList.
    */
   public void setForumsViewMode(String forumsViewMode)
   {
      this.forumsViewMode = forumsViewMode;
   }
   
   /**
    * @return Returns the forumsPageSize.
    */
   public int getForumsPageSize()
   {
      if (this.clientConfig == null)
      {
         initFromClientConfig();
      }
      
      return this.forumsPageSize;
   }
   
   /**
    * @param forumsPageSize The forumsPageSize to set.
    */
   public void setForumsPageSize(int forumsPageSize)
   {
      this.forumsPageSize = forumsPageSize;
   }
   
   // ------------------------------------------------------------------------------
   // IContextListener implementation 
   
   /**
    * @see org.alfresco.web.app.context.IContextListener#contextUpdated()
    */
   public void contextUpdated()
   {
      invalidateComponents();
   }
   
   // ------------------------------------------------------------------------------
   // Navigation action event handlers 
   
   /**
    * Change the current view mode based on user selection
    * 
    * @param event      ActionEvent
    */
   public void viewModeChanged(ActionEvent event)
   {
      UIModeList viewList = (UIModeList)event.getComponent();
      
      // get the view mode ID
      String viewMode = viewList.getValue().toString();
      
      // push the view mode into the lists
      setForumsViewMode(viewMode);
   }
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   /**
    * Initialise default values from client configuration
    */
   private void initFromClientConfig()
   {
      this.clientConfig = (ClientConfigElement)this.configService.getGlobalConfig().getConfigElement(
            ClientConfigElement.CONFIG_ELEMENT_ID);
      
      this.forumsViewMode = clientConfig.getDefaultForumsView();
      
      if (RichListRenderer.DetailsViewRenderer.VIEWMODEID.equals(this.forumsViewMode))
      {
         this.forumsPageSize = this.clientConfig.getForumsDetailsPageSize();
      }
      else if (RichListRenderer.IconViewRenderer.VIEWMODEID.equals(this.forumsViewMode))
      {
         this.forumsPageSize = this.clientConfig.getForumsIconsPageSize();
      }
      else if (RichListRenderer.ListViewRenderer.VIEWMODEID.equals(this.forumsViewMode))
      {
         this.forumsPageSize = this.clientConfig.getForumsListPageSize();
      }
      else
      {
         // in case another view mode appears we should have a default
         this.forumsPageSize = 20;
      }
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Set default forums view mode to: " + this.forumsViewMode);
         logger.debug("Set default forums page size to: " + this.forumsPageSize);
      }
   }
   
   /**
    * Invalidate list component state after an action which changes the UI context
    */
   private void invalidateComponents()
   {
      if (logger.isDebugEnabled())
         logger.debug("Invalidating UI List Components...");
      
      // clear the value for the list components - will cause re-bind to it's data and refresh
      if (this.forumsRichList != null)
      {
         this.forumsRichList.setValue(null);
         if (this.forumsRichList.getInitialSortColumn() == null)
         {
            this.forumsRichList.setInitialSortColumn("name");
         }
      }
   }
}
