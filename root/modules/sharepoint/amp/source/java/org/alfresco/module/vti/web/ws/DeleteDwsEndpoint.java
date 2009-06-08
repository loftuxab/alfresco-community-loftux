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

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.alfresco.module.vti.handler.alfresco.AbstractAuthenticationHandler;
import org.alfresco.repo.SessionUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

/**
 * Class for handling DeleteDws soap method
 * 
 * @author PavelYur
 *
 */
public class DeleteDwsEndpoint extends AbstractEndpoint
{
	
	private final static Log logger = LogFactory.getLog(DeleteDwsEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;

    /**
     * constructor
     *
     * @param handler
     */
    public DeleteDwsEndpoint(DwsServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Deletes document workspace
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception   {
    	if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is started.");
    	}       
        
        handler.deleteDws(getDwsFromUri(soapRequest), (SessionUser) soapRequest.getSession().getAttribute(AbstractAuthenticationHandler.AUTHENTICATION_USER));      
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("DeleteDwsResponse");
        Element deleteDwsResult = root.addElement("DeleteDwsResult", namespace);
        
        deleteDwsResult.addElement("Result");
        
        if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}        
    }

}
