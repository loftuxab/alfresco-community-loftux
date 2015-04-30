/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.transaction;

import java.util.ArrayList;


/**
 * Provides a list of enterprise-specific exception classes that should trigger a TX retry.
 * 
 * @author Matt Ward
 */
public class RetryExceptions extends ArrayList<Class<?>>
{
    private static final long serialVersionUID = 1L;
    
    public RetryExceptions()
    {
        // List of classes, e.g.
        // add(SpecialException.class);
    }
}
