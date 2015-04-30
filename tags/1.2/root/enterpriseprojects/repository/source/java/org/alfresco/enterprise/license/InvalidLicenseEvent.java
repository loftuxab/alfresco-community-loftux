/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.license;

import org.springframework.context.ApplicationEvent;

/**
 * Used to broadcast notification of an invalid license.
 * 
 * @author dward
 */
public class InvalidLicenseEvent extends ApplicationEvent
{

    private static final long serialVersionUID = 8594533904590809408L;

    /** The exception thrown during license validation. */
    private Throwable cause;

    /**
     * The Constructor.
     * 
     * @param source
     *            the source of the event
     * @param cause
     *            the exception thrown during license validation
     */
    public InvalidLicenseEvent(Object source, Throwable cause)
    {
        super(source);
        this.cause = cause;
    }

    /**
     * Gets the exception thrown during license validation.
     * 
     * @return the exception thrown during license validation
     */
    public Throwable getCause()
    {
        return this.cause;
    }
}
