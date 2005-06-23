/**
 * 
 */
package org.alfresco.repo.rule.condition;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.namespace.QName;

/**
 * No condition evaluator implmentation.
 * 
 * @author Roy Wetherall
 */
public class NoConditionEvaluator extends RuleConditionEvaluatorAbstractBase
{
	/**
	 * Evaluator constants
	 */
	public static final String NAME = "no-condition";	
	
    /**
     * The node service
     */
    private NodeService nodeService;
    
    /**
     * The dictionary service
     */
    private DictionaryService dictionaryService;
    
    /**
     * Set node service
     * 
     * @param nodeService  the node service
     */
    public void setNodeService(NodeService nodeService) 
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set dictionary service
     * 
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService) 
    {
        this.dictionaryService = dictionaryService;
    }   
    
    
    public boolean evaluateImpl(RuleCondition ruleCondition, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
        boolean result = false;
        
        if (this.nodeService.exists(actionedUponNodeRef) == true)
        {
            // TODO: Move this type check into its own Class Evaluator
            QName nodeType = nodeService.getType(actionedUponNodeRef);
            if (dictionaryService.isSubClass(nodeType, ContentModel.TYPE_CONTENT))
            {
                result = true;
            }
        }
        
        return result;
    }

	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		// No parameters to add
	}

}
