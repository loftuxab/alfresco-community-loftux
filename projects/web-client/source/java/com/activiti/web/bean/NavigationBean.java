/*
 * Created on 19-Apr-2005
 */
package com.activiti.web.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import com.activiti.repo.node.NodeService;
import com.activiti.repo.search.Searcher;
import com.activiti.web.jsf.component.IBreadcrumbHandler;
import com.activiti.web.jsf.component.UIBreadcrumb;
import com.activiti.web.jsf.component.data.UIRichList;

/**
 * @author Kevin Roast
 */
public class NavigationBean
{
   public NavigationBean()
   {
      // TEMP: test code to kick off the breadcrumb path
      List<IBreadcrumbHandler> elements = new ArrayList(1);
      elements.add(new NavigationBreadcrumbHandler("My Home"));
      setLocation(elements);
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
      this.currentNodeId = currentNodeId;
      // TODO: need a decoupled way to refresh components - a view-local context event service?
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
       * @see com.activiti.web.jsf.component.IBreadcrumbHandler#navigationOutcome(com.activiti.web.jsf.component.UIBreadcrumb)
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
   
   /** expanded state of the Shelf panel wrapper component */
   private boolean shelfExpanded = true;
   
   /** list of the breadcrumb handler elements representing the location path of the UI */
   private List<IBreadcrumbHandler> location = Collections.EMPTY_LIST;
}
