/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.extranet.database;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * The Class DatabaseCompanyBean.
 * 
 * @author muzquiano
 */
public class DatabaseCompanyBean 
{
	private JdbcTemplate jdbcTemplate;

	/**
	 * Sets the jdbc template.
	 * 
	 * @param jdbcTemplate the new jdbc template
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Insert.
	 * 
	 * @param company the company
	 * 
	 * @return the database company
	 */
	public DatabaseCompany insert(DatabaseCompany company) 
	{
	    // build sql statement
		String sql = "insert into company (name, description, oid, company_id) values (?,?,?,?)";
		
		// arguments and types
		Object args []= new Object[] {
		        company.getName(),
		        company.getDescription(),
		        company.getOid(),
		        company.getCompanyId()
		};
		int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
		
		// execute the update
		jdbcTemplate.update(sql, args, types);
		
        // return the company
        return get(company.getCompanyId());
	}

	/**
	 * Update.
	 * 
	 * @param company the company
	 * 
	 * @return true, if successful
	 */
	public boolean update(DatabaseCompany company) 
	{
	    // build sql statement
	    String sql = "update company set name=?, description=?, oid=?, company_id=? where id = ?";
	    
        // arguments and types
        Object args []= new Object[] {
                company.getName(),
                company.getDescription(),
                company.getOid(),
                company.getCompanyId(),
                company.getId()
        };
        int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER };
	    
        // execute the update
		int x = jdbcTemplate.update(sql, args, types);
		return (x > 0);
	}

	/**
	 * Delete.
	 * 
	 * @param company the company
	 * 
	 * @return true, if successful
	 */
	public boolean delete(DatabaseCompany company)
	{
	    // build sql statemnet
		String sql = "delete from company where company_id=?";
		
		// arguments and types
		Object params[] = new Object[] { company.getCompanyId() };
		int types[] = new int [] {Types.VARCHAR};
		
		// execute the update
		int x = jdbcTemplate.update(sql, params, types);
		return (x > 0);
	}

	/**
	 * List.
	 * 
	 * @return the list
	 */
	public List list() 
	{
		String sql = "select * from company";
		return jdbcTemplate.query(sql, new DatabaseCompanyRowMapper());
	}
	
    /**
     * Gets the.
     * 
     * @param companyId the company id
     * 
     * @return the database company
     */
    public DatabaseCompany get(String companyId) 
    {
        // build the sql statement
        String sql = "select * from company where company_id='" + companyId + "'";
        
        // run the query
        List list = jdbcTemplate.query(sql, new DatabaseCompanyRowMapper());
        if(list == null || list.size() == 0)
        {
            return null;
        }
        return (DatabaseCompany) list.get(0);        
    }   
	
}
