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
package org.alfresco.web.ui.repo.tag.shelf;

import javax.faces.component.UIComponent;

import org.alfresco.web.ui.common.tag.HtmlComponentTag;

/**
 * @author Kevin Roast
 */
public class ShelfGroupTag extends HtmlComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "org.alfresco.faces.ShelfGroup";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      // self rendering component
      return null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringProperty(component, "label", this.label);
      setBooleanProperty(component, "expanded", this.expanded);
   }
   
   /**
    * @see org.alfresco.web.ui.common.tag.HtmlComponentTag#release()
    */
   public void release()
   {
      super.release();
      
      this.label = null;
      this.expanded = null;
   }
   
   /**
    * Set the label
    *
    * @param label     the label
    */
   public void setLabel(String label)
   {
      this.label = label;
   }

   /**
    * Set the expanded flag
    *
    * @param expanded     the expanded flag
    */
   public void setExpanded(String expanded)
   {
      this.expanded = expanded;
   }


   /** the label */
   private String label;

   /** the expanded */
   private String expanded;
}
