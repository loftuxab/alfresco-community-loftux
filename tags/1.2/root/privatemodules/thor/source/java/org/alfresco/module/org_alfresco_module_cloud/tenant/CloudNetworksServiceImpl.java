package org.alfresco.module.org_alfresco_module_cloud.tenant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService.AccountMembershipType;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountUsages;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.query.PageDetails;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.tenant.Network;
import org.alfresco.repo.tenant.NetworksService;
import org.alfresco.repo.tenant.Quota;
import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.util.Pair;

public class CloudNetworksServiceImpl implements NetworksService
{
    private TenantAdminService tenantAdminService;
    private DirectoryService directoryService;
    private AccountService accountService;

    public CloudNetworksServiceImpl()
    {
    }

	public void setTenantAdminService(TenantAdminService tenantAdminService)
	{
		this.tenantAdminService = tenantAdminService;
	}

	public void setDirectoryService(DirectoryService directoryService)
	{
		this.directoryService = directoryService;
	}

	public void setAccountService(AccountService accountService)
	{
		this.accountService = accountService;
	}
	
	private Network constructNetwork(String username, Account account)
	{
		Network network = null;

        if(account != null)
        {
            AccountMembershipType type = directoryService.getAccountMembershipType(username, account.getId());
			AccountUsages accountUsages = account.getUsageQuota();
	    	String subscriptionLevel = account.getType().getSubscriptionLevel().toString();
	    	boolean paidNetwork = account.getType().getAccountClass().isPaidNetwork();
        	boolean isHomeNetwork = type == AccountMembershipType.HomeNetwork;
        	Date createdAt = account.getCreationDate();
        	List<Quota> quotas = new ArrayList<Quota>();
	    	quotas.add(new Quota("fileUploadQuota", accountUsages.getFileUploadQuota(), null));
	    	quotas.add(new Quota("fileQuota", accountUsages.getFileQuota(), accountUsages.getFileUsage()));
	    	quotas.add(new Quota("siteCountQuota", accountUsages.getSiteCountQuota(), accountUsages.getSiteCountUsage()));
	    	quotas.add(new Quota("personCountQuota", accountUsages.getPersonCountQuota(), accountUsages.getPersonCountUsage()));
	    	quotas.add(new Quota("personInternalOnlyCountQuota", accountUsages.getPersonIntOnlyCountQuota(), accountUsages.getPersonIntOnlyCountUsage()));
	    	quotas.add(new Quota("personNetworkAdminCountQuota", accountUsages.getPersonNetworkAdminCountQuota(), accountUsages.getPersonNetworkAdminCountUsage()));
			network = new CloudNetwork(account, isHomeNetwork, createdAt, subscriptionLevel, paidNetwork, quotas);
        }

        return network;
	}
	
	private Network constructNetwork(String username, String networkId)
	{
    	Account account = accountService.getAccountByDomain(networkId);
		Network network = constructNetwork(username, account);
        return network;
	}

	/**
	 * Get a user's network memberships, sorted in ascending order by networkId
	 * 
	 * @param email if null, the currently authenticated user
	 */
	public PagingResults<Network> getNetworks(PagingRequest pagingRequest)
	{
		String username = AuthenticationUtil.getFullyAuthenticatedUser();

        // remap tenant admin to system admin
        String admin = tenantAdminService.getBaseNameUser(AuthenticationUtil.getAdminUserName());
        String user = tenantAdminService.getBaseNameUser(username);

        List<Network> networks = null;
        if (user.equalsIgnoreCase(admin))
        {
            // admin
        	networks = new ArrayList<Network>(1);
        	
        	String networkId = tenantAdminService.getUserDomain(username);
            Network network = constructNetwork(username, networkId);
            networks.add(network);
        }
        else
        {
            List<Long> accounts = directoryService.getAllAccounts(username);
        	networks = new ArrayList<Network>(accounts.size());
            
            // put default account first
            Long defaultAccount = directoryService.getDefaultAccount(username);
            String networkId = accountService.getAccountTenant(defaultAccount);
            networks.add(constructNetwork(username, networkId));
            
            for (Long accountId : accounts)
            {
                networkId = accountService.getAccountTenant(accountId);
                Network network = constructNetwork(username, networkId);
                if(!networks.contains(network))
                {
                    networks.add(network);
                }
            }

            Collections.sort(networks);
        }
        
        final int totalSize = networks.size();
        final PageDetails pageDetails = PageDetails.getPageDetails(pagingRequest, totalSize);

		final List<Network> page = new ArrayList<Network>(pageDetails.getPageSize());
		Iterator<Network> it = networks.iterator();
        for(int counter = 0; counter < pageDetails.getEnd() && it.hasNext(); counter++)
        {
        	Network network = it.next();
			if(counter < pageDetails.getSkipCount())
			{
				continue;
			}
			
			if(counter > pageDetails.getEnd() - 1)
			{
				break;
			}

			page.add(network);
        }

        return new PagingResults<Network>()
        {
			@Override
			public List<Network> getPage()
			{
				return page;
			}

			@Override
			public boolean hasMoreItems()
			{
				return pageDetails.hasMoreItems();
			}

			@Override
			public Pair<Integer, Integer> getTotalResultCount()
			{
				Integer total = Integer.valueOf(totalSize);
				return new Pair<Integer, Integer>(total, total);
			}

			@Override
			public String getQueryExecutionId()
			{
				return null;
			}
        };
	}

	@Override
	public Network getNetwork(String networkId)
	{
		Network network = null;

		String username = AuthenticationUtil.getFullyAuthenticatedUser();
		Account account = accountService.getAccountByDomain(networkId);
		if(account != null)
		{
			boolean isMember = directoryService.isMember(username, account.getId());
			if(isMember)
			{
				network = constructNetwork(username, account);
			}
			else
			{
				throw new AccessDeniedException("Cannot get network, no permission");
			}
		}

		return network;
	}

	@Override
    public String getUserDefaultNetwork(String user)
    {
		String defaultNetwork = null;

		Long defaultAccount = directoryService.getDefaultAccount(user);
		if(defaultAccount != null)
		{
			Account account = accountService.getAccount(defaultAccount);
			defaultNetwork = account.getTenantId();
		}

		return defaultNetwork;
    }
}
