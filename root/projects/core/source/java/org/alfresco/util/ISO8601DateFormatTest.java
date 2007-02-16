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
package org.alfresco.util;

import java.util.Date;

import junit.framework.TestCase;

public class ISO8601DateFormatTest extends TestCase
{
    public void testConversion()
    {
        String test = "2005-09-16T17:01:03.456+01:00";
        // convert to a date
        Date date = ISO8601DateFormat.parse(test);
        // get the string form
        String strDate = ISO8601DateFormat.format(date);
        // convert back to a date from the converted string
        Date dateAfter = ISO8601DateFormat.parse(strDate);
        // make sure the date objects match, test this instead of the
        // string as the string form will be different in different
        // locales
        assertEquals(date, dateAfter);
    }
}
