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
package org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain;

import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck;

/**
 * Invalid email domain Entity Data Holder
 *  
 * @author Neil Mc Erlean
 * @since Thor Module 1.0
 */
public class InvalidDomainEntity
{
    /** Invalid domain Id **/
    private Long id;
    
    /** Email Domain **/
    private String domain;
    
    /** Email invalidity Reason. See {@link DomainValidityCheck.FailureReason}**/
    private String type;
    
    /** Email invalidity Notes **/
    private String note;
    
    /**
     * Default constructor required for myBatis bean-instantiation.
     */
    public InvalidDomainEntity()
    {
        // Intentionally empty
    }
    
    public InvalidDomainEntity(String domain, String type)
    {
        this(domain, type, null);
    }
    
    public InvalidDomainEntity(String domain, String type, String note)
    {
        setDomain(domain);
        setType(type);
        setNote(note);
    }
    
    public Long getId()
    {
        return id;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public String getType()
    {
        return this.type;
    }
    
    public String getNote()
    {
        return this.note;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setNote(String note)
    {
        this.note = note;
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
        if (!(obj instanceof InvalidDomainEntity))
        {
            return false;
        }
        InvalidDomainEntity other = (InvalidDomainEntity) obj;
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
