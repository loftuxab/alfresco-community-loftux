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

package org.alfresco.jlan.smb.nt;

import java.util.Vector;

/**
 * Security Id List Class
 * 
 * <p>Contains a list of SID objects.
 *
 * @author gkspencer
 */
public class SIDList {

	//	List of RID objects
	
	private Vector<SID> m_list;
	
	/**
	 * Default constructor
	 */
	public SIDList() {
		m_list = new Vector<SID>();
	}
	
	/**
	 * Add a SRID to the list
	 *
	 * @param sid SID
	 */
	public final void addSID(SID sid) {
		m_list.add(sid);
	}
	
	/**
	 * Return a SID from the list
	 *
	 * @param idx int
	 * @return SID
	 */
	public final SID getSIDAt(int idx) {
		if ( idx < 0 || idx >= m_list.size())
			return null;
		return m_list.get(idx);
	}
	
	/**
	 * Return the number of SIDs in the list
	 *
	 * @return int
	 */
	public final int numberOfSIDs() {
		return m_list.size();
	}

	/**
	 * Find the SID with the specified name
	 * 
	 * @param name String
	 * @return SID
	 */
	public final SID findSID(String name) {
		
		//	Search for the required SID
		
		for ( int i = 0; i < m_list.size(); i++) {
			
			//	Get the current SID
			
			SID curSID = m_list.get(i);
			
			if ( curSID.hasName() && curSID.getName().equals(name))
				return curSID;
		}
		
		//	SID not found
		
		return null;
	}

	/**
	 * Find the SID that matches the specified SID
	 * 
	 * @param sid SID
	 * @return SID
	 */	
	public final SID findSID(SID sid) {
		
		//	Search for the required SID
		
		for ( int i = 0; i < m_list.size(); i++) {
			
			//	Get the current SID
			
			SID curSID = m_list.get(i);
			
			if ( curSID.equalsSID(sid))
				return curSID;
		}
		
		//	SID not found
		
		return null;
	}

	/**
	 * Remove a SID from the list
	 *
	 * @param sid SID
	 * @return SID
	 */
	public final SID removeSID(SID sid) {
		
		//	Search for the SID in the list
		
		for ( int i = 0; i < m_list.size(); i++) {
			
			//	Get the current SID from the list
			
			SID curSID = m_list.get(i);
			if ( curSID.equalsSID(sid)) {
				
				//	Remove the SID from the list
				
				m_list.removeElementAt(i);
				
				//	Return the SID
				
				return curSID;
			}
		}
		
		//	SID not found, return null
		
		return null;
	}

	/**
	 * Remove all SIDs from the list
	 */
	public final void removeAllSIDs() {
		m_list.removeAllElements();	
	}
}
