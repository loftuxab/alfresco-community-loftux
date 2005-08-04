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
package org.alfresco.service.cmr.action;

import java.io.Serializable;
import java.util.Map;

/**
 * Rule item interface
 * 
 * @author Roy Wetherall
 */
public interface ParameterizedItem
{
	/**
	 * Unique identifier for the parameterized item
	 * 
	 * @return	the id string
	 */
	public String getId();
	
	/**
	 * Get the parameter values
	 * 
	 * @return	get the parameter values
	 */
	public Map<String, Serializable> getParameterValues();
	
	/**
	 * Get value of a named parameter.
	 * 
	 * @param name	the parameter name
	 * @return		the value of the parameter
	 */
	public Serializable getParameterValue(String name);
	
	/**
	 * Sets the parameter values
	 * 
	 * @param parameterValues	the parameter values
	 */
	public void setParameterValues(
            Map<String, Serializable> parameterValues);
	
	/**
	 * Sets the value of a parameter.
	 * 
	 * @param name		the parameter name
	 * @param value		the parameter value
	 */
	public void setParameterValue(String name, Serializable value);
}
