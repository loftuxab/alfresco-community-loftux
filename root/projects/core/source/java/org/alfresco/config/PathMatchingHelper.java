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
import org.springframework.util.PathMatcher;

/**
 * An interface for plug ins to {@link JBossEnabledResourcePatternResolver} that avoids direct dependencies on
 * application server specifics.
 * 
 * @author dward
 */
public interface PathMatchingHelper
{
    /**
     * Indicates whether this helper is capable of searching the given URL (i.e. its protocol is supported).
     * 
     * @param rootURL
     *            the root url to be searched
     * @return <code>true</code> if this helper is capable of searching the given URL
     */
    public boolean canHandle(URL rootURL);

    /**
     * Gets the resource at the given URL.
     * 
     * @param url
     * @return the resource at the given URL
     * @throws IOException
     *             for any error
     */
    public Resource getResource(URL url) throws IOException;

    /**
     * Gets the set of resources under the given URL whose path matches the given sub pattern.
     * 
     * @param matcher
     *            the matcher
     * @param rootURL
     *            the root URL to be searched
     * @param subPattern
     *            the ant-style pattern to match
     * @return the set of matching resources
     * @throws IOException
     *             for any error
     */
    public Set<Resource> getResources(PathMatcher matcher, URL rootURL, String subPattern) throws IOException;
}