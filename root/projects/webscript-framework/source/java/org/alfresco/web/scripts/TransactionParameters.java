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
package org.alfresco.web.scripts;

import org.alfresco.web.scripts.Description.RequiredTransaction;
import org.alfresco.web.scripts.Description.TransactionCapability;


/**
 * Web Script Transaction Parameters
 *
 * Records the desired transaction requirements for the Web Script
 */
public class TransactionParameters implements Description.RequiredTransactionParameters
{
    private RequiredTransaction required;
    private TransactionCapability capability;
    private int bufferSize = 4096;

    
    /**
     * Construct
     */
    public TransactionParameters()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.web.scripts.Description.RequiredTransactionParameters#getCapbility()
     */
    public TransactionCapability getCapability()
    {
        return capability;
    }

    /**
     * Sets capability
     * 
     * @param capability
     */
    public void setCapability(TransactionCapability capability)
    {
        this.capability = capability;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.web.scripts.Description.RequiredTransactionParameters#getRequired()
     */
    public RequiredTransaction getRequired()
    {
        return required;
    }
    
    /**
     * Sets required
     * 
     * @param required
     */
    public void setRequired(RequiredTransaction required)
    {
        this.required = required;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.web.scripts.Description.RequiredTransactionParameters#getBufferSize()
     */
    public int getBufferSize()
    {
        return bufferSize;
    }

    /**
     * Sets buffer size
     * 
     * @param bufferSize
     */
    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

}
