/*
 * #%L
 * Alfresco Sharepoint Protocol
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.module.vti.handler;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * An exception thrown when a site is not of the appropriate
 *  type for the operation being performed.
 * 
 * @author Nick Burch
 */
public class SiteTypeException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = -6073753941824982920L;

    /**
     * Constructs a <code>SiteTypeException</code>.
     * 
     * @param msgId
     *            the message id
     */
    public SiteTypeException(String msgId)
    {
        super(msgId);
    }

    /**
     * Constructs a <code>SiteTypeException</code>.
     * 
     * @param msgId
     *            the message id
     * @param msgParams
     *            the message parameters
     */
    public SiteTypeException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
    }

    /**
     * Constructs a <code>SiteTypeException</code>.
     * 
     * @param msgId
     *            the message id
     * @param cause
     *            the cause
     */
    public SiteTypeException(String msgId, Throwable cause)
    {
        super(msgId, cause);
    }

    /**
     * Constructs a <code>SiteTypeException</code>.
     * 
     * @param msgId
     *            the message id
     * @param msgParams
     *            the message parameters
     * @param cause
     *            the cause
     */
    public SiteTypeException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
    }
}