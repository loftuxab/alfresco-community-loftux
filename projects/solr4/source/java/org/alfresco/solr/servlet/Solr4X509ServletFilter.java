/*
* Copyright (C) 2005-2013 Alfresco Software Limited.
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

package org.alfresco.solr.servlet;

import javax.servlet.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.core.SolrResourceLoader;
import org.alfresco.web.scripts.servlet.X509ServletFilterBase;

/**
 * The Solr4X509ServletFilter implements the checkEnforce method of the X509ServletFilterBase.
 * This allows the configuration of X509 authentication to be toggled on/off through a
 * configuration outside of the web.xml.
 **/

public class Solr4X509ServletFilter extends X509ServletFilterBase
{

    private static final String SECURE_COMMS = "alfresco.secureComms";

    private static Log logger = LogFactory.getLog(Solr4X509ServletFilter.class);

    @Override
    protected boolean checkEnforce(ServletContext context) throws IOException
    {

        /*
        * Rely on the SolrResourceLoader to locate the solr home directory.
        */

        String solrHome = SolrResourceLoader.locateSolrHome();

        if(logger.isDebugEnabled())
        {
            logger.debug("solrHome:"+solrHome);
        }

        Properties props = new Properties();
        FileReader propReader = null;
        try
        {

            /*
            * Load solrcore.properies file from the proper location based on the solrHome.
            */
            propReader = new FileReader(solrHome+File.separator+"workspace-SpacesStore"+File.separator+"conf"+File.separator+"solrcore.properties");
            props.load(propReader);
            String prop = props.getProperty(SECURE_COMMS);

            if(logger.isDebugEnabled())
            {
                logger.debug("secureComms:"+prop);
            }

            /*
            * Return true or false based on the property. This will switch on/off X509 enforcement in the X509ServletFilterBase.
            */

            if (prop == null || "none".equals(prop))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        finally
        {
            if(propReader != null)
            {
                propReader.close();
            }
        }
    }
}