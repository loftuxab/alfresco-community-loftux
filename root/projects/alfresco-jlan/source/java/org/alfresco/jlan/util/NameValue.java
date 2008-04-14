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

package org.alfresco.jlan.util;

/**
 * Name Value Pair Class
 *
 * @author gkspencer
 */
public class NameValue {

	//	Item name
	
	private String m_name;
	
	//	Item value
	
	private Object m_value;
	
	/**
	 * Class constructor
	 * 
	 * @param name String
	 * @param val  Object
	 */
	public NameValue(String name, Object val) {
		m_name  = name;
		m_value = val;
	}
	
	/**
	 * Return the item name
	 * 
	 * @return String
	 */
	public final String getName() {
		return m_name;
	}
	
	/**
	 * Return the item value
	 * 
	 * @return String
	 */
	public final String getValue() {
		if ( m_value instanceof String)
			return (String) m_value;
		return m_value.toString();
	}

	/**
	 * Return the object value
	 * 
	 * @return Object
	 */
	public final Object getObject() {
		return m_value;	
	}

	/**
	 * Check if the value is a valid integer within the specified range
	 *
	 * @param low int
	 * @param high int
	 * @return int
	 * @exception NumberFormatException
	 */
	public final int getInteger(int low, int high)
		throws NumberFormatException {
		
		//	Check if the value is valid
		
		if ( m_value == null)
			throw new NumberFormatException("No value");
			
		//	Convert the value to an integer

		int ival = Integer.parseInt(getValue());
		
		//	Check if the value is within the valid range
		
		if ( ival < low || ival > high)
			throw new NumberFormatException("Out of valid range");
		
		//	Return the integer value
		
		return ival; 
	}
	
	/**
	 * Check if the value is a valid long within the specified range
	 *
	 * @param low long
	 * @param high long
	 * @return long
	 * @exception NumberFormatException
	 */
	public final long getLong(long low, long high)
		throws NumberFormatException {
		
		//	Check if the value is valid
		
		if ( m_value == null)
			throw new NumberFormatException("No value");
			
		//	Convert the value to a long

		long lval = Long.parseLong(getValue());
		
		//	Check if the value is within the valid range
		
		if ( lval < low || lval > high)
			throw new NumberFormatException("Out of valid range");
		
		//	Return the long value
		
		return lval; 
	}
	
	/**
	 * Return the name/value pair as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		
		str.append("[");
		str.append(getName());
		str.append(",");
		str.append(getValue());
		str.append("]");
		
		return str.toString();
	}
}
