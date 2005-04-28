package com.activiti.repo.policy;

import com.activiti.repo.policy.impl.PolicyDefinitionServiceImplTest;
import com.activiti.repo.policy.impl.PolicyRuntimeServiceImplTest;
import com.activiti.repo.version.common.VersionHistoryImplTest;
import com.activiti.repo.version.common.VersionImplTest;
import com.activiti.repo.version.common.counter.VersionCounterDaoServiceTest;
import com.activiti.repo.version.common.versionlabel.SerialVersionLabelPolicyTest;
import com.activiti.repo.version.lightweight.VersionStoreNodeServiceImplTest;
import com.activiti.repo.version.lightweight.VersionStoreVersionServiceImplTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PolicyTestSuite extends TestSuite
{
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PolicyDefinitionServiceImplTest.class);
        suite.addTestSuite(PolicyRuntimeServiceImplTest.class);
        return suite;
    }
}
