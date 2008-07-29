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

package org.alfresco.module.vti.endpoints;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.TransformerObjectSupport;

/**
 * Exception resolver for vti web services exceptions with error code
 *
 * @author Dmitry Lazurkin
 *
 */
public class ErrorCodeExceptionMappingResolver extends TransformerObjectSupport implements EndpointExceptionResolver, Ordered
{
    private final static Log logger = LogFactory.getLog("org.alfresco.module.vti.handler");

    private int order;

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    public boolean resolveException(MessageContext messageContext, Object endpoint, Exception ex)
    {
        Assert.isInstanceOf(SoapMessage.class, messageContext.getResponse(), "Requires a SoapMessage");
        Assert.isInstanceOf(VtiEndpoint.class, endpoint, "Requires a VtiEndpoint");

        if ((ex instanceof WebServiceErrorCodeException) == false)
        {
            return false;
        }

        WebServiceErrorCodeException errorCodeException = (WebServiceErrorCodeException) ex;
        VtiEndpoint vtiEndpoint = (VtiEndpoint) endpoint;

        if (logger.isDebugEnabled())
        {
            logger.debug("Web service exception with error code = " + errorCodeException.getErrorCode(), errorCodeException);
        }

        WebServiceMessage response = messageContext.getResponse();
        Document responseDocument = DocumentHelper.createDocument();

        try
        {
            // add result tag
            Element resultElement = responseDocument.addElement(EndpointUtils.getResponseTagName(vtiEndpoint.getName()), vtiEndpoint.getNamespace()).addElement(EndpointUtils.getResultTagName(vtiEndpoint.getName()));
            // add error tag
            resultElement.addElement("Error").addAttribute("ID", String.valueOf(errorCodeException.getErrorCode()));
            transform(new DocumentSource(responseDocument), response.getPayloadResult());

            return true;
        }
        catch (TransformerException e)
        {
            logger.error("Error while document transforming", e);

            return false;
        }
    }

}
