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
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import org.w3c.dom.Element;

/**
 * Works around http://jira.springframework.org/browse/SPR-6411.
 */
public class FixedClassPathXmlApplicationContext extends ClassPathXmlApplicationContext
{
    
    public FixedClassPathXmlApplicationContext()
    {
        super();
    }

    public FixedClassPathXmlApplicationContext(ApplicationContext parent)
    {
        super(parent);
    }

    public FixedClassPathXmlApplicationContext(String path, Class<?> clazz) throws BeansException
    {
        super(path, clazz);
    }

    public FixedClassPathXmlApplicationContext(String configLocation) throws BeansException
    {
        super(configLocation);
    }

    public FixedClassPathXmlApplicationContext(String[] configLocations, ApplicationContext parent)
            throws BeansException
    {
        super(configLocations, parent);
    }

    public FixedClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
            throws BeansException
    {
        super(configLocations, refresh, parent);
    }

    public FixedClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException
    {
        super(configLocations, refresh);
    }

    public FixedClassPathXmlApplicationContext(String[] paths, Class<?> clazz, ApplicationContext parent)
            throws BeansException
    {
        super(paths, clazz, parent);
    }

    public FixedClassPathXmlApplicationContext(String[] paths, Class<?> clazz) throws BeansException
    {
        super(paths, clazz);
    }

    public FixedClassPathXmlApplicationContext(String[] configLocations) throws BeansException
    {
        super(configLocations);
    }

    /* (non-Javadoc)
     * @see org.springframework.context.support.AbstractXmlApplicationContext#initBeanDefinitionReader(org.springframework.beans.factory.xml.XmlBeanDefinitionReader)
     */
    @Override
    protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader)
    {
        super.initBeanDefinitionReader(reader);
        reader.setDocumentReaderClass(FixedClassPathXmlApplicationContext.FixedBeanDefinitionDocumentReader.class);
    }

    public static class FixedBeanDefinitionDocumentReader extends DefaultBeanDefinitionDocumentReader
    {
    
        /*
         * (non-Javadoc)
         * @see
         * org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#importBeanDefinitionResource(org
         * .w3c.dom.Element)
         */
        @Override
        protected void importBeanDefinitionResource(Element ele)
        {
            String location = ele.getAttribute(DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE);
            if (!StringUtils.hasText(location))
            {
                getReaderContext().error("Resource location must not be empty", ele);
                return;
            }
    
            // Resolve system properties: e.g. "${user.dir}"
            location = SystemPropertyUtils.resolvePlaceholders(location);
    
            Set<Resource> actualResources = new LinkedHashSet<Resource>(4);
    
            // Discover whether the location is an absolute or relative URI
            boolean absoluteLocation = false;
    
            try
            {
                absoluteLocation = location.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)
                        || ResourceUtils.toURI(location).isAbsolute();
            }
            catch (Exception ex)
            {
                // cannot convert to an URI, considering the location relative
            }
    
            // check the
            if (absoluteLocation)
            {
                try
                {
                    int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
                    if (this.logger.isDebugEnabled())
                    {
                        this.logger.debug("Imported " + importCount + " bean definitions from URL location ["
                                + location + "]");
                    }
                }
                catch (BeanDefinitionStoreException ex)
                {
                    getReaderContext().error("Failed to import bean definitions from URL location [" + location + "]",
                            ele, ex);
                }
            }
            else
            {
                // No URL -> considering resource location as relative to the current file.
                try
                {
                    String baseLocation = getReaderContext().getResource().getURL().toString();
                    int importCount = getReaderContext().getReader().loadBeanDefinitions(
                            StringUtils.applyRelativePath(baseLocation, location), actualResources);
                    if (this.logger.isDebugEnabled())
                    {
                        this.logger.debug("Imported " + importCount + " bean definitions from relative location ["
                                + location + "]");
                    }
                }
                catch (IOException ex)
                {
                    getReaderContext().error("Failed to resolve current resource location", ele, ex);
                }
                catch (BeanDefinitionStoreException ex)
                {
                    getReaderContext().error(
                            "Failed to import bean definitions from relative location [" + location + "]", ele, ex);
                }
            }
            Resource[] actResArray = actualResources.toArray(new Resource[actualResources.size()]);
            getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
        }    
    }
}