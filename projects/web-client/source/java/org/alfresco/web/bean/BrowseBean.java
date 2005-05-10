/*
 * Created on 19-Apr-2005
 */
package org.alfresco.web.bean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.ResultSetRow;
import org.alfresco.repo.search.Searcher;
import org.alfresco.repo.value.ValueConverter;
import org.alfresco.util.Conversion;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.IBreadcrumbHandler;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.UIBreadcrumb;
import org.alfresco.web.ui.common.component.UIModeList;
import org.alfresco.web.ui.common.component.data.UIRichList;
import org.alfresco.web.ui.repo.component.UINodeDescendants;
import org.apache.log4j.Logger;

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
      return this.searchService;
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
      return this.navigator;
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
      return this.browseViewMode;
   }
   
   /**
    * @param browseViewMode      The browse View mode to set. See UIRichList.
    */
   public void setBrowseViewMode(String browseViewMode)
   {
      this.browseViewMode = browseViewMode;
   }
   
   /**
    * @return Returns the Space Node being used for the current browse screen action.
    */
   public Node getActionSpace()
   {
      return this.actionSpace;
   }
   
   /**
    * @param actionSpace     Set the Space Node to be used for the current browse screen action.
    */
   public void setActionSpace(Node actionSpace)
   {
      this.actionSpace = actionSpace;
   }
   
   /**
    * Page accessed bean method to get the nodes currently being browsed
    * 
    * @return List of Node objects for the current browse location
    */
   public List<Node> getNodes()
   {
      s_logger.info("getNodes() called in BrowseBean, querying...");
      
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
      List<Node> items = null;
      try
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
         
         List<ChildAssocRef> childRefs = this.nodeService.getChildAssocs(parentRef);
         items = new ArrayList<Node>(childRefs.size());
         for (ChildAssocRef ref: childRefs)
         {
            // display name is the QName localname part
            QName qname = ref.getQName();
            
            // create our Node representation
            Node node = new Node(ref.getChildRef(), qname.getNamespaceURI());  // TODO: where does Type come from?
            Map<String, Object> props = new HashMap<String, Object>(7, 1.0f);
            
            // convert the rest of the well known properties
            Map<QName, Serializable> childProps = this.nodeService.getProperties(ref.getChildRef());
            
            // name and ID always exist
            props.put("id", ref.getChildRef().getId());
            props.put("name", qname.getLocalName());
            props.put("nodeRef", ref.getChildRef());
            
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
            
            // TODO: resolve icon etc. some how using this e.g. on either make an ActionLink image
            //       property smart or better set in the Node wrapper as property
            //nodeService.hasAspect(ref.getChildRef(), DictionaryBootstrap.ASPECT_SPACE);
            
            // push the propeties into the Node
            node.setProperties(props);
            
            items.add(node);
         }
      }
      catch (InvalidNodeRefException refErr)
      {
         Utils.addErrorMessage( MessageFormat.format(ERROR_NODEREF, new Object[] {parentNodeId}) );
         items = Collections.<Node>emptyList();
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
   
   private static String getValueProperty(ResultSetRow row, String name, boolean convertNull)
   {
      Serializable value = row.getValue(QName.createQName(NamespaceService.ALFRESCO_URI, name));
      String property = null;
      if (value != null)
      {
         property = ValueConverter.convert(String.class, value);
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
         try
         {
            // TODO: wrap up common property set in the Node bean - and hide the NodeService usage
            //       this will hide the nasty code required to get simple props like "name"!
            NodeRef ref = new NodeRef(Repository.getStoreRef(), id);
            
            // refresh UI basedon node selection
            refreshUI(ref, link);
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage( MessageFormat.format(ERROR_NODEREF, new Object[] {id}) );
         }
      }
   }
   
   public void clickDescendantSpace(ActionEvent event)
   {
      UINodeDescendants.NodeSelectedEvent nodeEvent = (UINodeDescendants.NodeSelectedEvent)event;
      NodeRef nodeRef = nodeEvent.NodeReference;
      if (nodeRef == null)
      {
         throw new IllegalStateException("NodeRef returned from UINodeDescendants.NodeSelectedEvent cannot be null!");
      }
      
      s_logger.info("*****Selected noderef Id: " + nodeRef.getId());
      
      try
      {
         // user can either select a descendant of a node display on the page which means we
         // must add the it's parent and itself to the breadcrumb
         List<IBreadcrumbHandler> location = this.navigator.getLocation();
         ChildAssocRef parentAssocRef = nodeService.getPrimaryParent(nodeRef);
         
         s_logger.info("*****Selected item getPrimaryParent().getChildRef() noderef Id:  " + parentAssocRef.getChildRef().getId());
         s_logger.info("*****Selected item getPrimaryParent().getParentRef() noderef Id: " + parentAssocRef.getParentRef().getId());
         s_logger.info("*****Current value getNavigator().getCurrentNodeId() noderef Id: " + getNavigator().getCurrentNodeId());
         
         if (nodeEvent.IsParent == false)
         {
            // a descendant of the displayed node was selected
            // first refresh based on the parent and add to the breadcrumb
            refreshUI(parentAssocRef.getParentRef(), event.getComponent());
            
            // now add our selected node
            refreshUI(nodeRef, event.getComponent());
         }
         else
         {
            // else the parent ellipses i.e. the displayed node was selected
            refreshUI(nodeRef, event.getComponent());
         }
      }
      catch (InvalidNodeRefException refErr)
      {
         Utils.addErrorMessage( MessageFormat.format(ERROR_NODEREF, new Object[] {nodeRef.getId()}) );
      }
   }
   
   /**
    * Action event called by all Browse actions that need to setup a Space context
    * before an action page/wizard is called. The context will be a Node in setActionSpace() which
    * can be retrieved on the action page from BrowseBean.getActionSpace().
    * 
    * @param event   ActionEvent
    */
   public void spaceActionSetup(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         s_logger.debug("Setup for action, setting current space to: " + id);
         try
         {
            NodeRef ref = new NodeRef(Repository.getStoreRef(), id);
            QName qname = this.nodeService.getPrimaryParent(ref).getQName();
            
            // create our Node representation
            Node node = new Node(ref, qname.getNamespaceURI());  // TODO: where does Type come from?
            // TEMP: until we have a proper Node wrapper with lazy getting of props etc.
            // name and ID always exist
            Map<String, Object> props = new HashMap<String, Object>(3, 1.0f);
            props.put("id", id);
            props.put("name", qname.getLocalName());
            node.setProperties(props);
            
            setActionSpace(node);
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage( MessageFormat.format(ERROR_NODEREF, new Object[] {id}) );
         }
      }
      else
      {
         s_logger.warn("WARNING: setActionSpace called without a Space Id!");
         setActionSpace(null);
      }
   }
   
   /**
    * Handler called upon the completion of the Delete Space page
    * 
    * @return outcome
    */
   public String deleteSpaceOK()
   {
      String outcome = null;
      
      Node node = getActionSpace();
      if (node != null)
      {
         try
         {
            s_logger.debug("Trying to delete space Id: " + node.getId());
            this.nodeService.deleteNode(node.getNodeRef());
            
            // remove this node from the breadcrumb if required
            List<IBreadcrumbHandler> location = navigator.getLocation();
            IBreadcrumbHandler handler = location.get(location.size() - 1);
            if (handler instanceof BrowseBreadcrumbHandler)
            {
               // see if the current breadcrumb location is our node 
               if ( ((BrowseBreadcrumbHandler)handler).getNodeId().equals(node.getId()) == true )
               {
                  location.remove(location.size() - 1);
                  
                  // now work out which node to set the list to refresh against
                  if (location.size() != 0)
                  {
                     handler = location.get(location.size() - 1);
                     if (handler instanceof BrowseBreadcrumbHandler)
                     {
                        // change the current node Id
                        navigator.setCurrentNodeId(((BrowseBreadcrumbHandler)handler).getNodeId());
                     }
                     else
                     {
                        // TODO: shouldn't do this - but for now the user home dir is the root!
                        navigator.setCurrentNodeId(null);
                     }
                  }
               }
            }
            
            // clear action context
            setActionSpace(null);
            
            // setting the outcome will refresh the browse screen
            outcome = "browse";
         }
         catch (Throwable err)
         {
            Utils.addErrorMessage("Unable to delete Space due to system error: " + err.getMessage());
         }
      }
      else
      {
         s_logger.warn("WARNING: deleteSpaceOK called without a current Space!");
      }
      
      return outcome;
   }   
   
   // ------------------------------------------------------------------------------
   // Private helpers 
   
   /**
    * Refresh the UI after a Space selection change. Adds the selected space to the breadcrumb
    * location path and also updates the list components in the UI.
    * 
    * @param ref     NodeRef of the selected space
    * @param link    UIComponent responsible for the UI update
    */
   private void refreshUI(NodeRef ref, UIComponent link)
   {
      // get the current breadcrumb location and append a new handler to it
      // our handler know the ID of the selected node and the display label for it
      List<IBreadcrumbHandler> location = this.navigator.getLocation();
      String name = this.nodeService.getPrimaryParent(ref).getQName().getLocalName();
      location.add(new BrowseBreadcrumbHandler(ref.getId(), name));
      
      // set the current node Id ready for page refresh
      getNavigator().setCurrentNodeId(ref.getId());
      
      // clear the value for the list component - will cause it to re-bind to it's data and refresh
      // TODO: need a decoupled way to refresh components - a view-local context event service?
      // TODO: remove this weakness - use direct component binding here? e.g. a ref in this class
      UIRichList richList = (UIRichList)link.findComponent("browseList");
      if (richList != null)
      {
         s_logger.info("Clearing 'browseList' data source.");
         richList.setValue(null);
      }
      richList = (UIRichList)link.findComponent("detailsList");
      if (richList != null)
      {
         s_logger.info("Clearing 'detailsList' data source.");
         richList.setValue(null);
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
     * 
     */
    private static final long serialVersionUID = 3833183653173016630L;
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
       * @see org.alfresco.web.ui.common.component.IBreadcrumbHandler#navigationOutcome(org.alfresco.web.ui.common.component.UIBreadcrumb)
       */
      public String navigationOutcome(UIBreadcrumb breadcrumb)
      {
         // All browse breadcrumb element relate to a Node Id - when selected we
         // set the current node id
         getNavigator().setCurrentNodeId(this.nodeId);
         
         getNavigator().setLocation( (List)breadcrumb.getValue() );
         
         // return to browse page
         return "browse";
      }
      
      public String getNodeId()
      {
         return this.nodeId;
      }
      
      private String nodeId;
      private String label;
   }

   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static final String ERROR_NODEREF = "Unable to find the repository node referenced by Id: {0} - the node has probably been deleted from the database.";
   
   private static Logger s_logger = Logger.getLogger(BrowseBean.class);
   
   private static final String SEARCH_PATH = "PATH:\"/" + NamespaceService.ALFRESCO_PREFIX + ":{0}\"";
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The SearchService to be used by the bean */
   private Searcher searchService;
   
   /** The NavigationBean reference */
   private NavigationBean navigator;
   
   /** The current space and it's properties - if any */
   private Node actionSpace;
   
   /** The current browse view mode - set to a well known IRichListRenderer name */
   private String browseViewMode = "details";
}
