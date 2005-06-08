package org.alfresco.util.perf;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.vladium.utils.timing.ITimer;
import com.vladium.utils.timing.TimerFactory;

/**
 * An instance of this class keeps track of timings of method calls on a bean
 * 
 * @author Derek Hulley
 */
public class PerformanceMonitorAdvice extends AbstractPerformanceMonitor implements MethodInterceptor
{
    public PerformanceMonitorAdvice(String beanName)
    {
        super(beanName);
    }
    
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        // bypass all recording if performance logging is not required
        if (AbstractPerformanceMonitor.isDebugEnabled())
        {
            return invokeWithLogging(invocation);
        }
        else
        {
            // no logging required
            return invocation.proceed();
        }
    }
    
    private Object invokeWithLogging(MethodInvocation invocation) throws Throwable
    {
        // get the time prior to call
        ITimer timer = TimerFactory.newTimer ();
        
        timer.start ();

        //long start = System.currentTimeMillis();
        // execute - do not record exceptions
        Object ret = invocation.proceed();
        // get time after call
        //long end = System.currentTimeMillis();
        // record the stats
        timer.stop ();
       
        recordStats(invocation.getMethod().getName(),  timer.getDuration ());
        // done
        return ret;
    }
}
