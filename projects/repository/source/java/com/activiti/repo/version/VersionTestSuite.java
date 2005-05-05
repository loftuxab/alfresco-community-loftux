package com.activiti.repo.version;

import com.activiti.repo.version.common.VersionHistoryImplTest;
import com.activiti.repo.version.common.VersionImplTest;
import com.activiti.repo.version.common.counter.VersionCounterDaoServiceTest;
import com.activiti.repo.version.common.versionlabel.SerialVersionLabelPolicyTest;
import com.activiti.repo.version.lightweight.ContentServiceImplTest;
import com.activiti.repo.version.lightweight.NodeServiceImplTest;
import com.activiti.repo.version.lightweight.VersionServiceImplTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Version test suite
 * 
 * @author Roy Wetherall
 */
public class VersionTestSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return  the test suite
     */
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(VersionImplTest.class);
        suite.addTestSuite(VersionHistoryImplTest.class);
        suite.addTestSuite(SerialVersionLabelPolicyTest.class);
        suite.addTestSuite(VersionCounterDaoServiceTest.class);
        suite.addTestSuite(VersionServiceImplTest.class);
        suite.addTestSuite(NodeServiceImplTest.class);
        suite.addTestSuite(ContentServiceImplTest.class);
        return suite;
    }
}
