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
package org.alfresco.web.ui.repo.tag.property;

import javax.faces.component.UIComponent;

import org.alfresco.web.ui.common.tag.BaseComponentTag;

/**
 * Allows the AssociationEditor component to be added to a JSP page
 * 
 * @author gavinc
 */
public class AssociationEditorTag extends BaseComponentTag
{
   private String associationName;
   private String availableOptionsSize;
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "org.alfresco.faces.AssociationEditor";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringProperty(component, "associationName", this.associationName);
      setStringStaticProperty(component, "availableOptionsSize", this.availableOptionsSize);
   }
   
   /**
    * Sets the association name
    * 
    * @param associationName The association name
    */
   public void setAssociationName(String associationName)
   {
      this.associationName = associationName;
   }
   
   /**
    * @param availableOptionsSize Sets the size of the available options size when 
    *        multiple items can be selected
    */
   public void setAvailableOptionsSize(String availableOptionsSize)
   {
      this.availableOptionsSize = availableOptionsSize;
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#release()
    */
   public void release()
   {
      this.associationName = null;
      this.availableOptionsSize = null;

      super.release();
   }
}
