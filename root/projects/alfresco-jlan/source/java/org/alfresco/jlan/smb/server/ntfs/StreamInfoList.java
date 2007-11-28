package org.alfresco.jlan.smb.server.ntfs;

/*
 * StreamInfoList.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.util.Vector;

/**
 * Stream Information List Class
 */
public class StreamInfoList {

	//	List of stream information objects
	
	private Vector m_list;
	
	/**
	 * Default constructor
	 */
	public StreamInfoList() {
		m_list = new Vector();
	}
	
	/**
	 * Add an item to the list
	 * 
	 * @param info StreamInfo
	 */
	public final void addStream(StreamInfo info) {
		m_list.addElement(info);
	}
	
	/**
	 * Return the stream details at the specified index
	 *
	 * @param idx int
	 * @return StreamInfo
	 */
	public final StreamInfo getStreamAt(int idx) {
		
		//	Range check the index
		
		if ( idx < 0 || idx >= m_list.size())
			return null;
			
		//	Return the required stream information
		
		return (StreamInfo) m_list.elementAt(idx);
	}
	
	/**
	 * Find a stream by name
	 * 
	 * @param name String
	 * @return StreamInfo
	 */
	public final StreamInfo findStream(String name) {
		
		//	Search for the required stream
		
		for ( int i = 0; i < m_list.size(); i++) {
			
			//	Get the current stream information
			
			StreamInfo sinfo = (StreamInfo) m_list.elementAt(i);
			
			//	Check if the stream name matches
			
			if ( sinfo.getName().equals(name))
				return sinfo;
		}
		
		//	Stream not found
		
		return null;
	}
	
	/**
	 * Return the count of streams in the list
	 * 
	 * @return int
	 */
	public final int numberOfStreams() {
		return m_list.size();
	}
	
	/**
	 * Remove the specified stream from the list
	 * 
	 * @param idx int
	 * @return StreamInfo
	 */
	public final StreamInfo removeStream(int idx) {		

		//	Range check the index
			
		if ( idx < 0 || idx >= m_list.size())
			return null;
				
		//	Remove the required stream
		
		StreamInfo info = (StreamInfo) m_list.elementAt(idx);
		m_list.removeElementAt(idx);
		return info;
	}
	
	/**
	 * Remove the specified stream from the list
	 *
	 * @param name String
	 * @return StreamInfo
	 */
	public final StreamInfo removeStream(String name) {
		
		//	Search for the required stream
		
		for ( int i = 0; i < m_list.size(); i++) {
			
			//	Get the current stream information
			
			StreamInfo sinfo = (StreamInfo) m_list.elementAt(i);
			
			//	Check if the stream name matches
			
			if ( sinfo.getName().equals(name)) {
				
				//	Remove the stream from the list

				m_list.removeElementAt(i);
				return sinfo;
			}
		}
		
		//	Stream not found
		
		return null;
	}
	
	/**
	 * Remove all streams from the list
	 */
	public final void removeAllStreams() {
		m_list.removeAllElements();
	}
}
