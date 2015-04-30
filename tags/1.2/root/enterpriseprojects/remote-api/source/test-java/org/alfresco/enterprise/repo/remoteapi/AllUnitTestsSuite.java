package org.alfresco.enterprise.repo.remoteapi;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All enterprise remote-api project UNIT test classes should be added to this test suite.
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
        //we don't actually have any unit tests at the moment
        //please add some
        return suite;
    }
}
