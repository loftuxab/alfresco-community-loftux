package org.alfresco.jlan.util;

import java.util.Vector;

/*
 * StringList.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * String List Class
 */
public class StringList {

  //	List of strings
  
  private Vector<String> m_list;
  
  /**
   * Default constructor
   */
  public StringList() {
    m_list = new Vector<String>();
  }

  /**
   * Class constructor
   * 
   * @param list Vector
   */
  public StringList(Vector list) {
    
    //	Allocate the string list
    
    m_list = new Vector<String>();
    
    //	Copy values to the string list
    
    for ( int i = 0; i < list.size(); i++) {
      Object obj = list.get(i);
      if ( obj instanceof String)
        addString((String) obj);
      else
        addString(obj.toString());
    }
  }
  
  /**
   * Return the number of strings in the list
   *
   * @return int
   */
  public final int numberOfStrings() {
    return m_list.size();
  }
  
  /**
   * Add a string to the list
   *
   * @param str String
   */
  public final void addString(String str) {
    m_list.add(str);
  }
  
  /**
   * Return the string at the specified index
   *
   * @param idx int
   * @return String
   */
  public final String getStringAt(int idx) {
    if ( idx < 0 || idx >= m_list.size())
      return null;
    return m_list.get(idx);
  }

  /**
   * Check if the list contains the specified string
   * 
   * @param str String
   * @return boolean
   */
  public final boolean containsString(String str) {
    return m_list.contains(str);
  }

  /**
   * Return the index of the specified string, or -1 if not in the list
   * 
   * @param str String
   * @return int 
   */
  public final int findString(String str) {
    return m_list.indexOf(str);
  }
  
  /**
   * Remove the specified string from the list
   * 
   * @param str String
   * @return boolean
   */
  public final boolean removeString(String str) {
    return m_list.removeElement(str);
  }
  
  /**
   * Remove the string at the specified index within the list
   * 
   * @param idx int
   * @return String
   */
  public final String removeStringAt(int idx) {
    if ( idx < 0 || idx >= m_list.size())
      return null;
    return m_list.remove(idx);
  }
  
  /**
   * Clear the strings from the list
   */
  public final void remoteAllStrings() {
    m_list.removeAllElements();
  }
  
  /**
   * Return the string list as a string
   * 
   * @return String
   */
  public String toString() {
    
    //	Check if the list is empty
    
    if ( numberOfStrings() == 0)
      return "";
    
    //	Build the string
    
    StringBuffer str = new StringBuffer();
    
    for ( int i = 0; i < m_list.size(); i++) {
      str.append(getStringAt(i));
      str.append(",");
    }
    
    //	Remove the trailing comma
    
    if ( str.length() > 0)
      str.setLength(str.length() - 1);
    
    //	Return the string
    
    return str.toString();
  }
}
