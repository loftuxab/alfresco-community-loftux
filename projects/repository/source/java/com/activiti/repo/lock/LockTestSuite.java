package com.activiti.repo.lock;

import com.activiti.repo.lock.common.AbstractPolicyImplTest;
import com.activiti.repo.lock.simple.SimpleLockServiceTest;

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
