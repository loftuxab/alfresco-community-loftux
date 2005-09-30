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
package org.alfresco.web.bean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.transaction.UserTransaction;

import org.alfresco.repo.action.executer.ExporterActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Backing bean implementation for the Export dialog.
 * 
 * @author gavinc
 */
public class ExportBean
{
   private static final Log logger = LogFactory.getLog(ExportBean.class);
   
   private static final String ALL_SPACES = "all";
   private static final String CURRENT_SPACE = "current";
   
   private static final String DEFAULT_OUTCOME = "adminConsole";
   
   private static final String MSG_ERROR = "error_export";
   
   private BrowseBean browseBean;
   private NavigationBean navigator;
   private ActionService actionService;
   
   private String packageName;
   private String mode = CURRENT_SPACE;
   private NodeRef destination;
   private boolean includeChildren = true;
   
   /**
    * Performs the export operation using the current state of the bean
    */
   public String export()
   {
      if (logger.isDebugEnabled())
         logger.debug("Called export for " + this.mode + " with package name: " + this.packageName);
      
      String outcome = DEFAULT_OUTCOME;
      
      UserTransaction tx = null;
      
      try
      {
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // build the action params map based on the bean's current state
         Map<String, Serializable> params = new HashMap<String, Serializable>(4);
          
         // build the action to execute
         Action action = this.actionService.createAction(ExporterActionExecuter.NAME, params);
         action.setExecuteAsynchronously(true);
         
         // execute the action on the current document node
         this.actionService.executeAction(action, this.browseBean.getActionSpace().getNodeRef());
         
         if (logger.isDebugEnabled())
         {
            logger.debug("Executed export action with action params of " + params);
         }
         
         // commit the transaction
         tx.commit();
         
         // reset the bean
         reset();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), MSG_ERROR), e.toString()), e);
         outcome = null;
      }
      
      return outcome;
   }
   
   public String cancel()
   {
      reset();
      
      return DEFAULT_OUTCOME;
   }
   
   /**
    * Resets the dialog state back to the default
    */
   public void reset()
   {
      this.packageName = null;
      this.mode = CURRENT_SPACE;
      this.destination = null;
      this.includeChildren = true;
   }
   
   /**
    * Returns the package name for the export
    * 
    * @return The export package name
    */
   public String getPackageName()
   {
      return this.packageName;
   }
   
   /**
    * Sets the package name for the export
    * 
    * @param packageName The export package name 
    */
   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }
   
   /**
    * The destination for the export as a NodeRef
    * 
    * @return The destination
    */
   public NodeRef getDestination()
   {
      return this.destination;
   }
   
   /**
    * Sets the destination for the export
    * 
    * @param destination The destination for the export
    */
   public void setDestination(NodeRef destination)
   {
      this.destination = destination;
   }
   
   /**
    * Determines whether the export will include child spaces 
    * 
    * @return true includes children
    */
   public boolean getIncludeChildren()
   {
      return this.includeChildren;
   }
   
   /**
    * Sets whether child spaces are included in the export 
    * 
    * @param includeChildren true to include the child spaces
    */
   public void setIncludeChildren(boolean includeChildren)
   {
      this.includeChildren = includeChildren;
   }
   
   /**
    * Determines whether to export only the current space or all spaces
    * 
    * @return "all" to export all space and "current" to export the current space
    */
   public String getMode()
   {
      return this.mode;
   }
   
   /**
    * Sets whether to export the current space or all spaces
    * 
    * @param mode "all" to export all space and "current" to export the current space
    */
   public void setMode(String mode)
   {
      this.mode = mode;
   }
   
   /**
    * Sets the BrowseBean instance to use to retrieve the current document
    * 
    * @param browseBean BrowseBean instance
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }
   
   /**
    * Sets the NavigationBean to use
    * 
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * Sets the action service
    * 
    * @param actionRegistration  the action service
    */
   public void setActionService(ActionService actionService)
   {
      this.actionService = actionService;
   }
}
