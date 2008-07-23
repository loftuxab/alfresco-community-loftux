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
 * The Class DatabaseInvitedUserBean.
 * 
 * @author muzquiano
 */
public class DatabaseInvitedUserBean 
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
	 * @param user the user
	 * 
	 * @return the database invited user
	 */
	public DatabaseInvitedUser insert(DatabaseInvitedUser user) 
	{
	    // build sql statement
		String sql = "insert into invited_user (user_id, email, company_id, hash, completed, whd_user_id, alfresco_user_id, description, first_name, middle_name, last_name, expiration_date, group_ids, invitation_type, subscription_start, subscription_end) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
        // date formats
        String sqlExpirationDate = null;
        if(user.getExpirationDate() != null)
        {
            sqlExpirationDate = DatabaseService.SQL_DATE_FORMAT.format(user.getExpirationDate());
        }
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
				
		
		// date format
		//SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
	    //String sqlExpirationDate = SQL_DATE_FORMAT.format(user.getExpirationDate());
	    
		// arguments and types
		Object args []= new Object[] {
		        user.getUserId(),
		        user.getEmail(),
		        user.getCompanyId(),
		        user.getHash(),
		        user.isCompleted(),
		        user.getWebHelpdeskUserId(),
		        user.getAlfrescoUserId(),
		        user.getDescription(),
		        user.getFirstName(),
		        user.getMiddleName(),
		        user.getLastName(),
		        sqlExpirationDate,
		        user.getGroupIds(),
		        user.getInvitationType(),
		        sqlSubscriptionStartDate,
		        sqlSubscriptionEndDate
		};
		int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.DATE };
		
		// execute the update
		jdbcTemplate.update(sql, args, types);
		
        // return the user
        return get(user.getUserId());
	}

	/**
	 * Update.
	 * 
	 * @param user the user
	 * 
	 * @return true, if successful
	 */
	public boolean update(DatabaseInvitedUser user) 
	{
	    // build sql statement
	    String sql = "update invited_user set user_id=?, email=?, company_id=?, hash=?, completed=?, whd_user_id=?, alfresco_user_id=?, description=?, first_name=?, middle_name=?, last_name=?, expiration_date=?, group_ids=?, invitation_type=?, subscription_start=?, subscription_end=? where id = ?";
	    
        // date formats
        String sqlExpirationDate = null;
        if(user.getExpirationDate() != null)
        {
            sqlExpirationDate = DatabaseService.SQL_DATE_FORMAT.format(user.getExpirationDate());
        }
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
                user.getEmail(),
                user.getCompanyId(),
                user.getHash(),
                user.isCompleted(),
                user.getWebHelpdeskUserId(),
                user.getAlfrescoUserId(),
                user.getDescription(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                sqlExpirationDate,
                user.getGroupIds(),
                user.getInvitationType(),
                sqlSubscriptionStartDate,
                sqlSubscriptionEndDate,
                user.getId()
        };
        int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.DATE, Types.INTEGER };
	    
        // execute the update
		int x = jdbcTemplate.update(sql, args, types);
		return (x > 0);
	}

	/**
	 * Delete.
	 * 
	 * @param user the user
	 * 
	 * @return true, if successful
	 */
	public boolean delete(DatabaseInvitedUser user)
	{
	    // build sql statemnet
		String sql = "delete from invited_user where user_id=?";
		
		// arguments and types
		Object params[] = new Object[] { user.getUserId() };
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
		String sql = "select * from invited_user";
		return jdbcTemplate.query(sql, new DatabaseInvitedUserRowMapper());
	}

    /**
     * Gets the.
     * 
     * @param userId the user id
     * 
     * @return the database invited user
     */
    public DatabaseInvitedUser get(String userId) 
    {
        // build the sql statement
        String sql = "select * from invited_user where user_id='" + userId + "'";
        
        // run the query
        List list = jdbcTemplate.query(sql, new DatabaseInvitedUserRowMapper());
        if(list == null || list.size() == 0)
        {
            return null;
        }
        return (DatabaseInvitedUser) list.get(0);        
    }
    
    /**
     * Gets the from hash.
     * 
     * @param hash the hash
     * 
     * @return the from hash
     */
    public DatabaseInvitedUser getFromHash(String hash)
    {
        // build the sql statement
        String sql = "select * from invited_user where hash='" + hash + "'";
        
        // run the query
        List list = jdbcTemplate.query(sql, new DatabaseInvitedUserRowMapper());
        if(list == null || list.size() == 0)
        {
            return null;
        }
        return (DatabaseInvitedUser) list.get(0);                
    }

    /**
     * Gets the by email.
     * 
     * @param email the email
     * 
     * @return the by email
     */
    public DatabaseInvitedUser getByEmail(String email) 
    {
        // build the sql statement
        String sql = "select * from invited_user where email='" + email + "'";
        
        // run the query
        List list = jdbcTemplate.query(sql, new DatabaseInvitedUserRowMapper());
        if(list == null || list.size() == 0)
        {
            return null;
        }
        return (DatabaseInvitedUser) list.get(0);        
    }
    
}
