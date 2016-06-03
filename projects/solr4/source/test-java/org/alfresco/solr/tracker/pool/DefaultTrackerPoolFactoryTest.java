package org.alfresco.solr.tracker.pool;

import static org.junit.Assert.*;

import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link DefaultTrackerPoolFactory}.
 * 
 * @author Matt Ward
 */
public class DefaultTrackerPoolFactoryTest
{
    private DefaultTrackerPoolFactory poolFactory;
    private Properties properties;

    @Before
    public void setup()
    {
        poolFactory = null; // Ensure we don't accidentally reuse between runs.
        properties = new Properties();        
    }
    
    @Test
    public void testDefaults()
    {
        poolFactory = new DefaultTrackerPoolFactory(properties, "TheCore", "TrackerName");
        
        ThreadPoolExecutor tpe = poolFactory.create();
        
        assertEquals(3, tpe.getCorePoolSize());
        assertEquals(3, tpe.getMaximumPoolSize());
        assertEquals(120, tpe.getKeepAliveTime(TimeUnit.SECONDS));
    }
    
    @Test
    public void testNonDefaultProperties()
    {
        properties.put("alfresco.corePoolSize", "30");
        properties.put("alfresco.maximumPoolSize", "40");
        properties.put("alfresco.keepAliveTime", "200");
        
        poolFactory = new DefaultTrackerPoolFactory(properties, "TheCore", "TrackerName");
        
        ThreadPoolExecutor tpe = poolFactory.create();
        
        assertEquals(30, tpe.getCorePoolSize());
        assertEquals(40, tpe.getMaximumPoolSize());
        assertEquals(200, tpe.getKeepAliveTime(TimeUnit.SECONDS));
    }
}
