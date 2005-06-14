/**
 * 
 */
package org.alfresco.repo.rule.action;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationContext;

/**
 * Copy action executor.
 * <p>
 * Copies the actioned upon node to a specified location.
 * 
 * @author Roy Wetherall
 */
public class MoveActionExecutor extends RuleActionExecutorAbstractBase
{
    public static final String NAME = "move";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_ASSOC_TYPE_QNAME = "assoc-type";
    public static final String PARAM_ASSOC_QNAME = "assoc-name";
    
    /**
     * Node service
     */
    private NodeService nodeService;
        
    /**
     * Constructor 
     * 
     * @param ruleAction            the rule action
     * @param applicationContext    the application context
     */
    public MoveActionExecutor(RuleAction ruleAction, ApplicationContext applicationContext)
    {        
        super(ruleAction, applicationContext);
        
        this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
    }

    /**
     * @see org.alfresco.repo.rule.RuleActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        NodeRef destinationParent = (NodeRef)this.ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
        QName destinationAssocTypeQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
        QName destinationAssocQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_QNAME);
        
        this.nodeService.moveNode(
                actionedUponNodeRef,
                destinationParent,
                destinationAssocTypeQName,
                destinationAssocQName);
    }

}
