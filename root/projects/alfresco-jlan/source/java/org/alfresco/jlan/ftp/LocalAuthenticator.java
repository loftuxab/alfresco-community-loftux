package org.alfresco.jlan.ftp;

/*
 * LocalAuthenticator.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.jlan.server.auth.UserAccount;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.SecurityConfigSection;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.config.ConfigElement;

/**
 * <p>Local Authenticator Class.
 *
 * <p>Authenticate FTP users using the user accounts defined in the configuration or available via the
 * users interface.
 */
public class LocalAuthenticator implements FTPAuthenticator {

  // Server configuration and required sections

  protected ServerConfiguration m_config;
  protected SecurityConfigSection m_securityConfig;
  
  // Debug output enable
  
  private boolean m_debug;
  
  /**
   * Authenticate an FTP user
   * 
   * @param cInfo ClientInfo
   * @param sess FTPSrvSession
   * @return boolean
   */
  public boolean authenticateUser(ClientInfo cInfo, FTPSrvSession sess) {

    //  Check if the user exists in the user list

    UserAccount userAcc = getUserDetails(cInfo.getUserName());
    if (userAcc != null) {

      //  Validate the password

      boolean authSts = false;
      
      if ( cInfo.getPassword() != null) {
        
        //  Check if the user details has the MD4 password
        
        if ( userAcc.hasMD4Password()) {
          
          //  Convert the client password to an MD4 hash
          
          try {
            MessageDigest md4 = MessageDigest.getInstance("MD4");
  
            md4.update( cInfo.getPassword());
            byte[] md4Hash = md4.digest();

            //  Compare the passwords

            byte[] userMd4 = userAcc.getMD4Password();
            
            for ( int i = 0; i < userMd4.length; i++)
              if ( userMd4[i] != md4Hash[i])
                authSts = false;
          }
          catch ( NoSuchAlgorithmException ex) {
          }
        }
        else {
          
          //  Compare the plaintext passwords
          
          byte[] userPwd   = userAcc.getPassword().getBytes();
          byte[] clientPwd = cInfo.getPassword();
          
          if ( userPwd.length == clientPwd.length) {

            //  Compare the passwords
            
            authSts = true;
            
            for ( int i = 0; i < userPwd.length; i++)
              if ( userPwd[i] != clientPwd[i])
                authSts = false;
          }
        }
      }

      //  Return the authentication status
      
      return authSts;
    }

    //  Unknown user

    return false;
  }

  /**
   * Search for the requried user account details
   * 
   * @param user String
   * @return UserAccount
   */
  public final UserAccount getUserDetails(String user) {
    
    // Get the user account details via the users interface
    
    return m_securityConfig.getUsersInterface().getUserAccount( user);
  }
  
  /**
   * Check if debug output is enabled
   * 
   * @return boolean
   */
  public final boolean hasDebug() {
    return m_debug;
  }
  
  /**
   * Initialize the FTP authenticator
   * 
   * @param config ServerConfiguration
   * @param params ConfigElement
   * @throws InvalidConfigurationException
   */
  public void initialize(ServerConfiguration config, ConfigElement params)
    throws InvalidConfigurationException {

    // Save the server configuration

    m_config = config;

    //  Get the security configuration
    
    m_securityConfig = (SecurityConfigSection) m_config.getConfigSection( SecurityConfigSection.SectionName);

    // Check if debug output is enabled
    
    if ( params.getChild( "Debug") != null)
      m_debug = true;
  }
}
