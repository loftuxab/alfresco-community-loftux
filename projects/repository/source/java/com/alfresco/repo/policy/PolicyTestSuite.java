package org.alfresco.repo.policy;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.repo.policy.impl.PolicyDefinitionServiceImplTest;
import org.alfresco.repo.policy.impl.PolicyRuntimeServiceImplTest;

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
