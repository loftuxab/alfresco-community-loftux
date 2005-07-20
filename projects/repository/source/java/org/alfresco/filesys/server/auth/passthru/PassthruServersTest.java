/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */

package org.alfresco.filesys.server.auth.passthru;

import junit.framework.TestCase;

/**
 * Passthru Servers Class Units Tests
 *  
 * @author GKSpencer
 */
public class PassthruServersTest extends TestCase
{
    /**
     * Default constructor
     */
    public PassthruServersTest()
    {
        super();
    }
    
    /**
     * Class constructor
     * 
     * @param arg String
     */
    public PassthruServersTest(String arg)
    {
        super(arg);
    }
    
    /**
     * Test setup
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * Test using a server list
     */
    public void testServerList()
    {
        // Create the passthru authenticator
        
        PassthruServers passthru = new PassthruServers();
        
        // Use a list of servers where one or more servers are not available
        
        passthru.setServerList("90.1.0.2,LINUXSRV,NOSUCHNODE , 90.2.0.1");
        
        // Wait for some authentication servers to come online
        
        int waitCnt = 0;
        
        while ( waitCnt++ < 200 && passthru.hasOnlineServers() == false)
        {
            // Wait a short while ...
            
            try 
            {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex)
            {
            }
        }
        
        // Make sure there are some online servers
        
        assertTrue("No online authentication servers", passthru.hasOnlineServers());
        
        // Open some authentication sessions
        
        AuthenticateSession authSess = null;
        
        for ( int i = 0; i < 10; i++)
        {
            try
            {
                // Open an authentication session
                
                authSess = passthru.openSession();
                if ( authSess != null)
                    authSess.CloseSession();
                authSess = null;
            }
            catch (Exception ex)
            {
            }
        }
        
        // Shutdown the passthru authentication list
        
        passthru.shutdown();
        
        assertEquals("Online server list not cleared", passthru.getOnlineServerCount(), 0);
        assertEquals("Offline server list not cleared", passthru.getOfflineServerCount(), 0);
    }
    
    /**
     * Test using a domain
     */
    public void testDomain()
    {
        // Create the passthru authenticator
        
        PassthruServers passthru = new PassthruServers();
        
        // Use a domain/workgroup for authentication
        
        passthru.setDomain("STARLASOFT");
        
        // Wait for some authentication servers to come online
        
        int waitCnt = 0;
        
        while ( waitCnt++ < 200 && passthru.hasOnlineServers() == false)
        {
            // Wait a short while ...
            
            try 
            {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex)
            {
            }
        }
        
        // Make sure there are some online servers
        
        assertTrue("No online authentication servers", passthru.hasOnlineServers());
        
        // Open some authentication sessions
        
        AuthenticateSession authSess = null;
        
        for ( int i = 0; i < 10; i++)
        {
            try
            {
                // Open an authentication session
                
                authSess = passthru.openSession();
                if ( authSess != null)
                    authSess.CloseSession();
                authSess = null;
            }
            catch (Exception ex)
            {
            }
        }
        
        // Shutdown the passthru authentication list
        
        passthru.shutdown();
        
        assertEquals("Online server list not cleared", passthru.getOnlineServerCount(), 0);
        assertEquals("Offline server list not cleared", passthru.getOfflineServerCount(), 0);
    }
}
