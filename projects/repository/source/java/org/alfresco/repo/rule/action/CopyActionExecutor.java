/**
 * 
 */
package org.alfresco.repo.rule.action;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;

/**
 * Copy action executor.
 * <p>
 * Copies the actioned upon node to a specified location.
 * 
 * @author Roy Wetherall
 */
public class CopyActionExecutor extends RuleActionExecutorAbstractBase
{
    public static final String NAME = "copy";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_ASSOC_TYPE_QNAME = "assoc-type";
    public static final String PARAM_ASSOC_QNAME = "assoc-name";
    public static final String PARAM_DEEP_COPY = "deep-copy";
    
    /**
     * Node operations service
     */
    private CopyService copyService;
    
	/**
	 * Constructor
	 * 
	 * @param ruleAction
	 * @param serviceRegistry
	 */
	public CopyActionExecutor(RuleAction ruleAction, ServiceRegistry serviceRegistry) 
	{
		super(ruleAction, serviceRegistry);		
		this.copyService = serviceRegistry.getCopyService();
	}

    /**
     * @see org.alfresco.repo.rule.RuleActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        NodeRef destinationParent = (NodeRef)this.ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
        QName destinationAssocTypeQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
        QName destinationAssocQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_QNAME);
        
        // TODO get this from a parameter value
        boolean deepCopy = false;
        
        this.copyService.copy(
                actionedUponNodeRef, 
                destinationParent,
                destinationAssocTypeQName,
                destinationAssocQName,
                deepCopy);
    }

}
