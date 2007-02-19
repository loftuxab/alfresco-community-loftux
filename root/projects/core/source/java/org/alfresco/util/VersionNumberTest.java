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

import junit.framework.TestCase;

/**
 * Test for extension version class.
 * 
 * @author Roy Wetherall
 */
public class VersionNumberTest extends TestCase
{
    public void testCreate()
    {
        VersionNumber version1 = new VersionNumber("1");
        int[] parts1 = version1.getParts();
        assertNotNull(parts1);
        assertEquals(1, parts1.length);
        assertEquals(1, parts1[0]);

        VersionNumber version2 = new VersionNumber("1.2");
        int[] parts2 = version2.getParts();
        assertNotNull(parts2);
        assertEquals(2, parts2.length);
        assertEquals(1, parts2[0]);
        assertEquals(2, parts2[1]);

        VersionNumber version3 = new VersionNumber("1.2.3");
        int[] parts3 = version3.getParts();
        assertNotNull(parts3);
        assertEquals(3, parts3.length);
        assertEquals(1, parts3[0]);
        assertEquals(2, parts3[1]);
        assertEquals(3, parts3[2]);

        try
        {
            new VersionNumber("xxx");
            fail("Should not have created an invalid version");
        } catch (Exception exception)
        {
            // OK
        }
        try
        {
            new VersionNumber("1-1-2");
            fail("Should not have created an invalid version");
        } catch (Exception exception)
        {
            // OK
        }
        try
        {
            new VersionNumber("1.2.3a");
            fail("Should not have created an invalid version");
        } catch (Exception exception)
        {
            // OK
        }
    }

    public void testEquals()
    {
        VersionNumber version0 = new VersionNumber("1");
        VersionNumber version1 = new VersionNumber("1.2");
        VersionNumber version2 = new VersionNumber("1.2");
        VersionNumber version3 = new VersionNumber("1.2.3");
        VersionNumber version4 = new VersionNumber("1.2.3");
        VersionNumber version5 = new VersionNumber("1.3.3");
        VersionNumber version6 = new VersionNumber("1.0");

        assertFalse(version0.equals(version1));
        assertTrue(version1.equals(version2));
        assertFalse(version2.equals(version3));
        assertTrue(version3.equals(version4));
        assertFalse(version4.equals(version5));
        assertTrue(version0.equals(version6));
    }

    public void testCompare()
    {
        VersionNumber version0 = new VersionNumber("1");
        VersionNumber version1 = new VersionNumber("1.2");
        VersionNumber version2 = new VersionNumber("1.2");
        VersionNumber version3 = new VersionNumber("1.2.3");
        VersionNumber version5 = new VersionNumber("1.3.3");
        VersionNumber version6 = new VersionNumber("2.0");
        VersionNumber version7 = new VersionNumber("2.0.1");

        assertEquals(-1, version0.compareTo(version1));
        assertEquals(1, version1.compareTo(version0));
        assertEquals(0, version1.compareTo(version2));
        assertEquals(-1, version2.compareTo(version3));
        assertEquals(-1, version3.compareTo(version5));
        assertEquals(1, version6.compareTo(version5));
        assertEquals(-1, version6.compareTo(version7));
    }
}
