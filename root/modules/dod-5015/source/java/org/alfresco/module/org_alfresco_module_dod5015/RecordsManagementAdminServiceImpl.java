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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.BeforeCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.BeforeRemoveReference;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnRemoveReference;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.DefineCustomElementAbstractAction;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMListOfValuesConstraint;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Association;
import org.alfresco.repo.dictionary.M2ChildAssociation;
import org.alfresco.repo.dictionary.M2ClassAssociation;
import org.alfresco.repo.dictionary.M2Constraint;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.GUID;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Records Management AdminService Implementation.
 * 
 * @author Neil McErlean, janv
 */
public class RecordsManagementAdminServiceImpl implements RecordsManagementAdminService, RecordsManagementCustomModel
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementAdminServiceImpl.class);
    
    public static final String RMC_CUSTOM_ASSOCS = RecordsManagementCustomModel.RM_CUSTOM_PREFIX + ":customAssocs";
    
    private static final String CUSTOM_CONSTRAINT_TYPE = org.alfresco.module.org_alfresco_module_dod5015.caveat.RMListOfValuesConstraint.class.getName();
    
    private static final String PARAM_ALLOWED_VALUES = "allowedValues";
    private static final String PARAM_CASE_SENSITIVE = "caseSensitive";
    
    public static final String RMA_RECORD = "rma:record";
    
    private Map<QName, String> qnamesToClientNames;
    private static final String SOURCE_TARGET_ID_SEPARATOR = "__";
    
    /** Well-known node containing the dynamic ID mapping node. */
    private NodeRef dynamicIdMappingsNode = new NodeRef("workspace", "SpacesStore", "rm_dynamic_ids_map");

    /** Services */
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private ContentService contentService;

    private PolicyComponent policyComponent;
    
    /** Policy delegates */
    private ClassPolicyDelegate<BeforeCreateReference> beforeCreateReferenceDelegate;
    private ClassPolicyDelegate<OnCreateReference> onCreateReferenceDelegate;    
    private ClassPolicyDelegate<BeforeRemoveReference> beforeRemoveReferenceDelegate;
    private ClassPolicyDelegate<OnRemoveReference> onRemoveReferenceDelegate;
    
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
        beforeRemoveReferenceDelegate = policyComponent.registerClassPolicy(BeforeRemoveReference.class);
        onRemoveReferenceDelegate = policyComponent.registerClassPolicy(OnRemoveReference.class);
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
    
    protected void invokeBeforeRemoveReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // get qnames to invoke against
        Set<QName> qnames = RecordsManagementPoliciesUtil.getTypeAndAspectQNames(nodeService, fromNodeRef);
        // execute policy for node type and aspects
        BeforeRemoveReference policy = beforeRemoveReferenceDelegate.get(qnames);
        policy.beforeRemoveReference(fromNodeRef, toNodeRef, reference);
    }
    
    protected void invokeOnRemoveReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // get qnames to invoke against
        Set<QName> qnames = RecordsManagementPoliciesUtil.getTypeAndAspectQNames(nodeService, fromNodeRef);
        // execute policy for node type and aspects
        OnRemoveReference policy = onRemoveReferenceDelegate.get(qnames);
        policy.onRemoveReference(fromNodeRef, toNodeRef, reference);
    }

	/**
	 * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getCustomReferenceDefinitions()
	 */
    public Map<QName, AssociationDefinition> getCustomReferenceDefinitions()
    {
		QName relevantAspectQName = QName.createQName(RMC_CUSTOM_ASSOCS, namespaceService);
        AspectDefinition aspectDefn = dictionaryService.getAspect(relevantAspectQName);
        Map<QName, AssociationDefinition> assocDefns = aspectDefn.getAssociations();
        
        return assocDefns;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getCustomPropertyDefinitions()
     */
    public Map<QName, PropertyDefinition> getCustomPropertyDefinitions()
    {
    	Map<QName, PropertyDefinition> result = new HashMap<QName, PropertyDefinition>();
    	for (CustomisableRmElement elem : CustomisableRmElement.values())
    	{
    		result.putAll(getCustomPropertyDefinitions(elem));
    	}
        return result;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getCustomPropertyDefinitions(org.alfresco.module.org_alfresco_module_dod5015.CustomisableRmElement)
     */
    public Map<QName, PropertyDefinition> getCustomPropertyDefinitions(CustomisableRmElement rmElement)
    {
		QName relevantAspectQName = QName.createQName(rmElement.getCorrespondingAspect(), namespaceService);
        AspectDefinition aspectDefn = dictionaryService.getAspect(relevantAspectQName);
        Map<QName, PropertyDefinition> propDefns = aspectDefn.getProperties();

        return propDefns;
    }
    
    public void addCustomPropertyDefinition(String aspectName, String clientSideName, QName dataType, String title, String description)
    {
        addCustomPropertyDefinition(aspectName, clientSideName, dataType, title, description, null, false, false, false, null);
    }
    
    public void addCustomPropertyDefinition(String aspectName, String clientSideName, QName dataType, String title, String description, String defaultValue, boolean multiValued, boolean mandatory, boolean isProtected, QName lovConstraint)
    {
        ParameterCheck.mandatoryString("aspectName", aspectName);
        ParameterCheck.mandatory("clientSideName", clientSideName);
        ParameterCheck.mandatory("dataType", dataType);
        
        M2Model deserializedModel = readCustomContentModel();
        M2Aspect customPropsAspect = deserializedModel.getAspect(aspectName);

        QName newQName = this.generateAndRegisterQNameFor(clientSideName);
        String newQNameAsString = newQName.toPrefixString(namespaceService);
        
        if (customPropsAspect == null)
        {
            throw new AlfrescoRuntimeException("Unknown aspect: " + aspectName);
        }
        
        M2Property customProp = customPropsAspect.getProperty(newQNameAsString);
        if (customProp != null)
        {
            throw new AlfrescoRuntimeException("Property already exists: " + newQNameAsString);
        }
        
        M2Property newProp = customPropsAspect.createProperty(newQNameAsString);
        newProp.setName(newQNameAsString);
        newProp.setType(dataType.toPrefixString(namespaceService));
        
        newProp.setTitle(title);
        newProp.setTitle(clientSideName);
        newProp.setDescription(description);
        newProp.setDefaultValue(defaultValue);
        
        newProp.setMandatory(mandatory);
        newProp.setProtected(isProtected);
        newProp.setMultiValued(multiValued);
        
        if (lovConstraint != null)
        {
            if (! dataType.equals(DataTypeDefinition.TEXT))
            {
                throw new AlfrescoRuntimeException("Cannot apply constraint '"+lovConstraint+"' to property '"+newQNameAsString+"' with datatype '"+dataType+"' (expected: dataType = TEXT)");
            }
            
            String lovConstraintQNameAsString = lovConstraint.toPrefixString(namespaceService);
            newProp.addConstraintRef(lovConstraintQNameAsString);
        }
        
        writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomPropertyDefinition: "+clientSideName+
                    "=" + newQNameAsString + " to aspect: "+aspectName);
        }
    }
    
    public void removeCustomPropertyDefinition(QName propQName)
    {
        // data dictionary does not currently support incremental deletes
        throw new UnsupportedOperationException("removeCustomConstraintDefinition: "+propQName);
        
        /*
        ParameterCheck.mandatory("propQName", propQName);
        
        M2Model deserializedModel = readCustomContentModel();
        
        String propQNameAsString = propQName.toPrefixString(namespaceService);
        
        String aspectName = null;
        
        boolean found = false;
        
        // Need to select the correct aspect in the customModel from which we'll
        // attempt to delete the property definition.
        for (CustomisableRmElement elem : CustomisableRmElement.values())
        {
            aspectName = elem.getCorrespondingAspect();
            M2Aspect customPropsAspect = deserializedModel.getAspect(aspectName);
            
            if (customPropsAspect == null)
            {
                throw new AlfrescoRuntimeException("Unknown aspect: "+aspectName);
            }
            
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
            throw new AlfrescoRuntimeException("Could not find property to delete: "+propQNameAsString);
        }
        
        writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("deleteCustomPropertyDefinition: "+propQNameAsString+" from aspect: "+aspectName);
        }
        */
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#addCustomReference(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
	public void addCustomReference(NodeRef fromNode, NodeRef toNode, QName refId)
	{
		Map<QName, AssociationDefinition> availableAssocs = this.getCustomReferenceDefinitions();

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
		Map<QName, AssociationDefinition> availableAssocs = this.getCustomReferenceDefinitions();

		AssociationDefinition assocDef = availableAssocs.get(assocId);
		if (assocDef == null)
		{
			throw new IllegalArgumentException("No such custom reference: " + assocId);
		}
		
		invokeBeforeRemoveReference(fromNode, toNode, assocId);

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
		
		invokeOnRemoveReference(fromNode, toNode, assocId);
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
        
        M2Model deserializedModel = readCustomContentModel();
        
        String aspectName = RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS;
        M2Aspect customAssocsAspect = deserializedModel.getAspect(aspectName);
        
        if (customAssocsAspect == null)
        {
            throw new AlfrescoRuntimeException("Unknown aspect: "+aspectName);
        }
        
        // If this label is already taken...
        if (this.getQNameForClientId(label) != null)
        {
            throw new IllegalArgumentException("Reference label already in use: " + label);
        }

        QName generatedQName = this.generateAndRegisterQNameFor(label);
        String generatedShortQName = generatedQName.toPrefixString(namespaceService);
        
        M2ClassAssociation customAssoc = customAssocsAspect.getAssociation(generatedShortQName);
        if (customAssoc != null)
        {
            throw new AlfrescoRuntimeException("Assoc already exists: "+generatedShortQName);
        }
        
        M2Association newAssoc = customAssocsAspect.createAssociation(generatedShortQName);
        newAssoc.setSourceMandatory(false);
        newAssoc.setTargetMandatory(false);
        newAssoc.setTitle(label);
        
        // TODO Could be the customAssocs aspect
        newAssoc.setTargetClassName(RecordsManagementAdminServiceImpl.RMA_RECORD);
        
        writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomAssocDefinition: ("+label+")");
        }
    }
    
    public void addCustomChildAssocDefinition(String source, String target)
    {
        ParameterCheck.mandatoryString("source", source);
        ParameterCheck.mandatoryString("target", target);
        
        M2Model deserializedModel = readCustomContentModel();
        
        String aspectName = RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS;
        M2Aspect customAssocsAspect = deserializedModel.getAspect(aspectName);
        
        if (customAssocsAspect == null)
        {
            throw new AlfrescoRuntimeException("Unknown aspect: "+aspectName);
        }

        String compoundID = this.getCompoundIdFor(source, target);
        if (this.getQNameForClientId(compoundID) != null)
        {
            throw new IllegalArgumentException("Reference label already in use: " + compoundID);
        }
        
        M2ClassAssociation customAssoc = customAssocsAspect.getAssociation(compoundID);
        if (customAssoc != null)
        {
            throw new AlfrescoRuntimeException("ChildAssoc already exists: "+compoundID);
        }
        QName generatedQName = this.generateAndRegisterQNameFor(compoundID);
        
        M2ChildAssociation newAssoc = customAssocsAspect.createChildAssociation(generatedQName.toPrefixString(namespaceService));
        newAssoc.setSourceMandatory(false);
        newAssoc.setTargetMandatory(false);
        newAssoc.setTitle(compoundID);
        
        // TODO Could be the custom assocs aspect
        newAssoc.setTargetClassName(RecordsManagementAdminServiceImpl.RMA_RECORD);
        
        writeCustomContentModel(deserializedModel);
        
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
        
        M2Model deserializedModel = readCustomContentModel();
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint != null)
        {
            throw new AlfrescoRuntimeException("Constraint already exists: "+constraintNameAsPrefixString);
        }
        
        M2Constraint newCon = deserializedModel.createConstraint(constraintNameAsPrefixString, CUSTOM_CONSTRAINT_TYPE);
        
        newCon.setTitle(title);
        newCon.createParameter(PARAM_ALLOWED_VALUES, allowedValues);
        newCon.createParameter(PARAM_CASE_SENSITIVE, caseSensitive ? "true" : "false");
        
        writeCustomContentModel(deserializedModel);
        
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
        
        M2Model deserializedModel = readCustomContentModel();
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint == null)
        {
            throw new AlfrescoRuntimeException("Unknown constraint: "+constraintNameAsPrefixString);
        }
        
        String type = customConstraint.getType();
        if ((type == null) || (! type.equals(CUSTOM_CONSTRAINT_TYPE)))
        {
            throw new AlfrescoRuntimeException("Unexpected type '"+type+"' for constraint: "+constraintNameAsPrefixString+" (expected '"+CUSTOM_CONSTRAINT_TYPE+"')");
        }
        
        customConstraint.removeParameter(PARAM_ALLOWED_VALUES);
        customConstraint.createParameter(PARAM_ALLOWED_VALUES, newAllowedValues);
        
        writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("changeCustomConstraintValues: "+constraintNameAsPrefixString+" (valueCnt: "+newAllowedValues.size()+")");
        }
    }
    
    public void changeCustomConstraintTitle(QName constraintName, String title)
    {
        ParameterCheck.mandatory("constraintName", constraintName);
        ParameterCheck.mandatoryString("title", title);
        
        M2Model deserializedModel = readCustomContentModel();
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint == null)
        {
            throw new AlfrescoRuntimeException("Unknown constraint: "+constraintNameAsPrefixString);
        }
        
        String type = customConstraint.getType();
        if ((type == null) || (! type.equals(CUSTOM_CONSTRAINT_TYPE)))
        {
            throw new AlfrescoRuntimeException("Unexpected type '"+type+"' for constraint: "+constraintNameAsPrefixString+" (expected '"+CUSTOM_CONSTRAINT_TYPE+"')");
        }
        
        customConstraint.setTitle(title);
        
        writeCustomContentModel(deserializedModel);
        
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
        // throw new UnsupportedOperationException("removeCustomConstraintDefinition: "+ constraintQName);
        
        ParameterCheck.mandatory("constraintQName", constraintQName);
        
        M2Model deserializedModel = readCustomContentModel();
        
        String constraintNameAsPrefixString = constraintQName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint == null)
        {
            throw new AlfrescoRuntimeException("Constraint does not exist: "+constraintNameAsPrefixString);
        }
        
        deserializedModel.removeConstraint(constraintNameAsPrefixString);
        
        writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("deleteCustomConstraintDefinition: "+constraintNameAsPrefixString);
        }
    }
    
    private M2Model readCustomContentModel()
    {
        ContentReader reader = this.contentService.getReader(DefineCustomElementAbstractAction.RM_CUSTOM_MODEL_NODE_REF,
                                                             ContentModel.TYPE_CONTENT);
        
        if (reader.exists() == false) {throw new AlfrescoRuntimeException("RM CustomModel has no content.");}
        
        InputStream contentIn = null;
        M2Model deserializedModel = null;
        try
        {
            contentIn = reader.getContentInputStream();
            deserializedModel = M2Model.createModel(contentIn);
        }
        finally
        {
            try
            {
                if (contentIn != null) contentIn.close();
            }
            catch (IOException ignored)
            {
                // Intentionally empty.`
            }
        }
        return deserializedModel;
    }
    
    private void writeCustomContentModel(M2Model deserializedModel)
    {
        ContentWriter writer = this.contentService.getWriter(DefineCustomElementAbstractAction.RM_CUSTOM_MODEL_NODE_REF,
                                                             ContentModel.TYPE_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_XML);
        writer.setEncoding("UTF-8");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        deserializedModel.toXML(baos);
        
        final String updatedModelXml = baos.toString();
        
        writer.putContent(updatedModelXml);
        // putContent closes all resources.
    }

    
    // Dynamic ID handling. The following methods had been located in a dedicated
    // spring bean, but got inlined. There may be a case to refactor these out into
    // a bean again.
    
    //TODO After certification. This implementation currently does not support reference,
    // property, constraints definitions with the same names, which is technically allowed by Alfresco.
    public QName getQNameForClientId(String clientId)
    {
        Map<QName, String> mappings = this.getDynamicIdMappings();
        for (Map.Entry<QName, String> entry : mappings.entrySet())
        {
            if (entry.getValue().equals(clientId))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getClientIdForQName(QName qname)
    {
        Map<QName, String> mappings = this.getDynamicIdMappings();
        return mappings.get(qname);
    }
    
    public QName generateAndRegisterQNameFor(String clientId)
    {
        Map<QName, String> mappings = this.getDynamicIdMappings();
        if (mappings.containsValue(clientId))
        {
            throw new IllegalArgumentException("clientId already in use: " + clientId);
        }
        
        String newGUID = GUID.generate();
        QName newQName = QName.createQName(RM_CUSTOM_PREFIX, newGUID, namespaceService);
        
        this.qnamesToClientNames.put(newQName, clientId);
        this.saveMappings();
        
        return newQName;
    }
   
    public String[] splitSourceTargetId(String sourceTargetId)
    {
        if (!sourceTargetId.contains(SOURCE_TARGET_ID_SEPARATOR))
        {
            throw new IllegalArgumentException("Illegal sourceTargetId: " + sourceTargetId);
        }
        return sourceTargetId.split(SOURCE_TARGET_ID_SEPARATOR);
    }

    public String getCompoundIdFor(String sourceId, String targetId)
    {
        StringBuilder result = new StringBuilder();
        result.append(sourceId)
            .append(SOURCE_TARGET_ID_SEPARATOR)
            .append(targetId);
        return result.toString();
    }

    /**
     * Helper method which ensures lazy initialisation of the client-server ID mappings.
     * @return
     */
    private synchronized Map<QName, String> getDynamicIdMappings()
    {
        if (qnamesToClientNames == null)
        {
            loadMappings();
        }
        return qnamesToClientNames;
    }

    /**
     * Load the mappings from the persistent storage
     */
    private void loadMappings()
    {
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                // Get the mappings node
                if (nodeService.exists(dynamicIdMappingsNode) == false)
                {
                    throw new AlfrescoRuntimeException("Unable to find records management dynamicIdMappings node.");
                }
                
                // Read content from config node
                ContentReader reader = contentService.getReader(dynamicIdMappingsNode, ContentModel.PROP_CONTENT);
                String jsonString = reader.getContentString();
                
                JSONObject configJSON = new JSONObject(jsonString);
                JSONArray mappingsJSON = configJSON.getJSONArray("mappings");
                
                qnamesToClientNames = new HashMap<QName, String>(mappingsJSON.length());
                
                for (int i = 0; i < mappingsJSON.length(); i++)
                {
                    // Get the JSON object that represents the reference
                    JSONObject mappingJSON = mappingsJSON.getJSONObject(i);
                    
                    // Get the details of the event
                    String qnameString = mappingJSON.getString("qname");
                    String displayID = mappingJSON.getString("displayID");
                    
                    QName qname = QName.createQName(qnameString, namespaceService);
                    
                    qnamesToClientNames.put(qname, displayID);                    
                }
                return null;
            }
            
        }, AuthenticationUtil.getSystemUserName());
    }

    /**
     * Save the mappings to the peristent storage
     */
    private void saveMappings()
    {
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                // Get the mappings node
                if (nodeService.exists(dynamicIdMappingsNode) == false)
                {
                    throw new AlfrescoRuntimeException("Unable to find records management dynamicIdMappings node.");
                }
                
                JSONObject configJSON = new JSONObject();                        
                JSONArray mappingsJSON = new JSONArray();
                
                int index = 0;
                for (QName qn : qnamesToClientNames.keySet())
                {
                    JSONObject mappingJSON = new JSONObject();
                    mappingJSON.put("qname", qn.toPrefixString(namespaceService));
                    mappingJSON.put("displayID", qnamesToClientNames.get(qn));
                    
                    mappingsJSON.put(index, mappingJSON);
                    index++;
                }                        
                configJSON.put("mappings", mappingsJSON);
                
                // Get content writer
                ContentWriter contentWriter = contentService.getWriter(dynamicIdMappingsNode, ContentModel.PROP_CONTENT, true);
                contentWriter.putContent(configJSON.toString());
                
                return null;
            }
            
        }, AuthenticationUtil.getSystemUserName());
    }
}
