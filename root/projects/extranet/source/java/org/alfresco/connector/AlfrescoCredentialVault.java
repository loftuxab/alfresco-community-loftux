/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.connector;

import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.tools.ConnectorContextUtil;
import org.alfresco.web.config.RemoteConfigElement.CredentialVaultDescriptor;
import org.alfresco.web.site.FrameworkHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * A credential vault implementation that persists its credential state
 * into an Alfresco implementation.
 * 
 * @author muzquiano
 */
public class AlfrescoCredentialVault extends SecureXMLCredentialVault
{
    private static Log logger = LogFactory.getLog(AlfrescoCredentialVault.class);

    public AlfrescoCredentialVault(String id, CredentialVaultDescriptor descriptor)
    {
        super(id, descriptor);
    }
                
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#load()
     */
    public boolean load()
    {
        boolean success = false;
        
        ApplicationContext applicationContext = FrameworkHelper.getApplicationContext();
        ConnectorService connectorService = (ConnectorService) applicationContext.getBean("connector.service");
        Connector connector = null;
        try
        {
            connector = connectorService.getConnector("alfresco-system");
            
            Response response = connector.call("/extranet/credentialvault?id=" + this.id);
            if(response.getStatus().getCode() == 200)
            {
                String xml = response.getResponse();
                
                // deserialize
                deserialize(xml);
                
                // mark that the load succeeded
                success = true;
            }
        }
        catch(RemoteConfigException rce)
        {
            rce.printStackTrace();
        }
        
        return success;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#save()
     */
    public boolean save()
    {
        boolean success = false;
        
        // build the xml
        String xml = serialize();

        // connect
        ApplicationContext applicationContext = FrameworkHelper.getApplicationContext();
        ConnectorService connectorService = (ConnectorService) applicationContext.getBean("connector.service");
        Connector connector = null;
        try
        {
            connector = connectorService.getConnector("alfresco-system");
                                    
            // build post data
            String encodedXml = ConnectorContextUtil.formUrlEncode(xml);
            
            // uri
            String uri = "/extranet/credentialvault?id=" + this.id + "&xml=" + encodedXml;
            
            // build the connector context
            ConnectorContext connectorContext = new ConnectorContext();
            connectorContext.setMethod(HttpMethod.POST);
            connectorContext.setContentType("text/plain");
            Response response = connector.call(uri, connectorContext);
            if(response.getStatus().getCode() != 200)
            {
                throw new Exception("Unable to save vault, code: " + response.getStatus().getCode() + ", response: " + response.getResponse());
            }
            
            // mark that the save succeeded
            success = true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        return success;
    }
            
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "AlfrescoCredentialVault - " + credentialsMap.toString();
    }        
}
