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
package org.alfresco.repo.security.permissions.impl.acegi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.permissions.PermissionService;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author andyh
 */

public class ACLEntryVoter implements AccessDecisionVoter, InitializingBean
{
    private static Log log = LogFactory.getLog(ACLEntryVoter.class);

    private static final String ACL_NODE = "ACL_NODE";

    private static final String ACL_PARENT = "ACL_PARENT";

    private PermissionService permissionService;

    private NamespacePrefixResolver nspr;

    private NodeService nodeService;

    private AuthenticationService authenticationService;

    public ACLEntryVoter()
    {
        super();
    }

    // ~ Methods
    // ================================================================

    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    public PermissionService getPermissionService()
    {
        return permissionService;
    }

    public NamespacePrefixResolver getNamespacePrefixResolver()
    {
        return nspr;
    }

    public void setNamespacePrefixResolver(NamespacePrefixResolver nspr)
    {
        this.nspr = nspr;
    }

    public NodeService getNodeService()
    {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public AuthenticationService getAuthenticationService()
    {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public void afterPropertiesSet() throws Exception
    {
        if (permissionService == null)
        {
            throw new IllegalArgumentException("There must be a permission service");
        }
        if (nspr == null)
        {
            throw new IllegalArgumentException("There must be a namespace service");
        }
        if (nodeService == null)
        {
            throw new IllegalArgumentException("There must be a node service");
        }
        if (authenticationService == null)
        {
            throw new IllegalArgumentException("There must be an authentication service");
        }

    }

    public boolean supports(ConfigAttribute attribute)
    {
        if ((attribute.getAttribute() != null)
                && (attribute.getAttribute().startsWith(ACL_NODE) || attribute.getAttribute().startsWith(ACL_PARENT)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * This implementation supports only <code>MethodSecurityInterceptor</code>,
     * because it queries the presented <code>MethodInvocation</code>.
     * 
     * @param clazz
     *            the secure object
     * 
     * @return <code>true</code> if the secure object is
     *         <code>MethodInvocation</code>, <code>false</code> otherwise
     */
    public boolean supports(Class clazz)
    {
        return (MethodInvocation.class.isAssignableFrom(clazz));
    }

    public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config)
    {
        if (log.isDebugEnabled())
        {
            MethodInvocation mi = (MethodInvocation) object;
            log.debug("Method: " + mi.getMethod().toString());
        }
        if (authenticationService.isCurrentUserTheSystemUser())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Access granted for the system user");
            }
            return AccessDecisionVoter.ACCESS_GRANTED;
        }

        List<ConfigAttributeDefintion> supportedDefinitions = extractSupportedDefinitions(config);

        if (supportedDefinitions.size() == 0)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }

        MethodInvocation invocation = (MethodInvocation) object;

        Method method = invocation.getMethod();
        Class[] params = method.getParameterTypes();

        Iterator iter = config.getConfigAttributes();

        for (ConfigAttributeDefintion cad : supportedDefinitions)
        {
            NodeRef testNodeRef = null;

            if (cad.typeString.equals(ACL_NODE))
            {
                if (StoreRef.class.isAssignableFrom(params[cad.parameter]))
                {
                    if (invocation.getArguments()[cad.parameter] != null)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("\tPermission test against the store - using permissions on the root node");
                        }
                        StoreRef storeRef = (StoreRef) invocation.getArguments()[cad.parameter];
                        if (nodeService.exists(storeRef))
                        {
                            testNodeRef = nodeService.getRootNode(storeRef);
                        }
                    }
                }
                else if (NodeRef.class.isAssignableFrom(params[cad.parameter]))
                {
                    testNodeRef = (NodeRef) invocation.getArguments()[cad.parameter];
                    if (log.isDebugEnabled())
                    {
                        log.debug("\tPermission test on node " + nodeService.getPath(testNodeRef));
                    }
                }
                else if (ChildAssociationRef.class.isAssignableFrom(params[cad.parameter]))
                {
                    if (invocation.getArguments()[cad.parameter] != null)
                    {
                        testNodeRef = ((ChildAssociationRef) invocation.getArguments()[cad.parameter]).getChildRef();
                        if (log.isDebugEnabled())
                        {
                            log.debug("\tPermission test on node " + nodeService.getPath(testNodeRef));
                        }
                    }
                }
                else
                {
                    throw new ACLEntryVoterException("The specified parameter is not a NodeRef or ChildAssociationRef");
                }
            }
            else if (cad.typeString.equals(ACL_PARENT))
            {
                // There is no point having parent permissions for store
                // refs
                if (NodeRef.class.isAssignableFrom(params[cad.parameter]))
                {
                    NodeRef child = (NodeRef) invocation.getArguments()[cad.parameter];
                    if (child != null)
                    {
                        testNodeRef = nodeService.getPrimaryParent(child).getParentRef();
                        if (log.isDebugEnabled())
                        {
                            log.debug("\tPermission test for parent on node " + nodeService.getPath(testNodeRef));
                        }
                    }
                }
                else if (ChildAssociationRef.class.isAssignableFrom(params[cad.parameter]))
                {
                    if (invocation.getArguments()[cad.parameter] != null)
                    {
                        testNodeRef = ((ChildAssociationRef) invocation.getArguments()[cad.parameter]).getParentRef();
                        if (log.isDebugEnabled())
                        {
                            log.debug("\tPermission test for parent on child assoc ref for node "
                                    + nodeService.getPath(testNodeRef));
                        }
                    }

                }
                else
                {
                    throw new ACLEntryVoterException("The specified parameter is not a ChildAssociationRef");
                }
            }

            if (testNodeRef != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("\t\tNode ref is not null");
                }
                if (!permissionService.hasPermission(testNodeRef, cad.required))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
            }
        }

        return AccessDecisionVoter.ACCESS_GRANTED;
    }

    private List<ConfigAttributeDefintion> extractSupportedDefinitions(ConfigAttributeDefinition config)
    {
        List<ConfigAttributeDefintion> definitions = new ArrayList<ConfigAttributeDefintion>();
        Iterator iter = config.getConfigAttributes();

        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr))
            {
                definitions.add(new ConfigAttributeDefintion(attr));
            }

        }
        return definitions;
    }

    private class ConfigAttributeDefintion
    {
        String typeString;

        SimplePermissionReference required;

        int parameter;

        ConfigAttributeDefintion(ConfigAttribute attr)
        {
            StringTokenizer st = new StringTokenizer(attr.getAttribute(), ".", false);
            if (st.countTokens() != 4)
            {
                throw new ACLEntryVoterException("There must be four . separated tokens in each config attribute");
            }
            typeString = st.nextToken();
            String numberString = st.nextToken();
            String qNameString = st.nextToken();
            String permissionString = st.nextToken();

            if (!(typeString.equals(ACL_NODE) || typeString.equals(ACL_PARENT)))
            {
                throw new ACLEntryVoterException("Invalid type: must be ACL_NODE or ACL_PARENT");
            }

            parameter = Integer.parseInt(numberString);

            QName qName = QName.createQName(qNameString, nspr);

            required = new SimplePermissionReference(qName, permissionString);

        }
    }
}
