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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;


/**
 * VtiSoapResponse is wrapper for HttpServletResponse. It provides specific methods 
 * which allow to generate response for soap requests. 
 *  
 * @author Stas Sokolovsky
 *
 */
public class VtiSoapResponse extends HttpServletResponseWrapper
{
	
	public static final String NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    
	Document document;
    
	/**
     * Constructor
     * 
     * @param response HttpServletResponse 
     */
    public VtiSoapResponse(HttpServletResponse response)
    {
        super(response);
        document = DocumentHelper.createDocument();
        Element envelope = document.addElement(QName.get("Envelope", "s", VtiSoapResponse.NAMESPACE));
        envelope.addElement(QName.get("Body", "s", VtiSoapResponse.NAMESPACE));
    }
    
    /**
     * Get xml document that will be written to response
     *  
     * @return Element response xml document
     */
    public Element getDocument()
    {
        return (Element)document.getRootElement().elements().get(0);
    }
    
    /**
     * Write document to response
     *  
     * @return Document request xml document
     */
    @Override
    public void flushBuffer() throws IOException 
    {
        try
        {
            getOutputStream().write(document.asXML().getBytes());
            getOutputStream().close();
        }
        catch (Exception e)
        {
            // ignore
        }   
        
    }
    
}
