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
package org.alfresco.repo.security.authority;

import junit.framework.TestCase;

import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

public class AuthorityServiceTest extends TestCase
{
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
    
    private AuthenticationComponent authenticationComponent;
    
    private AuthenticationService authenticationService;
    
    private AuthorityService authorityService; 
    
    private AuthorityService pubAuthorityService; 

    public AuthorityServiceTest()
    {
        super();
        
    }
    
    public void setUp() throws Exception
    {
        authenticationComponent = (AuthenticationComponent) ctx.getBean("authenticationComponent");
        authenticationService = (AuthenticationService) ctx.getBean("authenticationService");
        authorityService = (AuthorityService) ctx.getBean("authorityService");
        pubAuthorityService = (AuthorityService) ctx.getBean("AuthorityService");
        
        if(!authenticationComponent.exists("andy"))
        {
            authenticationService.createAuthentication("andy", "andy".toCharArray());
        }
        
        if(!authenticationComponent.exists(AuthorityService.ALFRESCO_ADMIN_USER))
        {
            authenticationService.createAuthentication(AuthorityService.ALFRESCO_ADMIN_USER, AuthorityService.ALFRESCO_ADMIN_USER.toCharArray());
        }
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        authenticationService.clearCurrentSecurityContext();
        super.tearDown();
    }
    
    public void testNonAdminUser()
    {
        authenticationComponent.setCurrentUser("andy");
        assertFalse(authorityService.hasAdminAuthority());
        assertFalse(pubAuthorityService.hasAdminAuthority());
    }
    
    public void testAdminUser()
    {
        authenticationComponent.setCurrentUser(AuthorityService.ALFRESCO_ADMIN_USER);
        assertTrue(authorityService.hasAdminAuthority());
        assertTrue(pubAuthorityService.hasAdminAuthority());
    }

}
