package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.context.security.SecureContext;

/**
 * Extensions for the Alfresco security context.
 * 
 * This is based on the Linux model and supports real, effective and stored authorities
 * 
 * The real authority is used for auditing and reporting who the user is etc.
 * The effective authority is used for permission checks.
 * 
 * RunAs support leaves the real authority and changes only the effective authority
 * That means "special" code can run code as system but still be audited as Joe
 * 
 * In the future scrips etc can support a setUId flag and run as the owner of the script.
 * If the script chooses to do this ....
 * A method invocation could do the same (after entry security checks)
 * 
 * TODO: extent runAs to take a nodeRef context - it can then set the stored atc and set this as effective if required.
 * 
 * @author andyh
 *
 */
public interface AlfrescoSecureContext extends SecureContext
{
    /**
     * Get the effective authentication - used for permission checks
     * @return Authentication
     */
    public Authentication getEffectiveAuthentication();
    
    /**
     * Get the real authenticaiton - used for auditing and everything else
     * @return Authentication
     */
    public Authentication getRealAuthentication();
    
    /**
     * Set the effective authentication held by the context
     * 
     * @param effictiveAuthentication Authentication
     */
    public void setEffectiveAuthentication(Authentication effictiveAuthentication);
    
    /**
     * Set the real authentication held by the context
     * 
     * @param realAuthentication Authentication
     */
    public void setRealAuthentication(Authentication realAuthentication);
    
}
