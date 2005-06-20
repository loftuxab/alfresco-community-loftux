package org.alfresco.repo.audit;

import java.util.Date;

import net.sf.acegisecurity.Authentication;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.authentication.RepositoryUser;
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

    // Unknown user, for when authentication has not occured
    private static final String USERNAME_UNKNOWN = "unknown";
    
    // Dependencies
    private NodeService nodeService;
    private AuthenticationService authenticationService;
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
     * @param authenticationService  the authentication service
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService; 
    }

    
    /**
     * Initialise the Auditable Aspect
     */
    public void init()
    {
        // Bind auditable behaviour to node policies
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                ContentModel.ASPECT_AUDITABLE,
                new JavaBehaviour(this, "onCreateAudit"));
        
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"),
                ContentModel.ASPECT_AUDITABLE,
                new JavaBehaviour(this, "onAddAudit"));

        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateNode"),
                ContentModel.ASPECT_AUDITABLE,
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
        
        setCreatedProperties(childAssocRef.getChildRef());
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
        
        setCreatedProperties(nodeRef);
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

        setUpdatedProperties(nodeRef);
    }


    /**
     * Populates the "created" set of properties
     *  
     * @param nodeRef
     */
    private void setCreatedProperties(NodeRef nodeRef)
    {
        // Set created date
        Date now = new Date(System.currentTimeMillis());
        nodeService.setProperty(nodeRef, ContentModel.PROP_CREATED, now);

        // Set creator
        nodeService.setProperty(nodeRef, ContentModel.PROP_CREATOR, getUsername());
    }


    /**
     * Populates the "updated" set of properties
     * 
     * @param nodeRef
     */
    private void setUpdatedProperties(NodeRef nodeRef)
    {
        // Set updated date
        Date now = new Date(System.currentTimeMillis());
        nodeService.setProperty(nodeRef, ContentModel.PROP_MODIFIED, now);

        // Set modifier
        nodeService.setProperty(nodeRef, ContentModel.PROP_MODIFIER, getUsername());
    }
    
    
    /**
     * @return  the current username (or unknown, if unknown)
     */
    private String getUsername()
    {
        Authentication auth = authenticationService.getCurrentAuthentication();
        if (auth != null)
        {
            RepositoryUser user = (RepositoryUser)auth.getPrincipal();
            if (user != null)
            {
                return user.getUsername();
            }
        }
        return USERNAME_UNKNOWN;
    }
    
}
