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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.BeforeCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnCreateReference;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Records Management AdminService Implementation.
 * 
 * @author Neil McErlean
 */
public class RecordsManagementAdminServiceImpl implements RecordsManagementAdminService
{
	/** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementAdminServiceImpl.class);

    public static final String CUSTOM_MODEL_PREFIX = "rmc";
    public static final String RMC_CUSTOM_ASSOCS = CUSTOM_MODEL_PREFIX + ":customAssocs";

    /** Services */
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
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

	public void addCustomConstraintDefinition(QName constraintName,
			boolean caseSensitive, List<String> allowedValues) {
		// TODO Auto-generated method stub
	}

	public void addCustomConstraintDefinition(QName constraintName,
			String description, Map<String, Object> parameters) {
		// TODO Auto-generated method stub
	}

	public void changeCustomConstraintValues(QName constraintName,
			List<String> newValues) {
		// TODO Auto-generated method stub
	}

	public List<ConstraintDefinition> getCustomConstraintDefinitions() {
		// TODO I'm returning dummy data here for testing purposes.
		//      Obviously this will have to return live data
		List<ConstraintDefinition> result = new ArrayList<ConstraintDefinition>();
		
		ConstraintDefinition dummyData = new ConstraintDefinition() {
			public ModelDefinition getModel() {
				return null;
 			}
			public Constraint getConstraint() {
				return new Constraint() {
					public void evaluate(Object value) {}

					public Map<String, Object> getParameters() {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("caseSensitive", false);
						hashMap.put("allowedValues", Arrays.asList(new String[]{"foo", "bar", "other"}));
						return hashMap;
					}

					public String getType() {
						return "LIST";
					}

					public void initialize() {}

                    public String getTitle() {
                        return "title";
                    }
				};
			}
			public QName getName() {
				return QName.createQName("rmc:dummy", namespaceService);
			}
            public String getDescription()
            {
                return "description";
            }
            public String getTitle()
            {
                return "title";
            }
			
		};
		result.add(dummyData);
		return result;
		
		// The result would have to come from the dataDictionary.
		// So the DictionaryService will need to have something like
		// public List<ConstraintDefinition> getConstraints(QName model);
		//
		// It currently offers access to ConstraintDefinitions through the
		// PropertyDefinition, but RM will need to access Constraints not yet associated
		// with properties.

	}

	public void removeCustomConstraintDefinition(QName constraintName) {
		// TODO Auto-generated method stub
	}
}