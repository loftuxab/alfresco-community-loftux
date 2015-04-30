package org.alfresco.module.org_alfresco_module_cloud.tenant;

import java.util.Date;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.repo.tenant.Network;
import org.alfresco.repo.tenant.Quota;

public class CloudNetwork extends Network
{
	public CloudNetwork(Account account, Boolean isHomeNetwork, Date createdAt, String subscriptionLevel, Boolean paidNetwork, List<Quota> quotas)
	{
		super(account.getTenantId(), account.isEnabled(), null, null);
		this.subscriptionLevel = subscriptionLevel;
		this.paidNetwork = paidNetwork;
		this.quotas = quotas;
		this.isHomeNetwork = isHomeNetwork;
		this.createdAt = createdAt;
	}
}
