package org.alfresco.repo.lock;

import org.alfresco.repo.lock.common.AbstractPolicyImplTest;
import org.alfresco.repo.lock.simple.SimpleLockServiceTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class LockTestSuite extends TestSuite
{
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AbstractPolicyImplTest.class);
        suite.addTestSuite(SimpleLockServiceTest.class);
        return suite;
    }
}
