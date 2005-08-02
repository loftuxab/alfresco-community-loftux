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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.impl.PermissionReferenceImpl;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class PermissionGroup implements XMLModelInitialisable
{
    private static final String NAME = "name";

    private static final String ALLOW_FULL_CONTOL = "allowFullControl";

    private static final String INCLUDE_PERMISSION_GROUP = "includePermissionGroup";

    private static final String PERMISSION_GROUP = "permissionGroup";

    private static final String TYPE = "type";

    private String name;

    private boolean allowFullControl;

    private QName container;

    private Set<PermissionReference> includedPermissionGroups = new HashSet<PermissionReference>();

    public PermissionGroup(QName container)
    {
        super();
        this.container = container;
    }

    public void initialise(Element element, NamespacePrefixResolver nspr)
    {
        // Name
        name = element.attributeValue(NAME);
        // Allow full control
        Attribute att = element.attribute(ALLOW_FULL_CONTOL);
        if (att != null)
        {
            allowFullControl = Boolean.parseBoolean(att.getStringValue());
        }
        else
        {
            allowFullControl = false;
        }
        // Include permissions defined for other permission groups

        for (Iterator ipgit = element.elementIterator(INCLUDE_PERMISSION_GROUP); ipgit.hasNext(); /**/)
        {
            QName qName;
            Element includePermissionGroupElement = (Element) ipgit.next();
            Attribute typeAttribute = includePermissionGroupElement.attribute(TYPE);
            if (typeAttribute != null)
            {
                qName = QName.createQName(typeAttribute.getStringValue(), nspr);
            }
            else
            {
                qName = container;
            }
            String refName = includePermissionGroupElement.attributeValue(PERMISSION_GROUP);
            PermissionReference permissionReference = new PermissionReferenceImpl(qName, refName);
            includedPermissionGroups.add(permissionReference);
        }
    }

    public Set<PermissionReference> getIncludedPermissionGroups()
    {
        return Collections.unmodifiableSet(includedPermissionGroups);
    }

    public String getName()
    {
        return name;
    }

    public boolean isAllowFullControl()
    {
        return allowFullControl;
    }
    
    
}
