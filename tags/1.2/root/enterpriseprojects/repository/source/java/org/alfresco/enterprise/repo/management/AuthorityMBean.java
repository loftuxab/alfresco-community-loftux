/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

/**
 * An interface exposing basic properties of users and groups for monitoring purposes.
 * 
 * @author dward
 */
public interface AuthorityMBean
{
    /**
     * Gets the number known users.
     * 
     * @return the number of users
     */
    public int getNumberOfUsers();

    /**
     * Gets the number of known groups.
     * 
     * @return the number of groups
     */
    public int getNumberOfGroups();
}
