package org.alfresco.jlan.smb.dcerpc.server;

/*
 * DCEPipeHandler.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * DCE Pipe Handler Class
 * 
 * <p>Contains a list of the available DCE pipe handlers.
 */
public class DCEPipeHandler {

	//	DCE/RPC pipe request handlers
	
	private static DCEHandler[] _handlers = { new SrvsvcDCEHandler(),
	  																 				null,				// samr
	  																 				null,				// winreg
	  																 				new WkssvcDCEHandler(),
	  																 				null,				// NETLOGON
	  																 				null,				// lsarpc
	  																 				null,				// spoolss
	  																 				null,				// netdfs
	  																 				null,				// service control
	  																 				null,				// eventlog
	  																 				null				// netlogon1
	};
	
	/**
	 * Return the DCE/RPC request handler for the pipe type
	 * 
	 * @param typ int
	 * @return DCEHandler
	 */
	public final static DCEHandler getHandlerForType(int typ) {
	  if ( typ >= 0 && typ < _handlers.length)
	  	return _handlers[typ];
	  return null;
	}
}
