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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class RootLocatorImplTest extends TestCase
{
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    private static final String PROJECT_LEMON = "lemon";
    private static final String PROJECT_LEMON_QUERY = "/abc:lemon/";
    private static final String PROJECT_PLUM = "plum";
    private static final String PROJECT_PLUM_QUERY = "/cm:plum/slkjdcl";
    private static final String PROJECT_ORANGE = "orange";
    private static final String PROJECT_ORANGE_QUERY = "/dfg:orange/akjshx/AA:asx";
    private static final String PROJECT_BANANA = "banana";
    
    /**
     * 
     */
    public void testRootLocatorImpl()
    {
        RootLocatorImpl impl = new RootLocatorImpl();
        
        Map<String, String> map = new HashMap<String,String>();
        map.put(PROJECT_LEMON, PROJECT_LEMON_QUERY);
        map.put(PROJECT_PLUM, PROJECT_PLUM_QUERY);
        map.put(PROJECT_ORANGE, PROJECT_ORANGE_QUERY);
        // banana not added
        
        String defaultLocation = "/xyz:wibble";
        impl.setDefaultLocation(defaultLocation);
        assertEquals("default query not returned", defaultLocation, impl.getRootQuery(PROJECT_BANANA));
        
        impl.setProjectToQueryMap(map);
        assertEquals("default query not returned", defaultLocation, impl.getRootQuery(PROJECT_BANANA));
        assertEquals("default query not returned", PROJECT_PLUM_QUERY, impl.getRootQuery(PROJECT_PLUM));
        assertEquals("default query not returned", PROJECT_LEMON_QUERY, impl.getRootQuery(PROJECT_LEMON));
    }
}
