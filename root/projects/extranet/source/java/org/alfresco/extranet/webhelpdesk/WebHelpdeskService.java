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
package org.alfresco.extranet.webhelpdesk;

import java.sql.Types;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * Extranet WebHelpdeskService implementation.
 * 
 * @author muzquiano
 */
public class WebHelpdeskService implements ApplicationContextAware
{
    protected ApplicationContext applicationContext;
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
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Instantiates a new web helpdesk service.
     */
    public WebHelpdeskService()
    {
    }  

    /**
     * Migrates a single user from their old whd id to a new one
     * 
     * @param formerUserId
     * @param newUserId
     * @return
     */
    public boolean migrateUser(String formerUserId, String newUserId)
    {
        int ldapConnectionId = getDefaultLdapConnectionId();
        
        return migrateUser(formerUserId, newUserId, ldapConnectionId, false);
    }
    
    /**
     * Migrates a single user from their old whd id to a new one
     * This should only be called once during the invitation process
     * 
     * @param formerUserId
     * @param newUserId
     * @return
     */
    public boolean migrateUser(String formerUserId, String newUserId, int ldapConnectionId, boolean force)
    {
        boolean success = false;
        
        // get the existing user if available
        WebHelpdeskUser formerUser = getUser(formerUserId);
        if(formerUser != null)
        {
            // check to make sure that the new user doesn't exist
            WebHelpdeskUser checkUser = getUser(newUserId);
            if(checkUser == null || force)
            {
                if(force)
                {
                    System.out.println("Unable to migrate user: " + formerUserId + " to id: " + newUserId + " because the user with id: " + newUserId + " already exists");
                    System.out.println("Forcing migration anyway");
                    
                    deleteUser(formerUserId);
                }

                String sql = "update CLIENT set USER_NAME=?, LDAP_CONNECTION_ID=?, RDN=?, BASE_DN=? where USER_NAME = ?";
                
                // arguments and types
                String rdn = getRdn(newUserId);
                String baseDn = getBaseDn(newUserId);
                
                System.out.println(" -> migrating rdn to: " + rdn);
                System.out.println(" -> migrating base_dn to: " + baseDn);
                
                Object args []= new Object[] { newUserId, ldapConnectionId, rdn, baseDn, formerUserId };
                int types[] = new int[] { Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
                
                // execute the update
                int x = jdbcTemplate.update(sql, args, types);
                success = (x > 0);
            }
            else
            {
                // we can't proceed as there is already a user there with this id
                System.out.println("Unable to migrate user: " + formerUserId + " to id: " + newUserId + " because the user with id: " + newUserId + " already exists");
            }
        }
        else
        {
            // there is no former user
            // do not handle this case yet
        }
        
        return success;
    }

    /**
     * Inserts a web helpdesk user
     * 
     * @param formerUserId
     * @param user
     * @return
     */
    public boolean insertUser(WebHelpdeskUser whdUser)
    {
        String sql = "insert into CLIENT (USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, NOTES, LDAP_CONNECTION_ID, RDN, BASE_DN) values (?,?,?,?,?,?,?,?)";
        
        // arguments and types
        Object args []= new Object[] { whdUser.getUserId(), whdUser.getFirstName(), whdUser.getLastName(), whdUser.getEmail(), whdUser.getDescription(), whdUser.getLdapConnectionId(), whdUser.getRdn(), whdUser.getBaseDn() };
        int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR };
        
        // execute the update
        int x = jdbcTemplate.update(sql, args, types);
        return (x > 0);
    }
    
    /**
     * Updates a web helpdesk user
     * 
     * @param formerUserId
     * @param user
     * @return
     */
    public boolean updateUser(WebHelpdeskUser user)
    {
        String sql = "update CLIENT set USER_NAME=?, FIRST_NAME=?, LAST_NAME=?, EMAIL=?, NOTES=?, LDAP_CONNECTION_ID=?, RDN=?, BASE_DN=? where USER_NAME = ?";
        
        // arguments and types
        Object args []= new Object[] { user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getDescription(), user.getLdapConnectionId(), user.getUserId(), user.getRdn(), user.getBaseDn() };
        int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
        
        // execute the update
        int x = jdbcTemplate.update(sql, args, types);
        return (x > 0);
    }
    
    /**
     * Update password.
     * 
     * @param user the user
     * @param password the password
     * 
     * @return true, if successful
     */
    /*
    public boolean updatePassword(WebHelpdeskUser user, String password)
    {
        String sql = "update CLIENT set PASSWORD=? where USER_NAME = ?";
        
        // arguments and types
        Object args []= new Object[] { password, user.getUserId() };
        int types[] = new int[] { Types.VARCHAR, Types.VARCHAR };
        
        // execute the update
        int x = jdbcTemplate.update(sql, args, types);
        return (x > 0);        
    }
    */
    
    
    /**
     * Gets a Web Helpdesk user
     * 
     * @param userName the USER_NAME field on the whd CLIENT table
     * 
     * @return the user object
     */
    public WebHelpdeskUser getUser(String userName) 
    {
        // build the sql statement
        String sql = "select * from CLIENT where USER_NAME='" + userName + "'";
        
        // run the query
        List list = jdbcTemplate.query(sql, new WebHelpdeskUserRowMapper());
        if(list == null || list.size() == 0)
        {
            return null;
        }
        return (WebHelpdeskUser) list.get(0);        
    } 

    /**
     * Returns a user from the tech table
     * 
     * @param userName
     * @return
     */
    public SqlRowSet getTech(String userName) 
    {
        // build the sql statement
        String sql = "select * from TECH where USER_NAME='" + userName + "'";
        
        return jdbcTemplate.queryForRowSet(sql);
    } 
    
    public boolean deleteUser(String userName)
    {
        // build sql statemnet
        String sql = "delete from CLIENT where USER_NAME=?";
        
        // arguments and types
        Object params[] = new Object[] { userName };
        int types[] = new int [] {Types.VARCHAR};
        
        // execute the update
        int x = jdbcTemplate.update(sql, params, types);
        return (x > 0);
    }
    
    public int getDefaultLdapConnectionId()
    {
        // build the sql statement
        String sql = "select ID from LDAP_CONNECTION where FRIENDLY_NAME = 'network'";
        
        // run the query
        return jdbcTemplate.queryForInt(sql);
    }
    
    /**
     * Executes an arbitrary query
     * 
     * @return the list
     */
    public SqlRowSet query(String sql)
    {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        return rowSet;
    }
    
    public String getRdn(String userId)
    {
        return "uid=" + userId;
    }
    
    public String getBaseDn(String userId)
    {
        return "network.alfresco.com:dc=public,dc=people,dc=ds,dc=alfresco,dc=com";
    }

}
