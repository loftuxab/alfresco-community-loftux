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
package org.alfresco.web.ui.common.tag.evaluator;

import javax.faces.component.UIComponent;

import org.alfresco.web.ui.common.tag.BaseComponentTag;

/**
 * @author kevinr
 */
public abstract class GenericEvaluatorTag extends BaseComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public final String getRendererType()
   {
      // evaluator components do not use renderers 
      return null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      setStringProperty(component, "value", this.value);
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      this.value = null;
   }
   
   /**
    * Set the value to evaluate against
    *
    * @param value     the value to evaluate against
    */
   public void setValue(String value)
   {
      this.value = value;
   }
   
   /** the value to evaluate against */
   private String value;
}
