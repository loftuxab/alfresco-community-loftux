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

import org.alfresco.repo.security.permissions.AccessStatus;
import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.impl.PermissionReferenceImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class ModelPermissionEntry implements PermissionEntry, XMLModelInitialisable
{
    private static final String PERMISSION_REFERENCE = "permissionReference";

    private static final String RECIPIENT = "recipient";

    private static final String ACCESS = "access";

    private static final String DENY = "deny";

    private static final String ALLOW = "allow";

    private static final String TYPE = "type";
    
    private static final String NAME = "name";

    private String recipient;

    private AccessStatus access;

    private PermissionReference permissionReference;

    private NodeRef nodeRef;

    public ModelPermissionEntry(NodeRef nodeRef)
    {
        super();
        this.nodeRef = nodeRef;
    }

    public PermissionReference getPermissionReference()
    {
        return permissionReference;
    }

    public String getAuthority()
    {
        return getRecipient();
    }

    public String getRecipient()
    {
        return recipient;
    }

    public NodeRef getNodeRef()
    {
        return nodeRef;
    }

    public boolean isDenied()
    {
        return access == AccessStatus.DENIED;
    }

    public boolean isAllowed()
    {
        return access == AccessStatus.ALLOWED;
    }

    public AccessStatus getAccessStatus()
    {
        return access;
    }

    public void initialise(Element element, NamespacePrefixResolver nspr)
    {
        Attribute recipientAttribute = element.attribute(RECIPIENT);
        if (recipientAttribute != null)
        {
            recipient = recipientAttribute.getStringValue();
        }
        else
        {
            recipient = null;
        }

        Attribute accessAttribute = element.attribute(ACCESS);
        if (accessAttribute != null)
        {
            if (accessAttribute.getStringValue().equalsIgnoreCase(ALLOW))
            {
                access = AccessStatus.ALLOWED;
            }
            else if (accessAttribute.getStringValue().equalsIgnoreCase(DENY))
            {
                access = AccessStatus.DENIED;
            }
            else
            {
                throw new PermissionModelException("The default permission must be deny or allow");
            }
        }
        else
        {
            access = AccessStatus.DENIED;
        }
        
        
        Element permissionReferenceElement = element.element(PERMISSION_REFERENCE);
        QName typeQName = QName.createQName(permissionReferenceElement.attributeValue(TYPE), nspr);
        String name = permissionReferenceElement.attributeValue(NAME);
        permissionReference = new PermissionReferenceImpl(typeQName, name);
    }
}
