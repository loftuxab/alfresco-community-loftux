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

package org.alfresco.repo.security.authentication.ntlm;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.alfresco.filesys.server.auth.PasswordEncryptor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.CredentialsExpiredException;
import net.sf.acegisecurity.providers.ProviderManager;
import junit.framework.TestCase;

/**
 * <p>Test the Acegi NTLM passthru authentication provider.
 * 
 * @author GKSpencer
 */
public class NTLMProviderTest extends TestCase
{
    //  Spring configuration
    
    private static ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:alfresco/application-context.xml");
    
    // Acegi authentication manager bean
    
    private AuthenticationManager m_authMgr;
    
    /**
     * Default constructor
     */
    public NTLMProviderTest()
    {
        super();
    }
    
    /**
     * Class constructor
     * 
     * @param arg String
     */
    public NTLMProviderTest(String arg)
    {
        super(arg);
    }
    
    /**
     * Test setup
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        
        // Find the Acegi authentication manager bean
        
        m_authMgr = (AuthenticationManager) ctx.getBean("authenticationManager");
        
        if ( m_authMgr instanceof ProviderManager)
        {
            // Check if the NTLM provider is configured
            
            ProviderManager provMgr = (ProviderManager) m_authMgr;
            List providerList = provMgr.getProviders();
            
            if ( providerList != null)
            {
                // Check for the NTLM authentication provider
                
                int providerIdx = 0;
                boolean foundProvider = false;
                
                while ( providerIdx < providerList.size() && foundProvider == false)
                {
                    if ( providerList.get(providerIdx++) instanceof NTLMAuthenticationProvider)
                        foundProvider = true;
                }
                
                if (foundProvider == false)
                    throw new Exception("NTLM authentication provider is not available");
                else if (providerIdx > 1)
                    throw new Exception("NTLM authentication provider should be first in the list of providers");
            }
            else
                throw new Exception("No authentication providers available");
        }
        else
            throw new Exception("Test requires ProviderManager");
        
        // Wait for authentication server(s)

        try
        {
            Thread.sleep(2000L);
        }
        catch ( Exception ex)
        {
        }
    }

    /**
     * Test the local authentication token
     */
    public void testLocalToken()
    {
        // Create an authentication token
        
        NTLMLocalToken authToken = new NTLMLocalToken("testuser", "testpass");
        
        // Authenticate the user
        
        authToken = (NTLMLocalToken) m_authMgr.authenticate( authToken);
        
        assertNotNull("Returned token is null", authToken);
        assertTrue("Testuser not authenticated", authToken.isAuthenticated());
        assertFalse("Testuser used guest logon", authToken.isGuestLogon());
        assertFalse("Testuser has administrator authority", authToken.isAdministrator());
        
        // Create an authentication token for a guest logon
        
        NTLMLocalToken guestToken = new NTLMLocalToken("guest", "");
        
        // Authenticate the guest user
        
        guestToken = (NTLMLocalToken) m_authMgr.authenticate( guestToken);
        
        assertNotNull("Returned guest token is null", guestToken);
        assertTrue("Guest not authenticated", guestToken.isAuthenticated());
        assertTrue("Guest not logged on as guest", guestToken.isGuestLogon());
        assertFalse("Guest has administrator authority", guestToken.isAdministrator());

        // Authenticate an unknown user as guest
        
        guestToken = new NTLMLocalToken("GuestUser", "");
        guestToken = (NTLMLocalToken) m_authMgr.authenticate( guestToken);
        
        assertNotNull("Returned guest token is null", guestToken);
        assertTrue("GuestUser not authenticated", guestToken.isAuthenticated());
        assertTrue("GuestUser not logged on as guest", guestToken.isGuestLogon());
        assertFalse("GuestUser has administrator authority", guestToken.isAdministrator());
    }
    
    /**
     * Test the passthru authentication token
     */
    public void testPassthruToken()
    {
        // Test passthru authentication
        
        NTLMPassthruToken passToken = new NTLMPassthruToken();
        passToken = (NTLMPassthruToken) m_authMgr.authenticate( passToken);
        
        // Calculate the hashed password
        
        PasswordEncryptor pwdEnc = new PasswordEncryptor();
        int alg = PasswordEncryptor.NTLM1;
        
        try
        {
            byte[] hashedPwd = pwdEnc.generateEncryptedPassword("testpass", passToken.getChallenge().getBytes(), alg);
            passToken.setUserAndPassword("testuser", hashedPwd, alg);
        }
        catch (NoSuchAlgorithmException ex)
        {
            fail("JCE provider not configured");
        }
        
        // Perform the second stage of passthru authentication
        
        passToken = (NTLMPassthruToken) m_authMgr.authenticate( passToken);
        
        assertNotNull("Returned token is null", passToken);
        assertTrue("Testuser not authenticated", passToken.isAuthenticated());
        assertFalse("Testuser used guest logon", passToken.isGuestLogon());
        assertFalse("Testuser has administrator authority", passToken.isAdministrator());
        
        // Timeout a passthru authentication
        
        passToken = new NTLMPassthruToken();
        passToken = (NTLMPassthruToken) m_authMgr.authenticate( passToken);
        
        // Sleep for a while to let the authentication session expire
        
        try
        {
            Thread.sleep( 45000L);
        }
        catch (Exception ex)
        {
        }
        
        // Try the second stage of the passthru authentication

        try
        {
            passToken = (NTLMPassthruToken) m_authMgr.authenticate( passToken);

            fail("Passthru session did not expire");
        }
        catch ( CredentialsExpiredException ex)
        {
        }
        catch (Exception ex)
        {
            fail("Authentication error " + ex);
        }
    }
}
