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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.data.IDataContainer;
import org.alfresco.web.data.QuickSort;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.repo.component.template.TemplateNode;

/**
 * @author Kevin Roast
 */
public class DocumentPreviewBean
{
   private static final String NO_SELECTION = "none";

   /** BrowseBean instance */
   private BrowseBean browseBean;
   
   /** NodeService instance */
   private NodeService nodeService;
   
   /** The SearchService instance */
   private SearchService searchService;
   
   private NodeRef template;
   
   
   /**
    * @param nodeService The nodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }

   /**
    * @param browseBean The BrowseBean to set.
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }
   
   /**
    * @param searchService The searchService to set.
    */
   public void setSearchService(SearchService searchService)
   {
      this.searchService = searchService;
   }
   
   /**
    * Returns the document this bean is currently representing
    * 
    * @return The document Node
    */
   public Node getDocument()
   {
      return this.browseBean.getDocument();
   }
   
   /**
    * Returns the id of the current document
    * 
    * @return The id
    */
   public String getId()
   {
      return getDocument().getId();
   }
   
   /**
    * Returns the name of the current document
    * 
    * @return Name of the current document
    */
   public String getName()
   {
      return getDocument().getName();
   }
   
   /**
    * @return the list of available Content Templates that can be applied to the current document.
    */
   public SelectItem[] getTemplates()
   {
      // TODO: could cache this last for say 1 minute before requerying
      
      // get the template from the special Content Templates folder
      FacesContext context = FacesContext.getCurrentInstance();
      String actNs = NamespaceService.APP_MODEL_PREFIX;
      String xpath = actNs + ":" + 
            QName.createValidLocalName(Application.getRootPath(context)) + 
            "/" + actNs + ":" + 
            QName.createValidLocalName(Application.getGlossaryFolderName(context)) +
            "/" + actNs + ":" + 
            QName.createValidLocalName(Application.getContentTemplatesFolderName(context)) +
            "/*";
      
      NodeRef rootNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
      NamespaceService resolver = Repository.getServiceRegistry(context).getNamespaceService();
      List<NodeRef> results = this.searchService.selectNodes(rootNodeRef, xpath, null, resolver, false);
      
      List<SelectItem> templates = new ArrayList(results.size());
      if (results.size() != 0)
      {
         for (NodeRef assocRef : results)
         {
            Node childNode = new Node(assocRef, this.nodeService);
            templates.add(new SelectItem(childNode.getId(), childNode.getName()));
         }
         
         // make sure the list is sorted by the label
         QuickSort sorter = new QuickSort(templates, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
         sorter.sort();
      }
      
      // add an entry (at the start) to instruct the user to select a template
      templates.add(0, new SelectItem(NO_SELECTION, Application.getMessage(FacesContext.getCurrentInstance(), "select_a_template")));
      
      return templates.toArray(new SelectItem[results.size()]);
   }
   
   /**
    * Navigates to next item in the list of content for the current Space
    */
   public void nextItem(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         List<Node> nodes = this.browseBean.getContent();
         if (nodes.size() > 1)
         {
            // perform a linear search - this is slow but stateless
            // otherwise we would have to manage state of last selected node
            // this gets very tricky as this bean is instantiated once and never
            // reset - it does not know when the document has changed etc.
            for (int i=0; i<nodes.size(); i++)
            {
               if (id.equals(nodes.get(i).getId()) == true)
               {
                  // found our item - being search for next that has a preview aspect if any
                  int index = i + 1;
                  if (i == nodes.size() - 1)
                  {
                     // handle wrapping case
                     index = 0;
                  }
                  int nextIndex = findNextPreviewNode(nodes, index);
                  if (nextIndex != -1)
                  {
                     // prepare for showing details for this node
                     this.browseBean.setupContentAction(nodes.get(nextIndex).getId(), false);
                  }
                  break;
               }
            }
         }
      }
   }
   
   private int findNextPreviewNode(List<Node> nodes, int start)
   {
      // search from start to end of list
      for (int i=start; i<nodes.size(); i++)
      {
         Node next = nodes.get(i);
         if (next.hasAspect(ContentModel.ASPECT_TEMPLATABLE))
         {
            return i;
         }
      }
      // search from zero index to start - 1 (to skip original node)
      for (int i=0; i<start - 1; i++)
      {
         Node next = nodes.get(i);
         if (next.hasAspect(ContentModel.ASPECT_TEMPLATABLE))
         {
            return i;
         }
      }
      return -1;
   }
   
   /**
    * Navigates to the previous item in the list of content for the current Space
    */
   public void previousItem(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         List<Node> nodes = this.browseBean.getContent();
         if (nodes.size() > 1)
         {
            // see above
            for (int i=0; i<nodes.size(); i++)
            {
               if (id.equals(nodes.get(i).getId()) == true)
               {
                  // found our item - being search for next that has a preview aspect if any
                  int index = i - 1;
                  if (i == 0)
                  {
                     // handle wrapping case
                     index = nodes.size() - 1;
                  }
                  int prevIndex = findPrevPreviewNode(nodes, index);
                  if (prevIndex != -1)
                  {
                     // prepare for showing details for this node
                     this.browseBean.setupContentAction(nodes.get(prevIndex).getId(), false);
                  }
                  break;
               }
            }
         }
      }
   }
   
   private int findPrevPreviewNode(List<Node> nodes, int start)
   {
      // search from start to beginning of list
      for (int i=start; i>=0; i--)
      {
         Node next = nodes.get(i);
         if (next.hasAspect(ContentModel.ASPECT_TEMPLATABLE))
         {
            return i;
         }
      }
      // end of list to start + 1 (to skip original node)
      for (int i=nodes.size() - 1; i>start; i--)
      {
         Node next = nodes.get(i);
         if (next.hasAspect(ContentModel.ASPECT_TEMPLATABLE))
         {
            return i;
         }
      }
      return -1;
   }
   
   /**
    * Returns a model for use by a template on the Document Details page.
    * 
    * @return model containing current document and current space info.
    */
   public Map getTemplateModel()
   {
      HashMap model = new HashMap(3, 1.0f);
      
      TemplateNode documentNode = new TemplateNode(getDocument().getNodeRef(), this.nodeService);
      model.put("document", documentNode);
      
      return model;
   }

   /**
    * @return the current template as a full NodeRef
    */
   public NodeRef getTemplateRef()
   {
      return this.template;
   }
   
   /**
    * @return Returns the template Id.
    */
   public String getTemplate()
   {
      return (this.template != null ? this.template.getId() : null);
   }

   /**
    * @param template The template Id to set.
    */
   public void setTemplate(String template)
   {
      if (template != null && template.equals(NO_SELECTION) == false)
      {
         this.template = new NodeRef(Repository.getStoreRef(), template);
      }
   }
}
