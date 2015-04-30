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
package org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain;

import java.util.List;


/**
 * Data abstraction layer for {@link InvalidDomainEntity entities}.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public interface InvalidDomainDAO
{
    /**
     * Retrieves {@link InvalidDomainEntity} by email domain.
     * 
     * @param domain  the domain
     * @return        the {@link InvalidDomainEntity} or <tt>null</tt> if no entry for this domain.
     */
    InvalidDomainEntity getInvalidDomain(String domain);
    
    /**
     * Retrieves a page of invalid domain data in DB id order - which is insertion order.
     * @param startIndex
     * @param pageSize
     * @return
     */
    List<InvalidDomainEntity> getInvalidDomains(int startIndex, int pageSize);
    
    /**
     * Retrieves the total number of rows in the blackist table.
     * @since Thor Phase 2 Sprint 1
     */
    Integer getInvalidDomainCount();
    
    /**
     * Creates an {link InvalidDomainEntity}
     * 
     * @return        the InvalidDomainEntity
     * @throws InvalidDomainException if an InvalidDomainEntity exists with this domain.
     * @since Thor Phase 2 Sprint 1
     */
    InvalidDomainEntity createInvalidDomain(InvalidDomainEntity entity);

    /**
     * Deletes an {@link InvalidDomainEntity}.
     * 
     * @param domain the domain
     * @since Thor Phase 2 Sprint 1
     */
    int deleteInvalidDomain(String domain);
    
    /**
     * Updates an {@link InvalidDomainEntity}
     * 
     * @param entity the invalid domain entity to update. The row is identified by {@link InvalidDomainEntity#getDomain()} (which cannot
     *               be updated) and the {@link InvalidDomainEntity#getNote()} and the {@link InvalidDomainEntity#getType()} can be updated.
     * @param domain domain of the existing InvalidDomain to be updated.
     * @since Thor Phase 2 Sprint 1
     */
    int updateInvalidDomain(InvalidDomainEntity entity);
}

