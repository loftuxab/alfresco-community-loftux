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
package org.alfresco.module.org_alfresco_module_dod5015.audit;

import java.util.Date;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Class to represent the parameters for a Records Management 
 * audit log query.
 * 
 * @author Gavin Cornwell
 */
public final class RecordsManagementAuditQueryParameters
{
    private String user;
    private int maxEntries;
    private NodeRef nodeRef;
    private Date dateFrom;
    private Date dateTo;

    /**
     * Default constructor.
     */
    public RecordsManagementAuditQueryParameters()
    {
    }

    /**
     * 
     * @return The username to filter by
     */
    public String getUser()
    {
        return this.user;
    }

    /**
     * Restricts the retrieved audit trail to entries made by
     * the provided user.
     * 
     * @param user The username to filter by
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * 
     * @return The maximum number of audit log entries to retrieve
     */
    public int getMaxEntries()
    {
        return this.maxEntries;
    }

    /**
     * Restricts the retrieved audit trail to the last 
     * <code>maxEntries</code> entries.
     * 
     * @param maxEntries Maximum number of entries
     */
    public void setMaxEntries(int maxEntries)
    {
        this.maxEntries = maxEntries;
    }

    /**
     * 
     * @return The node to get entries for
     */
    public NodeRef getNodeRef()
    {
        return this.nodeRef;
    }

    /**
     * Restricts the retrieved audit trail to only those entries
     * created by the give node.
     * 
     * @param nodeRef The node to get entries for
     */
    public void setNodeRef(NodeRef nodeRef)
    {
        this.nodeRef = nodeRef;
    }

    /**
     * 
     * @return The date to retrieve entries from
     */
    public Date getDateFrom()
    {
        return this.dateFrom;
    }

    /**
     * Restricts the retrieved audit trail to only those entries
     * that occurred after the given date.
     * 
     * @param dateFrom Date to retrieve entries after
     */
    public void setDateFrom(Date dateFrom)
    {
        this.dateFrom = dateFrom;
    }

    /**
     * 
     * @return The date to retrive entries to
     */
    public Date getDateTo()
    {
        return this.dateTo;
    }

    /**
     * Restricts the retrieved audit trail to only those entries
     * that occurred before the given date.
     * 
     * @param dateTo Date to retrieve entries before
     */
    public void setDateTo(Date dateTo)
    {
        this.dateTo = dateTo;
    }
}
