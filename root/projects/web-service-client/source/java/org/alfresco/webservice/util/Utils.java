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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.webservice.util;

import org.alfresco.webservice.types.NamedValue;

/**
 * This class provides a number of common utility methods usful when using the web service API
 * 
 * @author Roy Wetherall
 */
public class Utils
{

    public static NamedValue createNamedValue(String name, String value)
    {
        NamedValue namedValue = new NamedValue();
        namedValue.setName(name);
        namedValue.setIsMultiValue(false);
        namedValue.setValue(value);
        return namedValue;
    }
    
    public static NamedValue createNamedValue(String name, String[] values)
    {
        NamedValue namedValue = new NamedValue();
        namedValue.setName(name);
        namedValue.setIsMultiValue(true);
        namedValue.setValues(values);
        return namedValue;
    }
}
