package org.alfresco.util.debug;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class that optionally records coding issues at runtime and logs them when the VM
 * terminates.
 * <p>
 * Use it as follows in code:
 * <pre>
 *      ...
 *      // some code
 *      CodeMonkey.todo("Check that status is correct");
 *      // more code
 *      CodeMonkey.issue("Should we catch the runtime exceptions?");
 * </pre>
 * <p>
 * To enable the dump of <b>todo</b> and <b>issue</b> items when the VM terminates,
 * set the following log level:
 * <pre>
 *      org.alfresco.util.debug.CodeMonkey=DEBUG
 * </pre>
 * <p>
 * This code only adds a performance overhead if the log level is activated, but since
 * this is a DEBUG log level, it would never be active in a live system.
 * 
 * @author Derek Hulley
 */
public final class CodeMonkey
{
    private static final Log logger = LogFactory.getLog(CodeMonkey.class);
    
    private static Map<String, StackTraceElement> todos;
    private static Map<String, StackTraceElement> issues;
    
    static
    {
        todos = new HashMap<String, StackTraceElement>(17); 
        issues = new HashMap<String, StackTraceElement>(17); 
        // only do any work if debug is enabled in the first place
        if (logger.isDebugEnabled())
        {
            // register the shutdown hook
            Thread hook = new ShutdownThread();
            Runtime.getRuntime().addShutdownHook(hook);
        }
    }
    
    /** Purely static class */
    private CodeMonkey()
    {
    }
    
    public static void todo(String msg)
    {
        if (!logger.isDebugEnabled())
        {
            // no output will be required, so record nothing
            return;
        }
        addElement(msg, todos);
    }
    
    public static void issue(String msg)
    {
        if (!logger.isDebugEnabled())
        {
            // no output will be required, so record nothing
            return;
        }
        addElement(msg, issues);
    }
    
    private static void addElement(String msg, Map<String, StackTraceElement> elements)
    {
        // obtain lock around access to static map
        Lock lock = new ReentrantLock();
        try
        {
            lock.lock();
            if (elements.get(msg) != null)
            {
                // this message has been logged
                return;
            }
            // get the location string
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            // save second stack trace element
            elements.put(msg, stack[4]);
        }
        finally
        {
            lock.unlock();
        }
    }
    
    /**
     * Dumps the output of all recorded code issues
     */
    private static class ShutdownThread extends Thread
    {
        public void run()
        {
            // avoid doing anything if there are no issues or todos
            if (todos.size() == 0 && issues.size() == 0)
            {
                return;
            }
            StringBuilder sb = new StringBuilder(256);
            // get todos
            for (String msg : todos.keySet())
            {
                StackTraceElement st = todos.get(msg);
                sb.append("\n")
                  .append("TODO: ").append(msg)
                  .append("  <").append(st).append(">");
            }
            // get issues
            for (String msg : issues.keySet())
            {
                StackTraceElement st = issues.get(msg);
                sb.append("\n")
                  .append("ISSUE: ").append(msg)
                  .append("  <").append(st).append(">");
            }
            // dump
            logger.debug(sb);
        }
    }
}
