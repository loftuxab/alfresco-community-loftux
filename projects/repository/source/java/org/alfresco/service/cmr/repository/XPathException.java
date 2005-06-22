/*
 * Created on 31-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.service.cmr.repository;

import org.alfresco.error.AlfrescoRuntimeException;

public class XPathException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 3544955454552815923L;

    public XPathException(String msg)
    {
        super(msg);
    }

    public XPathException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
