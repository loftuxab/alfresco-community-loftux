/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
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
