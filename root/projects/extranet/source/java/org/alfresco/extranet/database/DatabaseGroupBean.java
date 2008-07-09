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
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * The Class DatabaseGroupBean.
 * 
 * @author muzquiano
 */
public class DatabaseGroupBean 
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
	 * @param group the group
	 * 
	 * @return the database group
	 */
	public DatabaseGroup insert(DatabaseGroup group) 
	{
	    // build sql statement
		String sql = "insert into app_group (group_id, description, group_type, name) values (?,?,?,?)";
		
		// arguments and types
		Object args []= new Object[] {
		        group.getGroupId(),
		        group.getDescription(),
		        group.getGroupType(),
		        group.getName()
		};
		int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
		
		// execute the update
		jdbcTemplate.update(sql, args, types);
		
        // return the group
        return get(group.getGroupId());
	}

	/**
	 * Update.
	 * 
	 * @param group the group
	 * 
	 * @return true, if successful
	 */
	public boolean update(DatabaseGroup group) 
	{
	    // build sql statement
	    String sql = "update app_group set group_id=?, description=?, group_type=?, name=? where id = ?";
	    
        // arguments and types
        Object args []= new Object[] {
                group.getGroupId(),
                group.getDescription(),
                group.getGroupType(),
                group.getName(),
                group.getId()
        };
        int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER };
	    
        // execute the update
		int x = jdbcTemplate.update(sql, args, types);
		return (x > 0);
	}

	/**
	 * Delete.
	 * 
	 * @param group the group
	 * 
	 * @return true, if successful
	 */
	public boolean delete(DatabaseGroup group)
	{
	    // build sql statemnet
		String sql = "delete from app_group where group_id=?";
		
		// arguments and types
		Object params[] = new Object[] { group.getGroupId() };
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
		String sql = "select * from app_group";
		return jdbcTemplate.query(sql, new DatabaseGroupRowMapper());
	}
	
    /**
     * Gets the.
     * 
     * @param groupId the group id
     * 
     * @return the database group
     */
    public DatabaseGroup get(String groupId) 
    {
        // build the sql statement
        String sql = "select * from app_group where group_id='" + groupId + "'";
        
        // run the query
        List list = jdbcTemplate.query(sql, new DatabaseGroupRowMapper());
        if(list == null || list.size() == 0)
        {
            return null;
        }
        return (DatabaseGroup) list.get(0);        
    }
    
    /**
     * Adds the user to group.
     * 
     * @param groupId the group id
     * @param userId the user id
     * 
     * @return true, if successful
     */
    public boolean addUserToGroup(String groupId, String userId)
    {
        // first do a remove, in case
        removeUserFromGroup(groupId, userId);
        
        // now do an add
        String sql = "insert into group_membership (group_id, user_id) values (?,?)";
        
        // arguments and types
        Object args []= new Object[] { groupId, userId };
        int types[] = new int[] { Types.VARCHAR, Types.VARCHAR };
        
        // execute the update
        int x = jdbcTemplate.update(sql, args, types);
        return (x > 0);
    }
	
    /**
     * List user ids.
     * 
     * @param groupId the group id
     * 
     * @return the list
     */
    public List listUserIds(String groupId)
    {
        List userList = new ArrayList();
        
        String sql = "select distinct user_id from group_membership where group_id = '" + groupId + "'";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while(rowSet.next())
        {
            String userId = rowSet.getString("user_id");
            userList.add(userId);
        }
        
        return userList;
    }
    
    /**
     * Removes the user from group.
     * 
     * @param groupId the group id
     * @param userId the user id
     * 
     * @return true, if successful
     */
    public boolean removeUserFromGroup(String groupId, String userId)
    {
        // build sql statemnet
        String sql = "delete from group_membership where group_id=? and user_id=?";
        
        // arguments and types
        Object params[] = new Object[] { groupId, userId };
        int types[] = new int [] { Types.VARCHAR, Types.VARCHAR };
        
        // execute the update
        int x = jdbcTemplate.update(sql, params, types);
        return (x > 0);
    }
    
    /**
     * Gets the group ids for user.
     * 
     * @param userId the user id
     * 
     * @return the group ids for user
     */
    public List getGroupIdsForUser(String userId)
    {
        List groupList = new ArrayList();
        
        String sql = "select distinct group_id from group_membership where user_id = '" + userId + "'";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while(rowSet.next())
        {
            String groupId = rowSet.getString("group_id").trim();
            groupList.add(groupId);
        }
        
        return groupList;
    }
}
