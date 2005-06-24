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
package org.alfresco.web.ui.repo.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;

/**
 * @author Kevin Roast
 */
public class UISpaceSelector extends AbstractItemSelector
{
   // ------------------------------------------------------------------------------
   // Component Impl 
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.SpaceSelector";
   }

   /**
    * Returns the parent id of the current space or null if the parent space is the company home space
    * 
    * @see org.alfresco.web.ui.repo.component.AbstractItemSelector#getParentNodeId(javax.faces.context.FacesContext)
    */
   public String getParentNodeId(FacesContext context)
   {
      String id = null;
      
      if (this.navigationId.equals(Application.getCompanyRootId()) == false)
      {
         ChildAssociationRef parentRef = getNodeService(context).getPrimaryParent(
               new NodeRef(Repository.getStoreRef(), this.navigationId));
         id = parentRef.getParentRef().getId();
      }
      
      return id;
   }

   /**
    * Returns the child spaces of the current space
    * 
    * @see org.alfresco.web.ui.repo.component.AbstractItemSelector#getChildrenForNode(javax.faces.context.FacesContext)
    */
   public Collection<ChildAssociationRef> getChildrenForNode(FacesContext context)
   {
      NodeRef nodeRef = new NodeRef(Repository.getStoreRef(), this.navigationId);
      List<ChildAssociationRef> allKids = getNodeService(context).getChildAssocs(nodeRef);
      NodeService service = getNodeService(context);
      
      // filter out those children that are not spaces
      List<ChildAssociationRef> spaceKids = new ArrayList<ChildAssociationRef>(); 
      for (ChildAssociationRef childRef : allKids)
      {
         if (service.getType(childRef.getChildRef()).equals(ContentModel.TYPE_FOLDER))
         {
            spaceKids.add(childRef);
         }
      }
      
      return spaceKids;
   }

   /**
    * Returns the current users home space
    * 
    * @see org.alfresco.web.ui.repo.component.AbstractItemSelector#getRootChildren(javax.faces.context.FacesContext)
    */
   public Collection<ChildAssociationRef> getRootChildren(FacesContext context)
   {
      // get the root space from the current user
      //String rootId = Application.getCurrentUser(context).getHomeSpaceId();
      NodeRef rootRef = new NodeRef(Repository.getStoreRef(), Application.getCompanyRootId());

      // get a child association reference back to the real repository root to satisfy
      // the generic API we have in the abstract super class
      ChildAssociationRef childRefFromRealRoot = getNodeService(context).getPrimaryParent(rootRef);
      List<ChildAssociationRef> roots = new ArrayList<ChildAssociationRef>(1);
      roots.add(childRefFromRealRoot);
                  
      return roots;
   }
}
