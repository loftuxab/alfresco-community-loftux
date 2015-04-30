/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.explorer.ui.process.DefaultProcessDefinitionFilter;
import org.activiti.explorer.ui.process.ProcessDefinitionFilterFactory;
import org.alfresco.repo.tenant.TenantService;

/**
 * ALF-15948: {@link DefaultProcessDefinitionFilter} to make activiti-admin UI tenant-aware and
 * only show process-definitions of the current tenant, if available.
 *
 * @author Frederik Heremans
 */
public class MultiTenantProcessDefinitionFilter extends
		DefaultProcessDefinitionFilter {

	private static final String WILDCARD_SEARCH = "%";
	private TenantService tenantService;
	
	@Override
	public ProcessDefinitionQuery getQuery(RepositoryService repositoryService) {
		ProcessDefinitionQuery query =  super.getQuery(repositoryService);
		makeQueryMTAware(query);
		return query;
	}
	
	@Override
	public ProcessDefinitionQuery getCountQuery(RepositoryService repositoryService) {
		ProcessDefinitionQuery query =  super.getCountQuery(repositoryService);
		makeQueryMTAware(query);
		return query;
	}

	public void setTenantService(TenantService tenantService) {
		this.tenantService = tenantService;
	}
	
	public void setProcessDefinitionFilterFactory(
			ProcessDefinitionFilterFactory processDefinitionFilterFactory) {
		// Register this component with the process-definition factory
		processDefinitionFilterFactory.setProcessDefinitionFilter(this);
	}
	
	/**
	 * Add criteria to query to make it MT-aware.
	 * @param query
	 */
	protected void makeQueryMTAware(ProcessDefinitionQuery query)
	{
		if(tenantService.isEnabled())
		{
			String nameFilter = tenantService.getName(WILDCARD_SEARCH);
			// Ignore this filter if it's the system-tenant
			if(!nameFilter.equals(WILDCARD_SEARCH))
			{
				query.processDefinitionKeyLike(nameFilter);
			}
		}
	}
}
