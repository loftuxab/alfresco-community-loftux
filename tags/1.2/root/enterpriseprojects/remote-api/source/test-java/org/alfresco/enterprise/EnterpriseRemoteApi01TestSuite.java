/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All Enterprise Remote API project test classes and test suites as a sequence of EnterpriseRemoteApi&lt;NN>TestSuite
 * classes. The original order is the same as run by ant to avoid any data issues.
 * The new test suite boundaries exist to allow tests to have different suite setups.
 * It is better to have &lt;NN> startups than one for each test. 
 */
public class EnterpriseRemoteApi01TestSuite extends TestSuite
{
    /**
     * Creates the test suite
     *
     * @return  the test suite
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        tests1(suite);

        return suite;
    }

    static void tests1(TestSuite suite) // 
    {
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.admin.AuthenticationTestPostTest.class);
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.admin.SyncTest.class);
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.admin.UserRegistrySynchronizationTestPostTest.class);
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.sync.RemoteSyncedNodeRestApiTest.class);
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.sync.SyncAdminServiceRestApiTest.class);
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.sync.SyncAuditServiceRestApiTest.class);
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.XssVulnerabilityEnterpriseTest.class);
    }
    
    static void tests2(TestSuite suite) // 
    {
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.sync.SyncConfigGetTest.class);
    }
    
    static void tests3(TestSuite suite) // 
    {
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.sync.connector.CloudConnectorWebScriptsTest.class);
      suite.addTestSuite(org.alfresco.enterprise.repo.web.scripts.sync.transport.CloudTransportWebScriptsTest.class);
    }
}