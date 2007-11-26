/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.web.scripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Web Script Storage
 * 
 * @author davidc
 */
public class SearchPath
{
    private Collection<Store> searchPath = Collections.emptyList();


    public void setSearchPath(List<Store> searchPath)
    {
        this.searchPath = searchPath;
    }
    
    
    /**
     * Gets all Web Script Stores
     * 
     * @return  all Web Script Stores
     */
    @SuppressWarnings("unchecked")
    public Collection<Store> getStores()
    {
        Collection<Store> aliveStores = new ArrayList<Store>();
        for (Store store : searchPath)
        {
            if (store.exists())
            {
                aliveStores.add(store);
            }
        }
        return aliveStores;
    }

    /**
     * Gets the Web Script Store for the given Store path
     * 
     * @param storePath
     * @return  store (or null, if not found)
     */
    public Store getStore(String storePath)
    {
        Collection<Store> stores = getStores();
        for (Store store : stores)
        {
            if (store.getBasePath().equals(storePath))
            {
                return store;
            }
        }
        return null;
    }
    
}
