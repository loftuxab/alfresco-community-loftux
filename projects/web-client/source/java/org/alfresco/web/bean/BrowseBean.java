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
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.ResultSet;
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
import org.alfresco.web.ui.common.renderer.data.RichListRenderer;
import org.alfresco.web.ui.repo.component.UINodeDescendants;
import org.alfresco.web.ui.repo.component.UISimpleSearch;
import org.apache.log4j.Logger;

/**
 * @author Kevin Roast
 */
public class BrowseBean implements IContextListener
{
   public BrowseBean()
   {
      UIContextService.getInstance(FacesContext.getCurrentInstance()).registerBean(this);
   }
   
   
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
    * @return Returns the browsePageSize.
    */
   public int getBrowsePageSize()
   {
      return browsePageSize;
   }
   
   /**
    * @param browsePageSize The browsePageSize to set.
    */
   public void setBrowsePageSize(int browsePageSize)
   {
      this.browsePageSize = browsePageSize;
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
    * @return The document node being used for the current operation
    */
   public Node getDocument()
   {
      return document;
   }

   /**
    * @param document The document node to be used for the current operation
    */
   public void setDocument(Node document)
   {
      this.document = document;
   }

   /**
    * @param contentRichList The contentRichList to set.
    */
   public void setContentRichList(UIRichList browseRichList)
   {
      this.contentRichList = browseRichList;
   }
   
   /**
    * @return Returns the contentRichList.
    */
   public UIRichList getContentRichList()
   {
      return this.contentRichList;
   }
   
   /**
    * @param spacesRichList The spacesRichList to set.
    */
   public void setSpacesRichList(UIRichList detailsRichList)
   {
      this.spacesRichList = detailsRichList;
   }
   
   /**
    * @return Returns the spacesRichList.
    */
   public UIRichList getSpacesRichList()
   {
      return this.spacesRichList;
   }
   
   /**
    * Page accessed bean method to get the container nodes currently being browsed
    * 
    * @return List of container Node objects for the current browse location
    */
   public List<Node> getNodes()
   {
      // the references to container nodes and content nodes are transient for one use only
      // we do this so we only query/search once - as we cannot distinguish between node types
      // until after the query. The logic is a bit confusing but otherwise we would need to
      // perform the same query or search twice for every screen refresh.
      if (this.containerNodes == null)
      {
         if (navigator.getSearchText() == null)
         {
            queryBrowseNodes(getNavigator().getCurrentNodeId());
         }
         else
         {
            searchBrowseNodes(navigator.getSearchText(), navigator.getSearchMode());
         }
      }
      List<Node> result = this.containerNodes;
      this.containerNodes = null;
      
      return result;
   }
   
   /**
    * Page accessed bean method to get the content nodes currently being browsed
    * 
    * @return List of content Node objects for the current browse location
    */
   public List<Node> getContent()
   {
      // see comment in getNodes() above for reasoning here
      if (this.contentNodes == null)
      {
         if (navigator.getSearchText() == null)
         {
            queryBrowseNodes(getNavigator().getCurrentNodeId());
         }
         else
         {
            searchBrowseNodes(navigator.getSearchText(), navigator.getSearchMode());
         }
      }
      List<Node> result = this.contentNodes;
      this.contentNodes = null;
      
      return result;
   }
   
   
   // ------------------------------------------------------------------------------
   // IContextListener implementation 
   
   /**
    * @see org.alfresco.web.bean.IContextListener#contextUpdated()
    */
   public void contextUpdated()
   {
      s_logger.info("*****contextUpdated() listener called");
      invalidateComponents();
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
      
      // get the view mode ID
      String viewMode = viewList.getValue().toString();
      
      // set the page size based on the style of display
      if (viewMode.equals(RichListRenderer.DetailsViewRenderer.VIEWMODEID))
      {
         setBrowsePageSize(10);
      }
      else if (viewMode.equals(RichListRenderer.IconViewRenderer.VIEWMODEID))
      {
         setBrowsePageSize(12);
      }
      else if (viewMode.equals(RichListRenderer.ListViewRenderer.VIEWMODEID))
      {
         setBrowsePageSize(5);
      }
      else
      {
         // in-case another view mode appears
         setBrowsePageSize(10);
      }
      if (s_logger.isDebugEnabled())
         s_logger.debug("Browse view page size set to: " + getBrowsePageSize());
      
      // push the view mode into the lists
      setBrowseViewMode(viewMode);
   }
   
   
   // ------------------------------------------------------------------------------
   // Helper methods
   
   /**
    * Query a list of nodes for the specified parent node Id
    * 
    * @param parentNodeId     Id of the parent node or null for the root node
    */
   private void queryBrowseNodes(String parentNodeId)
   {
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
         this.containerNodes = new ArrayList<Node>(childRefs.size());
         this.contentNodes = new ArrayList<Node>(childRefs.size());
         for (ChildAssocRef ref: childRefs)
         {
            QName qname = ref.getQName();
            
            // create our Node representation
            Node node = new Node(ref.getChildRef(), qname.getNamespaceURI());  // TODO: where does Type come from?
            Map<String, Object> props = new HashMap<String, Object>(7, 1.0f);
            
            // convert the rest of the well known properties
            Map<QName, Serializable> childProps = this.nodeService.getProperties(ref.getChildRef());
            
            // name and ID always exist
            props.put("id", ref.getChildRef().getId());
            props.put("name", getNameForNode(ref.getChildRef()));
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
            
            // push the propeties into the Node
            node.setProperties(props);
            
            // TODO: resolve icon etc. some how using this e.g. on either make an ActionLink image
            //       property smart or better set in the Node wrapper as property
            if (nodeService.hasAspect(ref.getChildRef(), DictionaryBootstrap.ASPECT_SPACE))
            {
               this.containerNodes.add(node);
            }
            else if (nodeService.getType(ref.getChildRef()).equals(DictionaryBootstrap.TYPE_FILE))
            {
               this.contentNodes.add(node);
            }
            else
            {
               if (s_logger.isDebugEnabled())
                  s_logger.debug("Found neither a Space or File node:\n   " + node.getName() + "\n   " + node.getPath() + "\n   " + node.getType());
            }
         }
      }
      catch (InvalidNodeRefException refErr)
      {
         Utils.addErrorMessage( MessageFormat.format(ERROR_NODEREF, new Object[] {parentNodeId}) );
         this.containerNodes = Collections.<Node>emptyList();
         this.contentNodes = Collections.<Node>emptyList();
      }
   }
   
   /**
    * Search for a list of nodes using the specific search string
    * 
    * @param searchText       Search text
    * @param searchMode       Search mode to use (see UISimpleSearch constants)
    */
   private void searchBrowseNodes(String searchText, int searchMode)
   {
      // get the searcher object and perform the search of the root node
      String query = buildSearchQuery(searchText, searchMode);
      try
      {
         if (s_logger.isDebugEnabled())
            s_logger.debug("Searching using path: " + query);
         ResultSet results = this.searchService.query(Repository.getStoreRef(), "lucene", query, null, null);
         if (s_logger.isDebugEnabled())
            s_logger.debug("Search results returned: " + results.length());
         
         // create a list of items from the results
         this.containerNodes = new ArrayList<Node>(results.length());
         this.contentNodes = new ArrayList<Node>(results.length());
         if (results.length() != 0)
         {
            for (ResultSetRow row: results)
            {
               NodeRef ref = row.getNodeRef();
               Node node = new Node(ref, row.getQName().getNamespaceURI());
               Map<String, Object> props = new HashMap<String, Object>(7, 1.0f);
               
               // name and ID always exist
               props.put("id", ref.getId());
               String name = getValueProperty(row, "name", false);
               if (name == null)
               {
                  name = getNameForNode(ref);
               }
               props.put("name", name);
               props.put("nodeRef", ref);
               
               // other properties which may exist
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
               
               // TODO: resolve icon etc. some how using this e.g. on either make an ActionLink image
               //       property smart or better set in the Node wrapper as property
               if (nodeService.hasAspect(ref, DictionaryBootstrap.ASPECT_SPACE))
               {
                  this.containerNodes.add(node);
               }
               else if (nodeService.getType(ref).equals(DictionaryBootstrap.TYPE_FILE))
               {
                  this.contentNodes.add(node);
               }
               else
               {
                  if (s_logger.isDebugEnabled())
                     s_logger.debug("Found neither a Space or File node:\n   " + node.getName() + "\n   " + node.getPath() + "\n   " + node.getType());
               }
            }
         }
      }
      catch (Exception err)
      {
         s_logger.info("Search failed for: " + query);
         Utils.addErrorMessage( MessageFormat.format(ERROR_SEARCH, new Object[] {err.getMessage()}) );
         this.containerNodes = Collections.<Node>emptyList();
         this.contentNodes = Collections.<Node>emptyList();
      }
   }

   /**
    * Build the search query string
    * 
    * @param text    Search text
    * @param mode    Search mode
    * 
    * @return prepared search query string
    */
   private String buildSearchQuery(String text, int mode)
   {
      String query;
      switch (mode)
      {
         case UISimpleSearch.SEARCH_ALL:
            query = "+PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:" + text + "*";
            //query = MessageFormat.format(SEARCH_ALL, new Object[] {text});
            break;
         
         case UISimpleSearch.SEARCH_FILE_NAMES:
            query = "+TYPE:\"{" + NamespaceService.ALFRESCO_URI + "}file\"" + " +PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:" + text + "*";
            //query = MessageFormat.format(SEARCH_FILE_NAME, new Object[] {text});
            break;
         
         case UISimpleSearch.SEARCH_FILE_NAMES_CONTENTS:
            query = "+TYPE:\"{" + NamespaceService.ALFRESCO_URI + "}file\"" + " +PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:" + text + "*";
            //query = MessageFormat.format(SEARCH_FILE_NAME_CONTENT, new Object[] {text});
            break;
         
         case UISimpleSearch.SEARCH_SPACE_NAMES:
            query = "+TYPE:\"{" + NamespaceService.ALFRESCO_URI + "}folder\"" + " +PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:" + text + "*";
            //query = MessageFormat.format(SEARCH_FOLDER_NAME, new Object[] {text});
            break;
         
         default:
            throw new IllegalStateException("Unknown search mode specified: " + mode);
      }
      
      return query;
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
    * Action called from the Simple Search component
    * 
    */
   public void search(ActionEvent event)
   {
      // setup the search text string on the top-level navigation handler
      UISimpleSearch search = (UISimpleSearch)event.getComponent();
      navigator.setSearchText(search.getLastSearch());
      navigator.setSearchMode(search.getSearchMode());
      
      navigateBrowseScreen();
   }
   
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
            
            // refresh UI based on node selection
            refreshUI(ref, link);
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage( MessageFormat.format(ERROR_NODEREF, new Object[] {id}) );
         }
      }
   }
   
   /**
    * Action called when a content item is clicked.
    */
   public void clickContent(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         if (s_logger.isDebugEnabled())
            s_logger.debug("Setup for action, setting current document to: " + id);
         
         try
         {
            // creat the node ref, then our node representation
            NodeRef ref = new NodeRef(Repository.getStoreRef(), id);
            Node node = new Node(ref, "FILE");
            
            // remember the document
            setDocument(node);
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage( MessageFormat.format(ERROR_NODEREF, new Object[] {id}) );
         }
      }
      else
      {
         setDocument(null);
      }
   }
   
   /**
    * Action called when a folders direct descendant (in the 'list' browse mode) is clicked.
    * Navigate into the the descendant space.
    */
   public void clickDescendantSpace(ActionEvent event)
   {
      UINodeDescendants.NodeSelectedEvent nodeEvent = (UINodeDescendants.NodeSelectedEvent)event;
      NodeRef nodeRef = nodeEvent.NodeReference;
      if (nodeRef == null)
      {
         throw new IllegalStateException("NodeRef returned from UINodeDescendants.NodeSelectedEvent cannot be null!");
      }
      
      if (s_logger.isDebugEnabled())
         s_logger.debug("*Selected noderef Id: " + nodeRef.getId());
      
      try
      {
         // user can either select a descendant of a node display on the page which means we
         // must add the it's parent and itself to the breadcrumb
         List<IBreadcrumbHandler> location = this.navigator.getLocation();
         ChildAssocRef parentAssocRef = nodeService.getPrimaryParent(nodeRef);
         
         if (s_logger.isDebugEnabled())
         {
            s_logger.debug("Selected item getPrimaryParent().getChildRef() noderef Id:  " + parentAssocRef.getChildRef().getId());
            s_logger.debug("Selected item getPrimaryParent().getParentRef() noderef Id: " + parentAssocRef.getParentRef().getId());
            s_logger.debug("Current value getNavigator().getCurrentNodeId() noderef Id: " + getNavigator().getCurrentNodeId());
         }
         
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
         if (s_logger.isDebugEnabled())
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
            props.put("name", getNameForNode(ref));
            node.setProperties(props);
            
            // prepare a node for the action context
            setActionSpace(node);
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage( MessageFormat.format(ERROR_NODEREF, new Object[] {id}) );
         }
      }
      else
      {
         setActionSpace(null);
      }
      
      invalidateComponents();
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
            if (s_logger.isDebugEnabled())
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
            
            // setting the outcome will show the browse view
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
    * Helper to get the display name for a Node.
    * The method will attempt to use the "name" attribute, if not found it will revert to using
    * the QName.getLocalName() retrieved from the primary parent relationship.
    * 
    * @param ref     NodeRef
    * 
    * @return display name string for the specified Node.
    */
   private String getNameForNode(NodeRef ref)
   {
      String name;
      
      // try to find a display "name" property for this node
      Object nameProp = this.nodeService.getProperty(ref, QNAME_NAME);
      if (nameProp != null)
      {
         name = nameProp.toString();
      }
      else
      {
         // revert to using QName if not found
         name = this.nodeService.getPrimaryParent(ref).getQName().getLocalName();
      }
      
      return name;
   }
   
   /**
    * Refresh the UI after a Space selection change. Adds the selected space to the breadcrumb
    * location path and also updates the list components in the UI.
    * 
    * @param ref     NodeRef of the selected space
    * @param comp    UIComponent responsible for the UI update
    */
   private void refreshUI(NodeRef ref, UIComponent comp)
   {
      // get the current breadcrumb location and append a new handler to it
      // our handler know the ID of the selected node and the display label for it
      List<IBreadcrumbHandler> location = this.navigator.getLocation();
      String name = getNameForNode(ref);
      location.add(new BrowseBreadcrumbHandler(ref.getId(), name));
      
      // set the current node Id ready for page refresh
      getNavigator().setCurrentNodeId(ref.getId());
   }
   
   /**
    * Invalidate list component state after an action which changes the UI context
    */
   private void invalidateComponents()
   {
      if (s_logger.isDebugEnabled())
         s_logger.debug("Invalidating UI List Components...");
      
      // clear the value for the list components - will cause re-bind to it's data and refresh
      if (this.contentRichList != null)
      {
         this.contentRichList.setValue(null);
      }
      if (this.spacesRichList != null)
      {
         this.spacesRichList.setValue(null);
      }
   }
   
   /**
    * @return whether the current View ID is the "browse" screen
    */
   private boolean isViewCurrent()
   {
      return (FacesContext.getCurrentInstance().getViewRoot().getViewId().equals(BROWSE_VIEW_ID));
   }
   
   /**
    * Perform navigation to the browse screen if it is not already the current View
    */
   private void navigateBrowseScreen()
   {
      if (isViewCurrent() == false)
      {
         FacesContext fc = FacesContext.getCurrentInstance();
         fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "browse");
      }
   }
   

   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class to handle breadcrumb interaction for Browse pages
    */
   private class BrowseBreadcrumbHandler implements IBreadcrumbHandler
   {
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
         
         // return to browse page if required
         return (isViewCurrent() ? null : "browse"); 
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
   private static final String ERROR_SEARCH  = "Search failed during to a system error: {0}";
   
   public static final String BROWSE_VIEW_ID = "/jsp/browse/browse.jsp";
   
   private static final QName QNAME_NAME = QName.createQName(NamespaceService.ALFRESCO_URI, "name");
   
   private static Logger s_logger = Logger.getLogger(BrowseBean.class);
   
   //private static final String SEARCH_PATH1 = "PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":{0}\"";
   // TODO: AndyH said he will fix QNAME to prepend // later - so this should work in the future
   //private static final String SEARCH_PATH2 = "+QNAME:\"" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:{0}*";
   private static final String SEARCH_PATH         = "+PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:{0}*";
   
   private static final String SEARCH_ALL               = "+PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:{0}*";
   private static final String SEARCH_FILE_NAME         = "+TYPE:\"{" + NamespaceService.ALFRESCO_URI + "}file\"" + " +PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:{0}*";
   private static final String SEARCH_FILE_NAME_CONTENT = "+TYPE:\"{" + NamespaceService.ALFRESCO_URI + "}file\"" + " +PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:{0}*";
   private static final String SEARCH_FOLDER_NAME       = "+TYPE:\"{" + NamespaceService.ALFRESCO_URI + "}folder\"" + " +PATH:\"//" + NamespaceService.ALFRESCO_PREFIX + ":*\" +QNAME:{0}*";
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The SearchService to be used by the bean */
   private Searcher searchService;
   
   /** The NavigationBean reference */
   private NavigationBean navigator;
   
   /** Component references */
   private UIRichList spacesRichList;
   private UIRichList contentRichList;
   
   /** Transient lists */
   private List<Node> containerNodes = null;
   private List<Node> contentNodes = null;
   
   /** The current space and it's properties - if any */
   private Node actionSpace;
   
   /** The current document */
   private Node document;
   
   /** The current browse view mode - set to a well known IRichListRenderer name */
   private String browseViewMode = "details";
   
   /** The current browse view page size */
   private int browsePageSize = 10;
}
