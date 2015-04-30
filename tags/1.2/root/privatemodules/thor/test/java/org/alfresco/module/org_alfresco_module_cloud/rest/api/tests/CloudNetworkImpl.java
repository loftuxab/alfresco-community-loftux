package org.alfresco.module.org_alfresco_module_cloud.rest.api.tests;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountUsages;
import org.alfresco.rest.api.tests.PublicApiDateFormat;
import org.alfresco.rest.api.tests.client.data.NetworkImpl;
import org.alfresco.rest.api.tests.client.data.Quota;

public class CloudNetworkImpl extends NetworkImpl
{
	private static final long serialVersionUID = 699095390345859056L;

	public CloudNetworkImpl(Account account)
	{
		super(account.getName(), account.isEnabled());
		AccountUsages accountUsages = account.getUsageQuota();
    	this.createdAt = (account.getCreationDate() != null ? PublicApiDateFormat.getDateFormat().format(account.getCreationDate()) : null);
    	if(accountUsages != null)
    	{
	    	this.quotas.add(new Quota("fileUploadQuota", accountUsages.getFileUploadQuota(), null));
	    	this.quotas.add(new Quota("fileQuota", accountUsages.getFileQuota(), accountUsages.getFileUsage()));
	    	this.quotas.add(new Quota("siteCountQuota", accountUsages.getSiteCountQuota(), accountUsages.getSiteCountUsage()));
	    	this.quotas.add(new Quota("personCountQuota", accountUsages.getPersonCountQuota(), accountUsages.getPersonCountUsage()));
	    	this.quotas.add(new Quota("personInternalOnlyCountQuota", accountUsages.getPersonIntOnlyCountQuota(), accountUsages.getPersonIntOnlyCountUsage()));
	    	this.quotas.add(new Quota("personNetworkAdminCountQuota", accountUsages.getPersonNetworkAdminCountQuota(), accountUsages.getPersonNetworkAdminCountUsage()));
    	}
    	this.subscriptionLevel = account.getType().getSubscriptionLevel().toString();
    	this.paidNetwork = account.getType().getAccountClass().isPaidNetwork();
	}
}
