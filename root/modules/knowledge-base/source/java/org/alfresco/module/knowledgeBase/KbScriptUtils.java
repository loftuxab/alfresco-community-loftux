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
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.mozilla.javascript.Scriptable;

/**
 * Knowledge base script util methods
 * 
 * @author Roy Wetherall
 */
public class KbScriptUtils extends BaseProcessorExtension implements Scopeable, KbModel
{
    private ServiceRegistry serviceRegistry;
    
    @SuppressWarnings("unused")
    private Scriptable scope;
    
    /** Value converter */
    private ValueConverter valueConverter = new ValueConverter();
    
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    public void setScope(Scriptable scope)
    {
        this.scope = scope;
    }    
    
    public void updatePublishedArticle(ScriptNode node)
    {
        NodeRef article = (NodeRef)this.valueConverter.convertValueForRepo(node);
        if (this.serviceRegistry.getNodeService().hasAspect(article, KbModel.ASPECT_ARTICLE) == true)
        {
            // See if a rendition of the article already exists or not
            NodeRef rendition = null;
            List<ChildAssociationRef> children = this.serviceRegistry.getNodeService().getChildAssocs(article, ASSOC_PUBLISHED, RegexQNamePattern.MATCH_ALL);
            if (children.size() == 1)
            {
                rendition = children.get(0).getChildRef();
            }
            
            if (rendition == null)
            {
                // Create the rendition
                String articleName = getRenditionName((String)this.serviceRegistry.getNodeService().getProperty(article, ContentModel.PROP_NAME));
                Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
                props.put(ContentModel.PROP_NAME, articleName);                
                rendition = this.serviceRegistry.getNodeService().createNode(
                                                        article, 
                                                        ASSOC_PUBLISHED, 
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, articleName), 
                                                        ContentModel.TYPE_CONTENT, 
                                                        props).getChildRef();
            }
            
            // Transform the article
            ContentReader reader = this.serviceRegistry.getContentService().getReader(article, ContentModel.PROP_CONTENT);
            if (reader != null)
            {
                ContentWriter writer = this.serviceRegistry.getContentService().getWriter(rendition, ContentModel.PROP_CONTENT, true);
                writer.setMimetype(MimetypeMap.MIMETYPE_FLASH);
                writer.setEncoding("UTF-8");
                this.serviceRegistry.getContentService().transform(reader, writer);
            }
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
    
    public ScriptNode getKnowledgeBase(ScriptNode node)
    {
       ScriptNode result = null;
       
       NodeRef nodeRef = (NodeRef)this.valueConverter.convertValueForRepo(node);
       if (this.serviceRegistry.getNodeService().hasAspect(nodeRef, KbModel.ASPECT_ARTICLE) == true)
       {
           // Get the knowledge base node from the association
           List<AssociationRef> assocs = this.serviceRegistry.getNodeService().getTargetAssocs(nodeRef, ASSOC_KNOWLEDGE_BASE);
           if (assocs.size() == 1)
           {
               NodeRef kb = assocs.get(0).getTargetRef();
               result = (ScriptNode)this.valueConverter.convertValueForScript(this.serviceRegistry, this.scope, null, kb);
           }
       }
       
       if (result == null)
       {
          result = findKnowledgeBase(node);
       }
       
       return result;
    }

    public ScriptNode findKnowledgeBase(ScriptNode node)
    {
       ScriptNode result = null;
       
       NodeRef nodeRef = (NodeRef)this.valueConverter.convertValueForRepo(node);
       ChildAssociationRef parentAssocRef = this.serviceRegistry.getNodeService().getPrimaryParent(nodeRef);
       
       if (parentAssocRef != null)
       {
           NodeRef parent = parentAssocRef.getParentRef();
           if (parent != null)
           {           
               if (TYPE_KNOWLEDGE_BASE.equals(this.serviceRegistry.getNodeService().getType(parent)) == true)
               {
                   result = (ScriptNode)this.valueConverter.convertValueForScript(this.serviceRegistry, this.scope, null, parent);   
               }
               else
               {
                   result = findKnowledgeBase((ScriptNode)this.valueConverter.convertValueForScript(this.serviceRegistry, this.scope, null, parent));
               }
           }
       }
       
       return result;
    }

}
