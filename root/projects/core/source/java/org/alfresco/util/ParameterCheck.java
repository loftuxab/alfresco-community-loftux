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
package org.alfresco.util;

import java.util.Collection;

/**
 * Utility class to perform various common parameter checks
 * 
 * @author gavinc
 */
public final class ParameterCheck
{
    /**
     * Checks that the parameter with the given name has content i.e. it is not
     * null
     * 
     * @param strParamName Name of parameter to check
     * @param object Value of the parameter to check
     */
    public static void mandatory(final String strParamName, final Object object)
    {
        if (strParamName == null || strParamName.length() == 0)
        {
            throw new IllegalArgumentException("Parameter name is mandatory");
        }

        // check that the object is not null
        if (object == null)
        {
            throw new IllegalArgumentException(strParamName + " is a mandatory parameter");
        }
    }

    /**
     * Checks that the string parameter with the given name has content i.e. it
     * is not null and not zero length
     * 
     * @param strParamName Name of parameter to check
     * @param strParamValue Value of the parameter to check
     */
    public static void mandatoryString(final String strParamName, final String strParamValue)
    {
        if (strParamName == null || strParamName.length() == 0)
        {
            throw new IllegalArgumentException("Parameter name is mandatory");
        }

        // check that the given string value has content
        if (strParamValue == null || strParamValue.length() == 0)
        {
            throw new IllegalArgumentException(strParamName + " is a mandatory parameter");
        }
    }

    /**
     * Checks that the collection parameter contains at least one item.
     * 
     * @param strParamName Name of parameter to check
     * @param coll collection to check
     */
    public static void mandatoryCollection(final String strParamName, final Collection coll)
    {
        if (strParamName == null || strParamName.length() == 0)
        {
            throw new IllegalArgumentException("Parameter name is mandatory");
        }

        if (coll == null || coll.size() == 0)
        {
            throw new IllegalArgumentException(strParamName + " collection must contain at least one item");
        }
    }

}
