/*
 * Created on 19-Apr-2005
 */
package org.alfresco.web.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.component.IBreadcrumbHandler;
import org.alfresco.web.ui.common.component.UIBreadcrumb;
import org.alfresco.web.ui.common.component.UIModeList;
import org.apache.log4j.Logger;

/**
 * @author Kevin Roast
 */
public class NavigationBean
{
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
   public SearchService getSearchService()
   {
      return searchService;
   }

   /**
    * @param searchService The searchService to set.
    */
   public void setSearchService(SearchService searchService)
   {
      this.searchService = searchService;
   }
   
   /**
    * @return Returns the namespaceService.
    */
   public NamespaceService getNamespaceService()
   {
      return this.namespaceService;
   }
   
   /**
    * @param namespaceService The namespaceService to set.
    */
   public void setNamespaceService(NamespaceService namespaceService)
   {
      this.namespaceService = namespaceService;
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
    * @return Returns the toolbar Location.
    */
   public String getToolbarLocation()
   {
      return this.toolbarLocation;
   }
   
   /**
    * @param toolbarLocation  The toolbar Location to set.
    */
   public void setToolbarLocation(String toolbarLocation)
   {
      this.toolbarLocation = toolbarLocation;
   }
   
   /**
    * @return Returns the search context object if any.
    */
   public SearchContext getSearchContext()
   {
      return this.searchContext;
   }
   
   /**
    * @param searchContext    The search context object to set or null to clear search.
    */
   public void setSearchContext(SearchContext searchContext)
   {
      this.searchContext = searchContext;
      
      UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
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
      this.searchContext = null;
      
      UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
   }
   
   /**
    * @return Returns the Map of properties for the current Node. 
    */
   public Map<String, Object> getNodeProperties()
   {
      NodeRef nodeRef = new NodeRef(Repository.getStoreRef(), 
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
    * Action handler to toggle the expanded state of the shelf.
    * The panel component wrapping the shelf area of the UI is value bound to the shelfExpanded property.
    * 
    * @param event
    */
   public void toggleShelf(ActionEvent event)
   {
      this.shelfExpanded = !this.shelfExpanded;
   }
   
   /**
    * Action to change the toolbar location
    * Currently this will changed the location from Company to the users Home space
    */
   public void toolbarLocationChanged(ActionEvent event)
   {
      UIModeList locationList = (UIModeList)event.getComponent();
      String location = locationList.getValue().toString();
      setToolbarLocation(location);
      
      FacesContext context = FacesContext.getCurrentInstance();
      if (LOCATION_COMPANY.equals(location))
      {
         List<IBreadcrumbHandler> elements = new ArrayList(1);
         NodeRef companyRootRef = getCompanyRootRef();
         elements.add(new NavigationBreadcrumbHandler(companyRootRef, Application.getCompanyRootName(context)));
         setLocation(elements);
         setCurrentNodeId(companyRootRef.getId());
      }
      else if (LOCATION_HOME.equals(location))
      {
         List<IBreadcrumbHandler> elements = new ArrayList(1);
         String homeSpaceId = Application.getCurrentUser(context).getHomeSpaceId();
         NodeRef homeSpaceRef = new NodeRef(Repository.getStoreRef(), homeSpaceId);
         String homeSpaceName = Repository.getNameForNode(this.nodeService, homeSpaceRef);
         elements.add(new NavigationBreadcrumbHandler(homeSpaceRef, homeSpaceName));
         setLocation(elements);
         setCurrentNodeId(homeSpaceRef.getId());
      }
      
      // we need to force navigate to refresh the breadcrumb in the browse screen
      context.getApplication().getNavigationHandler().handleNavigation(context, null, "browse");
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   /**
    * Helper to get the company root node reference
    */
   private NodeRef getCompanyRootRef()
   {
      if (this.companyRootRef == null)
      {
         FacesContext context = FacesContext.getCurrentInstance();
         String companySpaceName = Application.getCompanyRootName(context);
         String companyXPath = NamespaceService.ALFRESCO_PREFIX + ":" + QName.createValidLocalName(companySpaceName);
         
         List<NodeRef> nodes = this.nodeService.selectNodes(
               this.nodeService.getRootNode(Repository.getStoreRef()),
               companyXPath, null, this.namespaceService, false);
         
         if (nodes.size() == 0)
         {
            throw new IllegalStateException("Unable to find company home space path: " + companySpaceName);
         }
         
         this.companyRootRef = nodes.get(0);
      }
      
      return this.companyRootRef;
   }
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class to handle breadcrumb interaction for top-level navigation pages
    */
   public class NavigationBreadcrumbHandler implements IBreadcrumbHandler
   {
      private static final long serialVersionUID = 4833194653193016638L;
      
      /**
       * Constructor
       * 
       * @param label      Element label
       */
      public NavigationBreadcrumbHandler(NodeRef ref, String label)
      {
         this.label = label;
         this.ref = ref;
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
         // set the current node to the specified top level node ID
         FacesContext fc = FacesContext.getCurrentInstance();
         setCurrentNodeId(ref.getId());
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
      private NodeRef ref;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static Logger s_logger = Logger.getLogger(NavigationBean.class);
   
   /** constant values used by the toolbar location modelist control */
   private static final String LOCATION_COMPANY = "company";
   private static final String LOCATION_HOME = "home";
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The SearchService to be used by the bean */
   private SearchService searchService;
   
   /** NamespaceService bean reference */
   private NamespaceService namespaceService;
   
   /** Node we are currently in the context of */
   private String currentNodeId;
   
   /** Cached version of company root Id */
   private NodeRef companyRootRef = null;
   
   /** Current toolbar location */
   private String toolbarLocation = LOCATION_HOME;
   
   /** Search context object we are currently using or null for no search */
   private SearchContext searchContext;
   
   /** bag of displayable properties for the current node */
   private Map<String, Object> nodeProperties = null;
   
   /** expanded state of the Shelf panel wrapper component */
   private boolean shelfExpanded = true;
   
   /** list of the breadcrumb handler elements representing the location path of the UI */
   private List<IBreadcrumbHandler> location = Collections.EMPTY_LIST;
}
