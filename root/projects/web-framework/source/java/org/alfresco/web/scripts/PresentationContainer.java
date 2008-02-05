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
import java.util.HashMap;
import java.util.Map;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;


/**
 * Presentation (web tier) Web Script Container
 * 
 * @author davidc
 */
public class PresentationContainer extends AbstractRuntimeContainer
{
	/* (non-Javadoc)
	 * @see org.alfresco.web.scripts.RuntimeContainer#executeScript(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse, org.alfresco.web.scripts.Authenticator)
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
        return new PresentationServerModel();
    }
    
    /**
     * Build the script root objects - add the ScriptRemote for remote HTTP API calls.
     */
    @Override
    public Map<String, Object> getScriptParameters()
    {
       // NOTE: returns unmodifable map from super
       Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
       params.putAll(super.getScriptParameters());
       
       // retrieve remote server configuration 
       Config config = this.configService.getConfig("Remote");
       if (config != null)
       {
           ConfigElement remoteConfig = (ConfigElement)config.getConfigElement("remote");
           String endpoint = remoteConfig.getChild("endpoint").getValue();
           
           // use appropriate webscript servlet here - one that supports TICKET param auth
           ScriptRemote remote = new ScriptRemote(endpoint + "/service", "UTF-8");
           
           //
           // TODO: remove this block - for testing only!
           //
           if (remoteConfig.getChild("username") != null && remoteConfig.getChild("password") != null)
           {
               remote.setUsernamePassword(
                    remoteConfig.getChild("username").getValue(),
                    remoteConfig.getChild("password").getValue());
           } 
           
           params.put("remote", remote);
       }
       
       return params;
    }


    /**
     * Presentation Tier Model
     *
     * TODO: Implement when versioning meta-data is applied to all .jars
	  *
     * @author davidc
     */
    private class PresentationServerModel implements ServerModel
    {
        public String getContainerName()
        {
            return getName();
        }

        public String getEdition()
        {
            return UNKNOWN;
        }

        public int getSchema()
        {
            return -1;
        }

        public String getVersion()
        {
            return UNKNOWN;
        }

        public String getVersionBuild()
        {
            return UNKNOWN;
        }

        public String getVersionLabel()
        {
            return UNKNOWN;
        }

        public String getVersionMajor()
        {
            return UNKNOWN;
        }

        public String getVersionMinor()
        {
            return UNKNOWN;
        }

        public String getVersionRevision()
        {
            return UNKNOWN;
        }
        
        private final static String UNKNOWN = "<unknown>"; 
    }
}
