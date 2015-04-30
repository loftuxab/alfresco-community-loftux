/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.module.org_alfresco_module_cloud.emailaddress;

import org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain.InvalidDomainEntity;

/**
 * This class encapsulates the result of a domain validity check.
 * 
 * @author Neil Mc Erlean
 * @since Thor Cloud Module 1.0
 */
public class DomainValidityCheck
{
    /**
     * These are the allowed values for the {@link InvalidDomainEntity#getType() domain invalidity reason.}
     */
    public enum FailureReason
    {
        /**
         * The domain is not specified correctly 
         */
        INVALID_FORMAT,
        /**
         * The domain is believed to be spam.
         */
        ANTISPAM,
        /**
         * The domain is public e.g. gmail.com
         */ 
        PUBLIC,
        /**
         * The domain has been blacklisted explicitly.
         */
        BLACKLISTED,
        /**
         * The domain is not reachable.
         */
        UNREACHABLE
    }
    
    private final String domain;
    private final FailureReason failureReason;
    private final String failureNotes;
    
    /**
     * @param domain The domain that was validated.
     * @param failureReason The reason for the validation failure. Can be null
     * @param failureNotes Any notes on the failure. Can be null
     */
    public DomainValidityCheck(String domain, FailureReason failureReason, String failureNotes)
    {
        this.domain = domain;
        this.failureReason = failureReason;
        this.failureNotes = failureNotes;
    }
    
    /**
     * Get the domain that was validated.
     * @return the domain.
     */
    public String getDomain()
    {
        return this.domain;
    }
    
    /**
     * Gets the failure reason.
     * @return the failure reason - if it failed, else <tt>null</tt>.
     */
    public FailureReason getFailureReason()
    {
        return failureReason;
    }
    
    /**
     * Gets the failure notes, if any.
     * @return the failure notes - if it failed and there are notes, else <tt>null</tt>.
     */
    public String getFailureNotes()
    {
        return this.failureNotes;
    }
}