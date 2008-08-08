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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.vti.httpconnector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
*
* @author Stas Sokolovsky
*
*/
public class VtiSessionManager extends Thread {

    private int sessionGuidLength;
    
    private long sessionsInspectTime;
    
    private long timeToLeave;
    
    private Map<String, Object[]> sessions = new HashMap<String, Object[]>();
    
    private ReadWriteLock sessionsLock = new ReentrantReadWriteLock();
    
    public static final String VTI_SESSION_NAME = "VTISESSION";
    
    public Map<String, Object> createSession(HttpServletRequest request, HttpServletResponse response) {
        String sessionGuid = createSession();
        setCookieValue(response, VTI_SESSION_NAME, sessionGuid);
        setCookieValue(request, VTI_SESSION_NAME, sessionGuid);
        return getSession(sessionGuid);
    }
    
    public Map<String, Object> getSession(HttpServletRequest request) {
        String sessionGuid = getCookieValue(request, VTI_SESSION_NAME);
        if (sessionGuid == null) {
           return null; 
        } else {
           return getSession(sessionGuid); 
        }        
    }
    
    protected String createSession() {
        String sessionGuid = getUniqueRandomString(sessionGuidLength);
        try {
            sessionsLock.writeLock().lock();            
            sessions.put(sessionGuid, new Object[] {                                       
                                      new AtomicLong(System.currentTimeMillis()),
                                      Collections.synchronizedMap(new HashMap<String, Object>())
            }
            );            
        } finally {
            sessionsLock.writeLock().unlock();
        }
        return sessionGuid;     
    }
    
    @SuppressWarnings("unchecked")
    protected Map<String, Object> getSession(String sessionGuid) {
        Map<String, Object> result = null;
        try {
            sessionsLock.readLock().lock(); 
            Object[] session = sessions.get(sessionGuid);
            if (session != null) {
                ((AtomicLong)session[0]).set(System.currentTimeMillis());
                result = (Map<String, Object>)session[1];
            }
        } finally {
            sessionsLock.readLock().unlock();
        }
        return result;
    }
    
    public void run() {
    
        while (true) {
            try {
                sleep(sessionsInspectTime);
            } catch (InterruptedException e) {
            }
            try {
                sessionsLock.writeLock().lock();            
                Set<Entry<String, Object[]>> enties = sessions.entrySet();
                List<String> toRemove = new ArrayList<String>();
                long currentTime = System.currentTimeMillis();
                for (Entry<String, Object[]> entry : enties) {
                    long lastUsage = (Long)entry.getValue()[0];
                    if ((currentTime - lastUsage) > timeToLeave) {
                        toRemove.add(entry.getKey());
                    }
                }
                for (String guidToRemove : toRemove) {
                    sessions.remove(guidToRemove);
                }
            } finally {
                sessionsLock.writeLock().unlock();
            }
        }
        
    }
    
    private String getUniqueRandomString(int length) {
        return UUID.randomUUID().toString();
    }
    
    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        String result = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return result;
    }
    
    private void setCookieValue(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
    
    private String setCookieValue(HttpServletRequest request, String name, String value) {
        Cookie[] cookies = request.getCookies();
        String result = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setValue(value);
                }
            }
        }
        return result;
    }

    public int getSessionGuidLength() {
        return sessionGuidLength;
    }

    public void setSessionGuidLength(int sessionGuidLength) {
        this.sessionGuidLength = sessionGuidLength;
    }

    public long getSessionsInspectTime() {
        return sessionsInspectTime;
    }

    public void setSessionsInspectTime(long sessionsInspectTime) {
        this.sessionsInspectTime = sessionsInspectTime;
    }

    public long getTimeToLeave() {
        return timeToLeave;
    }

    public void setTimeToLeave(long timeToLeave) {
        this.timeToLeave = timeToLeave;
    }
    
}

