/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck.FailureReason;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain.InvalidDomainException;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;


/**
 * Email Address Support
 */
public interface EmailAddressService
{
    /**
     * This method checks if the specified email address is accepted i.e. the address
     * is well-formed and the username is not blocked
     * 
     * @param email the email address to validate
     * @return <tt>true</tt> if accepted, else <tt>false</tt>
     */
    boolean isAcceptedAddress(String email);
    
    /**
     * This method checks if the specified email address is well-formed.
     * 
     * @param email the email address to validate
     * @return <tt>true</tt> if well-formed, else <tt>false</tt>.
     */
    boolean isWellFormedAddress(String email);
    
    /**
     * Compare two email addresses or domains to determine if their domains match
     * 
     * @param domainOrEmail1
     * @param domainOrEmail2
     * @return <tt>true</tt> domains do match 
     */
    boolean sameDomain(String domainOrEmail1, String domainOrEmail2);

    /**
     * Gets the domain part of the specified email
     * 
     * @param email  email
     * @return  domain
     */
    String getDomain(String email);

    /**
     * Validate a domain
     * 
     * @param domain domain to validate
     * @return the {@link DomainValidityCheck}
     * @throws NullPointerException if emailAddress was null.
     */
    DomainValidityCheck validateDomain(String domain);
    
    /**
     * Is the specified domain reachable?
     * 
     * @param domain  domain to check
     * @return  true => is reachable via DNS records corresponding to the domain
     */
    boolean isReachableDomain(String domain);
    
    /**
     * Is the specified domain public?
     * 
     * @param domain  domain to check
     * @return  true => is public
     */
    boolean isPublicDomain(String domain);
    
    /**
     * Tenant domain/ids are unique strings that are case-insensitive. Tenant ids must be valid filenames. 
     * They may also map onto domains and hence should allow valid FQDN.
     *
     *       The following PCRE-style
     *       regex defines a valid label within a FQDN:
     *
     *          ^[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]$
     *
     *       Less formally:
     *
     *          o  Case insensitive
     *          o  First/last character:  alphanumeric
     *          o  Interior characters:   alphanumeric plus hyphen
     *          o  Minimum length:        2  characters
     *          o  Maximum length:        63 characters
     *
     *       The FQDN (fully qualified domain name) has the following constraints:
     *
     *          o  Maximum 255 characters (***)
     *          o  Must contain at least one alpha
     *          
     *  Note: (***) Due to various internal restrictions (such as store identifier) we restrict tenant ids to 75 characters.
     */
    boolean isValidDomainName(String domain);
    
    /**
     * Creates a new Invalid Domain entry in the blacklist table.
     * 
     * @param domain the email domain to be invalidated. Cannot be null.
     * @param type   the reason it is invalid. Cannot be null.
     * @param notes  Optional notes on its invalidity. Cannot be null. "" is ok.
     * @throws InvalidDomainException if an entry already exists for this domain.
     * @since Thor Phase 2 Sprint 1
     */
    void createInvalidDomain(String domain, FailureReason type, String notes);
    
    /**
     * Update the type and/or notes of an entry in the blacklist table.
     * @param domain   the domain to be updated
     * @param newType  the new type of the entry - cannot be null.
     * @param newNotes the new notes for the entry - cannot be null, can be "".
     */
    void updateInvalidDomain(String domain, FailureReason newType, String newNotes);
    
    /**
     * Delete the row in the blacklist db table for the specified domain.
     * @param domain the email domain whose data row is to be deleted.
     * @since Thor Phase 2 Sprint 1
     */
    void deleteInvalidDomain(String domain);
    
    /**
     * Get a single item of invalid domain data. Doesn't actually perform validation.
     * 
     * @return
     * @since Thor Phase 2 Sprint 1
     */
    DomainValidityCheck getInvalidDomain(String emailDomain);
    
    /**
     * Get a page of invalid domain data.
     * 
     * @param startIndex
     * @param pageSize
     * @return
     * @since Thor Phase 2 Sprint 1
     */
    PagingResults<DomainValidityCheck> getInvalidDomains(PagingRequest pagingRequest);

    /**
     * Get the email address from a RFC822 formatted string.
     * 
     * @param inviteeEmail
     * @return
     */
	String getAddress(String inviteeEmail);
}
