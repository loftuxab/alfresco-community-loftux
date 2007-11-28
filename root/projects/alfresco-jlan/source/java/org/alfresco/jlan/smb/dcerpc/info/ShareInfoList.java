package org.alfresco.jlan.smb.dcerpc.info;

/*
 * ShareInfoList.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.util.Vector;

import org.alfresco.jlan.smb.dcerpc.DCEBuffer;
import org.alfresco.jlan.smb.dcerpc.DCEBufferException;
import org.alfresco.jlan.smb.dcerpc.DCEList;
import org.alfresco.jlan.smb.dcerpc.DCEReadable;

/**
 * Server Share Information List Class
 * 
 * <p>Holds the details for a DCE/RPC share enumeration request or response.
 */
public class ShareInfoList extends DCEList {

	/**
	 * Default constructor
	 */
	public ShareInfoList() {
	  super();
	}

  /**
   * Class constructor
   * 
   * @param buf DCEBuffer
   * @exception DCEBufferException
   */
  public ShareInfoList(DCEBuffer buf)
  	throws DCEBufferException {
    super(buf);
  }
  
	/**
	 * Class constructor
	 * 
	 * @param infoLevel int
	 */
	public ShareInfoList(int infoLevel) {
	  super(infoLevel);
	}

	/**
	 * Return share information object from the list
	 * 
	 * @param idx int
	 * @return ShareInfo
	 */
	public final ShareInfo getShare(int idx) {
	  return (ShareInfo) getElement(idx);
	}
	
  /**
   * Create a new share information object
   * 
   * @return DCEReadable
   */
  protected DCEReadable getNewObject() {
    return new ShareInfo(getInformationLevel());
  }

  /**
   * Add a share to the list
   * 
   * @param share ShareInfo
   */
  public final void addShare(ShareInfo share) {
    
    //	Check if the share list is valid
    
    if ( getList() == null)
      	setList(new Vector());
    
    //	Add the share
    
    getList().addElement(share);
  }
  
	/**
	 * Set the share information list
	 * 
	 * @param list Vector
	 */
	public final void setShareList(Vector list) {
	  setList(list);
	}
}
