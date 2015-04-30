package org.alfresco.enterprise.repo;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All enterprise repository project UNIT test classes should be added to this test suite.
 */
public class AllUnitTestsSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return the test suite
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        unitTests(suite);
        return suite;
    }

    static void unitTests(TestSuite suite)
    {
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cache.HibernateCacheProviderTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.cache.ClusterAwareCacheFactoryTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.cache.HazelcastSimpleCacheTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.cache.InvalidatingCacheTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.AlfrescoTcpIpConfigTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.ClusteringBootstrapTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.ClusterServiceImplTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.HazelcastConfigFactoryBeanTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.HazelcastInstanceFactoryTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.MembershipChangeLoggerTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.core.NonMemberIPAddressPickerImplTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.index.IndexRecoveryJobTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.lock.HazelcastLockStoreTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.messenger.HazelcastMessengerFactoryTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.cluster.messenger.HazelcastMessengerTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.management.script.TypeConversionUtilsTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChangeTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.sync.deltas.SsmnChangeManagementTest.class));
        suite.addTest(new JUnit4TestAdapter(org.alfresco.enterprise.repo.transaction.RetryExceptionsTest.class));
    }
}
