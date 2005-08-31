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
package org.alfresco.repo.security.authentication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.UserTransaction;

import junit.framework.TestCase;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.BadCredentialsException;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import net.sf.acegisecurity.providers.dao.SaltSource;
import net.sf.acegisecurity.providers.encoding.PasswordEncoder;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

public class AuthenticationTest extends TestCase
{
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    private NodeService nodeService;
    
    private SearchService searchService;

    private NodeRef rootNodeRef;

    private NodeRef systemNodeRef;

    private NodeRef typesNodeRef;

    private NodeRef personAndyNodeRef;

    private DictionaryService dictionaryService;

    private PasswordEncoder passwordEncoder;

    private MutableAuthenticationDao dao;

    private AuthenticationManager authenticationManager;

    private SaltSource saltSource;

    private TicketComponent ticketComponent;

    private AuthenticationService authenticationService;
    
    private AuthenticationComponent authenticationComponent;

    public AuthenticationTest()
    {
        super();
    }

    public AuthenticationTest(String arg0)
    {
        super(arg0);
    }

    public void setUp() throws Exception
    {

        nodeService = (NodeService) ctx.getBean("nodeService");
        searchService = (SearchService) ctx.getBean("searchService");
        dictionaryService = (DictionaryService) ctx.getBean("dictionaryService");
        passwordEncoder = (PasswordEncoder) ctx.getBean("passwordEncoder");
        ticketComponent = (TicketComponent) ctx.getBean("ticketComponent");
        authenticationService = (AuthenticationService) ctx.getBean("authenticationService");
        authenticationComponent = (AuthenticationComponent) ctx.getBean("authenticationComponent");

        dao = (MutableAuthenticationDao) ctx.getBean("alfDaoImpl");
        authenticationManager = (AuthenticationManager) ctx.getBean("authenticationManager");
        saltSource = (SaltSource) ctx.getBean("saltSource");

        TransactionService transactionService = (TransactionService) ctx.getBean(ServiceRegistry.TRANSACTION_SERVICE.getLocalName());
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);

        QName children = ContentModel.ASSOC_CHILDREN;
        QName system = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "system");
        QName container = ContentModel.TYPE_CONTAINER;
        QName types = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "people");

        systemNodeRef = nodeService.createNode(rootNodeRef, children, system, container).getChildRef();
        typesNodeRef = nodeService.createNode(systemNodeRef, children, types, container).getChildRef();
        Map<QName, Serializable> props = createPersonProperties("andy");
        personAndyNodeRef = nodeService.createNode(typesNodeRef, children, ContentModel.TYPE_PERSON, container, props).getChildRef();
        assertNotNull(personAndyNodeRef);
    }

    private Map<QName, Serializable> createPersonProperties(String userName)
    {
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_USERNAME, "andy");
        return properties;
    }

    public void testPersonAndyExists()
    {
        RepositoryAuthenticationDao dao = new RepositoryAuthenticationDao();
        dao.setNodeService(nodeService);
        dao.setSearchService(searchService);
        dao.setDictionaryService(dictionaryService);
        dao.setNamespaceService(getNamespacePrefixReolsver(""));
        dao.setPasswordEncoder(passwordEncoder);
      
        assertNotNull(authenticationComponent.getPerson(rootNodeRef.getStoreRef(), "andy"));
    }

    public void testCreateAndyUserAndOtherCRUD()
    {
        RepositoryAuthenticationDao dao = new RepositoryAuthenticationDao();
        dao.setNodeService(nodeService);
        dao.setSearchService(searchService);
        dao.setDictionaryService(dictionaryService);
        dao.setNamespaceService(getNamespacePrefixReolsver(""));
        dao.setPasswordEncoder(passwordEncoder);

        dao.createUser("andy", "cabbage");
        assertNotNull(dao.getUserOrNull("andy"));

        UserDetails andyDetails = (UserDetails) dao.loadUserByUsername("andy");
        assertNotNull(andyDetails);
        assertNotNull(authenticationComponent.getPerson(rootNodeRef.getStoreRef(), "andy"));
        assertEquals("andy", andyDetails.getUsername());
        assertNotNull(dao.getSalt(andyDetails));
        assertTrue(andyDetails.isAccountNonExpired());
        assertTrue(andyDetails.isAccountNonLocked());
        assertTrue(andyDetails.isCredentialsNonExpired());
        assertTrue(andyDetails.isEnabled());
        assertNotSame("cabbage", andyDetails.getPassword());
        assertEquals(andyDetails.getPassword(), passwordEncoder.encodePassword("cabbage", saltSource.getSalt(andyDetails)));
        assertEquals(1, andyDetails.getAuthorities().length);

        Object oldSalt = dao.getSalt(andyDetails);
        dao.updateUser("andy", "carrot");
        UserDetails newDetails = (UserDetails) dao.loadUserByUsername("andy");
        assertNotNull(newDetails);
        assertNotNull(authenticationComponent.getPerson(rootNodeRef.getStoreRef(), "andy"));
        assertEquals("andy", newDetails.getUsername());
        assertNotNull(dao.getSalt(newDetails));
        assertTrue(newDetails.isAccountNonExpired());
        assertTrue(newDetails.isAccountNonLocked());
        assertTrue(newDetails.isCredentialsNonExpired());
        assertTrue(newDetails.isEnabled());
        assertNotSame("carrot", newDetails.getPassword());
        assertEquals(1, newDetails.getAuthorities().length);

        assertNotSame(andyDetails.getPassword(), newDetails.getPassword());
        assertNotSame(oldSalt, dao.getSalt(newDetails));
        assertNotNull(authenticationComponent.getPerson(rootNodeRef.getStoreRef(), "andy"));

        dao.deleteUser("andy");
        assertNull(dao.getUserOrNull("andy"));
    }

    public void testAuthentication()
    {
        dao.createUser("andy", "squash");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("andy", "squash");
        token.setAuthenticated(false);

        Authentication result = authenticationManager.authenticate(token);
        assertNotNull(result);
        
        dao.deleteUser("andy");
        //assertNull(dao.getUserOrNull("andy"));
    }

    public void testAuthenticationFailure()
    {
        dao.createUser("andy", "squash");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("andy", "turnip");
        token.setAuthenticated(false);

        try
        {
            Authentication result = authenticationManager.authenticate(token);
            assertNotNull(result);
            assertNotNull(null);
        }
        catch (BadCredentialsException e)
        {
            // Expected
        }
        dao.deleteUser("andy");
       // assertNull(dao.getUserOrNull("andy"));
    }

    public void testTicket()
    {
        dao.createUser("andy", "ticket");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("andy", "ticket");
        token.setAuthenticated(false);

        Authentication result = authenticationManager.authenticate(token);
        result.setAuthenticated(true);

        String ticket = ticketComponent.getTicket(getUserName(result));
        String user = ticketComponent.validateTicket(ticket);

        try
        {
            user = ticketComponent.validateTicket("INVALID");
            assertNotNull(null);
        }
        catch (AuthenticationException e)
        {

        }

        ticketComponent.invalidateTicketById(ticket);
        try
        {
            user = ticketComponent.validateTicket(ticket);
            assertNotNull(null);
        }
        catch (AuthenticationException e)
        {

        }
        
        dao.deleteUser("andy");
        //assertNull(dao.getUserOrNull("andy"));

    }

    public void testTicketRepeat()
    {
        InMemoryTicketComponentImpl tc = new InMemoryTicketComponentImpl();
        tc.setOneOff(false);
        tc.setTicketsExpire(false);
        tc.setValidDuration("P0D");

        dao.createUser("andy", "ticket");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("andy", "ticket");
        token.setAuthenticated(false);

        Authentication result = authenticationManager.authenticate(token);
        result.setAuthenticated(true);

        String ticket = tc.getTicket(getUserName(result));
        tc.validateTicket(ticket);
        tc.validateTicket(ticket);
        
        dao.deleteUser("andy");
        //assertNull(dao.getUserOrNull("andy"));
    }

    public void testTicketOneOff()
    {
        InMemoryTicketComponentImpl tc = new InMemoryTicketComponentImpl();
        tc.setOneOff(true);
        tc.setTicketsExpire(false);
        tc.setValidDuration("P0D");

        dao.createUser("andy", "ticket");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("andy", "ticket");
        token.setAuthenticated(false);

        Authentication result = authenticationManager.authenticate(token);
        result.setAuthenticated(true);

        String ticket = tc.getTicket(getUserName(result));
        tc.validateTicket(ticket);
        try
        {
            tc.validateTicket(ticket);
            assertNotNull(null);
        }
        catch (AuthenticationException e)
        {

        }
        
        dao.deleteUser("andy");
        //assertNull(dao.getUserOrNull("andy"));
    }

    public void testTicketExpires()
    {
        InMemoryTicketComponentImpl tc = new InMemoryTicketComponentImpl();
        tc.setOneOff(false);
        tc.setTicketsExpire(true);
        tc.setValidDuration("P5S");

        dao.createUser("andy", "ticket");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("andy", "ticket");
        token.setAuthenticated(false);

        Authentication result = authenticationManager.authenticate(token);
        result.setAuthenticated(true);

        String ticket = tc.getTicket(getUserName(result));
        tc.validateTicket(ticket);
        tc.validateTicket(ticket);
        tc.validateTicket(ticket);
        synchronized (this)
        {
            try
            {
                wait(10000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            tc.validateTicket(ticket);
            assertNotNull(null);
        }
        catch (AuthenticationException e)
        {

        }
        
        dao.deleteUser("andy");
        //assertNull(dao.getUserOrNull("andy"));
    }

    public void testTicketDoesNotExpire()
    {
        InMemoryTicketComponentImpl tc = new InMemoryTicketComponentImpl();
        tc.setOneOff(false);
        tc.setTicketsExpire(true);
        tc.setValidDuration("P1D");

        dao.createUser("andy", "ticket");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("andy", "ticket");
        token.setAuthenticated(false);

        Authentication result = authenticationManager.authenticate(token);
        result.setAuthenticated(true);

        String ticket = tc.getTicket(getUserName(result));
        tc.validateTicket(ticket);
        tc.validateTicket(ticket);
        tc.validateTicket(ticket);
        synchronized (this)
        {
            try
            {
                wait(10000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        tc.validateTicket(ticket);
        
        dao.deleteUser("andy");
        //assertNull(dao.getUserOrNull("andy"));

    }

    public void testAuthenticationService()
    {
        // create an authentication object e.g. the user
        authenticationService.createAuthentication("andy", "auth1".toCharArray());

        // authenticate with this user details
        authenticationService.authenticate("andy", "auth1".toCharArray());

        // assert the user is authenticated
        assertEquals("andy", authenticationService.getCurrentUserName());
        // delete the user authenticatiom object

        authenticationService.deleteAuthentication("andy");

        // create a new authentication user object
        authenticationService.createAuthentication("andy", "auth2".toCharArray());
        // change the password
        authenticationService.setAuthentication("andy", "auth3".toCharArray());
        // authenticate again to assert password changed
        authenticationService.authenticate("andy", "auth3".toCharArray());

        try
        {
           authenticationService.authenticate("andy", "auth1".toCharArray());
           assertNotNull(null);
        }
        catch (AuthenticationException e)
        {

        }
        try
        {
            authenticationService.authenticate("andy", "auth2".toCharArray());
            assertNotNull(null);
        }
        catch (AuthenticationException e)
        {

        }

        // get the ticket that represents the current user authentication instance
        String ticket = authenticationService.getCurrentTicket();
        // validate our ticket is still valid
        authenticationService.validate(ticket);

        // destroy the ticket instance
        authenticationService.invalidateTicket(ticket);
        try
        {
            authenticationService.validate(ticket);
            assertNotNull(null);
        }
        catch (AuthenticationException e)
        {

        }
        
        // clear any context and check we are no longer authenticated
        authenticationService.clearCurrentSecurityContext();
        assertNull(authenticationService.getCurrentUserName());
        
        dao.deleteUser("andy");
        //assertNull(dao.getUserOrNull("andy"));
    }

    public void testPassThroughLogin()
    {
        authenticationService.createAuthentication("andy", "auth1".toCharArray());
        
        authenticationComponent.setCurrentUser("andy");
        assertEquals("andy", authenticationService.getCurrentUserName());
        
    }
    
    private String getUserName(Authentication authentication)
    {
        String username = authentication.getPrincipal().toString();

        if (authentication.getPrincipal() instanceof UserDetails)
        {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return username;
    }
    
    private NamespacePrefixResolver getNamespacePrefixReolsver(String defaultURI)
    {
        DynamicNamespacePrefixResolver nspr = new DynamicNamespacePrefixResolver(null);
        nspr.addDynamicNamespace(NamespaceService.SYSTEM_MODEL_PREFIX, NamespaceService.SYSTEM_MODEL_1_0_URI);
        nspr.addDynamicNamespace(NamespaceService.CONTENT_MODEL_PREFIX, NamespaceService.CONTENT_MODEL_1_0_URI);
        nspr.addDynamicNamespace(ContentModel.USER_MODEL_PREFIX, ContentModel.USER_MODEL_URI);
        nspr.addDynamicNamespace("namespace", "namespace");
        nspr.addDynamicNamespace(NamespaceService.DEFAULT_PREFIX, defaultURI);
        return nspr;
    }
}
