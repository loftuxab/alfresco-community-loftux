/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class that provides facilities for keeping track of the
 * rendering times of components, regions, templates and pages.
 * 
 * The framework will call into this class when execution of said
 * pieces.  If it is not enabled, then these calls will just NOP.
 * Otherwise, they will log using the Framework logger.
 * 
 * @author muzquiano
 */
public class Timer
{
    protected static String TIMER_KEY = "timer";
    protected static Log logger = LogFactory.getLog(Timer.class);
    
    /**
     * Either print out to the configured Timer logger
     * Or if they haven't set that up, just dump out to console
     */
    protected static void print(String value)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug(value);
        }
        else
        {
            System.out.println(value);
        }
    }
    
    /**
     * Determines whether the timer is enabled.
     * This is controlled via the configuration file and is false by default.
     * 
     * @return Whether the timer functionality is enabled or not
     */
    protected static boolean isTimerEnabled()
    {
        return Framework.getConfig().getDebugTimerEnabled();
    }
    
    /**
     * @return Whether to report in milliseconds (rather than nanoseconds)
     */
    protected static boolean showMilliseconds()
    {
        return true;
    }
    
    /**
     * Binds a timer container to the current context
     * This must be called at the top of the request execution chain
     * 
     * @param context The current request context
     */
    public static void bindTimer(ServletRequest request)
    {
        bindTimer(request, false);
    }
    
    public static void bindTimer(ServletRequest request, boolean forceNew)
    {
        if(isTimerEnabled())
        {
            Timer t = (Timer) request.getAttribute(TIMER_KEY);
            if(t == null || forceNew)
            {
                t = new Timer();
                request.setAttribute(TIMER_KEY, t);
            }
        }
    }
    
    /**
     * Releases the timer.  This should be called at the end of the
     * request processing chain
     * 
     * @param context The current request context
     */
    public static void unbindTimer(ServletRequest request)
    {
        if(isTimerEnabled())
        {
            request.removeAttribute(TIMER_KEY);
        }
    }
    
    /**
     * Begins timing for a specific block.  If timing has been captured
     * previously for this block id, it will be added upon
     * 
     * @param context The current request context
     * @param blockId The unique id of the block
     */
    public static void start(ServletRequest request, String blockId)
    {
        if(isTimerEnabled())
        {
            if(blockId == null)
            {
                return;
            }
            
            Timer t = (Timer) request.getAttribute(TIMER_KEY);
            if(t != null)
            {
                // check if this timer has not already been started
                if(t.startTimes.get(blockId) == null)
                {
                    t.keys.add(blockId);
                }
                
                Long l = new Long(System.nanoTime());
                t.startTimes.put(blockId, l);
            }
        }
    }
    
    public static void stop(ServletRequest request, String blockId)
    {
        if(isTimerEnabled())
        {
            long endTime = System.nanoTime();

            Timer t = (Timer) request.getAttribute(TIMER_KEY);
            if(t != null)
            {
                // start time
                Long startTime = (Long) t.startTimes.get(blockId);
                if(startTime != null)
                {
                    // execution time
                    long executionTime = endTime - startTime.longValue();
                    
                    // add to total time
                    Long totalTime = (Long) t.totalTimes.get(blockId);
                    if(totalTime == null)
                    {
                        totalTime = new Long(executionTime);
                    }
                    else
                    {
                        totalTime = new Long(totalTime.longValue() + executionTime);
                    }
                    
                    // store back
                    t.totalTimes.put(blockId, totalTime);
                }
            }
        }
    }
    
    /**
     * Writes to debug out the report for a single block id
     * 
     * @param context The current request context
     * @param blockId The block id to report on
     */
    public static void report(ServletRequest request, String blockId)
    {
        if(isTimerEnabled())
        {
            Timer t = (Timer) request.getAttribute(TIMER_KEY);
            if(t != null)
            {
                Long l = (Long) t.totalTimes.get(blockId);
                if(l != null)
                {
                    long value = (long) l.longValue();
                    String label = "ns";
                    
                    if(Timer.showMilliseconds())
                    {
                        value = (long)(value / 1000000);
                        label = "ms";
                    }

                    RequestContext context = RequestUtil.getRequestContext(request);
                    if(context == null)
                    {
                        print("[" + blockId + "] took " + value + " " + label);
                    }
                    else
                    {
                        print("[" + context.getId() + ":" + blockId + "] took " + value + " " + label);
                    }
                }
            }
        }        
    }
    
    public static void reportAll(ServletRequest request)
    {
        if(isTimerEnabled())
        {
            Timer t = (Timer) request.getAttribute(TIMER_KEY);
            if(t != null)
            {
                // report timing blocks in order they were received
                Iterator it = t.keys.iterator();
                while(it.hasNext())
                {
                    String blockId = (String) it.next();
                    report(request, blockId);
                }
            }
        }
    }
    
    public Timer()
    {
        this.totalTimes = new HashMap<String, Long>();
        this.startTimes = new HashMap<String, Long>();
        this.keys = new ArrayList<String>();
    }
    
    protected Map<String, Long> totalTimes;
    protected Map<String, Long> startTimes;
    protected List<String> keys;

}
