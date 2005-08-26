/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.config.source;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.alfresco.config.ConfigException;
import org.springframework.web.context.ServletContextAware;

/**
 * ConfigSource implementation that gets its data via files in a web
 * application.
 * 
 * TODO: Also deal with the source being specified as an init param i.e.
 * param:config.files
 * 
 * @author gavinc
 */
public class WebAppConfigSource extends BaseConfigSource implements ServletContextAware
{
    private ServletContext servletCtx;

    /**
     * Constructs a webapp configuration source that uses a single file
     * 
     * @param filename the name of the file from which to get config
     * 
     * @see WebAppConfigSource#WebAppConfigSource(List<String>)
     */
    public WebAppConfigSource(String filename)
    {
        this(Collections.singletonList(filename));
    }
    
    /**
     * @param sources
     *            List of paths to files in a web application
     */
    public WebAppConfigSource(List<String> sourceStrings)
    {
        super(sourceStrings);
    }

    /**
     * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext servletContext)
    {
        this.servletCtx = servletContext;
    }

    /**
     * @see org.alfresco.config.source.BaseConfigSource#getInputStream(java.lang.String)
     */
    public InputStream getInputStream(String sourceString)
    {
        InputStream is = null;

        try
        {
            String fullPath = this.servletCtx.getRealPath(sourceString);
            is = new BufferedInputStream(new FileInputStream(fullPath));
        } catch (IOException ioe)
        {
            throw new ConfigException("Failed to obtain input stream to file: " + sourceString,
                    ioe);
        }

        return is;
    }
}
