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
import junit.framework.JUnit4TestAdapter;

/**
 * All Enterprise Repository project test classes and test suites as a sequence of EnterpriseRepository&lt;NN>TestSuite
 * classes. The original order is the same as run by ant to avoid any data issues.
 * The new test suite boundaries exist to allow tests to have different suite setups.
 * It is better to have &lt;NN> startups than one for each test. 
 */
public class EnterpriseRepository01TestSuite extends TestSuite
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
    
    static void tests1(TestSuite suite) // tests="50" time="548.718"
    {
        suite.addTestSuite(org.alfresco.enterprise.repo.admin.indexcheck.IndexCheckServiceImplTest.class);
        suite.addTestSuite(org.alfresco.enterprise.heartbeat.HeartBeatTest.class);
  	    suite.addTestSuite(org.alfresco.enterprise.license.LicenseComponentTest.class); // 5.5 mins!
    }
  
    static void tests2(TestSuite suite) // tests="33" time="151.981"
    {
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.ClusterServiceImplSpringTest.class));
    }
    
    static void tests3(TestSuite suite) // tests="29" time="131.204"
    {
  	    suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.HazelcastTest.class));
  	    suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.MembershipChangeCacheDropperTest.class));
  	    suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.lock.HazelcastLockStoreTxTest.class));
    }
    
    static void tests4(TestSuite suite) // tests="15" time="6.872"
    {
  	    suite.addTestSuite(org.alfresco.enterprise.repo.content.routing.StoreSelectorAspectContentStoreTest.class); // fails with previous tests
  	    suite.addTestSuite(org.alfresco.enterprise.repo.forms.jmx.JMXFormProcessorTest.class);
    }
  
    static void tests5(TestSuite suite) // tests="29" time="114.604"
    {
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.management.script.JmxScriptTest.class));
    }
  
    static void tests6(TestSuite suite) // tests="45" time="266.888"
    {
        suite.addTestSuite(org.alfresco.enterprise.repo.management.subsystems.AuthenticationChainTest.class);
        suite.addTestSuite(org.alfresco.enterprise.repo.management.subsystems.RevertMethodForAbstractPropertyBackedBeanChildrenTest.class);
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.sync.SyncAdminServiceImplTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.sync.SyncChangeMonitorTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.sync.SyncServiceImplTest.class));
    }
    
    static void tests7(TestSuite suite) // tests="42" time="171.426"
    {
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.sync.SyncTrackerComponentTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.sync.audit.SyncAuditServiceImplTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.sync.audit.SyncAuditServiceIntegrationTest.class));
        suite.addTestSuite(org.alfresco.enterprise.security.sync.EnterpriseChainingUserRegistrySynchronizerTest.class);
    }
}
