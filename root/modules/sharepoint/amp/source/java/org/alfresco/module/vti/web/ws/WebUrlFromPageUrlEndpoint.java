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
package org.alfresco.module.vti.web.ws;

import java.net.URLDecoder;

import org.alfresco.module.vti.handler.MethodHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling WebUrlFromPageUrl method from webs web service
 *
 * @author PavelYur
 */
public class WebUrlFromPageUrlEndpoint extends AbstractEndpoint
{

    private static Log logger = LogFactory.getLog(WebUrlFromPageUrlEndpoint.class);
    
    // handler that provides methods for operating with documents and folders
    private MethodHandler handler;    

    // xml namespace prefix
    private static String prefix = "webs";

    /**
     * constructor
     *
     * @param handler that provides methods for operating with documents and folders
     */
    public WebUrlFromPageUrlEndpoint(MethodHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Retrieves url of the document workspace site from the document url
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is started.");
        
        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        // getting pageUrl parameter from request
        XPath xpath = new Dom4jXPath(buildXPath(prefix, "/WebUrlFromPageUrl/pageUrl"));
        xpath.setNamespaceContext(nc);
        String pageUrl = URLDecoder.decode(((Element) xpath.selectSingleNode(soapRequest.getDocument().getRootElement())).getTextTrim(), "UTF-8");        

        if (logger.isDebugEnabled())
            logger.debug("pageUrl parameter for this request: " + pageUrl);
        String server = getHost(soapRequest);
        String context = soapRequest.getAlfrescoContextName();
                
        String[] uris = handler.decomposeURL(pageUrl.replaceAll(server, ""), context);

        // creating soap response
        Element responsElement = soapResponse.getDocument().addElement("WebUrlFromPageUrlResponse", namespace);
        Element result = responsElement.addElement("WebUrlFromPageUrlResult");       
        result.setText(server + uris[0]);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Soap Method with name " + getName() + " is finished.");
        }
    }
}
