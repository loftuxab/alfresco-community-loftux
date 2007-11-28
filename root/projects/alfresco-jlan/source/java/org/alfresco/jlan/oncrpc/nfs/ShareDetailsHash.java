package org.alfresco.jlan.oncrpc.nfs;

/*
 * ShareDetailsHash.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.util.Hashtable;

/**
 * Share Details Hash Class
 * 
 * <p>Hashtable of ShareDetails for the available disk shared devices. ShareDetails are indexed using the
 * hash of the share name to allow mounts to be persistent across server restarts.
 */
public class ShareDetailsHash {

	//	Share name hash to share details
	
	private Hashtable<Integer, ShareDetails> m_details;
	
	/**
	 * Class constructor
	 */
	public ShareDetailsHash() {
		m_details = new Hashtable<Integer, ShareDetails>();
	}
	
	/**
	 * Add share details to the list of available shares
	 * 
	 * @param details ShareDetails 
	 */
	public final void addDetails(ShareDetails details) {
		m_details.put(new Integer(details.getName().hashCode()), details);
	}
	
	/**
	 * Delete share details from the list
	 *
	 * @param shareName String
	 * @return ShareDetails 
	 */
	public final ShareDetails deleteDetails(String shareName) {
		return m_details.get(new Integer(shareName.hashCode()));
	}
	
	/**
	 * Find share details for the specified share name
	 * 
	 * @param shareName String
	 * @return ShareDetails
	 */
	public final ShareDetails findDetails(String shareName) {
		
		//	Get the share details for the associated share name
		
		ShareDetails details = m_details.get(new Integer(shareName.hashCode()));
			
		//	Return the share details
		 
		return details; 
	}
	
	/**
	 * Find share details for the specified share name hash code
	 *
	 * @param hashCode int
	 * @return ShareDetails 
	 */
	public final ShareDetails findDetails(int hashCode) {
		
		//	Get the share details for the associated share name
		
		ShareDetails details = m_details.get(new Integer(hashCode));
			
		//	Return the share details
		 
		return details; 
	}
}
