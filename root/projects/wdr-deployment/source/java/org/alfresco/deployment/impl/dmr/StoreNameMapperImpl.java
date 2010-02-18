/*
 * Copyright (C) 2009-2009 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.deployment.impl.dmr;

/**
 * This store name mapper works with the DM deployment target(s)
 * as follows.
 * 
 * The root folder is found via an xpath query
 * e.g /app:company_home/cm:dm_deploy
 * 
 * AVM stores are mapped to one folder (e.g. staging, author, workflow all go
 * to the same destination folder.   This can be enabled or disabled via the "consolidate" 
 * property.
 */
public class StoreNameMapperImpl implements StoreNameMapper
{    
    /**
     * consolidate staging, author and workflow stores to one DM path.
     */
    private boolean consolidate = true;  
    
    /**
     * Map the storeName / project name
     */
    public String mapProjectName(String storeName)
    {
        /**
         * author AVM stores have the form
         * storeName--userId
         * 
         * workflow AVM sandboxes have the form
         * storeName--userId--workflowId
         */
        if(isConsolidate())
        {
            // collapse author and workflow sandboxes
            if(storeName.contains("--"))
            {
                return storeName.substring(0, storeName.indexOf("-"));
            }
        }
        return storeName;
    }

    public void setConsolidate(boolean consolidate)
    {
        this.consolidate = consolidate;
    }

    public boolean isConsolidate()
    {
        return consolidate;
    }
}
