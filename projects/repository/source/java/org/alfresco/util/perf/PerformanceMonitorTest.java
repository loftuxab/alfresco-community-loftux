package org.alfresco.util.perf;

import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * Enabled vm performance monitoring for <b>performance.summary.vm</b> and
 * <b>performance.PerformanceMonitorTest</b> to check.
 * 
 * @see org.alfresco.util.perf.PerformanceMonitor
 * 
 * @author Derek Hulley
 */
public class PerformanceMonitorTest extends TestCase
{
    private PerformanceMonitor testTimingMonitor;
    
    @Override
    public void setUp() throws Exception
    {
        Method testTimingMethod = PerformanceMonitorTest.class.getMethod("testTiming");
        testTimingMonitor = new PerformanceMonitor("PerformanceMonitorTest", "testTiming");
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull(testTimingMonitor);
    }
    
    public synchronized void testTiming() throws Exception
    {
        testTimingMonitor.start();
        
        wait(50);
        
        testTimingMonitor.stop();
    }
}
