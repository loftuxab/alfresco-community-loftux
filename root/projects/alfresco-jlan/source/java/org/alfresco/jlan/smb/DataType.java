package org.alfresco.jlan.smb;

/*
 * DataType.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 *  SMB data type class.
 *
 *  <p>This class contains the data types that are used within an SMB protocol
 *  packet.
 */

public class DataType {

  // SMB data types

  public static final char DataBlock 			= (char) 0x01;
  public static final char Dialect 				= (char) 0x02;
  public static final char Pathname 			= (char) 0x03;
  public static final char ASCII 					= (char) 0x04;
  public static final char VariableBlock 	= (char) 0x05;
}