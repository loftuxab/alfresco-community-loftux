package org.alfresco.jlan.smb.server;

/*
 * SMBServer.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.ServerListener;
import org.alfresco.jlan.server.SrvSessionList;
import org.alfresco.jlan.server.Version;
import org.alfresco.jlan.server.auth.CifsAuthenticator;
import org.alfresco.jlan.server.config.ConfigId;
import org.alfresco.jlan.server.config.ConfigurationListener;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.core.InvalidDeviceInterfaceException;
import org.alfresco.jlan.server.core.ShareType;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.server.filesys.DiskInterface;
import org.alfresco.jlan.server.filesys.NetworkFileServer;
import org.alfresco.jlan.smb.Dialect;
import org.alfresco.jlan.smb.DialectSelector;
import org.alfresco.jlan.smb.SMBException;
import org.alfresco.jlan.smb.ServerType;
import org.alfresco.jlan.smb.dcerpc.UUID;
import org.alfresco.jlan.smb.mailslot.HostAnnouncer;
import org.alfresco.jlan.smb.server.win32.Win32NetBIOSLanaMonitor;
import org.alfresco.jlan.smb.server.win32.Win32NetBIOSSessionSocketHandler;

/**
 * <p>Creates an SMB server with the specified host name.
 *
 * <p>The server can optionally announce itself so that it will appear under the Network Neighborhood, by enabling
 * the host announcer in the server configuration or using the enableAnnouncer() method.
 */
public class SMBServer extends NetworkFileServer implements Runnable, ConfigurationListener {

	//	Constants
	//
	//	Server version
	
	private static final String ServerVersion = Version.SMBServerVersion;
	
	//   CIFS server custom server events
	
	public static final int CIFSNetBIOSNamesAdded  = ServerListener.ServerCustomEvent;
	
  //  Configuration sections
  
  private CIFSConfigSection m_cifsConfig;
  
	//	Server thread
	
	private Thread m_srvThread;
	
	//	Session socket handlers (NetBIOS over TCP/IP, native SMB and/or Win32 NetBIOS)

	private Vector<SessionSocketHandler> m_sessionHandlers;
	
  //	Host announcers, server will appear under Network Neighborhood

	private Vector<HostAnnouncer> m_hostAnnouncers;

	//	Active session list
	
	private SrvSessionList m_sessions;
	
  //	Server type flags, used when announcing the host

  private int m_srvType = ServerType.WorkStation + ServerType.Server;

  // Server GUID
  
  private UUID m_serverGUID;
  
  /**
   * Create an SMB server using the specified configuration.
   *
   * @param cfg ServerConfiguration
   */
  public SMBServer(ServerConfiguration cfg)
  	throws IOException {

  	super("CIFS", cfg);

    //  Call the common constructor

    CommonConstructor();
  }
  
  /**
   * Add a session handler
   * 
   * @param handler SessionSocketHandler
   */
  public final void addSessionHandler(SessionSocketHandler handler) {
    
    //	Check if the session handler list has been allocated
    
    if ( m_sessionHandlers == null)
      m_sessionHandlers = new Vector<SessionSocketHandler>();
    
    //	Add the session handler
    
    m_sessionHandlers.add(handler);
  }
  
  /**
   * Add a host announcer
   * 
   * @param announcer HostAnnouncer
   */
  public final void addHostAnnouncer( HostAnnouncer announcer) {
    
    //	Check if the host announcer list has been allocated
    
    if ( m_hostAnnouncers == null)
      m_hostAnnouncers = new Vector<HostAnnouncer>();
    
    //	Add the host announcer
    
    m_hostAnnouncers.add(announcer);
  }
  
	/**
	 * Add a new session to the server
	 * 
	 * @param sess SMBSrvSession
	 */
	public final void addSession(SMBSrvSession sess) {

		//	Add the session to the session list
		
		m_sessions.addSession(sess);
		
		//	Propagate the debug settings to the new session
			
    if (Debug.EnableInfo && hasDebug()) {

      //  Enable session debugging, output to the same stream as the server

      sess.setDebug(getCIFSConfiguration().getSessionDebugFlags());
    }
	}
	
  /**
   * Check if the disk share is read-only.
   *
   * @param shr SharedDevice
   */
  protected final void checkReadOnly(SharedDevice shr) {

    //  For disk devices check if the shared device is read-only, this should also check if the shared device
    //  path actually exists.

    if (shr.getType() == ShareType.DISK) {

      //  Check if the disk device is read-only

      try {

        //  Get the device interface for the shared device

        DiskInterface disk = (DiskInterface) shr.getInterface();
        if (disk.isReadOnly(null, shr.getContext())) {

          //  The disk is read-only, mark the share as read-only

          int attr = shr.getAttributes();
          if ((attr & SharedDevice.ReadOnly) == 0)
            attr += SharedDevice.ReadOnly;
          shr.setAttributes(attr);

          //  Debug

          if (Debug.EnableInfo && hasDebug())
            Debug.println("[SMB] Add Share " + shr.toString() + " : isReadOnly");
        }
      }
      catch (InvalidDeviceInterfaceException ex) {

        //  Shared device interface error

        if (Debug.EnableInfo && hasDebug())
          Debug.println("[SMB] Add Share " + shr.toString() + " : " + ex.toString());
      }
      catch (FileNotFoundException ex) {

        //  Shared disk device local path does not exist

        if (Debug.EnableInfo && hasDebug())
          Debug.println("[SMB] Add Share " + shr.toString() + " : " + ex.toString());
      }
      catch (IOException ex) {

        //  Shared disk device access error

        if (Debug.EnableInfo && hasDebug())
          Debug.println("[SMB] Add Share " + shr.toString() + " : " + ex.toString());
      }
    }
  }

  /**
   * Common constructor code.
   */
  private void CommonConstructor() throws IOException {

    //  Get the CIFS server configuration
    
    m_cifsConfig = (CIFSConfigSection) getConfiguration().getConfigSection( CIFSConfigSection.SectionName);
    
    if ( m_cifsConfig != null) {

  		//	Add the SMB server as a configuration change listener of the server configuration
  		
  		getConfiguration().addListener(this);
  
  		//	Check if debug output is enabled
  		
  		if ( getCIFSConfiguration().getSessionDebugFlags() != 0)
  			setDebug(true);
  			
  		//	Set the server version
  		
  		setVersion(ServerVersion);
  
  		//	Create the session socket handler list
  		
  		m_sessionHandlers = new Vector<SessionSocketHandler>();
  		
  		//	Create the active session list
  		
  		m_sessions = new SrvSessionList();
    }
    else
      setEnabled( false);
  }

	/**
	 * Close the device contexts
	 */
	protected final void closeDevices() {

		//	Enumerate the shares and close each device to release resources
		
		Enumeration enm = getFullShareList(getServerName(), null).enumerateShares();
		
		while ( enm.hasMoreElements()) {
			
			//	Get the current shared device
			
			SharedDevice share = (SharedDevice) enm.nextElement();
			DeviceContext context = share.getContext();
			
			if ( context != null) {
				
				//	Close the current device
				
				context.CloseContext();
				
				//	Debug
		
				if ( Debug.EnableInfo && hasDebug())		
					Debug.println("[SMB] Closed device " + share.toString());
			}
		}
		
		//	Close the share mapper
		
// TODO:		getConfiguration().getShareMapper().closeMapper();
	}
	
	/**
	 * Close the host announcer, if enabled
	 */
	protected void closeHostAnnouncers() {

    //  Check if there are active host announcers

    if ( m_hostAnnouncers != null) {

      //	Shutdown the host announcers
      
      for ( int i = 0;i < m_hostAnnouncers.size(); i++) {
        
        //	Get the current host announcer from the active list
        
        HostAnnouncer announcer = (HostAnnouncer) m_hostAnnouncers.elementAt(i);
        
        //	Shutdown the host announcer
        
        announcer.shutdownAnnouncer();
      }
    }
	}
	
	/**
	 * Close the session handlers
	 */
	protected void closeSessionHandlers() {

	  //	Close the session handlers
	  
	  for ( int i = 0; i < m_sessionHandlers.size(); i++) {
	    
	    //	Get the current session handler and request the handler to shutdown
	    
	    SessionSocketHandler handler = (SessionSocketHandler) m_sessionHandlers.elementAt(i);
	    handler.shutdownRequest();
	  }
	  
	  //	Clear the session handler list
	  
	  m_sessionHandlers.removeAllElements();
	}
	
	/**
	 * Delete temporary shares created by the share mapper for the specified session
	 * 
	 * @param sess SMBSrvSession
	 */
	public final void deleteTemporaryShares(SMBSrvSession sess) {
		
		//	Delete temporary shares via the share mapper
		
		getShareMapper().deleteShares(sess);
	}
	
  /**
   * Return the CIFS server configuration
   * 
   * @return CIFSConfigSection
   */
  public final CIFSConfigSection getCIFSConfiguration() {
    return m_cifsConfig;
  }
  
  /**
   * Return the server comment.
   *
   * @return java.lang.String
   */
  public final String getComment() {
    return getCIFSConfiguration().getComment();
  }

  /**
   * Return the CIFS server name
   * 
   * @return String
   */
  public final String getServerName() {
    return getCIFSConfiguration().getServerName();
  }
  
  /**
   * Return the server type flags.
   *
   * @return int
   */
  public final int getServerType() {
    return m_srvType;
  }

  /**
   * Return the per session debug flag settings.
   */
  public final int getSessionDebug() {
    return getCIFSConfiguration().getSessionDebugFlags();
  }

  /**
   * Return the list of SMB dialects that this server supports.
   *
   * @return DialectSelector
   */
  public final DialectSelector getSMBDialects() {
    return getCIFSConfiguration().getEnabledDialects();
  }

  /**
   * Return the CIFS authenticator
   * 
   * @return CifsAuthenticator
   */
  public final CifsAuthenticator getCifsAuthenticator() {
    return getCIFSConfiguration().getAuthenticator();
  }
  
  /**
   * Return the active session list
   * 
   * @return SrvSessionList
   */
  public final SrvSessionList getSessions() {
    return m_sessions;
  }
  
  /**
   * Start the SMB server.
   */
  public void run() {

    //  Fire a server startup event
    
    fireServerEvent(ServerListener.ServerStartup);
    
    //  Indicate that the server is active

    setActive(true);

    //	Check if we are running under Windows
    
    boolean isWindows = isWindowsNTOnwards();

    // Generate a GUID for the server based on the server name
    
    Random r = new Random();
    m_serverGUID = new UUID( r.nextLong(), r.nextLong());
    
    //  Debug

    if (Debug.EnableInfo && hasDebug()) {
    	
    	//	Dump the server name/version and Java runtime details
    	
      Debug.println("[SMB] CIFS Server " + getServerName() + " starting");
      Debug.println("[SMB] Version " + isVersion());
      Debug.println("[SMB] Java VM " + System.getProperty("java.vm.version"));
      Debug.println("[SMB] OS " + System.getProperty("os.name") + ", version " + System.getProperty("os.version"));
      
      //	Check for server alias names
      
      if ( getCIFSConfiguration().hasAliasNames())
      	Debug.println("[SMB] Server alias(es) : " + getCIFSConfiguration().getAliasNames());
      
      //	Output the authenticator details
      
      if (getCifsAuthenticator() != null)
        Debug.println("[SMB] Using authenticator " + getCifsAuthenticator().getClass().getName() + ", mode=" + 
                    (getCifsAuthenticator().getAccessMode() == CifsAuthenticator.SHARE_MODE ? "SHARE" : "USER"));

			//	Display the timezone offset/name
			
			if ( getGlobalConfiguration().getTimeZone() != null)
				Debug.println("[SMB] Server timezone " + getGlobalConfiguration().getTimeZone() + ", offset from UTC = " + getGlobalConfiguration().getTimeZoneOffset() / 60 + "hrs");
			else
				Debug.println("[SMB] Server timezone offset = " + getGlobalConfiguration().getTimeZoneOffset() / 60 + "hrs");
			
      //  Dump the available dialect list

      Debug.println("[SMB] Dialects enabled = " + getSMBDialects());
      
      //	Dump the share list

			Debug.println("[SMB] Shares:");
			Enumeration<SharedDevice> enm = getFullShareList(getCIFSConfiguration().getServerName(), null).enumerateShares();
			
			while ( enm.hasMoreElements()) {
				SharedDevice share = enm.nextElement();	
				Debug.println("[SMB]  " + share.toString() + " " + (share.getContext() != null ? share.getContext().toString() : ""));
			}
    }

    //  Create a server socket to listen for incoming session requests

    try {

      //  Add the IPC$ named pipe shared device

      AdminSharedDevice admShare = new AdminSharedDevice();
      getFilesystemConfiguration().addShare(admShare);

      //  Clear the server shutdown flag

      setShutdown( false);

      //	Get the list of IP addresses the server is bound to

      getServerIPAddresses();

			//	Check if the NT SMB dialect is enabled, if so then update the server flags to indicate that
			//	this is an NT server
			
			if ( getCIFSConfiguration().getEnabledDialects().hasDialect(Dialect.NT) == true) {
				
				//	Enable the NT server flag
				
				getCIFSConfiguration().setServerType(getServerType() + ServerType.NTServer);
				
				//	Debug
				
				if (Debug.EnableInfo && hasDebug())
					Debug.println("[SMB] Added NTServer flag to host announcement");
			}

			//	Check if the socket connection debug flag is enabled
			
			boolean sockDbg = false;
			
			if (( getSessionDebug() & SMBSrvSession.DBG_SOCKET) != 0)
				sockDbg = true;
				
			//	Create the NetBIOS session socket handler, if enabled
			
			if ( getCIFSConfiguration().hasNetBIOSSMB()) {
				
			  //	Create the TCP/IP NetBIOS SMB/CIFS session handler(s), and host announcer(s) if enabled
			  
			  NetBIOSSessionSocketHandler.createSessionHandlers( this, sockDbg);
			}

			//	Create the TCP/IP SMB session socket handler, if enabled
			
			if ( getCIFSConfiguration().hasTcpipSMB()) {
				
			  //	Create the TCP/IP native SMB session handler(s)
			  
			  TcpipSMBSessionSocketHandler.createSessionHandlers( this, sockDbg);
			}
						
		  //	Create the Win32 NetBIOS session handler, if enabled

			if ( getCIFSConfiguration().hasWin32NetBIOS()) {
			
			  //	Only enable if running under Windows
			  
			  if ( isWindows == true) {
			    
					//	Create the Win32 NetBIOS SMB handler(s), and host announcer(s) if enabled
					
			    Win32NetBIOSSessionSocketHandler.createSessionHandlers(this, sockDbg);
			  }
			}

			//	Check if there are any session handlers installed, if not then close the server
			
			if ( m_sessionHandlers.size() > 0 || getCIFSConfiguration().hasWin32NetBIOS()) {
			
        //  Fire a server active event
        
        fireServerEvent(ServerListener.ServerActive);
        
	      //  Wait for incoming connection requests
	
	      while (hasShutdown() == false) {
	
					//	Sleep for a while
					
					try {
						Thread.sleep(1000L);
					}
					catch (InterruptedException ex) {
					}
	      }
      }
			else if ( Debug.EnableError && hasDebug()) {
			  
			  //	DEBUG
			  
			  Debug.println("[SMB] No valid session handlers, server closing");
			}
    }
    catch (SMBException ex) {
    	
    	//	Output the exception
    	
      Debug.println(ex);
      	
			//	Store the error, fire a server error event
      	
			setException(ex);
			fireServerEvent(ServerListener.ServerError);
    }
    catch (Exception ex) {

      //	Do not report an error if the server has shutdown, closing the server socket
      //	causes an exception to be thrown.

			if ( hasShutdown() == false) {
        Debug.println("[SMB] Server error : " + ex.toString());
        Debug.println(ex);
      	
				//	Store the error, fire a server error event
      	
				setException(ex);
				fireServerEvent(ServerListener.ServerError);
			}
    }

    //  Debug

    if (Debug.EnableInfo && hasDebug())
      Debug.println("[SMB] SMB Server shutting down ...");

		//	Close the shared devices, host announcer and session handlers
		
		closeDevices();
		closeHostAnnouncers();
		closeSessionHandlers();
		
		//	Shutdown the Win32 NetBIOS LANA monitor, if enabled
		
		if ( isWindows && Win32NetBIOSLanaMonitor.getLanaMonitor() != null) {
    
      // Shutdown the LANA monitor

		  Win32NetBIOSLanaMonitor.getLanaMonitor().shutdownRequest();
    }
    
    //  Indicate that the server is not active

    setActive(false);
    fireServerEvent(ServerListener.ServerShutdown);
  }

  /**
   * Notify the server that a session has been closed.
   *
   * @param sess SMBSrvSession
   */
  protected final void sessionClosed(SMBSrvSession sess) {

		//	Remove the session from the active session list
		
		m_sessions.removeSession(sess);
		
    //	Notify session listeners that a session has been closed

    fireSessionClosedEvent(sess);
  }

  /**
   * Notify the server that a user has logged on.
   *
   * @param sess SMBSrvSession
   */
  protected final void sessionLoggedOn(SMBSrvSession sess) {

    //	Notify session listeners that a user has logged on.

    fireSessionLoggedOnEvent(sess);
  }

  /**
   * Notify the server that a session has been closed.
   *
   * @param sess SMBSrvSession
   */
  protected final void sessionOpened(SMBSrvSession sess) {

    //	Notify session listeners that a session has been closed

    fireSessionOpenEvent(sess);
  }

  /**
   * Shutdown the SMB server
   * 
   * @param immediate boolean
   */
  public final void shutdownServer(boolean immediate) {

    //	Indicate that the server is closing

    setShutdown( true);

    try {

      //	Close the session handlers

      closeSessionHandlers();
    }
    catch (Exception ex) {
    }
    
    //	Close the active sessions
    
    Enumeration enm = m_sessions.enumerate();
    
    while(enm.hasMoreElements()) {
      
      //	Get the session id and associated session
      
      Integer sessId = (Integer) enm.nextElement();
      SMBSrvSession sess = (SMBSrvSession) m_sessions.findSession(sessId);

			//	Inform listeners that the session has been closed
			
			fireSessionClosedEvent(sess);

			//	Close the session

			sess.closeSession();			
    }
    
    //	Wait for the main server thread to close
    
    if ( m_srvThread != null) {
    	
    	try {
				m_srvThread.join(3000);
    	}
    	catch (Exception ex) {
    	}
    }
    
    //	Fire a shutdown notification event
    
    fireServerEvent(ServerListener.ServerShutdown);
  }

	/**
	 * Start the SMB server in a seperate thread
	 */
	public void startServer() {
		
		//	Create a seperate thread to run the SMB server
		
		m_srvThread = new Thread(this);
		m_srvThread.setName("CIFS Server");
		
		m_srvThread.start();
	}
	
	/**
	 *	Validate configuration changes that are relevant to the SMB server
	 *
	 * @param id int
	 * @param config ServerConfiguration
	 * @param newVal Object
	 * @return int
	 * @throws InvalidConfigurationException 
	 */
	public int configurationChanged(int id, ServerConfiguration config, Object newVal)
		throws InvalidConfigurationException {

		int sts = ConfigurationListener.StsIgnored;
		
		try {

			//	Check if the configuration change affects the SMB server
			
			switch ( id) {
				
				//	Server enable/disable
				
				case ConfigId.ServerSMBEnable:
				
					//	Check if the server is active

					Boolean enaSMB = (Boolean) newVal;
										
					if ( isActive() && enaSMB.booleanValue() == false) {
						
						//	Shutdown the server
						
						shutdownServer(false);
					}
					else if ( isActive() == false && enaSMB.booleanValue() == true) {
						
						//	Start the server
						
						startServer();
					}
					
					//	Indicate that the setting was accepted
					
					sts = ConfigurationListener.StsAccepted;
					break;
					
				//	SMB server type flags
				
				case ConfigId.SMBServerType:
				
					//	Check if there are active host announcers
					
					if ( m_hostAnnouncers != null) {
						
						//	Get the new server type flags value and update the host announcer
						
						Integer ival = (Integer) newVal;
						
						for ( int i = 0; i < m_hostAnnouncers.size(); i++) {
						  HostAnnouncer announcer = (HostAnnouncer) m_hostAnnouncers.elementAt(i);
						  announcer.setServerType(ival.intValue());
						}
					}
					
					//	Update the status
					
					sts = ConfigurationListener.StsAccepted;
					break;
					
				//	Changes that can be accepted without restart
				
				case ConfigId.SMBComment:
				case ConfigId.SMBDialects:
				case ConfigId.SMBTCPPort:
				case ConfigId.SMBMacExtEnable:
				case ConfigId.SMBDebugEnable:
				case ConfigId.ServerTimezone:
				case ConfigId.ServerTZOffset:
				case ConfigId.ShareList:
				case ConfigId.ShareMapper:
				case ConfigId.SecurityAuthenticator:
				case ConfigId.UsersList:
				case ConfigId.DebugDevice:
					sts = ConfigurationListener.StsAccepted;
					break;

				//	Changes that affect new sessions only
				
				case ConfigId.SMBSessionDebug:
					sts = ConfigurationListener.StsNewSessionsOnly;
					break;
									
				//	Changes that require a restart
				 		
				case ConfigId.SMBHostName:
				case ConfigId.SMBAliasNames:
				case ConfigId.SMBDomain:
				case ConfigId.SMBBroadcastMask:
				case ConfigId.SMBAnnceEnable:
				case ConfigId.SMBAnnceInterval:
				case ConfigId.SMBAnnceDebug:
				case ConfigId.SMBTCPEnable:
				case ConfigId.SMBBindAddress:
					sts = ConfigurationListener.StsRestartRequired;
					break;
			}
		}
		catch (Exception ex) {
			throw new InvalidConfigurationException("SMB Server configuration error", ex);
		}

		//	Return the status
		
		return sts;
	}
	
	/**
	 * Determine if we are running under Windows NT onwards
	 * 
	 * @return boolean
	 */
	private final boolean isWindowsNTOnwards() {
	  
	  //	Get the operating system name property
	  
	  String osName = System.getProperty("os.name");
	  
	  if ( osName.startsWith("Windows")) {
	    if ( osName.endsWith("95") || osName.endsWith("98") || osName.endsWith("ME")) {

	      //	Windows 95-ME
	      
	      return false;
	    }
	    
	    //	Looks like Windows NT onwards
	    
	    return true;
	  }
	  
	  //	Not Windows
	  
	  return false;
	}
	
	/**
	 * Get the list of local IP addresses
	 *
	 */
	private final void getServerIPAddresses() {

	  try {

	    //	Get the local IP address list
	  
		  Enumeration<NetworkInterface> enm = NetworkInterface.getNetworkInterfaces();
		  Vector<InetAddress> addrList = new Vector<InetAddress>();
		  
		  while ( enm.hasMoreElements()) {
		    
		    //	Get the current network interface
		    
		    NetworkInterface ni = enm.nextElement();
		    
		    //	Get the address list for the current interface
		    
		    Enumeration<InetAddress> addrs = ni.getInetAddresses();
		    
		    while ( addrs.hasMoreElements())
		      addrList.add( addrs.nextElement());
		  }
		  
		  //	Convert the vector of addresses to an array
		  
		  if ( addrList.size() > 0) {
		    
		    //	Convert the address vector to an array
		    
		    InetAddress[] inetAddrs = new InetAddress[addrList.size()];
		    
		    //	Copy the address details to the array
		    
		    for ( int i = 0; i < addrList.size(); i++)
		      inetAddrs[ i] = addrList.elementAt( i);
		    
		    //	Set the server IP address list
		    
		    setServerAddresses( inetAddrs);
		  }
	  }
	  catch ( Exception ex) {
	    
	    //	DEBUG
	    
	    if ( Debug.EnableError && hasDebug())
	      Debug.println("[SMB] Error getting local IP addresses, " + ex.toString());
	  }
  }
  
  /**
   * Return the server GUID
   * 
   * @return UUID
   */
  public final UUID getServerGUID() {
    return m_serverGUID;
  }
  
  /**
   * Send a NetBIOS names added event to server listeners
   * 
   * @param lana int
   */
  public final void fireNetBIOSNamesAddedEvent(int lana) {

    // Send the event to registered listeners, encode the LANA id in the top of the event id
    
    fireServerEvent( CIFSNetBIOSNamesAdded + ( lana << 16));
  }
}