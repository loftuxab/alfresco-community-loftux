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
package org.alfresco.repo.security.permissions.impl;

import java.util.HashSet;
import java.util.Set;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.providers.dao.User;

import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.permissions.AccessPermission;
import org.alfresco.repo.security.permissions.NodePermissionEntry;
import org.alfresco.repo.security.permissions.PermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.PermissionService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;

public class PermissionServiceImpl implements PermissionService
{
    private ModelDAO modelDAO;

    private PermissionsDAO permissionsDAO;

    private NodeService nodeService;

    private DictionaryService dictionaryService;
    
    private AuthenticationService authenticationService;

    public PermissionServiceImpl()
    {
        super();
    }

    //
    // Inversion of control
    //

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setModelDAO(ModelDAO modelDAO)
    {
        this.modelDAO = modelDAO;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setPermissionsDAO(PermissionsDAO permissionsDAO)
    {
        this.permissionsDAO = permissionsDAO;
    }
    
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    //
    // Permissions Service
    //
  
    public Set<AccessPermission> getPermissions(NodeRef nodeRef)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<AccessPermission> getAllPermissions(NodeRef nodeRef)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<PermissionReference> getSettablePermissions(NodeRef nodeRef)
    {
        return modelDAO.getPermissions(nodeRef);
    }

    public Set<PermissionReference> getSettablePermissions(QName type)
    {
        return modelDAO.getPermissions(type);
    }

    public NodePermissionEntry getSetPermissions(NodeRef nodeRef)
    {
        return permissionsDAO.getPermissions(nodeRef);
    }

    public boolean hasPermission(NodeRef nodeRef, PermissionReference perm)
    {
        Authentication auth = authenticationService.getCurrentAuthentication();
        // TODO: Dynamic permissions via evaluators
        Set<PermissionReference> available = modelDAO.getPermissions(nodeRef);
        if (!(available.contains(perm)))
        {
            return false;
        }
        Set<String> authorisations = getAuthorisations(auth);
        Set<Pair<String, PermissionReference>> denied = new HashSet<Pair<String, PermissionReference>>();

        NodeTest nodeTest = new NodeTest(perm, nodeService.getType(nodeRef), nodeService.getAspects(nodeRef));
        ChildAssociationRef car = nodeService.getPrimaryParent(nodeRef);
        while (car != null)
        {
            denied.addAll(nodeTest.getDenied(car.getChildRef()));
            if (nodeTest.evaluate(authorisations, car.getChildRef(), denied))
            {
                return true;
            }

            if (car.getParentRef() != null)
            {
                car = nodeService.getPrimaryParent(car.getParentRef());
            }
            else
            {
                car = null;
            }

        }

        return false;

    }

    private class NodeTest
    {
        PermissionReference required;

        Set<NodeTest> nodeRequirements = new HashSet<NodeTest>();

        Set<NodeTest> parentRequirements = new HashSet<NodeTest>();

        Set<NodeTest> ancestorRequirements = new HashSet<NodeTest>();

        NodeTest(PermissionReference required, QName qName, Set<QName> aspectQNames)
        {
            this(required, true, qName, aspectQNames);
        }

        NodeTest(PermissionReference required, boolean recursive, QName typeQName, Set<QName> aspectQNames)
        {
            this.required = required;
            Set<PermissionReference> requiredNodePermissions = modelDAO.getRequiredNodePermissions(required, typeQName,
                    aspectQNames);
            for (PermissionReference pr : requiredNodePermissions)
            {
                nodeRequirements.add(new NodeTest(pr, typeQName, aspectQNames));
            }
            Set<PermissionReference> requiredParentPermissions = modelDAO.getRequiredParentPermissions(required,
                    typeQName, aspectQNames);
            for (PermissionReference pr : requiredParentPermissions)
            {
                if (!recursive && !required.equals(pr))
                {
                    Set<PermissionReference> requiredParentPermissionsForRequiredParentPermission = modelDAO
                            .getRequiredParentPermissions(pr, typeQName, aspectQNames);
                    if (requiredParentPermissionsForRequiredParentPermission.contains(pr))
                    {
                        ancestorRequirements.add(new NodeTest(pr, false, typeQName, aspectQNames));
                    }
                    else
                    {
                        parentRequirements.add(new NodeTest(pr, typeQName, aspectQNames));
                    }
                }
            }
        }

        boolean evaluate(Set<String> authorisations, NodeRef nodeRef, Set<Pair<String, PermissionReference>> denied)
        {
            Set<Pair<String, PermissionReference>> locallyDenied = new HashSet<Pair<String, PermissionReference>>();
            locallyDenied.addAll(denied);
            boolean success = true;
            success &= checkRequired(authorisations, nodeRef, denied);
            for (NodeTest nt : nodeRequirements)
            {
                success &= nt.evaluate(authorisations, nodeRef, denied);
            }

            locallyDenied.addAll(getDenied(nodeRef));
            for (NodeTest nt : parentRequirements)
            {
                success &= nt.evaluate(authorisations, nodeRef, denied);
            }

            ChildAssociationRef car = nodeService.getPrimaryParent(nodeRef);
            NodePermissionEntry nodeEntry = permissionsDAO.getPermissions(nodeRef);
            while (success && (car.getParentRef() != null) && ((nodeEntry == null) || (nodeEntry.inheritPermissions())))
            {
                car = nodeService.getPrimaryParent(car.getParentRef());
                nodeEntry = permissionsDAO.getPermissions(car.getChildRef());
                locallyDenied.addAll(getDenied(car.getChildRef()));

                for (NodeTest nt : ancestorRequirements)
                {
                    success &= nt.evaluate(authorisations, car.getChildRef(), denied);
                }
            }
            return success;
        }

        boolean checkRequired(Set<String> authorisations, NodeRef nodeRef, Set<Pair<String, PermissionReference>> denied)
        {
            Set<PermissionReference> granters = modelDAO.getGrantingPermissions(required);
            granters.add(SimplePermissionEntry.ALL_PERMISSIONS);

            NodePermissionEntry nodeEntry = permissionsDAO.getPermissions(nodeRef);

            if (nodeEntry == null)
            {
                return false;
            }

            for (PermissionEntry pe : nodeEntry.getPermissionEntries())
            {

                if (isGranted(pe, granters, authorisations, denied))
                {
                    return true;
                }
            }
            return false;
        }

        private boolean isGranted(PermissionEntry pe, Set<PermissionReference> granters, Set<String> authorisations,
                Set<Pair<String, PermissionReference>> denied)
        {
            if(pe.isDenied())
            {
                return false;
            }
            
            for(PermissionReference granter :granters)
            {
                Pair<String, PermissionReference> specific = new Pair<String, PermissionReference>(pe.getAuthority(), granter);
                if(denied.contains(specific))
                {
                    return false;
                }
                
            }
            
            if (authorisations.contains(pe.getAuthority()) && granters.contains(pe.getPermissionReference()))
            {
                return true;
            }
            
            return false;

        }

        Set<Pair<String, PermissionReference>> getDenied(NodeRef nodeRef)
        {
            Set<Pair<String, PermissionReference>> authorisations = new HashSet<Pair<String, PermissionReference>>();

            Set<PermissionReference> granters = modelDAO.getGrantingPermissions(required);
            granters.add(SimplePermissionEntry.ALL_PERMISSIONS);

            NodePermissionEntry nodeEntry = permissionsDAO.getPermissions(nodeRef);
            if (nodeEntry != null)
            {
                for (PermissionEntry pe : nodeEntry.getPermissionEntries())
                {
                    if (granters.contains(pe.getPermissionReference()))
                    {
                        if (pe.isDenied())
                        {
                            authorisations.add(new Pair<String, PermissionReference>(pe.getAuthority(), pe
                                    .getPermissionReference()));
                        }
                    }
                }
            }
            return authorisations;
        }

    }

    private static class Pair<A, B>
    {
        A a;

        B b;

        Pair(A a, B b)
        {
            this.a = a;
            this.b = b;
        }

        A getA()
        {
            return a;
        }

        B getB()
        {
            return b;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (!(this instanceof Pair))
            {
                return false;
            }
            Pair other = (Pair) o;
            return EqualsHelper.nullSafeEquals(this.getA(), other.getA())
                    && EqualsHelper.nullSafeEquals(this.getB(), other.getB());
        }

        @Override
        public int hashCode()
        {
            return (((a == null) ? 0 : a.hashCode()) * 37) + ((b == null) ? 0 : b.hashCode());
        }

    }

    private Set<String> getAuthorisations(Authentication auth)
    {
        HashSet<String> auths = new HashSet<String>();
        User user = (User)auth.getPrincipal();
        auths.add(user.getUsername());
        auths.add(SimplePermissionEntry.ALL_AUTHORITIES);
        return auths;
    }

    public NodePermissionEntry explainPermission(NodeRef nodeRef, PermissionReference perm)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void deletePermissions(NodeRef nodeRef)
    {
        permissionsDAO.deletePermissions(nodeRef);
    }

    public void deletePermissions(NodePermissionEntry nodePermissionEntry)
    {
        permissionsDAO.deletePermissions(nodePermissionEntry);
    }

    public void deletePermission(PermissionEntry permissionEntry)
    {
        permissionsDAO.deletePermissions(permissionEntry);
    }

    public void deletePermission(NodeRef nodeRef, String authority, PermissionReference perm, boolean allow)
    {
        permissionsDAO.deletePermissions(nodeRef, authority, perm, allow);

    }

    public void setPermission(NodeRef nodeRef, String authority, PermissionReference perm, boolean allow)
    {
        permissionsDAO.setPermission(nodeRef, authority, perm, allow);
    }

    public void setPermission(PermissionEntry permissionEntry)
    {
        permissionsDAO.setPermission(permissionEntry);
    }

    public void setPermission(NodePermissionEntry nodePermissionEntry)
    {
        permissionsDAO.setPermission(nodePermissionEntry);
    }

    public void setInheritParentPermissions(NodeRef nodeRef, boolean inheritParentPermissions)
    {
        permissionsDAO.setInheritParentPermissions(nodeRef, inheritParentPermissions);
    }

}
