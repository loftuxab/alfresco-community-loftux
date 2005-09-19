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
package org.alfresco.repo.node;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.repo.node.NodeServicePolicies.BeforeAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateStorePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeUpdateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateStorePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.AssociationPolicyDelegate;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.search.Indexer;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.XPathException;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides common functionality for
 * {@link org.alfresco.service.cmr.repository.NodeService} implementations.
 * <p>
 * Some of the overloaded simpler versions of methods are implemented by passing
 * through the defaults as required.
 * <p>
 * The callback handling is also provided as a convenience for implementations.
 * 
 * @author Derek Hulley
 */
public abstract class AbstractNodeServiceImpl implements NodeService
{
    private static Log logger = LogFactory.getLog(AbstractNodeServiceImpl.class);

    /** a uuid identifying this unique instance */
    private String uuid;
    /** controls policy delegates */
    private PolicyComponent policyComponent;
    /** model reference */
    private DictionaryService dictionaryService;

    /*
     * Policy delegates
     */
    private ClassPolicyDelegate<BeforeCreateStorePolicy> beforeCreateStoreDelegate;
    private ClassPolicyDelegate<OnCreateStorePolicy> onCreateStoreDelegate;
    private ClassPolicyDelegate<BeforeCreateNodePolicy> beforeCreateNodeDelegate;
    private ClassPolicyDelegate<OnCreateNodePolicy> onCreateNodeDelegate;
    private ClassPolicyDelegate<BeforeUpdateNodePolicy> beforeUpdateNodeDelegate;
    private ClassPolicyDelegate<OnUpdateNodePolicy> onUpdateNodeDelegate;
    private ClassPolicyDelegate<OnUpdatePropertiesPolicy> onUpdatePropertiesDelegate;
    private ClassPolicyDelegate<BeforeDeleteNodePolicy> beforeDeleteNodeDelegate;
    private ClassPolicyDelegate<OnDeleteNodePolicy> onDeleteNodeDelegate;
    private ClassPolicyDelegate<BeforeAddAspectPolicy> beforeAddAspectDelegate;
    private ClassPolicyDelegate<OnAddAspectPolicy> onAddAspectDelegate;
    private ClassPolicyDelegate<BeforeRemoveAspectPolicy> beforeRemoveAspectDelegate;
    private ClassPolicyDelegate<OnRemoveAspectPolicy> onRemoveAspectDelegate;
    private AssociationPolicyDelegate<BeforeCreateChildAssociationPolicy> beforeCreateChildAssociationDelegate;
    private AssociationPolicyDelegate<OnCreateChildAssociationPolicy> onCreateChildAssociationDelegate;
    private AssociationPolicyDelegate<BeforeDeleteChildAssociationPolicy> beforeDeleteChildAssociationDelegate;
    private AssociationPolicyDelegate<OnDeleteChildAssociationPolicy> onDeleteChildAssociationDelegate;
    private AssociationPolicyDelegate<OnCreateAssociationPolicy> onCreateAssociationDelegate;
    private AssociationPolicyDelegate<OnDeleteAssociationPolicy> onDeleteAssociationDelegate;

    /**
     * @param policyComponent
     *            the component with which to register class policies and
     *            behaviour
     * @param dictionaryService
     *            used to check that node operations conform to the model
     */
    protected AbstractNodeServiceImpl(PolicyComponent policyComponent, DictionaryService dictionaryService)
    {
        this.uuid = GUID.generate();
        this.policyComponent = policyComponent;
        this.dictionaryService = dictionaryService;
    }

    /**
     * Checks equality by type and uuid
     */
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (!(obj instanceof AbstractNodeServiceImpl))
        {
            return false;
        }
        AbstractNodeServiceImpl that = (AbstractNodeServiceImpl) obj;
        return this.uuid.equals(that.uuid);
    }
    
    /**
     * @see #uuid
     */
    public int hashCode()
    {
        return uuid.hashCode();
    }

    /**
     * Registers the node policies as well as node indexing behaviour if the
     * {@link #setIndexer(Indexer) indexer} is present.
     */
    public void init()
    {
        // Register the various policies
        beforeCreateStoreDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeCreateStorePolicy.class);
        onCreateStoreDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnCreateStorePolicy.class);
        beforeCreateNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeCreateNodePolicy.class);
        onCreateNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnCreateNodePolicy.class);
        beforeUpdateNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeUpdateNodePolicy.class);
        onUpdateNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnUpdateNodePolicy.class);
        onUpdatePropertiesDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnUpdatePropertiesPolicy.class);
        beforeDeleteNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeDeleteNodePolicy.class);
        onDeleteNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnDeleteNodePolicy.class);

        beforeAddAspectDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeAddAspectPolicy.class);
        onAddAspectDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnAddAspectPolicy.class);
        beforeRemoveAspectDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeRemoveAspectPolicy.class);
        onRemoveAspectDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnRemoveAspectPolicy.class);

        beforeCreateChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.BeforeCreateChildAssociationPolicy.class);
        onCreateChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.OnCreateChildAssociationPolicy.class);
        beforeDeleteChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.BeforeDeleteChildAssociationPolicy.class);
        onDeleteChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.OnDeleteChildAssociationPolicy.class);

        onCreateAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.OnCreateAssociationPolicy.class);
        onDeleteAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.OnDeleteAssociationPolicy.class);
    }

    /**
     * @see NodeServicePolicies.BeforeCreateStorePolicy#beforeCreateStore(QName,
     *      StoreRef)
     */
    protected void invokeBeforeCreateStore(QName nodeTypeQName, StoreRef storeRef)
    {
        NodeServicePolicies.BeforeCreateStorePolicy policy = this.beforeCreateStoreDelegate.get(nodeTypeQName);
        policy.beforeCreateStore(nodeTypeQName, storeRef);
    }

    /**
     * @see NodeServicePolicies.OnCreateStorePolicy#onCreateStore(NodeRef)
     */
    protected void invokeOnCreateStore(NodeRef rootNodeRef)
    {
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(rootNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnCreateStorePolicy policy = onCreateStoreDelegate.get(qnames);
        policy.onCreateStore(rootNodeRef);
    }

    /**
     * @see NodeServicePolicies.BeforeCreateNodePolicy#beforeCreateNode(NodeRef,
     *      QName, QName, QName)
     */
    protected void invokeBeforeCreateNode(NodeRef parentNodeRef, QName assocTypeQName, QName assocQName, QName childNodeTypeQName)
    {
        // execute policy for node type
        NodeServicePolicies.BeforeCreateNodePolicy policy = beforeCreateNodeDelegate.get(childNodeTypeQName);
        policy.beforeCreateNode(parentNodeRef, assocTypeQName, assocQName, childNodeTypeQName);
    }

    /**
     * @see NodeServicePolicies.OnCreateNodePolicy#onCreateNode(ChildAssociationRef)
     */
    protected void invokeOnCreateNode(ChildAssociationRef childAssocRef)
    {
        NodeRef childNodeRef = childAssocRef.getChildRef();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(childNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnCreateNodePolicy policy = onCreateNodeDelegate.get(qnames);
        policy.onCreateNode(childAssocRef);
    }

    /**
     * @see NodeServicePolicies.BeforeUpdateNodePolicy#beforeUpdateNode(NodeRef)
     */
    protected void invokeBeforeUpdateNode(NodeRef nodeRef)
    {
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(nodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.BeforeUpdateNodePolicy policy = beforeUpdateNodeDelegate.get(qnames);
        policy.beforeUpdateNode(nodeRef);
    }

    /**
     * @see NodeServicePolicies.OnUpdateNodePolicy#onUpdateNode(NodeRef)
     */
    protected void invokeOnUpdateNode(NodeRef nodeRef)
    {
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(nodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnUpdateNodePolicy policy = onUpdateNodeDelegate.get(qnames);
        policy.onUpdateNode(nodeRef);
    }
    
    /**
     * @see NodeServicePolicies.OnUpdateProperties#onUpdatePropertiesPolicy(NodeRef, Map, Map)
     */
    protected void invokeOnUpdateProperties(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(nodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnUpdatePropertiesPolicy policy = onUpdatePropertiesDelegate.get(qnames);
        policy.onUpdateProperties(nodeRef, before, after);
    }

    /**
     * @see NodeServicePolicies.BeforeDeleteNodePolicy#beforeDeleteNode(NodeRef)
     */
    protected void invokeBeforeDeleteNode(NodeRef nodeRef)
    {
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(nodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.BeforeDeleteNodePolicy policy = beforeDeleteNodeDelegate.get(qnames);
        policy.beforeDeleteNode(nodeRef);
    }

    /**
     * @see NodeServicePolicies.OnDeleteNodePolicy#onDeleteNode(ChildAssociationRef)
     */
    protected void invokeOnDeleteNode(ChildAssociationRef childAssocRef, QName childNodeTypeQName, Set<QName> childAspectQnames)
    {
        NodeRef parentNodeRef = childAssocRef.getParentRef();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnDeleteNodePolicy policy = onDeleteNodeDelegate.get(qnames);
        policy.onDeleteNode(childAssocRef);
    }

    /**
     * @see NodeServicePolicies.BeforeAddAspectPolicy#beforeAddAspect(NodeRef,
     *      QName)
     */
    protected void invokeBeforeAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeServicePolicies.BeforeAddAspectPolicy policy = beforeAddAspectDelegate.get(aspectTypeQName);
        policy.beforeAddAspect(nodeRef, aspectTypeQName);
    }

    /**
     * @see NodeServicePolicies.OnAddAspectPolicy#onAddAspect(NodeRef, QName)
     */
    protected void invokeOnAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeServicePolicies.OnAddAspectPolicy policy = onAddAspectDelegate.get(aspectTypeQName);
        policy.onAddAspect(nodeRef, aspectTypeQName);
    }

    /**
     * @see NodeServicePolicies.BeforeRemoveAspectPolicy#BeforeRemoveAspect(NodeRef,
     *      QName)
     */
    protected void invokeBeforeRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeServicePolicies.BeforeRemoveAspectPolicy policy = beforeRemoveAspectDelegate.get(aspectTypeQName);
        policy.beforeRemoveAspect(nodeRef, aspectTypeQName);
    }

    /**
     * @see NodeServicePolicies.OnRemoveAspectPolicy#onRemoveAspect(NodeRef,
     *      QName)
     */
    protected void invokeOnRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeServicePolicies.OnRemoveAspectPolicy policy = onRemoveAspectDelegate.get(aspectTypeQName);
        policy.onRemoveAspect(nodeRef, aspectTypeQName);
    }

    /**
     * @see NodeServicePolicies.BeforeCreateChildAssociationPolicy#beforeCreateChildAssociation(NodeRef,
     *      NodeRef, QName, QName)
     */
    protected void invokeBeforeCreateChildAssociation(NodeRef parentNodeRef, NodeRef childNodeRef, QName assocTypeQName, QName assocQName)
    {
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type
        NodeServicePolicies.BeforeCreateChildAssociationPolicy policy = beforeCreateChildAssociationDelegate.get(qnames, assocTypeQName);
        policy.beforeCreateChildAssociation(parentNodeRef, childNodeRef, assocTypeQName, assocQName);
    }

    /**
     * @see NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(ChildAssociationRef)
     */
    protected void invokeOnCreateChildAssociation(ChildAssociationRef childAssocRef)
    {
        // Get the parent reference and the assoc type qName
        NodeRef parentNodeRef = childAssocRef.getParentRef();
        QName assocTypeQName = childAssocRef.getTypeQName();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnCreateChildAssociationPolicy policy = onCreateChildAssociationDelegate.get(qnames, assocTypeQName);
        policy.onCreateChildAssociation(childAssocRef);
    }

    /**
     * @see NodeServicePolicies.BeforeDeleteChildAssociationPolicy#beforeDeleteChildAssociation(ChildAssociationRef)
     */
    protected void invokeBeforeDeleteChildAssociation(ChildAssociationRef childAssocRef)
    {
        NodeRef parentNodeRef = childAssocRef.getParentRef();
        QName assocTypeQName = childAssocRef.getTypeQName();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.BeforeDeleteChildAssociationPolicy policy = beforeDeleteChildAssociationDelegate.get(qnames, assocTypeQName);
        policy.beforeDeleteChildAssociation(childAssocRef);
    }

    /**
     * @see NodeServicePolicies.OnDeleteChildAssociationPolicy#onDeleteChildAssociation(ChildAssociationRef)
     */
    protected void invokeOnDeleteChildAssociation(ChildAssociationRef childAssocRef)
    {
        NodeRef parentNodeRef = childAssocRef.getParentRef();
        QName assocTypeQName = childAssocRef.getTypeQName();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnDeleteChildAssociationPolicy policy = onDeleteChildAssociationDelegate.get(qnames, assocTypeQName);
        policy.onDeleteChildAssociation(childAssocRef);
    }

    /**
     * @see NodeServicePolicies.OnCreateAssociationPolicy#onCreateAssociation(NodeRef, NodeRef, QName)
     */
    protected void invokeOnCreateAssociation(AssociationRef nodeAssocRef)
    {
        NodeRef sourceNodeRef = nodeAssocRef.getSourceRef();
        QName assocTypeQName = nodeAssocRef.getTypeQName();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(sourceNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnCreateAssociationPolicy policy = onCreateAssociationDelegate.get(qnames, assocTypeQName);
        policy.onCreateAssociation(nodeAssocRef);
    }

    /**
     * @see NodeServicePolicies.OnDeleteAssociationPolicy#onDeleteAssociation(AssociationRef)
     */
    protected void invokeOnDeleteAssociation(AssociationRef nodeAssocRef)
    {
        NodeRef sourceNodeRef = nodeAssocRef.getSourceRef();
        QName assocTypeQName = nodeAssocRef.getTypeQName();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(sourceNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnDeleteAssociationPolicy policy = onDeleteAssociationDelegate.get(qnames, assocTypeQName);
        policy.onDeleteAssociation(nodeAssocRef);
    }

    /**
     * Get all aspect and node type qualified names
     * 
     * @param nodeRef
     *            the node we are interested in
     * @return Returns a set of qualified names containing the node type and all
     *         the node aspects
     */
    protected Set<QName> getTypeAndAspectQNames(NodeRef nodeRef)
    {
        Set<QName> aspectQNames = getAspects(nodeRef);
        QName typeQName = getType(nodeRef);
        // combine
        Set<QName> qnames = new HashSet<QName>(aspectQNames);
        qnames.add(typeQName);
        // done
        return qnames;
    }

    /**
     * Generates a GUID for the node using either the creation properties or just by
     * generating a value randomly.
     * 
     * @param preCreationProperties the properties that will be applied to the node
     * @return Returns the ID to create the node with
     */
    protected String generateGuid(Map<QName, Serializable> preCreationProperties)
    {
        String uuid = (String) preCreationProperties.get(ContentModel.PROP_NODE_UUID);
        if (uuid == null)
        {
            uuid = GUID.generate();
        }
        else
        {
            // remove the property as we don't want to persist it
            preCreationProperties.remove(ContentModel.PROP_NODE_UUID);
        }
        // done
        return uuid;
    }
    
    /**
     * Remove all properties used by the
     * {@link ContentModel#ASPECT_REFERENCABLE referencable aspect}.
     * <p>
     * This method can be used to ensure that the information already stored
     * by the node key is not duplicated by the properties.
     * 
     * @param properties properties to change
     */
    protected void removeReferencableProperties(Map<QName, Serializable> properties)
    {
        properties.remove(ContentModel.PROP_STORE_PROTOCOL);
        properties.remove(ContentModel.PROP_STORE_IDENTIFIER);
        properties.remove(ContentModel.PROP_NODE_UUID);
    }
    
    /**
     * Adds all properties used by the
     * {@link ContentModel#ASPECT_REFERENCABLE referencable aspect}.
     * <p>
     * This method can be used to ensure that the values used by the aspect
     * are present as node properties.
     * 
     * @param nodeRef the node reference containing the values required
     * @param properties the node properties
     */
    protected void addReferencableProperties(NodeRef nodeRef, Map<QName, Serializable> properties)
    {
        properties.put(ContentModel.PROP_STORE_PROTOCOL, nodeRef.getStoreRef().getProtocol());
        properties.put(ContentModel.PROP_STORE_IDENTIFIER, nodeRef.getStoreRef().getIdentifier());
        properties.put(ContentModel.PROP_NODE_UUID, nodeRef.getId());
    }
    
    /**
     * Defers to the pattern matching overload
     * 
     * @see RegexQNamePattern#MATCH_ALL
     * @see NodeService#getParentAssocs(NodeRef, QNamePattern)
     */
    public List<ChildAssociationRef> getParentAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return getParentAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
    }

    /**
     * Defers to the pattern matching overload
     * 
     * @see RegexQNamePattern#MATCH_ALL
     * @see NodeService#getChildAssocs(NodeRef, QNamePattern)
     */
    public final List<ChildAssociationRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return getChildAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
    }

    /**
     * Helper method to generate a descriptive exception
     * 
     * @see #checkAssoc(NodeRef, NodeRef, QName, QName)
     */
    private void throwDictionaryException(
            String msg,
            QName sourceTypeQName,
            QName targetTypeQName,
            QName assocTypeQName,
            QName assocQName) throws DictionaryException
    {
        throw new DictionaryException(msg + ": \n" +
                "   source node type: " + sourceTypeQName + "\n" +
                "   target node type: " + targetTypeQName + "\n" +
                "   assoc type qname: " + assocTypeQName + "\n" +
                "   assoc qname: " + assocQName);
    }
    
    /**
     * Helper method to convert the <code>Serializable</code> value into a full,
     * persistable {@link PropertyValue}.
     * <p>
     * Where the property definition is null, the value will take on the
     * {@link DataTypeDefinition#ANY generic ANY} value.
     * 
     * @param propertyDef the property dictionary definition, may be null
     * @param value the value, which will be converted according to the definition -
     *      may be null
     * @return Returns the persistable property value
     */
    protected PropertyValue makePropertyValue(PropertyDefinition propertyDef, Serializable value)
    {
        // get property attributes
        QName propertyTypeQName = null;
        if (propertyDef == null)                // property not recognised
        {
            // allow it for now - persisting excess properties can be useful sometimes
            propertyTypeQName = DataTypeDefinition.ANY;
        }
        else
        {
            propertyTypeQName = propertyDef.getDataType().getName();
            // check that multi-valued properties are allowed
            boolean isMultiValued = propertyDef.isMultiValued();
            if (isMultiValued && !(value instanceof Collection))
            {
                throw new DictionaryException("A multi-valued property must be a collection: \n" +
                        "   Property: " + propertyDef + "\n" +
                        "   Value: " + value);
            }
            else if (!isMultiValued && (value instanceof Collection))
            {
                throw new DictionaryException("A single-valued property may not be a collection: \n" +
                        "   Property: " + propertyDef + "\n" +
                        "   Value: " + value);
            }
        }
        PropertyValue propertyValue = new PropertyValue(propertyTypeQName, value);
        // done
        return propertyValue;
    }
    
    /**
     * Extracts the externally-visible property from the {@link PropertyValue propertyValue}.
     * 
     * @param propertyDef
     * @param propertyValue
     * @return Returns the value of the property in the format dictated by the property
     *      definition, or null if the property value is null 
     */
    protected Serializable makeSerializableValue(PropertyDefinition propertyDef, PropertyValue propertyValue)
    {
        if (propertyValue == null)
        {
            return null;
        }
        // get property attributes
        QName propertyTypeQName = null;
        boolean isMultiValued = false;
        if (propertyDef == null)
        {
            // allow this for now
            propertyTypeQName = DataTypeDefinition.ANY;
        }
        else
        {
            propertyTypeQName = propertyDef.getDataType().getName();
        }
        Serializable value = propertyValue.getValue(propertyTypeQName);
        // done
        return value;
    }
    
    /**
     * Checks that the type of the association is recognised and that the association may
     * be added to the given node.  If the association name is supplied, then the check
     * assumes that it is looking for a child association.
     * 
     * @param sourceNodeRef the source of the association
     * @param targetNodeRef the target of the association
     * @param assocTypeQName the type of the association
     * @param assocQName the name of the association - only required for child assocs
     * @throws DictionaryException thrown if any dictionary model violation occurs
     */
    protected final void checkAssoc(
            NodeRef sourceNodeRef,
            NodeRef targetNodeRef,
            QName assocTypeQName,
            QName assocQName) throws DictionaryException
    {
        QName sourceTypeQName = getType(sourceNodeRef);
        QName targetTypeQName = getType(targetNodeRef);
        
        AssociationDefinition assocDef = dictionaryService.getAssociation(assocTypeQName);
        if (assocDef == null)
        {
            throwDictionaryException("Association type not found: " + assocTypeQName,
                    sourceTypeQName, targetTypeQName, assocTypeQName, assocQName);
        }
        
        // check the association name
        QName assocRoleQName = assocDef.getTargetRoleName();
        if (assocRoleQName != null && assocQName != null)
        {
            if (!assocDef.isChild())
            {
                throw new IllegalArgumentException("Assoc name supplied for regular node assoc");
            }
            // the assoc defines a role name - check it
            RegexQNamePattern rolePattern = new RegexQNamePattern(assocRoleQName.toString());
            if (!rolePattern.isMatch(assocQName))
            {
                throwDictionaryException("Association role must match " + rolePattern,
                        sourceTypeQName, targetTypeQName, assocTypeQName, assocQName);
            }
        }
        
        // check the association source type
        ClassDefinition sourceDef = assocDef.getSourceClass();
        if (sourceDef instanceof TypeDefinition)
        {
            // the node type must be a match
            if (!dictionaryService.isSubClass(sourceTypeQName, sourceDef.getName()))
            {
                throwDictionaryException("Association source must be of type " + sourceDef.getName(),
                        sourceTypeQName, targetTypeQName, assocTypeQName, assocQName);
            }
        }
        else if (sourceDef instanceof AspectDefinition)
        {
            // the source must have a relevant aspect
            Set<QName> sourceAspects = getAspects(sourceNodeRef);
            boolean found = false;
            for (QName sourceAspectTypeQName : sourceAspects)
            {
                if (dictionaryService.isSubClass(sourceAspectTypeQName, sourceDef.getName()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                throwDictionaryException("Association source must have aspect of type " + sourceDef.getName(),
                        sourceTypeQName, targetTypeQName, assocTypeQName, assocQName);
            }
        }
        else
        {
            throwDictionaryException("Unknown ClassDefinition subclass: " + sourceDef,
                    sourceTypeQName, targetTypeQName, assocTypeQName, assocQName);
        }
        
        // check the association target type
        ClassDefinition targetDef = assocDef.getTargetClass();
        if (targetDef instanceof TypeDefinition)
        {
            // the node type must be a match
            if (!dictionaryService.isSubClass(targetTypeQName, targetDef.getName()))
            {
                throwDictionaryException("Association target must be of type " + targetDef.getName(),
                        sourceTypeQName, targetTypeQName, assocTypeQName, assocQName);
            }
        }
        else if (targetDef instanceof AspectDefinition)
        {
            // the target must have a relevant aspect
            Set<QName> targetAspects = getAspects(targetNodeRef);
            boolean found = false;
            for (QName targetAspectTypeQName : targetAspects)
            {
                if (dictionaryService.isSubClass(targetAspectTypeQName, targetDef.getName()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                throwDictionaryException("Association target must have aspect of type " + sourceDef.getName(),
                        sourceTypeQName, targetTypeQName, assocTypeQName, assocQName);
            }
        }
        else
        {
            throwDictionaryException("Unknown ClassDefinition subclass: " + targetDef,
                    sourceTypeQName, targetTypeQName, assocTypeQName, assocQName);
        }
    }

    @Deprecated
    public List<NodeRef> selectNodes(NodeRef contextNodeRef, String xpath, QueryParameterDefinition[] parameters, NamespacePrefixResolver namespacePrefixResolver, boolean followAllParentLinks) throws InvalidNodeRefException, XPathException
    {
        throw new UnsupportedOperationException("Methods moved to SearchService");
    }

    @Deprecated
    public List<Serializable> selectProperties(NodeRef contextNodeRef, String xpath, QueryParameterDefinition[] parameters, NamespacePrefixResolver namespacePrefixResolver, boolean followAllParentLinks) throws InvalidNodeRefException, XPathException
    {
        throw new UnsupportedOperationException("Methods moved to SearchService");
    }

    @Deprecated
    public boolean like(NodeRef nodeRef, QName propertyQName, String sqlLikePattern, boolean includeFTS) throws InvalidNodeRefException
    {
        throw new UnsupportedOperationException("Methods moved to SearchService");
    }

    @Deprecated
    public boolean contains(NodeRef nodeRef, QName propertyQName, String googleLikePattern) throws InvalidNodeRefException
    {
        throw new UnsupportedOperationException("Methods moved to SearchService");
    }
}
