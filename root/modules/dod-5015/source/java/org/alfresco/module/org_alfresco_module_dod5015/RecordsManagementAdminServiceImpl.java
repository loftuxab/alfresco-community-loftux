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
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.CustomReferenceId;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.DefineCustomAssociationAction;
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
    
    public static final String RMC_CUSTOM_ASSOCS = RecordsManagementCustomModel.RM_CUSTOM_PREFIX + ":customAssocs";
    
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
    
    public void addCustomPropertyDefinition(String aspectName, QName propQName, QName dataType, String title, String description)
    {
        addCustomPropertyDefinition(aspectName, propQName, dataType, title, description, null, false, false, false, null);
    }
    
    public void addCustomPropertyDefinition(String aspectName, QName propQName, QName dataType, String title, String description, String defaultValue, boolean multiValued, boolean mandatory, boolean isProtected, QName lovConstraint)
    {
        ParameterCheck.mandatoryString("aspectName", aspectName);
        ParameterCheck.mandatory("propQName", propQName);
        ParameterCheck.mandatory("dataType", dataType);
        
        M2Model deserializedModel = readCustomContentModel();
        M2Aspect customPropsAspect = deserializedModel.getAspect(aspectName);
        
        if (customPropsAspect == null)
        {
            throw new AlfrescoRuntimeException("Unknown aspect: "+aspectName);
        }
        
        String propQNameAsString = propQName.toPrefixString(namespaceService);
        
        M2Property customProp = customPropsAspect.getProperty(propQNameAsString);
        if (customProp != null)
        {
            throw new AlfrescoRuntimeException("Property already exists: "+propQNameAsString);
        }
        
        M2Property newProp = customPropsAspect.createProperty(propQNameAsString);
        newProp.setName(propQNameAsString);
        newProp.setType(dataType.toPrefixString(namespaceService));
        
        newProp.setTitle(title);
        newProp.setDescription(description);
        newProp.setDefaultValue(defaultValue);
        
        newProp.setMandatory(mandatory);
        newProp.setProtected(isProtected);
        newProp.setMultiValued(multiValued);
        
        if (lovConstraint != null)
        {
            if (! dataType.equals(DataTypeDefinition.TEXT))
            {
                throw new AlfrescoRuntimeException("Cannot apply constraint '"+lovConstraint+"' to property '"+propQNameAsString+"' with datatype '"+dataType+"' (expected: dataType = TEXT)");
            }
            
            String lovConstraintQNameAsString = lovConstraint.toPrefixString(namespaceService);
            newProp.addConstraintRef(lovConstraintQNameAsString);
        }
        
        writeCustomContentModel(deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomPropertyDefinition: "+propQNameAsString+" to aspect: "+aspectName);
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
        
        M2Model deserializedModel = readCustomContentModel();
        
        String aspectName = RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS;
        M2Aspect customAssocsAspect = deserializedModel.getAspect(aspectName);
        
        if (customAssocsAspect == null)
        {
            throw new AlfrescoRuntimeException("Unknown aspect: "+aspectName);
        }
        
        CustomReferenceId crId = new CustomReferenceId(label, null, null);
        String assocName = crId.getReferenceId();
        
        M2ClassAssociation customAssoc = customAssocsAspect.getAssociation(assocName);
        if (customAssoc != null)
        {
            throw new AlfrescoRuntimeException("Assoc already exists: "+assocName);
        }
        
        M2Association newAssoc = customAssocsAspect.createAssociation(assocName);
        newAssoc.setSourceMandatory(false);
        newAssoc.setTargetMandatory(false);
        
        // TODO Could be the customAssocs aspect
        newAssoc.setTargetClassName(DefineCustomAssociationAction.RMA_RECORD);
        
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
        
        CustomReferenceId crId = new CustomReferenceId(null, source, target);
        String assocName = crId.getReferenceId();
        
        M2ClassAssociation customAssoc = customAssocsAspect.getAssociation(assocName);
        if (customAssoc != null)
        {
            throw new AlfrescoRuntimeException("ChildAssoc already exists: "+assocName);
        }
        
        M2ChildAssociation newAssoc = customAssocsAspect.createChildAssociation(assocName);
        newAssoc.setSourceMandatory(false);
        newAssoc.setTargetMandatory(false);
        
        // TODO Could be the custom assocs aspect
        newAssoc.setTargetClassName(DefineCustomAssociationAction.RMA_RECORD);
        
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
}