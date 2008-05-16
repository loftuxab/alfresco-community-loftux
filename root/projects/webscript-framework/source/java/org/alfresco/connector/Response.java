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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.alfresco.web.scripts.Status;

/**
 * Representation of the response from a remote HTTP API call.
 * 
 * @author Kevin Roast
 */
public class Response
{
    private String data;
    private InputStream is;
    private Status status;
    private String encoding = null;

    /**
     * Instantiates a new response.
     * 
     * @param status the status
     */
    Response(Status status)
    {
        this.status = status;
    }

    /**
     * Instantiates a new response.
     * 
     * @param data the data
     * @param status the status
     */
    Response(String data, Status status)
    {
        this.data = data;
        this.status = status;
    }

    /**
     * Instantiates a new response.
     * 
     * @param is the is
     * @param status the status
     */
    Response(InputStream is, Status status)
    {
        this.is = is;
        this.status = status;
    }

    /* package *//**
                 * Sets the encoding.
                 * 
                 * @param encoding the new encoding
                 */
    void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Gets the response.
     * 
     * @return the data stream from the response object - will be null on error
     *         or if the response has already been streamed to an OutputStream.
     */
    public String getResponse()
    {
        return this.data;
    }

    /**
     * Gets the response stream.
     * 
     * @return the response InputStream if set during construction, else will be
     *         null.
     */
    public InputStream getResponseStream()
    {
        try
        {
            return (this.is != null ? this.is : new ByteArrayInputStream(
                    this.data.getBytes(encoding)));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(
                    "UnsupportedEncodingException: " + encoding);
        }
    }

    /**
     * Gets the status.
     * 
     * @return Status object representing the response status and any error
     *         information {@link Status}
     */
    public Status getStatus()
    {
        return this.status;
    }

    /**
     * Gets the encoding.
     * 
     * @return the response encoding
     */
    public String getEncoding()
    {
        return this.encoding;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.data;
    }
}
