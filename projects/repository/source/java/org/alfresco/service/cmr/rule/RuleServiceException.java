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

/**
 * Rule Service Exception Class
 * 
 * @author Roy Wetherall
 */
public class RuleServiceException extends RuntimeException 
{
	/**
	 * Serial version UID 
	 */
	private static final long serialVersionUID = 3257571685241467958L;

	/**
	 * Constructor
	 */
	public RuleServiceException() 
	{
		super();
	}

	/**
	 * Construtor
	 * 
	 * @param message 	the message string
	 */
	public RuleServiceException(String message) 
	{
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message	the message string
	 * @param source	the source exception
	 */
	public RuleServiceException(String message, Throwable source) 
	{
		super(message, source);
	}

	/**
	 * Constructor
	 * 
	 * @param source	the source exception
	 */
	public RuleServiceException(Throwable source) 
	{
		super(source);
	}
}
