/*
 * Copyright 2005-2011 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.workflow.activiti;

import java.util.Date;
import java.util.Set;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.repository.DeploymentBuilderImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.explorer.ui.management.deployment.DefaultDeploymentFilter;
import org.activiti.explorer.ui.management.deployment.DeploymentFilterFactory;
import org.activiti.explorer.ui.management.deployment.DeploymentListQuery.DeploymentListitem;
import org.alfresco.repo.tenant.TenantService;

/**
 * ALF-15948: {@link DefaultDeploymentFilter} to make activiti-admin UI tenant-aware and
 * only show deployments of the current tenant, if available.
 *
 * @author Frederik Heremans
 */
public class MultiTenantDeploymentFilter extends
	DefaultDeploymentFilter {

	private static final String WILDCARD_SEARCH = "%";
	private TenantService tenantService;
	
	@Override
	public DeploymentQuery getQuery(RepositoryService repositoryService) {
		DeploymentQuery query =  super.getQuery(repositoryService);
		makeQueryMTAware(query);
		return query;
	}
	
	@Override
	public DeploymentQuery getCountQuery(RepositoryService repositoryService) {
		DeploymentQuery query =  super.getCountQuery(repositoryService);
		makeQueryMTAware(query);
		return query;
	}
	
	@Override
	public DeploymentListitem createItem(Deployment deployment) {
		return super.createItem(new MTDeploymentWrapper(deployment));
	}

	public void setTenantService(TenantService tenantService) {
		this.tenantService = tenantService;
	}
	
	public void setDeploymentFilterFactory(
			DeploymentFilterFactory deploymentFilterFactory) {
		// Register this component with the deployment factory
		deploymentFilterFactory.setDeploymentFilter(this);
	}
	
	@Override
	public void beforeDeploy(DeploymentBuilder deployment) {
		if(tenantService.isEnabled())
		{
			// ALF-16856: use tenant-specific deployment-name
			if(deployment instanceof DeploymentBuilderImpl) 
			{
				String baseDeploymentName = null;
				DeploymentBuilderImpl builderImpl = (DeploymentBuilderImpl) deployment;
				Set<String> resourceKeys = builderImpl.getDeployment().getResources().keySet();
				if(!resourceKeys.isEmpty()) 
				{
					// Use first resource as deployment-name
					baseDeploymentName = resourceKeys.iterator().next();					
				}
				else
				{
					// No resources, revert to deployment name
					baseDeploymentName = new Date().getTime() + "";
				}
				deployment.name(tenantService.getName(baseDeploymentName));
			}
		}
	}
	
	/**
	 * Add criteria to query to make it MT-aware.
	 * @param query
	 */
	protected void makeQueryMTAware(DeploymentQuery query)
	{
		if(tenantService.isEnabled())
		{
			String nameFilter = tenantService.getName(WILDCARD_SEARCH);
			// Ignore this filter if it's the system-tenant
			if(!nameFilter.equals(WILDCARD_SEARCH))
			{
				query.deploymentNameLike(nameFilter);
			}
		}
	}
	
	/**
	 * Wrapper around existing deployment to alter the name to remove the tenant-bit.
	 * 
	 * @author Frederik Heremans
	 *
	 */
	private class MTDeploymentWrapper implements Deployment 
	{
		private Deployment wrapped;
		private String safeName;
		
		public MTDeploymentWrapper(Deployment deployment)
		{
			wrapped = deployment;
			safeName = tenantService.getBaseName(deployment.getName());
		}
		
		@Override
		public String getId() {
			return wrapped.getId();
		}

		@Override
		public String getName() {
			return safeName;
		}

		@Override
		public Date getDeploymentTime() {
			return wrapped.getDeploymentTime();
		}

        @Override
        public String getCategory()
        {
            return wrapped.getCategory();
        }

        @Override
        public String getTenantId()
        {
            return wrapped.getTenantId();
        }
	}
}
