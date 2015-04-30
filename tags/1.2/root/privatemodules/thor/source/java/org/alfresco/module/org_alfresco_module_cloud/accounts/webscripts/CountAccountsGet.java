package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class CountAccountsGet extends DeclarativeWebScript
{
	private AccountService accountService;

	public void setAccountService(AccountService accountService)
	{
		this.accountService = accountService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
	{
		long numberOfAccounts = accountService.getNumberOfAccounts();

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("numberOfAccounts", Long.valueOf(numberOfAccounts));
		return model;
	}
}
