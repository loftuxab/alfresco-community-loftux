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

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.app.context.IContextListener;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.repo.component.shelf.UIRecentSpacesShelfItem;
import org.apache.log4j.Logger;

/**
 * This bean manages the real-time updated list of Recent Spaces in the Shelf component.
 * <p>
 * Registers itself as a UI Context Listener so it is informed as to when the current Node ID
 * has changed in the NavigationBeans. This is used to keep the list of spaces up-to-date. 
 * 
 * @author Kevin Roast
 */
public class RecentSpacesBean implements IContextListener
{
   private static Logger logger = Logger.getLogger(RecentSpacesBean.class);
   
   private static final int MAX_RECENT_SPACES = 6;
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The NavigationBean reference */
   private NavigationBean navigator;
   
   /** The BrowseBean reference */
   private BrowseBean browseBean;
   
   /** List of recent space nodes */
   private List<Node> recentSpaces = new LinkedList<Node>();
   
   
   // ------------------------------------------------------------------------------
   // Construction 
   
   /**
    * Default Constructor
    */
   public RecentSpacesBean()
   {
      UIContextService.getInstance(FacesContext.getCurrentInstance()).registerBean(this);
   }


   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * @return Returns the NodeService.
    */
   public NodeService getNodeService()
   {
      return this.nodeService;
   }

   /**
    * @param nodeService The NodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @return Returns the navigation bean instance.
    */
   public NavigationBean getNavigator()
   {
      return this.navigator;
   }
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * @return The BrowseBean
    */
   public BrowseBean getBrowseBean()
   {
      return this.browseBean;
   }

   /**
    * @param browseBean The BrowseBean to set.
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }
   
   /**
    * @return the List of recent spaces
    */
   public List<Node> getRecentSpaces()
   {
      return this.recentSpaces;
   }
   
   /**
    * @param spaces     List of Nodes
    */
   public void setRecentSpaces(List<Node> spaces)
   {
      this.recentSpaces = spaces;
   }
   
   /**
    * Action handler bound to the recent spaces Shelf component called when a Space is clicked
    */
   public void navigate(ActionEvent event)
   {
      // work out which node was clicked from the event data
      UIRecentSpacesShelfItem.RecentSpacesEvent spaceEvent = (UIRecentSpacesShelfItem.RecentSpacesEvent)event;
      UIRecentSpacesShelfItem component = (UIRecentSpacesShelfItem)event.getComponent();
      Node selectedNode = this.recentSpaces.get(spaceEvent.Index);
      // then navigate to the appropriate node in UI
      // use browse bean functionality for this as it will update the breadcrumb for us
      this.browseBean.updateUILocation(selectedNode.getNodeRef());
   }
   
   
   // ------------------------------------------------------------------------------
   // IContextListener implementation
   
   /**
    * @see org.alfresco.web.app.context.IContextListener#contextUpdated()
    */
   public void contextUpdated()
   {
      // We use this listener handler to refresh the recent spaces list. At the point
      // where this method is called, the current node Id in UI will probably have changed.
      Node node = this.navigator.getCurrentNode();
      
      // search for this node - if it's already in the list remove it so
      // that it appears at the top for us
      for (int i=0; i<this.recentSpaces.size(); i++)
      {
         if (node.getId().equals(this.recentSpaces.get(i).getId()))
         {
            // found same node already in the list - remove it
            this.recentSpaces.remove(i);
            break;
         }
      }
      
      // remove an item if the list is at the maximum length
      if (this.recentSpaces.size() == MAX_RECENT_SPACES)
      {
         this.recentSpaces.remove(MAX_RECENT_SPACES - 1);
      }
      
      // insert our Node at the top of the list so it's most relevent
      this.recentSpaces.add(0, node);
   }
}
