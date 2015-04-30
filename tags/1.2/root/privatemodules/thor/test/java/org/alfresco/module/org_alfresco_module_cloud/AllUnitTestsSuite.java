package org.alfresco.module.org_alfresco_module_cloud;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.module.org_alfresco_module_cloud.webdav.CloudPutMethodTest;
import org.alfresco.module.org_alfresco_module_cloud.webdav.CloudWebDAVHelperTest;
import org.alfresco.module.org_alfresco_module_cloud.webdav.CloudWebDAVServletTest;

/**
 * All thor project UNIT test classes should be added to this test suite.
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
        unitTests(suite);
        return suite;
    }

    static void unitTests(TestSuite suite)
    {
        suite.addTest(new JUnit4TestAdapter(CloudPutMethodTest.class));
        suite.addTest(new JUnit4TestAdapter(CloudWebDAVHelperTest.class));
        suite.addTest(new JUnit4TestAdapter(CloudWebDAVServletTest.class));
    }
}
