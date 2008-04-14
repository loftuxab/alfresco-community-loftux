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
 * RID List Class
 *
 * @author gkspencer
 */
public class RIDList {

	//	List of RID objects
	
	private Vector<RID> m_list;
	
	/**
	 * Default constructor
	 */
	public RIDList() {
		m_list = new Vector<RID>();
	}
	
	/**
	 * Add a RID to the list
	 *
	 * @param rid RID
	 */
	public final void addRID(RID rid) {
		m_list.add(rid);
	}
	
	/**
	 * Add RIDs from the spefied list to this list
	 * 
	 * @param list RIDList
	 */
	public final void addRIDs( RIDList list) {
	  if( list != null && list.numberOfRIDs() > 0) {
	    for ( int i = 0; i < list.numberOfRIDs(); i++)
	      addRID( list.getRIDAt( i));
	  }
	}
	
	/**
	 * Return a RID from the list
	 *
	 * @param idx int
	 * @return RID
	 */
	public final RID getRIDAt(int idx) {
		if ( idx < 0 || idx >= m_list.size())
			return null;
		return m_list.get(idx);
	}
	
	/**
	 * Return the number of RIDs in the list
	 *
	 * @return int
	 */
	public final int numberOfRIDs() {
		return m_list.size();
	}

	/**
	 * Find the RID with the specified name and type
	 * 
	 * @param name String
	 * @param typ int
	 * @return RID
	 */
	public final RID findRID(String name, int typ) {
		
		//	Search for the required RID
		
		for ( int i = 0; i < m_list.size(); i++) {
			
			//	Get the current RID
			
			RID curRID = m_list.get(i);
			
			if ( curRID.isType() == typ && curRID.getName().equals(name))
				return curRID;
		}
		
		//	RID no found
		
		return null;
	}

	/**
	 * Find the RID with the specified id and type
	 * 
	 * @param id int
	 * @param typ int
	 * @return RID
	 */	
	public final RID findRID(int id, int typ) {
		
		//	Search for the required RID
		
		for ( int i = 0; i < m_list.size(); i++) {
			
			//	Get the current RID
			
			RID curRID = m_list.get(i);
			
			if ( curRID.getRID() == id && curRID.isType() == typ)
				return curRID;
		}
		
		//	RID no found
		
		return null;
	}

	/**
	 * Find the RID with the specified id
	 * 
	 * @param id int
	 * @return RID
	 */	
	public final RID findRID(int id) {
		
		//	Search for the required RID
		
		for ( int i = 0; i < m_list.size(); i++) {
			
			//	Get the current RID
			
			RID curRID = m_list.get(i);
			
			if ( curRID.getRID() == id)
				return curRID;
		}
		
		//	RID no found
		
		return null;
	}

	/**
	 * Remove a RID from the list
	 *
	 * @param id int
	 * @return RID
	 */
	public final RID removeRID(int id) {
		
		//	Search for the RID in the list
		
		for ( int i = 0; i < m_list.size(); i++) {
			
			//	Get the current RID from the list
			
			RID curRID = m_list.get(i);
			if ( curRID.getRID() == id) {
				
				//	Remove the RID from the list
				
				m_list.removeElementAt(i);
				
				//	Return the RID
				
				return curRID;
			}
		}
		
		//	RID not found, return null
		
		return null;
	}

	/**
	 * Remove all RIDs from the list
	 */
	public final void removeAllRIDs() {
		m_list.removeAllElements();	
	}
	
	/**
	 * Get the id list
	 * 
	 * @return int[]
	 */
	public final int[] getIdList() {
	  
	  //	Allocate the id list array
	  
	  int[] ids = new int[m_list.size()];
	  
	  //	Copy the relative ids to the array
	  
	  for ( int i = 0; i < m_list.size(); i++) {
	    RID rid = m_list.get(i);
	    ids[i] = rid.getRID();
	  }
	  
	  //	Return the relative ids list
	  
	  return ids;
	}
}
