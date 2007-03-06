/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.module.phpIntegration.lib;

import java.util.List;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * @author Roy Wetherall
 */
public class Session
{
    private ServiceRegistry serviceRegistry;
    
    public Session(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    public ServiceRegistry getServiceRegistry()
    {
        return serviceRegistry;
    }
    
    public Store[] getStores()
    {
        // Get the node service
        NodeService nodeService = this.serviceRegistry.getNodeService();
        
        // Get the stores
        List<StoreRef> storeRefs = nodeService.getStores();
        
        // Buld the result array
        Store[] result = new Store[storeRefs.size()];
        int index = 0;
        for (StoreRef storeRef : storeRefs)
        {
            result[index] = new Store(this, storeRef);
            index ++;
        }
        
        return result;
    }
}
