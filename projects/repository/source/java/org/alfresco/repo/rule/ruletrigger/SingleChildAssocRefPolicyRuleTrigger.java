package org.alfresco.repo.rule.ruletrigger;

import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

public class SingleChildAssocRefPolicyRuleTrigger extends RuleTriggerAbstractBase
{
	private static final String ERR_POLICY_NAME_NOT_SET = "Unable to register rule trigger since policy name has not been set.";
	
	private String policyNamespace = NamespaceService.ALFRESCO_URI;
	
	private String policyName;
	
	private boolean isClassBehaviour = false;
	
	public void setPolicyNamespace(String policyNamespace)
	{
		this.policyNamespace = policyNamespace;
	}
	
	public void setPolicyName(String policyName)
	{
		this.policyName = policyName;
	}
	
	public void setIsClassBehaviour(boolean isClassBehaviour)
	{
		this.isClassBehaviour = isClassBehaviour;
	}
	
	/**
	 * @see org.alfresco.repo.rule.ruletrigger.RuleTrigger#registerRuleTrigger()
	 */
	public void registerRuleTrigger()
	{
		if (policyName == null)
		{
			throw new RuleServiceException(ERR_POLICY_NAME_NOT_SET);
		}
		
		if (isClassBehaviour == true)
		{
			this.policyComponent.bindClassBehaviour(
					QName.createQName(this.policyNamespace, this.policyName), 
					this, 
					new JavaBehaviour(this, "policyBehaviour"));
		}
		else
		{
			this.policyComponent.bindAssociationBehaviour(
					QName.createQName(this.policyNamespace, this.policyName), 
					this, 
					new JavaBehaviour(this, "policyBehaviour"));
		}
	}

	public void policyBehaviour(ChildAssociationRef childAssocRef)
	{
		triggerRules(childAssocRef.getParentRef(), childAssocRef.getChildRef());
	}
}
