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

import java.util.List;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.context.Context;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.context.security.SecureContext;
import net.sf.acegisecurity.context.security.SecureContextImpl;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import net.sf.acegisecurity.providers.dao.User;

import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

public class AuthenticationComponentImpl implements AuthenticationComponent
{
    private static final String SYSTEM_USER_NAME = "System";
    
    public static final String SYSTEM_FOLDER = "/sys:system";

    public static final String PEOPLE_FOLDER = SYSTEM_FOLDER + "/sys:people";

    private MutableAuthenticationDao authenticationDao;

    private NodeService nodeService;

    private SearchService searchService;

    private DictionaryService dictionaryService;

    private NamespacePrefixResolver namespacePrefixResolver;

    public AuthenticationComponentImpl()
    {
        super();
    }

    public Authentication setCurrentUser(String userName)
    {
        UserDetails ud;
        if(userName.equals(SYSTEM_USER_NAME))
        {
            GrantedAuthority[] gas = new GrantedAuthority[1];
            gas[0] = new GrantedAuthorityImpl("ROLE_SYSTEM");
            ud = new User(SYSTEM_USER_NAME, "", true, true, true, true, gas);
        }
        else
        {
            ud = (UserDetails) authenticationDao.loadUserByUsername(userName);
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(ud, "", ud.getAuthorities());
        auth.setDetails(ud);
        auth.setAuthenticated(true);
        return setCurrentAuthentication(auth);

    }

    public void clearCurrentSecurityContext()
    {
        ContextHolder.setContext(null);
    }

    public Authentication setCurrentAuthentication(Authentication authentication) throws AuthenticationException
    {
        Context context = ContextHolder.getContext();
        SecureContext sc = null;
        if ((context == null) || !(context instanceof SecureContext))
        {
            sc = new SecureContextImpl();
            ContextHolder.setContext(sc);
        }
        else
        {
            sc = (SecureContext) context;
        }
        authentication.setAuthenticated(true);
        sc.setAuthentication(authentication);
        return authentication;
    }

    public NodeRef getPerson(StoreRef storeRef, String userName) throws AuthenticationException
    {
        NodeRef rootNode = nodeService.getRootNode(storeRef);
        QueryParameterDefinition[] defs = new QueryParameterDefinition[1];
        DataTypeDefinition text = dictionaryService.getDataType(DataTypeDefinition.TEXT);
        defs[0] = new QueryParameterDefImpl(QName.createQName("cm", "var", namespacePrefixResolver), text, true,
                userName);
        List<NodeRef> results = searchService.selectNodes(rootNode, PEOPLE_FOLDER
                + "/cm:person[@cm:userName = $cm:var ]", defs, namespacePrefixResolver, false);
        if (results.size() != 1)
        {
            throw new AuthenticationException("No user for " + userName);
        }
        return results.get(0);
    }

    public NodeRef createPerson(StoreRef storeRef, String userName) throws AuthenticationException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Authentication getCurrentAuthentication() throws AuthenticationException
    {
        Context context = ContextHolder.getContext();
        if ((context == null) || !(context instanceof SecureContext))
        {
            return null;
        }
        return ((SecureContext) context).getAuthentication();
    }

    public void setAuthenticationDao(MutableAuthenticationDao authenticationDao)
    {
        this.authenticationDao = authenticationDao;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public Authentication setSystemUserAsCurrentUser()
    {
        return this.setCurrentUser(SYSTEM_USER_NAME);
    }

    public String getSystemUserName()
    {
        return SYSTEM_USER_NAME;
    }

}
