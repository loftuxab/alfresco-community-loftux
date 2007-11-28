package org.alfresco.jlan.server.auth.asn;

/*
 * DERObject.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.io.IOException;

/**
 * DER Object Class
 * 
 * <p>Base class for ASN.1 DER encoded objects.
 */
public abstract class DERObject {

  // Value to indicate that the object is not tagged
  
  public static final int NotTagged = -1;
  
  // Tag number, or -1 if not tagged
  
  private int m_tagNo = NotTagged;
  
  /**
   * Check if the object is tagged
   * 
   * @return boolean
   */
  public final boolean isTagged() {
    return m_tagNo != -1 ? true : false;
  }
  
  /**
   * Return the tag number
   * 
   * @return int
   */
  public final int getTagNo() {
    return m_tagNo;
  }
  
  /**
   * Set the tag number
   * 
   * @param tagNo int
   */
  public final void setTagNo( int tagNo) {
    m_tagNo = tagNo;
  }
  
  /**
   * DER encode the object
   * 
   * @param buf DERBuffer
   */
  public abstract void derEncode( DERBuffer buf)
    throws IOException;
  
  /**
   * DER decode the object
   * 
   * @param buf DERBuffer
   */
  public abstract void derDecode( DERBuffer buf)
    throws IOException;
}
