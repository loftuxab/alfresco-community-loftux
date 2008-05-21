/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
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
 * Credentials are stored into a container called MediaWikiHeaders 
 * which is stored in the credential vault using the 
 * CREDENTIAL_WIKI_HEADERS header.
 * 
 * The authentication handshake is described here:
 * http://www.mediawiki.org/wiki/API:Login
 * 
 * @author muzquiano
 */
public class MediaWikiAuthenticator implements Authenticator
{
    protected static Log logger = LogFactory.getLog(MediaWikiAuthenticator.class);
    
    public final static String CREDENTIAL_WIKI_USERNAME = "mediaWikiUsername";
    public final static String CREDENTIAL_WIKI_PASSWORD = "mediaWikiPassword";
    public final static String CREDENTIAL_WIKI_HEADERS = "mediaWikiHeaders";
        
    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.connector.Authenticator#authenticate(org.alfresco.connector.Client,
     *      org.alfresco.connector.Credentials)
     */
    public boolean authenticate(Client client, Credentials credentials)
            throws AuthenticationException
    {
        if(logger.isDebugEnabled())
            logger.debug("Authenticate start");
        
        boolean authenticated = false;

        if (client instanceof RemoteClient)
        {
            // set up the remote client
            RemoteClient remoteClient = (RemoteClient) client;
            remoteClient.setTicket(null);
            remoteClient.setUsernamePassword(null, null);

            // call the login web script
            String user = (String) credentials.getProperty(CREDENTIAL_WIKI_USERNAME);
            String pass = (String) credentials.getProperty(CREDENTIAL_WIKI_PASSWORD);
                        
            String url = "/w/api.php?action=login&lgname=" + user + "&lgpassword=" + pass + "&format=xml";            
            Response response = remoteClient.call(url);
            
            if(logger.isDebugEnabled())
                logger.debug("Authenticate response code: " + response.getStatus().getCode());

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

                            if(logger.isDebugEnabled())
                                logger.debug("MediaWiki authenticator received result: " + result);

                            if("Success".equalsIgnoreCase(result))
                            {
                                MediaWikiHeaders headers = new MediaWikiHeaders(login);
                                
                                // store credentials back into vault
                                credentials.setProperty(CREDENTIAL_WIKI_HEADERS, headers);
                                authenticated = true;
                            }
                            else if("Illegal".equalsIgnoreCase(result))
                            {
                                // TODO
                            }
                            else if("NotExists".equalsIgnoreCase(result))
                            {
                                // TODO
                            }
                            else if("EmptyPass".equalsIgnoreCase(result))
                            {
                                // TODO
                            }
                            else if("WrongPass".equalsIgnoreCase(result))
                            {
                                // TODO
                            }
                            else if("WrongPluginPass".equalsIgnoreCase(result))
                            {
                                // TODO
                            }
                            else if("CreateBlocked".equalsIgnoreCase(result))
                            {
                                // TODO
                            }
                            else if("NeedToWait".equalsIgnoreCase(result))
                            {
                                // TODO
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

        return authenticated;
    }
}
