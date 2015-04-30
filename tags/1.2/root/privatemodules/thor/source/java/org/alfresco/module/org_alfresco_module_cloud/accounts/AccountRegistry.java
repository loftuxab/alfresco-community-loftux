/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Account Type Registry, keeps a registry of all the account types that can be
 * created
 *
 * @author David Gildeh
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class AccountRegistry
{
    private Map<AccountClass.Name, AccountClass> classes = new HashMap<AccountClass.Name, AccountClass>();
    private Map<Integer, AccountType> types = new HashMap<Integer, AccountType>();
    
    public void register(AccountType type)
    {
        types.put(type.getId(), type);
    }

    public void register(AccountClass accountClass)
    {
        classes.put(accountClass.getName(), accountClass);
    }

    public void setAccountTypes(List<AccountType> types)
    {
        for (AccountType type : types)
        {
            register(type);
        }
    }
    
    /**
     * Get a list of all the account classes
     * 
     * @return
     */
    public Collection<AccountClass> getClasses()
    {
        return Collections.unmodifiableCollection(classes.values());
    }

    /**
     * Get a list of all the account types
     *
     * @return Collection<AccountType>  collection of account types
     */
    public List<AccountType> getTypes()
    {
        return new ArrayList<AccountType>(this.types.values());
    }

    /**
     * Get an account type
     * 
     * @param id
     * @return
     */
    public AccountType getType(int id)
    {
        return types.get(id);
    }
}
