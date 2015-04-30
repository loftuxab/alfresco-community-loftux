/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.accounts.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Account Entity Data Holder
 *  
 * @author davidc
 */
public class AccountEntity implements Serializable
{
    /** Account Id **/
    private Long id;
    
    /** Account Name **/
    private String name;
    
    /** Account Domain **/
    private List<String> domains;
    
    /** Account Package Type **/
    private int type;
    
    /** The Account Creation Date **/
    private Date creationDate;
    
    // Note: For constructing from persistent store
    @SuppressWarnings("unused")
    private AccountEntity()
    {
    }
    
    public AccountEntity(AccountEntity source)
    {
        if (source.id != null)
        {
            id = Long.valueOf(source.id);
        }
        if (source.name != null)
        {
            name = new String(source.name);
        }
        if (source.domains != null)
        {
            domains = new ArrayList<String>(source.domains);
        }
        type = source.type;
        if (source.creationDate != null)
        {
            creationDate = new Date(source.creationDate.getTime());
        }
    }
    
    public AccountEntity(String domain, int type)
    {
        setDomain(domain);
        setType(type);
    }
    
    public Long getId()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public List<String> getDomains()
    {
        if (domains == null)
        {
            return Collections.emptyList();
        }
        return domains;
    }
    
    // TODO: This is only required for persistence mapping - replace when multiple domains handled properly
    @SuppressWarnings("unused")
    private String getDomain()
    {
        if (domains == null || domains.size() == 0)
        {
            return null;
        }
        return domains.get(0);
    }
    
    public int getType()
    {
        return type;
    }
    
    public Date getCreationDate()
    {
        return creationDate;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setDomain(String domain)
    {
        this.domains = Arrays.asList(new String[]{domain});
    }
    
    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof AccountEntity))
        {
            return false;
        }
        AccountEntity other = (AccountEntity) obj;
        if (this.id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!this.id.equals(other.id))
        {
            return false;
        }
        return true;
    }
}
