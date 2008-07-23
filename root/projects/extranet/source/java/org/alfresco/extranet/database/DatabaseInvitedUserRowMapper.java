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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * The Class DatabaseInvitedUserRowMapper.
 * 
 * @author muzquiano
 */
public class DatabaseInvitedUserRowMapper implements RowMapper 
{
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
	    int id = rs.getInt("id");
	    
	    // core properties
	    String userId = rs.getString("user_id");
	    String email = rs.getString("email");
        String firstName = rs.getString("first_name");
        String middleName = rs.getString("middle_name");
        String lastName = rs.getString("last_name");
        String description = rs.getString("description");	    
	    
        // migration and invitation properties
	    String companyId = rs.getString("company_id");
	    String hash = rs.getString("hash");
	    boolean completed = rs.getBoolean("completed");
	    String whdUserId = rs.getString("whd_user_id");
	    String alfrescoUserId = rs.getString("alfresco_user_id");
	    	    
	    // group ids
	    String groupIds = rs.getString("group_ids");
	    
	    // invitation type
	    String invitationType = rs.getString("invitation_type");

	    // create the user
	    DatabaseInvitedUser user = new DatabaseInvitedUser(id, userId);
	    user.setEmail(email);
	    user.setFirstName(firstName);
	    user.setMiddleName(middleName);
	    user.setLastName(lastName);
	    user.setDescription(description);
	    
	    // apply migration properties
	    user.setCompanyId(companyId);
	    user.setHash(hash);
	    user.setCompleted(completed);
	    user.setWebHelpdeskUserId(whdUserId);
	    user.setAlfrescoUserId(alfrescoUserId);
	    
	    
	    
	    // apply invitation expiration date
        java.sql.Date expirationSqlDate = rs.getDate("expiration_date");
	    if(expirationSqlDate != null)
	    {
    	    long expirationTime = expirationSqlDate.getTime();
    	    java.util.Date expirationDate = new java.util.Date(expirationTime);
    	    user.setExpirationDate(expirationDate);
	    }
	    
        // subscription start date
        java.sql.Date subscriptionStartDate = rs.getDate("subscription_start");
        if(subscriptionStartDate != null)
        {
            long subscriptionStartTime = subscriptionStartDate.getTime();
            java.util.Date _subscriptionStartDate = new java.util.Date(subscriptionStartTime);
            user.setSubscriptionStart(_subscriptionStartDate);
        }
        
        // subscription end date
        java.sql.Date subscriptionEndDate = rs.getDate("subscription_end");
        if(subscriptionEndDate != null)
        {
            long subscriptionEndTime = subscriptionEndDate.getTime();
            java.util.Date _subscriptionEndDate = new java.util.Date(subscriptionEndTime);
            user.setSubscriptionEnd(_subscriptionEndDate);
        }
	    
	    
        
	    // apply group ids and invitation type
	    user.setGroupIds(groupIds);
	    user.setInvitationType(invitationType);

		return user;
	}
}
