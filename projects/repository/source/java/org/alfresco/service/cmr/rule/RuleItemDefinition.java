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
package org.alfresco.service.cmr.rule;

import java.util.List;

public interface RuleItemDefinition 
{
	/**
	 * Get the name of the rule item.
	 * <p>
	 * The name is unique and is used to identify the rule item.
	 * 
	 * @return	the name of the rule action
	 */
	public String getName();
	
	/**
	 * The title of the rule item.
	 * 
	 * @return	the title of the rule item
	 */
	public String getTitle();
	
	/**
	 * The description of the rule item.
	 * 
	 * @return	the description of the rule item
	 */
	public String getDescription();
	
	/**
	 * A list containing the parmameter defintions for this rule item.
	 * 
	 * @return	a list of parameter definitions
	 */
	public List<ParameterDefinition> getParameterDefinitions();
    
    /**
     * Get the parameter definition by name
     * 
     * @param name  the name of the parameter
     * @return      the parameter definition, null if none found
     */
    public ParameterDefinition getParameterDefintion(String name);
}
