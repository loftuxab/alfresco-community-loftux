/**
 * Created on Jun 16, 2005
 */
package org.alfresco.repo.rule.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.namespace.QName;

/**
 * Link category action executor
 * 
 * @author Roy Wetherall
 */
public class LinkCategoryActionExecutor extends RuleActionExecutorAbstractBase 
{
	/**
	 * Rule constants
	 */
	public static final String NAME = "link-category";
    public static final String PARAM_CATEGORY_ASPECT = "category-aspect";
    public static final String PARAM_CATEGORY_VALUE = "category-value";
	
	/**
	 * The node service
	 */
	private NodeService nodeService;
	
	/**
	 * The category service
	 */
	private CategoryService categoryService;
	
	/**
	 * The dictionary service
	 */
	private DictionaryService dictionaryService;
    
    /**
     * Sets the node service
     * 
     * @param nodeService  the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Sets the category service
     * 
     * @param categoryService   the category service
     */
    public void setCategoryService(CategoryService categoryService)
    {
        this.categoryService = categoryService;
    }
    
    /**
     * Sets the dictionary service
     * 
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Add the parameter definitions
     */
	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
        paramList.add(new ParameterDefinitionImpl(PARAM_CATEGORY_ASPECT, ParameterType.QNAME, true, getParamDisplayLabel(PARAM_CATEGORY_ASPECT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_CATEGORY_VALUE, ParameterType.NODE_REF, true, getParamDisplayLabel(PARAM_CATEGORY_VALUE)));
	}
	
    /**
     * Execute action implementation
     */
    @Override
    protected void executeImpl(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
		// Double check that the node still exists
		if (this.nodeService.exists(actionableNodeRef) == true)
		{
			// Get the rule parameter values
			QName categoryAspect = (QName)ruleAction.getParameterValue(PARAM_CATEGORY_ASPECT);
			NodeRef categoryValue = (NodeRef)ruleAction.getParameterValue(PARAM_CATEGORY_VALUE);
			
            // Check that the apect is classifiable and is currently applied to the node
            if (this.dictionaryService.isSubClass(categoryAspect, ContentModel.ASPECT_CLASSIFIABLE) == true)
            {
                // Get the category property qname
                QName categoryProperty = null;
                Map<QName, PropertyDefinition> propertyDefs = this.dictionaryService.getAspect(categoryAspect).getProperties();
                for (Map.Entry<QName, PropertyDefinition> entry : propertyDefs.entrySet()) 
                {
                    if (PropertyTypeDefinition.CATEGORY.equals(entry.getValue().getPropertyType().getName()) == true)
                    {
                        // Found the category property
                        categoryProperty = entry.getKey();
                        break;
                    }
                }
                
                if (categoryAspect != null)
                {
                    // Add the aspect setting the category property to the approptiate values
                    Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
                    properties.put(categoryProperty, categoryValue);
                    this.nodeService.addAspect(actionedUponNodeRef, categoryAspect, properties);
                }
            }			
		}
	}
}
