/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.capability;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.vote.AccessDecisionVoter;

import org.alfresco.module.org_alfresco_module_dod5015.DispositionAction;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.impl.SimplePermissionReference;
import org.alfresco.repo.security.permissions.impl.acegi.ACLEntryVoterException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

public class RMEntryVoter implements AccessDecisionVoter, InitializingBean
{
    private static Log logger = LogFactory.getLog(RMEntryVoter.class);

    private static final String RM = "RM";

    private static final String RM_ALLOW = "RM_ALLOW";

    private static final String RM_DENY = "RM_DENY";

    private static final String RM_CAP = "RM_CAP";

    private static final String RM_ABSTAIN = "RM_ABSTAIN";

    private NamespacePrefixResolver nspr;

    private NodeService nodeService;

    private PermissionService permissionService;

    protected RMCaveatConfigImpl caveatConfigImpl;

    private DictionaryService dictionaryService;

    private RecordsManagementService recordsManagementService;

    private static HashMap<String, Policy> policies = new HashMap<String, Policy>();

    private static HashMap<QName, String> restrictedProperties = new HashMap<QName, String>();

    static
    {
        policies.put("Read", new ReadPolicy());
        policies.put("Create", new CreatePolicy());
        policies.put("Move", new MovePolicy());
        policies.put("Update", new UpdatePolicy());
        policies.put("Delete", new DeletePolicy());
        policies.put("UpdateProperties", new UpdatePropertiesPolicy());
        policies.put("Assoc", new AssocPolicy());
        policies.put("WriteContent", new WriteContentPolicy());
        policies.put("Capability", new CapabilityPolicy());

        // restrictedProperties.put(RecordsManagementModel.PROP_IS_CLOSED, value)

    }

    private static class Key
    {
        QName property;

        boolean requiresFiling;

        boolean rejectIfFrozen;

        boolean rejectIfDeclared;

        String byAction;
    }

    private static class RestrictedKey extends Key
    {
        Serializable value;
    }

    /**
     * Set the permission service
     * 
     * @param permissionService
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    /**
     * Set the node service
     * 
     * @param nodeService
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set the name space prefix resolver
     * 
     * @param nspr
     */
    public void setNamespacePrefixResolver(NamespacePrefixResolver nspr)
    {
        this.nspr = nspr;
    }

    public void setCaveatConfigImpl(RMCaveatConfigImpl caveatConfigImpl)
    {
        this.caveatConfigImpl = caveatConfigImpl;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public boolean supports(ConfigAttribute attribute)
    {
        if ((attribute.getAttribute() != null)
                && (attribute.getAttribute().equals(RM_ABSTAIN)
                        || attribute.getAttribute().equals(RM_ALLOW) || attribute.getAttribute().equals(RM_DENY) || attribute.getAttribute().startsWith(RM_CAP) || attribute
                        .getAttribute().startsWith(RM)))
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

    public int checkRead(NodeRef nodeRef)
    {
        if (nodeRef != null)
        {
            // now we know the node - we can abstain for certain types and aspects (eg, rm)
            return checkRead(this, nodeRef, false);
        }

        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config)
    {
        if (logger.isDebugEnabled())
        {
            MethodInvocation mi = (MethodInvocation) object;
            logger.debug("Method: " + mi.getMethod().toString());
        }
        if (AuthenticationUtil.isRunAsUserTheSystemUser())
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Access granted for the system user");
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

        for (ConfigAttributeDefintion cad : supportedDefinitions)
        {
            if (cad.typeString.equals(RM_ABSTAIN))
            {
                return AccessDecisionVoter.ACCESS_ABSTAIN;
            }
            else if (cad.typeString.equals(RM_DENY))
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else if (cad.typeString.equals(RM_ALLOW))
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }
            else if (((cad.parameters.get(0) != null) && (cad.parameters.get(0) >= invocation.getArguments().length))
                    || ((cad.parameters.get(1) != null) && (cad.parameters.get(1) >= invocation.getArguments().length)))
            {
                continue;
            }
            else if (cad.typeString.equals(RM_CAP))
            {
                if (checkCapability(invocation, params, cad) == AccessDecisionVoter.ACCESS_DENIED)
                {
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
            }
            else if (cad.typeString.equals(RM))
            {
                if (checkPolicy(invocation, params, cad) == AccessDecisionVoter.ACCESS_DENIED)
                {
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
            }
        }

        return AccessDecisionVoter.ACCESS_GRANTED;

    }

    private int checkCapability(MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
    {
        NodeRef testNodeRef = getTestNode(nodeService, invocation, params, cad.parameters.get(0), cad.parent);

        if (testNodeRef != null)
        {
            if (nodeService.hasAspect(testNodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
            {
                // now we know the node - we can abstain for certain types and aspects (eg, rm)

                if (logger.isDebugEnabled())
                {
                    logger.debug("\t\tNode ref is not null");
                }
                if (permissionService.hasPermission(testNodeRef, cad.required.toString()) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }
            else
            {
                return AccessDecisionVoter.ACCESS_ABSTAIN;
            }
        }

        return AccessDecisionVoter.ACCESS_ABSTAIN;

    }

    private static QName getType(NodeService nodeService, MethodInvocation invocation, Class[] params, int position, boolean parent)
    {
        if (QName.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                QName qname = (QName) invocation.getArguments()[position];
                return qname;
            }
        }
        else if (NodeRef.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                NodeRef nodeRef = (NodeRef) invocation.getArguments()[position];
                return nodeService.getType(nodeRef);
            }
        }

        throw new ACLEntryVoterException("Unknown type");
    }

    private static NodeRef getTestNode(NodeService nodeService, MethodInvocation invocation, Class[] params, int position, boolean parent)
    {
        NodeRef testNodeRef = null;
        if (StoreRef.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("\tPermission test against the store - using permissions on the root node");
                }
                StoreRef storeRef = (StoreRef) invocation.getArguments()[position];
                if (nodeService.exists(storeRef))
                {
                    testNodeRef = nodeService.getRootNode(storeRef);
                }
            }
        }
        else if (NodeRef.class.isAssignableFrom(params[position]))
        {
            testNodeRef = (NodeRef) invocation.getArguments()[position];
            if (parent)
            {
                testNodeRef = nodeService.getPrimaryParent(testNodeRef).getParentRef();
                if (logger.isDebugEnabled())
                {
                    if (nodeService.exists(testNodeRef))
                    {
                        logger.debug("\tPermission test for parent on node " + nodeService.getPath(testNodeRef));
                    }
                    else
                    {
                        logger.debug("\tPermission test for parent on non-existing node " + testNodeRef);
                    }
                    logger.debug("\tPermission test for parent on node " + nodeService.getPath(testNodeRef));
                }
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    if (nodeService.exists(testNodeRef))
                    {
                        logger.debug("\tPermission test on node " + nodeService.getPath(testNodeRef));
                    }
                    else
                    {
                        logger.debug("\tPermission test on non-existing node " + testNodeRef);
                    }
                }
            }
        }
        else if (ChildAssociationRef.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                if (parent)
                {
                    testNodeRef = ((ChildAssociationRef) invocation.getArguments()[position]).getParentRef();
                }
                else
                {
                    testNodeRef = ((ChildAssociationRef) invocation.getArguments()[position]).getChildRef();
                }
                if (logger.isDebugEnabled())
                {
                    if (nodeService.exists(testNodeRef))
                    {
                        logger.debug("\tPermission test on node " + nodeService.getPath(testNodeRef));
                    }
                    else
                    {
                        logger.debug("\tPermission test on non-existing node " + testNodeRef);
                    }
                }
            }
        }
        else if (AssociationRef.class.isAssignableFrom(params[position]))
        {
            if (invocation.getArguments()[position] != null)
            {
                if (parent)
                {
                    testNodeRef = ((AssociationRef) invocation.getArguments()[position]).getSourceRef();
                }
                else
                {
                    testNodeRef = ((AssociationRef) invocation.getArguments()[position]).getTargetRef();
                }
                if (logger.isDebugEnabled())
                {
                    if (nodeService.exists(testNodeRef))
                    {
                        logger.debug("\tPermission test on node " + nodeService.getPath(testNodeRef));
                    }
                    else
                    {
                        logger.debug("\tPermission test on non-existing node " + testNodeRef);
                    }
                }
            }
        }
        return testNodeRef;
    }

    private int checkPolicy(MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
    {
        Policy policy = policies.get(cad.policyName);
        if (policy == null)
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }
        else
        {
            return policy.evaluate(this, invocation, params, cad);
        }
    }

    public void afterPropertiesSet() throws Exception
    {
        // TODO Auto-generated method stub

    }

    private List<ConfigAttributeDefintion> extractSupportedDefinitions(ConfigAttributeDefinition config)
    {
        List<ConfigAttributeDefintion> definitions = new ArrayList<ConfigAttributeDefintion>(2);
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

        String policyName;

        SimplePermissionReference required;

        HashMap<Integer, Integer> parameters = new HashMap<Integer, Integer>(2, 1.0f);

        boolean parent = false;

        ConfigAttributeDefintion(ConfigAttribute attr)
        {
            StringTokenizer st = new StringTokenizer(attr.getAttribute(), ".", false);
            if (st.countTokens() < 1)
            {
                throw new ACLEntryVoterException("There must be at least one token in a config attribute");
            }
            typeString = st.nextToken();

            if (!(typeString.equals(RM) || typeString.equals(RM_ALLOW) || typeString.equals(RM_CAP) || typeString.equals(RM_DENY)))
            {
                throw new ACLEntryVoterException("Invalid type: must be ACL_NODE, ACL_PARENT or ACL_ALLOW");
            }

            if (typeString.equals(RM))
            {
                policyName = st.nextToken();
                int position = 0;
                while (st.hasMoreElements())
                {
                    String numberString = st.nextToken();
                    Integer value = Integer.parseInt(numberString);
                    parameters.put(position, value);
                    position++;
                }
            }
            else if (typeString.equals(RM_CAP))
            {
                String numberString = st.nextToken();
                String qNameString = st.nextToken();
                String permissionString = st.nextToken();

                Integer value = Integer.parseInt(numberString);
                parameters.put(0, value);

                QName qName = QName.createQName(qNameString, nspr);

                required = SimplePermissionReference.getPermissionReference(qName, permissionString);

                if (st.hasMoreElements())
                {
                    parent = true;
                }
            }
        }
    }

    private static int checkRead(RMEntryVoter voter, NodeRef nodeRef, boolean allowDMRead)
    {
        if (voter.nodeService.hasAspect(nodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("\t\tNode ref is not null");
            }
            if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.VIEW_RECORDS) == AccessStatus.DENIED)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("\t\tPermission is denied");
                    Thread.dumpStack();
                }
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                if (voter.caveatConfigImpl.hasAccess(nodeRef))
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
            }
        }
        else
        {
            if (allowDMRead)
            {
                // Check DM read for copy etc
                // DM does not grant - it can only deny
                if (voter.permissionService.hasPermission(nodeRef, PermissionService.READ) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }
            else
            {
                return AccessDecisionVoter.ACCESS_ABSTAIN;
            }
        }
    }

    private static int checkFiling(RMEntryVoter voter, NodeRef nodeRef)
    {
        if (voter.nodeService.hasAspect(nodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("\t\tNode ref is not null");
            }

            if (isRecord(voter, nodeRef))
            {
                //TOTO: Multifiling
                ChildAssociationRef car = voter.nodeService.getPrimaryParent(nodeRef);
                if (car != null)
                {
                    if (voter.permissionService.hasPermission(voter.nodeService.getPrimaryParent(nodeRef).getParentRef(), RMPermissionModel.DECLARE_RECORDS) == AccessStatus.DENIED)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("\t\tPermission is denied");
                            Thread.dumpStack();
                        }

                        return AccessDecisionVoter.ACCESS_DENIED;
                    }
                    else
                    {

                        return AccessDecisionVoter.ACCESS_GRANTED;
                    }
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_ABSTAIN;  
                }
            }
            if (isRecordFolder(voter, voter.nodeService.getType(nodeRef)))
            {

                if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.DECLARE_RECORDS) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                     
                        Thread.dumpStack();
                    }

                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {

                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }
            // else other file plan component
            else
            {
                if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }
        }

        return AccessDecisionVoter.ACCESS_ABSTAIN;

    }

    private static int checkCreate(RMEntryVoter voter, NodeRef nodeRef, QName type)
    {
        if (voter.nodeService.hasAspect(nodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("\t\tNode ref is not null");
            }

            // we can read
            // we file into arg 0

            // Filing Record
            if (isRecordFolder(voter, voter.nodeService.getType(nodeRef)))
            {

                if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.DECLARE_RECORDS) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }

                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {

                    if (isClosed(voter, nodeRef))
                    {
                        if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS) == AccessStatus.DENIED)
                        {
                            return AccessDecisionVoter.ACCESS_DENIED;
                        }
                    }
                    if (isCutoff(voter, nodeRef))
                    {
                        if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS) == AccessStatus.DENIED)
                        {
                            return AccessDecisionVoter.ACCESS_DENIED;
                        }
                    }
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }
            // Create Record Folder
            else if (isRecordFolder(voter, type))
            {
                if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }
            // else other file plan component
            else
            {
                if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }
        }

        return AccessDecisionVoter.ACCESS_ABSTAIN;

    }

    private static int checkDelete(RMEntryVoter voter, NodeRef nodeRef)
    {
        if (voter.nodeService.hasAspect(nodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("\t\tNode ref is not null");
            }

            if (isRecord(voter, nodeRef))
            {

                // We can delete anything

                if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.DELETE_RECORDS) == AccessStatus.ALLOWED)
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }

                DispositionSchedule dispositionSchedule = voter.recordsManagementService.getDispositionSchedule(nodeRef);
                for (DispositionActionDefinition dispositionActionDefinition : dispositionSchedule.getDispositionActionDefinitions())
                {
                    if (dispositionActionDefinition.getName().equals("destroy"))
                    {
                        if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.DESTROY_RECORDS) == AccessStatus.ALLOWED)
                        {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                }

                // The record is all set up for destruction
                DispositionAction nextDispositionAction = voter.recordsManagementService.getNextDispositionAction(nodeRef);
                if (nextDispositionAction != null)
                {
                    if (nextDispositionAction.getDispositionActionDefinition().getName().equals("destroy"))
                    {
                        if (voter.recordsManagementService.isNextDispositionActionEligible(nodeRef))
                        {
                            if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION) == AccessStatus.ALLOWED)
                            {
                                return AccessDecisionVoter.ACCESS_GRANTED;
                            }
                        }
                    }
                }

                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                if (voter.permissionService.hasPermission(nodeRef, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA) == AccessStatus.DENIED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("\t\tPermission is denied");
                        Thread.dumpStack();
                    }
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }

        }

        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    private static boolean isRecord(RMEntryVoter voter, NodeRef nodeRef)
    {
        return voter.nodeService.hasAspect(nodeRef, RecordsManagementModel.ASPECT_RECORD);
    }

    private static boolean isRecordFolder(RMEntryVoter voter, QName type)
    {
        return voter.dictionaryService.isSubClass(type, RecordsManagementModel.TYPE_RECORD_FOLDER);
    }

    private static boolean isCutoff(RMEntryVoter voter, NodeRef nodeRef)
    {
        return voter.nodeService.hasAspect(nodeRef, RecordsManagementModel.ASPECT_CUT_OFF);
    }

    private static boolean isClosed(RMEntryVoter voter, NodeRef nodeRef)
    {
        Serializable serializableValue = voter.nodeService.getProperty(nodeRef, RecordsManagementModel.PROP_IS_CLOSED);
        if (serializableValue == null)
        {
            return false;
        }
        Boolean isClosed = DefaultTypeConverter.INSTANCE.convert(Boolean.class, serializableValue);
        return isClosed;
    }

    private static boolean isRm(RMEntryVoter voter, NodeRef nodeRef)
    {
        return voter.nodeService.hasAspect(nodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT);
    }

    interface Policy
    {
        int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad);
    }

    private static class ReadPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef testNodeRef = getTestNode(voter.nodeService, invocation, params, cad.parameters.get(0), cad.parent);

            if (testNodeRef != null)
            {
                // now we know the node - we can abstain for certain types and aspects (eg, rm)
                return checkRead(voter, testNodeRef, false);
            }

            return AccessDecisionVoter.ACCESS_ABSTAIN;

        }

    }

    private static class CreatePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {

            NodeRef destination = getTestNode(voter.nodeService, invocation, params, cad.parameters.get(0), cad.parent);
            QName type = getType(voter.nodeService, invocation, params, cad.parameters.get(1), cad.parent);
            // linkee is not null for creating secondary child assocs
            NodeRef linkee = getTestNode(voter.nodeService, invocation, params, cad.parameters.get(1), cad.parent);
            if (linkee != null)
            {
                if (checkRead(voter, linkee, false) != AccessDecisionVoter.ACCESS_GRANTED)
                {
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
            }
            return checkCreate(voter, destination, type);

        }

    }

    private static class MovePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            int first = AccessDecisionVoter.ACCESS_ABSTAIN;
            int second = AccessDecisionVoter.ACCESS_ABSTAIN;

            NodeRef movee = null;
            if (cad.parameters.get(0) > -1)
            {
                movee = getTestNode(voter.nodeService, invocation, params, cad.parameters.get(0), cad.parent);
            }

            if (movee != null)
            {
                // now we know the node - we can abstain for certain types and aspects (eg, rm)
                first = checkRead(voter, movee, true);
                // TODO: CHECK DELETE

                NodeRef destination = null;
                if (cad.parameters.get(1) > -1)
                {
                    destination = getTestNode(voter.nodeService, invocation, params, cad.parameters.get(1), cad.parent);
                }

                if (destination != null)
                {
                    QName type = voter.nodeService.getType(movee);
                    // now we know the node - we can abstain for certain types and aspects (eg, rm)
                    second = checkCreate(voter, destination, type);
                }
            }
            if ((first == AccessDecisionVoter.ACCESS_DENIED) || (second == AccessDecisionVoter.ACCESS_DENIED))
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }

            if ((first == AccessDecisionVoter.ACCESS_GRANTED) && (second == AccessDecisionVoter.ACCESS_GRANTED))
            {
                return AccessDecisionVoter.ACCESS_GRANTED;
            }

            return AccessDecisionVoter.ACCESS_ABSTAIN;

        }
    }

    private static class UpdatePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            Policy policy = policies.get("Read");
            if (policy == null)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return policy.evaluate(voter, invocation, params, cad);
            }
        }

    }

    private static class DeletePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef deletee = null;
            if (cad.parameters.get(0) > -1)
            {
                deletee = getTestNode(voter.nodeService, invocation, params, cad.parameters.get(0), cad.parent);
            }
            if (deletee != null)
            {

                if (checkRead(voter, deletee, false) != AccessDecisionVoter.ACCESS_GRANTED)
                {
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return checkDelete(voter, deletee);
                }

            }
            else
            {
                return AccessDecisionVoter.ACCESS_ABSTAIN;
            }
        }

    }

    private static class UpdatePropertiesPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            Policy policy = policies.get("Read");
            if (policy == null)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return policy.evaluate(voter, invocation, params, cad);
            }
        }

    }

    private static class AssocPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            Policy policy = policies.get("Read");
            if (policy == null)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return policy.evaluate(voter, invocation, params, cad);
            }
        }

    }

    private static class WriteContentPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            Policy policy = policies.get("Read");
            if (policy == null)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return policy.evaluate(voter, invocation, params, cad);
            }
        }

    }

    private static class CapabilityPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            Policy policy = policies.get("Read");
            if (policy == null)
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }
            else
            {
                return policy.evaluate(voter, invocation, params, cad);
            }
        }

    }
}
