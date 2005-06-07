/**
 * 
 */
package org.alfresco.repo.rule.impl.ruletype;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleType;

/**
 * @author Roy Wetherall
 */
public class InboundRuleTypeAdapter extends RuleTypeAdapterAbstractBase
{

    /**
     * @param policyComponent
     */
    public InboundRuleTypeAdapter(
            RuleType ruleType,
            PolicyComponent policyComponent,
            RuleService ruleService)
    {
        super(ruleType, policyComponent, ruleService);
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

    public void onCreateChildAssociation(ChildAssocRef childAssocRef)
    {
        executeRules(childAssocRef.getParentRef());
    }
}
