package org.alfresco.jlan.smb;

/*
 * FindFirstNext.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Find First/Next Flags
 * 
 * <p>Contains constants used by the Find First/Find Next SMB/CIFS requests.
 */
public class FindFirstNext {

	//	Find first/find next flags

  public static final int CloseSearch 		= 0x01;
  public static final int CloseAtEnd 			= 0x02;
  public static final int ReturnResumeKey = 0x04;
  public static final int ResumePrevious 	= 0x08;
  public static final int BackupIntent 		= 0x10;
}
