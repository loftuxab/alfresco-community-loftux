package com.activiti.util.perf;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An instance of this class keeps track of timings of method calls, logging either
 * after each invocation or only on VM shutdown or both.  Failed invocations are not
 * recorded.
 * <p>
 * Logging output is managed down to either the class or method level as follows:
 * <p>
 * <pre>
 *      performance.summary.method
 *      performance.summary.vm
 *          AND
 *      perf.targetClassName
 *      perf.targetClassName.methodName
 * </pre>
 * In order to activate the performance logging, DEBUG must be active for both the method
 * <b>AND</b> the PerformanceMonitorAdvice (<b>performance</b> logger).
 * <p>
 * The following examples illustrate how it can be used:
 * <p>
 * <pre>
 *      performance.summary.method=DEBUG
 *      performance.x.y.MyClass=DEBUG
 *          --> Output method statistic on each call to MyClass
 *          
 *      performance.summary.vm=DEBUG
 *      performance.x.y.MyClass.doSomething=DEBUG
 *          --> Output summary for doSomething() method of MyClass when VM terminates
 * 
 *      performance=DEBUG
 *          --> Output all performance data - after each call and upon VM closure          
 * </pre>
 * <p>
 * 
 * @author Derek Hulley
 */
public class PerformanceMonitorAdvice  implements MethodInterceptor
{
    private static final Log methodSummaryLogger = LogFactory.getLog("performance.summary.method");
    private static final Log vmSummaryLogger = LogFactory.getLog("performance.summary.vm");
    
    private SortedMap<Method, MethodStats> stats;

    public PerformanceMonitorAdvice()
    {
        Comparator<Method> methodComparator = new MethodComparator();
        stats = new TreeMap<Method, MethodStats>(methodComparator);
    }
    
    /**
     * Registers a VM shutdown hook that will dump the performance results gathered
     * if the correct debug level has been set
     */
    public void init()
    {
        if (vmSummaryLogger.isDebugEnabled())
        {
            Thread hook = new ShutdownThread();
            Runtime.getRuntime().addShutdownHook(hook);
        }
    }

    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        // bypass all recording if performance logging is not required
        if (methodSummaryLogger.isDebugEnabled() || vmSummaryLogger.isDebugEnabled())
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
        long start = System.currentTimeMillis();
        // execute - do not record exceptions
        Object ret = invocation.proceed();
        // get time after call
        long end = System.currentTimeMillis();
        // record the stats
        recordStats(invocation.getMethod(), (end - start));
        // done
        return ret;
    }
    
    /**
     * Dumps the results of the method execution to:
     * <ul>
     *   <li>DEBUG output if the method level debug logging is active</li>
     *   <li>Performance store if required</li>
     * </ul>
     * 
     * @param method the method against which to store the results
     * @param delayMs
     */
    private void recordStats(Method method, long delayMs)
    {
        String methodName = method.getName();
        String className = method.getDeclaringClass().getName();
        
        Log methodLogger = LogFactory.getLog("performance." + className + "." + methodName);
        if (!methodLogger.isDebugEnabled())
        {
            // no recording for this method
            return;
        }
        // must we log on a per-method call?
        if (methodSummaryLogger.isDebugEnabled())
        {
            methodLogger.debug("Executed " + className + "#" + methodName + " in " + delayMs + "ms");
        }
        if (vmSummaryLogger.isDebugEnabled())
        {
            synchronized(this)  // only synchronize if absolutely necessary
            {
                // get stats
                MethodStats methodStats = stats.get(method);
                if (methodStats == null)
                {
                    methodStats = new MethodStats();
                    stats.put(method, methodStats);
                }
                methodStats.record(delayMs);
            }
        }
    }
    
    /**
     * Used to sort <code>Method</code> instances
     */
    private class MethodComparator implements Comparator<Method>
    {
        /**
         * Compares methods by their <code>toString</code> values
         */
        public int compare(Method m1, Method m2)
        {
            String m1ClassName = m1.getDeclaringClass().getName();
            String m2ClassName = m2.getDeclaringClass().getName();
            int classNameCompare = m1ClassName.compareTo(m2ClassName);
            // first compare class names
            if (classNameCompare != 0)
            {
                return classNameCompare;
            }
            // compare method names next
            return m1.getName().compareTo(m2.getName());
        }
    }
    
    /**
     * Stores the execution count and total execution time for any method 
     */
    private class MethodStats
    {
        private int count;
        private long totalTimeMs;
        
        /**
         * Records the time for a method to execute and bumps up the execution count
         * 
         * @param delayMs the time the method took to execute in milliseconds
         */
        public void record(long delayMs)
        {
           count++;
           totalTimeMs += delayMs;
        }
        
        public String toString()
        {
            long averageMs = totalTimeMs / (long) count;
            return ("Executed " + count + " times, averaging " + averageMs + "ms per call");
        }
    }
    
    /**
     * Dumps the output of all recorded method statistics
     */
    private class ShutdownThread extends Thread
    {
        public void run()
        {
            Set<Method> methods = stats.keySet();
            for (Method method : methods)
            {
                vmSummaryLogger.debug("\nMethod performance summary: \n" +
                        "   Class: " + method.getDeclaringClass().getName() + "\n" +
                        "   Method: " + method.getName() + "\n" +
                        "   Statistics: " + stats.get(method));
            }
        }
    }
}
