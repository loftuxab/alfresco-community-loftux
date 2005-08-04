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
package org.alfresco.repo.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;

/**
 * Action implementation
 * 
 * @author Roy Wetherall
 */
public class ActionImpl extends ParameterizedItemImpl 
						implements Serializable, Action
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3258135760426186548L;
    
    /**
     * Rule action definition name
     */
    private String actionDefinitionName;
    
    /**
     * Action conditions
     */
    private List<ActionCondition> actionConditions = new ArrayList<ActionCondition>();

    /**
     * Constructor
     * 
     * @param id					the action id
     * @param actionDefinitionName  the name of the action definition
     */
    public ActionImpl(String id, String actionDefinitionName)
    {
        this(id, actionDefinitionName, null);
    }

    /**
     * Constructor 
     * 
     * @param id					the action id
     * @param actionDefinitionName  the action definition name
     * @param parameterValues       the parameter values
     */
    public ActionImpl(
    		String id,
    		String actionDefinitionName, 
            Map<String, Serializable> parameterValues)
    {
        super(id, parameterValues);
        this.actionDefinitionName = actionDefinitionName;
    }
    
    public String getActionDefinitionName()
    {
    	return this.actionDefinitionName;
    }

    /**
     * @see org.alfresco.service.cmr.action.Action#hasActionConditions()
     */
	public boolean hasActionConditions()
	{
		return (this.actionConditions.isEmpty() == false);
	}

	/**
	 * @see org.alfresco.service.cmr.action.Action#indexOfActionCondition(org.alfresco.service.cmr.action.ActionCondition)
	 */
	public int indexOfActionCondition(ActionCondition actionCondition)
	{
		return this.actionConditions.indexOf(actionCondition);
	}

	/**
	 * @see org.alfresco.service.cmr.action.Action#getActionConditions()
	 */
	public List<ActionCondition> getActionConditions()
	{
		return this.actionConditions;
	}

	/**
	 * @see org.alfresco.service.cmr.action.Action#getActionCondition(int)
	 */
	public ActionCondition getActionCondition(int index)
	{
		return this.actionConditions.get(index);
	}

	/**
	 * @see org.alfresco.service.cmr.action.Action#addActionCondition(org.alfresco.service.cmr.action.ActionCondition)
	 */
	public void addActionCondition(ActionCondition actionCondition)
	{
		this.actionConditions.add(actionCondition);
	}

	/**
	 * @see org.alfresco.service.cmr.action.Action#addActionCondition(int, org.alfresco.service.cmr.action.ActionCondition)
	 */
	public void addActionCondition(int index, ActionCondition actionCondition)
	{
		this.actionConditions.add(index, actionCondition);
	}

	/**
	 * @see org.alfresco.service.cmr.action.Action#setActionCondition(int, org.alfresco.service.cmr.action.ActionCondition)
	 */
	public void setActionCondition(int index, ActionCondition actionCondition)
	{
		this.actionConditions.set(index, actionCondition);
	}

	/**
	 * @see org.alfresco.service.cmr.action.Action#removeActionCondition(org.alfresco.service.cmr.action.ActionCondition)
	 */
	public void removeActionCondition(ActionCondition actionCondition)
	{
		this.actionConditions.remove(actionCondition);
	}

	/**
	 * @see org.alfresco.service.cmr.action.Action#removeAllActionConditions()
	 */
	public void removeAllActionConditions()
	{
		this.actionConditions.clear();
	}
}
