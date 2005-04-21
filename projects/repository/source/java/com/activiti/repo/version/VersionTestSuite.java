package com.activiti.repo.version;

import com.activiti.repo.version.common.VersionHistoryImplTest;
import com.activiti.repo.version.common.VersionImplTest;
import com.activiti.repo.version.common.counter.VersionCounterDaoServiceTest;
import com.activiti.repo.version.lightweight.VersionStoreNodeServiceImplTest;
import com.activiti.repo.version.lightweight.VersionStoreVersionServiceImplTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class VersionTestSuite extends TestSuite
{
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(VersionImplTest.class);
        suite.addTestSuite(VersionHistoryImplTest.class);
        suite.addTestSuite(VersionCounterDaoServiceTest.class);
        suite.addTestSuite(VersionStoreVersionServiceImplTest.class);
        suite.addTestSuite(VersionStoreNodeServiceImplTest.class);
        return suite;
    }
}
