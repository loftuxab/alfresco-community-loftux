/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for the org.alfresco.enterprise.repo.cluster package.
 * <p>
 * This includes tests which will <strong>fail</strong> on the build servers -
 * do not include this suite in the CI build targets.
 * 
 * @author Matt Ward
 */
@RunWith(Suite.class)
@SuiteClasses({
    // Run the standard tests
    org.alfresco.enterprise.repo.cluster.BuildSafeTestSuite.class,
    
    // Additionally run these tests that cannot be run on the build servers.
    org.alfresco.enterprise.repo.cluster.core.HazelcastTest.class
})
public class ClusterTestSuite
{
}
