package org.alfresco.jlan.oncrpc.portmap;

/*
 * PortMapper.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

/**
 * PortMapper RPC Service Constants Class
 */
public class PortMapper {

  //	Default port mapper port
  
  public static final int DefaultPort			= 111;
  
  //	Program and version id
  
  public static final int ProgramId				= 100000;
  public static final int VersionId				= 2; 
    
  //	RPC procedure ids
  
  public static final int ProcNull				= 0;
  public static final int ProcSet					= 1;
  public static final int ProcUnSet				= 2;
  public static final int ProcGetPort			= 3;
  public static final int ProcDump				= 4;
  
  public static final int ProcMax					= 4;
  
  //	RPC procedure names
  
  private static final String[] _procNames = { "Null", "Set", "UnSet", "GetPort", "Dump" };
  
  /**
   * Return a procedure id as a name
   *
   * @param id int
   * @return String
   */
  public final static String getProcedureName(int id) {
    if ( id < 0 || id > ProcMax)
      return null;
    return _procNames[id];
  }
}
