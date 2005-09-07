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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ActionServiceException;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * Compare property value evaluator
 * 
 * @author Roy Wetherall
 */
public class ComparePropertyValueEvaluator extends ActionConditionEvaluatorAbstractBase 
{
	/**
	 * Evaluator constants
	 */
	public final static String NAME = "compare-property-value";
    public final static String PARAM_PROPERTY = "property"; 
	public final static String PARAM_VALUE = "value";
	public final static String PARAM_OPERATION = "operation";
    
	/**
     * The default property to check if none is specified in the properties
     */
    private final static QName DEFAULT_PROPERTY = ContentModel.PROP_NAME;
    
    /**
     * I18N message ID's
     */
    private static final String MSGID_INVALID_OPERATION = "compare_property_value_evaluator.invalid_operation";
    
    /**
     * Map of comparators used by different property types
     */
    private Map<QName, PropertyValueComparator> comparators = new HashMap<QName, PropertyValueComparator>();
    
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
     * Set the dictionary service
     * 
     * @param dictionaryService     the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    /**
     * Set the list of property value comparators
     *
     * @param comparators  the list of property value comparators
     */
    public void setPropertyValueComparators(List<PropertyValueComparator> comparators)
    {
        for (PropertyValueComparator comparator : comparators)
        {
            comparator.registerComparator(this);
        }
    }
    
    /**
     * Registers a comparator for a given property data type.
     * 
     * @param dataType      property data type
     * @param comparator    property value comparator
     */
    public void registerComparator(QName dataType, PropertyValueComparator comparator)
    {
        this.comparators.put(dataType, comparator);
    }
	
    /**
     * Add paremeter defintions
     */
	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
        paramList.add(new ParameterDefinitionImpl(PARAM_PROPERTY, DataTypeDefinition.QNAME, false, getParamDisplayLabel(PARAM_PROPERTY)));
		paramList.add(new ParameterDefinitionImpl(PARAM_VALUE, DataTypeDefinition.ANY, true, getParamDisplayLabel(PARAM_VALUE)));
		paramList.add(new ParameterDefinitionImpl(PARAM_OPERATION, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_OPERATION)));
	}

	/**
     * @see ActionConditionEvaluatorAbstractBase#evaluateImpl(ActionCondition, NodeRef)
	 */
	public boolean evaluateImpl(
			ActionCondition ruleCondition,
			NodeRef actionedUponNodeRef) 
	{
		boolean result = false;
		
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
		    // Get the name value of the node
            QName propertyQName = (QName)ruleCondition.getParameterValue(PARAM_PROPERTY);
            if (propertyQName == null)
            {
                propertyQName = DEFAULT_PROPERTY;
            }
            
            // Get the origional value and the value to match
            Serializable propertyValue = this.nodeService.getProperty(actionedUponNodeRef, propertyQName);
            Serializable compareValue = ruleCondition.getParameterValue(PARAM_VALUE);
            
            // Get the operation
            ComparePropertyValueOperation operation = (ComparePropertyValueOperation)ruleCondition.getParameterValue(PARAM_OPERATION);
            
            // Look at the type of the property (assume to be ANY if none found in dicitionary)
            QName propertyTypeQName = DataTypeDefinition.ANY;
            PropertyDefinition propertyDefintion = this.dictionaryService.getProperty(propertyQName);
            if (propertyDefintion != null)
            {
                propertyTypeQName = propertyDefintion.getDataType().getName();
            }
            
            // Try and get a matching comparator
            PropertyValueComparator comparator = this.comparators.get(propertyTypeQName);
            if (comparator != null)
            {
                // Call the comparator for the property type
                result = comparator.compare(propertyValue, compareValue, operation);
            }
            else
            {
                // The default behaviour is to assume the property can only be compared using equals
                if (operation != null && operation != ComparePropertyValueOperation.EQUALS)
                {
                    // Error since only the equals operation is valid
                    throw new ActionServiceException(
                            MSGID_INVALID_OPERATION, 
                            new Object[]{operation.toString(), propertyTypeQName.toString()});
                }
                
                // Use equals to compare the values
                result = compareValue.equals(propertyValue);
            }
        }
		
		return result;
	}
}
