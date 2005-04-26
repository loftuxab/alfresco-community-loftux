/*
 * Created on 19-Apr-2005
 */
package com.activiti.web.bean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.QName;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.ResultSetRow;
import com.activiti.repo.search.Searcher;
import com.activiti.repo.search.Value;
import com.activiti.util.Conversion;
import com.activiti.web.bean.repository.Node;
import com.activiti.web.bean.repository.Repository;
import com.activiti.web.jsf.component.IBreadcrumbHandler;
import com.activiti.web.jsf.component.UIModeList;

/**
 * @author Kevin Roast
 */
public class BrowseBean
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
   
   public List<Node> getNodes()
   {
      return querySpaces("*");
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
   
   private List<Node> querySpaces(String path)
   {
      // get the node service and root node
      NodeRef rootNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
      
      Collection<ChildAssocRef> childRefs = this.nodeService.getChildAssocs(rootNodeRef);
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
         
         String name = qname.getLocalName();
         props.put("name", name);
         
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
         
         node.setProperties(props);
         
         items.add(node);
      }
      
      /* -- Example of Search code -- leave here for now
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
   // Inner classes
   
   /**
    * Class to handle breadcrumb interaction for Browse pages
    */
   private static class BrowseBreadcrumbHandler implements IBreadcrumbHandler
   {
      /**
       * Constructor
       * 
       * @param label      Element label
       */
      public BrowseBreadcrumbHandler(String label)
      {
         // TODO: this class will probably store an ID/QName of the Node it represents!
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
       * @see com.activiti.web.jsf.component.IBreadcrumbHandler#navigationOutcome()
       */
      public String navigationOutcome()
      {
         return null;
      }
      
      private String label;
   }

   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static final String SEARCH_PATH = "PATH:\"/" + NamespaceService.ACTIVITI_PREFIX + ":{0}\"";
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The SearchService to be used by the bean */
   private Searcher searchService;
   
   /** The current browse view mode - set to a well known IRichListRenderer name */
   private String browseViewMode = "details";
}
