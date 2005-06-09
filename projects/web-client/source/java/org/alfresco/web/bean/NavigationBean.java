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

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.Searcher;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.component.IBreadcrumbHandler;
import org.alfresco.web.ui.common.component.UIBreadcrumb;
import org.alfresco.web.ui.common.component.data.UIRichList;

/**
 * @author Kevin Roast
 */
public class NavigationBean
{
   public NavigationBean()
   {
      // kick off the breadcrumb path and our root node Id
      List<IBreadcrumbHandler> elements = new ArrayList(1);
      elements.add(new NavigationBreadcrumbHandler(
            Application.getCompanyRootName(FacesContext.getCurrentInstance())));
      setLocation(elements);
      setCurrentNodeId(Application.getCurrentUser(
            FacesContext.getCurrentInstance()).getHomeSpaceId());
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
    * @return Returns the search text.
    */
   public String getSearchText()
   {
      return this.searchText;
   }
   
   /**
    * @param searchText    The search text to set.
    */
   public void setSearchText(String searchText)
   {
      this.searchText = searchText;
      
      UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
   }
   
   /**
    * @return Returns the search Mode (see UISimpleSearch constants)
    */
   public int getSearchMode()
   {
      return searchMode;
   }

   /**
    * @param searchMode The searchMode to set (see UISimpleSearch constants).
    */
   public void setSearchMode(int searchMode)
   {
      this.searchMode = searchMode;
   }
   
   /**
    * @return Returns the currently browsing node Id.
    */
   public String getCurrentNodeId()
   {
      return this.currentNodeId;
   }
   
   /**
    * @param currentNodeId    The currently browsing node Id.
    */
   public void setCurrentNodeId(String currentNodeId)
   {
      s_logger.debug("Setting current node id to: " + currentNodeId);
      
      if (currentNodeId == null)
      {
         throw new AlfrescoRuntimeException("Can not set the current node id to null");
      }
         
      this.currentNodeId = currentNodeId;
      this.searchText = null;
      
      UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
   }
   
   /**
    * @return Returns the Map of properties for the current Node. 
    */
   public Map<String, Object> getNodeProperties()
   {
      NodeRef nodeRef = new NodeRef(Repository.getStoreRef(FacesContext.getCurrentInstance()), 
            this.currentNodeId);
      Node node = new Node(nodeRef, this.nodeService);
      this.nodeProperties = node.getProperties();
      
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
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class to handle breadcrumb interaction for top-level navigation pages
    */
   private class NavigationBreadcrumbHandler implements IBreadcrumbHandler
   {
      private static final long serialVersionUID = 4833194653193016638L;
      
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
       * @see org.alfresco.web.ui.common.component.IBreadcrumbHandler#navigationOutcome(org.alfresco.web.ui.common.component.UIBreadcrumb)
       */
      public String navigationOutcome(UIBreadcrumb breadcrumb)
      {
         // set the current node to the user home folder node ID
         FacesContext fc = FacesContext.getCurrentInstance();
         setCurrentNodeId(Application.getCurrentUser(fc).getHomeSpaceId());
         setLocation( (List)breadcrumb.getValue() );
         
         if (fc.getViewRoot().getViewId().equals(BrowseBean.BROWSE_VIEW_ID))
         {
            return null;
         }
         else
         {
            return "browse";
         }
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
   
   /** Search text we are currently using */
   private String searchText;
   
   /** Search mode we are currently using */
   private int searchMode;
   
   /** bag of displayable properties for the current node */
   private Map<String, Object> nodeProperties = null;
   
   /** expanded state of the Shelf panel wrapper component */
   private boolean shelfExpanded = true;
   
   /** list of the breadcrumb handler elements representing the location path of the UI */
   private List<IBreadcrumbHandler> location = Collections.EMPTY_LIST;
}
