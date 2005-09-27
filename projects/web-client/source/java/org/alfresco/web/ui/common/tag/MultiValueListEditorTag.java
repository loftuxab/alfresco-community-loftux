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
package org.alfresco.web.ui.common.tag;

import javax.faces.component.UIComponent;

/**
 * Tag to combine the multi value component and list renderer
 * 
 * @author gavinc
 */
public class MultiValueListEditorTag extends HtmlComponentTag
{
   private String value;
   private String lastItemAdded;
   private String readOnly;
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "org.alfresco.faces.MultiValueEditor";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return "org.alfresco.faces.List";
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      setStringBindingProperty(component, "value", this.value);
      setStringBindingProperty(component, "lastItemAdded", this.lastItemAdded);
      setBooleanProperty(component, "readOnly", this.readOnly);
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      this.value = null;
      this.lastItemAdded = null;
      this.readOnly = null;
   }

   /**
    * @param value The value to set.
    */
   public void setValue(String value)
   {
      this.value = value;
   }

   /**
    * Sets the lastItemAdded value expression binding
    * 
    * @param lastItemAdded lastItemAdded binding
    */
   public void setLastItemAdded(String lastItemAdded)
   {
      this.lastItemAdded = lastItemAdded;
   }

   /**
    * Sets the readOnly flag for the component
    * 
    * @param readOnly true if the component will be read only
    */
   public void setReadOnly(String readOnly)
   {
      this.readOnly = readOnly;
   }
}
