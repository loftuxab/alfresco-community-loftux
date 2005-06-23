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
package org.alfresco.service.cmr.coci;

/**
 * Version opertaions service exception class
 * 
 * @author Roy Wetherall
 */
public class CheckOutCheckInServiceException extends RuntimeException 
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3258410621186618417L;

	/**
	 * Constructor
	 */
	public CheckOutCheckInServiceException() 
	{
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message  the error message
	 */
	public CheckOutCheckInServiceException(String message) 
	{
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message		the error message	
	 * @param throwable		the cause of the exeption
	 */
	public CheckOutCheckInServiceException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
