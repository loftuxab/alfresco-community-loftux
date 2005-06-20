/*
 * Created on 13-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.UserDetails;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Extension to the acegi user details to access the password salt
 * 
 * @author andyh
 *
 */
public interface RepositoryUserDetails extends UserDetails
{
    /**
     * Get the salt
     * 
     * @return
     */
    public String getSalt();
   
    /**
     * A noderef for the user 
     * 
     * @return
     */
    public NodeRef getUserNodeRef();
    
    /**
     * A node ref for the person 
     * 
     * @return
     */
    public NodeRef getPersonNodeRef();
}
