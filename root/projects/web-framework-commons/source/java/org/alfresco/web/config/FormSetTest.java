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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.config;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.alfresco.config.ConfigException;

public class FormSetTest extends TestCase
{
    private FormConfigElement testElement;
    public FormSetTest(String name)
    {
        super(name);
    }
    
    @Override
    public void setUp()
    {
        this.testElement = new FormConfigElement();
        this.testElement.addSet("root1", null, null);
        this.testElement.addSet("intermediate1", "root1", null);
        this.testElement.addSet("intermediate2", "root1", null);
        this.testElement.addSet("leaf1", "intermediate1", null);

        this.testElement.addSet("root2", null, null);

        this.testElement.addSet("root66", null, null);
        this.testElement.addSet("leaf2", "root66", null);
    }
    
    public void testGetSetIDsAreCorrect()
    {
        Map<String, FormSet> sets = this.testElement.getSets();
        assertEquals("Error in set names.", testElement.getSetIDs(), sets.keySet());
        
        // Note ordering is important here.
        Set<String> expectedSetIDs = new LinkedHashSet<String>();
        expectedSetIDs.add("root1");
        expectedSetIDs.add("intermediate1");
        expectedSetIDs.add("intermediate2");
        expectedSetIDs.add("leaf1");
        expectedSetIDs.add("root2");
        expectedSetIDs.add("root66");
        expectedSetIDs.add("leaf2");
        assertEquals(expectedSetIDs, sets.keySet());
    }
    
    public void testRootSetsCorrectlyIdentified()
    {
        Map<String, FormSet> rootSets = testElement.getRootSets();
        assertNotNull(rootSets);
        
        Set<String> expectedRoots = new LinkedHashSet<String>();
        expectedRoots.add("root1");
        expectedRoots.add("root2");
        expectedRoots.add("root66");
        assertEquals(expectedRoots, rootSets.keySet());
    }
    
    public void testRootSetsShouldHaveNoParent()
    {
        Map<String, FormSet> rootSets = testElement.getRootSets();
        for (String id : rootSets.keySet())
        {
            FormSet nextRoot = rootSets.get(id);
            assertNull(nextRoot.getParent());
        }
    }
    
    public void testNavigationToChildSets()
    {
        Map<String, FormSet> allSets = testElement.getSets();
        
        Set<String> expectedChildrenIDs = new LinkedHashSet<String>();
        expectedChildrenIDs.add("intermediate1");
        expectedChildrenIDs.add("intermediate2");

        // parent with children
        FormSet root1 = allSets.get("root1");
        assertEquals(expectedChildrenIDs, root1.getChildren().keySet());

        // parent without children
        FormSet root2 = allSets.get("root2");
        assertEquals(Collections.emptySet(), root2.getChildren().keySet());
    }
    
    public void testNavigationToParentSets()
    {
        Map<String, FormSet> allSets = testElement.getSets();

        FormSet leaf2 = allSets.get("leaf2");
        assertEquals("root66", leaf2.getParent().getSetId());
    }
    
    public void testDetectCyclicAncestors()
    {
        // It should not be possible to create a set of sets whose ancestors are
        // cyclic. This is true if we disallow the creation of a set with a parentID
        // of a set that does not exist.
        try
        {
            FormConfigElement brokenFormElement = new FormConfigElement();
            brokenFormElement.addSet("root", "leaf", null);
            // This next line will not in fact be called but it illustrates what we're
            // trying to prevent.
            brokenFormElement.addSet("leaf", "root", null);
        }
        catch (ConfigException expected)
        {
            return;
        }
        fail("Expected exception not thrown.");
    }
    
    public void testCannotGiveTheDefaultSetAParent() throws Exception
    {
        // It should not be possible to create the default set except as a 'root set'.
        try
        {
            this.testElement.addSet(FormConfigElement.DEFAULT_SET_ID, "root1", null);
        }
        catch(ConfigException expected)
        {
            expected.toString();
            return;
        }
        fail("Expected exception not thrown.");
    }
}
