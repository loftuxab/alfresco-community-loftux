package org.alfresco.jlan.server;

/*
 * SrvSession.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.net.InetAddress;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.auth.AuthContext;
import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.server.core.SharedDeviceList;
import org.alfresco.jlan.server.filesys.TransactionalFilesystemInterface;


/**
 * Server Session Base Class
 * 
 * <p>Base class for server session implementations for different protocols.
 */
public abstract class SrvSession {

	//	Network server this session is associated with
	
	private NetworkServer m_server;
	
  //	Session id/slot number

  private int m_sessId;
  
  //	Unique session id string
  
  private String m_uniqueId;

	//	Process id
	
	private int m_processId = -1;
	
	//	Session/user is logged on/validated
	
	private boolean m_loggedOn;
	
	//  Client details

	private ClientInfo m_clientInfo;

  //  Authentication context, used during the initial session setup phase
  
  private AuthContext m_authContext;
	
  // Debug flags for this session, and debug output interface.

  private int m_debug;
  private String m_dbgPrefix;

	//	List of dynamic/temporary shares created for this session
	
	private SharedDeviceList m_dynamicShares;
	
	//	Session shutdown flag
	
	private boolean m_shutdown;

	//	Protocol type
	
	private String m_protocol;
	
	//	Remote client/host name
	
	private String m_remoteName;
  
  // Transaction object, for filesystems that implement the TransactionalFilesystemInterface
  
  private ThreadLocal<Object> m_tx;
  private TransactionalFilesystemInterface m_txInterface;
  
	/**
	 * Class constructor
	 * 
	 * @param sessId int
	 * @param srv NetworkServer
	 * @param proto String
	 * @param remName String
	 */
	public SrvSession (int sessId, NetworkServer srv, String proto, String remName) {
		m_sessId = sessId;
		m_server = srv;
		
		setProtocolName(proto);
		setRemoteName(remName);
	}

	/**
	 * Output a string to the debug device
	 * 
	 * @param str
	 */
	public final void debugPrint(String str) {
		Debug.print(str);
	}
	
	/**
	 * Output a  string and a newline to the debug device
	 * 
	 * @param str String
	 */
	public final void debugPrintln(String str) {
		Debug.print(m_dbgPrefix);
		Debug.println(str);
	}
	
	/**
	 * Output an exception stack trace to the debug device
	 * 
	 * @param ex Exception
	 */
	public final void debugPrintln(Exception ex) {
		Debug.println(ex);
	}

  /**
   * Check if the session has an authentication context
   * 
   * @return boolean
   */
  public final boolean hasAuthenticationContext() {
    return m_authContext != null ? true : false;
  }
  
  /**
   * Return the authentication context for this sesion
   * 
   * @return AuthContext
   */
  public final AuthContext getAuthenticationContext() {
    return m_authContext;
  }
  
	/**
	 * Add a dynamic share to the list of shares created for this session
	 * 
	 * @param shrDev SharedDevice
	 */
	public final void addDynamicShare(SharedDevice shrDev) {
		
		//	Check if the dynamic share list must be allocated
		
		if ( m_dynamicShares == null)
			m_dynamicShares = new SharedDeviceList();
			
		//	Add the new share to the list
		
		m_dynamicShares.addShare(shrDev);
	}
	
	/**
	 * Determine if the session has any dynamic shares
	 * 
	 * @return boolean
	 */
	public final boolean hasDynamicShares() {
		return m_dynamicShares != null ? true : false;
	}

	/**
	 * Return the list of dynamic shares created for this session
	 * 
	 * @return SharedDeviceList
	 */
	public final SharedDeviceList getDynamicShareList() {
		return m_dynamicShares;
	}

	/**
	 * Return the process id
	 * 
	 * @return int
	 */
	public final int getProcessId() {
		return m_processId;
	}
	
	/**
	 * Return the remote client network address
	 * 
	 * @return InetAddress
	 */
	public abstract InetAddress getRemoteAddress();
			
  /**
   * Return the session id for this session.
   *
   * @return int
   */
  public final int getSessionId() {
    return m_sessId;
  }

	/**
	 * Return the server this session is associated with
	 * 
	 * @return NetworkServer
	 */
	public final NetworkServer getServer () { 
		return m_server;
	}

	/**
	 * Check if the session has valid client information
	 * 
	 * @return boolean
	 */
	public final boolean hasClientInformation() {
		return m_clientInfo != null ? true : false;
	}
	
	/**
	 * Return the client information
	 * 
	 * @return ClientInfo
	 */
	public final ClientInfo getClientInformation() {
		return m_clientInfo;
	}

	/**
	 * Determine if the protocol type has been set
	 * 
	 * @return boolean
	 */		
	public final boolean hasProtocolName() {
		return m_protocol != null ? true : false;
	}

	/**
	 * Return the protocol name
	 * 
	 * @return String
	 */	
	public final String getProtocolName() {
		return m_protocol;
	}
	
	/**
	 * Determine if the remote client name has been set
	 * 
	 * @return boolean
	 */
	public final boolean hasRemoteName() {
		return m_remoteName != null ? true : false;
	}
	
	/**
	 * Return the remote client name
	 * 
	 * @return String
	 */
	public final String getRemoteName() {
		return m_remoteName;
	}
	
	/**
	 * Determine if the session is logged on/validated
	 * 
	 * @return boolean
	 */	
	public final boolean isLoggedOn() {
		return m_loggedOn;
	}
	
	/**
	 * Determine if the session has been shut down
	 * 
	 * @return boolean
	 */
	public final boolean isShutdown() {
		return m_shutdown;
	}
	
	/**
	 * Return the unique session id
	 * 
	 * @return String
	 */
	public final String getUniqueId() {
		return m_uniqueId;
	}

  /**
   * Determine if the specified debug flag is enabled.
   *
   * @param dbgFlag int
   * @return boolean
   */
  public final boolean hasDebug(int dbgFlag) {
    if ((m_debug & dbgFlag) != 0)
      return true;
    return false;
  }

  /**
   * Set the authentication context, used during the initial session setup phase
   * 
   * @param ctx AuthContext
   */
  public final void setAuthenticationContext( AuthContext ctx) {
    m_authContext = ctx;
  }
  
	/**
	 * Set the client information
	 * 
	 * @param client ClientInfo
	 */
	public final void setClientInformation(ClientInfo client) {
		m_clientInfo = client;
	}
	
  /**
   * Set the debug output interface.
   *
   * @param flgs int
   */
  public final void setDebug(int flgs) {
    m_debug = flgs;
  }

	/**
	 * Set the debug output prefix for this session
	 * 
	 * @param prefix String
	 */
	public final void setDebugPrefix(String prefix) {
		m_dbgPrefix = prefix;
	}

	/**
	 * Set the logged on/validated status for the session
	 * 
	 * @param loggedOn boolean
	 */
	public final void setLoggedOn(boolean loggedOn) {
		m_loggedOn = loggedOn;
	}

	/**
	 * Set the process id
	 * 
	 * @param id int
	 */
	public final void setProcessId(int id) {
		m_processId = id;
	}

	/**
	 * Set the protocol name
	 * 
	 * @param name String
	 */	
	public final void setProtocolName(String name) {
		m_protocol = name;
	}
	
	/**
	 * Set the remote client name
	 * 
	 * @param name String
	 */
	public final void setRemoteName(String name) {
		m_remoteName = name;
	}
	
	/**
	 * Set the session id for this session.
	 *
	 * @param id int
	 */
	public final void setSessionId(int id) {
		m_sessId = id;
	}

	/**
	 * Set the unique session id
	 * 
	 * @param unid String
	 */
	public final void setUniqueId(String unid) {
		m_uniqueId = unid;
	}
	 
	/**
	 * Set the shutdown flag
	 * 
	 * @param flg boolean
	 */
	protected final void setShutdown(boolean flg) {
		m_shutdown = flg; 
	}

  /**
   * Close the network session
   */
  public void closeSession() {
  	
  	//	Release any dynamic shares owned by this session
  	
  	if ( hasDynamicShares()) {
  		
  		//	Close the dynamic shares
  		
  		getServer().getShareMapper().deleteShares(this);
  	}
  }

  /**
   * Initialize the thread local transaction object
   */
  public final void initializeTransactionObject() {
    if ( m_tx == null)
      m_tx = new ThreadLocal<Object>();
  }
  
  /**
   * Return the transaction context
   * 
   * @return ThreadLocal<Object>
   */
  public final ThreadLocal<Object> getTransactionObject() {
    return m_tx;
  }
  
  /**
   * Set the active transaction and transaction interface
   * 
   * @param tx ThreadLocal<Object>
   * @param txIface TransactionalFilesystemInterface
   */
  public final void setTransaction( ThreadLocal<Object> tx, TransactionalFilesystemInterface txIface) {
    m_tx = tx;
    m_txInterface = txIface;
  }
  
  /**
   * Set the active transaction interface
   * 
   * @param txIface TransactionalFilesystemInterface
   */
  public final void setTransaction( TransactionalFilesystemInterface txIface) {
    m_txInterface = txIface;
  }
  
  /**
   * Clear the stored transaction
   */
  public final void clearTransaction() {
    m_tx = null;
    m_txInterface = null;
  }
  
  /**
   * End a transaction
   */
  public final void endTransaction() {
    
    // Check if there is an active transaction
    
    if ( m_txInterface != null && m_tx != null) {
      
      // Use the transaction interface to end the transaction
      
      m_txInterface.endTransaction( this, m_tx);
    }
  }
}
