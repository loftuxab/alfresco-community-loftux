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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.ibatis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.extensions.surf.util.PropertyCheck;
import org.alfresco.util.resource.HierarchicalResourceLoader;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;
import org.springframework.util.ObjectUtils;

import com.ibatis.common.xml.NodeletException;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapConfigParser;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapParser;

/**
 * Extends Spring's support for iBatis by allowing a choice of {@link ResourceLoader}. The
 * {@link #setResourceLoader(HierarchicalResourceLoader) ResourceLoader} will be used to load the <b>SqlMapConfig</b>
 * file, but will also be injected into a {@link HierarchicalSqlMapConfigParser} that will read the individual iBatis
 * resources.
 * 
 * @author Derek Hulley
 * @since 3.2 (Mobile)
 */
public class HierarchicalSqlMapClientFactoryBean extends SqlMapClientFactoryBean
{
    private HierarchicalResourceLoader resourceLoader;

    /**
     * Default constructor
     */
    public HierarchicalSqlMapClientFactoryBean()
    {
    }

    /**
     * Set the resource loader to use. To use the <b>&#35;resource.dialect&#35</b> placeholder, use the
     * {@link HierarchicalResourceLoader}.
     * 
     * @param resourceLoader
     *            the resource loader to use
     */
    public void setResourceLoader(HierarchicalResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "resourceLoader", resourceLoader);
        super.afterPropertiesSet();
    }

    @Override
    protected SqlMapClient buildSqlMapClient(Resource[] configLocations, Resource[] mappingLocations,
            Properties properties) throws IOException
    {

        if (ObjectUtils.isEmpty(configLocations))
        {
            throw new IllegalArgumentException("At least 1 'configLocation' entry is required");
        }

        SqlMapClient client = null;
        HierarchicalSqlMapConfigParser configParser = new HierarchicalSqlMapConfigParser(resourceLoader);
        for (Resource configLocation : configLocations)
        {
            InputStream is = configLocation.getInputStream();
            try
            {
                client = properties == null ? configParser.parse(is) : configParser.parse(is, properties);
            }
            catch (RuntimeException ex)
            {
                throw new NestedIOException("Failed to parse config resource: " + configLocation, ex.getCause());
            }
        }

        if (mappingLocations != null)
        {
            SqlMapParser mapParser = new SqlMapParser(configParser.state);
            for (Resource mappingLocation : mappingLocations)
            {
                try
                {
                    mapParser.parse(mappingLocation.getInputStream());
                }
                catch (NodeletException ex)
                {
                    throw new NestedIOException("Failed to parse mapping resource: " + mappingLocation, ex);
                }
            }
        }

        return client;
    }
}
