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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;

/**
 * The standard iBatis-based implementation of an {@link InvalidDomainDAO}.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class InvalidDomainDAOImpl implements InvalidDomainDAO
{
    private static final String INSERT_INVALID_DOMAIN = "alfresco.invaliddomains.insert_InvalidDomain";
    
    private static final String SELECT_INVALID_DOMAIN = "alfresco.invaliddomains.select_InvalidDomain";
    private static final String SELECT_ALL_INVALID_DOMAINS = "alfresco.invaliddomains.select_AllInvalidDomains";
    private static final String SELECT_INVALID_DOMAIN_COUNT = "alfresco.invaliddomains.select_InvalidDomainCount";
    
    private static final String UPDATE_INVALID_DOMAIN = "alfresco.invaliddomains.update_InvalidDomain";
    private static final String DELETE_INVALID_DOMAIN = "alfresco.invaliddomains.delete_InvalidDomain";
    
    private SqlSessionTemplate template;
    
    public final void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) 
    {
        this.template = sqlSessionTemplate;
    }
    
    @Override
    public InvalidDomainEntity getInvalidDomain(String domain)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("domain", domain);
        return (InvalidDomainEntity)template.selectOne(SELECT_INVALID_DOMAIN, params);
    }
    
    @Override
    public List<InvalidDomainEntity> getInvalidDomains(int startIndex, int pageSize)
    {
        @SuppressWarnings("unchecked")
        List<InvalidDomainEntity> result = template.selectList(SELECT_ALL_INVALID_DOMAINS,
                                                                       null,
                                                                       new RowBounds(startIndex, pageSize));
        return result;
    }
    
    @Override public Integer getInvalidDomainCount()
    {
        return (Integer) template.selectOne(SELECT_INVALID_DOMAIN_COUNT);
    }
    
    @Override
    public InvalidDomainEntity createInvalidDomain(InvalidDomainEntity entity)
    {
        verifyDomainDoesNotExist(entity);
        template.insert(INSERT_INVALID_DOMAIN, entity);
        return entity;
    }
    
    @Override
    public int deleteInvalidDomain(String domain)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("domain", domain);
        int rowsDeleted = template.delete(DELETE_INVALID_DOMAIN, params);
        
        if (rowsDeleted == 0)
        {
            throw new InvalidDomainException("Domain doesn't exist: " + domain);
        }
        else
        {
            return rowsDeleted;
        }
    }
    
    @Override
    public int updateInvalidDomain(InvalidDomainEntity entity)
    {
        verifyDomainExists(entity);
        
        return template.update(UPDATE_INVALID_DOMAIN, entity);
    }
    
    private void verifyDomainExists(InvalidDomainEntity entity)
    {
        if (entity == null || entity.getDomain() == null)
        {
            throw new NullPointerException("Illegal null domain");
        }
        if (getInvalidDomain(entity.getDomain()) == null)
        {
            throw new InvalidDomainException("Domain doesn't exist: " + entity.getDomain());
        }
    }
    
    private void verifyDomainDoesNotExist(InvalidDomainEntity entity)
    {
        if (entity == null || entity.getDomain() == null)
        {
            throw new NullPointerException("Illegal null " + InvalidDomainEntity.class.getSimpleName());
        }
        if (getInvalidDomain(entity.getDomain()) != null)
        {
            throw new InvalidDomainException("Duplicate domain: " + entity.getDomain());
        }
    }
}
