/**
 * 
 */
package org.alfresco.repo.rule.impl.ruletype;

import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationContext;

/**
 * Outbound rule type adapter class.
 * 
 * @author Roy Wetherall
 */
public class OutboundRuleTypeAdapter extends RuleTypeAdapterAbstractBase
{
    /**
     * Constructor
     * 
     * @param ruleType			   the rule type
     * @param applicationContext   the application context
     */
    public OutboundRuleTypeAdapter(
            RuleType ruleType,
            RuleService ruleService,
            NodeService nodeService,
            ApplicationContext applicationContext)
    {
        super(ruleType, ruleService, nodeService, applicationContext);
    }

    /**
     * @see org.alfresco.repo.rule.RuleTypeAdapter#registerPolicyBehaviour()
     */
    public void registerPolicyBehaviour()
    {
        policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteChildAssociation"),
                this,
                new JavaBehaviour(this, "onDeleteChildAssociation"));
    }

	/**
	 * onDeleteChildAssociation policy behaviour
	 * 
	 * @param childAssocRef		the child association reference deleted
	 */
    public void onDeleteChildAssociation(ChildAssociationRef childAssocRef)
    {
        executeRules(childAssocRef.getParentRef(), childAssocRef.getChildRef());
    }
}
