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
package org.alfresco.web.ui.repo.component.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.web.app.servlet.DownloadContentServlet;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.QNameMap;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.NewSpaceWizard;
import org.alfresco.web.ui.repo.WebResources;

/**
 * Node class specific for use by Template pages.
 * <p>
 * The class exposes Node properties, children as dynamically populated maps and lists.
 * <p>
 * Various helper methods are provided to access common and useful node variables such
 * as the content url and type information. 
 * 
 * @author Kevin Roast
 */
public class TemplateNode extends Node
{
   private final static String NAMESPACE_BEGIN = "" + QName.NAMESPACE_BEGIN;
   
   /** The children of this node */
   private List<TemplateNode> children = null;
   
   /** The associations from this node */
   private Map<String, List<TemplateNode>> assocs = null;
   
   /** Cached values */
   private ServiceRegistry services = null;
   private Boolean isDocument = null;
   private Boolean isContainer = null;
   private String displayPath = null;
   private TemplateNode parent = null;
   
   
   /**
    * Constructor
    * 
    * @param nodeRef The NodeRef this Node wrapper represents
    * @param nodeService The node service to use to retrieve data for this node 
    */
   public TemplateNode(NodeRef nodeRef, NodeService nodeService)
   {
      super(nodeRef, nodeService);
   }
   
   /**
    * @return The children of this Node as TemplateNode wrappers
    */
   public List<TemplateNode> getChildren()
   {
      if (this.children == null)
      {
         List<ChildAssociationRef> childRefs = this.nodeService.getChildAssocs(this.nodeRef);
         this.children = new ArrayList<TemplateNode>(childRefs.size());
         for (ChildAssociationRef ref : childRefs)
         {
            // create our Node representation from the NodeRef
            TemplateNode child = new TemplateNode(ref.getChildRef(), this.nodeService);
            this.children.add(child);
         }
      }
      
      return this.children;
   }
   
   /**
    * @return The associations for this Node. As a Map of assoc name to a List of TemplateNodes. 
    */
   public Map<String, List<TemplateNode>> getAssocs()
   {
      if (this.assocs == null)
      {
         List<AssociationRef> refs = this.nodeService.getTargetAssocs(this.nodeRef, RegexQNamePattern.MATCH_ALL);
         this.assocs = new QNameMap<String, List<TemplateNode>>(this);
         for (AssociationRef ref : refs)
         {
            String qname = ref.getTypeQName().toString();
            List<TemplateNode> nodes = assocs.get(qname);
            if (nodes == null)
            {
               // first access for the list for this qname
               nodes = new ArrayList<TemplateNode>(4);
               this.assocs.put(ref.getTypeQName().toString(), nodes);
            }
            nodes.add( new TemplateNode(ref.getTargetRef(), this.nodeService) );
         }
      }
      
      return this.assocs;
   }
   
   /**
    * @return All the properties known about this node.
    */
   public Map<String, Object> getProperties()
   {
      if (this.propsRetrieved == false)
      {
         Map<QName, Serializable> props = this.nodeService.getProperties(this.nodeRef);
         
         for (QName qname: props.keySet())
         {
            Serializable propValue = props.get(qname);
            if (propValue instanceof NodeRef)
            {
               // NodeRef object properties are converted to new TemplateNode objects
               // so they can be used as objects within a template
               propValue = new TemplateNode(((NodeRef)propValue), this.nodeService);
            }
            this.properties.put(qname.toString(), propValue);
         }
         
         this.propsRetrieved = true;
      }
      
      return this.properties;
   }
   
   /**
    * @return true if this Node is a container (i.e. a folder)
    */
   public boolean getIsContainer()
   {
      if (isContainer == null)
      {
         DictionaryService dd = getServiceRegistry().getDictionaryService();
         isContainer = Boolean.valueOf( (dd.isSubClass(getType(), ContentModel.TYPE_FOLDER) == true && 
                                       dd.isSubClass(getType(), ContentModel.TYPE_SYSTEM_FOLDER) == false) );
      }
      
      return isContainer.booleanValue();
   }
   
   /**
    * @return true if this Node is a Document (i.e. with content)
    */
   public boolean getIsDocument()
   {
      if (isDocument == null)
      {
         DictionaryService dd = getServiceRegistry().getDictionaryService();
         isDocument = Boolean.valueOf(dd.isSubClass(getType(), ContentModel.TYPE_CONTENT));
      }
      
      return isDocument.booleanValue();
   }
   
   /**
    * @param aspect The aspect name to test for
    * 
    * @return true if the node has the aspect false otherwise
    */
   public boolean hasAspect(String aspect)
   {
      if (aspect.startsWith(NAMESPACE_BEGIN))
      {
         return super.hasAspect(QName.createQName(aspect));
      }
      else
      {
         boolean found = false;
         for (QName qname : getAspects())
         {
            if (qname.toPrefixString(getServiceRegistry().getNamespaceService()).equals(aspect))
            {
               found = true;
               break;
            }
         }
         return found;
      }
   }
   
   /**
    * @return the content String for this node
    */
   public String getContent()
   {
      ContentService contentService = getServiceRegistry().getContentService();
      ContentReader reader = contentService.getReader(this.nodeRef, ContentModel.PROP_CONTENT);
      return reader != null ? reader.getContentString() : "";
   }
   
   /**
    * @return url to the content stream for this node
    */
   public String getUrl()
   {
      return DownloadContentServlet.generateBrowserURL(this.nodeRef, getName());
   }
   
   /**
    * @return Display path to this node
    */
   public String getDisplayPath()
   {
      if (displayPath == null)
      {
         displayPath = Repository.getDisplayPath(this.nodeService.getPath(this.nodeRef));
      }
      
      return displayPath;
   }
   
   /**
    * @return the small icon image for this node
    */
   public String getIcon16()
   {
      if (getIsDocument())
      {
         return Repository.getFileTypeImage(this, true);
      }
      else
      {
         return WebResources.IMAGE_SPACE;
      }
   }
   
   /**
    * @return the large icon image for this node
    */
   public String getIcon32()
   {
      if (getIsDocument())
      {
         return Repository.getFileTypeImage(this, false);
      }
      else
      {
         String icon = (String)getProperties().get("app:icon");
         if (icon != null)
         {
            return "/images/icons/" + icon + ".gif";
         }
         else
         {
            return "/images/icons/" + NewSpaceWizard.SPACE_ICON_DEFAULT + ".gif";
         }
      }
   }
   
   /**
    * @return true if the node is currently locked
    */
   public boolean getIsLocked()
   {
      boolean locked = false;
      
      if (hasAspect(ContentModel.ASPECT_LOCKABLE))
      {
         LockStatus lockStatus = getServiceRegistry().getLockService().getLockStatus(this.nodeRef);
         if (lockStatus == LockStatus.LOCKED || lockStatus == LockStatus.LOCK_OWNER)
         {
            locked = true;
         }
      }
      
      return locked;
   }
   
   /**
    * @return the parent node
    */
   public TemplateNode getParent()
   {
      if (parent == null)
      {
         NodeRef parentRef = getServiceRegistry().getNodeService().getPrimaryParent(nodeRef).getParentRef();
         // handle root node (no parent!)
         if (parentRef != null)
         {
            parent = new TemplateNode(parentRef, getServiceRegistry().getNodeService());
         }
      }
      
      return parent;
   }
   
   
   /**
    * @return The Service Registry
    */
   private ServiceRegistry getServiceRegistry()
   {
      if (services == null)
      {
         services = Repository.getServiceRegistry(FacesContext.getCurrentInstance());
      }
      return services;
   }
}
