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
package org.alfresco.jcr.session;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.alfresco.jcr.test.JCRTest;


/**
 * Test JCR Session
 * 
 * @author David Caruana
 */
public class SessionImplTest extends JCRTest
{
    protected Session superuserSession;
    protected Session readuserSession;
    
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        SimpleCredentials superuser = new SimpleCredentials("superuser", "".toCharArray());
        superuser.setAttribute("attr1", "superuserValue");
        superuser.setAttribute("attr2", new Integer(1));
        superuserSession = repository.login(superuser, getWorkspace());

        SimpleCredentials readuser = new SimpleCredentials("readuser", "".toCharArray());
        readuser.setAttribute("attr1", "readuserValue");
        readuser.setAttribute("attr2", new Integer(2));
        readuserSession = repository.login(readuser, getWorkspace());
    }
    
    public void testRepository()
        throws RepositoryException
    {
        Repository sessionRepository = superuserSession.getRepository();
        assertNotNull(sessionRepository);
        assertEquals(repository, sessionRepository);
    }

    public void testUserId()
    {
        {
            String userId = superuserSession.getUserID();
            assertNotNull(userId);
            assertEquals("superuser", userId);
        }
        {
            String userId = readuserSession.getUserID();
            assertNotNull(userId);
            assertEquals("readuser", userId);
        }
    }

    public void testAttributes()
    {
        {
            String[] names = superuserSession.getAttributeNames();
            assertNotNull(names);
            assertEquals(2, names.length);
            String value1 = (String)superuserSession.getAttribute("attr1");
            assertNotNull(value1);
            assertEquals("superuserValue", value1);
            Integer value2 = (Integer)superuserSession.getAttribute("attr2");
            assertNotNull(value2);
            assertEquals(new Integer(1), value2);
            String value3 = (String)superuserSession.getAttribute("unknown");
            assertNull(value3);
        }
        {
            String[] names = readuserSession.getAttributeNames();
            assertNotNull(names);
            assertEquals(2, names.length);
            String value1 = (String)readuserSession.getAttribute("attr1");
            assertNotNull(value1);
            assertEquals("readuserValue", value1);
            Integer value2 = (Integer)readuserSession.getAttribute("attr2");
            assertNotNull(value2);
            assertEquals(new Integer(2), value2);
            String value3 = (String)readuserSession.getAttribute("unknown");
            assertNull(value3);
        }
    }
    
    public void testLogout()
    {
        boolean isLive = superuserSession.isLive();
        assertTrue(isLive);
        superuserSession.logout();
        isLive = superuserSession.isLive();
        assertFalse(isLive);
    }
    
}

