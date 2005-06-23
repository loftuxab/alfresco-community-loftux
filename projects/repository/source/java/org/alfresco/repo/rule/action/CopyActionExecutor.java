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
/**
 * 
 */
package org.alfresco.repo.rule.action;

import java.util.List;

import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;

/**
 * Copy action executor.
 * <p>
 * Copies the actioned upon node to a specified location.
 * 
 * @author Roy Wetherall
 */
public class CopyActionExecutor extends RuleActionExecutorAbstractBase
{
    public static final String NAME = "copy";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_ASSOC_TYPE_QNAME = "assoc-type";
    public static final String PARAM_ASSOC_QNAME = "assoc-name";
    public static final String PARAM_DEEP_COPY = "deep-copy";
    
    /**
     * Node operations service
     */
    private CopyService copyService;
	
	/**
	 * The node service
	 */
	private NodeService nodeService;
	
	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}
	
	public void setCopyService(CopyService copyService) 
	{
		this.copyService = copyService;
	}
    

	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, ParameterType.NODE_REF, true, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_TYPE_QNAME, ParameterType.QNAME, true, getParamDisplayLabel(PARAM_ASSOC_TYPE_QNAME)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_QNAME, ParameterType.QNAME, true, getParamDisplayLabel(PARAM_ASSOC_QNAME)));
		paramList.add(new ParameterDefinitionImpl(PARAM_DEEP_COPY, ParameterType.BOOLEAN, false, getParamDisplayLabel(PARAM_DEEP_COPY)));		
	}

    /**
     * @see org.alfresco.repo.rule.action.RuleActionExecutor#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(RuleAction ruleAction, NodeRef actionableNodeRef, NodeRef actionedUponNodeRef)
    {
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
	        NodeRef destinationParent = (NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
	        QName destinationAssocTypeQName = (QName)ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
	        QName destinationAssocQName = (QName)ruleAction.getParameterValue(PARAM_ASSOC_QNAME);
	        
	        // TODO get this from a parameter value
	        boolean deepCopy = false;
	        
	        this.copyService.copy(
	                actionedUponNodeRef, 
	                destinationParent,
	                destinationAssocTypeQName,
	                destinationAssocQName,
	                deepCopy);
		}
    }
}
