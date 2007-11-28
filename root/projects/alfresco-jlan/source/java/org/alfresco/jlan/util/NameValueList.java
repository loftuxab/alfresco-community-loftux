package org.alfresco.jlan.util;

/*
 * NameValueList.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.util.Vector;

/**
 * Name/Value Pair List Class
 */
public class NameValueList {

	//	List of name/value pairs
	
	private Vector<NameValue> m_list;
	
	/**
	 * Default constructor
	 */
	public NameValueList() {
		m_list = new Vector<NameValue>();
	}
	
	/**
	 * Add a name/value pair to the list
	 * 
	 * @param nameVal NameValue
	 */
	public final void addItem(NameValue nameVal) {
		m_list.add(nameVal);
	}
	
	/**
	 * Return the count of items in the list
	 * 
	 * @return int
	 */
	public final int numberOfItems() {
		return m_list.size();
	}

	/**
	 * Return the specified item
	 * 
	 * @param idx
	 * @return NameValue
	 */
	public final NameValue getItemAt(int idx) {
		if ( idx < 0 || idx >= m_list.size())
			return null;
		return m_list.get(idx);
	}
		
	/**
	 * Find an item in the list
	 * 
	 * @param name String
	 * @return NameValue
	 */
	public final NameValue findItem(String name) {
		for ( int i = 0; i < m_list.size(); i++) {
			NameValue nameVal = m_list.get(i);
			if ( nameVal.getName().compareTo(name) == 0)
				return nameVal;
		}
		return null;
	}

	/**
	 * Find all items with the specified name and return as a new list
	 * 
	 * @param name String
	 * @return NameValueList
	 */
	public final NameValueList findAllItems(String name) {
		
		//	Allocate the list to hold the matching items
		
		NameValueList list = new NameValueList();
		
		//	Find the matching items
		
		for ( int i = 0; i < m_list.size(); i++) {
			NameValue nameVal = m_list.get(i);
			if ( nameVal.getName().compareTo(name) == 0)
				list.addItem(nameVal);
		}
		
		//	Check if the list is empty, return the list
		
		if ( list.numberOfItems() == 0)
			list = null;
		return list;
	}
	
	/**
	 * Find an item in the list using a caseless search
	 * 
	 * @param name String
	 * @return NameValue
	 */
	public final NameValue findItemCaseless(String name) {
		for ( int i = 0; i < m_list.size(); i++) {
			NameValue nameVal = m_list.get(i);
			if ( nameVal.getName().equalsIgnoreCase(name))
				return nameVal;
		}
		return null;
	}
	
	/**
	 * Remote all items from the list
	 */
	public final void removeAllItems() {
		m_list.removeAllElements();
	}
  
  /**
   * Return the name/value list as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer(256);
    
    str.append("[");
    for ( int i = 0; i < numberOfItems(); i++) {
      if ( str.length() > 1)
        str.append( ",");
      NameValue nameVal = getItemAt( i);
      
      str.append(nameVal.getName());
      str.append( "=");
      str.append( nameVal.getValue());
    }
    str.append( "]");
    
    return str.toString();
  }
}
