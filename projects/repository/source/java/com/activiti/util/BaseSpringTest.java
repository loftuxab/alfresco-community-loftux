package com.activiti.util;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * Base test class providing Hibernate sessions
 * 
 * @author derekh
 */
public abstract class BaseSpringTest extends AbstractTransactionalDataSourceSpringContextTests
{
    public BaseSpringTest()
    {
    }

    protected String[] getConfigLocations()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Getting config locations");
        }
        return new String[] { "classpath:applicationContext.xml" };
    }
}
