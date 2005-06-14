/**
 * 
 */
package org.alfresco.repo.rule.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.version.Version;
import org.springframework.context.ApplicationContext;

/**
 * Check in action executor
 * 
 * @author Roy Wetherall
 */
public class CheckInActionExecutor extends RuleActionExecutorAbstractBase
{
    public static final String NAME = "check-in";
    public static final String PARAM_DESCRIPTION = "description";
    
    /**
     * The node service
     */
    private NodeService nodeService;
    
    /**
     * The coci service
     */
    private CheckOutCheckInService cociService;
    
    /**
     * Constructor 
     * 
     * @param ruleAction
     * @param applicationContext
     */
    public CheckInActionExecutor(
            RuleAction ruleAction,
            ApplicationContext applicationContext)
    {
        super(ruleAction, applicationContext);
        
        this.nodeService = (NodeService)applicationContext.getBean("nodeService");
        this.cociService = (CheckOutCheckInService)applicationContext.getBean("versionOperationsService");
    }

    /**
     * @see org.alfresco.repo.rule.RuleActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        // First ensure that the actionedUponNodeRef is a workingCopy
        if (this.nodeService.hasAspect(actionedUponNodeRef, DictionaryBootstrap.ASPECT_QNAME_WORKING_COPY) == true)
        {
            // Get the version description
            String description = (String)this.ruleAction.getParameterValue(PARAM_DESCRIPTION);
            Map<String, Serializable> versionProperties = new HashMap<String, Serializable>(1);
            versionProperties.put(Version.PROP_DESCRIPTION, description);
            
            // TODO determine whether the document should be kept checked out
            
            // Check the node in
            this.cociService.checkin(actionedUponNodeRef, versionProperties);
        }
    }

}
