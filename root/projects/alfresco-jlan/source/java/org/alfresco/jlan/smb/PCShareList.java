package org.alfresco.jlan.smb;

/*
 * PCShareList.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.util.Vector;

/**
 * PC share list class.
 *
 * <p>The PCShareList class contains a list of PCShare objects.
 */
public class PCShareList implements java.io.Serializable {

  private static final long serialVersionUID = 6318830926098970791L;

  //	Vector used to store the PCShare objects

  private Vector<PCShare> m_list;
  
  /**
   * Class constructor
   */
  public PCShareList() {
    m_list = new Vector<PCShare>();
  }
  
  /**
   * Add a PCShare to the list
   * @param shr PCShare object to be added to the list
   */
  public final void addPCShare(PCShare shr) {
    m_list.add(shr);
  }
  
  /**
   * Clear the list of PCShare objects
   */
  public final void clearList() {
    m_list.removeAllElements();
  }
  
  /**
   * Return the required PCShare object from the list.
   *
   * @return PCShare
   * @param idx Index of the PCShare to be returned
   * @exception java.lang.ArrayIndexOutOfBoundsException  If the index is not valid
   */
  public final PCShare getPCShare(int idx)
    throws ArrayIndexOutOfBoundsException {

    //  Bounds check the index

    if (idx >= m_list.size())
      throw new ArrayIndexOutOfBoundsException();

    //  Return the required share information

    return m_list.get(idx);
  }
  
  /**
   * Return the number of PCShare objects that are in this list.
   *
   * @return Number of PCShare objects in the list.
   */
  public final int NumberOfPCShares() {
    return m_list.size();
  }
}