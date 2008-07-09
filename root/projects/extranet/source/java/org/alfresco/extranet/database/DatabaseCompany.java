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

import org.alfresco.extranet.AbstractCompany;

/**
 * The Class DatabaseCompany.
 * 
 * @author muzquiano
 */
public class DatabaseCompany extends AbstractCompany
{
    protected int id;
    
    /**
     * Instantiates a new database company.
     * 
     * @param id the id
     * @param companyId the company id
     */
    public DatabaseCompany(int id, String companyId)
    {
        super(companyId);
        
        this.id = id;
    }
    
    /**
     * Instantiates a new database company.
     * 
     * @param companyId the company id
     */
    public DatabaseCompany(String companyId)
    {
        super(companyId);
    }
    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId()
    {
        return this.id;
    }
}
