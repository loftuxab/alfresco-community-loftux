/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.knowledgeBase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.copy.CopyServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Article aspect behaviour
 * 
 * @author Roy Wetherall
 */
public class ArticleAspect implements KbModel, 
                                      CopyServicePolicies.OnCopyNodePolicy,
                                      NodeServicePolicies.OnUpdatePropertiesPolicy,
                                      NodeServicePolicies.OnAddAspectPolicy,
                                      NodeServicePolicies.OnCreateChildAssociationPolicy,
                                      ContentServicePolicies.OnContentUpdatePolicy
{
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private ContentService contentService;
    private ActionService actionService;
    private DictionaryService dictionaryService;
    
    public void setpolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public void init()
    {

        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyNode"),
                ASPECT_ARTICLE,
                new JavaBehaviour(this, "onCopyNode"));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                ASPECT_ARTICLE,
                new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"),
                ASPECT_ARTICLE,
                new JavaBehaviour(this, "onAddAspect", NotificationFrequency.FIRST_EVENT));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onContentUpdate"),
                ASPECT_ARTICLE,
                new JavaBehaviour(this, "onContentUpdate", NotificationFrequency.TRANSACTION_COMMIT)); 
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                ContentModel.TYPE_FOLDER,
                ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onCreateChildAssociation", NotificationFrequency.FIRST_EVENT));
    }
    
    public void onCopyNode(
            QName sourceClassRef, 
            NodeRef sourceNodeRef, 
            StoreRef destinationStoreRef,
            boolean copyToNewNode,            
            PolicyScope copyDetails)
    {
        // Do nothing since we do not want to copy the article aspect
    }

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        if (this.nodeService.exists(nodeRef) == true && 
            this.nodeService.hasAspect(nodeRef, ASPECT_ARTICLE) == true &&
            this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
        {
            String beforeStatus = (String)before.get(PROP_STATUS).toString();
            String afterStatus = (String)after.get(PROP_STATUS).toString();
            if (beforeStatus.equals(afterStatus) == false && STATUS_PUBLISHED.toString().equals(afterStatus) == true)
            {
                updatePublishedArticle(nodeRef);
            }
        }        
    }    

    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (this.nodeService.exists(nodeRef) == true)
        {
            NodeRef kb = getKnowledgeBase(nodeRef);
            
            // Get the article count
            Action counterAction = this.actionService.createAction("counter");
            this.actionService.executeAction(counterAction, kb);
            String id = this.nodeService.getProperty(kb, ContentModel.PROP_COUNTER).toString();
               
            // Set the kb id
            this.nodeService.setProperty(nodeRef, PROP_KB_ID, pad(id, 4));
        }        
    }
    
    private String pad(String s, int len)
    {
       String result = s;
       for (int i=0; i<(len - s.length()); i++)
       {
           result = "0" + result;
       }
       return result;
    }

    public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode)
    {
        NodeRef nodeRef = childAssocRef.getChildRef();   
        if (this.nodeService.exists(nodeRef) == true && 
            this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
        {
            QName className = this.nodeService.getType(nodeRef);
            if (this.dictionaryService.isSubClass(className, ContentModel.TYPE_CONTENT) == true)
            {
                NodeRef kb = getKnowledgeBase(nodeRef);
                
                if (kb != null)
                {   
                    // Link the article to the relevant knowledge base
                    this.nodeService.createAssociation(nodeRef, kb, ASSOC_KNOWLEDGE_BASE);
                    
                    // Apply the article aspect
                    this.nodeService.addAspect(nodeRef, ASPECT_ARTICLE, null);
                }
            }
        }
    }

    public void onContentUpdate(NodeRef nodeRef, boolean newContent)
    {
        if (this.nodeService.exists(nodeRef) == true && 
            this.nodeService.hasAspect(nodeRef, ASPECT_ARTICLE) == true &&
            this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
        {
            String status = this.nodeService.getProperty(nodeRef, PROP_STATUS).toString();
            if (STATUS_PUBLISHED.toString().equals(status) == true)
            {
                updatePublishedArticle(nodeRef);
            }
        }        
    }
    
    private void updatePublishedArticle(NodeRef article)
    {
        // See if a rendition of the article already exists or not
        NodeRef rendition = null;
        List<ChildAssociationRef> children = this.nodeService.getChildAssocs(article, ASSOC_PUBLISHED, RegexQNamePattern.MATCH_ALL);
        if (children.size() == 1)
        {
            rendition = children.get(0).getChildRef();
        }
        
        if (rendition == null)
        {
            // Create the rendition
            String articleName = getRenditionName((String)this.nodeService.getProperty(article, ContentModel.PROP_NAME));
            Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
            props.put(ContentModel.PROP_NAME, articleName);                
            rendition = this.nodeService.createNode(
                                            article, 
                                            ASSOC_PUBLISHED, 
                                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, articleName), 
                                            ContentModel.TYPE_CONTENT, 
                                            props).getChildRef();
        }
        
        // Transform the article
        ContentReader reader = this.contentService.getReader(article, ContentModel.PROP_CONTENT);
        if (reader != null)
        {
            ContentWriter writer = this.contentService.getWriter(rendition, ContentModel.PROP_CONTENT, true);
            writer.setMimetype(MimetypeMap.MIMETYPE_FLASH);
            writer.setEncoding("UTF-8");
            this.contentService.transform(reader, writer);
        }              
    }
    
    private String getRenditionName(String original)
    {
        // get the current extension
        int dotIndex = original.lastIndexOf('.');
        StringBuilder sb = new StringBuilder(original.length());
        if (dotIndex > -1)
        {
            // add the new extension
            sb.append(original.substring(0, dotIndex));            
            sb.append('.').append("swf");
        }
        else
        {
            // no extension so dont add a new one
            sb.append(original);
            sb.append('.').append("swf");
        }

        return sb.toString();
    }   
    
    private NodeRef getKnowledgeBase(NodeRef nodeRef)
    {
       NodeRef result = null;
       
       if (this.nodeService.hasAspect(nodeRef, KbModel.ASPECT_ARTICLE) == true)
       {
           // Get the knowledge base node from the association
           List<AssociationRef> assocs = this.nodeService.getTargetAssocs(nodeRef, ASSOC_KNOWLEDGE_BASE);
           if (assocs.size() == 1)
           {
               result = assocs.get(0).getTargetRef();
           }
       }
       
       if (result == null)
       {
          result = findKnowledgeBase(nodeRef);
       }
       
       return result;
    }

    private NodeRef findKnowledgeBase(NodeRef nodeRef)
    {
       NodeRef result = null;
       
       ChildAssociationRef parentAssocRef = this.nodeService.getPrimaryParent(nodeRef);
       
       if (parentAssocRef != null)
       {
           NodeRef parent = parentAssocRef.getParentRef();
           if (parent != null)
           {           
               if (TYPE_KNOWLEDGE_BASE.equals(this.nodeService.getType(parent)) == true)
               {
                   result = parent;
               }
               else
               {
                   result = findKnowledgeBase(parent);
               }
           }
       }
       
       return result;
    }

}
