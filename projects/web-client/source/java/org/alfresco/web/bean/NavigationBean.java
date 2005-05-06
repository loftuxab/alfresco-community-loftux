/*
 * Created on 19-Apr-2005
 */
package org.alfresco.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.Searcher;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.jsf.component.IBreadcrumbHandler;
import org.alfresco.web.jsf.component.UIBreadcrumb;
import org.alfresco.web.jsf.component.data.UIRichList;

/**
 * @author Kevin Roast
 */
public class NavigationBean
{
   public NavigationBean()
   {
      // TODO: remove this test code - to kick off the breadcrumb path and root node Id
      List<IBreadcrumbHandler> elements = new ArrayList(1);
      elements.add(new NavigationBreadcrumbHandler("My Home"));
      setLocation(elements);
      setCurrentNodeId(null);
   }
   
   
   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * @return Returns the nodeService.
    */
   public NodeService getNodeService()
   {
      return this.nodeService;
   }

   /**
    * @param nodeService The nodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @return Returns the searchService.
    */
   public Searcher getSearchService()
   {
      return searchService;
   }

   /**
    * @param searchService The searchService to set.
    */
   public void setSearchService(Searcher searchService)
   {
      this.searchService = searchService;
   }

   /**
    * Return the expanded state of the Shelf panel wrapper component
    * 
    * @return the expanded state of the Shelf panel wrapper component
    */
   public boolean isShelfExpanded()
   {
      return this.shelfExpanded;
   }
   
   /**
    * Set the expanded state of the Shelf panel wrapper component
    * 
    * @param expanded      true to expanded the Shelf panel area, false to hide it
    */
   public void setShelfExpanded(boolean expanded)
   {
      this.shelfExpanded = expanded;
   }
   
   /**
    * @return Returns the currently browsing node Id.
    */
   public String getCurrentNodeId()
   {
      return currentNodeId;
   }
   
   /**
    * @param currentNodeId    The currently browsing node Id.
    */
   public void setCurrentNodeId(String currentNodeId)
   {
      s_logger.debug("Setting current node id to: " + currentNodeId);
      
      if (currentNodeId != null)
      {
         try
         {
            NodeRef ref = new NodeRef(Repository.getStoreRef(), currentNodeId);
            
            Map<QName, Serializable> props = this.nodeService.getProperties(new NodeRef(Repository.getStoreRef(), currentNodeId));
            
            // setup some properties
            this.nodeProperties.clear();
            this.nodeProperties.put("id", currentNodeId);
            String name = this.nodeService.getPrimaryParent(ref).getQName().getLocalName();
            this.nodeProperties.put("name", name);
            String desc = getQNameProperty(props, "description", true);
            this.nodeProperties.put("description", desc);
            
            this.currentNodeId = currentNodeId;
         }
         catch (InvalidNodeRefException err)
         {
            // TODO: how to handle this?
            s_logger.error("Unable to locate node Id: " + currentNodeId);
         }
      }
      else
      {
         // TODO: replace this with correct root node handling!
         // setup some properties
         this.nodeProperties.clear();
         this.nodeProperties.put("id", currentNodeId);
         this.nodeProperties.put("name", "Linton's Home");
         this.nodeProperties.put("description", "Text description of Linton's Home from Space property");
         
         this.currentNodeId = currentNodeId;
      }
   }
   
   /**
    * @return Returns the Map of properties for the current Node. 
    */
   public Map<String, String> getNodeProperties()
   {
      return this.nodeProperties;
   }
   
   /**
    * @return Returns the breadcrumb handler elements representing the location path of the UI.
    */
   public List<IBreadcrumbHandler> getLocation()
   {
      return this.location;
   }

   /**
    * @param location      The UI location representation to set.
    */
   public void setLocation(List<IBreadcrumbHandler> location)
   {
      this.location = location;
   }
   
   
   // ------------------------------------------------------------------------------
   // Navigation action event handlers
   
   /**
    * Action to toggle the expanded state of the shelf.
    * The panel component wrapping the shelf area of the UI is value bound to the shelfExpanded property.
    * 
    * @param event
    */
   public void toggleShelf(ActionEvent event)
   {
      this.shelfExpanded = !this.shelfExpanded;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   private static String getQNameProperty(Map<QName, Serializable> props, String property, boolean convertNull)
   {
      String value = null;
      
      QName propQName = QName.createQName(NamespaceService.ALFRESCO_URI, property);
      Object obj = props.get(propQName);
      
      if (obj != null)
      {
         value = obj.toString();
      }
      else if (convertNull == true && obj == null)
      {
         value = "";
      }
      
      return value;
   }
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class to handle breadcrumb interaction for top-level navigation pages
    */
   private class NavigationBreadcrumbHandler implements IBreadcrumbHandler
   {
      /**
       * Constructor
       * 
       * @param label      Element label
       */
      public NavigationBreadcrumbHandler(String label)
      {
         this.label = label;
      }
      
      /**
       * @see java.lang.Object#toString()
       */
      public String toString()
      {
         return this.label;
      }

      /**
       * @see org.alfresco.web.jsf.component.IBreadcrumbHandler#navigationOutcome(org.alfresco.web.jsf.component.UIBreadcrumb)
       */
      public String navigationOutcome(UIBreadcrumb breadcrumb)
      {
         // TODO: replace this one we have home folders etc.!
         
         // TEMP!
         setCurrentNodeId(null);
         setLocation( (List)breadcrumb.getValue() );
         
         return "browse";
      }
      
      private String label;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static Logger s_logger = Logger.getLogger(NavigationBean.class);
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The SearchService to be used by the bean */
   private Searcher searchService;
   
   /** Node we are currently in the context of */
   private String currentNodeId;
   
   /** bag of displayable properties for the current node */
   private Map<String, String> nodeProperties = new HashMap<String, String>(7, 1.0f);
   
   /** expanded state of the Shelf panel wrapper component */
   private boolean shelfExpanded = true;
   
   /** list of the breadcrumb handler elements representing the location path of the UI */
   private List<IBreadcrumbHandler> location = Collections.EMPTY_LIST;
}
