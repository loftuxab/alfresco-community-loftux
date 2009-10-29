/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.cmis.test.ws;

import org.alfresco.cmis.test.ws.wsi.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class CmisWebServiceTestSuite extends TestSuite
{
    private static Log LOGGER = LogFactory.getLog(CmisWebServiceTestSuite.class);

    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(CmisDiscoveryServiceClient.class);
        suite.addTestSuite(CmisMultifilingServiceClient.class);
        suite.addTestSuite(CmisNavigationServiceClient.class);
        suite.addTestSuite(CmisObjectServiceClient.class);
        suite.addTestSuite(CmisRelationshipServiceClient.class);
        suite.addTestSuite(CmisRepositoryServiceClient.class);
        suite.addTestSuite(CmisVersioningServiceClient.class);
        return suite;
    }

    public static void main(String[] args)
    {
        LOGGER.info("\r Usage: Add '-wsi' option to run WS-I Profiler,\n if no option provided, UnitTests will be run");
        if (args != null && args.length > 0 && "-wsi".equalsIgnoreCase(args[0]))
        {
            LOGGER.info("Starting WS-I Profiler");
            Profiler.main(args);
        } else {
            LOGGER.info("Starting UnitTests");
            TestRunner.run(suite());
        }
    }
}
