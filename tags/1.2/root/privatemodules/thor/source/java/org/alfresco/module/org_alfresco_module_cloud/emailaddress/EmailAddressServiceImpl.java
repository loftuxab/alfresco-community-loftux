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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck.FailureReason;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain.InvalidDomainDAO;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain.InvalidDomainEntity;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.Pair;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Email Address Support
 */
public class EmailAddressServiceImpl implements EmailAddressService
{
	private static final Log log = LogFactory.getLog(EmailAddressServiceImpl.class);
    
    public static final String EMAIL_DOMAIN_SEPARATOR = "@";
    
    // domain validators
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("[a-zA-Z0-9.-]+.[a-zA-Z]");
    private static final String DOMAIN_VALID_DNS_LABEL = "^[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]$";
    private static final String DOMAIN_VALID_SINGLE_CHAR_DNS_LABEL = "^[a-zA-Z0-9]$";
    private static final String DOMAIN_CONTAINS_ALPHA = "^(.*)[a-zA-Z](.*)$";
    private static final int DOMAIN_MAX_LEN = 75;
    
    private static final List<String> blockedUsernames = Arrays.asList("admin", "guest");
    
    private static final InitialDirContext idc = newIDC();
    private static final InitialDirContext newIDC()
    {
        try
        {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            return new InitialDirContext( env );
        }
        catch(NamingException e)
        {
            log.error("Failed to initialise Email address service", e);
            throw new AlfrescoRuntimeException("Failed to initialise Email address service", e); 
        }
    }
    
    private boolean reachableCheckEnabled = true;
    private InvalidDomainDAO invalidDomainDAO;
    
    
    public void setReachableCheckEnabled(boolean reachableCheckEnabled)
    {
        this.reachableCheckEnabled = reachableCheckEnabled;
    }
    
    public void setInvalidDomainDAO(InvalidDomainDAO invalidDomainDAO)
    {
        this.invalidDomainDAO = invalidDomainDAO;
    }
    
    @Override
    public boolean isAcceptedAddress(String email)
    {
        return isWellFormedAddress(email) && !isBlockedUsername(email);
    }

    private boolean isBlockedUsername(String email)
    {
        String[] parts = email.split(EMAIL_DOMAIN_SEPARATOR);
        return blockedUsernames.contains(parts[0].toLowerCase());
    }
    
    @Override
    public boolean isWellFormedAddress(String email)
    {
        try
        {
            new InternetAddress(email, true);
            return true;
        }
        catch (AddressException ignored) { /* Intentionally empty */ }
        return false;
    }

    @Override
    public boolean sameDomain(String domainOrEmail1, String domainOrEmail2)
    {
        String domain1 = (isDomain(domainOrEmail1) ? domainOrEmail1 : getDomain(domainOrEmail1));
        String domain2 = (isDomain(domainOrEmail2) ? domainOrEmail2 : getDomain(domainOrEmail2));
        return EqualsHelper.nullSafeEquals(domain1, domain2);
    }

    private boolean isDomain(String domain)
    {
    	return DOMAIN_PATTERN.matcher(domain).matches();
    }
    
    @Override
    public String getDomain(String email)
    {
        if (email == null || email.length() == 0)
        {
            return null;
        }
        if (!isWellFormedAddress(email))
        {
            return null;
        }
        
        InternetAddress address = null;
        try
        {
            address = new InternetAddress(email, true);
        }
        catch(AddressException ex) 
        {
            return null;
        }
        
        String[] parts = address.getAddress().split(EMAIL_DOMAIN_SEPARATOR);
        return parts[1];
    }
    
    @Override
    public DomainValidityCheck validateDomain(String domain)
    {
        DomainValidityCheck check = null;
        
        // ensure a domain has been provided
        if (domain == null || domain.length() == 0 || !isValidDomainName(domain))
        {
            check = new DomainValidityCheck(domain, FailureReason.INVALID_FORMAT, null);
        }
        else
        {
            // ensure domain is not in blacklist
            InvalidDomainEntity validityEntity = this.invalidDomainDAO.getInvalidDomain(domain);
            if (validityEntity != null)
            {
                String invalidityType = validityEntity.getType();
                FailureReason failureReason = FailureReason.valueOf(invalidityType);
                check = new DomainValidityCheck(domain, failureReason, validityEntity.getNote());
            }
        }
        if (check == null)
        {
            // ensure domain is reachable
            boolean reachable = isReachableDomain(domain);
            if (!reachable)
            {
                check = new DomainValidityCheck(domain, FailureReason.UNREACHABLE, null);
            }
        }
        
        return check == null ? new DomainValidityCheck(domain, null, null) : check;
    }
    
    @Override
    public boolean isValidDomainName(String domain)
    {
        if (domain.length() > DOMAIN_MAX_LEN)
        {
            return false;
        }
        
        if (!Pattern.matches(DOMAIN_CONTAINS_ALPHA, domain))
        {
            return false;
        }
        
        String[] dnsLabels = domain.split("\\.");
        if (dnsLabels.length != 0)
        {
            for (int i = 0; i < dnsLabels.length; i++)
            {
                if (i >= dnsLabels.length - 2)
                {
                    if (!Pattern.matches(DOMAIN_VALID_DNS_LABEL, dnsLabels[i]))
                    {
                        return false;
                    }
                }
                else
                {
                    if (!(Pattern.matches(DOMAIN_VALID_DNS_LABEL, dnsLabels[i]) || Pattern.matches(DOMAIN_VALID_SINGLE_CHAR_DNS_LABEL, dnsLabels[i])))
                    {
                        return false;
                    }
                }
            }
        }
        else
        {
            if (!Pattern.matches(DOMAIN_VALID_DNS_LABEL, domain))
            {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean isPublicDomain(String domain)
    {
        DomainValidityCheck check = validateDomain(domain);
        return check != null && check.getFailureReason() != null && check.getFailureReason() == FailureReason.PUBLIC;
    }
    
    @Override
    public boolean isReachableDomain(String domain)
    {
        // don't bother is the reachable check has been disabled completely
        if (!reachableCheckEnabled)
        {
            return true;
        }

        // exclude .test and .example TLDs (see RFC2606)
        if (domain.endsWith(".test") || domain.endsWith(".example"))
        {
            return true;
        }
        
        boolean reachable = false;
        try
        {
            Attributes attrs = idc.getAttributes(domain, new String[] { "MX", "A" });
            Attribute mx = attrs.get("MX");
            Attribute a = attrs.get("A");
            reachable = (mx != null && mx.size() > 0) || (a != null && a.size() > 0);
            
            if (log.isDebugEnabled() && !reachable)
            {
                String mxStr = (mx == null) ? "null" : "size: " + mx.size();
                String aStr = (a == null) ? "null" : "size: " + a.size();
                log.debug("Domain not reachable: " + domain + " MX[" + mxStr + "], A[" + aStr + "]");
            }
        }
        catch(NamingException e)
        {
            // NOTE: not reachable
            log.error("Failed to get attributes for domain: " + domain, e);
        }
        return reachable;
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Override public void createInvalidDomain(String domain, FailureReason type, String notes)
    {
        ParameterCheck.mandatoryString("domain", domain);
        ParameterCheck.mandatory("type", type);
        ParameterCheck.mandatory("notes", notes); // Note a value of "" is ok.
        
        InvalidDomainEntity entity = new InvalidDomainEntity();
        entity.setDomain(domain);
        entity.setType(type.toString());
        entity.setNote(notes);
        invalidDomainDAO.createInvalidDomain(entity);
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Override public void updateInvalidDomain(String domain, FailureReason newType, String newNotes)
    {
        ParameterCheck.mandatoryString("domain", domain);
        ParameterCheck.mandatory("newType", newType);
        ParameterCheck.mandatory("newNotes", newNotes); // Note a value of "" is ok.
        
        InvalidDomainEntity entity = new InvalidDomainEntity();
        entity.setDomain(domain);
        entity.setType(newType.toString());
        entity.setNote(newNotes);
        invalidDomainDAO.updateInvalidDomain(entity);
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Override public void deleteInvalidDomain(String domain)
    {
        ParameterCheck.mandatoryString("domain", domain);
        
        invalidDomainDAO.deleteInvalidDomain(domain);
    }
    
    /**
     * @since Thor Phase 2 Sprint 1
     */
    @Override public PagingResults<DomainValidityCheck> getInvalidDomains(final PagingRequest pagingRequest)
    {
        // Get data from the DB
        final int totalDataRows = invalidDomainDAO.getInvalidDomainCount();
        List<InvalidDomainEntity> entities = invalidDomainDAO.getInvalidDomains(pagingRequest.getSkipCount(), pagingRequest.getMaxItems());
        
        // Build the data into a list
        final List<DomainValidityCheck> data = new ArrayList<DomainValidityCheck>();
        for (InvalidDomainEntity entity : entities)
        {
            DomainValidityCheck check = new DomainValidityCheck(entity.getDomain(),
                                                                FailureReason.valueOf(entity.getType()),
                                                                entity.getNote());
            data.add(check);
        }
        
        // Put it in the Alfresco standard paging object.
        PagingResults<DomainValidityCheck> result = new PagingResults<DomainValidityCheck>()
        {
            @Override public List<DomainValidityCheck> getPage()
            {
                return data;
            }
            
            @Override public boolean hasMoreItems()
            {
                return (pagingRequest.getSkipCount() + pagingRequest.getMaxItems()) < totalDataRows;
            }
            
            @Override public Pair<Integer, Integer> getTotalResultCount()
            {
                return new Pair<Integer, Integer>(totalDataRows, totalDataRows);
            }
            
            @Override public String getQueryExecutionId()
            {
                return null;
            }
        };
        
        return result;
    }
    
    @Override public DomainValidityCheck getInvalidDomain(String emailDomain)
    {
        DomainValidityCheck result = null;
        
        InvalidDomainEntity entity = invalidDomainDAO.getInvalidDomain(emailDomain);
        
        if (entity != null)
        {
            result = new DomainValidityCheck(entity.getDomain(),
                    FailureReason.valueOf(entity.getType()),
                    entity.getNote());
        }
        return result;
    }

    @Override public String getAddress(String inviteeEmail)
    {
		try 
		{
			InternetAddress address = new InternetAddress(inviteeEmail, true);
			return address.getAddress();
		}
		catch (AddressException ex)
		{
			return null;
		}
		
	}
}
