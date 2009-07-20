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

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.capability.group.CreateCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AccessAuditCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AddModifyEventDatesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ApproveRecordsScheduledForCutoffCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AttachRulesToMetadataPropertiesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AuthorizeAllTransfersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.AuthorizeNominatedTransfersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateAndAssociateSelectionListsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyClassificationGuidesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyFileplanMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyFileplanTypesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyRecordTypesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyReferenceTypesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyRolesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyTimeframesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyUsersAndGroupsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyRecordsInCuttoffFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeclareAuditAsRecordCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeleteAuditCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeleteLinksCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ChangeOrDeleteReferencesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CloseFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyEventsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CreateModifyDestroyFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.CycleVitalRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeclareRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeclareRecordsInClosedFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DeleteRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DestroyRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DestroyRecordsScheduledForDestructionCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.DisplayRightsReportCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EditDeclaredRecordMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EditNonRecordMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EditRecordMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EditSelectionListsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.EnableDisableAuditByTypesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ExportAuditCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ExtendRetentionPeriodOrFreezeCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.MakeOptionalPropertiesMandatoryCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ManageAccessControlsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ManageAccessRightsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ManuallyChangeDispositionDatesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.MapClassificationGuideMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.MapEmailMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.MoveRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.PasswordControlCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.PlanningReviewCyclesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ReOpenFoldersCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.SelectAuditMetadataCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UndeclareRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UnfreezeCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpdateClassificationDatesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpdateExemptionCategoriesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpdateTriggerDatesCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpdateVitalRecordCycleInformationCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.UpgradeDowngradeAndDeclassifyRecordsCapability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ViewRecordsCapabilty;
import org.alfresco.module.org_alfresco_module_dod5015.capability.impl.ViewUpdateReasonsForFreezeCapability;
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

    private static final String RM_QUERY = "RM_QUERY";

    NamespacePrefixResolver nspr;

    private NodeService nodeService;

    private PermissionService permissionService;

    private RMCaveatConfigImpl caveatConfigImpl;

    private DictionaryService dictionaryService;

    private RecordsManagementService recordsManagementService;

    private static HashMap<String, Policy> policies = new HashMap<String, Policy>();

    private static HashMap<QName, String> restrictedProperties = new HashMap<QName, String>();

    //
    
    public ViewRecordsCapabilty viewRecordsCapability = new ViewRecordsCapabilty(this);

    public DeclareRecordsCapability declareRecordsCapability = new DeclareRecordsCapability(this);

    public CreateModifyDestroyFoldersCapability createModifyDestroyFoldersCapability = new CreateModifyDestroyFoldersCapability(this);

    public EditRecordMetadataCapability editRecordMetadataCapability = new EditRecordMetadataCapability(this);

    public EditNonRecordMetadataCapability editNonRecordMetadataCapability = new EditNonRecordMetadataCapability(this);

    public AddModifyEventDatesCapability addModifyEventDatesCapability = new AddModifyEventDatesCapability(this);

    public CloseFoldersCapability closeFoldersCapability = new CloseFoldersCapability(this);

    public DeclareRecordsInClosedFoldersCapability declareRecordsInClosedFoldersCapability = new DeclareRecordsInClosedFoldersCapability(this);

    public ReOpenFoldersCapability reOpenFoldersCapability = new ReOpenFoldersCapability(this);

    public CycleVitalRecordsCapability cycleVitalRecordsCapability = new CycleVitalRecordsCapability(this);

    public PlanningReviewCyclesCapability planningReviewCyclesCapability = new PlanningReviewCyclesCapability(this);

    public UpdateTriggerDatesCapability updateTriggerDatesCapability = new UpdateTriggerDatesCapability(this);

    public CreateModifyDestroyEventsCapability createModifyDestroyEventsCapability = new CreateModifyDestroyEventsCapability(this);

    public ManageAccessRightsCapability manageAccessRightsCapability = new ManageAccessRightsCapability(this);

    public MoveRecordsCapability moveRecordsCapability = new MoveRecordsCapability(this);

    public ChangeOrDeleteReferencesCapability changeOrDeleteReferencesCapability = new ChangeOrDeleteReferencesCapability(this);

    public DeleteLinksCapability deleteLinksCapability = new DeleteLinksCapability(this);

    public EditDeclaredRecordMetadataCapability editDeclaredRecordMetadataCapability = new EditDeclaredRecordMetadataCapability(this);
    
    public ManuallyChangeDispositionDatesCapability manuallyChangeDispositionDatesCapability = new ManuallyChangeDispositionDatesCapability(this);

    public ApproveRecordsScheduledForCutoffCapability approveRecordsScheduledForCutoffCapability = new ApproveRecordsScheduledForCutoffCapability(this);
    
    public CreateModifyRecordsInCuttoffFoldersCapability createModifyRecordsInCuttoffFoldersCapability = new CreateModifyRecordsInCuttoffFoldersCapability(this);
    
    public  ExtendRetentionPeriodOrFreezeCapability extendRetentionPeriodOrFreezeCapability = new ExtendRetentionPeriodOrFreezeCapability(this);
    
    public UnfreezeCapability unfreezeCapability = new UnfreezeCapability(this);
    
    public ViewUpdateReasonsForFreezeCapability viewUpdateReasonsForFreezeCapability = new ViewUpdateReasonsForFreezeCapability(this);
    
    public DestroyRecordsScheduledForDestructionCapability destroyRecordsScheduledForDestructionCapability = new DestroyRecordsScheduledForDestructionCapability(this);
    
    public DestroyRecordsCapability destroyRecordsCapability = new DestroyRecordsCapability(this);
    
    public UpdateVitalRecordCycleInformationCapability updateVitalRecordCycleInformationCapability = new UpdateVitalRecordCycleInformationCapability(this);
    
    public UndeclareRecordsCapability undeclareRecordsCapability = new UndeclareRecordsCapability(this);
    
    public DeclareAuditAsRecordCapability declareAuditAsRecordCapability = new DeclareAuditAsRecordCapability(this);
    
    public DeleteAuditCapability deleteAuditCapability = new DeleteAuditCapability(this);
    
    public CreateModifyDestroyTimeframesCapability createModifyDestroyTimeframesCapability = new CreateModifyDestroyTimeframesCapability(this);
    
    public AuthorizeNominatedTransfersCapability authorizeNominatedTransfersCapability = new AuthorizeNominatedTransfersCapability(this);
    
    public EditSelectionListsCapability editSelectionListsCapability = new EditSelectionListsCapability(this);
    
    public AuthorizeAllTransfersCapability authorizeAllTransfersCapability = new AuthorizeAllTransfersCapability(this);
    
    public CreateModifyDestroyFileplanMetadataCapability createModifyDestroyFileplanMetadataCapability = new CreateModifyDestroyFileplanMetadataCapability(this);
    
    public CreateAndAssociateSelectionListsCapability createAndAssociateSelectionListsCapability = new CreateAndAssociateSelectionListsCapability(this);
    
    public AttachRulesToMetadataPropertiesCapability attachRulesToMetadataPropertiesCapability = new AttachRulesToMetadataPropertiesCapability(this);
    
    public CreateModifyDestroyFileplanTypesCapability createModifyDestroyFileplanTypesCapability = new CreateModifyDestroyFileplanTypesCapability(this);
    
    public CreateModifyDestroyRecordTypesCapability createModifyDestroyRecordTypesCapability = new CreateModifyDestroyRecordTypesCapability(this);
    
    public MakeOptionalPropertiesMandatoryCapability makeOptionalPropertiesMandatoryCapability = new MakeOptionalPropertiesMandatoryCapability(this);
    
    public MapEmailMetadataCapability mapEmailMetadataCapability = new MapEmailMetadataCapability(this);
    
    public DeleteRecordsCapability deleteRecordsCapability = new DeleteRecordsCapability(this);
    
    public CreateModifyDestroyRolesCapability createModifyDestroyRolesCapability = new CreateModifyDestroyRolesCapability(this);
    
    public CreateModifyDestroyUsersAndGroupsCapability createModifyDestroyUsersAndGroupsCapability = new CreateModifyDestroyUsersAndGroupsCapability(this);
    
    public PasswordControlCapability passwordControlCapability = new PasswordControlCapability(this);
    
    public EnableDisableAuditByTypesCapability enableDisableAuditByTypesCapability = new EnableDisableAuditByTypesCapability(this);
    
    public SelectAuditMetadataCapability selectAuditMetadataCapability = new SelectAuditMetadataCapability(this);
    
    public DisplayRightsReportCapability displayRightsReportCapability = new DisplayRightsReportCapability(this);
    
    public AccessAuditCapability accessAuditCapability = new AccessAuditCapability(this);
    
    public ExportAuditCapability exportAuditCapability = new ExportAuditCapability(this);
    
    public CreateModifyDestroyReferenceTypesCapability createModifyDestroyReferenceTypesCapability = new CreateModifyDestroyReferenceTypesCapability(this);
    
    public UpdateClassificationDatesCapability updateClassificationDatesCapability = new UpdateClassificationDatesCapability(this);
    
    public CreateModifyDestroyClassificationGuidesCapability createModifyDestroyClassificationGuidesCapability = new CreateModifyDestroyClassificationGuidesCapability(this);
    
    public UpgradeDowngradeAndDeclassifyRecordsCapability upgradeDowngradeAndDeclassifyRecordsCapability = new UpgradeDowngradeAndDeclassifyRecordsCapability(this);
    
    public UpdateExemptionCategoriesCapability updateExemptionCategoriesCapability = new UpdateExemptionCategoriesCapability(this);
    
    public MapClassificationGuideMetadataCapability mapClassificationGuideMetadataCapability = new MapClassificationGuideMetadataCapability(this);
    
    public ManageAccessControlsCapability manageAccessControlsCapability = new ManageAccessControlsCapability(this);
    
    //

    public CreateCapability createCapability = new CreateCapability(this);

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
                        || attribute.getAttribute().equals(RM_QUERY) || attribute.getAttribute().equals(RM_ALLOW) || attribute.getAttribute().equals(RM_DENY)
                        || attribute.getAttribute().startsWith(RM_CAP) || attribute.getAttribute().startsWith(RM)))
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
            else if (cad.typeString.equals(RM_QUERY))
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
        NodeRef testNodeRef = getTestNode(getNodeService(), invocation, params, cad.parameters.get(0), cad.parent);

        if (testNodeRef != null)
        {
            if (getNodeService().hasAspect(testNodeRef, RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
            {
                // now we know the node - we can abstain for certain types and aspects (eg, rm)

                if (logger.isDebugEnabled())
                {
                    logger.debug("\t\tNode ref is not null");
                }
                if (getPermissionService().hasPermission(testNodeRef, cad.required.toString()) == AccessStatus.DENIED)
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

    /**
     * @return the nodeService
     */
    public NodeService getNodeService()
    {
        return nodeService;
    }

    /**
     * @return the permissionService
     */
    public PermissionService getPermissionService()
    {
        return permissionService;
    }

    /**
     * @return the caveatConfigImpl
     */
    public RMCaveatConfigImpl getCaveatConfigImpl()
    {
        return caveatConfigImpl;
    }

    /**
     * @param recordsManagementService
     *            the recordsManagementService to set
     */
    public void setRecordsManagementService(RecordsManagementService recordsManagementService)
    {
        this.recordsManagementService = recordsManagementService;
    }

    /**
     * @return the recordsManagementService
     */
    public RecordsManagementService getRecordsManagementService()
    {
        return recordsManagementService;
    }

    /**
     * @return the dictionaryService
     */
    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
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

            if (!(typeString.equals(RM) || typeString.equals(RM_ALLOW) || typeString.equals(RM_CAP) || typeString.equals(RM_DENY) || typeString.equals(RM_QUERY)))
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

    interface Policy
    {
        int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad);
    }

    private static class ReadPolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {
            NodeRef testNodeRef = getTestNode(voter.getNodeService(), invocation, params, cad.parameters.get(0), cad.parent);
            return voter.viewRecordsCapability.evaluate(testNodeRef);
        }

    }

    private static class CreatePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {

            NodeRef destination = getTestNode(voter.getNodeService(), invocation, params, cad.parameters.get(0), cad.parent);
            QName type = getType(voter.getNodeService(), invocation, params, cad.parameters.get(1), cad.parent);
            // linkee is not null for creating secondary child assocs
            NodeRef linkee = getTestNode(voter.getNodeService(), invocation, params, cad.parameters.get(1), cad.parent);

            return voter.createCapability.evaluate(destination, linkee, type);
        }

    }

    private static class MovePolicy implements Policy
    {

        public int evaluate(RMEntryVoter voter, MethodInvocation invocation, Class[] params, ConfigAttributeDefintion cad)
        {

            NodeRef movee = null;
            if (cad.parameters.get(0) > -1)
            {
                movee = getTestNode(voter.getNodeService(), invocation, params, cad.parameters.get(0), cad.parent);
            }

            NodeRef destination = null;
            if (cad.parameters.get(1) > -1)
            {
                destination = getTestNode(voter.getNodeService(), invocation, params, cad.parameters.get(1), cad.parent);
            }

            if ((movee != null) && (destination != null))
            {
                return voter.moveRecordsCapability.evaluate(movee, destination);
            }
            else
            {
                return AccessDecisionVoter.ACCESS_DENIED;
            }

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
                deletee = getTestNode(voter.getNodeService(), invocation, params, cad.parameters.get(0), cad.parent);
            }
            if (deletee != null)
            {

                if (voter.viewRecordsCapability.evaluate(deletee) != AccessDecisionVoter.ACCESS_GRANTED)
                {
                    return AccessDecisionVoter.ACCESS_DENIED;
                }
                else
                {
                    return voter.viewRecordsCapability.checkDelete(deletee);
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
