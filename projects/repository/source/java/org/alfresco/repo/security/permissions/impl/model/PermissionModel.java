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
 * Created on 01-Aug-2005
 */
package org.alfresco.repo.security.permissions.impl.model;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.repo.security.permissions.AccessStatus;
import org.alfresco.repo.security.permissions.NodePermissionEntry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class PermissionModel
{
    private static final String NAMESPACES = "namespaces";

    private static final String NAMESPACE = "namespace";

    private static final String NAMESPACE_URI = "uri";

    private static final String NAMESPACE_PREFIX = "prefix";

    private static final String PERMISSION_SET = "permissionSet";

    private static final String NODE_PERMISSIONS = "nodePermissions";
    
    private static final String DENY = "deny";
    
    private static final String ALLOW = "allow";
    
    private static final String DEFAULT_PERMISSION = "defaultPermission";

    private Map<QName, PermissionSet> permissionSets = new HashMap<QName, PermissionSet>();

    private Map<NodeRef, NodePermissionEntry> nodePermissions = new HashMap<NodeRef, NodePermissionEntry>();

    private AccessStatus defaultPermission;
    
    public PermissionModel()
    {
        super();
    }

    public void initialise(File file)
    {
        Document document = createDocument(file);
        Element root = document.getRootElement();
        
        Attribute defaultPermissionAttribute = root.attribute(DEFAULT_PERMISSION);
        if(defaultPermissionAttribute != null)
        {
            if(defaultPermissionAttribute.getStringValue().equalsIgnoreCase(ALLOW))
            {
                defaultPermission = AccessStatus.ALLOWED;  
            }
            else if(defaultPermissionAttribute.getStringValue().equalsIgnoreCase(DENY))
            {
                defaultPermission = AccessStatus.DENIED;  
            }
            else
            {
                throw new PermissionModelException("The default permission must be deny or allow");
            }
        }
        else
        {
            defaultPermission = AccessStatus.DENIED;
        }

        DynamicNamespacePrefixResolver nspr = new DynamicNamespacePrefixResolver();

        // Namespaces

        for (Iterator nsit = root.elementIterator(NAMESPACES); nsit.hasNext(); /**/)
        {
            Element namespacesElement = (Element) nsit.next();
            for (Iterator it = namespacesElement.elementIterator(NAMESPACE); it.hasNext(); /**/)
            {
                Element nameSpaceElement = (Element) it.next();
                nspr.addDynamicNamespace(nameSpaceElement.attributeValue(NAMESPACE_PREFIX), nameSpaceElement.attributeValue(NAMESPACE_URI));
            }
        }

        // Permission Sets

        for (Iterator psit = root.elementIterator(PERMISSION_SET); psit.hasNext(); /**/)
        {
            Element permissionSetElement = (Element) psit.next();
            PermissionSet permissionSet = new PermissionSet();
            permissionSet.initialise(permissionSetElement, nspr);

            permissionSets.put(permissionSet.getQName(), permissionSet);
        }

        // NodePermissions

        for (Iterator npit = root.elementIterator(NODE_PERMISSIONS); npit.hasNext(); /**/)
        {
            Element nodePermissionElement = (Element) npit.next();
            NodePermission nodePermission = new NodePermission();
            nodePermission.initialise(nodePermissionElement, nspr);
        }
    }

    private Document createDocument(File file)
    {
        SAXReader reader = new SAXReader();
        Document document;
        try
        {
            document = reader.read(file);
            return document;
        }
        catch (DocumentException e)
        {
            throw new PermissionModelException("Failed to create permission model document ", e);
        }

    }

    public AccessStatus getDefaultPermission()
    {
        return defaultPermission;
    }

    public Map<NodeRef, NodePermissionEntry> getNodePermissions()
    {
        return Collections.unmodifiableMap(nodePermissions);
    }

    public Map<QName, PermissionSet> getPermissionSets()
    {
        return Collections.unmodifiableMap(permissionSets);
    }
    
    
}
