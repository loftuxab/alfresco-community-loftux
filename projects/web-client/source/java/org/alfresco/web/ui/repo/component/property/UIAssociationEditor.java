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
package org.alfresco.web.ui.repo.component.property;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.DataDictionary;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Component that allows associations to be edited 
 * i.e. new associations to be added, existing ones to be 
 * removed whilst following the rules in the data dictionary 
 * 
 * @author gavinc
 */
public class UIAssociationEditor extends UIInput
{
   private static final Log logger = LogFactory.getLog(UIAssociationEditor.class);
   
   private final static String ACTION_SEPARATOR = ";";
   private final static int ACTION_NONE   = -1;
   private final static int ACTION_REMOVE = 0;
   private final static int ACTION_ADD_NEW = 1;
   private final static int ACTION_ADD = 2;
   private final static int ACTION_CHANGE = 3;
   
   protected static String FIELD_AVAILABLE = "_available";
   
   /** I18N message strings */
   private final static String MSG_REMOVE = "remove";
   private final static String MSG_ADD_NEW = "add";
   private final static String MSG_ADD = "add";
   private final static String MSG_CHANGE = "change";
   private static final String MSG_ERROR_ASSOC = "error_association";
   
   protected String associationName;
   protected String availableOptionsSize;
   protected boolean showAvailable = false;
   
   /** Map of the original associations keyed by the id of the child */
   protected Map<String, AssociationRef> originalAssocs;
   protected Map<String, AssociationRef> added;
   protected Map<String, AssociationRef> removed;
   
   // ------------------------------------------------------------------------------
   // Component implementation
   
   /**
    * Default constructor
    */
   public UIAssociationEditor()
   {
      setRendererType(null);
   }
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.AssociationEditor";
   }
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.associationName = (String)values[1];
      this.originalAssocs = (Map)values[2];
      this.availableOptionsSize = (String)values[3];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[14];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = this.associationName;
      values[2] = this.originalAssocs;
      values[3] = this.availableOptionsSize;
      
      // NOTE: we don't save the state of the added and removed maps as these
      //       need to be rebuilt everytime
      
      return (values);
   }

   /**
    * @see javax.faces.component.UIComponent#decode(javax.faces.context.FacesContext)
    */
   public void decode(FacesContext context)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      Map valuesMap = context.getExternalContext().getRequestParameterValuesMap();
      String fieldId = getHiddenFieldName();
      String value = (String)requestMap.get(fieldId);
      
      int action = ACTION_NONE;
      String removeId = null;
      if (value != null && value.length() != 0)
      {
         // break up the action into it's parts
         int sepIdx = value.indexOf(ACTION_SEPARATOR);
         if (sepIdx != -1)
         {
            action = Integer.parseInt(value.substring(0, sepIdx));
            removeId = value.substring(sepIdx+1);
         }
         else
         {
            action = Integer.parseInt(value);
         }
      }
      
      // gather the current state and queue an event
      String[] addedItems = (String[])valuesMap.get(fieldId + FIELD_AVAILABLE);
      
      AssocEditorEvent event = new AssocEditorEvent(this, action, addedItems, removeId);
      queueEvent(event);
      
      super.decode(context);
   }

   /**
    * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
    */
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      if (event instanceof AssocEditorEvent)
      {
         AssocEditorEvent assocEvent = (AssocEditorEvent)event;
         Node node = (Node)getValue();
         
         switch (assocEvent.Action)
         {
            case ACTION_ADD_NEW:
            {
               this.showAvailable = true;
               break;
            }
            case ACTION_ADD:
            {
               addTarget(node, assocEvent.ToAdd);
               break;
            }
            case ACTION_REMOVE:
            {
               removeTarget(node, assocEvent.RemoveId);
               break;
            }
            case ACTION_CHANGE:
            {
               removeTarget(node, assocEvent.RemoveId);
               this.showAvailable = true;
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
    * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      if (isRendered() == false)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      String clientId = getClientId(context);

      // get the child associations currently on the node and any that have been added
      NodeService nodeService = Repository.getServiceRegistry(context).getNodeService();
      Node node = (Node)getValue();
      
      // get some metadata about the association from the data dictionary
      DataDictionary dd = (DataDictionary)FacesContextUtils.getRequiredWebApplicationContext(
               context).getBean(Application.BEAN_DATA_DICTIONARY);
      AssociationDefinition assocDef = dd.getAssociationDefinition(node, this.associationName);
      if (assocDef == null)
      {
         logger.warn("Failed to find association definition for association '" + associationName + "'");
         
         // add an error message as the property is not defined in the data dictionary and 
         // not in the node's set of properties
         String msg = MessageFormat.format(Application.getMessage(context, MSG_ERROR_ASSOC), new Object[] {this.associationName});
         Utils.addErrorMessage(msg);
      }
      else
      {
         String childType = assocDef.getTargetClass().getName().toString();
         boolean allowManyChildren = assocDef.isTargetMany();
         
         // we need to remember the original set of associations (if there are any)
         // and place them in a map keyed by the id of the child node
         if (this.originalAssocs == null)
         {
            this.originalAssocs = new HashMap<String, AssociationRef>();
   
            List assocs = (List)node.getAssociations().get(this.associationName);
            if (assocs != null)
            {
               Iterator iter = assocs.iterator();
               while (iter.hasNext())
               {
                  AssociationRef assoc = (AssociationRef)iter.next();
                  
                  // add the association to the map
                  this.originalAssocs.put(assoc.getTargetRef().getId(), assoc);
               }
            }
         }
         
         // start outer table
         out.write("<table class='child-assoc-editor' border='0' cellspacing='4' cellpadding='0'>");
         
         // get the map of added associations for this node and association type
         this.added = node.getAddedAssociations().get(this.associationName);
         if (added == null)
         {
            // if there aren't any added associations for 'associationName' create a map and add it
            added = new HashMap<String, AssociationRef>();
            node.getAddedAssociations().put(this.associationName, added);
         }
         
         // get the map of removed associations for this node and association type
         this.removed = node.getRemovedAssociations().get(this.associationName);
         if (removed == null)
         {
            // if there aren't any added associations for 'associationName' create a map and add it
            removed = new HashMap<String, AssociationRef>();
            node.getRemovedAssociations().put(this.associationName, removed);
         }
         
         // show the associations from the original list if they are not in the removed list
         for (AssociationRef assoc : this.originalAssocs.values())
         {
            if (removed.containsKey(assoc.getTargetRef().getId()) == false)
            {
               renderExistingAssociation(context, out, nodeService, assoc, allowManyChildren);
            }
         }
         
         // also show any associations added in this session
         for (AssociationRef assoc : added.values())
         {
            renderExistingAssociation(context, out, nodeService, assoc, allowManyChildren);
         }
         
         if (this.showAvailable)
         {
            // if we are adding a new item then display the list of available options for this association 
            renderAvailableOptions(context, out, nodeService, childType, allowManyChildren);
         }
         else
         {
            // show the add button if required
            if (allowManyChildren || 
               (allowManyChildren == false && this.originalAssocs.size() == 0 && this.added.size() == 0) ||
               (allowManyChildren == false && this.originalAssocs.size() == 1 && this.removed.size() == 1 && this.added.size() == 0) )
            {
               out.write("<tr><td colspan='2'><input type='submit' value='");
               out.write(Application.getMessage(context, "add"));
               out.write("' onclick=\"");
               out.write(generateFormSubmit(context, Integer.toString(ACTION_ADD_NEW)));
               out.write("\"/></td></tr>");
            }
         }
         
         if (logger.isDebugEnabled())
         {
            logger.debug("number original = " + this.originalAssocs.size());
            logger.debug("number added = " + this.added.size());
            logger.debug("number removed = " + this.removed.size());
         }
         
         // close table
         out.write("</table>");
      }
   }

   /**
    * Returns the name of the association this component is editing
    * 
    * @return Association name
    */
   public String getAssociationName()
   {
      ValueBinding vb = getValueBinding("associationName");
      if (vb != null)
      {
         this.associationName = (String)vb.getValue(getFacesContext());
      }
      
      return this.associationName;
   }

   /**
    * Sets the name of the association this component will edit
    * 
    * @param associationName Name of the association to edit
    */
   public void setAssociationName(String associationName)
   {
      this.associationName = associationName;
   }
   
   /**
    * Returns the size of the select control when multiple items
    * can be selected 
    * 
    * @return The size of the select control
    */
   public String getAvailableOptionsSize()
   {
      if (this.availableOptionsSize == null)
      {
         this.availableOptionsSize = "8";
      }
      
      return this.availableOptionsSize;
   }
   
   /**
    * Sets the size of the select control used when multiple items can
    * be selected 
    * 
    * @param availableOptionsSize The size
    */
   public void setAvailableOptionsSize(String availableOptionsSize)
   {
      this.availableOptionsSize = availableOptionsSize;
   }
   
   /**
    * Renders an existing association with the appropriate options
    * 
    * @param context FacesContext
    * @param out Writer to write output to
    * @param nodeService The NodeService
    * @param assoc The association to render
    * @param allowManyChildren Whether the current association allows multiple children
    * @throws IOException
    */
   protected void renderExistingAssociation(FacesContext context, ResponseWriter out, NodeService nodeService,
         AssociationRef assoc, boolean allowManyChildren) throws IOException
   {
      out.write("<tr><td>");
      out.write(Repository.getDisplayPath(nodeService.getPath(assoc.getTargetRef())));
      out.write("/");
      out.write(Repository.getNameForNode(nodeService, assoc.getTargetRef()));
      out.write("</td><td align='right'><input type='submit' value='");
      out.write(Application.getMessage(context, "remove"));
      out.write("' onclick=\"");
      out.write(generateFormSubmit(context, ACTION_REMOVE + ACTION_SEPARATOR + assoc.getTargetRef().getId()));
      out.write("\"/>");
      if (allowManyChildren == false)
      {
         out.write("&nbsp;<input type='submit' value='");
         out.write(Application.getMessage(context, "change"));
         out.write("' onclick=\"");
         out.write(generateFormSubmit(context, ACTION_CHANGE + ACTION_SEPARATOR + assoc.getTargetRef().getId()));
         out.write("\"/>");
      }
      out.write("</td></tr>");
   }
   
   /**
    * Renders the list of available options for a new association
    * 
    * @param context FacesContext
    * @param out Writer to write output to
    * @param nodeService The NodeService
    * @param childType The type of the child at the end of the association
    * @param allowManyChildren Whether the current association allows multiple children
    * @throws IOException
    */
   protected void renderAvailableOptions(FacesContext context, ResponseWriter out, NodeService nodeService, 
         String childType, boolean allowManyChildren) throws IOException
   {
      out.write("<tr><td colspan='2'><select name='");
      out.write(getClientId(context) + FIELD_AVAILABLE);
      out.write("' size='");
      if (allowManyChildren)
      {
         out.write(getAvailableOptionsSize());
         out.write("' multiple");
      }
      else
      {
         out.write("1'");
      }
      out.write(">");
      
      // find and show all the available options for the current association
      String query = "TYPE:\"" + childType + "\"";
         
      if (logger.isDebugEnabled())
         logger.debug("Available children query: " + query);
       
      ResultSet results = Repository.getServiceRegistry(context).getSearchService().query(
            Repository.getStoreRef(), SearchService.LANGUAGE_LUCENE, query);
      List<NodeRef> available = results.getNodeRefs();
      
      for (NodeRef item : available)
      {
         // NOTE: only show the items that are not already associated to (if/when we support adding an
         //       association name via the UI this restriction can be removed)
         if ((this.originalAssocs.containsKey(item.getId()) == false && this.added.containsKey(item.getId()) == false))
         {
            out.write("<option value='");
            out.write(item.getId());
            out.write("'>");
            out.write(Repository.getDisplayPath(nodeService.getPath(item)));
            out.write("/");
            out.write(Repository.getNameForNode(nodeService, item));
            out.write("</option>");
         }
      }
      
      out.write("</select></td></tr>");
      
      // add the Add button 
      String buttonLabel = allowManyChildren ? Application.getMessage(context, "add") : Application.getMessage(context, "set");
      out.write("<tr><td colspan='2' align='right'><input type='submit' value='");
      out.write(buttonLabel);
      out.write("' onclick=\"");
      out.write(generateFormSubmit(context, Integer.toString(ACTION_ADD)));
      out.write("\"/></td></tr>");
   }
   
   /**
    * Updates the component and node state to reflect an association being removed 
    * 
    * @param node The node we are dealing with
    * @param targetId The id of the child to remove
    */
   protected void removeTarget(Node node, String targetId)
   {
      if (node != null && targetId != null)
      {
         QName assocQName = Repository.resolveToQName(this.associationName);
         AssociationRef newAssoc = new AssociationRef(node.getNodeRef(), assocQName, new NodeRef(Repository.getStoreRef(), targetId));
         
         // update the node so it knows to remove the association, but only if the association
         // was one of the original ones
         if (this.originalAssocs.containsKey(targetId))
         {
            Map<String, AssociationRef> removed = node.getRemovedAssociations().get(this.associationName);
            removed.put(targetId, newAssoc);
            
            if (logger.isDebugEnabled())
               logger.debug("Added association to " + targetId + " to the removed list");
         }
         
         // if this association was previously added in this session it will still be
         // in the added list so remove it if it is
         Map<String, AssociationRef> added = node.getAddedAssociations().get(this.associationName);
         if (added.containsKey(targetId))
         {
            added.remove(targetId);
            
            if (logger.isDebugEnabled())
               logger.debug("Removed association to " + targetId + " from the added list");
         }
      }
   }
   
   /**
    * Updates the component and node state to reflect an association being added 
    * 
    * @param node The node we are dealing with
    * @param childId The id of the child to add
    */
   protected void addTarget(Node node, String[] toAdd)
   {
      if (node != null && toAdd != null && toAdd.length > 0)
      {
         for (int x = 0; x < toAdd.length; x++)
         {
            String targetId = toAdd[x];
            QName assocQName = Repository.resolveToQName(this.associationName);
            AssociationRef newAssoc = new AssociationRef(node.getNodeRef(), assocQName, new NodeRef(Repository.getStoreRef(), targetId));   
            
            // update the node so it knows to add the association
            Map<String, AssociationRef> added = node.getAddedAssociations().get(this.associationName);
            added.put(targetId, newAssoc);
            
            if (logger.isDebugEnabled())
               logger.debug("Added association to " + targetId + " to the added list");
            
            // if the association was previously removed and has now been re-added it
            // will still be in the "to be removed" list so remove it if it is
            Map<String, AssociationRef> removed = node.getRemovedAssociations().get(this.associationName);
            if (removed.containsKey(targetId))
            {
               removed.remove(targetId);
               
               if (logger.isDebugEnabled())
                  logger.debug("Removed association to " + targetId + " from the removed list");
            }
         }
      }
   }
   
   /**
    * We use a hidden field per picker instance on the page.
    * 
    * @return hidden field name
    */
   private String getHiddenFieldName()
   {
      return getClientId(getFacesContext());
   }
   
   /**
    * Generate FORM submit JavaScript for the specified action
    *  
    * @param context    FacesContext
    * @param action     Action string
    * 
    * @return FORM submit JavaScript
    */
   private String generateFormSubmit(FacesContext context, String action)
   {
      return Utils.generateFormSubmit(context, this, getHiddenFieldName(), action);
   }
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class representing an action relevant to the AssociationEditor component.
    */
   public static class AssocEditorEvent extends ActionEvent
   {
      public int Action;
      public String[] ToAdd;
      public String RemoveId;
      
      public AssocEditorEvent(UIComponent component, int action, String[] toAdd, String removeId)
      {
         super(component);
         this.Action = action;
         this.ToAdd = toAdd;
         this.RemoveId = removeId;
      }
   }
}
