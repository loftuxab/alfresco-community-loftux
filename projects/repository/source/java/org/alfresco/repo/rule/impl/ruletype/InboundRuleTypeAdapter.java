/**
 * 
 */
package org.alfresco.repo.rule.impl.ruletype;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleType;

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
     * @param ruleType			the rule type
     * @param policyComponent	the policy component
     * @param ruleService		the rule service
     * @param nodeService		the node service
     */
    public InboundRuleTypeAdapter(
            RuleType ruleType,
            PolicyComponent policyComponent,
            RuleService ruleService,
            NodeService nodeService)
    {
        super(ruleType, policyComponent, ruleService, nodeService);
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
    }

	/**
	 * onCreateChildAssociation policy behaviour
	 * 
	 * @param childAssocRef		the child association reference created
	 */
    public void onCreateChildAssociation(ChildAssocRef childAssocRef)
    {
        executeRules(childAssocRef.getParentRef(), childAssocRef.getChildRef());
    }
}
