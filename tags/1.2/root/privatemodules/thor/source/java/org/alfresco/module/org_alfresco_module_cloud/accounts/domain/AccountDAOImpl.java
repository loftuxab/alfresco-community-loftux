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

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;


/**
 * The standard iBatis-based implementation of an {@link AccountDAO}.
 * 
 * @author Neil Mc Erlean, janv
 * @since Thor
 */
public class AccountDAOImpl extends AbstractAccountDAOImpl
{
    private static final String INSERT_ACCOUNT = "alfresco.accounts.insert_Account";
    private static final String SELECT_ACCOUNT_BY_ID = "alfresco.accounts.select_AccountById";
    private static final String SELECT_ACCOUNT_BY_DOMAIN = "alfresco.accounts.select_AccountByDomain";
    private static final String UPDATE_ACCOUNT = "alfresco.accounts.update_Account";
    private static final String DELETE_ACCOUNT = "alfresco.accounts.delete_Account";
    private static final String COUNT_ALL_ACCOUNTS = "alfresco.accounts.count_AllAccounts";

    private SqlSessionTemplate template;
    
    public final void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) 
    {
        this.template = sqlSessionTemplate;
    }
    
    @Override
    public AccountEntity createAccountEntity(AccountEntity account)
    {
        template.insert(INSERT_ACCOUNT, account);
        return account;
    }
    
    @Override
    public AccountEntity getAccountEntity(Long id)
    {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("id", id);
        return (AccountEntity)template.selectOne(SELECT_ACCOUNT_BY_ID, params);
    }
    
    @Override
    public AccountEntity getAccountEntity(String domain)
    {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("domain", domain);
        return (AccountEntity)template.selectOne(SELECT_ACCOUNT_BY_DOMAIN, params);
    }
    
    @Override
    protected int updateAccountEntity(AccountEntity accountEntity)
    {
        return template.update(UPDATE_ACCOUNT, accountEntity);
    }
    
    @Override
    public int deleteAccountEntity(Long id)
    {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("id", id);
        return template.delete(DELETE_ACCOUNT, params);
    }
    
    @Override
    public long getNumberOfAccounts()
    {
    	return ((Long)template.selectOne(COUNT_ALL_ACCOUNTS)).longValue();
    }
}
