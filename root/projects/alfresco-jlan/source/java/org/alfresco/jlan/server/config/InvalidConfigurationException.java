/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.jlan.server.config;

/**
 * <p>Indicates that one or more parameters in the server configuration are not valid.
 *
 * @author gkspencer
 */
public class InvalidConfigurationException extends Exception {

  private static final long serialVersionUID = 4660972667850041322L;

  //	Chained exception details
	
  private Exception m_exception;
	
  /**
   * InvalidConfigurationException constructor.
   */
  public InvalidConfigurationException() {
    super();
  }

  /**
   * InvalidConfigurationException constructor.
   * 
   * @param s java.lang.String
   */
  public InvalidConfigurationException(String s) {
    super(s);
  }

	/**
	 * InvalidConfigurationException constructor.
	 * 
	 * @param s java.lang.String
	 * @param ex Exception
	 */
	public InvalidConfigurationException(String s, Exception ex) {
		super(s, ex);
		m_exception = ex;
	}
	
	/**
	 * Check if there is a chained exception
	 * 
	 * @return boolean
	 */
	public final boolean hasChainedException() {
		return m_exception != null ? true : false;
	}
	
	/**
	 * Return the chained exception details
	 * 
	 * @return Exception
	 */
	public final Exception getChainedException() {
		return m_exception;
	}
}
