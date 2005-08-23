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
 * Created on 22-Aug-2005
 */
package org.alfresco.repo.security.permissions.impl.acegi;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.acegisecurity.AccessDeniedException;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.afterinvocation.AfterInvocationProvider;

import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.PermissionService;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

public class ACLEntryAfterInvocationProvider implements AfterInvocationProvider, InitializingBean
{

    private static final String AFTER_ACL_NODE = "AFTER_ACL_NODE";

    private static final String AFTER_ACL_PARENT = "AFTER_ACL_PARENT";

    private PermissionService permissionService;

    private NamespacePrefixResolver nspr;

    private NodeService nodeService;

    public ACLEntryAfterInvocationProvider()
    {
        super();
    }

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

    }

    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
            Object returnedObject) throws AccessDeniedException
    {

        if(returnedObject == null)
        {
            return null;
        }
        if (NodeRef.class.isAssignableFrom(returnedObject.getClass()))
        {
            return decide(authentication, object, config, (NodeRef) returnedObject);
        }
        else if (ChildAssociationRef.class.isAssignableFrom(returnedObject.getClass()))
        {
            return decide(authentication, object, config, (ChildAssociationRef) returnedObject);
        }
        else if (ResultSet.class.isAssignableFrom(returnedObject.getClass()))
        {
            return decide(authentication, object, config, (ResultSet) returnedObject);
        }
        else if (Collection.class.isAssignableFrom(returnedObject.getClass()))
        {
            return decide(authentication, object, config, (Collection) returnedObject);
        }
        else if (returnedObject.getClass().isArray())
        {
            return decide(authentication, object, config, (Object[]) returnedObject);
        }
        else
        {
            return returnedObject;
        }
    }

    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
            NodeRef returnedObject) throws AccessDeniedException

    {

        Iterator iter = config.getConfigAttributes();

        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr))
            {
                if (returnedObject == null)
                {
                    return null;
                }

                StringTokenizer st = new StringTokenizer(attr.getAttribute(), ".", false);
                if (st.countTokens() != 3)
                {
                    throw new ACLEntryVoterException("There must be three . separated tokens in each config attribute");
                }
                String typeString = st.nextToken();
                String qNameString = st.nextToken();
                String permissionString = st.nextToken();

                if (!(typeString.equals(AFTER_ACL_NODE) || typeString.equals(AFTER_ACL_PARENT)))
                {
                    throw new ACLEntryVoterException("Invalid type: must be ACL_NODE or ACL_PARENT");
                }

                QName qName = QName.createQName(qNameString, nspr);

                PermissionReference required = new SimplePermissionReference(qName, permissionString);

                NodeRef testNodeRef = null;

                if (typeString.equals(AFTER_ACL_NODE))
                {
                    testNodeRef = returnedObject;
                }
                else if (typeString.equals(AFTER_ACL_PARENT))
                {
                    testNodeRef = nodeService.getPrimaryParent(returnedObject).getParentRef();
                }

                if ((testNodeRef == null) || permissionService.hasPermission(testNodeRef, required))
                {
                    return returnedObject;
                }

            }
        }

        throw new AccessDeniedException("Access Denied");
    }

    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
            ChildAssociationRef returnedObject) throws AccessDeniedException

    {
        Iterator iter = config.getConfigAttributes();

        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr))
            {
                if (returnedObject == null)
                {
                    return null;
                }

                StringTokenizer st = new StringTokenizer(attr.getAttribute(), ".", false);
                if (st.countTokens() != 3)
                {
                    throw new ACLEntryVoterException("There must be three . separated tokens in each config attribute");
                }
                String typeString = st.nextToken();
                String qNameString = st.nextToken();
                String permissionString = st.nextToken();

                if (!(typeString.equals(AFTER_ACL_NODE) || typeString.equals(AFTER_ACL_PARENT)))
                {
                    throw new ACLEntryVoterException("Invalid type: must be ACL_NODE or ACL_PARENT");
                }

                QName qName = QName.createQName(qNameString, nspr);

                PermissionReference required = new SimplePermissionReference(qName, permissionString);

                NodeRef testNodeRef = null;

                if (typeString.equals(AFTER_ACL_NODE))
                {
                    testNodeRef = ((ChildAssociationRef) returnedObject).getChildRef();
                }
                else if (typeString.equals(AFTER_ACL_PARENT))
                {
                    testNodeRef = ((ChildAssociationRef) returnedObject).getParentRef();
                }

                if ((testNodeRef == null) || permissionService.hasPermission(testNodeRef, required))
                {
                    return returnedObject;
                }
            }
        }

        throw new AccessDeniedException("Access Denied");
    }

    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
            ResultSet returnedObject) throws AccessDeniedException

    {
        FilteringResultSet filteringResultSet = new FilteringResultSet((ResultSet) returnedObject);

        Iterator iter = config.getConfigAttributes();

        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr))
            {
                if (returnedObject == null)
                {
                    return null;
                }

                StringTokenizer st = new StringTokenizer(attr.getAttribute(), ".", false);
                if (st.countTokens() != 3)
                {
                    throw new ACLEntryVoterException("There must be three . separated tokens in each config attribute");
                }
                String typeString = st.nextToken();
                String qNameString = st.nextToken();
                String permissionString = st.nextToken();

                if (!(typeString.equals(AFTER_ACL_NODE) || typeString.equals(AFTER_ACL_PARENT)))
                {
                    throw new ACLEntryVoterException("Invalid type: must be ACL_NODE or ACL_PARENT");
                }

                QName qName = QName.createQName(qNameString, nspr);

                PermissionReference required = new SimplePermissionReference(qName, permissionString);

                if (typeString.equals(AFTER_ACL_NODE))
                {

                    for (int i = 0; i < returnedObject.length(); i++)
                    {
                        if (!filteringResultSet.getIncluded(i))
                        {
                            if (permissionService.hasPermission(returnedObject.getNodeRef(i), required))
                            {
                                filteringResultSet.setIncluded(i, true);
                            }
                        }
                    }
                }
                else if (typeString.equals(AFTER_ACL_PARENT))
                {
                    for (int i = 0; i < returnedObject.length(); i++)
                    {
                        if (!filteringResultSet.getIncluded(i))
                        {
                            NodeRef parentRef = returnedObject.getChildAssocRef(i).getParentRef();
                            if ((parentRef == null) || permissionService.hasPermission(parentRef,
                                    required))
                            {
                                filteringResultSet.setIncluded(i, true);
                            }
                        }
                    }
                }
            }
        }

        return filteringResultSet;
    }

    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
            Collection returnedObject) throws AccessDeniedException

    {
        List<ConfigAttributeDefintion> definitions = new ArrayList<ConfigAttributeDefintion>();
        Iterator iter = config.getConfigAttributes();
        Set<Object> removed = new HashSet<Object>();

        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr))
            {
                definitions.add(new ConfigAttributeDefintion(attr));
            }

        }

        for (Object nextObject : returnedObject)
        {
            boolean allowed = false;
            for (ConfigAttributeDefintion cad : definitions)
            {
                NodeRef testNodeRef = null;

                if (cad.typeString.equals(AFTER_ACL_NODE))
                {
                    if (NodeRef.class.isAssignableFrom(nextObject.getClass()))
                    {
                        testNodeRef = (NodeRef) nextObject;
                    }
                    else if (ChildAssociationRef.class.isAssignableFrom(nextObject.getClass()))
                    {
                        testNodeRef = ((ChildAssociationRef) nextObject).getChildRef();
                    }
                    else
                    {
                        throw new ACLEntryVoterException(
                                "The specified parameter is not a collection of NodeRefs or ChildAssociationRefs");
                    }
                }
                else if (cad.typeString.equals(AFTER_ACL_PARENT))
                {
                    if (NodeRef.class.isAssignableFrom(nextObject.getClass()))
                    {
                        testNodeRef = nodeService.getPrimaryParent((NodeRef) nextObject).getParentRef();
                    }
                    else if (ChildAssociationRef.class.isAssignableFrom(nextObject.getClass()))
                    {
                        testNodeRef = ((ChildAssociationRef) nextObject).getParentRef();
                    }
                    else
                    {
                        throw new ACLEntryVoterException(
                                "The specified parameter is not a collection of NodeRefs or ChildAssociationRefs");
                    }
                }

                if (!allowed && ((testNodeRef == null) || permissionService.hasPermission(testNodeRef, cad.required)))
                {
                    allowed = true;
                }
            }
            if (!allowed)
            {
                removed.add(nextObject);
            }
        }
        for (Object toRemove : removed)
        {
            while(returnedObject.remove(toRemove));
        }
        return returnedObject;
    }

    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
            Object[] returnedObject) throws AccessDeniedException

    {
        BitSet incudedSet = new BitSet(returnedObject.length);

        Iterator iter = config.getConfigAttributes();

        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr))
            {
                ConfigAttributeDefintion cad = new ConfigAttributeDefintion(attr);

                for (int i = 0, l = returnedObject.length; i < l; i++)
                {
                    if (!incudedSet.get(i))
                    {
                        Object current = returnedObject[i];

                        NodeRef testNodeRef = null;

                        if (cad.typeString.equals(AFTER_ACL_NODE))
                        {
                            if (NodeRef.class.isAssignableFrom(current.getClass()))
                            {
                                testNodeRef = (NodeRef) current;
                            }
                            else if (ChildAssociationRef.class.isAssignableFrom(current.getClass()))
                            {
                                testNodeRef = ((ChildAssociationRef) current).getChildRef();
                            }
                            else
                            {
                                throw new ACLEntryVoterException(
                                        "The specified array is not of NodeRef or ChildAssociationRef");
                            }
                        }
                        else if (cad.typeString.equals(AFTER_ACL_PARENT))
                        {
                            if (NodeRef.class.isAssignableFrom(current.getClass()))
                            {
                                testNodeRef = nodeService.getPrimaryParent((NodeRef) current).getParentRef();
                            }
                            else if (ChildAssociationRef.class.isAssignableFrom(current.getClass()))
                            {
                                testNodeRef = ((ChildAssociationRef) current).getParentRef();
                            }
                            else
                            {
                                throw new ACLEntryVoterException(
                                        "The specified array is not of NodeRef or ChildAssociationRef");
                            }
                        }

                        if ((testNodeRef == null) || permissionService.hasPermission(testNodeRef, cad.required))
                        {
                            incudedSet.set(i);
                        }
                    }
                }
            }
        }
        
        if(incudedSet.cardinality() == returnedObject.length)
        {
            return returnedObject;
        }
        else
        {
            Object[] answer = new Object[incudedSet.cardinality()];
            for(int i = incudedSet.nextSetBit(0), p = 0; i >= 0; i = incudedSet.nextSetBit(++i), p++)
            {
                answer[p] = returnedObject[i];
            }
            return answer;
        }
    }

    public boolean supports(ConfigAttribute attribute)
    {
        if ((attribute.getAttribute() != null)
                && (attribute.getAttribute().startsWith(AFTER_ACL_NODE) || attribute.getAttribute().startsWith(
                        AFTER_ACL_PARENT)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean supports(Class clazz)
    {
        return (MethodInvocation.class.isAssignableFrom(clazz));
    }

    private class ConfigAttributeDefintion
    {

        String typeString;

        SimplePermissionReference required;

        ConfigAttributeDefintion(ConfigAttribute attr)
        {

            StringTokenizer st = new StringTokenizer(attr.getAttribute(), ".", false);
            if (st.countTokens() != 3)
            {
                throw new ACLEntryVoterException("There must be three . separated tokens in each config attribute");
            }
            typeString = st.nextToken();
            String qNameString = st.nextToken();
            String permissionString = st.nextToken();

            if (!(typeString.equals(AFTER_ACL_NODE) || typeString.equals(AFTER_ACL_PARENT)))
            {
                throw new ACLEntryVoterException("Invalid type: must be ACL_NODE or ACL_PARENT");
            }

            QName qName = QName.createQName(qNameString, nspr);

            required = new SimplePermissionReference(qName, permissionString);
        }
    }
}
