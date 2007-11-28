package org.alfresco.jlan.ftp;

/*
 * FTPConfigSection.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import java.net.InetAddress;

import org.alfresco.jlan.server.config.ConfigId;
import org.alfresco.jlan.server.config.ConfigSection;
import org.alfresco.jlan.server.config.ConfigurationListener;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.GenericConfigElement;

/**
 * FTP Server Configuration Section Class
 */
public class FTPConfigSection extends ConfigSection {

  // FTP server configuration section name
  
  public static final String SectionName = "FTP";

  //  Bind address and FTP server port. A port of -1 indicates do not start FTP server.
  
  private InetAddress m_ftpBindAddress;
  private int m_ftpPort = -1;
  
  //  Allow anonymous FTP access and anonymous FTP account name
  
  private boolean m_ftpAllowAnonymous;
  private String m_ftpAnonymousAccount;
  
  //  FTP root path, if not specified defaults to listing all shares as the root
  
  private String m_ftpRootPath;
  
  //  FTP data socket range
  
  private int m_ftpDataPortLow;
  private int m_ftpDataPortHigh;
  
  //  FTP authenticaor interface
  
  private FTPAuthenticator m_ftpAuthenticator;
  
  //  FTP server debug flags
  
  private int m_ftpDebug;
  
  // FTP SITE interface
  
  private FTPSiteInterface m_ftpSiteInterface;

  // FTP character set
  
  private String m_ftpCharSet;
  
  /**
   * Class constructor
   * 
   * @param config ServerConfiguration
   */
  public FTPConfigSection(ServerConfiguration config) {
    super( SectionName, config);
    
    //  Set the default FTP authenticator
    
    m_ftpAuthenticator = new LocalAuthenticator();
    
    try {
      m_ftpAuthenticator.initialize( config, new GenericConfigElement( "ftpAuthenticator"));
    }
    catch ( InvalidConfigurationException ex) {
    }
  }
  
  /**
   * Return the FTP server bind address, may be null to indicate bind to all available addresses
   * 
   * @return InetAddress
   */
  public final InetAddress getFTPBindAddress() {
    return m_ftpBindAddress;
  }
  
  /**
   * Return the FTP server port to use for incoming connections
   * 
   * @return int
   */
  public final int getFTPPort() {
    return m_ftpPort;
  }

  /**
   * Return the FTP authenticator interface
   * 
   * @return FTPAuthenticator
   */
  public final FTPAuthenticator getFTPAuthenticator() {
    return m_ftpAuthenticator;
  }
  
  /**
   * Determine if anonymous FTP access is allowed
   * 
   * @return boolean
   */
  public final boolean allowAnonymousFTP() {
    return m_ftpAllowAnonymous;
  }
  
  /**
   * Return the anonymous FTP account name
   * 
   * @return String
   */
  public final String getAnonymousFTPAccount() {
    return m_ftpAnonymousAccount;
  }
  
  /**
   * Return the FTP debug flags
   * 
   * @return int
   */
  public final int getFTPDebug() {
    return m_ftpDebug;
  }
  
  /**
   * Check if an FTP root path has been configured
   * 
   * @return boolean
   */
  public final boolean hasFTPRootPath() {
    return m_ftpRootPath != null ? true : false;
  }

  /**
   * Return the FTP root path
   * 
   * @return String
   */
  public final String getFTPRootPath() {
    return m_ftpRootPath;
  }
  
  /**
   * Determine if a port range is set for FTP data sockets
   * 
   * @return boolean
   */
  public final boolean hasFTPDataPortRange() {
    if ( m_ftpDataPortLow > 0 && m_ftpDataPortHigh > 0)
      return true;
    return false;
  }
  
  /**
   * Return the FTP data socket range low value
   * 
   * @return int
   */
  public final int getFTPDataPortLow() {
    return m_ftpDataPortLow;
  }
  
  /**
   * Return the FTP data socket range high value
   * 
   * @return int
   */
  public final int getFTPDataPortHigh() {
    return m_ftpDataPortHigh;
  }
  
  /**
   * Determine if the FTP SITE interface is enabled
   * 
   * @return boolean
   */
  public final boolean hasFTPSiteInterface() {
    return m_ftpSiteInterface != null ? true : false;
  }
  
  /**
   * Return the FTP SITE interface
   * 
   * @return FTPSiteInterface
   */
  public final FTPSiteInterface getFTPSiteInterface() {
    return m_ftpSiteInterface;
  }
  
  /**
   * Return the FTP character set
   * 
   * @return String
   */
  public final String getFTPCharacterSet() {
    return m_ftpCharSet;
  }
  
  /**
   * Set the FTP character set
   * 
   * @param charSet String
   */
  public final void setFTPCharacterSet( String charSet) {
    m_ftpCharSet = charSet;
  }
  
  /**
   * Set the FTP server bind address, may be null to indicate bind to all available addresses
   * 
   * @param addr InetAddress
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setFTPBindAddress(InetAddress addr)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.FTPBindAddress, addr);
    m_ftpBindAddress = addr;
    
    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the FTP server port to use for incoming connections, -1 indicates disable the FTP server
   * 
   * @param port int
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setFTPPort(int port)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.FTPPort, new Integer(port));
    m_ftpPort = port;
    
    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the FTP server data port range low value
   * 
   * @param port int
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setFTPDataPortLow(int port)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.FTPDataPortLow, new Integer(port));
    m_ftpDataPortLow = port;
    
    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the FTP server data port range high value
   * 
   * @param port int
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setFTPDataPortHigh(int port)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.FTPDataPortHigh, new Integer(port));
    m_ftpDataPortHigh = port;
    
    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the FTP root path
   * 
   * @param path String
   * @return int
   * @throws InvalidConfigurationException
   */
  public final int setFTPRootPath(String path)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.FTPRootPath, path);
    m_ftpRootPath = path;
  
    //  Return the change status
  
    return sts;
  }
    
  /**
   * Enable/disable anonymous FTP access
   * 
   * @param ena boolean
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setAllowAnonymousFTP(boolean ena)
    throws InvalidConfigurationException {

    //  Check if the value has changed
    
    int sts = ConfigurationListener.StsIgnored;
    
    if ( m_ftpAllowAnonymous != ena) {      

      //  Inform listeners, validate the configuration change
  
      sts = fireConfigurationChange(ConfigId.FTPAllowAnon, new Boolean(ena));
      m_ftpAllowAnonymous = ena;
    }
    
    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the anonymous FTP account name
   * 
   * @param acc String
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setAnonymousFTPAccount(String acc)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.FTPAnonAccount, acc);
    m_ftpAnonymousAccount = acc;
    
    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the FTP debug flags
   * 
   * @param dbg int
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setFTPDebug(int dbg)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.FTPDebugFlags, new Integer(dbg));
    m_ftpDebug = dbg;
    
    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the FTP SITE interface to handle custom FTP commands
   * 
   * @param siteInterface FTPSiteInterface
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setFTPSiteInterface(FTPSiteInterface siteInterface)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.FTPSiteInterface, siteInterface);
    m_ftpSiteInterface = siteInterface;
    
    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the authenticator to be used to authenticate FTP users.
   *
   * @param authClass String
   * @param params ConfigElement
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setAuthenticator(String authClass, ConfigElement params)
    throws InvalidConfigurationException {
      
    //  Validate the authenticator class

    int sts = ConfigurationListener.StsIgnored;
    FTPAuthenticator auth = null;
            
    try {

      //  Load the authenticator class

      Object authObj = Class.forName(authClass).newInstance();
      if ( authObj instanceof FTPAuthenticator) {

        //  Set the server authenticator

        auth = (FTPAuthenticator) authObj;
      }
      else
        throw new InvalidConfigurationException("Authenticator is not derived from required base class");
    }
    catch ( ClassNotFoundException ex) {
      throw new InvalidConfigurationException("Authenticator class " + authClass + " not found");
    }
    catch ( Exception ex) {
      throw new InvalidConfigurationException("Authenticator class error");
    }

    //  Initialize the authenticator using the parameter values
        
    auth.initialize( getServerConfiguration(), params);
        
    //  Inform listeners, validate the configuration change
    
    sts = fireConfigurationChange(ConfigId.FTPAuthenticator, auth);

    //  Set the FTP authenticator interface
        
    m_ftpAuthenticator = auth;
      
    //  Return the change status
    
    return sts;
  }
}
