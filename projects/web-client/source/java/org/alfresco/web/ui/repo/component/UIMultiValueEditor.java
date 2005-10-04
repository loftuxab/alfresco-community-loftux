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
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This component wraps a standard component to give it multi value capabilities.
 * 
 * A list of existing values are available, items can be removed from this list
 * or new items added to the list. To add new items the component dynamically
 * shows the child component this one wraps. 
 * 
 * @author gavinc
 */
public class UIMultiValueEditor extends UIInput
{
   private static final Log logger = LogFactory.getLog(UIMultiValueEditor.class);
   
   public final static String ACTION_SEPARATOR = ";";
   public final static int ACTION_NONE   = -1;
   public final static int ACTION_REMOVE = 0;
   public final static int ACTION_SELECT = 1;
   public final static int ACTION_ADD = 2;
   
   private Boolean addingNewItem = Boolean.FALSE;
   private Boolean readOnly;
   private Object lastItemAdded;
   private String selectItemMsg;
   private String selectedItemsMsg;
   
   // ------------------------------------------------------------------------------
   // Component implementation
   
   /**
    * Default constructor
    */
   public UIMultiValueEditor()
   {
      setRendererType("org.alfresco.faces.List");
   }
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.MultiValueEditor";
   }
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.lastItemAdded = values[1];
      this.readOnly = (Boolean)values[2];
      this.addingNewItem = (Boolean)values[3];
      this.selectItemMsg = (String)values[4];
      this.selectedItemsMsg = (String)values[5];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[14];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = this.lastItemAdded;
      values[2] = this.readOnly;
      values[3] = this.addingNewItem;
      values[4] = this.selectItemMsg;
      values[5] = this.selectedItemsMsg;
      return (values);
   }

   /**
    * Returns the last item added by the user
    * 
    * @return The last item added
    */
   public Object getLastItemAdded()
   {
      ValueBinding vb = getValueBinding("lastItemAdded");
      if (vb != null)
      {
         this.lastItemAdded = vb.getValue(getFacesContext());
      }
      
      return this.lastItemAdded;
   }

   /**
    * Sets the last item to be added by the user
    * 
    * @param lastItemAdded The last item added
    */
   public void setLastItemAdded(Object lastItemAdded)
   {
      this.lastItemAdded = lastItemAdded;
   }
   
   /**
    * Determines whether the component is in read only mode
    * 
    * @return true if the component is in read only mode
    */
   public boolean getReadOnly()
   {
      ValueBinding vb = getValueBinding("readOnly");
      if (vb != null)
      {
         this.readOnly = (Boolean)vb.getValue(getFacesContext());
      }
      
      if (this.readOnly == null)
      {
         this.readOnly = Boolean.FALSE;
      }
      
      return this.readOnly.booleanValue();
   }

   /**
    * Sets the read only mode for the component
    * 
    * @param readOnly true to set read only mode
    */
   public void setReadOnly(boolean readOnly)
   {
      this.readOnly = new Boolean(readOnly);
   }
   
   /**
    * Determines whether the component is adding a new item
    * 
    * @return true if we are adding a new item
    */
   public boolean getAddingNewItem()
   {
      return this.addingNewItem.booleanValue();
   }

   /**
    * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
    */
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      if (event instanceof MultiValueEditorEvent)
      {
         MultiValueEditorEvent assocEvent = (MultiValueEditorEvent)event;
         List items = (List)getValue();
         
         switch (assocEvent.Action)
         {
            case ACTION_SELECT:
            {
               this.addingNewItem = Boolean.TRUE;
               break;
            }
            case ACTION_ADD:
            {
               if (items == null)
               {
                  items = new ArrayList();
                  setSubmittedValue(items);
               }
               
               items.add(getLastItemAdded());
               this.addingNewItem = Boolean.FALSE;
               break;
            }
            case ACTION_REMOVE:
            {
               items.remove(assocEvent.RemoveIndex);
               break;
            }
         }
      }
      else
      {
         super.broadcast(event);
      }
   }
   
   
   /**
    * @see javax.faces.component.UIComponent#getRendersChildren()
    */
   public boolean getRendersChildren()
   {
      // only show the wrapped component when the add button has been clicked 
      
      return !this.addingNewItem.booleanValue();
   }
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class representing an action relevant to the ChildAssociationEditor component.
    */
   public static class MultiValueEditorEvent extends ActionEvent
   {
      public int Action;
      public int RemoveIndex;
      
      public MultiValueEditorEvent(UIComponent component, int action, int removeIndex)
      {
         super(component);
         this.Action = action;
         this.RemoveIndex = removeIndex;
      }
   }
}
