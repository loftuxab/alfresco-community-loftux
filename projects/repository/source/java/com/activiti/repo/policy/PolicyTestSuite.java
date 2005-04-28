package com.activiti.repo.policy;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.activiti.repo.policy.impl.PolicyDefinitionServiceImplTest;
import com.activiti.repo.policy.impl.PolicyRuntimeServiceImplTest;

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
