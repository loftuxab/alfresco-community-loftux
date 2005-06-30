/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.node.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.domain.ChildAssoc;
import org.alfresco.repo.domain.Node;
import org.alfresco.repo.domain.NodeAssoc;
import org.alfresco.repo.domain.NodeKey;
import org.alfresco.repo.domain.Store;
import org.alfresco.repo.node.AbstractNodeServiceImpl;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.InvalidAspectException;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.AssociationExistsException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.CyclicChildRelationshipException;
import org.alfresco.service.cmr.repository.EntityRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.InvalidStoreRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.PropertyException;
import org.alfresco.service.cmr.repository.StoreExistsException;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.springframework.util.Assert;

/**
 * Node service using database persistence layer to fulfill functionality
 * 
 * @author Derek Hulley
 */
public class DbNodeServiceImpl extends AbstractNodeServiceImpl
{
    private final DictionaryService dictionaryService;
    private final NodeDaoService nodeDaoService;
    
    public DbNodeServiceImpl(
			PolicyComponent policyComponent,
            DictionaryService dictionaryService,
            NodeDaoService nodeDaoService)
    {
		super(policyComponent, dictionaryService);
		
        this.dictionaryService = dictionaryService;
        this.nodeDaoService = nodeDaoService;
    }

    /**
     * @param storeRef store to search for
     * @return Returns a non-null <code>Store</code> instance
     * @throws InvalidStoreRefException if the reference is to a store that doesn't exist
     */
    private Store getStoreNotNull(StoreRef storeRef) throws InvalidStoreRefException
    {
        Store store = nodeDaoService.getStore(storeRef.getProtocol(), storeRef.getIdentifier());
        if (store == null)
        {
            throw new InvalidStoreRefException(storeRef);
        }
        return store;
    }

    /**
     * Performs a null-safe get of the node
     * 
     * @param nodeRef the node to retrieve
     * @return Returns the node entity (never null)
     * @throws InvalidNodeRefException if the referenced node could not be found
     */
    private Node getNodeNotNull(NodeRef nodeRef) throws InvalidNodeRefException
    {
        String protocol = nodeRef.getStoreRef().getProtocol();
        String identifier = nodeRef.getStoreRef().getIdentifier();
        Node unchecked = nodeDaoService.getNode(protocol, identifier, nodeRef.getId());
        if (unchecked == null)
        {
            throw new InvalidNodeRefException("Node does not exist: " + nodeRef, nodeRef);
        }
        return unchecked;
    }

    public boolean exists(StoreRef storeRef)
    {
        Store store = nodeDaoService.getStore(storeRef.getProtocol(), storeRef.getIdentifier());
        boolean exists = (store != null);
        // done
        return exists;
    }
    
    public boolean exists(NodeRef nodeRef)
    {
        StoreRef storeRef = nodeRef.getStoreRef();
        Node node = nodeDaoService.getNode(storeRef.getProtocol(),
                storeRef.getIdentifier(),
                nodeRef.getId());
        boolean exists = (node != null);
        // done
        return exists;
    }
    
    /**
     * Defers to the typed service
     * @see StoreDaoService#createWorkspace(String)
     */
    public StoreRef createStore(String protocol, String identifier)
    {
        StoreRef storeRef = new StoreRef(protocol, identifier);
        // check that the store does not already exist
        Store store = nodeDaoService.getStore(protocol, identifier);
        if (store != null)
        {
            throw new StoreExistsException("Unable to create a store that already exists",
                    new StoreRef(protocol, identifier));
        }
        
        // invoke policies
        invokeBeforeCreateStore(ContentModel.TYPE_STOREROOT, storeRef);
        
        // create a new one
        store = nodeDaoService.createStore(protocol, identifier);
        // get the root node
        Node rootNode = store.getRootNode();
        // assign the root aspect - this is expected of all roots, even store roots
        addAspect(rootNode.getNodeRef(),
                ContentModel.ASPECT_ROOT,
                Collections.<QName, Serializable>emptyMap());
        
        // invoke policies
        invokeOnCreateStore(rootNode.getNodeRef());
        
        // done
        if (!store.getStoreRef().equals(storeRef))
        {
            throw new RuntimeException("Incorrect store reference");
        }
        return storeRef;
    }

    public NodeRef getRootNode(StoreRef storeRef) throws InvalidStoreRefException
    {
        Store store = nodeDaoService.getStore(storeRef.getProtocol(), storeRef.getIdentifier());
        // get the root
        Node node = store.getRootNode();
        NodeRef nodeRef = node.getNodeRef();
        // done
        return nodeRef;
    }

    public ChildAssociationRef createNode(NodeRef parentRef,
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName)
    {
        return this.createNode(parentRef, assocTypeQName, assocQName, nodeTypeQName, null);
    }

    /**
     * @see org.alfresco.service.cmr.repository.NodeService#createNode(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, org.alfresco.service.namespace.QName, org.alfresco.service.namespace.QName, java.util.Map)
     */
    public ChildAssociationRef createNode(NodeRef parentRef,
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName,
            Map<QName, Serializable> properties)
    {
        Assert.notNull(parentRef);
        Assert.notNull(assocTypeQName);
        Assert.notNull(assocQName);
        
        // TODO: Check that the child association is allowed
        if (properties == null)
        {
            properties = Collections.emptyMap();
        }
		
		// Invoke policy behaviour
		invokeBeforeUpdateNode(parentRef);
		invokeBeforeCreateNode(parentRef, assocTypeQName, assocQName, nodeTypeQName);
        
        // get the store that the parent belongs to
        StoreRef storeRef = parentRef.getStoreRef();
        Store store = nodeDaoService.getStore(storeRef.getProtocol(), storeRef.getIdentifier());
        if (store == null)
        {
            throw new RuntimeException("No store found for parent node: " + parentRef);
        }
        
        // create the node instance
        Node node = nodeDaoService.newNode(store, nodeTypeQName);
        NodeRef childRef = node.getNodeRef();
        // get the parent node
        Node parentNode = getNodeNotNull(parentRef);
        
        // create the association - invoke policy behaviour
        //invokeBeforeCreateChildAssociation(parentRef, childRef, assocTypeQName, assocQName);
        ChildAssoc childAssoc = nodeDaoService.newChildAssoc(parentNode, node, true, assocTypeQName, assocQName);
        ChildAssociationRef childAssocRef = childAssoc.getChildAssocRef();
        //invokeOnCreateChildAssociation(childAssocRef);
        
        // get the mandatory aspects for the node type
        TypeDefinition nodeTypeDef = dictionaryService.getType(nodeTypeQName);
        if (nodeTypeDef == null)
        {
            throw new InvalidTypeException(nodeTypeQName);
        }
        List<AspectDefinition> defaultAspectDefs = nodeTypeDef.getDefaultAspects();
        // check that property requirements are met
        checkProperties(nodeTypeDef, defaultAspectDefs, properties);
        
        // add all the aspects to the node
        Set<QName> nodeAspects = node.getAspects();
        for (AspectDefinition defaultAspectDef : defaultAspectDefs)
        {
            nodeAspects.add(defaultAspectDef.getName());
        }
        
        // set the properties - it is a new node so only set properties if there are any
        if (properties.size() > 0)
        {
            this.setProperties(node.getNodeRef(), properties);
        }
        
        // Invoke policy behaviour
		invokeOnCreateNode(childAssocRef);
        invokeOnUpdateNode(parentRef);
		
		// done
		return childAssocRef;
    }
    
    /**
     * Drops the old primary association and creates a new one
     */
    public ChildAssociationRef moveNode(
            NodeRef nodeToMoveRef,
            NodeRef newParentRef,
            QName assocTypeQName,
            QName assocQName)
            throws InvalidNodeRefException
    {
        Assert.notNull(nodeToMoveRef);
        Assert.notNull(newParentRef);
        Assert.notNull(assocTypeQName);
        Assert.notNull(assocQName);
        
        // TODO: Check that the child association is allowed
        
        // check the node references
        Node nodeToMove = getNodeNotNull(nodeToMoveRef);
        Node newParentNode = getNodeNotNull(newParentRef);
        // get the primary parent assoc
        ChildAssoc oldAssoc = nodeDaoService.getPrimaryParentAssoc(nodeToMove);
        ChildAssociationRef oldAssocRef = oldAssoc.getChildAssocRef();
        // get the old parent
        Node oldParentNode = oldAssoc.getParent();
        
        // Invoke policy behaviour
        invokeBeforeDeleteChildAssociation(oldAssocRef);
        invokeBeforeCreateChildAssociation(newParentRef, nodeToMoveRef, assocTypeQName, assocQName);
        invokeBeforeUpdateNode(oldParentNode.getNodeRef());    // old parent will be updated
        invokeBeforeUpdateNode(newParentRef);                  // new parent ditto
        
        // remove the child assoc from the old parent
        nodeDaoService.deleteChildAssoc(oldAssoc);
        // create a new assoc
        ChildAssoc newAssoc = nodeDaoService.newChildAssoc(newParentNode, nodeToMove, true, assocTypeQName, assocQName);

        // invoke policy behaviour
        invokeOnCreateChildAssociation(newAssoc.getChildAssocRef());
        invokeOnDeleteChildAssociation(oldAssoc.getChildAssocRef());
        invokeOnUpdateNode(oldParentNode.getNodeRef());
        invokeOnUpdateNode(newParentRef);
        
        // done
        return newAssoc.getChildAssocRef();
    }

    public QName getType(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        return node.getTypeQName();
    }
    
    /**
     * Checks that the properties required by the given classes or aspects are all present
     * in the map given.
     * <p>
     * No dependencies are fetched e.g. if a <code>TypeDefinition</code> is given, the
     * aspects for that type will not be checked.
     * 
     * @param classDef the primary class definition to check against
     * @param aspectDefs additional aspects definitions to check against - may be null
     * @param properties the properties to check
     * @throws PropertyException if the class or aspect requires a property not present
     *      amongst those provided
     */
    private void checkProperties(
            ClassDefinition classDef,
            List<AspectDefinition> aspectDefs,
            Map<QName, Serializable> properties)
            throws PropertyException
    {
        Map<QName,PropertyDefinition> allPropertyDefs = new HashMap<QName,PropertyDefinition>();

        // add class properties
        allPropertyDefs.putAll(classDef.getProperties()); 
        
        // add additional aspect properties
        if (aspectDefs != null)
        {
            for (AspectDefinition aspectDef : aspectDefs)
            {
                Map<QName,PropertyDefinition> aspectProperties = aspectDef.getProperties();
                allPropertyDefs.putAll(aspectProperties);
            }
        }
        
        // check that each required property is present
        for (PropertyDefinition propertyDef : allPropertyDefs.values())
        {
            // ignore optional properties
            if (!propertyDef.isMandatory())
            {
                continue;
            }
            QName qname = propertyDef.getName();
            // is it present?
            if (properties == null || properties.containsKey(qname) == false)
            {
                // not present
                throw new PropertyException("Mandatory property value not supplied: " + qname, qname);
            }
            // property has a value
        }
        // all required properties have values
        // done
    }
    
    /**
     * @param node
     * @return Returns a list of all aspects (default and optional) applied to the node
     */
    private List<AspectDefinition> getNodeAspects(Node node)
    {
        Set<QName> aspectQNames = node.getAspects();
        List<AspectDefinition> aspectDefs = new ArrayList<AspectDefinition>(aspectQNames.size());
        for (QName qname : aspectQNames)
        {
            AspectDefinition aspectDef = dictionaryService.getAspect(qname);
            aspectDefs.add(aspectDef);
        }
        // done
        return aspectDefs;
    }
    
    /**
     * @see #checkProperties(ClassDefinition, List<AspectDefinition>, Map<QName,Serializable>)
     * @see Node#getAspects()
     */
    public void addAspect(
            NodeRef nodeRef,
            QName aspectTypeQName,
            Map<QName, Serializable> aspectProperties)
            throws InvalidNodeRefException, InvalidAspectException, PropertyException
    {
        // get the aspect
        AspectDefinition aspectDef = dictionaryService.getAspect(aspectTypeQName);
        if (aspectDef == null)
        {
            throw new InvalidAspectException(aspectTypeQName);
        }
        // check that the properties supplied are adequate for the aspect
        checkProperties(aspectDef, null, aspectProperties);
        
        // Invoke policy behaviours
        invokeBeforeUpdateNode(nodeRef);
        invokeBeforeAddAspect(nodeRef, aspectTypeQName);
        
        Node node = getNodeNotNull(nodeRef);
        // physically attach the aspect to the node
        node.getAspects().add(aspectTypeQName);
        
		if (aspectProperties != null)
		{
			// attach the properties to the current node properties
		    Map<QName, Serializable> nodeProperties = getProperties(nodeRef);
		    nodeProperties.putAll(aspectProperties);
		    setProperties(nodeRef, nodeProperties);
		}
		
		// Invoke policy behaviours
		invokeOnUpdateNode(nodeRef);
        invokeOnAddAspect(nodeRef, aspectTypeQName);
    }

    /**
     * @see Node#getAspects()
     */
    public void removeAspect(NodeRef nodeRef, QName aspectTypeQName)
            throws InvalidNodeRefException, InvalidAspectException
    {
		// Invoke policy behaviours
		invokeBeforeUpdateNode(nodeRef);
        invokeBeforeRemoveAspect(nodeRef, aspectTypeQName);
		
        // get the aspect
        AspectDefinition aspectDef = dictionaryService.getAspect(aspectTypeQName);
        if (aspectDef == null)
        {
            throw new InvalidAspectException(aspectTypeQName);
        }
        // get the node
        Node node = getNodeNotNull(nodeRef);
        
        // check that the aspect may be removed
        TypeDefinition nodeTypeDef = dictionaryService.getType(node.getTypeQName());
        if (nodeTypeDef == null)
        {
            throw new InvalidNodeRefException("The node type is no longer valid: " + nodeRef, nodeRef);
        }
        List<AspectDefinition> defaultAspects = nodeTypeDef.getDefaultAspects();
        if (defaultAspects.contains(aspectDef))
        {
            throw new InvalidAspectException(
                    "The aspect is a default for the node's type and cannot be removed: " + aspectTypeQName,
                    aspectTypeQName);
        }
        
        // remove the aspect, if present
        boolean removed = node.getAspects().remove(aspectTypeQName);
        // if the aspect was present, remove the associated properties
        if (removed)
        {
            Map<String, Serializable> nodeProperties = node.getProperties();
            Map<QName,PropertyDefinition> propertyDefs = aspectDef.getProperties();
            for (QName propertyName : propertyDefs.keySet())
            {
                nodeProperties.remove(propertyName.toString());
            }
            
            // Invoke policy behaviours
            invokeOnUpdateNode(nodeRef);
            invokeOnRemoveAspect(nodeRef, aspectTypeQName);
        }
    }

    /**
     * Performs a check on the set of node aspects
     * 
     * @see Node#getAspects()
     */
    public boolean hasAspect(NodeRef nodeRef, QName aspectRef) throws InvalidNodeRefException, InvalidAspectException
    {
        Node node = getNodeNotNull(nodeRef);
        Set<QName> aspectQNames = node.getAspects();
        boolean hasAspect = aspectQNames.contains(aspectRef);
        // done
        return hasAspect;
    }

    /**
     * Transforms the results from {@link Node#getAspects()} into an unmodifiable set
     */
    public Set<QName> getAspects(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        Set<QName> aspectQNames = node.getAspects();
        // copy the set to ensure initialization
        Set<QName> ret = new HashSet<QName>(aspectQNames.size());
        ret.addAll(aspectQNames);
        // done
        return Collections.unmodifiableSet(ret);
    }

    public void deleteNode(NodeRef nodeRef)
    {
		// Invoke policy behaviours
		invokeBeforeDeleteNode(nodeRef);
		
        // get the node
        Node node = getNodeNotNull(nodeRef);
        // get the primary parent-child relationship before it is gone
        ChildAssociationRef childAssocRef = getPrimaryParent(nodeRef);
		// get type and aspect QNames as they will be unavailable after the delete
		QName nodeTypeQName = node.getTypeQName();
        Set<QName> nodeAspectQNames = node.getAspects();
        // delete it
        nodeDaoService.deleteNode(node);
		
		// Invoke policy behaviours
		invokeOnDeleteNode(childAssocRef, nodeTypeQName, nodeAspectQNames);
    }

    public ChildAssociationRef addChild(NodeRef parentRef, NodeRef childRef, QName assocTypeQName, QName assocQName)
    {
		// Invoke policy behaviours
		invokeBeforeUpdateNode(parentRef);
        invokeBeforeCreateChildAssociation(parentRef, childRef, assocTypeQName, assocQName);
		
        // TODO: Check that the child association is allowed
        // check that both nodes belong to the same store
        if (!parentRef.getStoreRef().equals(childRef.getStoreRef()))
        {
            throw new InvalidNodeRefException("Parent and child nodes must belong to the same store: \n" +
                    "   parent: " + parentRef + "\n" +
                    "   child: " + childRef,
                    childRef);
        }
        String protocol = parentRef.getStoreRef().getProtocol();
        String identifier = parentRef.getStoreRef().getIdentifier();
        // get the parent node and ensure that it is a container node
        Node parentNode = getNodeNotNull(parentRef);
        // get the child node
        Node childNode = getNodeNotNull(childRef);
        // make the association
        ChildAssoc assoc = nodeDaoService.newChildAssoc(parentNode, childNode, false, assocTypeQName, assocQName);

		// Invoke policy behaviours
        invokeOnCreateChildAssociation(assoc.getChildAssocRef());
		invokeOnUpdateNode(parentRef);
		
        return assoc.getChildAssocRef();
    }

    public Collection<EntityRef> removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException
    {
        Node parentNode = getNodeNotNull(parentRef);
        Node childNode = getNodeNotNull(childRef);
        NodeKey childNodeKey = childNode.getKey();
        
        // maintain a list of deleted entities
        List<EntityRef> deletedRefs = new ArrayList<EntityRef>(5);
        
        // get all the child assocs
        ChildAssociationRef primaryAssocRef = null;
        Set<ChildAssoc> assocs = parentNode.getChildAssocs();
        assocs = new HashSet<ChildAssoc>(assocs);   // copy set as we will be modifying it
        for (ChildAssoc assoc : assocs)
        {
            if (!assoc.getChild().getKey().equals(childNodeKey))
            {
                continue;  // not a matching association
            }
            ChildAssociationRef assocRef = assoc.getChildAssocRef();
            // Is this a primary association?
            if (assoc.getIsPrimary())
            {
                // keep the primary associaton for last
                primaryAssocRef = assocRef;
            }
            else
            {
                // delete the association instance - it is not primary
                invokeBeforeDeleteChildAssociation(assocRef);
                nodeDaoService.deleteChildAssoc(assoc);
                invokeOnDeleteChildAssociation(assocRef);
                deletedRefs.add(assoc.getChildAssocRef());    // save for return value
            }
        }
        // remove the child if the primary association was a match
        if (primaryAssocRef != null)
        {
            deleteNode(primaryAssocRef.getChildRef());
            deletedRefs.add(primaryAssocRef);
            deletedRefs.add(primaryAssocRef.getChildRef());
        }

		// Invoke policy behaviours
		invokeOnUpdateNode(parentRef);
		
        // done
        return deletedRefs;
    }

    public Map<QName, Serializable> getProperties(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        Map<String, Serializable> nodeProperties = node.getProperties();
        Map<QName, Serializable> ret = new HashMap<QName, Serializable>(nodeProperties.size());
        // copy values
        for (Map.Entry entry: nodeProperties.entrySet())
        {
            String key = (String) entry.getKey();
            Serializable value = (Serializable) entry.getValue();
            // check if the property is a null
            if (value instanceof DbNodeServiceImpl.NullPropertyValue)
            {
                value = null;
            }
            QName qname = QName.createQName(key.toString());
            // copy across
            ret.put(qname, value);
        }
        return ret;
    }
    
    public Serializable getProperty(NodeRef nodeRef, QName qname) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        Map<String, Serializable> properties = node.getProperties();
        Serializable value = properties.get(qname.toString());
        // check if the property is a null
        if (value instanceof DbNodeServiceImpl.NullPropertyValue)
        {
            value = null;
        }
        // done
        return value;
    }

    /**
     * Ensures that all required properties are present on the node and copies the
     * property values to the <code>Node</code>.
     * <p>
     * Null-valued properties are removed.
     * <p>
     * If any of the values are null, a marker object is put in to mimic nulls.  They will be turned back into
     * a real nulls when the properties are requested again.
     * 
     * @see #checkProperties(ClassDefinition, List<AspectDefinition>, Map<QName,Serializable>)
     * @see Node#getProperties()
     */
    public void setProperties(NodeRef nodeRef, Map<QName, Serializable> properties) throws InvalidNodeRefException
    {
		// Invoke policy behaviours
		invokeBeforeUpdateNode(nodeRef);
		
        if (properties == null)
        {
            throw new IllegalArgumentException("Properties may not be null");
        }
        Node node = getNodeNotNull(nodeRef);

        // check that the properties fulfill all the requirements of the node type
        // and any additional aspects
        QName nodeClassRef = getType(nodeRef);
        ClassDefinition nodeClassDef = dictionaryService.getClass(nodeClassRef);
        List<AspectDefinition> nodeAspectDefs = getNodeAspects(node);
        checkProperties(nodeClassDef, nodeAspectDefs, properties);  // confirms that properties are valid
        
        // copy properties onto node
        Map<String, Serializable> nodeProperties = node.getProperties();
        nodeProperties.clear();
        // copy all the values across
        for (QName qname : properties.keySet())
        {
            Serializable value = properties.get(qname);
            // if the value is null, it gets replaced with a dummy serializable
            if (value == null)
            {
                value = new DbNodeServiceImpl.NullPropertyValue();
            }
            nodeProperties.put(qname.toString(), value);
        }

		// Invoke policy behaviours
		invokeOnUpdateNode(nodeRef);
    }

    /**
     * Gets the properties map, sets the value (null is allowed) and checks that the new set
     * of properties is valid.
     * <p>
     * If the value is null, a marker object is put in to mimic a null.  It will be turned back into
     * a real null when the property is requested again.
     * 
     * @see DbNodeServiceImpl.NullPropertyValue
     */
    public void setProperty(NodeRef nodeRef, QName qname, Serializable value) throws InvalidNodeRefException
    {
        Assert.notNull(qname);
        
        // if the value is null, it gets replaced with a dummy serializable
        if (value == null)
        {
            value = new DbNodeServiceImpl.NullPropertyValue();
        }
        
		// Invoke policy behaviours
		invokeBeforeUpdateNode(nodeRef);
		
        Node node = getNodeNotNull(nodeRef);
        Map<String, Serializable> properties = node.getProperties();
        properties.put(qname.toString(), value);

		// Invoke policy behaviours
		invokeOnUpdateNode(nodeRef);
    }

    /**
     * Transforms {@link Node#getParentAssocs()} to a new collection
     */
    public Collection<NodeRef> getParents(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        // get the assocs pointing to it
        Set<ChildAssoc> parentAssocs = node.getParentAssocs();
        // list of results
        Collection<NodeRef> results = new ArrayList<NodeRef>(parentAssocs.size());
        for (ChildAssoc assoc : parentAssocs)
        {
            // get the parent
            Node parentNode = assoc.getParent();
            results.add(parentNode.getNodeRef());
        }
        // done
        return Collections.unmodifiableCollection(results);
    }

    /**
     * Filters out any associations if their qname is not a match to the given pattern.
     * <p>
     * Transforms {@link Node#getParentAssocs()} into an unmodifiable list
     */
    public List<ChildAssociationRef> getParentAssocs(NodeRef nodeRef, QNamePattern qnamePattern)
    {
        Node node = getNodeNotNull(nodeRef);
        // get the assocs pointing to it
        Set<ChildAssoc> parentAssocs = node.getParentAssocs();
        // shortcut if there are no assocs
        if (parentAssocs.size() == 0)
        {
            return Collections.emptyList();
        }
        // list of results
        List<ChildAssociationRef> results = new ArrayList<ChildAssociationRef>(parentAssocs.size());
        for (ChildAssoc assoc : parentAssocs)
        {
            // does the qname match the pattern?
            if (!qnamePattern.isMatch(assoc.getQName()))
            {
                // no match - ignore
                continue;
            }
            results.add(assoc.getChildAssocRef());
        }
        // done
        return Collections.unmodifiableList(results);
    }

    /**
     * Filters out any associations if their qname is not a match to the given pattern.
     * <p>
     * Transforms {@link Node#getChildAssocs()} into an unmodifiable list.
     */
    public List<ChildAssociationRef> getChildAssocs(NodeRef nodeRef, QNamePattern qnamePattern)
    {
        Node node = getNodeNotNull(nodeRef);
        // get the assocs pointing from it
        Set<ChildAssoc> childAssocs = node.getChildAssocs();
        // shortcut if there are no assocs
        if (childAssocs.size() == 0)
        {
            return Collections.emptyList();
        }
        // list of results
        List<ChildAssociationRef> results = new ArrayList<ChildAssociationRef>(childAssocs.size());
        for (ChildAssoc assoc : childAssocs)
        {
            // does the qname match the pattern?
            if (!qnamePattern.isMatch(assoc.getQName()))
            {
                // no match - ignore
                continue;
            }
            // get the child
            results.add(assoc.getChildAssocRef());
        }
        // done
        return Collections.unmodifiableList(results);
    }

    public ChildAssociationRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Node node = getNodeNotNull(nodeRef);
        // get the primary parent assoc
        ChildAssoc assoc = nodeDaoService.getPrimaryParentAssoc(node);

        // done - the assoc may be null for a root node
        ChildAssociationRef assocRef = null;
        if (assoc == null)
        {
            assocRef = new ChildAssociationRef(null, null, null, nodeRef);
        }
        else
        {
            assocRef = assoc.getChildAssocRef();
        }
        return assocRef;
    }

    public AssociationRef createAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocTypeQName)
            throws InvalidNodeRefException, AssociationExistsException
    {
		// Invoke policy behaviours
		invokeBeforeUpdateNode(sourceRef);
		
        // TODO: Check that the association is allowed
        Node sourceNode = getNodeNotNull(sourceRef);
        Node targetNode = getNodeNotNull(targetRef);
        // see if it exists
        NodeAssoc assoc = nodeDaoService.getNodeAssoc(sourceNode, targetNode, assocTypeQName);
        if (assoc != null)
        {
            throw new AssociationExistsException(sourceRef, targetRef, assocTypeQName);
        }
        // we are sure that the association doesn't exist - make it
        assoc = nodeDaoService.newNodeAssoc(sourceNode, targetNode, assocTypeQName);
        AssociationRef assocRef = assoc.getNodeAssocRef();

		// Invoke policy behaviours
		invokeOnUpdateNode(sourceRef);
		
        return assocRef;
    }

    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocTypeQName)
            throws InvalidNodeRefException
    {
		// Invoke policy behaviours
		invokeBeforeUpdateNode(sourceRef);
		
        Node sourceNode = getNodeNotNull(sourceRef);
        Node targetNode = getNodeNotNull(targetRef);
        // get the association
        NodeAssoc assoc = nodeDaoService.getNodeAssoc(sourceNode, targetNode, assocTypeQName);
        // delete it
        nodeDaoService.deleteNodeAssoc(assoc);
		
		// Invoke policy behaviours
		invokeOnUpdateNode(sourceRef);
    }

    /**
     * Transforms {@link NodeDaoService#getNodeAssocTargets(Node, String)} into
     * an unmodifiable collection.
     */
    public List<AssociationRef> getTargetAssocs(NodeRef sourceRef, QNamePattern qnamePattern)
            throws InvalidNodeRefException
    {
        Node sourceNode = getNodeNotNull(sourceRef);
        // get all assocs to target
        Set<NodeAssoc> assocs = sourceNode.getTargetNodeAssocs();
        List<AssociationRef> nodeAssocRefs = new ArrayList<AssociationRef>(assocs.size());
        for (NodeAssoc assoc : assocs)
        {
            // check qname pattern
            if (!qnamePattern.isMatch(assoc.getTypeQName()))
            {
                continue;   // the assoc name doesn't match the pattern given 
            }
            nodeAssocRefs.add(assoc.getNodeAssocRef());
        }
        // done
        return Collections.unmodifiableList(nodeAssocRefs);
    }

    /**
     * Transforms {@link NodeDaoService#getNodeAssocSources(Node, String)} into
     * an unmodifiable collection.
     */
    public List<AssociationRef> getSourceAssocs(NodeRef targetRef, QNamePattern qnamePattern)
            throws InvalidNodeRefException
    {
        Node sourceNode = getNodeNotNull(targetRef);
        // get all assocs to source
        Set<NodeAssoc> assocs = sourceNode.getSourceNodeAssocs();
        List<AssociationRef> nodeAssocRefs = new ArrayList<AssociationRef>(assocs.size());
        for (NodeAssoc assoc : assocs)
        {
            // check qname pattern
            if (!qnamePattern.isMatch(assoc.getTypeQName()))
            {
                continue;   // the assoc name doesn't match the pattern given 
            }
            nodeAssocRefs.add(assoc.getNodeAssocRef());
        }
        // done
        return Collections.unmodifiableList(nodeAssocRefs);
    }
    
    /**
     * Recursive method used to build up paths from a given node to the root.
     * <p>
     * Whilst walking up the hierarchy to the root, some nodes may have a <b>root</b> aspect.
     * Everytime one of these is encountered, a new path is farmed off, but the method
     * continues to walk up the hierarchy.
     * 
     * @param currentNode the node to start from, i.e. the child node to work upwards from
     * @param currentPath the path from the current node to the descendent that we started from
     * @param completedPaths paths that have reached the root are added to this collection
     * @param assocStack the parent-child relationships traversed whilst building the path.
     *      Used to detected cyclic relationships.
     * @param primaryOnly true if only the primary parent association must be traversed.
     *      If this is true, then the only root is the top level node having no parents.
     * @throws CyclicChildRelationshipException
     */
    private void prependPaths(final Node currentNode,
            final Path currentPath,
            Collection<Path> completedPaths,
            Stack<ChildAssoc> assocStack,
            boolean primaryOnly)
        throws CyclicChildRelationshipException
    {
        NodeRef currentNodeRef = currentNode.getNodeRef();
        // get the parent associations of the given node
        Set<ChildAssoc> parentAssocs = currentNode.getParentAssocs();
        // does the node have parents
        boolean hasParents = parentAssocs.size() > 0;
        // does the current node have a root aspect?
        boolean isRoot = hasAspect(currentNodeRef, ContentModel.ASPECT_ROOT);
        boolean isStoreRoot = currentNode.getTypeQName().equals(ContentModel.TYPE_STOREROOT);
        
        // look for a root.  If we only want the primary root, then ignore all but the top-level root.
        if (isRoot && !(primaryOnly && hasParents))  // exclude primary search with parents present
        {
            // create a one-sided assoc ref for the root node and prepend to the stack
            // this effectively spoofs the fact that the current node is not below the root
            // - we put this assoc in as the first assoc in the path must be a one-sided
            //   reference pointing to the root node
            ChildAssociationRef assocRef = new ChildAssociationRef(
                    null,
                    null,
                    null,
                    getRootNode(currentNode.getNodeRef().getStoreRef()));
            // create a path to save and add the 'root' assoc
            Path pathToSave = new Path();
            Path.ChildAssocElement first = null;
            for (Path.Element element: currentPath)
            {
                if (first == null)
                {
                    first = (Path.ChildAssocElement) element;
                }
                else
                {
                    pathToSave.append(element);
                }
            }
            if (first != null)
            {
                // mimic an association that would appear if the current node was below
                // the root node
                // or if first beneath the root node it will make the real thing 
                ChildAssociationRef updateAssocRef = new ChildAssociationRef(
                       isStoreRoot ? ContentModel.ASSOC_CHILDREN : first.getRef().getTypeQName(),
                       getRootNode(currentNode.getNodeRef().getStoreRef()),
                       first.getRef().getQName(),
                       first.getRef().getChildRef());
                Path.Element newFirst =  new Path.ChildAssocElement(updateAssocRef);
                pathToSave.prepend(newFirst);
            }
            
            Path.Element element = new Path.ChildAssocElement(assocRef);
            pathToSave.prepend(element);
            
            // store the path just built
            completedPaths.add(pathToSave);
        }

        if (parentAssocs.size() == 0 && !isRoot)
        {
            throw new RuntimeException("Node without parents does not have root aspect: " +
                    currentNodeRef);
        }
        // walk up each parent association
        for (ChildAssoc assoc : parentAssocs)
        {
            // does the association already exist in the stack
            if (assocStack.contains(assoc))
            {
                // the association was present already
                throw new CyclicChildRelationshipException(
                        "Cyclic parent-child relationship detected: \n" +
                        "   current node: " + currentNode + "\n" +
                        "   current path: " + currentPath + "\n" +
                        "   next assoc: " + assoc,
                        assoc);
            }
            // do we consider only primary assocs?
            if (primaryOnly && !assoc.getIsPrimary())
            {
                continue;
            }
            // build a path element
            NodeRef parentRef = assoc.getParent().getNodeRef();
            QName qname = assoc.getQName();
            NodeRef childRef = assoc.getChild().getNodeRef();
            boolean isPrimary = assoc.getIsPrimary();
            // build a real association reference
            ChildAssociationRef assocRef = new ChildAssociationRef(assoc.getTypeQName(), parentRef, qname, childRef, isPrimary, -1);
            // TODO: Issue - Is ordering relevant here?
            Path.Element element = new Path.ChildAssocElement(assocRef);
            // create a new path that builds on the current path
            Path path = new Path();
            path.append(currentPath);
            // prepend element
            path.prepend(element);
            // get parent node
            Node parentNode = assoc.getParent();
            
            // push the assoc stack, recurse and pop
            assocStack.push(assoc);
            prependPaths(parentNode, path, completedPaths, assocStack, primaryOnly);
            assocStack.pop();
        }
        // done
    }

    /**
     * @see #getPaths(NodeRef, boolean)
     * @see #prependPaths(Node, Path, Collection<Path>, Stack<ChildAssoc>, boolean)
     */
    public Path getPath(NodeRef nodeRef) throws InvalidNodeRefException
    {
        Collection<Path> paths = getPaths(nodeRef, true);   // checks primary path count
        if (paths.size() == 1)
        {
            for (Path path : paths)
            {
                return path;   // we know there is only one
            }
        }
        throw new RuntimeException("Primary path count not checked");  // checked by getPaths()
    }

    /**
     * When searching for <code>primaryOnly == true</code>, checks that there is exactly
     * one path.
     * @see #prependPaths(Node, Path, Collection<Path>, Stack<ChildAssoc>, boolean)
     */
    public Collection<Path> getPaths(NodeRef nodeRef, boolean primaryOnly) throws InvalidNodeRefException
    {
        // get the starting node
        Node node = getNodeNotNull(nodeRef);
        // create storage for the paths - only need 1 bucket if we are looking for the primary path
        Collection<Path> paths = new ArrayList<Path>(primaryOnly ? 1 : 10);
        // create an emtpy current path to start from
        Path currentPath = new Path();
        // create storage for touched associations
        Stack<ChildAssoc> assocStack = new Stack<ChildAssoc>();
        // call recursive method to sort it out
        prependPaths(node, currentPath, paths, assocStack, primaryOnly);
        
        // check that for the primary only case we have exactly one path
        if (primaryOnly && paths.size() != 1)
        {
            throw new RuntimeException("Node has " + paths.size() + " primary paths: " + nodeRef);
        }
        
        // done
        return Collections.unmodifiableCollection(paths);
    }
    
    /**
     * Simple marker class to allow setting and retrieval of null property values. 
     */
    private static class NullPropertyValue implements Serializable
    {
        private static final long serialVersionUID = 3977860683100664115L;
    }
}
