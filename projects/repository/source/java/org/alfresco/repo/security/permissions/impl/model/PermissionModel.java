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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.security.permissions.AccessStatus;
import org.alfresco.repo.security.permissions.NodePermissionEntry;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.impl.ModelDAO;
import org.alfresco.repo.security.permissions.impl.SimplePermissionEntry;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;

/**
 * The implementation of the model DAO
 * 
 * Reads and stores the top level model information
 * 
 * Encapsulates access to this information
 * 
 * @author andyh
 */
public class PermissionModel implements ModelDAO, InitializingBean
{
    // IOC

    private NodeService nodeService;

    private DictionaryService dictionaryService;

    // XML Constants

    private static final String NAMESPACES = "namespaces";

    private static final String NAMESPACE = "namespace";

    private static final String NAMESPACE_URI = "uri";

    private static final String NAMESPACE_PREFIX = "prefix";

    private static final String PERMISSION_SET = "permissionSet";

    private static final String NODE_PERMISSIONS = "nodePermissions";

    private static final String DENY = "deny";

    private static final String ALLOW = "allow";

    private static final String DEFAULT_PERMISSION = "defaultPermission";

    // Instance variables

    private String model;

    private Map<QName, PermissionSet> permissionSets = new HashMap<QName, PermissionSet>();

    private Map<NodeRef, NodePermissionEntry> nodePermissions = new HashMap<NodeRef, NodePermissionEntry>();

    private AccessStatus defaultPermission;

    // Cache granting permissions
    private HashMap<PermissionReference, Set<PermissionReference>> grantingPermissions = new HashMap<PermissionReference, Set<PermissionReference>>();

    // Cache grantees
    private HashMap<PermissionReference, Set<PermissionReference>> granteePermissions = new HashMap<PermissionReference, Set<PermissionReference>>();

    // Cache the mapping of extended groups to the base
    private HashMap<PermissionGroup, PermissionGroup> groupsToBaseGroup = new HashMap<PermissionGroup, PermissionGroup>();

    public PermissionModel()
    {
        super();
    }

    // IOC

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /*
     * Initialise from file
     * 
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */

    public void afterPropertiesSet()
    {
        Document document = createDocument(model);
        Element root = document.getRootElement();

        Attribute defaultPermissionAttribute = root.attribute(DEFAULT_PERMISSION);
        if (defaultPermissionAttribute != null)
        {
            if (defaultPermissionAttribute.getStringValue().equalsIgnoreCase(ALLOW))
            {
                defaultPermission = AccessStatus.ALLOWED;
            }
            else if (defaultPermissionAttribute.getStringValue().equalsIgnoreCase(DENY))
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
                nspr.addDynamicNamespace(nameSpaceElement.attributeValue(NAMESPACE_PREFIX), nameSpaceElement
                        .attributeValue(NAMESPACE_URI));
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

    /*
     * Create the XML document from the file location
     */
    private Document createDocument(String model)
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(model);
        if (is == null)
        {
            throw new PermissionModelException("File not found: " + model);
        }
        SAXReader reader = new SAXReader();
        try
        {
            Document document = reader.read(is);
            is.close();
            return document;
        }
        catch (DocumentException e)
        {
            throw new PermissionModelException("Failed to create permission model document ", e);
        }
        catch (IOException e)
        {
            throw new PermissionModelException("Failed to close permission model document ", e);
        }

    }

    public AccessStatus getDefaultPermission()
    {
        return defaultPermission;
    }

    public AccessStatus getDefaultPermission(PermissionReference pr)
    {
        for (PermissionSet ps : permissionSets.values())
        {
            for (Permission p : ps.getPermissions())
            {
                if (p.equals(pr))
                {
                    return p.getDefaultPermission();
                }
            }
        }
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

    public Set<PermissionReference> getPermissions(QName type)
    {
        Set<PermissionReference> permissions = new HashSet<PermissionReference>();
        if (dictionaryService.getClass(type).isAspect())
        {
            addAspectPermissions(type, permissions);
        }
        else
        {
            addTypePermissions(type, permissions);
        }
        return permissions;
    }

    /**
     * Support to add permissions for types
     * 
     * @param type
     * @param permissions
     */
    private void addTypePermissions(QName type, Set<PermissionReference> permissions)
    {
        TypeDefinition typeDef = dictionaryService.getType(type);
        if (typeDef.getParentName() != null)
        {
            addTypePermissions(typeDef.getParentName(), permissions);
        }
        for (AspectDefinition ad : typeDef.getDefaultAspects())
        {
            addAspectPermissions(ad.getName(), permissions);
        }
        mergePermissions(permissions, type);
    }

    /**
     * Support to add permissions for aspects.
     * 
     * @param type
     * @param permissions
     */
    private void addAspectPermissions(QName type, Set<PermissionReference> permissions)
    {
        AspectDefinition aspectDef = dictionaryService.getAspect(type);
        if (aspectDef.getParentName() != null)
        {
            addTypePermissions(aspectDef.getParentName(), permissions);
        }
        mergePermissions(permissions, type);
    }

    /**
     * Support to merge permissions together. Respects extended permissions.
     * 
     * @param target
     * @param type
     */
    private void mergePermissions(Set<PermissionReference> target, QName type)
    {
        PermissionSet permissionSet = permissionSets.get(type);
        if (permissionSet != null)
        {
            for (PermissionGroup pg : permissionSet.getPermissionGroups())
            {
                if (!pg.isExtends())
                {
                    target.add(pg);
                }
            }
            target.addAll(permissionSet.getPermissions());
        }
    }

    public Set<PermissionReference> getPermissions(NodeRef nodeRef)
    {
        QName typeName = nodeService.getType(nodeRef);
        Set<PermissionReference> permissions = getPermissions(typeName);
        // Add non mandatory aspects..
        Set<QName> defaultAspects = new HashSet<QName>();
        for (AspectDefinition aspDef : dictionaryService.getType(typeName).getDefaultAspects())
        {
            defaultAspects.add(aspDef.getName());
        }
        for (QName aspect : nodeService.getAspects(nodeRef))
        {
            if (!defaultAspects.contains(aspect))
            {
                addAspectPermissions(aspect, permissions);
            }
        }
        return permissions;
    }

    public synchronized Set<PermissionReference> getGrantingPermissions(PermissionReference permissionReference)
    {
        // Cache the results
        Set<PermissionReference> granters = grantingPermissions.get(permissionReference);
        if (granters == null)
        {
            granters = getGrantingPermissionsImpl(permissionReference);
            grantingPermissions.put(permissionReference, granters);
        }
        return granters;
    }

    private Set<PermissionReference> getGrantingPermissionsImpl(PermissionReference permissionReference)
    {
        // Query the model
        HashSet<PermissionReference> permissions = new HashSet<PermissionReference>();
        permissions.add(permissionReference);
        for (PermissionSet ps : permissionSets.values())
        {
            for (PermissionGroup pg : ps.getPermissionGroups())
            {
                if (pg.getIncludedPermissionGroups().contains(permissionReference))
                {
                    permissions.add(getBasePermissionGroup(pg));
                }
                if (pg.isAllowFullControl())
                {
                    permissions.add(pg);
                }
            }
            for (Permission p : ps.getPermissions())
            {
                if (p.equals(permissionReference))
                {
                    for (PermissionReference pg : p.getGrantedToGroups())
                    {
                        permissions.add(getBasePermissionGroup(getPermissionGroup(pg)));
                    }
                }
                for (RequiredPermission rp : p.getRequiredPermissions())
                {
                    if (rp.equals(permissionReference) && rp.isImplies())
                    {
                        permissions.add(p);
                        break;
                    }
                }
            }
        }
        return permissions;
    }

    public synchronized Set<PermissionReference> getGranteePermissions(PermissionReference permissionReference)
    {
        // Cache the results
        Set<PermissionReference> grantees = granteePermissions.get(permissionReference);
        if (grantees == null)
        {
            grantees = getGranteePermissionsImpl(permissionReference);
            granteePermissions.put(permissionReference, grantees);
        }
        return grantees;
    }

    private Set<PermissionReference> getGranteePermissionsImpl(PermissionReference permissionReference)
    {
        // Query the model
        HashSet<PermissionReference> permissions = new HashSet<PermissionReference>();
        permissions.add(permissionReference);
        for (PermissionSet ps : permissionSets.values())
        {
            for (PermissionGroup pg : ps.getPermissionGroups())
            {
                if (pg.equals(permissionReference))
                {
                    for (PermissionReference included : pg.getIncludedPermissionGroups())
                    {
                        permissions.addAll(getGranteePermissions(included));
                    }
                }
                if (pg.isAllowFullControl())
                {
                    permissions.add(SimplePermissionEntry.ALL_PERMISSIONS);
                }
                if (pg.isExtends())
                {
                    if (pg.getTypeQName() != null)
                    {
                        permissions.addAll(getGranteePermissions(new SimplePermissionReference(pg.getTypeQName(), pg
                                .getName())));
                    }
                    else
                    {
                        ClassDefinition classDefinition = dictionaryService.getClass(pg.getQName());
                        QName parent = classDefinition.getParentName();
                        if (parent != null)
                        {
                            classDefinition = dictionaryService.getClass(parent);
                            PermissionGroup attempt = getPermissionGroupOrNull(new SimplePermissionReference(parent, pg
                                    .getName()));
                            if (attempt != null)
                            {
                                permissions.addAll(getGranteePermissions(attempt));
                            }
                        }
                    }
                }
            }
            PermissionGroup baseGroup = getBasePermissionGroupOrNull(getPermissionGroupOrNull(permissionReference));
            if (baseGroup != null)
            {
                for (Permission p : ps.getPermissions())
                {
                    for (PermissionReference grantedTo : p.getGrantedToGroups())
                    {
                        PermissionGroup base = getBasePermissionGroupOrNull(getPermissionGroupOrNull(grantedTo));
                        if (baseGroup.equals(base))
                        {
                            permissions.add(p);
                        }
                    }
                }
            }
        }
        return permissions;
    }

    /**
     * Support to find permission groups
     * 
     * @param target
     * @return
     */
    private PermissionGroup getPermissionGroupOrNull(PermissionReference target)
    {
        for (PermissionSet ps : permissionSets.values())
        {
            for (PermissionGroup pg : ps.getPermissionGroups())
            {
                if (pg.equals(target))
                {
                    return pg;
                }
            }
        }
        return null;
    }

    /**
     * Support to get a permission group
     * 
     * @param target
     * @return
     */
    private PermissionGroup getPermissionGroup(PermissionReference target)
    {
        PermissionGroup pg = getPermissionGroupOrNull(target);
        if (pg == null)
        {
            throw new PermissionModelException("There is no permission group :"
                    + target.getQName() + " " + target.getName());
        }
        return pg;
    }

    /**
     * Get the base permission group for a given permission group.
     * 
     * @param pg
     * @return
     */
    private synchronized PermissionGroup getBasePermissionGroupOrNull(PermissionGroup pg)
    {
        if (groupsToBaseGroup.containsKey(pg))
        {
            return groupsToBaseGroup.get(pg);
        }
        else
        {
            PermissionGroup answer = getBasePermissionGroupOrNullImpl(pg);
            groupsToBaseGroup.put(pg, answer);
            return answer;
        }
    }

    /**
     * Query the model for a base permission group
     * 
     * Uses the Data Dictionary to reolve inheritance
     * 
     * @param pg
     * @return
     */
    private PermissionGroup getBasePermissionGroupOrNullImpl(PermissionGroup pg)
    {
        if (pg == null)
        {
            return null;
        }
        if (pg.isExtends())
        {
            if (pg.getTypeQName() != null)
            {
                return getPermissionGroup(new SimplePermissionReference(pg.getTypeQName(), pg.getName()));
            }
            else
            {
                ClassDefinition classDefinition = dictionaryService.getClass(pg.getQName());
                QName parent;
                while ((parent = classDefinition.getParentName()) != null)
                {
                    classDefinition = dictionaryService.getClass(parent);
                    PermissionGroup attempt = getPermissionGroupOrNull(new SimplePermissionReference(parent, pg
                            .getName()));
                    if ((attempt != null) && (!attempt.isExtends()))
                    {
                        return attempt;
                    }
                }
                return null;
            }
        }
        else
        {
            return pg;
        }
    }

    private PermissionGroup getBasePermissionGroup(PermissionGroup target)
    {
        PermissionGroup pg = getBasePermissionGroupOrNull(target);
        if (pg == null)
        {
            throw new PermissionModelException("There is no parent for permission group :"
                    + target.getQName() + " " + target.getName());
        }
        return pg;
    }

    public Set<PermissionReference> getRequiredNodePermissions(PermissionReference required, QName qName,
            Set<QName> aspectQNames)
    {
        return getRequiredPermissions(required, qName, aspectQNames, RequiredPermission.On.NODE);
    }

    public Set<PermissionReference> getRequiredParentPermissions(PermissionReference required, QName qName,
            Set<QName> aspectQNames)
    {
        return getRequiredPermissions(required, qName, aspectQNames, RequiredPermission.On.PARENT);
    }

    /**
     * Utility method to determine required permissions
     * 
     * @param required
     * @param qName
     * @param aspectQNames
     * @param on
     * @return
     */
    private Set<PermissionReference> getRequiredPermissions(PermissionReference required, QName qName,
            Set<QName> aspectQNames, RequiredPermission.On on)
    {
        PermissionGroup pg = getBasePermissionGroupOrNull(getPermissionGroupOrNull(required));
        if (pg == null)
        {
            return getRequirementsForPermission(required, on);
        }
        else
        {
            return getRequirementsForPermissionGroup(pg, on, qName, aspectQNames);
        }
    }

    /**
     * Get the requirements for a permission
     * 
     * @param required
     * @param on
     * @return
     */
    private Set<PermissionReference> getRequirementsForPermission(PermissionReference required, RequiredPermission.On on)
    {
        HashSet<PermissionReference> requiredPermissions = new HashSet<PermissionReference>();
        Permission p = getPermissionOrNull(required);
        if (p != null)
        {
            for (RequiredPermission rp : p.getRequiredPermissions())
            {
                if (!rp.isImplies() && rp.getOn().equals(on))
                {
                    requiredPermissions.add(rp);
                }
            }
        }
        return requiredPermissions;
    }

    /**
     * Get the requirements for a permission set
     * @param target
     * @param on
     * @param qName
     * @param aspectQNames
     * @return
     */
    private Set<PermissionReference> getRequirementsForPermissionGroup(PermissionGroup target,
            RequiredPermission.On on, QName qName, Set<QName> aspectQNames)
    {
        HashSet<PermissionReference> requiredPermissions = new HashSet<PermissionReference>();
        if (target == null)
        {
            return requiredPermissions;
        }
        for (PermissionSet ps : permissionSets.values())
        {
            for (PermissionGroup pg : ps.getPermissionGroups())
            {
                if (target.equals(getBasePermissionGroupOrNull(pg))
                        && isPartOfDynamicPermissionGroup(pg, qName, aspectQNames))
                {
                    // Add includes
                    for (PermissionReference pr : pg.getIncludedPermissionGroups())
                    {
                        requiredPermissions.addAll(getRequirementsForPermissionGroup(
                                getBasePermissionGroupOrNull(getPermissionGroupOrNull(pr)), on, qName, aspectQNames));
                    }
                }
            }
            for (Permission p : ps.getPermissions())
            {
                for (PermissionReference grantedTo : p.getGrantedToGroups())
                {
                    PermissionGroup base = getBasePermissionGroupOrNull(getPermissionGroupOrNull(grantedTo));
                    if (target.equals(base) && isPartOfDynamicPermissionGroup(grantedTo, qName, aspectQNames))
                    {
                        if (on == RequiredPermission.On.NODE)
                        {
                            requiredPermissions.add(p);
                        }
                    }
                }
            }
        }
        return requiredPermissions;
    }

    /**
     * Check type specifc extension of permission sets.
     * 
     * @param pr
     * @param typeQname
     * @param aspects
     * @return
     */
    private boolean isPartOfDynamicPermissionGroup(PermissionReference pr, QName typeQname, Set<QName> aspects)
    {
        if (dictionaryService.isSubClass(typeQname, pr.getQName()))
        {
            return true;
        }
        for (QName aspect : aspects)
        {
            if (dictionaryService.isSubClass(aspect, pr.getQName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Utility method to find a permission
     * 
     * @param perm
     * @return
     */
    private Permission getPermissionOrNull(PermissionReference perm)
    {
        for (PermissionSet ps : permissionSets.values())
        {

            for (Permission p : ps.getPermissions())
            {
                if (p.equals(perm))
                {
                    return p;
                }
            }
        }
        return null;
    }

}
