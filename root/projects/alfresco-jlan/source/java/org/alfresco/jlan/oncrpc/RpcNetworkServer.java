package org.alfresco.jlan.oncrpc;

/*
 * RpcNetworkServer.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import java.io.IOException;
import java.net.InetAddress;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.oncrpc.portmap.PortMapper;
import org.alfresco.jlan.server.NetworkServer;
import org.alfresco.jlan.server.config.ServerConfiguration;


/**
 * RPC Network Server Abstract Class
 * 
 * <p>Provides the base class for RPC servers (such as mount and NFS).
 */
public abstract class RpcNetworkServer extends NetworkServer implements RpcProcessor {

	/**
	 * Class constructor
	 *
	 * @param name String
	 * @param config ServerConfiguration
	 */
	public RpcNetworkServer(String name, ServerConfiguration config) {
		super(name, config);
	}
	
	/**
	 * Register a port/protocol for the RPC server
	 * 
	 * @param mapping PortMapping
	 * @throws IOException
	 */
	protected final void registerRPCServer(PortMapping mapping)
		throws IOException {

	  //	Call the main registration method
	  
	  PortMapping[] mappings = new PortMapping[1];
	  mappings[0] = mapping;
	  
	  registerRPCServer(mappings);
	}
	
	/**
	 * Register a set of ports/protocols for the RPC server
	 * 
	 * @param mappings PortMapping[]
	 * @throws IOException
	 */
	protected final void registerRPCServer(PortMapping[] mappings)
		throws IOException {

	  //	Connect to the local portmapper service to register the RPC service
	  
	  InetAddress localHost = InetAddress.getByName("127.0.0.1");
	  
	  TcpRpcClient rpcClient = new TcpRpcClient(localHost, PortMapper.DefaultPort, 512);
	  
	  //	Allocate RPC request and response packets
	  
	  RpcPacket setPortRpc = new RpcPacket(512);
	  RpcPacket rxRpc      = new RpcPacket(512);
	  
	  //	Loop through the port mappings and register each port with the portmapper service
	  
	  for ( int i = 0; i < mappings.length; i++) {
	    
	    //	Build the RPC request header  

	    setPortRpc.buildRequestHeader(PortMapper.ProgramId, PortMapper.VersionId, PortMapper.ProcSet, 0, null, 0, null);
	    
	    //	Pack the request parameters and set the request length

	    setPortRpc.packPortMapping(mappings[i]);
	    setPortRpc.setLength();
	    
	    //	DEBUG
	    
//	    if ( Debug.EnableInfo && hasDebug())
//	      Debug.println("[" + getProtocolName() + "] Register server RPC " + setPortRpc.toString());
	    
	    //	Send the RPC request and receive a response
	    
	    rxRpc = rpcClient.sendRPC(setPortRpc, rxRpc);

	    //	DEBUG
	    
//	    if ( Debug.EnableInfo && hasDebug())
//	      Debug.println("[" + getProtocolName() + "] Register response " + rxRpc.toString());
	  }
	}
	
	/**
	 * Unregister a port/protocol for the RPC server
	 * 
	 * @param mapping PortMapping
	 * @throws IOException
	 */
	protected final void unregisterRPCServer(PortMapping mapping)
		throws IOException {

	  //	Call the main unregister ports method
	  
	  PortMapping[] mappings = new PortMapping[1];
	  mappings[0] = mapping;
	  
	  unregisterRPCServer(mappings);
	}
	
	/**
	 * Unregister a set of ports/protocols for the RPC server
	 * 
	 * @param mappings PortMapping[]
	 * @throws IOException
	 */
	protected final void unregisterRPCServer(PortMapping[] mappings)
		throws IOException {
	  
    //  Connect to the local portmapper service to unregister the RPC service
    
    InetAddress localHost = InetAddress.getByName("127.0.0.1");
    
    TcpRpcClient rpcClient = new TcpRpcClient(localHost, PortMapper.DefaultPort, 512);
    
    //  Allocate RPC request and response packets
    
    RpcPacket setPortRpc = new RpcPacket(512);
    RpcPacket rxRpc      = new RpcPacket(512);
    
    //  Loop through the port mappings and unregister each port with the portmapper service
    
    for ( int i = 0; i < mappings.length; i++) {
      
      //  Build the RPC request header  

      setPortRpc.buildRequestHeader(PortMapper.ProgramId, PortMapper.VersionId, PortMapper.ProcUnSet, 0, null, 0, null);
      
      //  Pack the request parameters and set the request length

      setPortRpc.packPortMapping(mappings[i]);
      setPortRpc.setLength();
      
      //  DEBUG
      
      if ( Debug.EnableInfo && hasDebug())
        Debug.println("[" + getProtocolName() + "] UnRegister server RPC " + setPortRpc.toString());
      
      //  Send the RPC request and receive a response
      
      rxRpc = rpcClient.sendRPC(setPortRpc, rxRpc);

      //  DEBUG
      
      if ( Debug.EnableInfo && hasDebug())
        Debug.println("[" + getProtocolName() + "] UnRegister response " + rxRpc.toString());
    }
	}
	
  /**
   * Start the RPC server
   */
  public abstract void startServer();

  /**
   * Shutdown the RPC server
   * 
   * @param immediate boolean
   */
  public abstract void shutdownServer(boolean immediate);

  /**
   * Process an RPC request
   * 
   * @param rpc RpcPacket
   * @return RpcPacket
   * @throws IOException
   */
  public abstract RpcPacket processRpc(RpcPacket rpc)
  	throws IOException;
}
