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
package org.alfresco.module.vti.endpoints.dws;

import org.alfresco.module.vti.endpoints.EndpointUtils;
import org.alfresco.module.vti.endpoints.VtiEndpoint;
import org.alfresco.module.vti.handler.soap.DwsServiceHandler;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * @author PavelYur
 *
 */
public class RenameDwsEndpoint extends VtiEndpoint
{

    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * constructor
     *
     * @param handler
     */
    public RenameDwsEndpoint(DwsServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     *
     */
    @Override
    protected Element invokeInternal(Element element, Document document) throws Exception
    {

        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);

        // getting title parameter from request
        XPath titlePath = new Dom4jXPath(EndpointUtils.buildXPath(prefix, "/RenameDws/title"));
        titlePath.setNamespaceContext(nc);
        Element title = (Element) titlePath.selectSingleNode(element);

        // creating soap response
        Element root = document.addElement("RenameDwsResponse");
        Element renameDwsResult = root.addElement("RenameDwsResult", namespace);
        
        String dws = sessionManager.getSession(EndpointUtils.getRequest()).get(VtiEndpoint.DWS).toString();
        handler.renameDws(dws, title.getText());

        renameDwsResult.setText("<Result/>");

        return root;
    }

}
