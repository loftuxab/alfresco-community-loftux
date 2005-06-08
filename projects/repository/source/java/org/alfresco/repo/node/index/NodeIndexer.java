package org.alfresco.repo.node.index;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.Indexer;

/**
 * Handles the node policy callbacks to ensure that the node hierarchy is properly
 * indexed.
 * 
 * @author Derek Hulley
 */
public class NodeIndexer
        implements NodeServicePolicies.OnCreateStorePolicy,
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
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateStore"),
                DictionaryBootstrap.TYPE_QNAME_BASE,
                new JavaBehaviour(this, "onCreateStore"));   
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                DictionaryBootstrap.TYPE_QNAME_BASE,
                new JavaBehaviour(this, "onCreateNode"));   
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateNode"),
                DictionaryBootstrap.TYPE_QNAME_BASE,
                new JavaBehaviour(this, "onUpdateNode"));   
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onNode"),
                DictionaryBootstrap.TYPE_QNAME_BASE,
                new JavaBehaviour(this, "onUpdateNode"));   
        policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                DictionaryBootstrap.TYPE_QNAME_FOLDER,
                DictionaryBootstrap.ASSOC_QNAME_CONTAINS,
                new JavaBehaviour(this, "onCreateChildAssociation"));   
        policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                DictionaryBootstrap.TYPE_QNAME_FOLDER,
                DictionaryBootstrap.ASSOC_QNAME_CONTAINS,
                new JavaBehaviour(this, "onCreateChildAssociation"));   
    }

    public void onCreateStore(NodeRef rootNodeRef)
    {
        ChildAssocRef assocRef = new ChildAssocRef(null, null, null, rootNodeRef);
        indexer.createNode(assocRef);
    }

    public void onCreateNode(ChildAssocRef childAssocRef)
    {
        indexer.createNode(childAssocRef);
    }

    public void onUpdateNode(NodeRef nodeRef)
    {
        indexer.updateNode(nodeRef);
    }

    public void onDeleteNode(ChildAssocRef childAssocRef)
    {
        indexer.deleteNode(childAssocRef);
    }

    public void onCreateChildAssociation(ChildAssocRef childAssocRef)
    {
        indexer.createChildRelationship(childAssocRef);
    }

    public void onDeleteChildAssociation(ChildAssocRef childAssocRef)
    {
        indexer.deleteChildRelationship(childAssocRef);
    }
}
