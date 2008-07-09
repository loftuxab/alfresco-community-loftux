/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.extranet.ldap;

import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.alfresco.extranet.database.DatabaseUser;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;

/**
 * The Class LDAPUserBean.
 * 
 * @author muzquiano
 */
public class LDAPUserBean 
{
    private LdapTemplate ldapTemplate;
    
    /**
     * Instantiates a new lDAP user bean.
     */
    public LDAPUserBean()
    {
    }
    
    /**
     * Sets the ldap template.
     * 
     * @param ldapTemplate the new ldap template
     */
    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    /**
     * Inserts a user into the database.
     * 
     * @param user the user
     * 
     * @return the LDAP user
     */
    public LDAPUser insert(LDAPUser user) 
    {
        Attributes attribs = new BasicAttributes();
        
        // add the object class
        BasicAttribute objectBasicAttribute = new BasicAttribute("objectclass");
        objectBasicAttribute.add("person");
        attribs.put(objectBasicAttribute);
        
        // add other attributes
        attribs.put("cn", user.getUserId());
        attribs.put("sn", user.getLastName());
        attribs.put("description", user.getDescription());
        
        // define the distinguished name
        DistinguishedName dn = new DistinguishedName("ou=users");
        dn.add("cn", user.getUserId());
        
        // bind in
        ldapTemplate.bind(dn, null, attribs);
        
        return user;
    }
    
    /**
     * Updates a user.
     * 
     * @param user the user
     * 
     * @return true, if update
     */
    public boolean update(LDAPUser user) 
    {
        Attributes attribs = new BasicAttributes();
        
        // add the object class
        BasicAttribute objectBasicAttribute = new BasicAttribute("objectclass");
        objectBasicAttribute.add("person");
        attribs.put(objectBasicAttribute);
        
        // add other attributes
        attribs.put("cn", user.getUserId());
        attribs.put("sn", user.getLastName());
        attribs.put("description", user.getDescription());
        
        // define the distinguished name
        DistinguishedName dn = new DistinguishedName("ou=users");
        dn.add("cn", user.getUserId());
        
        // bind in
        ldapTemplate.rebind(dn, null, attribs);
        
        return true;
    }

    /**
     * Deletes a user.
     * 
     * @param user the user
     * 
     * @return true, if delete
     */
    public boolean delete(DatabaseUser user)
    {
        DistinguishedName dn = new DistinguishedName("ou=users");
        dn.add("cn", user.getUserId());
        ldapTemplate.unbind(dn);
        
        return true;
    }

    /**
     * Returns a list of users.
     * 
     * @return the list
     */
    public List list() 
    {
        return ldapTemplate.search("", "(objectclass=person)", new LDAPUserMapper());
    }
    
    /**
     * Gets the.
     * 
     * @param userId the user id
     * 
     * @return the lDAP user
     */
    public LDAPUser get(String userId) 
    {
        DistinguishedName dn = new DistinguishedName("ou=users");
        dn.add("cn", userId);

        LDAPUser user = null;
        
        List list = ldapTemplate.search(dn, null, new LDAPUserMapper());
        if(list != null && list.size() > 0)
        {
            user = (LDAPUser) list.get(0);
        }
        
        return user;
    }   
    

}
