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

import org.alfresco.util.ParameterCheck;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * This class defines an 'account class' and its allowed range of internal account codes and related metadata.
 * Account classes are dependency injected via Spring. Examples include "Free public email domain" and "Paid Premium Account"
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class AccountClass implements InitializingBean
{
    public enum Name
    {
        /**
         * @since Thor Phase 2 Sprint 1
         */
        PUBLIC_EMAIL_DOMAIN,
        PRIVATE_EMAIL_DOMAIN,
        PAID_BUSINESS
    }
    
    /**
     * We'll use this number internally to identify when min/max limits have not been set.
     */
    private static final int CODE_FOR_UNSET_LIMIT = Integer.MIN_VALUE;
    
    private int minCode = CODE_FOR_UNSET_LIMIT;
    private int maxCode = CODE_FOR_UNSET_LIMIT;
    private Name name;
    private boolean paidNetwork;
    
    private AccountRegistry accountRegistry;
    
    public void setAccountRegistry(AccountRegistry accountRegistry)
    {
        this.accountRegistry = accountRegistry;
    }
    
    /**
     * This initialisation method registers the Account Class with the {@link AccountRegistry}.
     */
    public void init()
    {
        accountRegistry.register(this);
    }
    
    public boolean isPaidNetwork()
    {
		return paidNetwork;
	}

	public void setPaidNetwork(boolean paidNetwork)
	{
		this.paidNetwork = paidNetwork;
	}

	public int getMinCode()
    {
        return minCode;
    }
    
    public int getMaxCode()
    {
        return maxCode;
    }
    
    public Name getName()
    {
        return name;
    }
    
    public void setMinCode(int minCode)
    {
        this.minCode = minCode;
    }
    
    public void setMaxCode(int maxCode)
    {
        this.maxCode = maxCode;
    }
    
    public void setName(Name name)
    {
        this.name = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        ParameterCheck.mandatory("paidNetwork", this.paidNetwork);
    }
        
    /**
     * This method returns a localised display-name for the Account Class.
     */
    public String getDisplayName()
    {
        String l10nKey = "account.class." + this.name.toString() + ".display-name";
        
        String displayName = I18NUtil.getMessage(l10nKey);
        return displayName == null ? this.name.toString() : displayName;
    }
    
    /**
     * Is the specified Account Code a valid number for this Account Class?
     * 
     * @param code a potential account code
     * @return <tt>true</tt> if valid, else <tt>false</tt>
     */
    public boolean isValidCode(int code)
    {
        boolean result = true;
        
        if (isMinCodeSet() && code < this.minCode)
        {
            result = false;
        }
        if (isMaxCodeSet() && code > this.maxCode)
        {
            result = false;
        }
        return result;
    }
    
    private boolean isMaxCodeSet()
    {
        return this.maxCode != CODE_FOR_UNSET_LIMIT;
    }
    
    private boolean isMinCodeSet()
    {
        return this.minCode != CODE_FOR_UNSET_LIMIT;
    }
    
    @Override
    public String toString()
    {
        // AccountClass name1 [1..1]
        // AccountClass name2 [2..99]
        // AccountClass name3 [100..]
        
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName())
              .append(" '").append(this.name).append("' [");
        if (isMinCodeSet())
        {
            result.append(this.minCode);
        }
        result.append("..");
        if (isMaxCodeSet())
        {
            result.append(this.maxCode);
        }
        result.append("]");
        
        return result.toString();
    }
}
