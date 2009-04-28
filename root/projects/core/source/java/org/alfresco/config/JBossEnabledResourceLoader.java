/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.config;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.PathMatcher;

/**
 * A ResourceLoader capable of handling JBoss VFS resources. Wraps a regular resource loader and intercepts those
 * resources whose URLs have vfs schemes. JBoss specific functionality is provided by {@link JBossVFSHelper},
 * constructed by reflection to avoid direct JBoss dependencies.
 * 
 * @author dward
 */
public class JBossEnabledResourceLoader implements ResourceLoader, PathMatchingHelper
{

    /** The wrapped resource loader. */
    private final ResourceLoader wrapped;

    /** The helper. */
    private final PathMatchingHelper helper;

    /**
     * The Constructor.
     * 
     * @param wrapped
     *            the resource loader to be wrapped
     */
    public JBossEnabledResourceLoader(ResourceLoader wrapped)
    {
        super();
        this.wrapped = wrapped;
        // Attempt to construct the helper. This will fail if JBoss classes aren't on the classpath
        PathMatchingHelper helper = null;
        try
        {
            helper = (PathMatchingHelper) Class.forName("org.alfresco.config.JBossVFSHelper").newInstance();
        }
        catch (Throwable e)
        {
            // ignore and assume non-JBoss server
        }
        this.helper = helper;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.core.io.ResourceLoader#getClassLoader()
     */
    public ClassLoader getClassLoader()
    {
        return this.wrapped.getClassLoader();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.core.io.ResourceLoader#getResource(java.lang.String)
     */
    public Resource getResource(String location)
    {
        Resource result = this.wrapped.getResource(location);
        try
        {
            if (canHandle(result.getURL()))
            {
                return getResource(result.getURL());
            }
        }
        catch (IOException e)
        {
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.config.PathMatchingHelper#canHandle(java.net.URL)
     */
    public boolean canHandle(URL rootURL)
    {
        return this.helper != null && this.helper.canHandle(rootURL);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.config.PathMatchingHelper#getResource(java.net.URL)
     */
    public Resource getResource(URL url) throws IOException
    {
        if (this.helper == null)
        {
            throw new IOException("No helper available");
        }
        return this.helper.getResource(url);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.config.PathMatchingHelper#getResources(org.springframework.util.PathMatcher, java.net.URL,
     * java.lang.String)
     */
    public Set<Resource> getResources(PathMatcher matcher, URL rootURL, String subPattern) throws IOException
    {
        if (this.helper == null)
        {
            throw new IOException("No helper available");
        }
        return this.helper.getResources(matcher, rootURL, subPattern);
    }
}
