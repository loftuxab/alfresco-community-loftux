package org.alfresco.service.cmr.security;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * The interface used to support reporting back if permissions are allowed or
 * denied.
 * 
 * @author Andy Hind
 */
@AlfrescoPublicApi
public interface AccessPermission
{   
    /**
     * The permission.
     * 
     * @return String
     */
    public String getPermission();
    
    /**
     * Get the Access enumeration value
     * 
     * @return AccessStatus
     */
    public AccessStatus getAccessStatus();
    
    
    /**
     * Get the authority to which this permission applies.
     * 
     * @return String
     */
    public String getAuthority();
    
 
    /**
     * Get the type of authority to which this permission applies.
     * 
     * @return AuthorityType
     */
    public AuthorityType getAuthorityType();
    
   
    /**
     * At what position in the inheritance chain for permissions is this permission set?
     * = 0 -> Set direct on the object.
     * > 0 -> Inherited
     * < 0 -> We don't know and are using this object for reporting (e.g. the actual permissions that apply to a node for the current user)
     * @return int
     */
    public int getPosition();
    
   /**
    * Is this an inherited permission entry?
    * @return boolean
    */ 
    public boolean isInherited();
    
    /**
     * Is this permission set on the object?
     * @return boolean
     */ 
     public boolean isSetDirectly();
}
