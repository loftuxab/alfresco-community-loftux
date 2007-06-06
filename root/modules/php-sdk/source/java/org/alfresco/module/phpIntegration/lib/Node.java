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
import org.alfresco.module.phpIntegration.PHPProcessorException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.GUID;
import org.apache.log4j.Logger;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;

/**
 * Repository node implementaiton class.
 * 
 * @author Roy Wetherall
 */
public class Node implements ScriptObject
{
    /** Logger **/
    private static Logger logger = Logger.getLogger(Node.class);
    
    /** Script object name */
    private static final String SCRIPT_OBJECT_NAME = "Node";
    
    /** New node id delimiter */
    private static final String NEW_NODE_DELIM = "new_";
    
    /** Node service */
    private NodeService nodeService;
    
    /** Session object */
    private Session session;
    
    /** Node id */
    private String id;
    
    /** Node type */
    private String type;
    
    /** Node store */
    private Store store;
    
    /** List of nodes aspects (removed and added)*/
    private List<String> aspects;
    private List<String> addedAspects;
    private List<String> removedAspects;
    
    /** Indicates if the properties have been modified */
    private boolean arePropertiesDirty = false;
    private Map<String, Object> properties;
    
    private List<ChildAssociation> children; 
    private List<ChildAssociation> addedChildren;
    private List<ChildAssociation> removedChildren;
    private List<ChildAssociation> parents;
    private ChildAssociation primaryParent;
    private List<Association> associations;
    private List<Association> addedAssociations;
    private List<Association> removedAssociations;
    
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    public Node(Session session, NodeRef nodeRef)
    {
        // Call the constructor
        this(session, session.getStoreFromString(nodeRef.getStoreRef().toString()), nodeRef.getId());
    }
    
    public Node(Session session, Store store, String id)
    {
        // Call the constructor
        this(session, store, id, null);
    }
    
    public Node(Session session, Store store, String id, String type)
    {
        // Set the attribute details
        this.session = session;
        this.store = store;
        this.id = id;
        if (type != null)
        {
            this.type = type;
        }
        
        // Set the node service
        this.nodeService = session.getServiceRegistry().getNodeService();
        
        // Add the node to the session
        this.session.addNode(this);
    }
    
    /**
     * Get the node ref that this node represents
     * 
     * @return  the node reference
     */
    public NodeRef getNodeRef()
    {
        NodeRef nodeRef = null;
        if (isNewNode() == false)
        {
            nodeRef = new NodeRef(this.store.getStoreRef(), this.id);
        }
        return nodeRef;
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
        return this.store;
    }
    
    /**
     * Gets the id of the node
     * 
     * @return  the id of the node
     */
    public String getId()
    {
        return this.id;
    }
    
    /** 
     * Gets the type of the node
     * 
     * @return  the node type
     */
    public String getType()
    {
        if (this.type == null)
        {
            this.type = this.nodeService.getType(getNodeRef()).toString();
        }
        return this.type;
    }
    
    /**
     * Indicates whether the node is newly created.  True if it is yet to be saved, false otherwise.
     * 
     * @return boolean True if it is a new node, false otherwise.
     */
    public boolean isNewNode()
    {
        return this.id.startsWith(NEW_NODE_DELIM);
    }
    
    /**
     * Get the map of property names and values
     * 
     * @return  a map of property names and values
     */
    public Map<String, Object> getProperties()
    {
        // Make sure the properties are populated
        populateProperties();
        
        // Return the properties
        return new HashMap<String, Object>(this.properties);
    }
    
    /**
     * Sets the property values for the node
     * 
     * @param properties    a map of property names and values
     */
    public void setProperties(Map<String, Object> properties)
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Setting properties on node " + this.getId());
        }
        
        // Set the property values
        this.properties = new HashMap<String, Object>(properties);
        this.arePropertiesDirty = true;
    }        
    
    /**
     * Sets the values of the properties found in the array provided
     * 
     * @param properties a map of property values
     */
    public void setPropertyValues(Map<String, Object> properties)
    {
        // Make sure the properties are populated
        populateProperties();
        
        // Overrite/set the properties with the passed property values
        for (Map.Entry<String, Object> entry : properties.entrySet())
        {
            String fullName = this.session.getNamespaceMap().getFullName(entry.getKey());
            this.properties.put(fullName, entry.getValue());
        }
        this.arePropertiesDirty = true;
    }
    
    /**
     * Callback used to indicate that a content property has been modified directly.
     */
    /*package*/ void contentUpdated()
    {
        this.arePropertiesDirty = true;
    }
    
    /**
     * Gets a list of the node's aspects
     * 
     * @return  List<String>    a list of the node aspects
     */
    public List<String> getAspects()
    {
        // Check that the aspects have been populated
        populateAspects();
        
        return this.aspects;
    }
    
    /**
     * Indicates whether an node has the specified aspect or not.
     * 
     * @param aspect    the aspect type (short names accepted)
     * @return boolean  true if the node has the aspect, false otherwise
     */
    public boolean hasAspect(String aspect)
    {
        // Check that the aspects have been populated
        populateAspects();
        
        // Map aspect name to full name
        aspect = this.session.getNamespaceMap().getFullName(aspect);
        
        // Check to see if the aspect is in the list
        return this.aspects.contains(aspect);
    }
    
    /**
     * Adds an aspect to the node
     * 
     * @param aspect        the aspect
     * @param properties    the propeties of teh aspect
     */
    public void addAspect(String aspect, Map<String, Object> properties)
    {
        // Check that the aspects have been populated
        populateAspects();
        
        // Map aspect name to full name
        aspect = this.session.getNamespaceMap().getFullName(aspect);
        
        // Add the aspect
        if (this.aspects.contains(aspect) == false)
        {
            // Deal with re-added aspects
            if (this.removedAspects.contains(aspect) == true)
            {
                this.removedAspects.remove(aspect);
            }
            else
            {
                this.addedAspects.add(aspect);
            }
            
            this.aspects.add(aspect);                     
        }
        
        // Add the properties
        if (properties != null)
        {
            setPropertyValues(properties);
        }
    }
    
    /**
     * Removes as aspect from the node.
     * 
     * @param aspect    the aspect
     */
    public void removeAspect(String aspect)
    {
        // Check  that the aspects have been populated
        populateAspects();
        
        // Map the aspect name to the correct full name
        aspect = this.session.getNamespaceMap().getFullName(aspect);
        
        // Remove the aspect
        if (this.aspects.contains(aspect) == true)
        {
            if (this.addedAspects.contains(aspect) == true)
            {
                this.addedAspects.remove(aspect);
            }
            else
            {
                this.removedAspects.add(aspect);
            }
            this.aspects.remove(aspect);
        }
    }
  
    /**
     * Get the child associations of this node
     * 
     * @return List<ChildAssociation>   a list of child associations
     */
    public List<ChildAssociation> getChildren()
    {
        // Check the children have been populated
        populateChildren();
        
        return this.children;
    }
    
    /** 
     * Get the parent associations of this node
     * 
     * @return List<ChildAssociation>   a list of parent assocations
     */
    public List<ChildAssociation> getParents()
    {
        // Check that the parents have been populated
        populateParents();
        
        return this.parents;
    }
    
    /**
     * Get the primary parent of this node
     * 
     * @return  the primary parent node
     */
    public Node getPrimaryParent()
    {
        // Check that the parents have been populated
        populateParents();
        
        // Return the primary parent of this node
        return this.primaryParent.getParent();
    }
    
    /**
     * Get the associations eminating from this node
     * 
     * @return List<Association>    a list of associations
     */
    public List<Association> getAssociations()
    {
        // Check that associations have been populated
        populateAssociations();
        
        return this.associations;
    }
    
    /**
     * Sets the content on a content property
     * 
     * @param property  the content property name
     * @param mimetype  the content mimetype
     * @param encoding  the content encoding
     * @param content   the content
     * @return ContentData the contetn data
     */
    public org.alfresco.module.phpIntegration.lib.ContentData setContent(String property, String mimetype, String encoding, String content)
    {
        // Make sure the properties are populated
        populateProperties();
        
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
        
        return contentData;
    }
    
    /**
     * Create a new child node
     * 
     * @param type              the type of the node
     * @param associationType   the association type
     * @param associationName   the association name
     * @return Node             the newly create node     
     */
    public Node createChild(String type, String associationType, String associationName)
    {
        // Convert to full names
        type = this.session.getNamespaceMap().getFullName(type);
        associationType = this.session.getNamespaceMap().getFullName(associationType);
        associationName = this.session.getNamespaceMap().getFullName(associationName);
        
        // Check the children have been populates
        populateChildren();
        
        // Create the new node
        String id = NEW_NODE_DELIM + GUID.generate();
        Node newNode = new Node(this.session, this.getStore(), id, type);
        
        // Create the child association object
        ChildAssociation childAssociation = new ChildAssociation(this, newNode, associationType, associationName, true, 0);
        
        // Set the parent array of the node node        
        newNode.parents = new ArrayList<ChildAssociation>(5);
        newNode.primaryParent = childAssociation;
        newNode.parents.add(childAssociation);
        
        // Add as a child of the parent node
        this.children.add(childAssociation);
        this.addedChildren.add(childAssociation);
        
        return newNode;
    }
    
    /**
     * Add a new child to the node.  Creates a non-primary child association.
     * 
     * @param node              the child node
     * @param associationType   the association type
     * @param associationName   the association name
     */
    public void addChild(Node node, String associationType, String associationName)
    {
        // Convert to full names
        associationType = this.session.getNamespaceMap().getFullName(associationType);
        associationName = this.session.getNamespaceMap().getFullName(associationName);
        
        // Check that the children have been populated 
        populateChildren();
        
        // Check the parents of the child node have been populated
        node.populateParents();
        
        // Create the child association
        ChildAssociation childAssociation = new ChildAssociation(this, node, associationType, associationName, false, 0);
     
        // Add to the parent list of the child node
        node.parents.add(childAssociation);
        
        // Add to the child lists of the parent node
        this.children.add(childAssociation);
        if (this.removedChildren.contains(childAssociation) == true)
        {
            this.removedChildren.remove(childAssociation);
        }
        else
        {        
            this.addedChildren.add(childAssociation);
        }
    }
    
    /**
     * Removes a non-primary child association from the node.
     * 
     * @param childAssociation  the child association to remove.
     */
    public void removeChild(ChildAssociation childAssociation)
    {
        if (childAssociation.getIsPrimary() == false)
        {
            // Check that the children have been populated
            populateChildren();
            
            if (this.children.contains(childAssociation) == true)
            {
                // Check the parents of the child have been populated
                childAssociation.getChild().populateParents();
                
                // Adjust lists accordingly
                this.children.remove(childAssociation);                
                childAssociation.getChild().parents.remove(childAssociation);
                
                if (this.addedChildren.contains(childAssociation) == true)
                {
                    this.addedChildren.remove(childAssociation);
                }
                else
                {
                    this.removedChildren.add(childAssociation);
                }
            }
            else
            {
                if (logger.isDebugEnabled() == true)
                {
                    logger.debug("The child association being delete is not present of the node.");
                }
            }
        }
        else
        {
            throw new PHPProcessorException("Cannot remove a primary child association.");
        }                
    }
    
    /**
     * Adds an association from one node to another.
     * 
     * @param toNode            the destination node
     * @param associationType   the assocation type
     */
    public void addAssociation(Node toNode, String associationType)
    {
        // Convert to full name
        associationType = this.session.getNamespaceMap().getFullName(associationType);
        
        // Populate the associations for this node
        populateAssociations();
        
        // Create the association
        Association association = new Association(this, toNode, associationType);
        
        // Adjust lists accordingly
        if (removedAssociations.contains(association) == true)
        {
            this.removedAssociations.remove(association);
        }
        else
        {
            this.addedAssociations.add(association);
        }
        this.associations.add(association);
    }
    
    /**
     * Remove an association
     * 
     * @param association   the association
     */
    public void removeAssociation(Association association)
    {   
        // Populate the associations for this node
        populateAssociations();
        
        // Adjust lists accordingly
        if (addedAssociations.contains(association) == true)
        {
            this.addedAssociations.remove(association);
        }
        else
        {
            this.removedAssociations.add(association);
        }
        this.associations.remove(association);        
    }
    
    /**
     * Copyies the node and optionally all its children, to another destination
     * 
     * @param destination       the destination node
     * @param associationType   the association type
     * @param associationName   the association name
     * @param copyChildren      indicates whether the children of the node should be copied or not
     * @return Node             the newly created copy of the origional node 
     */
    public Node copy(Node destination, String associationType, String associationName, boolean copyChildren)
    {
        // Get the full names of the assoc type and name
        associationType = this.session.getNamespaceMap().getFullName(associationType);
        associationName = this.session.getNamespaceMap().getFullName(associationName);
        
        // Check that the destination node is not an unsaved node
        if (destination.isDirty() == true)
        {
            throw new PHPProcessorException("Can not copy node (" + toString() + ") since there are outstanding modifications that require saving on the destination node (" + destination.toString() + ")");
        }
        
        // Check whether there are any outstanding changes
        if (isDirty() == true)
        {
            throw new PHPProcessorException("Can not copy node (" + toString() + ") since there are outstanding modifications that require saving");
        }
        
        // Copy the node
        CopyService copyService = this.session.getServiceRegistry().getCopyService();
        NodeRef nodeRef = copyService.copyAndRename(
                getNodeRef(), 
                destination.getNodeRef(),
                QName.createQName(associationType),
                QName.createQName(associationName),
                copyChildren);
        
        // To ensure information is up to date, clean the destination node
        destination.cleanNode();
        
        // Return the newly created node
        return this.session.getNodeFromString(nodeRef.toString());
    }
    
    /**
     * Moves the node from its current primary parent into another.
     * 
     * @param destination       the destination node
     * @param associationType   the assocation type
     * @param assocationName    the association name
     */
    public void move(Node destination, String associationType, String associationName)    
    {
        // Get the full names of the assoc type and name
        associationType = this.session.getNamespaceMap().getFullName(associationType);
        associationName = this.session.getNamespaceMap().getFullName(associationName);
        
        // Check the current primary parent for modifications
        Node currentParent = getPrimaryParent();
        if (currentParent.isDirty() == true)
        {
            throw new PHPProcessorException("Can not move node (" + toString() + ") since there are outstanding modifications that require saving on the current parent node (" + currentParent.toString() + ")");
        }
        
        // Check that the destination node is not an unsaved node
        if (destination.isDirty() == true)
        {
            throw new PHPProcessorException("Can not move node (" + toString() + ") since there are outstanding modifications that require saving on the destination node (" + destination.toString() + ")");
        }
        
        // Check whether there are any outstanding changes
        if (isDirty() == true)
        {
            throw new PHPProcessorException("Can not move node (" + toString() + ") since there are outstanding modifications that require saving");
        }
        
        // Do the move
        this.nodeService.moveNode(
                getNodeRef(),
                destination.getNodeRef(),
                QName.createQName(associationType),
                QName.createQName(associationName));    
        
        // Clean all 3 nodes involved in the mode to ensure no data is out of date
        currentParent.cleanNode();
        destination.cleanNode();
        cleanNode();
    }
    
    /**
     * Dynamic implementation of get properties
     * 
     * @param name      the name of the property
     * @return Value    the value of the property
     */
    public Value __getField(Env env, Value name)
    {
        Value result = null;
        
        String fullName = this.session.getNamespaceMap().getFullName(name.toString());
        if (fullName.equals(name) == false)
        {
            // Make sure the properties are populated
            populateProperties();
            
            Object value = this.properties.get(fullName);
            if (value != null)
            {
                result = PHPProcessor.convertToValue(env, this.session, value);
            }
            else
            {
                result = NullValue.NULL;
            }
        }

        return result;        
    }

    /**
     * Dynamic implemenatation of set properties
     * 
     * @param name    the name of the property
     * @param value   the value of the property
     */
    public void __setField(String name, String value)
    {
        String fullName = this.session.getNamespaceMap().getFullName(name.toString());
        if (fullName.equals(name) == false)
        {
            // Make sure the properties are populated
            populateProperties();
            
            if (logger.isDebugEnabled() == true)
            {
                logger.debug("Setting field on node " + this.getId() + " (name="+ fullName + "; value:" + value.toString() + ")");
            }
            
            // Set the property value
            this.properties.put(fullName, value);
            this.arePropertiesDirty = true;
        }
    }
    
    /**
     * PHP toString implementation
     * 
     * @return  the node string representation
     */
    public String __toString()
    {
        return this.toString();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.store.getScheme() + "://" + this.store.getAddress() + "/" + this.id;
    }
    
    /*package*/ void prepareSave()
    {
        // Handle the creation of a new node
        if (isNewNode() == true)
        {
            // Get the primary parent
            if (this.primaryParent == null)
            {
                throw new PHPProcessorException("Unable to save new node since no valid primary parent has been found.");
            }
            
            // Create the new node
            ChildAssociationRef childAssocRef = this.nodeService.createNode(
                    this.primaryParent.getParent().getNodeRef(),
                    QName.createQName(this.primaryParent.getType()),
                    QName.createQName(this.primaryParent.getName()), 
                    QName.createQName(this.type));
            
            // Set the id of the new node
            this.id = childAssocRef.getChildRef().getId();
        }
    }
    
    /**
     * Called when the node is saved.  Inspects the node and persists any changes as appropriate.
     */
    /*package*/ void onSave()
    {
        // Get the node reference
        NodeRef nodeRef = getNodeRef();        
        if (this.arePropertiesDirty == true)
        {
            // Log details
            if (logger.isDebugEnabled() == true)
            {
                logger.debug("Saving property updates made to node " + this.getId());
            }
            
            // List of pending content properties to process
            List<org.alfresco.module.phpIntegration.lib.ContentData> pendingContentProperties = new ArrayList<org.alfresco.module.phpIntegration.lib.ContentData>(1);
            
            // Update the properties
            Map<QName, Serializable> currentProperties = this.nodeService.getProperties(nodeRef);
            for (Map.Entry<String, Object> entry : this.properties.entrySet())
            {
                if (entry.getValue() instanceof org.alfresco.module.phpIntegration.lib.ContentData)
                {
                    // Save the content property
                    org.alfresco.module.phpIntegration.lib.ContentData contentData = (org.alfresco.module.phpIntegration.lib.ContentData)entry.getValue();
                    pendingContentProperties.add(contentData);
                }
                else
                {
                    Serializable propValue = null; 
                        
                    // Get the property definition so we can do the correct conversion
                    QName propertyName = QName.createQName(entry.getKey());
                    DictionaryService dictionaryService = this.session.getServiceRegistry().getDictionaryService();                    
                    PropertyDefinition propDefintion = dictionaryService.getProperty(propertyName);
                    if (propDefintion == null)
                    {
                        // TODO summert here!
                        propValue = (Serializable)entry.getValue();
                    }
                    else
                    {
                        propValue = (Serializable)DefaultTypeConverter.INSTANCE.convert(propDefintion.getDataType(), entry.getValue());
                    }
                    
                    // Set the property value in the temp map
                    if (propValue == null || propValue.equals(currentProperties.get(propertyName)) == false)
                    {
                        currentProperties.put(propertyName, propValue);
                    }
                }
            }
            
            // Set the values of the updated properties
            this.nodeService.setProperties(nodeRef, currentProperties);
            
            // Sort out any pending content properties
            for (org.alfresco.module.phpIntegration.lib.ContentData contentData : pendingContentProperties)
            {
                contentData.onSave();
            }
        }
        
        // Update the aspects
        if (this.addedAspects != null && this.addedAspects.size() != 0)
        {
            for (String aspect : this.addedAspects)
            {
                this.nodeService.addAspect(nodeRef, QName.createQName(aspect), null);                
            }
        }
        if (this.removedAspects != null && this.removedAspects.size() != 0)
        {
            for (String aspect : this.removedAspects)
            {
                this.nodeService.removeAspect(nodeRef, QName.createQName(aspect));
            }
        }
        
        // Update the child associations
        if (this.addedChildren != null && this.addedChildren.size() != 0)
        {
            for (ChildAssociation addedChildAssociation : this.addedChildren)
            {
                if (addedChildAssociation.getIsPrimary() == false)
                {
                    this.nodeService.addChild(
                            nodeRef, 
                            addedChildAssociation.getChild().getNodeRef(),
                            QName.createQName(addedChildAssociation.getType()),
                            QName.createQName(addedChildAssociation.getName()));
                }
            }
        }
        if (this.removedChildren != null && this.removedChildren.size() != 0)
        {
            for (ChildAssociation removedChildAssociation : this.removedChildren)
            {
                this.nodeService.removeChild(nodeRef, removedChildAssociation.getChild().getNodeRef());
            }
        }
        
        // Update the associations
        if (this.addedAssociations != null && this.addedAssociations.size() != 0)
        {
            for (Association addedAssociation : this.addedAssociations)
            {
                this.nodeService.createAssociation(
                        nodeRef, 
                        addedAssociation.getTo().getNodeRef(), 
                        QName.createQName(addedAssociation.getType()));
            }
        }
        if (this.removedAssociations != null && this.removedAssociations.size() != 0)
        {
            for (Association removedAssociation : this.removedAssociations)
            {
                this.nodeService.removeAssociation(
                        nodeRef, 
                        removedAssociation.getTo().getNodeRef(),
                        QName.createQName(removedAssociation.getType()));
            }
        }
        
        // Refresh the state of the node
        cleanNode();      
    }
    
    /**
     * Cleans the nodes cached data and restores it to its initial state
     */
    private void cleanNode()
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Cleaning node " + getId());            
        }
        
        this.properties = null;
        this.arePropertiesDirty = false;
        this.aspects = null;
        this.addedAspects = null;
        this.removedAspects = null;
        this.children = null;
        this.addedChildren = null;
        this.removedChildren = null;
        this.parents = null;
        this.primaryParent = null;
        this.associations = null;
        this.addedAssociations = null;
        this.removedAssociations = null;
    }
    
    private boolean isDirty()
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Calling isDrity() for " + getId() + " (isNewNode = " + isNewNode() + "; arePropertiesDirty = " + this.arePropertiesDirty + ")");
        }
        
        if (isNewNode() == false &&
            this.arePropertiesDirty == false &&
            (this.addedAspects == null || this.addedAspects.size() == 0 ) &&
            (this.removedAspects == null || this.removedAspects.size() == 0 ) &&
            (this.addedChildren == null || this.addedChildren.size() == 0 ) &&
            (this.removedChildren == null || this.removedChildren.size() == 0 ) &&
            (this.addedAssociations == null || this.addedAssociations.size() == 0 ) &&
            (this.removedAssociations == null || this.removedAssociations.size() == 0))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * Populates the properties of the node
     */
    private void populateProperties()
    {
        if (this.properties == null)
        {
            if (logger.isDebugEnabled() == true)
            {
                logger.debug("Populating properties for node " + this.getId());                
            }
            
            if (isNewNode() == false)
            {
                Map<QName, Serializable> properties = this.nodeService.getProperties(getNodeRef());
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
            }
            else
            {
                this.properties = new HashMap<String, Object>(10);
            }
            this.arePropertiesDirty = false;
        }
    }
    
    /**
     * Get the list of aspects for this node.
     * 
     * @return  List<String>    list containing aspects
     */
    private void populateAspects()
    {
        if (this.aspects == null)
        {
            if (isNewNode() == false)
            {
                // Populate the aspect list from the node service
                Set<QName> aspects = this.nodeService.getAspects(getNodeRef());
                this.aspects = new ArrayList<String>(aspects.size());
                for (QName aspect : aspects)
                {
                    this.aspects.add(aspect.toString());
                }
            }
            else
            {
                this.aspects = new ArrayList<String>(5);
            }
            
            // Create the list's used to monitor added and deleted aspects
            this.addedAspects = new ArrayList<String>();
            this.removedAspects = new ArrayList<String>();
        }
    }
    
    /**
     * Populates the child information for this node
     */
    private void populateChildren()
    {
        if (this.children == null)
        {
            if (isNewNode() == false)
            {                
                List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(getNodeRef());
                this.children = new ArrayList<ChildAssociation>(assocs.size());
                for (ChildAssociationRef assoc : assocs)
                {
                    this.children.add(
                            new ChildAssociation(
                                    this.session.getNodeFromString(assoc.getParentRef().toString()),
                                    this.session.getNodeFromString(assoc.getChildRef().toString()),
                                    assoc.getTypeQName().toString(),
                                    assoc.getQName().toString(),
                                    assoc.isPrimary(),
                                    assoc.getNthSibling()));
                }
            }
            else
            {
                this.children = new ArrayList<ChildAssociation>(10);
            }
            
            // Create the added and removed lists
            this.addedChildren = new ArrayList<ChildAssociation>(5);
            this.removedChildren = new ArrayList<ChildAssociation>(5);
        }     
    }
    
    /**
     * Populates the parent information for this node
     */
    private void populateParents()
    {
        if (this.parents == null)
        {
            if (isNewNode() == false)
            {
                List<ChildAssociationRef> parents = this.nodeService.getParentAssocs(getNodeRef());
                this.parents = new ArrayList<ChildAssociation>(parents.size());
                for (ChildAssociationRef assoc : parents)
                {
                    ChildAssociation childAssociation = new ChildAssociation(
                            this.session.getNodeFromString(assoc.getParentRef().toString()),
                            this.session.getNodeFromString(assoc.getChildRef().toString()),
                            assoc.getTypeQName().toString(),
                            assoc.getQName().toString(),
                            assoc.isPrimary(),
                            assoc.getNthSibling());
                    this.parents.add(childAssociation);
                    
                    // Set the primary parent when we come across it
                    if (assoc.isPrimary() == true)
                    {
                        this.primaryParent = childAssociation;
                    }
                }
            }
            else
            {
                this.parents = new ArrayList<ChildAssociation>(5);
            }
        }
    }
    
    /**
     * Populates the association information for this node
     */
    private void populateAssociations()
    {
        if (this.associations == null)
        {
            if (isNewNode() == false)
            {
                List<AssociationRef> associations = this.nodeService.getTargetAssocs(getNodeRef(), RegexQNamePattern.MATCH_ALL);
                this.associations = new ArrayList<Association>(associations.size());
                for (AssociationRef association : associations)
                {
                    this.associations.add(
                            new Association(
                                    this.session.getNodeFromString(association.getSourceRef().toString()),
                                    this.session.getNodeFromString(association.getTargetRef().toString()),
                                    association.getTypeQName().toString()));
                }
            }
            else
            {
                this.associations = new ArrayList<Association>(5);
            }
            
            // Create the added and removes association lists
            this.addedAssociations = new ArrayList<Association>(5);
            this.removedAssociations = new ArrayList<Association>(5);
        }
    }
    
    @SuppressWarnings("unused")
    private void dumpProperties(String message)
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Current property values (" + message + ") ...");
            for (Map.Entry<String, Object> entry : this.properties.entrySet())
            {
                logger.debug("   - " + entry.getKey() + ":" + entry.getValue());
            }
        }
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Node))
        {
            return false;
        }
        Node other = (Node) o;

        return (EqualsHelper.nullSafeEquals(this.id, other.id)
                && EqualsHelper.nullSafeEquals(this.store, other.store));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return id.hashCode();
    }
    
}
