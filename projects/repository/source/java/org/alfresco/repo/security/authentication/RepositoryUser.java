/*
 * Created on 13-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.providers.dao.User;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Extension to the acegi user to encapsulate a user in the content model
 * 
 * @author andyh
 *
 */
public class RepositoryUser extends User implements RepositoryUserDetails
{

    /**
     * 
     */
    private static final long serialVersionUID = 3258415040725005107L;
    
    private String salt;

    private NodeRef personNodeRef;

    private NodeRef userNodeRef;

    public RepositoryUser(String username, String password, boolean enabled,
            boolean accountNonExpired, boolean credentialsNonExpired,
            boolean accountNonLocked, GrantedAuthority[] authorities, 
            String salt, NodeRef userNodeRef, NodeRef personNodeRef)
            throws IllegalArgumentException
    {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.salt = salt;
        this.userNodeRef = userNodeRef;
        this.personNodeRef = personNodeRef;
    }

    public String getSalt()
    {
        System.out.println("Salt is "+salt);
        return salt;
    }

    public NodeRef getUserNodeRef()
    {
        return userNodeRef;
    }

    public NodeRef getPersonNodeRef()
    {
        return personNodeRef;
    }

}
