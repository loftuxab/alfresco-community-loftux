package org.alfresco.jlan.server.filesys;

/*
 * DiskDeviceContext.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.core.DeviceContextException;
import org.alfresco.jlan.server.filesys.quota.QuotaManager;
import org.alfresco.jlan.smb.server.notify.NotifyChangeHandler;
import org.alfresco.jlan.smb.server.notify.NotifyRequest;

/**
 * Disk Device Context Class
 */
public class DiskDeviceContext extends DeviceContext {
	
	//	Change notification handler
	
	private NotifyChangeHandler m_changeHandler;

	//	Volume information
	
	private VolumeInfo m_volumeInfo;

	//	Disk sizing information
	
	private SrvDiskInfo m_diskInfo;

	//	Quota manager
	
	private QuotaManager m_quotaManager;
			
	//	Filesystem attributes, required to enable features such as compression and encryption
	
	private int m_filesysAttribs;
	
	//	Disk device attributes, can be used to make the device appear as a removeable, read-only,
	//	or write-once device for example.

	private int m_deviceAttribs;
	
	/**
	 * Class constructor
	 */
	public DiskDeviceContext() {
		super();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param devName String
	 */
	public DiskDeviceContext(String devName) {
		super(devName);
	}

  /**
   * Class constructor
   * 
   * @param devName String
   * @param shareName String
   */
  public DiskDeviceContext(String devName, String shareName) {
    super(devName, shareName);
  }

	/**
	 * Determine if the volume information is valid
	 * 
	 * @return boolean
	 */
	public final boolean hasVolumeInformation() {
		return m_volumeInfo != null ? true : false;
	}
	
	/**
	 * Return the volume information
	 * 
	 * @return VolumeInfo
	 */
	public final VolumeInfo getVolumeInformation() {
		return m_volumeInfo;
	}
	
	/**
	 * Determine if the disk sizing information is valid
	 * 
	 * @return boolean
	 */
	public final boolean hasDiskInformation() {
		return m_diskInfo != null ? true : false;
	}
	
	/**
	 * Return the disk sizing information
	 * 
	 * @return SMBSrvDiskInfo
	 */
	public final SrvDiskInfo getDiskInformation() {
		return m_diskInfo;
	}
	
	/**
	 * Return the filesystem attributes
	 * 
	 * @return int
	 */
	public final int getFilesystemAttributes() {
	  return m_filesysAttribs;
	}

	/**
	 * Return the device attributes
	 * 
	 * @return int
	 */
	public final int getDeviceAttributes() {
	  return m_deviceAttribs;
	}
	
  /**
   * Return the filesystem type, either FileSystem.TypeFAT or FileSystem.TypeNTFS.
   * 
   * Defaults to FileSystem.FAT but will be overridden if the filesystem driver implements the
   * NTFSStreamsInterface.
   * 
   * @return String
   */
  public String getFilesystemType() {
    return FileSystem.TypeFAT;
  }
  
	/**
	 * Determine if the filesystem is case sensitive or not
	 * 
	 * @return boolean
	 */
	public final boolean isCaseless() {
	  return ( m_filesysAttribs & FileSystem.CasePreservedNames) == 0 ? true : false;
	}
	
	/**
	 * Enable/disable the change notification handler for this device
	 * 
	 * @param ena boolean
	 */
	public final void enableChangeHandler(boolean ena) {
		if ( ena == true)
			m_changeHandler = new NotifyChangeHandler(this);
		else {
			
			//	Shutdown the change handler, if valid
			
			if ( m_changeHandler != null)
				m_changeHandler.shutdownRequest();
			m_changeHandler = null;
		}
	}
	
	/**
	 * Close the disk device context. Release the file state cache resources.
	 */
	public void CloseContext() {
		
		//	Call the base class
		
		super.CloseContext();
		
    // Close the change notification handler
    
    if ( hasChangeHandler())
      enableChangeHandler( false);
	}

	/**
	 * Determine if the disk context has a change notification handler
	 * 
	 * @return boolean
	 */
	public final boolean hasChangeHandler() {
		return m_changeHandler != null ? true : false;
	}
	
	/**
	 * Return the change notification handler
	 * 
	 * @return NotifyChangeHandler
	 */
	public final NotifyChangeHandler getChangeHandler() {
		return m_changeHandler;
	}
	
	/**
	 * Add a request to the change notification list
	 * 
	 * @param req NotifyRequest
	 */
	public final void addNotifyRequest(NotifyRequest req) {
		m_changeHandler.addNotifyRequest(req);
	}
	
	/**
	 * Remove a request from the notify change request list
	 * 
	 * @param req NotifyRequest
	 */
	public final void removeNotifyRequest(NotifyRequest req) {
		m_changeHandler.removeNotifyRequest(req);
	}

	/**
	 * Set the volume information
	 * 
	 * @param vol VolumeInfo
	 */
	public final void setVolumeInformation(VolumeInfo vol) {
		m_volumeInfo = vol;
	}
	
	/**
	 * Set the disk information
	 * 
	 * @param disk SMBSrvDiskInfo
	 */
	public final void setDiskInformation(SrvDiskInfo disk) {
		m_diskInfo = disk;
	}
	
	/**
	 * Check if there is a quota manager configured for this filesystem.
	 * 
	 * @return boolean
	 */
	public final boolean hasQuotaManager() {
		return m_quotaManager != null ? true : false;
	}
	
	/**
	 * Return the quota manager for the filesystem
	 *
	 * @return QuotaManager
	 */
	public final QuotaManager getQuotaManager() {
		return m_quotaManager;
	}
	
	/**
	 * Set the quota manager for this filesystem
	 * 
	 * @param quotaMgr QuotaManager
	 */
	public final void setQuotaManager(QuotaManager quotaMgr) {
		m_quotaManager = quotaMgr;
	}

	/**
	 * Set the filesystem attributes
	 * 
	 * @param attrib int
	 */
	public final void setFilesystemAttributes(int attrib) {
	  m_filesysAttribs = attrib;
	}

	/**
	 * Set the device attributes
	 * 
	 * @param attrib int
	 */
	public final void setDeviceAttributes(int attrib) {
	  m_deviceAttribs = attrib;
	}
	
	/**
	 * Context has been initialized and attached to a shared device, do any startup processing in
	 * this method.
	 * 
	 * @param share DiskSharedDevice
	 * @exception DeviceContextException
	 */
	public void startFilesystem(DiskSharedDevice share)
		throws DeviceContextException {  
	}
}
