/**
 * 
 */
package org.alfresco.repo.rule.impl.action;

import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationContext;

/**
 * Check out action executor
 * 
 * @author Roy Wetherall
 */
public class CheckOutActionExecutor extends RuleActionExecutorAbstractBase
{
    public static final String NAME = "check-out";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_ASSOC_TYPE_QNAME = "assoc-type";
    public static final String PARAM_ASSOC_QNAME = "assoc-name";

    /**
     * The version operations service
     */
    private CheckOutCheckInService cociService;
    
    /**
     * Constructor 
     * 
     * @param ruleAction
     * @param applicationContext
     */
    public CheckOutActionExecutor(RuleAction ruleAction,
            ApplicationContext applicationContext)
    {
        super(ruleAction, applicationContext);
        
        this.cociService = (CheckOutCheckInService)applicationContext.getBean("versionOperationsService");
    }

    /**
     * @see org.alfresco.repo.rule.RuleActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        // Get the destination details
        NodeRef destinationParent = (NodeRef)this.ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
        QName destinationAssocTypeQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
        QName destinationAssocQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_QNAME);
        
        if (destinationParent == null || destinationAssocTypeQName == null || destinationAssocQName == null)
        {
            // Check the node out to the current location
            this.cociService.checkout(actionedUponNodeRef);
        }
        else
        {
            // Check the node out to the specified location
            this.cociService.checkout(
                    actionedUponNodeRef, 
                    destinationParent, 
                    destinationAssocTypeQName, 
                    destinationAssocQName);
        }
    }

}
