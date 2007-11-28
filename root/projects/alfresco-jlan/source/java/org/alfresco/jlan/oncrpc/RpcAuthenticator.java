package org.alfresco.jlan.oncrpc;

/*
 * RpcAuthenticator.java
 *
 * Copyright (c) Starlasoft 2004. All rights reserved.
 */

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;


/**
 * RPC Authenticator Interface
 * 
 * <p>Provides authentication support for ONC/RPC requests.
 */
public interface RpcAuthenticator {

	/**
	 * Initialize the RPC authenticator
	 * 
	 * @param config ServerConfiguration
	 * @param params ConfigElement
	 * @exception InvalidConfigurationException
	 */
	public void initialize(ServerConfiguration config, ConfigElement params)
		throws InvalidConfigurationException;
	
  /**
   * Authenticate an RPC client using the credentials within the RPC request. The object that is returned is
   * used as the key to find the associated session object.
   * 
   * @param authType int
   * @param rpc RpcPacket
   * @return Object
   * @exception RpcAuthenticationException
   */
  public Object authenticateRpcClient(int authType, RpcPacket rpc)
  	throws RpcAuthenticationException;

  /**
   * Get RPC client information from the RPC request.
   * 
   * <p>This method is called when a new session object is created by an RPC server.
   * 
   * @param sessKey Object
   * @param rpc RpcPacket
   * @return ClientInfo
   */
  public ClientInfo getRpcClientInformation(Object sessKey, RpcPacket rpc);
  
  /**
   * Return a list of the authentication types that the RPC authenticator implementation supports. The
   * authentication types are specified in the AuthType class.
   * 
   * @return int[]
   */
  public int[] getRpcAuthenticationTypes();
  
  /**
   * Set the current authenticated user context for processing of the current RPC request
   * 
   * @param sess SrvSession
   * @param client ClientInfo
   */
  public void setCurrentUser( SrvSession sess, ClientInfo client);
}
