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
import net.sf.acegisecurity.GrantedAuthority;
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

/**
 * The Alfresco implementation of a permissions service against our APIs for the
 * permissions model and permissions persistence.
 * 
 * 
 * @author andyh
 */
public class PermissionServiceImpl implements PermissionService
{
    /*
     * Access to the mode,
     */
    private ModelDAO modelDAO;

    /*
     * Access to permissions
     */
    private PermissionsDAO permissionsDAO;

    /*
     * Access to the node service
     */
    private NodeService nodeService;

    /*
     * Access to the data dictionary
     */
    private DictionaryService dictionaryService;

    /*
     * Access to the authentication service
     */
    private AuthenticationService authenticationService;

    /*
     * Standard spring construction.
     */
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
        /*
         * Does the current authentication have the supplied permission on the
         * given node.
         */

        // Get the current authentications
        Authentication auth = authenticationService.getCurrentAuthentication();

        //
        // TODO: Dynamic permissions via evaluators
        //

        // If the node does not support the given permission there is no point
        // doing the test
        Set<PermissionReference> available = modelDAO.getPermissions(nodeRef);

        if (!(available.contains(perm)))
        {
            return false;
        }

        // Get the available authorisations
        Set<String> authorisations = getAuthorisations(auth);
        // Keep track of permission that are denied
        Set<Pair<String, PermissionReference>> denied = new HashSet<Pair<String, PermissionReference>>();

        // Build a utility class to carry out a test on a node
        NodeTest nodeTest = new NodeTest(perm, nodeService.getType(nodeRef), nodeService.getAspects(nodeRef));
        // Permissions are only evaluated up the primary parent chain
        // TODO: Do not ignore non primary permissions
        ChildAssociationRef car = nodeService.getPrimaryParent(nodeRef);
        // Work up the parent chain evaluating permissions.
        while (car != null)
        {
            // Add any denied permission to the denied list - these can not then
            // be used to given authentication.
            // A -> B -> C
            // If B denies all permissions to any - allowing all permissions to
            // andy at node A has no effect

            denied.addAll(nodeTest.getDenied(car.getChildRef()));

            // If the current node allows the permission we are done
            // The test includes any parent or ancestor requirements
            if (nodeTest.evaluate(authorisations, car.getChildRef(), denied))
            {
                return true;
            }

            // Build the next element of the evaluation chain
            if (car.getParentRef() != null)
            {
                NodePermissionEntry nodePermissions = permissionsDAO.getPermissions(car.getChildRef());
                if((nodePermissions == null) || (nodePermissions.inheritPermissions()))
                {
                   car = nodeService.getPrimaryParent(car.getParentRef());
                }
                else
                {
                    car = null;
                }
            }
            else
            {
                car = null;
            }

        }

        // TODO: Support meta data permissions on the root node?

        // We have dropped of the end without allowing any permission - so the
        // permission is not allowed.
        return false;

    }

    /**
     * Get the authorisatons for the currently authenticated user
     * @param auth
     * @return
     */
    private Set<String> getAuthorisations(Authentication auth)
    {
        HashSet<String> auths = new HashSet<String>();
        // No authenticated user then no permissions
        if(auth == null)
        {
            return auths;
        }
        // TODO: Refactor and use the authentication service for this.
        User user = (User) auth.getPrincipal();
        auths.add(user.getUsername());
        auths.add(SimplePermissionEntry.ALL_AUTHORITIES);
        for (GrantedAuthority authority : auth.getAuthorities())
        {
            auths.add(authority.getAuthority());
        }
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

    //
    // SUPPORT CLASSES
    //
    
    /**
     * Support class to test the permission on a node.
     * 
     * @author andyh
     */
    private class NodeTest
    {
        /*
         * The required permission.
         */
        PermissionReference required;

        /*
         * The additional permissions required at the node level.
         */
        Set<NodeTest> nodeRequirements = new HashSet<NodeTest>();

        /*
         * The additional permissions required on the parent.
         */
        Set<NodeTest> parentRequirements = new HashSet<NodeTest>();

        /*
         * The permissions required on all ancestors .
         */
        Set<NodeTest> ancestorRequirements = new HashSet<NodeTest>();

        /*
         * The type name of the node.
         */
        QName typeQName;

        /*
         * The aspects set on the node.
         */
        Set<QName> aspectQNames;

        /*
         * The recursive constructor
         */
        NodeTest(PermissionReference required, QName typeQName, Set<QName> aspectQNames)
        {
            this(required, true, typeQName, aspectQNames);
        }

        /*
         * The constructor with recursive control - used for ancestor
         * requirements - or we would go round in circles.
         */
        NodeTest(PermissionReference required, boolean recursive, QName typeQName, Set<QName> aspectQNames)
        {
            this.required = required;
            this.typeQName = typeQName;
            this.aspectQNames = aspectQNames;

            // Set the required node permissions
            Set<PermissionReference> requiredNodePermissions = modelDAO.getRequiredNodePermissions(required, typeQName,
                    aspectQNames);
            for (PermissionReference pr : requiredNodePermissions)
            {
                nodeRequirements.add(new NodeTest(pr, typeQName, aspectQNames));
            }

            // Set the required parent permissions and ancestor permissions
            Set<PermissionReference> requiredParentPermissions = modelDAO.getRequiredParentPermissions(required,
                    typeQName, aspectQNames);
            for (PermissionReference pr : requiredParentPermissions)
            {
                // If we are creating an ancestor requirement we do not add the
                // recursive element - this is taken care of
                // in the recursive test.
                if (recursive || !required.equals(pr))
                {
                    // Test for recursion on the parent - does it depend on its self?
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

        /**
         * Evaluate if a permissions is allowed
         * 
         * @param authorisations - the available authorisations
         * @param nodeRef - the node ref against which to do the tests
         * @param denied - the set of specifically denied permissions
         * @return true if allowed
         */
        boolean evaluate(Set<String> authorisations, NodeRef nodeRef, Set<Pair<String, PermissionReference>> denied)
        {
            // If we have ancestor requirements we have to test the,m recursively and allow for denied permissions
            Set<Pair<String, PermissionReference>> locallyDenied = new HashSet<Pair<String, PermissionReference>>();
            locallyDenied.addAll(denied);
            
            // Start out true and "and" all other results
            boolean success = true;

            // Check the required permissions but not for sets they rely on their underlying permissions
            if(modelDAO.isPermission(required))
            {
               success &= checkRequired(authorisations, nodeRef, denied);
            }
            
            // Check the other permissions required on the node
            for (NodeTest nt : nodeRequirements)
            {
                success &= nt.evaluate(authorisations, nodeRef, denied);
            }

            // Check the permission required of the parent
            ChildAssociationRef car = nodeService.getPrimaryParent(nodeRef);
            if(success && (car.getParentRef() != null))
            {
               locallyDenied.addAll(getDenied(car.getParentRef()));
               for (NodeTest nt : parentRequirements)
               {
                  success &= nt.evaluate(authorisations, car.getParentRef(), locallyDenied);
               }
            }

           
            // Check the ancestor dependencies 
            // If there is no parent then the test will pass
            // Or we run out of parents it will pass 
            NodePermissionEntry nodeEntry = permissionsDAO.getPermissions(nodeRef);
            while (success && (car.getParentRef() != null) && ((nodeEntry == null) || (nodeEntry.inheritPermissions())))
            {
                car = nodeService.getPrimaryParent(car.getParentRef());
                nodeEntry = permissionsDAO.getPermissions(car.getChildRef());
                locallyDenied.addAll(getDenied(car.getChildRef()));

                for (NodeTest nt : ancestorRequirements)
                {
                    success &= nt.evaluate(authorisations, car.getChildRef(), locallyDenied);
                }
            }
            return success;
        }

        /**
         * Check that a given authentication is available on a node
         * 
         * @param authorisations
         * @param nodeRef
         * @param denied
         * @return
         */
        boolean checkRequired(Set<String> authorisations, NodeRef nodeRef, Set<Pair<String, PermissionReference>> denied)
        {
            // Find all the permissions that grant the allowed permission
            // All permissions are treated specially.
            Set<PermissionReference> granters = modelDAO.getGrantingPermissions(required);
            granters.add(SimplePermissionEntry.ALL_PERMISSIONS);

            NodePermissionEntry nodeEntry = permissionsDAO.getPermissions(nodeRef);

            // No permissions set - short cut to deny
            if (nodeEntry == null)
            {
                return false;
            }

            // Check if each permission allows - the first wins.
            // We could have other voting style mechanisms here
            for (PermissionEntry pe : nodeEntry.getPermissionEntries())
            {
                if (isGranted(pe, granters, authorisations, denied, nodeRef))
                {
                    return true;
                }
            }
            return false;
        }

        /**
         * Is a permission granted
         * @param pe - the permissions entry to consider
         * @param granters - the set of granters
         * @param authorisations - the set of authorities 
         * @param denied - the set of denied permissions/authority pais
         * @param nodeRef - the node ref
         * @return
         */
        private boolean isGranted(PermissionEntry pe, Set<PermissionReference> granters, Set<String> authorisations,
                Set<Pair<String, PermissionReference>> denied, NodeRef nodeRef)
        {
            // If the permission entry denies then we just deny
            if (pe.isDenied())
            {
                return false;
            }

            // The permission is allowed but we deny it as it is in the denied set
            Pair<String, PermissionReference> specific = new Pair<String, PermissionReference>(pe.getAuthority(),
                    required);
            if (denied.contains(specific))
            {
                return false;
            }

            // If the permission has a match in both the authorities and granters list it is allowed
            // It applies to the current user and it is granted
            if (authorisations.contains(pe.getAuthority()) && granters.contains(pe.getPermissionReference()))
            {
                {
                    return true;
                }
            }

            // Default deny
            return false;

        }

        /**
         * Get the list of permissions denied for this node.
         * 
         * @param nodeRef
         * @return
         */
        Set<Pair<String, PermissionReference>> getDenied(NodeRef nodeRef)
        {
            Set<Pair<String, PermissionReference>> deniedSet = new HashSet<Pair<String, PermissionReference>>();

            // Loop over all denied permissions
            NodePermissionEntry nodeEntry = permissionsDAO.getPermissions(nodeRef);
            if (nodeEntry != null)
            {
                for (PermissionEntry pe : nodeEntry.getPermissionEntries())
                {
                    if (pe.isDenied())
                    {
                        // All the sets that grant this permission must be denied 
                        // Note that granters includes the orginal permission
                        Set<PermissionReference> granters = modelDAO
                                .getGrantingPermissions(pe.getPermissionReference());
                        for (PermissionReference granter : granters)
                        {
                            deniedSet.add(new Pair<String, PermissionReference>(pe.getAuthority(), granter));
                        }
                        
                        // All the things granted by this permission must be denied
                        Set<PermissionReference> grantees = modelDAO.getGranteePermissions(pe.getPermissionReference());
                        for (PermissionReference grantee : grantees)
                        {
                            deniedSet.add(new Pair<String, PermissionReference>(pe.getAuthority(), grantee));
                        }
                        
                        // All permission excludes all permissions available for the node.
                        if (pe.getPermissionReference().equals(SimplePermissionEntry.ALL_PERMISSIONS))
                        {
                            for (PermissionReference deny : modelDAO.getPermissions(nodeRef))
                            {
                                deniedSet.add(new Pair<String, PermissionReference>(pe.getAuthority(), deny));
                            }
                        }
                    }
                }
            }
            return deniedSet;
        }

    }

    /**
     * Helper class to store a pair of objects which may be null
     * 
     * @author andyh
     */
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


}
