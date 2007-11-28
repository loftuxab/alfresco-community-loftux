package org.alfresco.jlan.smb;

/*
 * SeekType.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Seek file position types.
 * 
 * <p>Defines constants used by the SeekFile SMB request to specify where the seek position is relative to.
 */
public class SeekType {

  //	Seek file types

  public static final int StartOfFile = 0;
  public static final int CurrentPos 	= 1;
  public static final int EndOfFile 	= 2;
}