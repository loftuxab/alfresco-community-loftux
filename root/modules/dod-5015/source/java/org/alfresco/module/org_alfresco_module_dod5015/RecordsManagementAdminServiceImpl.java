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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.BeforeCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.CustomReferenceId;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.DefineCustomAssociationAction;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMListOfValuesConstraint;
import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Association;
import org.alfresco.repo.dictionary.M2ChildAssociation;
import org.alfresco.repo.dictionary.M2Constraint;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Records Management AdminService Implementation.
 * 
 * @author Neil McErlean, janv
 */
public class RecordsManagementAdminServiceImpl implements RecordsManagementAdminService
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementAdminServiceImpl.class);
    
    public static final String RMC_CUSTOM_ASSOCS = CustomModelUtil.RMC_CUSTOM_ASSOCS;
    
    private static final String CUSTOM_CONSTRAINT_TYPE = org.alfresco.module.org_alfresco_module_dod5015.caveat.RMListOfValuesConstraint.class.getName();
    
    private static final String PARAM_ALLOWED_VALUES = "allowedValues";
    private static final String PARAM_CASE_SENSITIVE = "caseSensitive";
    
    /** Services */
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private ContentService contentService;

    private PolicyComponent policyComponent;
    
    /** Policy delegates */
    private ClassPolicyDelegate<BeforeCreateReference> beforeCreateReferenceDelegate;
    private ClassPolicyDelegate<OnCreateReference> onCreateReferenceDelegate;

    /**
     * @param dictionaryService     the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
		this.dictionaryService = dictionaryService;
	}

    /**
     * @param namespaceService      the namespace service
     */
	public void setNamespaceService(NamespaceService namespaceService)
	{
		this.namespaceService = namespaceService;
	}

	/**
	 * @param nodeService      the node service
	 */
	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}
	
	public void setContentService(ContentService contentService)
	{
	    this.contentService = contentService;
	}
	
	/**
	 * @param policyComponent  the policy component
	 */
	public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
	
	/**
	 * Initialisation method
	 */
	public void init()
    {
        // Register the various policies
        beforeCreateReferenceDelegate = policyComponent.registerClassPolicy(BeforeCreateReference.class);
        onCreateReferenceDelegate = policyComponent.registerClassPolicy(OnCreateReference.class);
    }
	
    protected void invokeBeforeCreateReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // get qnames to invoke against
        Set<QName> qnames = RecordsManagementPoliciesUtil.getTypeAndAspectQNames(nodeService, fromNodeRef);
        // execute policy for node type and aspects
        BeforeCreateReference policy = beforeCreateReferenceDelegate.get(qnames);
        policy.beforeCreateReference(fromNodeRef, toNodeRef, reference);
    }
    
    protected void invokeOnCreateReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // get qnames to invoke against
        Set<QName> qnames = RecordsManagementPoliciesUtil.getTypeAndAspectQNames(nodeService, fromNodeRef);
        // execute policy for node type and aspects
        OnCreateReference policy = onCreateReferenceDelegate.get(qnames);
        policy.onCreateReference(fromNodeRef, toNodeRef, reference);
    }

	/**
	 * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getAvailableCustomReferences()
	 */
    public Map<QName, AssociationDefinition> getAvailableCustomReferences()
    {
		QName relevantAspectQName = QName.createQName(RMC_CUSTOM_ASSOCS, namespaceService);
        AspectDefinition aspectDefn = dictionaryService.getAspect(relevantAspectQName);
        Map<QName, AssociationDefinition> assocDefns = aspectDefn.getAssociations();
        
        return assocDefns;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getAvailableCustomProperties()
     */
    public Map<QName, PropertyDefinition> getAvailableCustomProperties()
    {
    	Map<QName, PropertyDefinition> result = new HashMap<QName, PropertyDefinition>();
    	for (CustomisableRmElement elem : CustomisableRmElement.values())
    	{
    		result.putAll(getAvailableCustomProperties(elem));
    	}
        return result;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getAvailableCustomProperties(org.alfresco.module.org_alfresco_module_dod5015.CustomisableRmElement)
     */
    public Map<QName, PropertyDefinition> getAvailableCustomProperties(CustomisableRmElement rmElement)
    {
		QName relevantAspectQName = QName.createQName(rmElement.getCorrespondingAspect(), namespaceService);
        AspectDefinition aspectDefn = dictionaryService.getAspect(relevantAspectQName);
        Map<QName, PropertyDefinition> propDefns = aspectDefn.getProperties();

        return propDefns;
    }
    
    public void addCustomPropertyDefinition(String aspectName, QName propQName, QName dataType, String title, String description, String defaultValue, boolean multiValued, boolean mandatory, boolean isProtected)
    {
        ParameterCheck.mandatoryString("aspectName", aspectName);
        ParameterCheck.mandatory("propQName", propQName);
        ParameterCheck.mandatory("dataType", dataType);
        
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);
        
        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        M2Aspect customPropsAspect = deserializedModel.getAspect(aspectName);
        
        String propQNameAsString = propQName.toPrefixString(namespaceService);
        
        M2Property newProp = customPropsAspect.createProperty(propQNameAsString);
        newProp.setName(propQNameAsString);
        newProp.setType(dataType.toPrefixString(namespaceService));
        
        newProp.setTitle(title);
        newProp.setDescription(description);
        newProp.setDefaultValue(defaultValue);
        
        newProp.setMandatory(mandatory);
        newProp.setProtected(isProtected);
        newProp.setMultiValued(multiValued);
        
        customModelUtil.writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomPropertyDefinition: "+propQName+" to aspect: "+aspectName);
        }
    }
    
    public void removeCustomPropertyDefinition(QName propQName)
    {
        // data dictionary does not currently support incremental deletes
        throw new UnsupportedOperationException("removeCustomConstraintDefinition: "+propQName);
        
        /*
        ParameterCheck.mandatory("propQName", propQName);
        
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);
        
        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        
        String propQNameAsString = propQName.toPrefixString(namespaceService);
        
        String aspectName = null;
        
        boolean found = false;
        
        // Need to select the correct aspect in the customModel from which we'll
        // attempt to delete the property definition.
        for (CustomisableRmElement elem : CustomisableRmElement.values())
        {
            aspectName = elem.getCorrespondingAspect();
            M2Aspect customPropsAspect = deserializedModel.getAspect(aspectName);
            
            M2Property prop = customPropsAspect.getProperty(propQNameAsString);
            if (prop != null)
            {
                if (logger.isDebugEnabled())
                {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Attempting to delete custom property: ");
                    msg.append(propQNameAsString);
                    logger.debug(msg.toString());
                }
                
                found = true;
                customPropsAspect.removeProperty(propQNameAsString);
                break;
            }
        }
        
        if (! found)
        {
            throw new AlfrescoRuntimeException("Could not find property to delete: "+propQName);
        }
        
        customModelUtil.writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("deleteCustomPropertyDefinition: "+propQName+" from aspect: "+aspectName);
        }
        */
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#addCustomReference(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
	public void addCustomReference(NodeRef fromNode, NodeRef toNode, QName refId)
	{
		Map<QName, AssociationDefinition> availableAssocs = this.getAvailableCustomReferences();

		AssociationDefinition assocDef = availableAssocs.get(refId);
		if (assocDef == null)
		{
			throw new IllegalArgumentException("No such custom reference: " + refId);
		}

		// Invoke before create reference policy
		invokeBeforeCreateReference(fromNode, toNode, refId);
		
		if (assocDef.isChild())
		{
			this.nodeService.addChild(fromNode, toNode, refId, refId);
		}
		else
		{
			this.nodeService.createAssociation(fromNode, toNode, refId);
		}
		
		// Invoke on create reference policy
        invokeOnCreateReference(fromNode, toNode, refId);
	}

	public void removeCustomReference(NodeRef fromNode, NodeRef toNode, QName assocId) 
	{
		Map<QName, AssociationDefinition> availableAssocs = this.getAvailableCustomReferences();

		AssociationDefinition assocDef = availableAssocs.get(assocId);
		if (assocDef == null)
		{
			throw new IllegalArgumentException("No such custom reference: " + assocId);
		}

		if (assocDef.isChild())
		{
			List<ChildAssociationRef> children = nodeService.getChildAssocs(fromNode);
			for (ChildAssociationRef chRef : children)
			{
				if (assocId.equals(chRef.getTypeQName()) && chRef.getChildRef().equals(toNode))
				{
					nodeService.removeChildAssociation(chRef);
				}
			}
		}
		else
		{
			nodeService.removeAssociation(fromNode, toNode, assocId);
		}
	}

	public List<AssociationRef> getCustomReferencesFor(NodeRef node)
	{
    	List<AssociationRef> retrievedAssocs = nodeService.getTargetAssocs(node, RegexQNamePattern.MATCH_ALL);
    	return retrievedAssocs;
	}

	public List<ChildAssociationRef> getCustomChildReferencesFor(NodeRef node)
	{
    	List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(node);
    	return childAssocs;
	}
	
    public void addCustomAssocDefinition(String label)
    {
        ParameterCheck.mandatoryString("label", label);
        
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);
        
        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        M2Aspect customAssocsAspect = deserializedModel.getAspect(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS);
        
        CustomReferenceId crId = new CustomReferenceId(label, null, null);
        
        M2Association newAssoc = customAssocsAspect.createAssociation(crId.getReferenceId());
        newAssoc.setSourceMandatory(false);
        newAssoc.setTargetMandatory(false);
        
        // TODO Could be the customAssocs aspect
        newAssoc.setTargetClassName(DefineCustomAssociationAction.RMA_RECORD);
        
        customModelUtil.writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomAssocDefinition: ("+label+")");
        }
    }
    
    public void addCustomChildAssocDefinition(String source, String target)
    {
        ParameterCheck.mandatoryString("source", source);
        ParameterCheck.mandatoryString("target", target);
        
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);
        
        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        M2Aspect customAssocsAspect = deserializedModel.getAspect(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS);
        
        CustomReferenceId crId = new CustomReferenceId(null, source, target);
        
        M2ChildAssociation newAssoc = customAssocsAspect.createChildAssociation(crId.getReferenceId());
        newAssoc.setSourceMandatory(false);
        newAssoc.setTargetMandatory(false);
        
        // TODO Could be the custom assocs aspect
        newAssoc.setTargetClassName(DefineCustomAssociationAction.RMA_RECORD);
        
        customModelUtil.writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomChildAssocDefinition: ("+source+","+target+")");
        }
    }
    
    public void addCustomConstraintDefinition(QName constraintName, String title, boolean caseSensitive, List<String> allowedValues) 
    {
        ParameterCheck.mandatory("constraintName", constraintName);
        ParameterCheck.mandatoryString("title", title);
        ParameterCheck.mandatory("allowedValues", allowedValues);
        
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);
        
        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint != null)
        {
            throw new AlfrescoRuntimeException("Constraint already exists: "+constraintNameAsPrefixString);
        }
        
        customConstraint = deserializedModel.createConstraint(constraintNameAsPrefixString, CUSTOM_CONSTRAINT_TYPE);
        
        customConstraint.setTitle(title);
        customConstraint.createParameter(PARAM_ALLOWED_VALUES, allowedValues);
        customConstraint.createParameter(PARAM_CASE_SENSITIVE, caseSensitive ? "true" : "false");
        
        customModelUtil.writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomConstraintDefinition: "+constraintNameAsPrefixString+" (valueCnt: "+allowedValues.size()+")");
        }
    }
    
    /*
    public void addCustomConstraintDefinition(QName constraintName, String description, Map<String, Object> parameters) 
    {
        // TODO Auto-generated method stub
    }
    */
    
    public void changeCustomConstraintValues(QName constraintName, List<String> newAllowedValues)
    {
        ParameterCheck.mandatory("constraintName", constraintName);
        ParameterCheck.mandatory("newAllowedValues", newAllowedValues);
        
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);
        
        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint == null)
        {
            throw new AlfrescoRuntimeException("Unknown constraint ("+constraintNameAsPrefixString+")");
        }
        
        String type = customConstraint.getType();
        if ((type == null) || (! type.equals(CUSTOM_CONSTRAINT_TYPE)))
        {
            throw new AlfrescoRuntimeException("Unexpected type '"+type+"' for constraint: "+constraintNameAsPrefixString+" (expected '"+CUSTOM_CONSTRAINT_TYPE+"')");
        }
        
        customConstraint.removeParameter(PARAM_ALLOWED_VALUES);
        customConstraint.createParameter(PARAM_ALLOWED_VALUES, newAllowedValues);
        
        customModelUtil.writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("changeCustomConstraintValues: "+constraintNameAsPrefixString+" (valueCnt: "+newAllowedValues.size()+")");
        }
    }
    
    public void changeCustomConstraintTitle(QName constraintName, String title)
    {
        ParameterCheck.mandatory("constraintName", constraintName);
        ParameterCheck.mandatoryString("title", title);
        
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(contentService);
        
        M2Model deserializedModel = customModelUtil.readCustomContentModel();
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint == null)
        {
            throw new AlfrescoRuntimeException("Unknown constraint ("+constraintNameAsPrefixString+")");
        }
        
        String type = customConstraint.getType();
        if ((type == null) || (! type.equals(CUSTOM_CONSTRAINT_TYPE)))
        {
            throw new AlfrescoRuntimeException("Unexpected type '"+type+"' for constraint: "+constraintNameAsPrefixString+" (expected '"+CUSTOM_CONSTRAINT_TYPE+"')");
        }
        
        customConstraint.setTitle(title);
        
        customModelUtil.writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("changeCustomConstraintTitle: "+constraintNameAsPrefixString+" (title: "+title+")");
        }
    }
    
    public List<ConstraintDefinition> getCustomConstraintDefinitions() 
    {
        // all resolved defs (ie. constraint defs + property constraint defs (references + in-line defs)
        Collection<ConstraintDefinition> conDefs = dictionaryService.getConstraints(RecordsManagementCustomModel.RM_CUSTOM_MODEL);
        
        Collection<QName> customProps = dictionaryService.getProperties(RecordsManagementCustomModel.RM_CUSTOM_MODEL);
        for (QName customProp : customProps)
        {
            PropertyDefinition propDef = dictionaryService.getProperty(customProp);
            for (ConstraintDefinition conDef : propDef.getConstraints())
            {
                conDefs.remove(conDef);
            }
        }
        
        for (ConstraintDefinition conDef : conDefs)
        {
            Constraint con = conDef.getConstraint();
            if (! (con instanceof RMListOfValuesConstraint))
            {
                conDefs.remove(conDef);
            }
        }
        
        return new ArrayList<ConstraintDefinition>(conDefs);
    }
    
    public void removeCustomConstraintDefinition(QName constraintQName) 
    {
        throw new UnsupportedOperationException("removeCustomConstraintDefinition: "+ constraintQName);
    }
}