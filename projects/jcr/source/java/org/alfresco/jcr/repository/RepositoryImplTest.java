/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jcr.repository;

import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.alfresco.jcr.test.BaseJCRTest;


/**
 * Test JCR Repository Implementation
 * 
 * @author David Caruana
 */
public class RepositoryImplTest extends BaseJCRTest
{

    public void testDescriptors()
    {
        String[] keys = repository.getDescriptorKeys();
        assertEquals(8, keys.length);
        for (String key : keys)
        {
            String value = repository.getDescriptor(key);
            assertNotNull(value);
        }
        
        assertNotNull(repository.getDescriptor(Repository.REP_NAME_DESC));
        assertNotNull(repository.getDescriptor(Repository.REP_VENDOR_DESC));
        assertNotNull(repository.getDescriptor(Repository.REP_VENDOR_URL_DESC));
        assertNotNull(repository.getDescriptor(Repository.REP_VERSION_DESC));
        assertNotNull(repository.getDescriptor(Repository.SPEC_NAME_DESC));
        assertNotNull(repository.getDescriptor(Repository.SPEC_VERSION_DESC));
        assertNotNull(repository.getDescriptor(Repository.LEVEL_1_SUPPORTED));
        assertNotNull(repository.getDescriptor(Repository.OPTION_TRANSACTIONS_SUPPORTED));
        assertNull(repository.getDescriptor(Repository.LEVEL_2_SUPPORTED));
    }
    

    public void testLogin()
        throws RepositoryException
    {
        SimpleCredentials badUser = new SimpleCredentials("baduser", "".toCharArray());
        try
        {
            repository.login(badUser);
            fail("Failed to catch bad username - username should not exist.");
        }
        catch (LoginException e)
        {
        }
        
        SimpleCredentials badPwd = new SimpleCredentials("superuser", "badpwd".toCharArray());
        try
        {
            repository.login(badPwd);
            fail("Failed to catch bad password - password is invalid.");
        }
        catch (LoginException e)
        {
        }
        
        try
        {
            repository.login();
            fail("Failed to catch no credentials.");
        }
        catch (LoginException e)
        {
        }
        
        SimpleCredentials good = new SimpleCredentials("superuser", "".toCharArray());
        try
        {
            Session session = repository.login(good, getWorkspace());
            assertNotNull(session);
        }
        catch (LoginException e)
        {
            fail("Failed to login.");
        }

        try
        {
            repository.login(good, null);
        }
        catch (NoSuchWorkspaceException e)
        {
        }
    }
    
}

