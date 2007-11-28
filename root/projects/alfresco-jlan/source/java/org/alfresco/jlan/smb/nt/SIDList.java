package org.alfresco.jlan.smb.nt;

/*
 * SIDList.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.util.Vector;

/**
 * Security Id List Class
 * 
 * <p>Contains a list of SID objects.
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
