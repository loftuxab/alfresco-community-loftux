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

import org.alfresco.connector.exception.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Authenticates credentials against a MediaWiki service.
 * 
 * The authentication handshake is described here:
 * http://www.mediawiki.org/wiki/API:Login
 * 
 * @author muzquiano
 */
public class MediaWikiAuthenticator extends AbstractAuthenticator
{
    public final static String CS_PARAM_LGUSERID = "lguserid";
    public final static String CS_PARAM_LGUSERNAME = "lgusername";
    public final static String CS_PARAM_LGTOKEN = "lgtoken";
    public final static String CS_PARAM_COOKIEPREFIX = "cookieprefix";
    protected static Log logger = LogFactory.getLog(MediaWikiAuthenticator.class);
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Authenticator#authenticate(java.lang.String, org.alfresco.connector.Credentials, org.alfresco.connector.ConnectorSession)
     */
    public ConnectorSession authenticate(String endpoint, Credentials credentials, ConnectorSession connectorSession)
            throws AuthenticationException
    {
        ConnectorSession cs = null;
        
        if(credentials != null)
        {        
            // build a new remote client
            RemoteClient remoteClient = new RemoteClient(endpoint);
    
            // call the login web script
            String user = (String) credentials.getProperty(Credentials.CREDENTIAL_USERNAME);
            String pass = (String) credentials.getProperty(Credentials.CREDENTIAL_PASSWORD);
            String url = "/w/api.php?action=login&lgname=" + user + "&lgpassword=" + pass + "&format=xml";
            
            if(logger.isDebugEnabled())
                logger.debug("MediaWiki authenticator url: " + url);
            
            Response response = remoteClient.call(url);
    
            if(logger.isDebugEnabled())
            {
                logger.debug("Heard back: " + response.getStatus().getCode());
            }
            
            
            // read back the ticket
            if (response.getStatus().getCode() == 200)
            {
                String responseText = response.getResponse();
                if(logger.isDebugEnabled())
                    logger.debug("Authenticate response text: " + responseText);
                
                // read out the headers
                try
                {
                    // get the root element
                    Element api = DocumentHelper.parseText(responseText).getRootElement();
                    if(api != null)
                    {
                        // get the login element
                        Element login = api.element("login");
                        if(login != null)
                        {
                            String result = (String) login.attributeValue("result");
                            if("Success".equalsIgnoreCase(result))
                            {
                                // the login was successful
                                // so pull back auth cookies
                                this.processResponse(response, connectorSession);
    
                                // values we get back in the xml payload
                                String lguserid = login.attributeValue("lguserid");
                                String lgusername = login.attributeValue("lgusername");
                                String lgtoken = login.attributeValue("lgtoken");
                                String cookieprefix = login.attributeValue("cookieprefix");
    
                                // determine the prefix by hand if we don't get it back
                                if(cookieprefix == null)
                                {
                                    // set username and userid cookies by hand
                                    String[] cookieNames = connectorSession.getCookieNames();
                                    for(int z = 0; z < cookieNames.length; z++)
                                    {
                                        if(cookieNames[z].endsWith("Token"))
                                        {
                                            cookieprefix = cookieNames[z].substring(0, cookieNames[z].length() - 5); 
                                        }
                                    }
                                }
                                
                                // set the userid and username by hand
                                connectorSession.setCookie(cookieprefix+"UserID", lguserid);
                                connectorSession.setCookie(cookieprefix+"UserName", lgusername);
                                
                                // store values back
                                connectorSession.setParameter(CS_PARAM_LGUSERID, lguserid);
                                connectorSession.setParameter(CS_PARAM_LGUSERNAME, lgusername);
                                connectorSession.setParameter(CS_PARAM_LGTOKEN, lgtoken);
                                connectorSession.setParameter(CS_PARAM_COOKIEPREFIX, cookieprefix);
                                
                                // signal that this succeeded
                                cs = connectorSession;                            
                            }
                            else
                            {
                                if(logger.isDebugEnabled())
                                    logger.debug("MediaWiki authenticator heard result: " + result);
                            }
                        }
                    }
                }
                catch (DocumentException de)
                {
                    // the ticket that came back was unparseable or invalid
                    // this will cause the entire handshake to fail
                    throw new AuthenticationException(
                            "Unable to retrieve ticket from MediaWiki", de);
                }
            }
        }
        
        return cs;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractAuthenticator#isAuthenticated(java.lang.String, org.alfresco.connector.ConnectorSession)
     */
    public boolean isAuthenticated(String endpoint, ConnectorSession connectorSession)
    {
        String lguserid = connectorSession.getParameter(CS_PARAM_LGUSERID);
        String lgusername = connectorSession.getParameter(CS_PARAM_LGUSERNAME);
        String lgtoken = connectorSession.getParameter(CS_PARAM_LGTOKEN);
        
        // if we have these cookies, then assume we're good to go
        return(lguserid != null && lgusername != null && lgtoken != null);
    }
}
