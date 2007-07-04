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
package org.alfresco.webservice.util;

import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;

/**
 * This class provides a number of common utility methods usful when using the web service API
 * 
 * @author Roy Wetherall
 */
public class Utils
{
	/**
	 * Creates a single valued named value.
	 * 
	 * @param name			the name 
	 * @param value			the value
	 * @return NamedValue	the created NamedValue object
	 */
    public static NamedValue createNamedValue(String name, String value)
    {
        NamedValue namedValue = new NamedValue();
        namedValue.setName(name);
        namedValue.setIsMultiValue(false);
        namedValue.setValue(value);
        return namedValue;
    }
    
    /**
     * Creates a multi-valued named value
     * 
     * @param name			the name
     * @param values		the array of values
     * @return NamedValue	the create NamedValue object
     */
    public static NamedValue createNamedValue(String name, String[] values)
    {
        NamedValue namedValue = new NamedValue();
        namedValue.setName(name);
        namedValue.setIsMultiValue(true);
        namedValue.setValues(values);
        return namedValue;
    }
    
    /**
     * Gets the store reference string for a given Store object.
     * 
     * @param store		the store
     * @return String	the store reference string
     */
    public static String getStoreRef(Store store)
    {
    	return store.getScheme() + "://" + store.getAddress();
    }

    /**
     * Gets the node reference string for a given Reference object
     * 
     * @param reference		the reference
     * @return String		the node reference string
     */
    public static String getNodeRef(Reference reference)
    {
    	return getStoreRef(reference.getStore()) + "/" + reference.getUuid();
    }
}
