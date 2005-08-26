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
package org.alfresco.web.ui.repo.component;

import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;

/**
 * Component to allow the selection of a category
 * 
 * @author gavinc
 */
public class UICategorySelector extends AbstractItemSelector
{
   // ------------------------------------------------------------------------------
   // Component Impl 
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.CategorySelector";
   }
   
   /**
    * Use Spring JSF integration to return the category service bean instance
    * 
    * @param context    FacesContext
    * 
    * @return category service bean instance or throws runtime exception if not found
    */
   private static CategoryService getCategoryService(FacesContext context)
   {
      CategoryService service = Repository.getServiceRegistry(context).getCategoryService();
      if (service == null)
      {
         throw new IllegalStateException("Unable to obtain CategoryService bean reference.");
      }
      
      return service;
   }

   /**
    * Returns the parent id of the current category, or null if the parent has the category root type
    * 
    * @see org.alfresco.web.ui.repo.component.AbstractItemSelector#getParentNodeId(javax.faces.context.FacesContext)
    */
   public String getParentNodeId(FacesContext context)
   {
      String id = null;
      
      ChildAssociationRef parentRef = getNodeService(context).getPrimaryParent(
            new NodeRef(Repository.getStoreRef(), this.navigationId));
      Node parentNode = new Node(parentRef.getParentRef(), getNodeService(context));
      
      DictionaryService dd = Repository.getServiceRegistry(FacesContext.getCurrentInstance()).getDictionaryService();
      
      if (dd.isSubClass(parentNode.getType(), ContentModel.TYPE_CATEGORYROOT) == false)
      {
         id = parentRef.getParentRef().getId();
      }
      
      return id;
   }

   /**
    * Returns the child categories of the current navigation node
    * 
    * @see org.alfresco.web.ui.repo.component.AbstractItemSelector#getChildrenForNode(javax.faces.context.FacesContext)
    */
   public Collection<ChildAssociationRef> getChildrenForNode(FacesContext context)
   {
      NodeRef nodeRef = new NodeRef(Repository.getStoreRef(), this.navigationId);
      
      // TODO: replace this code with the proper call to the category service but 
      //       this seems to work for now until we get proper categories to test with
      List<ChildAssociationRef> childRefs = getNodeService(context).getChildAssocs(nodeRef);
//      Collection<ChildAssocRef> childRefs = getCategoryService(context).getChildren(nodeRef, 
//            CategoryService.Mode.ALL, CategoryService.Depth.IMMEDIATE);
      
      return childRefs;
   }

   /**
    * Returns the root categories
    * 
    * @see org.alfresco.web.ui.repo.component.AbstractItemSelector#getRootChildren(javax.faces.context.FacesContext)
    */
   public Collection<ChildAssociationRef> getRootChildren(FacesContext context)
   {
      return getCategoryService(context).getRootCategories(Repository.getStoreRef());
   }
}
