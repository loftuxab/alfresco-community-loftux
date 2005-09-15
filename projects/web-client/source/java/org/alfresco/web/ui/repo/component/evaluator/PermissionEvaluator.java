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
package org.alfresco.web.ui.repo.component.evaluator;

import javax.faces.el.ValueBinding;

import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.ui.common.component.evaluator.BaseEvaluator;

/**
 * @author Kevin Roast
 */
public class PermissionEvaluator extends BaseEvaluator
{
   /**
    * Evaluate against the component attributes. Return true to allow the inner
    * components to render, false to hide them during rendering.
    * 
    * @return true to allow rendering of child components, false otherwise
    */
   public boolean evaluate()
   {
      boolean result = false;
      
      try
      {
         Object obj = getValue();
         if (obj instanceof Node)
         {
            // TODO: either check for permissions here against NodeRef using service
            //PermissionService service = Repository.getServiceRegistry(getFacesContext()).getPermissionService();
            //service.hasPermission( ((Node)obj).getNodeRef() );
            
            // TODO: or cache the permissions checks in the Node instance
            //       this means multiple calls to evaluators don't need to keep calling service
            //       and permissions on a Node shouldn't realistically change over the life of an instance
            
            result = true;
         }
      }
      catch (Exception err)
      {
         // return default value on error
         s_logger.debug("Error during PermissionEvaluator evaluation: " + err.getMessage());
      }
      
      return result;
   }
   
   /**
    * Get the allow permissions to match value against
    * 
    * @return the allow permissions to match value against
    */
   public String getAllow()
   {
      ValueBinding vb = getValueBinding("allow");
      if (vb != null)
      {
         this.allow = (String)vb.getValue(getFacesContext());
      }
      
      return this.allow;
   }
   
   /**
    * Set the allow permissions to match value against
    * 
    * @param allow     allow permissions to match value against
    */
   public void setAllow(String allow)
   {
      this.allow = allow;
   }
   
   /**
    * Get the deny permissions to match value against
    * 
    * @return the deny permissions to match value against
    */
   public String getDeny()
   {
      ValueBinding vb = getValueBinding("deny");
      if (vb != null)
      {
         this.deny = (String)vb.getValue(getFacesContext());
      }
      
      return this.deny;
   }
   
   /**
    * Set the deny permissions to match value against
    * 
    * @param deny     deny permissions to match value against
    */
   public void setDeny(String deny)
   {
      this.deny = deny;
   }
   
   
   /** the deny permissions to match value against */
   private String deny = null;
   
   /** the allow permissions to match value against */
   private String allow = null;
}
