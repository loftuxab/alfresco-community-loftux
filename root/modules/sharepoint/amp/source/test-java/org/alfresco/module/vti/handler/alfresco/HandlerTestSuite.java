package org.alfresco.module.vti.handler.alfresco;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite for running all the test classes in this package.
 * 
 * @author Matt Ward
 */
@RunWith(Suite.class)
@SuiteClasses({
    AlfrescoDwsServiceHandlerTest.class,
    AlfrescoListServiceHandlerTest.class,
    DefaultUrlHelperTest.class,
    VtiPathHelperTest.class,
    AlfrescoMethodHandlerTest.class
})
public class HandlerTestSuite
{
   // See @SuiteClasses
}
