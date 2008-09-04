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
package org.alfresco.web.scripts;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Presentation (web tier) Web Script Container
 * 
 * @author davidc
 */
public class PresentationContainer extends AbstractRuntimeContainer
{
    private static final Log logger = LogFactory.getLog(PresentationContainer.class);
    
	/* (non-Javadoc)
	 * @see org.alfresco.web.scripts.RuntimeContainer#executeScript(org.alfresco.web.scripts.WebScriptRequest,
     *      org.alfresco.web.scripts.WebScriptResponse, org.alfresco.web.scripts.Authenticator)
	 */
    public void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
        throws IOException
    {
        // TODO: Consider Web Tier Authentication
        WebScript script = scriptReq.getServiceMatch().getWebScript();
        script.execute(scriptReq, scriptRes);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getDescription()
     */
    public ServerModel getDescription()
    {
        Properties props = null;
        URL url = this.getClass().getClassLoader().getResource("version.properties");
        if (url != null)
        {
            try
            {
                props = new Properties();
                props.load(url.openStream());
            }
            catch (IOException err)
            {
                logger.warn("Failed to load version properties: " + err.getMessage(), err);
            }
        }
        return new PresentationServerModel(props);
    }
    
    /**
     * Presentation Tier Model
     *
     * @author davidc
     */
    private class PresentationServerModel implements ServerModel
    {
        private Properties props = null;
        private String version = null;
        
        public PresentationServerModel(Properties props)
        {
            this.props = props;
        }
        
        public String getContainerName()
        {
            return getName();
        }

        public String getId()
        {
            return UNKNOWN;
        }

        public String getName()
        {
            return UNKNOWN;
        }

        public String getEdition()
        {
            return (props != null ? props.getProperty("version.edition") : UNKNOWN);
        }

        public int getSchema()
        {
            return (props != null ? Integer.parseInt(props.getProperty("version.schema")) : -1);
        }

        public String getVersion()
        {
            if (this.version == null)
            {
                if (props != null)
                {
                    StringBuilder version = new StringBuilder(getVersionMajor());
                    version.append(".");
                    version.append(getVersionMinor());
                    version.append(".");
                    version.append(getVersionRevision());
                    
                    String label = getVersionLabel();
                    String build = getVersionBuild();
                    
                    boolean hasLabel = (label != null && label.length() > 0);
                    boolean hasBuild = (build != null && build.length() > 0);
                    
                    // add opening bracket if either a label or build number is present
                    if (hasLabel || hasBuild)
                    {
                       version.append(" (");
                    }
                    
                    // add label if present
                    if (hasLabel)
                    {
                       version.append(label);
                    }
                    
                    // add build number is present
                    if (hasBuild)
                    {
                       // if there is also a label we need a separating space
                       if (hasLabel)
                       {
                          version.append(" ");
                       }
                       
                       version.append(build);
                    }
                    
                    // add closing bracket if either a label or build number is present
                    if (hasLabel || hasBuild)
                    {
                       version.append(")");
                    }
                    
                    this.version = version.toString();
                }
                else
                {
                    this.version = UNKNOWN;
                }
            }
            return this.version;
        }

        public String getVersionBuild()
        {
            return (props != null ? props.getProperty("version.build") : UNKNOWN);
        }

        public String getVersionLabel()
        {
            return (props != null ? props.getProperty("version.label") : UNKNOWN);
        }

        public String getVersionMajor()
        {
            return (props != null ? props.getProperty("version.major") : UNKNOWN);
        }

        public String getVersionMinor()
        {
            return (props != null ? props.getProperty("version.minor") : UNKNOWN);
        }

        public String getVersionRevision()
        {
            return (props != null ? props.getProperty("version.revision") : UNKNOWN);
        }
        
        private final static String UNKNOWN = "<unknown>"; 
    }
}
