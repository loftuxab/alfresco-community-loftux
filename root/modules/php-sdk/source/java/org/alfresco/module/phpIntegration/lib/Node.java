/*
 * Copyright (C) 2005 Alfresco, Inc.
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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.phpIntegration.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.module.phpIntegration.PHPProcessor;
import org.alfresco.repo.domain.ChildAssoc;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.namespace.RegexQNamePattern;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.sun.java_cup.internal.assoc;

/**
 * @author Roy Wetherall
 */
public class Node implements ScriptObject
{
    private static final String SCRIPT_OBJECT_NAME = "Node";
    
    private NodeService nodeService;
    private Session session;
    private NodeRef nodeRef;
    
    private List<String> aspects;
    
    private boolean arePropertiesDirty = false;
    private Map<String, Object> properties;
    
    private Map<String, ChildAssociation> children;   
    private Map<String, ChildAssociation> parents;
    private Map<String, Association> associations;
    
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    public Node(Session session, NodeRef nodeRef)
    {
        this.session = session;
        this.nodeRef = nodeRef;
        this.nodeService = session.getServiceRegistry().getNodeService();
    }
    
    public Node(Session session, Store store, String id)
    {
        this.session = session;
        this.nodeRef = new NodeRef(store.getStoreRef(), id);
    }
    
    /**
     * Get the node ref that this node represents
     * 
     * @return  the node reference
     */
    public NodeRef getNodeRef()
    {
        return this.nodeRef;
    }
    
    /**
     * Get the nodes session
     * 
     * @return  the session
     */
    public Session getSession()
    {
        return this.session;
    }
    
    /**
     * Get the nodes store
     * 
     * @return  the Store
     */
    public Store getStore()
    {
        return new Store(this.session, this.nodeRef.getStoreRef());
    }
    
    /**
     * Gets the id of the node
     * 
     * @return  the id of the node
     */
    public String getId()
    {
        return this.nodeRef.getId();
    }
    
    /** 
     * Gets the type of the node
     * 
     * @return  the node type
     */
    public String getType()
    {
        return this.nodeService.getType(this.nodeRef).toString();
    }
    
    /**
     * Get the map of property names and values
     * 
     * @return  a map of property names and values
     */
    public Map<String, Object> getProperties()
    {
        return getPropertiesImpl();
    }
    
    /**
     * Sets the property values for the node
     * 
     * @param properties    a map of property names and values
     */
    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
        this.arePropertiesDirty = true;
    }        
    
    /**
     * Gets a list of the node's aspects
     * 
     * @return  List<String>    a list of the node aspects
     */
    public List<String> getAspects()
    {
        return getAspectsImpl();
    }
    
    /**
     * Indicates whether an node has the specified aspect or not.
     * 
     * @param aspect    the aspect type (short names accepted)
     * @return boolean  true if the node has the aspect, false otherwise
     */
    public boolean hasAspect(String aspect)
    {
        // Map aspect name to full name
        aspect = this.session.getNamespaceMap().getFullName(aspect);
        
        // Check to see if the aspect is in the list
        List<String> aspects = getAspectsImpl();
        return aspects.contains(aspect);
    }
  
    public Map<String, ChildAssociation> getChildren()
    {
        return getChildrenImpl();
    }
    
    public Map<String, ChildAssociation> getParents()
    {
        return getParentsImpl();
    }
    
    public Map<String, Association> getAssociations()
    {
        return getAssociationsImpl();
    }
    
    public void setContent(String property, String mimetype, String encoding, String content)
    {
        // Convert to full name
        property = this.session.getNamespaceMap().getFullName(property);
        
        // Create the content data object
        org.alfresco.module.phpIntegration.lib.ContentData contentData = new org.alfresco.module.phpIntegration.lib.ContentData(this, property, mimetype, encoding);
        if (content != null)
        {
            contentData.setContent(content);
        }
        
        // Assign to property
        this.properties.put(property, contentData);
    }
    
    /**
     * Dynamic implementation of the properties
     * 
     * @param name
     * @return
     */
    public Value __getField(Env env, Value name)
    {
        Value result = null;
        
        String fullName = this.session.getNamespaceMap().getFullName(name.toString());
        if (fullName.equals(name) == false)
        {
            Object value = getPropertiesImpl().get(fullName);
            if (value != null)
            {
                result = PHPProcessor.convertToValue(env, this.session, value);
            }
        }

        return result;        
    }

    public void __setField(String name, String value)
    {
        System.out.println("__set: " + name.toString());
    }
    
    public String __toString()
    {
        return this.nodeRef.toString();
    }
    
    private Map<String, Object> getPropertiesImpl()
    {
        if (this.properties == null)
        {
            Map<QName, Serializable> properties = this.nodeService.getProperties(this.nodeRef);
            this.properties = new HashMap<String, Object>(properties.size());
            for (Map.Entry<QName, Serializable> entry : properties.entrySet())
            {
                if (entry.getValue() instanceof ContentData)
                {
                    ContentData value = (ContentData)entry.getValue();
                    org.alfresco.module.phpIntegration.lib.ContentData contentData = new org.alfresco.module.phpIntegration.lib.ContentData(
                                                                                        this,
                                                                                        entry.getKey().toString(),
                                                                                        value.getMimetype(),
                                                                                        value.getEncoding(),
                                                                                        value.getSize());
                    this.properties.put(entry.getKey().toString(), contentData);
                }
                else
                {
                    String value = DefaultTypeConverter.INSTANCE.convert(String.class, entry.getValue());
                    this.properties.put(entry.getKey().toString(), value);
                }
            }
            this.arePropertiesDirty = false;
        }
        return this.properties;
    }
    
    /**
     * Get the list of aspects for this node.
     * 
     * @return  List<String>    list containing aspects
     */
    private List<String> getAspectsImpl()
    {
        if (this.aspects == null)
        {
            Set<QName> aspects = this.nodeService.getAspects(this.nodeRef);
            this.aspects = new ArrayList<String>(aspects.size());
            for (QName aspect : aspects)
            {
                this.aspects.add(aspect.toString());
            }
        }
        return this.aspects;
    }
    
    private Map<String, ChildAssociation> getChildrenImpl()
    {
        if (this.children == null)
        {
            List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(this.nodeRef);
            this.children = new HashMap<String, ChildAssociation>(assocs.size());
            for (ChildAssociationRef assoc : assocs)
            {
                this.children.put(
                        assoc.getChildRef().toString(),
                        new ChildAssociation(
                                this.session.getNode(assoc.getParentRef().toString()),
                                this.session.getNode(assoc.getChildRef().toString()),
                                assoc.getTypeQName().toString(),
                                assoc.getQName().toString(),
                                assoc.isPrimary(),
                                assoc.getNthSibling()));
            }
        }
        
        return this.children;        
    }
    
    private Map<String, ChildAssociation> getParentsImpl()
    {
        if (this.parents == null)
        {
            List<ChildAssociationRef> parents = this.nodeService.getParentAssocs(this.nodeRef);
            this.parents = new HashMap<String, ChildAssociation>(parents.size());
            for (ChildAssociationRef assoc : parents)
            {
                this.parents.put(
                        assoc.getParentRef().toString(),
                        new ChildAssociation(
                                this.session.getNode(assoc.getParentRef().toString()),
                                this.session.getNode(assoc.getChildRef().toString()),
                                assoc.getTypeQName().toString(),
                                assoc.getQName().toString(),
                                assoc.isPrimary(),
                                assoc.getNthSibling()));
            }
        }
        return this.parents;
    }
    
    private Map<String, Association> getAssociationsImpl()
    {
        if (this.associations == null)
        {
            List<AssociationRef> associations = this.nodeService.getTargetAssocs(this.nodeRef, RegexQNamePattern.MATCH_ALL);
            this.associations = new HashMap<String, Association>(associations.size());
            for (AssociationRef association : associations)
            {
                this.associations.put(
                        association.getTargetRef().toString(),
                        new Association(
                                this.session.getNode(association.getSourceRef().toString()),
                                this.session.getNode(association.getTargetRef().toString()),
                                association.getTypeQName().toString()));
            }
        }
        return this.associations;
    }
    
}
