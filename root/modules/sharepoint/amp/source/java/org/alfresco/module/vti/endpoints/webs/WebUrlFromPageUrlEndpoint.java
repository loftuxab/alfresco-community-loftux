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
package org.alfresco.module.vti.endpoints.webs;

import java.net.URLDecoder;
import java.util.Map;

import org.alfresco.module.vti.endpoints.EndpointUtils;
import org.alfresco.module.vti.endpoints.VtiEndpoint;
import org.alfresco.module.vti.handler.VtiMethodHandler;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling WebUrlFromPageUrl method from webs web service
 *
 * @author PavelYur
 */
public class WebUrlFromPageUrlEndpoint extends VtiEndpoint
{

    // handler that provides methods for operating with documents and folders
    private VtiMethodHandler handler;

    // xml namespace prefix
    private static String prefix = "webs";

    /**
     * constructor
     *
     * @param handler that provides methods for operating with documents and folders
     */
    public WebUrlFromPageUrlEndpoint(VtiMethodHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Handle the WebUrlFromPageUrl method from webs web service
     *
     * @param element part of soap message from request that contains methods parameters
     * @param document soap response
     */
    @Override
    protected Element invokeInternal(Element element, Document document) throws Exception
    {
        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);

        // getting pageUrl parameter from request
        XPath xpath = new Dom4jXPath(EndpointUtils.buildXPath(prefix, "/WebUrlFromPageUrl/pageUrl"));
        xpath.setNamespaceContext(nc);
        String pageUrl = ((Element) xpath.selectSingleNode(element)).getTextTrim();        

        // recognizing the host
        String server = "http://" + EndpointUtils.getHost() + EndpointUtils.getContext();        
        Map<String, Object> session = sessionManager.getSession(EndpointUtils.getRequest());
        session.put(VtiEndpoint.DWS, URLDecoder.decode(pageUrl.substring(0, pageUrl.lastIndexOf('/')).replaceAll(server + "/", ""),"UTF-8"));

        // creating soap response
        Element responsElement = document.addElement("WebUrlFromPageUrlResponse", namespace);
        Element result = responsElement.addElement("WebUrlFromPageUrlResult");       
        result.setText(server);
        return responsElement;
    }

}
