/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.connector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The EndpointManager is responsible for maintaining connection timeout and connection
 * retry information for endpoints. It may be used by multiple Connector objects to
 * ensure that shared endpoints are not repeatedly connected to or waited on.
 * 
 * @author Kevin Roast
 */
public final class EndpointManager
{
    /** timeout value in milliseconds before a reconnection
        to a particular endpoint should be attempted */
    private static final int RECONNECT_TIMEOUT = 20000;
    
    /** conncurrent map of endpoint->timeout values */
    private static ConcurrentMap<String, Long> endpointTimeouts = new ConcurrentHashMap<String, Long>();
    
    
    /**
     * Private constructor
     */
    private EndpointManager()
    {
    }
    
    
    /**
     * Register an endpoint with the manager - the same endpoint can be registered
     * any number of times with side effects.
     * 
     * @param endpoint      The endpoint to register
     */
    public static void registerEndpoint(String endpoint)
    {
        endpointTimeouts.putIfAbsent(endpoint, 0L);
    }
    
    /**
     * Returns true if the connector should make a connection attempt to the specified
     * endpoint, false if the endpoint is still in the "wait" period between retries.
     * 
     * @param endpoint      The endpoint to test
     * 
     * @return true to allow connect, false otherwise
     */
    public static boolean allowConnect(String endpoint)
    {
        return (endpointTimeouts.get(endpoint) + RECONNECT_TIMEOUT < System.currentTimeMillis());
    }
    
    /**
     * Process the given response code for an endpoint - recording if that remote
     * connection is unavailable for a time. Returns true if further response
     * processing should continue, false otherwise.
     * 
     * @param endpoint      The endpoint to record code against
     * @param code          Response code
     * 
     * @return true if further processing should continue, false otherwise
     */
    public static boolean processResponseCode(String endpoint, int code)
    {
        boolean allowContinue = true;;
        
        if (RemoteClient.SC_REMOTE_CONN_NOHOST == code ||
            RemoteClient.SC_REMOTE_CONN_TIMEOUT == code)
        {
            // If special error codes were returned, don't check the remote connection
            // again for a short time. This is to ensure that if an endpoint is not
            // currently available, we don't continually connect+timeout potentially
            // 100's of times in a row therefore slowing the server startup etc. 
            endpointTimeouts.put(endpoint, System.currentTimeMillis());
            allowContinue = false;
        }
        
        return allowContinue;
    }
}