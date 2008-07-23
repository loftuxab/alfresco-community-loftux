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
 * The Class DatabaseUserBean.
 * 
 * @author muzquiano
 */
public class DatabaseUserBean 
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
	 * Inserts a user into the database.
	 * 
	 * @param user the user
	 * 
	 * @return the database user
	 */
	public DatabaseUser insert(DatabaseUser user) 
	{
	    // build sql statement
		String sql = "insert into user (user_id, first_name, middle_name, last_name, email, description, subscription_start, subscription_end, level) values (?,?,?,?,?,?,?,?,?)";
		
        // date formats
        String sqlSubscriptionStartDate = null;
        if(user.getSubscriptionStart() != null)
        {
            sqlSubscriptionStartDate = DatabaseService.SQL_DATE_FORMAT.format(user.getSubscriptionStart());
        }
        String sqlSubscriptionEndDate = null;
        if(user.getSubscriptionEnd() != null)
        {
            sqlSubscriptionEndDate = DatabaseService.SQL_DATE_FORMAT.format(user.getSubscriptionEnd());
        }
		
		// arguments and types
		Object args []= new Object[] {
		        user.getUserId(),
		        user.getFirstName(),
		        user.getMiddleName(),
		        user.getLastName(),
		        user.getEmail(),
		        user.getDescription(),
		        sqlSubscriptionStartDate,
		        sqlSubscriptionEndDate,
		        user.getLevel()
		};
		int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.DATE, Types.VARCHAR};
		
		// execute the update
		jdbcTemplate.update(sql, args, types);
		
		// return the user
		return get(user.getUserId());
	}

	/**
	 * Updates a user.
	 * 
	 * @param user the user
	 * 
	 * @return true, if update
	 */
	public boolean update(DatabaseUser user) 
	{
	    // build sql statement
	    String sql = "update user set user_id=?, first_name=?, middle_name=?, last_name=?, email=?, description=?, subscription_start=?, subscription_end=?, level=? where id = ?";
	    
        // date formats
        String sqlSubscriptionStartDate = null;
        if(user.getSubscriptionStart() != null)
        {
            sqlSubscriptionStartDate = DatabaseService.SQL_DATE_FORMAT.format(user.getSubscriptionStart());
        }
        String sqlSubscriptionEndDate = null;
        if(user.getSubscriptionEnd() != null)
        {
            sqlSubscriptionEndDate = DatabaseService.SQL_DATE_FORMAT.format(user.getSubscriptionEnd());
        }
	    
        // arguments and types
        Object args []= new Object[] {
                user.getUserId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getEmail(),
                user.getDescription(),
                sqlSubscriptionStartDate,
                sqlSubscriptionEndDate,
                user.getLevel(),
                user.getId()
        };
        int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.DATE, Types.VARCHAR, Types.INTEGER };
	    
        // execute the update
		int x = jdbcTemplate.update(sql, args, types);
		return (x > 0);
	}

	/**
	 * Deletes a user.
	 * 
	 * @param user the user
	 * 
	 * @return true, if delete
	 */
	public boolean delete(DatabaseUser user)
	{
	    // build sql statemnet
		String sql = "delete from user where user_id=?";
		
		// arguments and types
		Object params[] = new Object[] { user.getUserId() };
		int types[] = new int [] {Types.VARCHAR};
		
		// execute the update
		int x = jdbcTemplate.update(sql, params, types);
		return (x > 0);
	}

	/**
	 * Returns a list of users.
	 * 
	 * @return the list
	 */
	public List list() 
	{
		String sql = "select * from user";
		return jdbcTemplate.query(sql, new DatabaseUserRowMapper());
	}
	
    /**
     * Gets the.
     * 
     * @param userId the user id
     * 
     * @return the database user
     */
    public DatabaseUser get(String userId) 
    {
        // build the sql statement
        String sql = "select * from user where user_id='" + userId + "'";
        
        // run the query
        List list = jdbcTemplate.query(sql, new DatabaseUserRowMapper());
        if(list == null || list.size() == 0)
        {
            return null;
        }
        return (DatabaseUser) list.get(0);        
    }	

    /**
     * Gets the by email.
     * 
     * @param email the email
     * 
     * @return the by email
     */
    public DatabaseUser getByEmail(String email) 
    {
        // build the sql statement
        String sql = "select * from user where email='" + email + "'";
        
        // run the query
        List list = jdbcTemplate.query(sql, new DatabaseUserRowMapper());
        if(list == null || list.size() == 0)
        {
            return null;
        }
        return (DatabaseUser) list.get(0);        
    }   
    
}
