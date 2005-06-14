package org.alfresco.repo.audit;

import java.util.Date;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This aspect maintains the audit properties of the Auditable aspect.
 *  
 * @author David Caruana
 */
public class AuditableAspect
{
    // Logger
    private static final Log logger = LogFactory.getLog(AuditableAspect.class);

    // Node Service
    private NodeService nodeService;
    
    // Policy Component
    private PolicyComponent policyComponent;


    /**
     * @param nodeService  the node service to use for audit property maintenance
     */    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }


    /**
     * @param policyComponent  the policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    
    /**
     * Initialise the Auditable Aspect
     */
    public void init()
    {
        // Bind auditable behaviour to node policies
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                DictionaryBootstrap.ASPECT_QNAME_AUDITABLE,
                new JavaBehaviour(this, "onCreateAudit"));
        
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"),
                DictionaryBootstrap.ASPECT_QNAME_AUDITABLE,
                new JavaBehaviour(this, "onAddAudit"));

        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateNode"),
                DictionaryBootstrap.ASPECT_QNAME_AUDITABLE,
                new JavaBehaviour(this, "onUpdateAudit"));
    }
    

    /**
     * Maintain audit properties on creation of Node
     * 
     * @param childAssocRef  the association to the child created
     */
    public void onCreateAudit(ChildAssociationRef childAssocRef)
    {
        if (logger.isDebugEnabled())
            logger.debug("AuditableAspect: setting create audit properties for created node " + childAssocRef.toString());
        
        // Get the node to perform auditing on
        NodeRef nodeRef = childAssocRef.getChildRef();

        // Set created date
        Date now = new Date(System.currentTimeMillis());
        nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_CREATED, now);
        
        // TODO: Set created by
    }


    /**
     * Maintain audit properties on addition of audit aspect to a node
     * 
     * @param nodeRef  the node to which auditing has been added 
     * @param aspect  the aspect added
     */
    public void onAddAudit(NodeRef nodeRef, QName aspect)
    {
        if (logger.isDebugEnabled())
            logger.debug("AuditableAspect: setting create audit properties for introduced audit aspect on node " + nodeRef.toString());
        
        // Set created date
        Date now = new Date(System.currentTimeMillis());
        nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_CREATED, now);
        
        // TODO: Set created by
    }
    

    /**
     * Maintain audit properties on update of node
     * 
     * @param nodeRef  the updated node
     */
    public void onUpdateAudit(NodeRef nodeRef)
    {
        if (logger.isDebugEnabled())
            logger.debug("AuditableAspect: setting update audit properties for updated node " + nodeRef.toString());

        // Set updated date
        Date now = new Date(System.currentTimeMillis());
        nodeService.setProperty(nodeRef, DictionaryBootstrap.PROP_QNAME_MODIFIED, now);
        
        // TODO: Set updated by
    }

}
