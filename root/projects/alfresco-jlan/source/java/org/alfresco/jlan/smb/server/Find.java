package org.alfresco.jlan.smb.server;

/*
 * Find.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Find First Flags Class
 */
class Find {

  //	Find first flags

  protected static final int CloseSearch 				= 0x01;
  protected static final int CloseSearchAtEnd 	= 0x02;
  protected static final int ResumeKeysRequired = 0x04;
  protected static final int ContinuePrevious 	= 0x08;
  protected static final int BackupIntent 			= 0x10;
}
