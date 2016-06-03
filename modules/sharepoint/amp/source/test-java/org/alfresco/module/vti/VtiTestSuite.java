package org.alfresco.module.vti;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite to run all the run all the tests in the Sharepoint module.
 * 
 * @author Matt Ward
 */
@RunWith(Suite.class)
@SuiteClasses({
    org.alfresco.module.vti.handler.alfresco.HandlerTestSuite.class,
    org.alfresco.module.vti.web.ws.VtiWebWSPackageTestSuite.class,
    org.alfresco.module.vti.web.VtiRequestDispatcherTest.class
})
public class VtiTestSuite
{
    // See @SuiteClasses
}
