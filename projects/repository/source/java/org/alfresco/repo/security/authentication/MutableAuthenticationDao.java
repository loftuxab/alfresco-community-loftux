/*
 * Created on 13-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.providers.dao.AuthenticationDao;

public interface MutableAuthenticationDao extends AuthenticationDao
{
    public void createUser(String userName, String rawPassword) throws AuthenticationException;
    public void updateUser(String userName, String rawPasswrod) throws AuthenticationException;
    public void deleteUser(String userName) throws AuthenticationException;
}
