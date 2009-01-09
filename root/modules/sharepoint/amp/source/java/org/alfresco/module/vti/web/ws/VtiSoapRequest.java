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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.alfresco.module.vti.web.VtiRequestDispatcher;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * VtiSoapRequest is wrapper for HttpServletRequest. It provides specific methods 
 * which allow to retrieve appropriate xml document from request data. 
 * 
 * @author Stas Sokolovsky
 *
 */
public class VtiSoapRequest extends HttpServletRequestWrapper
{

	private Document document;
    
	 /**
     * Constructor
     * 
     * @param request HttpServletRequest 
     */
    public VtiSoapRequest(HttpServletRequest request)
    {
        super(request);
        try
        {
            SAXReader reader = new SAXReader();
            reader.setValidation(false);
            document = reader.read(request.getInputStream());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get xml document
     *  
     * @return Document request xml document
     */
    public Document getDocument()
    {
        return document;
    }
    
    /**
     * Get alfresco context name
     *  
     * @return String alfresco context name
     */
    public String getAlfrescoContextName()
    {
        return (String) this.getAttribute(VtiRequestDispatcher.VTI_ALFRESCO_CONTEXT);
    }

        
}
