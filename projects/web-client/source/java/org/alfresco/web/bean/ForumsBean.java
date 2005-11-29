/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.bean;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.config.ConfigService;
import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.context.IContextListener;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.repository.MapNode;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.NodePropertyResolver;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.config.ClientConfigElement;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIModeList;
import org.alfresco.web.ui.common.component.data.UIRichList;
import org.alfresco.web.ui.common.renderer.data.RichListRenderer;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Bean providing properties and behaviour for the forums screens.
 * 
 * @author gavinc
 */
public class ForumsBean implements IContextListener
{
   private static Logger logger = Logger.getLogger(ForumsBean.class);
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The ContentService to be used by the bean */
   private ContentService contentService;
   
   /** ConfigService bean reference */
   private ConfigService configService;
   
   /** The DictionaryService bean reference */
   private DictionaryService dictionaryService;
   
   /** The browse bean */
   private BrowseBean browseBean;
   
   /** The NavigationBean bean reference */
   private NavigationBean navigator;
   
   /** Client configuration object */
   private ClientConfigElement clientConfig = null;
   
   /** Component references */
   private UIRichList forumsRichList;
   private UIRichList forumRichList;
   private UIRichList topicRichList;

   /** Node lists */
   private List<Node> forums;
   private List<Node> topics;
   private List<Node> posts;
   
   /** The current forums view mode - set to a well known IRichListRenderer identifier */
   private String forumsViewMode;
   
   /** The current forums view page size */
   private int forumsPageSize;
   
   /** The current forum view mode - set to a well known IRichListRenderer identifier */
   private String forumViewMode;
   
   /** The current forum view page size */
   private int forumPageSize = 20;
   
   /** The current topic view mode - set to a well known IRichListRenderer identifier */
   private String topicViewMode;
   
   /** The current topic view page size */
   private int topicPageSize = 20;
   
   // ------------------------------------------------------------------------------
   // Construction 

   /**
    * Default Constructor
    */
   public ForumsBean()
   {
      UIContextService.getInstance(FacesContext.getCurrentInstance()).registerBean(this);
   }
   
   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * @param nodeService The NodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * Sets the content service to use
    * 
    * @param contentService The ContentService
    */
   public void setContentService(ContentService contentService)
   {
      this.contentService = contentService;
   }

   /**
    * @param dictionaryService The DictionaryService to set.
    */
   public void setDictionaryService(DictionaryService dictionaryService)
   {
      this.dictionaryService = dictionaryService;
   }
   
   /**
    * @param configService The ConfigService to set.
    */
   public void setConfigService(ConfigService configService)
   {
      this.configService = configService;
   }
   
   /**
    * Sets the BrowseBean instance to use to retrieve the current document
    * 
    * @param browseBean BrowseBean instance
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * @param forumsRichList The forumsRichList to set.
    */
   public void setForumsRichList(UIRichList forumsRichList)
   {
      this.forumsRichList = forumsRichList;
      if (this.forumsRichList != null)
      {
         this.forumsRichList.setInitialSortColumn("name");
      }
   }
   
   /**
    * @return Returns the forumsRichList.
    */
   public UIRichList getForumsRichList()
   {
      return this.forumsRichList;
   }
   
   /**
    * @return Returns the forums View mode. See UIRichList
    */
   public String getForumsViewMode()
   {
      if (this.clientConfig == null)
      {
         initFromClientConfig();
      }
      
      return this.forumsViewMode;
   }
   
   /**
    * @param forumsViewMode      The forums View mode to set. See UIRichList.
    */
   public void setForumsViewMode(String forumsViewMode)
   {
      this.forumsViewMode = forumsViewMode;
   }
   
   /**
    * @return Returns the forumsPageSize.
    */
   public int getForumsPageSize()
   {
      if (this.clientConfig == null)
      {
         initFromClientConfig();
      }
      
      return this.forumsPageSize;
   }
   
   /**
    * @param forumsPageSize The forumsPageSize to set.
    */
   public void setForumsPageSize(int forumsPageSize)
   {
      this.forumsPageSize = forumsPageSize;
   }
   
   /**
    * @param topicRichList The topicRichList to set.
    */
   public void setTopicRichList(UIRichList topicRichList)
   {
      this.topicRichList = topicRichList;
      
      if (this.topicRichList != null)
      {
         this.topicRichList.setInitialSortColumn("created");
         
         // set the initial sort direction
         if (this.clientConfig != null)
         {
            String sortDir = this.clientConfig.getDefaultTopicSortDir();
            this.topicRichList.setInitialSortDescending("descending".equalsIgnoreCase(sortDir));
         }
      }
   }
   
   /**
    * @return Returns the topicRichList.
    */
   public UIRichList getTopicRichList()
   {
      return this.topicRichList;
   }
   
   /**
    * @return Returns the topics View mode. See UIRichList
    */
   public String getTopicViewMode()
   {
      if (this.clientConfig == null)
      {
         initFromClientConfig();
      }
      
      return this.topicViewMode;
   }
   
   /**
    * @param topicViewMode      The topic View mode to set. See UIRichList.
    */
   public void setTopicViewMode(String topicViewMode)
   {
      this.topicViewMode = topicViewMode;
   }
   
   /**
    * @return Returns the topicsPageSize.
    */
   public int getTopicPageSize()
   {
      if (this.clientConfig == null)
      {
         initFromClientConfig();
      }
      
      return this.topicPageSize;
   }
   
   /**
    * @param topicPageSize The topicPageSize to set.
    */
   public void setTopicPageSize(int topicPageSize)
   {
      this.topicPageSize = topicPageSize;
   }
   
   /**
    * @param forumRichList The forumRichList to set.
    */
   public void setForumRichList(UIRichList forumRichList)
   {
      this.forumRichList = forumRichList;
      
      if (this.forumRichList != null)
      {
         this.forumRichList.setInitialSortColumn("name");
      }
   }
   
   /**
    * @return Returns the forumRichList.
    */
   public UIRichList getForumRichList()
   {
      return this.forumRichList;
   }
   
   /**
    * @return Returns the forum View mode. See UIRichList
    */
   public String getForumViewMode()
   {
      if (this.clientConfig == null)
      {
         initFromClientConfig();
      }
      
      return this.forumViewMode;
   }
   
   /**
    * @param forumViewMode      The forum View mode to set. See UIRichList.
    */
   public void setForumViewMode(String forumViewMode)
   {
      this.forumViewMode = forumViewMode;
   }
   
   /**
    * @return Returns the forumPageSize.
    */
   public int getForumPageSize()
   {
      if (this.clientConfig == null)
      {
         initFromClientConfig();
      }
      
      return this.forumPageSize;
   }
   
   /**
    * @param forumPageSize The forumPageSize to set.
    */
   public void setForumPageSize(int forumPageSize)
   {
      this.forumPageSize = forumPageSize;
   }
   
   public List<Node> getForums()
   {
      if (this.forums == null)
      {
         getNodes();
      }
      
      return this.forums;
   }
   
   public List<Node> getTopics()
   {
      if (this.topics == null)
      {
         getNodes();
      }
      
      return this.topics;
   }
   
   public List<Node> getPosts()
   {
      if (this.posts == null)
      {
         getNodes();
      }
      
      return this.posts;
   }
   
   private void getNodes()
   {
      long startTime = 0;
      if (logger.isDebugEnabled())
         startTime = System.currentTimeMillis();
      
      UserTransaction tx = null;
      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(context, true);
         tx.begin();
         
         // get the current space from NavigationBean
         String parentNodeId = this.navigator.getCurrentNodeId();
         
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
         
         // TODO: can we improve the Get here with an API call for children of a specific type?
         List<ChildAssociationRef> childRefs = this.nodeService.getChildAssocs(parentRef);
         this.forums = new ArrayList<Node>(childRefs.size());
         this.topics = new ArrayList<Node>(childRefs.size());
         this.posts = new ArrayList<Node>(childRefs.size());
         
         for (ChildAssociationRef ref: childRefs)
         {
            // create our Node representation from the NodeRef
            NodeRef nodeRef = ref.getChildRef();
            
            if (this.nodeService.exists(nodeRef))
            {
               // find it's type so we can see if it's a node we are interested in
               QName type = this.nodeService.getType(nodeRef);
               
               // make sure the type is defined in the data dictionary
               TypeDefinition typeDef = this.dictionaryService.getType(type);
               
               if (typeDef != null)
               {
                  // extract forums, forum, topic and post types
                  
                  if (this.dictionaryService.isSubClass(type, ContentModel.TYPE_SYSTEM_FOLDER) == false)
                  {
                     if (this.dictionaryService.isSubClass(type, ForumModel.TYPE_FORUMS) || 
                         this.dictionaryService.isSubClass(type, ForumModel.TYPE_FORUM)) 
                     {
                        // create our Node representation
                        MapNode node = new MapNode(nodeRef, this.nodeService, true);
                        node.addPropertyResolver("icon", this.browseBean.resolverSpaceIcon);
                        
                        this.forums.add(node);
                     }
                     if (this.dictionaryService.isSubClass(type, ForumModel.TYPE_TOPIC)) 
                     {
                        // create our Node representation
                        MapNode node = new MapNode(nodeRef, this.nodeService, true);
                        node.addPropertyResolver("icon", this.browseBean.resolverSpaceIcon);
                        node.addPropertyResolver("replies", this.resolverReplies);
                        
                        this.topics.add(node);
                     }
                     else if (this.dictionaryService.isSubClass(type, ForumModel.TYPE_POST))
                     {
                        // create our Node representation
                        MapNode node = new MapNode(nodeRef, this.nodeService, true);
                        
                        this.browseBean.setupDataBindingProperties(node);
                        node.addPropertyResolver("message", this.resolverContent);
                        
                        this.posts.add(node);
                     }
                  }
               }
               else
               {
                  if (logger.isEnabledFor(Priority.WARN))
                     logger.warn("Found invalid object in database: id = " + nodeRef + ", type = " + type);
               }
            }
         }
         
         // commit the transaction
         tx.commit();
      }
      catch (InvalidNodeRefException refErr)
      {
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), Repository.ERROR_NODEREF), new Object[] {refErr.getNodeRef()}) );
         this.forums = Collections.<Node>emptyList();
         this.topics = Collections.<Node>emptyList();
         this.posts = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      catch (Throwable err)
      {
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), Repository.ERROR_GENERIC), err.getMessage()), err);
         this.forums = Collections.<Node>emptyList();
         this.topics = Collections.<Node>emptyList();
         this.posts = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      
      if (logger.isDebugEnabled())
      {
         long endTime = System.currentTimeMillis();
         logger.debug("Time to query and build forums nodes: " + (endTime - startTime) + "ms");
      }
   }
   
   
   // ------------------------------------------------------------------------------
   // IContextListener implementation 
   
   /**
    * @see org.alfresco.web.app.context.IContextListener#contextUpdated()
    */
   public void contextUpdated()
   {
      if (logger.isDebugEnabled())
         logger.debug("Invalidating forums components...");
      
      // clear the value for the list components - will cause re-bind to it's data and refresh
      if (this.forumsRichList != null)
      {
         this.forumsRichList.setValue(null);
         if (this.forumsRichList.getInitialSortColumn() == null)
         {
            this.forumsRichList.setInitialSortColumn("name");
         }
      }
      
      if (this.forumRichList != null)
      {
         this.forumRichList.setValue(null);
         if (this.forumRichList.getInitialSortColumn() == null)
         {
            this.forumRichList.setInitialSortColumn("name");
         }
      }
      
      if (this.topicRichList != null)
      {
         this.topicRichList.setValue(null);
         if (this.topicRichList.getInitialSortColumn() == null)
         {
            this.topicRichList.setInitialSortColumn("created");
            
            // set the initial sort direction
            if (this.clientConfig != null)
            {
               String sortDir = this.clientConfig.getDefaultTopicSortDir();
               this.topicRichList.setInitialSortDescending("descending".equalsIgnoreCase(sortDir));
            }
         }
      }
      
      // reset the lists
      this.forums = null;
      this.topics = null;
      this.posts = null;
   }
   
   // ------------------------------------------------------------------------------
   // Navigation action event handlers 
   
   /**
    * Change the current forums view mode based on user selection
    * 
    * @param event      ActionEvent
    */
   public void forumsViewModeChanged(ActionEvent event)
   {
      UIModeList viewList = (UIModeList)event.getComponent();
      
      // get the view mode ID
      String viewMode = viewList.getValue().toString();
      
      // push the view mode into the lists
      setForumsViewMode(viewMode);
   }
   
   /**
    * Change the current forum view mode based on user selection
    * 
    * @param event      ActionEvent
    */
   public void forumViewModeChanged(ActionEvent event)
   {
//      UIModeList viewList = (UIModeList)event.getComponent();
      
      // get the view mode ID
//      String viewMode = viewList.getValue().toString();
      
      // push the view mode into the lists
//      setForumViewMode(viewMode);
   }
   
   /**
    * Change the current topic view mode based on user selection
    * 
    * @param event      ActionEvent
    */
   public void topicViewModeChanged(ActionEvent event)
   {
      // TODO: enable when we have the bubble view plugged in
      
//      UIModeList viewList = (UIModeList)event.getComponent();
      
      // get the view mode ID
//      String viewMode = viewList.getValue().toString();
      
      // push the view mode into the lists
//      setTopicViewMode(viewMode);
   }
   
   // ------------------------------------------------------------------------------
   // Property Resolvers
   
   public NodePropertyResolver resolverReplies = new NodePropertyResolver() {
      public Object get(Node node) 
      {
         // TODO: query the node for the number of posts inside the topic
         
         return "1";
      }
   };
   
   public NodePropertyResolver resolverContent = new NodePropertyResolver() {
      public Object get(Node node) 
      {
         String content = null;
         
         // get the content property from the node and retrieve the 
         // full content as a string (obviously should only be used
         // for small amounts of content)
         
         ContentReader reader = contentService.getReader(node.getNodeRef(), 
               ContentModel.PROP_CONTENT);
         
         if (reader != null)
         {
            content = reader.getContentString();
         }
         
         return content;
      }
   };
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   /**
    * Initialise default values from client configuration
    */
   private void initFromClientConfig()
   {
      this.clientConfig = (ClientConfigElement)this.configService.getGlobalConfig().getConfigElement(
            ClientConfigElement.CONFIG_ELEMENT_ID);
      
      this.forumsViewMode = clientConfig.getDefaultForumsView();
      
      if (RichListRenderer.DetailsViewRenderer.VIEWMODEID.equals(this.forumsViewMode))
      {
         this.forumsPageSize = this.clientConfig.getForumsDetailsPageSize();
      }
      else if (RichListRenderer.IconViewRenderer.VIEWMODEID.equals(this.forumsViewMode))
      {
         this.forumsPageSize = this.clientConfig.getForumsIconsPageSize();
      }
      else if (RichListRenderer.ListViewRenderer.VIEWMODEID.equals(this.forumsViewMode))
      {
         this.forumsPageSize = this.clientConfig.getForumsListPageSize();
      }
      else
      {
         // in case another view mode appears we should have a default
         this.forumsPageSize = 20;
      }
      
      this.forumViewMode = clientConfig.getDefaultForumView();
      
      if (RichListRenderer.DetailsViewRenderer.VIEWMODEID.equals(this.forumViewMode))
      {
         this.forumPageSize = this.clientConfig.getForumDetailsPageSize();
      }
//      else if (RichListRenderer.ForumBubbleViewRenderer.VIEWMODEID.equals(this.forumViewMode))
//      {
//         this.forumPageSize = this.clientConfig.getForumBubblePageSize();
//      }
      else
      {
         // in case another view mode appears we should have a default
         this.forumPageSize = 20;
      }
      
      this.topicViewMode = clientConfig.getDefaultTopicView();
      
      if (RichListRenderer.DetailsViewRenderer.VIEWMODEID.equals(this.topicViewMode))
      {
         this.topicPageSize = this.clientConfig.getTopicDetailsPageSize();
      }
//      else if (RichListRenderer.TopicBubbleViewRenderer.VIEWMODEID.equals(this.topicViewMode))
//      {
//         this.topicPageSize = this.clientConfig.getTopicBubblePageSize();
//      }
      else
      {
         // in case another view mode appears we should have a default
         this.topicPageSize = 20;
      }
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Set default forums view mode to: " + this.forumsViewMode);
         logger.debug("Set default forums page size to: " + this.forumsPageSize);
         logger.debug("Set default forum view mode to: " + this.forumViewMode);
         logger.debug("Set default forum page size to: " + this.forumPageSize);
         logger.debug("Set default topic view mode to: " + this.topicViewMode);
         logger.debug("Set default topic page size to: " + this.topicPageSize);
      }
   }
}
