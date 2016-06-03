package org.alfresco.repo.tenant;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Tenant
 *
 */
@AlfrescoPublicApi
public class Tenant
{
    private String tenantDomain;
    
    private boolean enabled = false;
    
    private String rootContentStoreDir = null; // if configured - can be null

    // from Thor - unused
    private String dbUrl = null;

    
    public Tenant(String tenantDomain, boolean enabled, String rootContentStoreDir, String dbUrl)
    {
        this.tenantDomain = tenantDomain;
        this.enabled = enabled;
        this.rootContentStoreDir = rootContentStoreDir;
        this.dbUrl = dbUrl;
    }

    public String getTenantDomain()
    {
        return tenantDomain;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
    
    public String getRootContentStoreDir()
    {
        return rootContentStoreDir;
    }
    
    public String getDbUrl()
    {
        return dbUrl;
    }

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tenantDomain == null) ? 0 : tenantDomain.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tenant other = (Tenant) obj;
		if (tenantDomain == null) {
			if (other.tenantDomain != null)
				return false;
		} else if (!tenantDomain.equals(other.tenantDomain))
			return false;
		return true;
	}
}
