package org.alfresco.sample.contentHits;

import java.util.Date;

import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * This class contains the behaviour behind the 'my:contentHits' aspect.
 * 
 * @author Roy Wetherall
 */
public class ContentHitsAspect implements ContentServicePolicies.OnContentReadPolicy,
                                          ContentServicePolicies.OnContentUpdatePolicy,
                                          NodeServicePolicies.OnAddAspectPolicy
{
    /** Aspect name */
    public static final QName ASPECT_CONTENT_HITS = QName.createQName("extension.contenthits", "contentHits");
    
    /** Property names */
    public static final QName PROP_COUNT_STARTED_DATE = QName.createQName("extension.contenthits", "countStartedDate");
    public static final QName PROP_UPDATE_COUNT = QName.createQName("extension.contenthits", "updateCount");
    public static final QName PROP_READ_COUNT = QName.createQName("extension.contenthits", "readCount");
    
    /** The policy component */
    private PolicyComponent policyComponent;
    
    /** The node service */
    private NodeService nodeService;
    
    /**
     * Sets the policy component
     * 
     * @param policyComponent   the policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /** 
     * Sets the node service 
     * 
     * @param nodeService   the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Spring initilaise method used to register the policy behaviours
     */
    public void initialise()
    {
        // Register the policy behaviours
        this.policyComponent.bindClassBehaviour(
                                 QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"),
                                 ASPECT_CONTENT_HITS,
                                 new JavaBehaviour(this, "onAddAspect"));
        this.policyComponent.bindClassBehaviour(
                                 ContentServicePolicies.ON_CONTENT_READ,
                                 ASPECT_CONTENT_HITS,
                                 new JavaBehaviour(this, "onContentRead"));
        this.policyComponent.bindClassBehaviour(
                                 ContentServicePolicies.ON_CONTENT_UPDATE,
                                 ASPECT_CONTENT_HITS,
                                 new JavaBehaviour(this, "onContentUpdate"));
    }

    /**
     * onAddAspect policy behaviour.
     * 
     * Sets the count started date to the date/time at which the contentHits aspect was
     * first applied.
     * 
     * @param nodeRef           the node reference
     * @param aspectTypeQName   the qname of the aspect being applied
     */
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (aspectTypeQName.equals(ASPECT_CONTENT_HITS) == true)
        {
            // Set the count started date
            this.nodeService.setProperty(nodeRef, PROP_COUNT_STARTED_DATE, new Date());
        }
    }
    
    /**
     * onContentRead policy behaviour.
     * 
     * Increments the aspect's read count property by one.
     * 
     * @see org.alfresco.repo.content.ContentServicePolicies.OnContentReadPolicy#onContentRead(org.alfresco.service.cmr.repository.NodeRef)
     */
    public void onContentRead(NodeRef nodeRef)
    {
        // Increment the read count property value
        Integer currentValue = (Integer)this.nodeService.getProperty(nodeRef, PROP_READ_COUNT);
        int newValue = currentValue.intValue() + 1;
        this.nodeService.setProperty(nodeRef, PROP_READ_COUNT, newValue);
    }

    /**
     * onContentUpdate policy behaviour.
     * 
     * Increments the aspect's update count property by one.
     * 
     * @see org.alfresco.repo.content.ContentServicePolicies.OnContentUpdatePolicy#onContentUpdate(org.alfresco.service.cmr.repository.NodeRef, boolean)
     */
    public void onContentUpdate(NodeRef nodeRef, boolean newContent)
    {
        // Increment the update count property value
        Integer currentValue = (Integer)this.nodeService.getProperty(nodeRef, PROP_UPDATE_COUNT);
        int newValue = currentValue.intValue() + 1;
        this.nodeService.setProperty(nodeRef, PROP_UPDATE_COUNT, newValue);        
    }    
}
