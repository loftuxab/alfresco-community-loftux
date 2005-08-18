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

import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Store and read the definition of a permission set
 * @author andyh
 */
public class PermissionSet implements XMLModelInitialisable
{
    private static final String TYPE = "type";
    private static final String PERMISSION_GROUP = "permissionGroup";
    private static final String PERMISSION = "permission";
    private static final String EXPOSE = "expose";
    private static final String EXPOSE_ALL = "all";
    private static final String EXPOSE_SELECTED = "selected";
    
    
    private QName qname;
    
    private boolean exposeAll;
    
    private Set<PermissionGroup> permissionGroups = new HashSet<PermissionGroup>();
    
    private Set<Permission> permissions = new HashSet<Permission>();
    
    public PermissionSet()
    {
        super();
    }
    
    public void initialise(Element element, NamespacePrefixResolver nspr)
    {
        qname = QName.createQName(element.attributeValue(TYPE), nspr);
        
        Attribute exposeAttribute = element.attribute(EXPOSE);
        if(exposeAttribute != null)
        {
            exposeAll = exposeAttribute.getStringValue().equalsIgnoreCase(EXPOSE_ALL);
        }
        else
        {
            exposeAll = true;
        }
        
        for(Iterator pgit = element.elementIterator(PERMISSION_GROUP); pgit.hasNext(); /**/)
        {
            Element permissionGroupElement = (Element)pgit.next();
            PermissionGroup permissionGroup = new PermissionGroup(qname);
            permissionGroup.initialise(permissionGroupElement, nspr);
            permissionGroups.add(permissionGroup);
        }
        
        for(Iterator pit = element.elementIterator(PERMISSION); pit.hasNext(); /**/)
        {
            Element permissionElement = (Element)pit.next();
            Permission permission = new Permission(qname);
            permission.initialise(permissionElement, nspr);
            permissions.add(permission);
        }
        
    }

    public Set<PermissionGroup> getPermissionGroups()
    {
        return Collections.unmodifiableSet(permissionGroups);
    }

    public Set<Permission> getPermissions()
    {
        return Collections.unmodifiableSet(permissions);
    }

    public QName getQName()
    {
        return qname;
    }

    public boolean exposeAll()
    {
        return exposeAll;
    }
    
    

}
