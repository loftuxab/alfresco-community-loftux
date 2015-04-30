/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.accounts;

import org.alfresco.module.org_alfresco_module_cloud.accounts.exceptions.AccountServiceException;
import org.alfresco.module.org_alfresco_module_cloud.usage.TenantQuotaService;
import org.alfresco.util.ParameterCheck;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.surf.util.I18NUtil;


/**
 * Stores a type of an account with default quotas => refer to "account-types-context.xml"
 *
 * @author David Gildeh
 * @author Neil Mc Erlean
 * @author janv
 * 
 * @since Cloud
 */
public class AccountType implements InitializingBean
{
    public static final int PUBLIC_DOMAIN_ACCOUNT_TYPE      = -1;
    public static final int FREE_NETWORK_ACCOUNT_TYPE       = 0;
    public static final int STANDARD_NETWORK_ACCOUNT_TYPE   = 100;
    public static final int PARTNER_NETWORK_ACCOUNT_TYPE    = 101;
    public static final int ENTERPRISE_NETWORK_ACCOUNT_TYPE = 1000;
    public static final int TRIAL_NETWORK_ACCOUNT_TYPE      = 1001;

    public static enum SubscriptionLevel
    {
    	Free, Standard, Enterprise;
    };


    private AccountRegistry accountRegistry;
    
    // Default quotas - used to set initial quota when creating account of this type (0 = no quota, -1 = unlimited)

    private long fileUploadQuota              = TenantQuotaService.QUOTA_ZERO;      // quota in bytes
    private long fileQuota                    = TenantQuotaService.QUOTA_ZERO;      // quota in bytes
    private long siteCountQuota               = TenantQuotaService.QUOTA_UNLIMITED; // quota for num of sites (all types - ie. private & public)
    private long personCountQuota             = TenantQuotaService.QUOTA_UNLIMITED; // quota for total num of people (internal and external)
    private long personIntOnlyCountQuota      = TenantQuotaService.QUOTA_UNLIMITED; // quota for num of internal people only (not counting external)
    private long personNetworkAdminCountQuota = TenantQuotaService.QUOTA_ZERO;      //  quota for num of network admins
    
    /** Account Type ID **/
    private int id = 0;
    
    /** Account class */
    private AccountClass accountClass;
    
    public SubscriptionLevel subscriptionLevel;

    /**
     * Default Constructor
     */
    public AccountType()
    {
        // Intentionally empty
    }

    public void setAccountRegistry(AccountRegistry accountRegistry)
    {
        this.accountRegistry = accountRegistry;
    }
    
    public void init()
    {
        accountRegistry.register(this);
    }
    
    public void setSubscriptionLevel(String subscriptionLevel)
    {
		this.subscriptionLevel = SubscriptionLevel.valueOf(subscriptionLevel);
	}
    
	public String getSubscriptionLevel()
	{
		return subscriptionLevel.toString();
	}

	/**
     * Set the account file upload quota
     *
     * @param fileQuota   The file upload quota
     */
    public void setFileUploadQuota(long fileUploadQuota)
    {
        this.fileUploadQuota = fileUploadQuota;
    }

    /**
     * Get the account file upload quota
     *
     * @return  The account file upload quota
     */
    public long getFileUploadQuota()
    {
        return fileUploadQuota;
    }
    
    /**
     * Set the account file quota
     *
     * @param fileQuota   The file quota
     */
    public void setFileQuota(long fileQuota)
    {
        this.fileQuota = fileQuota;
    }

    /**
     * Get the account file quota
     *
     * @return  The account file quota
     */
    public long getFileQuota()
    {
        return fileQuota;
    }
    
    /**
     * Set the account site count quota
     *
     * @param siteCountQuota   The site count quota
     */
    public void setSiteCountQuota(long siteCountQuota)
    {
        this.siteCountQuota = siteCountQuota;
    }

    /**
     * Get the account site count quota
     *
     * @return  The account site count quota
     */
    public long getSiteCountQuota()
    {
        return siteCountQuota;
    }
    
    /**
     * Set the account person count quota
     *
     * @param personCountQuota   The person count quota
     */
    public void setPersonCountQuota(long personCountQuota)
    {
        this.personCountQuota = personCountQuota;
    }

    /**
     * Get the account person count quota
     *
     * @return  The account person count quota
     */
    public long getPersonCountQuota()
    {
        return personCountQuota;
    }
    
    /**
     * Set the account person (internal) count quota
     *
     * @param personCountQuota   The person (internal) count quota
     */
    public void setPersonIntOnlyCountQuota(long personIntOnlyCountQuota)
    {
        this.personIntOnlyCountQuota = personIntOnlyCountQuota;
    }

    /**
     * Get the account person (internal) count quota
     *
     * @return  The account person (internal) count quota
     */
    public long getPersonIntOnlyCountQuota()
    {
        return personIntOnlyCountQuota;
    }
    
    /**
     * Set the account network admin count quota
     *
     * @param networkAdminCountQuota   The network admin count quota
     */
    public void setPersonNetworkAdminCountQuota(long personNetworkAdminCountQuota)
    {
        this.personNetworkAdminCountQuota = personNetworkAdminCountQuota;
    }

    /**
     * Get the account network admin count quota
     *
     * @return  The account network admin count quota
     */
    public long getPersonNetworkAdminCountQuota()
    {
        return personNetworkAdminCountQuota;
    }
    

    public void setAccountClass(AccountClass accountClass)
    {
        this.accountClass = accountClass;
    }
    
    public AccountClass getAccountClass()
    {
        return this.accountClass;
    }
    
    /**
     * Set the account type ID
     *
     * @param accountTypeId   The accountTypeId (int code)
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Get the account name
     *
     * @return  the account name
     */
    public String getName()
    {
        String l10nKey = "account.type." + id + ".display-name";
        String name = I18NUtil.getMessage(l10nKey);
        return name == null ? Integer.toString(this.id) : name;
    }
    
    /**
     * Get the account display-name, which is "$name ($id)" e.g. "Free (0)"
     *
     * @return  the account display-name
     */
    public String getDisplayName()
    {
        StringBuilder dn = new StringBuilder();
        dn.append(getName()).append(" (").append(id).append(")");
        return dn.toString();
    }

    /**
     * Get the account type ID
     *
     * @return  The accountTypeId (int code)
     */
    public int getId()
    {
        return id;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        ParameterCheck.mandatory("accountClass", this.accountClass);
        ParameterCheck.mandatory("accountTypeId", this.id);
        
        // We must guarantee that the accountTypeId falls within the range allowed by the accountClass.
        if ( !this.accountClass.isValidCode(this.id))
        {
            throw new AccountServiceException("Illegal account type id '" + this.id + "' for " + this.accountClass);
        }
    }
}
