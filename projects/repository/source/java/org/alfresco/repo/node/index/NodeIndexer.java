package org.alfresco.repo.node.index;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.search.Indexer;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.debug.CodeMonkey;

/**
 * Handles the node policy callbacks to ensure that the node hierarchy is properly
 * indexed.
 * 
 * @author Derek Hulley
 */
public class NodeIndexer
        implements NodeServicePolicies.BeforeCreateStorePolicy,
                   NodeServicePolicies.OnCreateNodePolicy,
                   NodeServicePolicies.OnUpdateNodePolicy,
                   NodeServicePolicies.OnDeleteNodePolicy,
                   NodeServicePolicies.OnCreateChildAssociationPolicy,
                   NodeServicePolicies.OnDeleteChildAssociationPolicy
{
    /** the component to register the behaviour with */
    private PolicyComponent policyComponent;
    /** the component to index the node hierarchy */
    private Indexer indexer;
    
    /**
     * @param indexer the indexer that will be index
     */
    public NodeIndexer(PolicyComponent policyComponent, Indexer indexer)
    {
        this.policyComponent = policyComponent;
        this.indexer = indexer;
        
        // initialise
        init();
    }
    
    /**
     * Registers the policy behaviour methods
     */
    private void init()
    {
        CodeMonkey.issue("How can we be sure that the behaviour isn't hijacked?");  // TODO: Behaviour hijacking
        
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "beforeCreateStore"),
                DictionaryBootstrap.TYPE_QNAME_STOREROOT,
                new JavaBehaviour(this, "beforeCreateStore"));   
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                this,
                new JavaBehaviour(this, "onCreateNode"));   
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateNode"),
                this,
                new JavaBehaviour(this, "onUpdateNode"));   
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteNode"),
                this,
                new JavaBehaviour(this, "onDeleteNode"));   
        policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                this,
                new JavaBehaviour(this, "onCreateChildAssociation"));   
        policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteChildAssociation"),
                this,
                new JavaBehaviour(this, "onDeleteChildAssociation"));   
    }

    public void beforeCreateStore(QName nodeTypeQName, StoreRef storeRef)
    {
        // indexer can perform some cleanup here, if required
    }

    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        indexer.createNode(childAssocRef);
    }

    public void onUpdateNode(NodeRef nodeRef)
    {
        indexer.updateNode(nodeRef);
    }

    public void onDeleteNode(ChildAssociationRef childAssocRef)
    {
        indexer.deleteNode(childAssocRef);
    }

    public void onCreateChildAssociation(ChildAssociationRef childAssocRef)
    {
        indexer.createChildRelationship(childAssocRef);
    }

    public void onDeleteChildAssociation(ChildAssociationRef childAssocRef)
    {
        indexer.deleteChildRelationship(childAssocRef);
    }
}
