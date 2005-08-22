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
 *
 * Created on 19-Aug-2005
 */
package org.alfresco.repo.security.permissions.impl.acegi;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.PermissionService;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author andyh
 */

public class ACLEntryVoter implements AccessDecisionVoter, InitializingBean
{
    private static final String ACL_NODE = "ACL_NODE";

    private static final String ACL_PARENT = "ACL_PARENT";

    private PermissionService permissionService;

    private NamespacePrefixResolver nspr;

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
        int defaultResult = AccessDecisionVoter.ACCESS_ABSTAIN;

        MethodInvocation invocation = (MethodInvocation) object;

        Method method = invocation.getMethod();
        Class[] params = method.getParameterTypes();

        Iterator iter = config.getConfigAttributes();

        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr))
            {
                defaultResult = AccessDecisionVoter.ACCESS_DENIED;
                StringTokenizer st = new StringTokenizer(attr.getAttribute(), ".", false);
                if (st.countTokens() != 4)
                {
                    throw new ACLEntryVoterException("There must be four . separated tokens in each config attribute");
                }
                String typeString = st.nextToken();
                String numberString = st.nextToken();
                String qNameString = st.nextToken();
                String permissionString = st.nextToken();

                if (!(typeString.equals(ACL_NODE) || typeString.equals(ACL_PARENT)))
                {
                    throw new ACLEntryVoterException("Invalid type: must be ACL_NODE or ACL_PARENT");
                }

                int parameter = Integer.parseInt(numberString);

                QName qName = QName.createQName(qNameString, nspr);

                PermissionReference required = new SimplePermissionReference(qName, permissionString);

                NodeRef testNodeRef = null;

                if (typeString.equals(ACL_NODE))
                {
                    if (NodeRef.class.isAssignableFrom(params[parameter]))
                    {
                        testNodeRef = (NodeRef) invocation.getArguments()[parameter];
                    }
                    else if (ChildAssociationRef.class.isAssignableFrom(params[parameter]))
                    {
                        if (invocation.getArguments()[parameter] != null)
                        {
                            testNodeRef = ((ChildAssociationRef) invocation.getArguments()[parameter]).getChildRef();
                        }
                    }
                    else
                    {
                        throw new ACLEntryVoterException(
                                "The specified parameter is not a NodeRef or ChildAssociationRef");
                    }
                }
                else if (typeString.equals(ACL_PARENT))
                {
                    if (ChildAssociationRef.class.isAssignableFrom(params[parameter]))
                    {
                        if (invocation.getArguments()[parameter] != null)
                        {
                            testNodeRef = ((ChildAssociationRef) invocation.getArguments()[parameter]).getParentRef();
                        }
                    }
                    else
                    {
                        throw new ACLEntryVoterException("The specified parameter is not a ChildAssociationRef");
                    }
                }

                if (testNodeRef != null)
                {
                    if (permissionService.hasPermission(testNodeRef, required))
                    {
                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
                else
                {
                    // We allow access to null objects
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }

            }
        }

        // No configuration attribute matched, so abstain
        return defaultResult;
    }
}
