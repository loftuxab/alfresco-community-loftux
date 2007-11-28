package org.alfresco.jlan.server.config;

/*
 * SecurityConfigSection.java
 *
 * Copyright (c) 2007 Starlasoft. All rights reserved.
 */

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.jlan.server.auth.DefaultUsersInterface;
import org.alfresco.jlan.server.auth.UserAccountList;
import org.alfresco.jlan.server.auth.UsersInterface;
import org.alfresco.jlan.server.auth.acl.AccessControlList;
import org.alfresco.jlan.server.auth.acl.AccessControlManager;
import org.alfresco.jlan.server.auth.passthru.DomainMapping;
import org.alfresco.jlan.server.core.ShareMapper;
import org.alfresco.jlan.server.filesys.DefaultShareMapper;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.GenericConfigElement;

/**
 * Server Security Configuration Section Class
 */
public class SecurityConfigSection extends ConfigSection {

  // Security configuration section name
  
  public static final String SectionName = "Security";
  
  //  Share mapper
  
  private ShareMapper m_shareMapper;
  private ConfigElement m_mapperParams;
  
  //  Access control manager
  
  private AccessControlManager m_aclManager;
  private ConfigElement m_aclParams;
  
  //  Global access control list, applied to all shares that do not have access controls
  
  private AccessControlList m_globalACLs;
  
  //  User account list

  private UserAccountList m_userList;

  //  Users interface
  
  private UsersInterface m_usersInterface;
  
  //  JCE provider class name
  
  private String m_jceProviderClass;
  
  // Domain mappings, by subnet
  
  private List<DomainMapping> m_domainMappings;
  
  /**
   * Class constructor
   * 
   * @param config ServerConfiguration
   */
  public SecurityConfigSection(ServerConfiguration config) {
    super( SectionName, config);
    
    // Create the default users interface
    
    m_usersInterface = new DefaultUsersInterface();
    
    try {
      m_usersInterface.initializeUsers( config, null);
    }
    catch ( InvalidConfigurationException ex) {
    }
    
    //  Create the default share mapper
    
    m_shareMapper = new DefaultShareMapper();
    
    try {
      m_shareMapper.initializeMapper( config, new GenericConfigElement("shareMapper"));
    }
    catch ( InvalidConfigurationException ex) {
    }
  }
  
  /**
   * Check if there is an access control manager configured
   * 
   * @return boolean
   */
  public final boolean hasAccessControlManager() {
    return m_aclManager != null ? true : false;
  }
  
  /**
   * Get the access control manager that is used to control per share access
   * 
   * @return AccessControlManager
   */
  public final AccessControlManager getAccessControlManager() {
    return m_aclManager;
  }

  /**
   * Check if the global access control list is configured
   * 
   * @return boolean
   */
  public final boolean hasGlobalAccessControls() {
    return m_globalACLs != null ? true : false;
  }
  
  /**
   * Return the global access control list
   * 
   * @return AccessControlList
   */
  public final AccessControlList getGlobalAccessControls() {
    return m_globalACLs;
  }
  
  /**
   * Return the access control manager initialization parameters
   * 
   * @return ConfigElement
   */
  public final ConfigElement getAccessControlManagerParameters() {
    return m_aclParams;
  }
  
  /**
   * Return the share mapper
   * 
   * @return ShareMapper
   */
  public final ShareMapper getShareMapper() {
    return m_shareMapper;
  }
  
  /**
   * Return the share mapper initialization parameters
   * 
   * @return ConfigElement
   */
  public final ConfigElement getShareMapperParameters() {
    return m_mapperParams;
  }
  
  /**
   * Return the user account list.
   *
   * @return UserAccountList
   */
  public final UserAccountList getUserAccounts() {
    return m_userList;
  }

  /**
   * Return the users interface
   * 
   * @return UsersInterface
   */
  public final UsersInterface getUsersInterface() {
    return m_usersInterface;
  }
  
  /**
   * Return the JCE provider class name
   * 
   * @return String
   */
  public final String getJCEProvider() {
    return m_jceProviderClass;
  }
  
  /**
   * Determine if there are any user accounts defined.
   *
   * @return boolean
   */
  public final boolean hasUserAccounts() {
    if (m_userList != null && m_userList.numberOfUsers() > 0)
      return true;
    return false;
  }

  /**
   * Check if there are any domain mappings defined
   * 
   * @return boolean
   */
  public final boolean hasDomainMappings() {
    return m_domainMappings != null ? true : false;
  }
  
  /**
   * Return the domain mappings
   * 
   * @return List<DomainMapping>
   */
  public final List<DomainMapping> getDomainMappings() {
    return m_domainMappings;
  }
  
  /**
   * Set the access control manager to be used to control per share access
   *
   * @param aclMgrClass String
   * @param params ConfigElement
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setAccessControlManager(String aclMgrClass, ConfigElement params)
    throws InvalidConfigurationException {
      
    //  Validate the access control manager class

    int sts = ConfigurationListener.StsIgnored;
    AccessControlManager aclMgr = null;
            
    try {

      //  Load the access control manager class

      Object aclObj = Class.forName(aclMgrClass).newInstance();
      if ( aclObj instanceof AccessControlManager) {

        //  Set the ACL manager

        aclMgr = (AccessControlManager) aclObj;
      }
      else
        throw new InvalidConfigurationException("Access control manager does not implement required interface");
    }
    catch ( ClassNotFoundException ex) {
      throw new InvalidConfigurationException("Access control manager class " + aclMgrClass + " not found");
    }
    catch ( Exception ex) {
      throw new InvalidConfigurationException("Access control manager class error");
    }

    //  Initialize the access control manager using the parameter values
        
    aclMgr.initialize( getServerConfiguration(), params);
        
    //  Inform listeners, validate the configuration change
    
    sts = fireConfigurationChange(ConfigId.SecurityACLManager, aclMgr);

    //  Set the server access control manager and initialization parameters
        
    m_aclManager = aclMgr;
    m_aclParams  = params;
      
    //  Return the change status
    
    return sts;
  }

  /**
   * Set the JCE provider
   * 
   * @param providerClass String
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setJCEProvider(String providerClass)
    throws InvalidConfigurationException {
      
    //  Validate the JCE provider class

    int sts = ConfigurationListener.StsIgnored;
      
    try {

      //  Load the JCE provider class and validate

      Object jceObj = Class.forName( providerClass).newInstance();
      if ( jceObj instanceof java.security.Provider) {

        //  Inform listeners, validate the configuration change

        Provider jceProvider = (Provider) jceObj;
        sts = fireConfigurationChange(ConfigId.SecurityJCEProvider, jceProvider);

        //  Save the JCE provider class name
      
        m_jceProviderClass  = providerClass;
        
        //  Add the JCE provider
        
        Security.addProvider( jceProvider);
      }
      else
        throw new InvalidConfigurationException("JCE provider class is not a valid Provider class");
    }
    catch ( ClassNotFoundException ex) {
      throw new InvalidConfigurationException("JCE provider class " + providerClass + " not found");
    }
    catch ( Exception ex) {
      throw new InvalidConfigurationException("JCE provider class error");
    }

    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the share mapper
   * 
   * @param mapperClass String
   * @param params ConfigElement
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setShareMapper(String mapperClass, ConfigElement params)
    throws InvalidConfigurationException {
      
    //  Validate the share mapper class

    int sts = ConfigurationListener.StsIgnored;
      
    try {

      //  Load the share mapper class

      Object mapperObj = Class.forName(mapperClass).newInstance();
      if ( mapperObj instanceof ShareMapper) {

        //  Initialize the share mapper

        ShareMapper shareMapper = (ShareMapper) mapperObj;
        shareMapper.initializeMapper( getServerConfiguration(), params);

        //  Inform listeners, validate the configuration change

        sts = fireConfigurationChange(ConfigId.ShareMapper, shareMapper);

        //  Set the share mapper and initializtion parameters
      
        m_shareMapper  = shareMapper;
        m_mapperParams = params;
      }
      else
        throw new InvalidConfigurationException("Share mapper class is not implementation of ShareMapper interface");
    }
    catch ( ClassNotFoundException ex) {
      throw new InvalidConfigurationException("Share mapper class " + mapperClass + " not found");
    }
    catch ( Exception ex) {
      throw new InvalidConfigurationException("Share mapper class error");
    }

    //  Return the change status
    
    return sts;
  }
  
  /**
   * Set the user account list.
   *
   * @param users UserAccountList
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setUserAccounts(UserAccountList users)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.UsersList, users);
    m_userList = users;
    
    //  Return the change status
    
    return sts;
  }

  /**
   * Set the users interface to provide user account information
   *
   * @param usersClass String
   * @param params ConfigElement
   * @return int
   * @exception InvalidConfigurationException
   */
  public final int setUsersInterface(String usersClass, ConfigElement params)
    throws InvalidConfigurationException {
      
    //  Validate the users interfaceaccess class

    int sts = ConfigurationListener.StsIgnored;
    UsersInterface usersIface = null;
            
    try {

      //  Load the users interface class

      Object usersObj = Class.forName( usersClass).newInstance();
      if ( usersObj instanceof UsersInterface) {

        //  Set the users interface

        usersIface = (UsersInterface) usersObj;
      }
      else
        throw new InvalidConfigurationException("Users interface class does not implement required interface");
    }
    catch ( ClassNotFoundException ex) {
      throw new InvalidConfigurationException("Users interface class " + usersClass + " not found");
    }
    catch ( Exception ex) {
      throw new InvalidConfigurationException("Users interface class error");
    }

    //  Initialize the users interface using the parameter values
        
    usersIface.initializeUsers( getServerConfiguration(), params);
        
    //  Inform listeners, validate the configuration change
    
    sts = fireConfigurationChange(ConfigId.SecurityUsersInterface, usersIface);

    //  Set the users interface and initialization parameters
        
    m_usersInterface = usersIface;
      
    //  Return the change status
    
    return sts;
  }

  /**
   * Set the global access control list
   * 
   * @param acls AccessControlList
   * @return int
   * @throws InvalidConfigurationException
   */
  public final int setGlobalAccessControls(AccessControlList acls)
    throws InvalidConfigurationException {
      
    //  Inform listeners, validate the configuration change

    int sts = fireConfigurationChange(ConfigId.SecurityGlobalACLs, acls);
    m_globalACLs = acls;
  
    //  Return the change status
  
    return sts;
  }
  
  /**
   * Add a domain mapping
   * 
   * @param mapping DomainMapping
   */
  public final void addDomainMapping(DomainMapping mapping) {
    if ( m_domainMappings == null)
      m_domainMappings = new ArrayList<DomainMapping>();
    m_domainMappings.add( mapping);
  }
}
