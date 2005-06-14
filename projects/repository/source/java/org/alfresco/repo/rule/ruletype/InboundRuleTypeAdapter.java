/**
 * 
 */
package org.alfresco.repo.rule.ruletype;

import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Inbound rule type adapter class.
 * 
 * @author Roy Wetherall
 */
public class InboundRuleTypeAdapter extends RuleTypeAdapterAbstractBase
{
	/**
	 * Constructor
	 *  
	 * @param ruleType
	 * @param policyComponent
	 * @param serviceRegistry
	 */
    public InboundRuleTypeAdapter(RuleType ruleType, RuleService ruleService, PolicyComponent policyComponent, ServiceRegistry serviceRegistry) 
	{
		super(ruleType, ruleService, policyComponent, serviceRegistry);
	}

	/**
     * @see org.alfresco.repo.rule.RuleTypeAdapter#registerPolicyBehaviour()
     */
    public void registerPolicyBehaviour()
    {
        policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                this,
                new JavaBehaviour(this, "onCreateChildAssociation"));
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                this,
                new JavaBehaviour(this, "onCreateChildAssociation"));
    }

	/**
	 * onCreateChildAssociation policy behaviour
	 * 
	 * @param childAssocRef		the child association reference created
	 */
    public void onCreateChildAssociation(ChildAssociationRef childAssocRef)
    {
        executeRules(childAssocRef.getParentRef(), childAssocRef.getChildRef());
    }
}
