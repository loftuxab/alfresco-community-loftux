/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content;

import org.artofsolving.jodconverter.office.OfficeManager;

public interface JodConverter
{
    /**
     * Gets the JodConverter OfficeManager.
     * @return
     */
    public abstract OfficeManager getOfficeManager();
    
    /**
     * This method returns a boolean indicating whether the JodConverter connection to OOo is available.
     * @return <code>true</code> if available, else <code>false</code>
     */
    public abstract boolean isAvailable();
}