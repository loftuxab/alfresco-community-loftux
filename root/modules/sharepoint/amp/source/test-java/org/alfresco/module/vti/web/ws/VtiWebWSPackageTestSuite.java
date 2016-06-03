package org.alfresco.module.vti.web.ws;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
    TestAbstractListEndpointTest.class,
    TestAbstractEndpointTest.class,
    TestAbstractListItemsEndpointTest.class
})
public class VtiWebWSPackageTestSuite
{
}
