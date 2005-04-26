/*
 * Created on 19-Apr-2005
 */
package com.activiti.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.search.ResultSetRow;
import com.activiti.repo.search.Searcher;
import com.activiti.repo.search.Value;
import com.activiti.util.Conversion;
import com.activiti.web.bean.repository.Node;
import com.activiti.web.bean.repository.Repository;
import com.activiti.web.jsf.component.IBreadcrumbHandler;
import com.activiti.web.jsf.component.UIActionLink;
import com.activiti.web.jsf.component.UIBreadcrumb;
import com.activiti.web.jsf.component.UIModeList;
import com.activiti.web.jsf.component.data.UIRichList;

/**
 * @author Kevin Roast
 */
public class BrowseBean
{
   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * @return Returns the NodeService.
    */
   public NodeService getNodeService()
   {
      return this.nodeService;
   }

   /**
    * @param nodeService The NodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @return Returns the Searcher service.
    */
   public Searcher getSearchService()
   {
      return searchService;
   }

   /**
    * @param searchService The Searcher to set.
    */
   public void setSearchService(Searcher searchService)
   {
      this.searchService = searchService;
   }
   
   /**
    * @return Returns the navigation bean instance.
    */
   public NavigationBean getNavigator()
   {
      return navigator;
   }
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * @return Returns the browse View mode. See UIRichList
    */
   public String getBrowseViewMode()
   {
      return browseViewMode;
   }
   
   /**
    * @param browseViewMode      The browse View mode to set. See UIRichList.
    */
   public void setBrowseViewMode(String browseViewMode)
   {
      this.browseViewMode = browseViewMode;
   }
   
   /**
    * Page accessed bean method to get the nodes currently being browsed
    * 
    * @return List of Node objects for the current browse location
    */
   public List<Node> getNodes()
   {
      return queryBrowseNodes(getNavigator().getCurrentNodeId());
   }
   
   
   // ------------------------------------------------------------------------------
   // Navigation action event handlers 
   
   /**
    * Change the current view mode based on user selection
    * 
    * @param event      ActionEvent
    */
   public void viewModeChanged(ActionEvent event)
   {
      UIModeList viewList = (UIModeList)event.getComponent();
      setBrowseViewMode(viewList.getValue().toString());
   }
   
   
   // ------------------------------------------------------------------------------
   // Helper methods 
   
   /**
    * Query a list of nodes for the specified parent node Id
    * 
    * @param parentNodeId     Id of the parent node or null for the root node
    * 
    * @return a List of Node object found directly below the specified parent node
    */
   private List<Node> queryBrowseNodes(String parentNodeId)
   {
      NodeRef parentRef;
      if (parentNodeId == null)
      {
         // no specific parent node specified - use the root node
         parentRef = this.nodeService.getRootNode(Repository.getStoreRef());
      }
      else
      {
         // build a NodeRef for the specified Id and our store
         parentRef = new NodeRef(Repository.getStoreRef(), parentNodeId);
      }
      
      Collection<ChildAssocRef> childRefs = this.nodeService.getChildAssocs(parentRef);
      List<Node> items = new ArrayList<Node>(childRefs.size());
      for (ChildAssocRef ref: childRefs)
      {
         // display name is the QName localname part
         QName qname = ref.getName();
         
         // create our Node representation
         Node node = new Node(qname.getNamespaceURI());  // TODO: where does Type come from?
         Map<String, Object> props = new HashMap<String, Object>(7, 1.0f);
         
         // convert the rest of the well known properties
         Map<QName, Serializable> childProps = this.nodeService.getProperties(ref.getChildRef());
         
         // name and ID always exist
         props.put("id", ref.getChildRef().getId());
         props.put("name", qname.getLocalName());
         
         // other properties which may exist
         String description = getQNameProperty(childProps, "description", true);
         props.put("description", description);
         
         String createdDate = getQNameProperty(childProps, "createddate", false);
         if (createdDate != null)
         {
            props.put("createddate", Conversion.dateFromXmlDate(createdDate));
         }
         else
         {
            // TODO: a null created/modified date shouldn't happen!? - remove this later
            props.put("createddate", null);
         }
         
         String modifiedDate = getQNameProperty(childProps, "modifieddate", false);
         if (modifiedDate != null)
         {
            props.put("modifieddate", Conversion.dateFromXmlDate(createdDate));
         }
         else
         {
            // TODO: a null created/modified date shouldn't happen!?
            props.put("modifieddate", null);
         }
         
         // push the propeties into the Node
         node.setProperties(props);
         
         items.add(node);
      }
      
      /* -- Example of Search code -- leave here for now
       * -- Note: The passed in path was "*" for a root node search
      
      // get the searcher object and perform the search of the root node
      String s = MessageFormat.format(SEARCH_PATH, new Object[] {path});
      ResultSet results = this.searchService.query(rootNodeRef.getStoreRef(), "lucene", s, null, null);
      
      // create a list of items from the results
      List<Node> items = new ArrayList<Node>(results.length());
      if (results.length() != 0)
      {
         for (ResultSetRow row: results)
         {
            Node node = new Node(row.getQName().getNamespaceURI());  // TODO: where does Type come from?
            Map<String, Object> props = new HashMap<String, Object>(7, 1.0f);
            
            String name = row.getQName().getLocalName();
            props.put("name", name);
            
            props.put("description", getValueProperty(row, "description", true));
            
            String createdDate = getValueProperty(row, "createddate", false);
            if (createdDate != null)
            {
               props.put("createddate", Conversion.dateFromXmlDate(createdDate));
            }
            else
            {
               // TODO: a null created/modified date shouldn't happen!?
               props.put("createddate", null);
            }
            
            String modifiedDate = getValueProperty(row, "modifieddate", false);
            if (modifiedDate != null)
            {
               props.put("modifieddate", Conversion.dateFromXmlDate(createdDate));
            }
            else
            {
               // TODO: a null created/modified date shouldn't happen!?
               props.put("modifieddate", null);
            }
            
            node.setProperties(props);
            
            items.add(node);
         }
      }*/
      
      return items;
   }
   
   private String getQNameProperty(Map<QName, Serializable> props, String property, boolean convertNull)
   {
      String value = null;
      
      QName propQName = QName.createQName(NamespaceService.ACTIVITI_URI, property);
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
   
   private String getValueProperty(ResultSetRow row, String name, boolean convertNull)
   {
      Value value = row.getValue(QName.createQName(NamespaceService.ACTIVITI_URI, name));
      String property = null;
      if (value != null)
      {
         property = value.getString();
      }
      
      if (convertNull == true && property == null)
      {
         property = "";
      }
      
      return property;
   }
   
   
   // ------------------------------------------------------------------------------
   // Navigation action event handlers

   /**
    * Action called when a folder space is clicked.
    * Navigate into the space.
    */
   public void clickSpace(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         s_logger.debug("Clicked Space Id: " + id);
         // TODO: wrap up common property set in the Node bean - and hide the NodeService usage
         //       this will hide the nasty code required to get simple props like "name"!
         NodeRef ref = new NodeRef(Repository.getStoreRef(), id);
         String name = this.nodeService.getPrimaryParent(ref).getName().getLocalName();
         
         // get the current breadcrumb location and append a new handler to it
         // our handler know the ID of the selected node and the display label for it
         List<IBreadcrumbHandler> location = this.navigator.getLocation();
         location.add(new BrowseBreadcrumbHandler(id, name));
         
         // set the current node Id ready for page refresh
         getNavigator().setCurrentNodeId(id);
         
         // clear the value for the list component - will cause it to re-bind to it's data and refresh
         // TODO: need a decoupled way to refresh components - a view-local context event service?
         // TODO: remove this weakness!
         UIRichList richList = (UIRichList)link.findComponent("browseList");
         if (richList != null)
         {
            s_logger.debug("Clearing RichList data source.");
            richList.setValue(null);
         }
      }
   }
   

   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class to handle breadcrumb interaction for Browse pages
    */
   private class BrowseBreadcrumbHandler implements IBreadcrumbHandler
   {
      /**
       * Constructor
       * 
       * @param nodeId     The nodeID for this browse navigation element
       * @param label      Element label
       */
      public BrowseBreadcrumbHandler(String nodeId, String label)
      {
         this.label = label;
         this.nodeId = nodeId;
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
         // All browse breadcrumb element relate to a Node Id - when selected we
         // set the current node id
         getNavigator().setCurrentNodeId( nodeId );
         
         getNavigator().setLocation( (List)breadcrumb.getValue() );
         
         // return to browse page
         return "browse";
      }
      
      private String nodeId;
      private String label;
   }

   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static Logger s_logger = Logger.getLogger(BrowseBean.class);
   
   private static final String SEARCH_PATH = "PATH:\"/" + NamespaceService.ACTIVITI_PREFIX + ":{0}\"";
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The SearchService to be used by the bean */
   private Searcher searchService;
   
   /** The NavigationBean reference */
   private NavigationBean navigator;
   
   /** The current browse view mode - set to a well known IRichListRenderer name */
   private String browseViewMode = "details";
}
