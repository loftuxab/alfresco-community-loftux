/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client;

import java.util.Collection;

/**
 * Web Site Service Interface
 * 
 * @author Roy Wetherall
 * @author Brian Remmington
 */
public abstract class WebSiteService
{
    private static ThreadLocal<WebSite> currentWebsite = new ThreadLocal<WebSite>();

    /**
     * Gets all the web sites hosted on the repository
     * 
     * @return Collection<WebSite> web sites
     */
    public abstract Collection<WebSite> getWebSites();

    /**
     * Gets the web site that relates to the host name and port.
     * 
     * @param hostName
     *            host name
     * @param hostPort
     *            port number
     * @return WebSite web site, null if non found
     */
    public abstract WebSite getWebSite(String hostName, int hostPort);

    /**
     * Set the supplied website in a thread-local container to make it available
     * for all activity that subsequently takes place on the current thread
     * 
     * @param website
     */
    public static void setThreadWebSite(WebSite website)
    {
        currentWebsite.set(website);
    }

    /**
     * Retrieve the WebSite object that has most recently been set on the
     * current thread via a call to {@link #setThreadWebSite(WebSite)}.
     * 
     * @return The WebSite object most recently set on this thread or null if no
     *         object has been set.
     */
    public static WebSite getThreadWebSite()
    {
        return currentWebsite.get();
    }
}