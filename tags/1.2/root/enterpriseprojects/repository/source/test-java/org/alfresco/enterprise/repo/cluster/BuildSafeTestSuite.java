/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.enterprise.repo.cluster;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for org.alfresco.enterprise.repo.cluster tests, but <strong>excluding</strong>
 * tests which are known to fail in the CI environment (Bamboo).
 * <p>
 * These tests are still useful in the desktop development environment however,
 * so are kept for this reason. {@link ClusterTestSuite} runs all the tests in this
 * suite, plus the offending tests.
 * 
 * @author Matt Ward
 */
@RunWith(Suite.class)
@SuiteClasses({
    org.alfresco.enterprise.repo.cluster.core.AlfrescoTcpIpConfigTest.class,
    org.alfresco.enterprise.repo.cluster.cache.ClusterAwareCacheFactoryTest.class,
    org.alfresco.enterprise.repo.cluster.core.ClusterServiceImplSpringTest.class,
    org.alfresco.enterprise.repo.cluster.core.ClusterServiceImplTest.class,
    org.alfresco.enterprise.repo.cluster.core.ClusteringBootstrapTest.class,
    org.alfresco.enterprise.repo.cluster.core.HazelcastConfigFactoryBeanTest.class,
    org.alfresco.enterprise.repo.cluster.messenger.HazelcastMessengerFactoryTest.class,
    org.alfresco.enterprise.repo.cluster.messenger.HazelcastMessengerTest.class,
    org.alfresco.enterprise.repo.cluster.cache.HazelcastSimpleCacheTest.class,
    org.alfresco.enterprise.repo.cluster.cache.InvalidatingCacheTest.class,
    org.alfresco.enterprise.repo.cluster.index.IndexRecoveryJobTest.class,
    org.alfresco.enterprise.repo.cluster.lock.HazelcastLockStoreTest.class,
    org.alfresco.enterprise.repo.cluster.lock.HazelcastLockStoreTxTest.class
})
public class BuildSafeTestSuite
{
    // Annotations specify the suite.
}
