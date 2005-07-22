/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.rule.action;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;

/**
 * Add features action executor implementation.
 * 
 * @author Roy Wetherall
 */
public class AddFeaturesActionExecuter extends RuleActionExecuterAbstractBase
{
    /**
     * Action constants
     */
	public static final String NAME = "add-features";
	public static final String PARAM_ASPECT_NAME = "aspect-name";
    public static final String PARAM_ASPECT_PROPERTIES = "aspect_properties";
	
	/**
	 * The node service
	 */
	private NodeService nodeService;
	
    /**
     * Set the node service
     * 
     * @param nodeService  the node service
     */
	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}

    /**
     * @see org.alfresco.repo.rule.action.RuleActionExecuter#execute(org.alfresco.service.cmr.repository.NodeRef, NodeRef)
     */
    public void executeImpl(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
	        // Get the name of the aspec to add
			Map<String, Serializable> paramValues = ruleAction.getParameterValues();
	        QName aspectQName = (QName)paramValues.get(PARAM_ASPECT_NAME);
	        
			// Get the aspect properties (may be null if no values set)
            Map<QName, Serializable> properties = (Map<QName, Serializable>)paramValues.get(PARAM_ASPECT_PROPERTIES);
			
	        // Add the aspect
	        this.nodeService.addAspect(actionedUponNodeRef, aspectQName, properties);
		}
    }

    /**
     * Add parameter definitions
     */
	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		paramList.add(new ParameterDefinitionImpl(PARAM_ASPECT_NAME, ParameterType.QNAME, true, getParamDisplayLabel(PARAM_ASPECT_NAME)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ASPECT_PROPERTIES, ParameterType.PROPERTY_VALUES, false, getParamDisplayLabel(PARAM_ASPECT_PROPERTIES)));
	}

}
