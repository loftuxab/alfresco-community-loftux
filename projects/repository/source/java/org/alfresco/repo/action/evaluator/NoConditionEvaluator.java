/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.action.evaluator;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * No condition evaluator implmentation.
 * 
 * @author Roy Wetherall
 */
public class NoConditionEvaluator extends ActionConditionEvaluatorAbstractBase
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
    
    
    public boolean evaluateImpl(ActionCondition ruleCondition, NodeRef actionedUponNodeRef)
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
