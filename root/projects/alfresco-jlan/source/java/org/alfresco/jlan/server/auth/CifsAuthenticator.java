package org.alfresco.jlan.server.auth;

/*
 * AuthContext.java
 *
 * Copyright (c) 2006 Starlasoft. All rights reserved.
 */

import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.auth.passthru.DomainMapping;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.config.SecurityConfigSection;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.alfresco.jlan.server.core.SharedDevice;
import org.alfresco.jlan.smb.Capability;
import org.alfresco.jlan.smb.Dialect;
import org.alfresco.jlan.smb.DialectSelector;
import org.alfresco.jlan.smb.SMBStatus;
import org.alfresco.jlan.smb.server.CIFSConfigSection;
import org.alfresco.jlan.smb.server.SMBSrvException;
import org.alfresco.jlan.smb.server.SMBSrvPacket;
import org.alfresco.jlan.smb.server.SMBSrvSession;
import org.alfresco.jlan.smb.server.SecurityMode;
import org.alfresco.jlan.smb.server.VirtualCircuit;
import org.alfresco.jlan.util.DataPacker;
import org.alfresco.jlan.util.HexDump;
import org.alfresco.jlan.util.IPAddress;
import org.alfresco.config.ConfigElement;

/**
 * CIFS Authenticator Class
 * 
 * <p>
 * An authenticator is used by the CIFS server to authenticate users when in user level access mode
 * and authenticate requests to connect to a share when in share level access.
 */
public abstract class CifsAuthenticator {

  //  Server access mode

  public static final int SHARE_MODE  = 0;
  public static final int USER_MODE   = 1;
  
  // Encryption algorithm types

  public static final int LANMAN  = PasswordEncryptor.LANMAN;
  public static final int NTLM1   = PasswordEncryptor.NTLM1;
  public static final int NTLM2   = PasswordEncryptor.NTLM2;

  // Authentication status values

  public static final int AUTH_ALLOW        = 0;
  public static final int AUTH_GUEST        = 0x10000000;
  public static final int AUTH_DISALLOW     = -1;
  public static final int AUTH_BADPASSWORD  = -2;
  public static final int AUTH_BADUSER      = -3;
  public static final int AUTH_PASSEXPIRED  = -4;
  public static final int AUTH_ACCDISABLED  = -5;

  // Share access permissions, returned by authenticateShareConnect()

  public static final int NoAccess    = 0;
  public static final int ReadOnly    = 1;
  public static final int Writeable   = 2;

  // Standard encrypted password and challenge length

  public static final int STANDARD_PASSWORD_LEN   = 24;
  public static final int STANDARD_CHALLENGE_LEN  = 8;

  // Default guest user name

  protected static final String GUEST_USERNAME = "guest";

  // Default SMB dialects to enable

  private DialectSelector m_dialects;

  // Security mode flags

  private int m_securityMode = SecurityMode.UserMode + SecurityMode.EncryptedPasswords;

  // Password encryption algorithms

  private PasswordEncryptor m_encryptor = new PasswordEncryptor();

  //  Server access mode

  private int m_accessMode = SHARE_MODE;
  
  // Enable extended security mode
  
  private boolean m_extendedSecurity;
  
  // Flag to enable/disable the guest account, and control mapping of unknown users to the guest
  // account

  private boolean m_allowGuest;
  private boolean m_mapToGuest;

  // Default guest user name

  private String m_guestUserName = GUEST_USERNAME;

  // Random number generator used to generate challenge keys

  protected Random m_random = new Random(System.currentTimeMillis());

  // Server configuration and required sections

  protected ServerConfiguration m_config;
  protected SecurityConfigSection m_securityConfig;
  protected CIFSConfigSection m_cifsConfig;
  
  // Debug output enable
  
  private boolean m_debug;

  /**
   * Authenticate a connection to a share.
   * 
   * @param client User/client details from the tree connect request.
   * @param share Shared device the client wants to connect to.
   * @param sharePwd Share password.
   * @param sess Server session.
   * @return int Granted file permission level or disallow status if negative. See the
   *         FilePermission class.
   */
  public int authenticateShareConnect(ClientInfo client, SharedDevice share, String sharePwd, SrvSession sess) {

    // Allow write access
    //
    // Main authentication is handled by authenticateUser()

    return CifsAuthenticator.Writeable;
  }

  /**
   * Authenticate a user. A user may be granted full access, guest access or no access.
   * 
   * @param client User/client details from the session setup request.
   * @param sess Server session
   * @param alg Encryption algorithm
   * @return int Access level or disallow status.
   */
  public int authenticateUser(ClientInfo client, SrvSession sess, int alg) {

    //  Check if the user exists in the user list

    UserAccount userAcc = getUserDetails(client.getUserName());
    if (userAcc != null) {

      //  Validate the password

      boolean authSts = false;
      
      if ( client.getPassword() != null) {
        
        //  Validate using the Unicode password
        
        authSts = validatePassword( userAcc, client, sess.getAuthenticationContext(), alg);
      }
      else if ( client.hasANSIPassword()) {
        
        //  Validate using the ANSI password with the LanMan encryption
        
        authSts = validatePassword( userAcc, client, sess.getAuthenticationContext(), LANMAN);
      }

      //  Return the authentication status
      
      return authSts == true ? AUTH_ALLOW : AUTH_BADPASSWORD;
    }

    //  Check if this is an SMB/CIFS null session logon.
    //
    //  The null session will only be allowed to connect to the IPC$ named pipe share.
    
    if ( client.isNullSession() && sess instanceof SMBSrvSession)
      return AUTH_ALLOW;
    
    //  Unknown user

    return allowGuest() ? AUTH_GUEST : AUTH_DISALLOW;
  }

  /**
   * Authenticate a user using a plain text password.
   * 
   * @param client   User/client details from the session setup request.
   * @param sess     Server session
   * @return int     Access level or disallow status.
   * @throws InvalidConfigurationException
   */
  public final int authenticateUserPlainText(ClientInfo client, SrvSession sess) {
    
    //  Get a challenge key
    
    NTLanManAuthContext authCtx = (NTLanManAuthContext) sess.getAuthenticationContext();
    if ( authCtx == null) {
      authCtx = new NTLanManAuthContext();
      sess.setAuthenticationContext( authCtx);
    }
    
    //  Get the plain text password
    
    String textPwd = client.getPasswordAsString();
    if ( textPwd == null)
      textPwd = client.getANSIPasswordAsString();

    //  Encrypt the password
          
    byte[] encPwd = generateEncryptedPassword(textPwd, authCtx.getChallenge(), NTLM1, client.getUserName(), client.getDomain());
    client.setPassword(encPwd);
    
    //  Authenticate the user
    
    return authenticateUser(client, sess, NTLM1);  
  }
  
  
  /**
   * Initialize the authenticator
   * 
   * @param config ServerConfiguration
   * @param params ConfigElement
   * @exception InvalidConfigurationException
   */
  public void initialize(ServerConfiguration config, ConfigElement params) throws InvalidConfigurationException {

    // Save the server configuration so we can access the authentication component

    m_config = config;

    // Allocate the SMB dialect selector, and initialize using the default list of dialects

    m_dialects = new DialectSelector();

    m_dialects.AddDialect(Dialect.DOSLanMan1);
    m_dialects.AddDialect(Dialect.DOSLanMan2);
    m_dialects.AddDialect(Dialect.LanMan1);
    m_dialects.AddDialect(Dialect.LanMan2);
    m_dialects.AddDialect(Dialect.LanMan2_1);
    m_dialects.AddDialect(Dialect.NT);

    //  Get the required configuration sections
    
    m_securityConfig = (SecurityConfigSection) m_config.getConfigSection( SecurityConfigSection.SectionName);
    m_cifsConfig     = (CIFSConfigSection) m_config.getConfigSection( CIFSConfigSection.SectionName);

    // Check if debug output is enabled
    
    if ( params.getChild( "Debug") != null)
      m_debug = true;
  }

  /**
   * Encrypt the plain text password with the specified encryption key using the specified
   * encryption algorithm.
   * 
   * @param plainPwd String
   * @param encryptKey byte[]
   * @param alg int
   * @param userName String
   * @param domain String
   * @return byte[]
   */
  protected final byte[] generateEncryptedPassword(String plainPwd, byte[] encryptKey, int alg, String userName, String domain) {

    // Use the password encryptor

    byte[] encPwd = null;

    try {
      // Encrypt the password

      encPwd = m_encryptor.generateEncryptedPassword(plainPwd, encryptKey, alg, userName, domain);
    }
    catch (NoSuchAlgorithmException ex) {
    }
    catch (InvalidKeyException ex) {
    }

    // Return the encrypted password

    return encPwd;
  }

  /**
   * Return the access mode of the server, either SHARE_MODE or USER_MODE.
   *
   * @return int
   */
  public final int getAccessMode() {
    return m_accessMode;
  }

  /**
   * Determine if extended security methods are available
   * 
   * @return boolean
   */
  public final boolean hasExtendedSecurity() {
    return m_extendedSecurity;
  }
  
  /**
   * Return an authentication context for the new session
   * 
   * @return AuthContext
   */
  public AuthContext getAuthContext(SMBSrvSession sess) {

    AuthContext authCtx = null;

    if (sess.hasAuthenticationContext() && sess.getAuthenticationContext() instanceof NTLanManAuthContext) {

      // Use the existing authentication context

      authCtx = sess.getAuthenticationContext();
    }
    else {
      
      // Create a new authentication context for the session

      authCtx = new NTLanManAuthContext();
      sess.setAuthenticationContext(authCtx);
    }

    // Return the authentication context

    return authCtx;
  }

  /**
   * Return the enabled SMB dialects that the server will use when negotiating sessions.
   * 
   * @return DialectSelector
   */
  public final DialectSelector getEnabledDialects() {
    return m_dialects;
  }

  /**
   * Return the security mode flags
   * 
   * @return int
   */
  public final int getSecurityMode() {
    return m_securityMode;
  }

  /**
   * Return the CIFS configuration section
   * 
   * @return CIFSConfigSection
   */
  public final CIFSConfigSection getCIFSConfig() {
    return m_cifsConfig;
  }
  
  /**
   * Return the security configuration section
   * 
   * @return SecurityConfigSection
   */
  public final SecurityConfigSection getsecurityConfig() {
    return m_securityConfig;
  }
  
  /**
   * Determine if debug output is enabled
   * 
   * @return boolean
   */
  public final boolean hasDebug() {
    return m_debug;
  }
  
  /**
   * Generate the CIFS negotiate response packet, the authenticator should add authentication
   * specific fields to the response.
   * 
   * @param sess SMBSrvSession
   * @param respPkt SMBSrvPacket
   * @param extendedSecurity boolean
   * @exception AuthenticatorException
   */
  public void generateNegotiateResponse(SMBSrvSession sess, SMBSrvPacket respPkt, boolean extendedSecurity)
      throws AuthenticatorException {

    // Pack the negotiate response for NT/LanMan challenge/response authentication

    NTLanManAuthContext authCtx = (NTLanManAuthContext) getAuthContext(sess);

    // Encryption key and primary domain string should be returned in the byte area

    int pos = respPkt.getByteOffset();
    byte[] buf = respPkt.getBuffer();

    if (authCtx.getChallenge() == null) {

      // Return a dummy encryption key

      for (int i = 0; i < 8; i++)
        buf[pos++] = 0;
    }
    else {

      // Store the encryption key

      byte[] key = authCtx.getChallenge();
      for (int i = 0; i < key.length; i++)
        buf[pos++] = key[i];
    }

    // Pack the local domain name

    String domain = sess.getSMBServer().getCIFSConfiguration().getDomainName();
    if (domain != null)
      pos = DataPacker.putString(domain, buf, pos, true, true);

    // Pack the local server name

    pos = DataPacker.putString(sess.getSMBServer().getServerName(), buf, pos, true, true);

    // Set the packet length

    respPkt.setByteCount(pos - respPkt.getByteOffset());
  }

  /**
   * Process the CIFS session setup request packet and build the session setup response
   * 
   * @param sess SMBSrvSession
   * @param reqPkt SMBSrvPacket
   * @param respPkt SMBSrvPacket
   * @exception SMBSrvException
   */
  public void processSessionSetup(SMBSrvSession sess, SMBSrvPacket reqPkt, SMBSrvPacket respPkt) throws SMBSrvException {

    // Check that the received packet looks like a valid NT session setup andX request

    if (reqPkt.checkPacketIsValid(13, 0) == false) {
      throw new SMBSrvException(SMBStatus.NTInvalidParameter, SMBStatus.ErrSrv, SMBStatus.SRVNonSpecificError);
    }

    // Extract the session details

    int maxBufSize = reqPkt.getParameter(2);
    int maxMpx = reqPkt.getParameter(3);
    int vcNum = reqPkt.getParameter(4);
    int ascPwdLen = reqPkt.getParameter(7);
    int uniPwdLen = reqPkt.getParameter(8);
    int capabs = reqPkt.getParameterLong(11);

    // Extract the client details from the session setup request

    byte[] buf = reqPkt.getBuffer();

    // Determine if ASCII or unicode strings are being used

    boolean isUni = reqPkt.isUnicode();

    // Extract the password strings

    byte[] ascPwd = reqPkt.unpackBytes(ascPwdLen);
    byte[] uniPwd = reqPkt.unpackBytes(uniPwdLen);

    // Extract the user name string

    String user = reqPkt.unpackString(isUni);

    if (user == null)
      throw new SMBSrvException(SMBStatus.NTInvalidParameter, SMBStatus.ErrSrv, SMBStatus.SRVNonSpecificError);

    // Extract the clients primary domain name string

    String domain = "";

    if (reqPkt.hasMoreData()) {

      // Extract the callers domain name

      domain = reqPkt.unpackString(isUni);

      if (domain == null)
        throw new SMBSrvException(SMBStatus.NTInvalidParameter, SMBStatus.ErrSrv, SMBStatus.SRVNonSpecificError);
    }

    // Extract the clients native operating system

    String clientOS = "";

    if (reqPkt.hasMoreData()) {

      // Extract the callers operating system name

      clientOS = reqPkt.unpackString(isUni);

      if (clientOS == null)
        throw new SMBSrvException(SMBStatus.NTInvalidParameter, SMBStatus.ErrSrv, SMBStatus.SRVNonSpecificError);
    }

    // DEBUG

    if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE)) {
      Debug.println("[SMB] NT Session setup from user=" + user + ", password=" + (uniPwd != null ? HexDump.hexString(uniPwd) : "none")
          + ", ANSIpwd=" + (ascPwd != null ? HexDump.hexString(ascPwd) : "none") + ", domain=" + domain + ", os=" + clientOS
          + ", VC=" + vcNum + ", maxBuf=" + maxBufSize + ", maxMpx=" + maxMpx + ", authCtx=" + sess.getAuthenticationContext());
      Debug.println("[SMB]  MID=" + reqPkt.getMultiplexId() + ", UID=" + reqPkt.getUserId() + ", PID=" + reqPkt.getProcessId());
    }

    // Store the client maximum buffer size, maximum multiplexed requests count and client
    // capability flags

    sess.setClientMaximumBufferSize(maxBufSize != 0 ? maxBufSize : SMBSrvSession.DefaultBufferSize);
    sess.setClientMaximumMultiplex(maxMpx);
    sess.setClientCapabilities(capabs);

    // Create the client information and store in the session

    ClientInfo client = ClientInfo.getFactory().createInfo(user, uniPwd);
    client.setANSIPassword(ascPwd);
    client.setDomain(domain);
    client.setOperatingSystem(clientOS);

    if (sess.hasRemoteAddress())
      client.setClientAddress(sess.getRemoteAddress().getHostAddress());

    // Check if this is a null session logon

    if (user.length() == 0 && domain.length() == 0 && uniPwdLen == 0 && ascPwdLen == 1)
      client.setLogonType(ClientInfo.LogonNull);

    // Authenticate the user

    boolean isGuest = false;

    int sts = authenticateUser(client, sess, CifsAuthenticator.NTLM1);

    if (sts > 0 && (sts & CifsAuthenticator.AUTH_GUEST) != 0) {

      // Guest logon

      isGuest = true;

      // DEBUG

      if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
        Debug.println("[SMB] User " + user + ", logged on as guest");
    }
    else if (sts != CifsAuthenticator.AUTH_ALLOW) {

      // DEBUG

      if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE))
        Debug.println("[SMB] User " + user + ", access denied");

      // Invalid user, reject the session setup request

      throw new SMBSrvException(SMBStatus.NTLogonFailure,  SMBStatus.ErrDos, SMBStatus.DOSAccessDenied);
    }
    else if (Debug.EnableInfo && sess.hasDebug(SMBSrvSession.DBG_NEGOTIATE)) {

      // DEBUG

      Debug.println("[SMB] User " + user + " logged on " + (client != null ? " (type " + client.getLogonTypeString() + ")" : ""));
    }

    // Create a virtual circuit and allocate a UID to the new circuit

    VirtualCircuit vc = new VirtualCircuit( vcNum, client);
    int uid = sess.addVirtualCircuit( vc);
    
    if ( uid == VirtualCircuit.InvalidUID) {
    
      // DEBUG
      
      if ( Debug.EnableInfo && sess.hasDebug( SMBSrvSession.DBG_NEGOTIATE))
        Debug.println("[SMB] Failed to allocate UID for virtual circuit, " + vc);
      
      // Failed to allocate a UID
      
      throw new SMBSrvException(SMBStatus.NTLogonFailure,  SMBStatus.ErrDos, SMBStatus.DOSAccessDenied);
    }
    else if ( Debug.EnableInfo && sess.hasDebug( SMBSrvSession.DBG_NEGOTIATE)) {
      
      // DEBUG
      
      Debug.println("[SMB] Allocated UID=" + uid + " for VC=" + vc);
    }
    
    // Set the guest flag for the client, indicate that the session is logged on

    client.setGuest(isGuest);
    sess.setLoggedOn(true);

    // Build the session setup response SMB

    respPkt.setParameterCount(3);
    respPkt.setParameter(0, 0); // No chained response
    respPkt.setParameter(1, 0); // Offset to chained response
    respPkt.setParameter(2, isGuest ? 1 : 0);
    respPkt.setByteCount(0);

    respPkt.setTreeId(0);
    respPkt.setUserId(uid);

    // Set the various flags

    int flags = respPkt.getFlags();
    flags &= ~SMBSrvPacket.FLG_CASELESS;
    respPkt.setFlags(flags);

    int flags2 = SMBSrvPacket.FLG2_LONGFILENAMES;
    if ( isUni)
      flags2 += SMBSrvPacket.FLG2_UNICODE;
    
    if ( hasExtendedSecurity() == false)
      flags2 &= ~SMBSrvPacket.FLG2_EXTENDEDSECURITY;
    
    respPkt.setFlags2(flags2);

    // Pack the OS, dialect and domain name strings.

    int pos = respPkt.getByteOffset();
    buf = respPkt.getBuffer();

    if (isUni)
      pos = DataPacker.wordAlign(pos);

    pos = DataPacker.putString("Java", buf, pos, true, isUni);
    pos = DataPacker.putString("Alfrsco CIFS Server " + sess.getServer().isVersion(), buf, pos, true, isUni);
    pos = DataPacker.putString(sess.getSMBServer().getCIFSConfiguration().getDomainName(), buf, pos, true, isUni);

    respPkt.setByteCount(pos - respPkt.getByteOffset());
  }

  /**
   * Return the encryption key/challenge length
   * 
   * @return int
   */
  public int getEncryptionKeyLength() {

    return STANDARD_CHALLENGE_LEN;
  }

  /**
   * Return the server capability flags
   * 
   * @return int
   */
  public int getServerCapabilities() {

    return Capability.Unicode + Capability.RemoteAPIs + Capability.NTSMBs + Capability.NTFind + Capability.NTStatus
        + Capability.LargeFiles + Capability.LargeRead + Capability.LargeWrite;
  }

  /**
   * Determine if guest access is allowed
   * 
   * @return boolean
   */
  public final boolean allowGuest() {
    return m_allowGuest;
  }

  /**
   * Return the guest user name
   * 
   * @return String
   */
  public final String getGuestUserName() {
    return m_guestUserName;
  }

  /**
   * Determine if unknown users should be mapped to the guest account
   * 
   * @return boolean
   */
  public final boolean mapUnknownUserToGuest() {
    return m_mapToGuest;
  }

  /**
   * Enable/disable the guest account
   * 
   * @param ena Enable the guest account if true, only allow defined user accounts access if false
   */
  public final void setAllowGuest(boolean ena) {
    m_allowGuest = ena;
  }

  /**
   * Set the guest user name
   * 
   * @param guest String
   */
  public final void setGuestUserName(String guest) {
    m_guestUserName = guest;
  }

  /**
   * Enable/disable mapping of unknown users to the guest account
   * 
   * @param ena Enable mapping of unknown users to the guest if true
   */
  public final void setMapToGuest(boolean ena) {
    m_mapToGuest = ena;
  }

  /**
   * Set the security mode flags
   * 
   * @param flg int
   */
  protected final void setSecurityMode(int flg) {
    m_securityMode = flg;
  }

  /**
   * Set the extended security flag
   * 
   * @param extSec boolean
   */
  protected final void setExtendedSecurity( boolean extSec) {
    m_extendedSecurity = extSec;
  }
  
  /**
   * Close the authenticator, perform any cleanup
   */
  public void closeAuthenticator() {

    // Override if cleanup required
  }

  /**
   * Validate a password by encrypting the plain text password using the specified encryption key
   * and encryption algorithm.
   * 
   * @param user UserAccount
   * @param client ClientInfo
   * @param authCtx AuthContext
   * @param alg int
   * @return boolean
   */
  protected final boolean validatePassword(UserAccount user, ClientInfo client, AuthContext authCtx, int alg) {

    // Get the challenge
    
    byte[] encryptKey = null;
    
    if ( authCtx != null && authCtx instanceof NTLanManAuthContext) {

      // Get the NT/LanMan challenge
      
      NTLanManAuthContext ntlmCtx = (NTLanManAuthContext) authCtx;
      encryptKey = ntlmCtx.getChallenge();
    }
    else
      return false;

    // Get the encrypted password
    
    byte[] encryptedPwd = null;
    
    if ( alg == LANMAN)
      encryptedPwd = client.getANSIPassword();
    else
      encryptedPwd = client.getPassword();
    
    // Check if the user account has the MD4 password hash

    byte[] encPwd = null;
    
    if ( user.hasMD4Password() && alg != LANMAN) {

      try {
        
        // Generate the encrpyted password
        
        if ( alg == NTLM1) {
          
          // Get the MD4 hashed password
          
          byte[] p21 = new byte[21];
          System.arraycopy( user.getMD4Password(), 0, p21, 0, user.getMD4Password().length);
          
          // Generate an NTLMv1 encrypted password
        
          encPwd = getEncryptor().doNTLM1Encryption( p21, encryptKey);
        }
        else if ( alg == NTLM2) {
          
          // Generate an NTLMv2 encrypted password
          
          encPwd = getEncryptor().doNTLM2Encryption( user.getMD4Password(), client.getUserName(), client.getDomain());
        }
      }
      catch ( NoSuchAlgorithmException ex) {
      }
      catch ( InvalidKeyException ex) {
      }
    }
    else {
      
      // Generate an encrypted version of the plain text password
  
      encPwd = generateEncryptedPassword( user.getPassword()  != null ? user.getPassword() : "", encryptKey, alg, client.getUserName(), client.getDomain());
    }
    
    // Compare the generated password with the received password

    if (encPwd != null && encryptedPwd != null && encPwd.length == STANDARD_PASSWORD_LEN
        && encryptedPwd.length == STANDARD_PASSWORD_LEN) {

      // Compare the password arrays

      for (int i = 0; i < STANDARD_PASSWORD_LEN; i++)
        if (encPwd[i] != encryptedPwd[i])
          return false;

      // Password is valid

      return true;
    }

    // User or password is invalid

    return false;
  }

  /**
   * Convert the password string to a byte array
   * 
   * @param pwd String
   * @return byte[]
   */

  protected final byte[] convertPassword(String pwd) {

    // Create a padded/truncated 14 character string

    StringBuffer p14str = new StringBuffer();
    p14str.append(pwd);
    if (p14str.length() > 14)
      p14str.setLength(14);
    else {
      while (p14str.length() < 14)
        p14str.append((char) 0x00);
    }

    // Convert the P14 string to an array of bytes. Allocate the return 16 byte array.

    return p14str.toString().getBytes();
  }

  /**
   * Return the password encryptor
   * 
   * @return PasswordEncryptor
   */
  protected final PasswordEncryptor getEncryptor() {
    return m_encryptor;
  }

  /**
   * Return the authentication status as a string
   * 
   * @param sts int
   * @return String
   */
  protected final String getStatusAsString(int sts) {

    String str = null;

    switch (sts) {
      case AUTH_ALLOW:
        str = "Allow";
        break;
      case AUTH_DISALLOW:
        str = "Disallow";
        break;
      case AUTH_GUEST:
        str = "Guest";
        break;
      case AUTH_BADPASSWORD:
        str = "BadPassword";
        break;
      case AUTH_BADUSER:
        str = "BadUser";
        break;
    }

    return str;
  }

  /**
   * Set the access mode of the server.
   *
   * @param mode Either SHARE_MODE or USER_MODE.
   */
  public final void setAccessMode(int mode) {
    m_accessMode = mode;
  }
  
  /**
   * Logon using the guest user account
   * 
   * @param client ClientInfo
   * @param sess SrvSession
   */
  protected void doGuestLogon(ClientInfo client, SrvSession sess) {

    // Set the home folder for the guest user

    client.setUserName(getGuestUserName());

    // Mark the client as being a guest logon

    client.setGuest(true);
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
   * Set the current authenticated user context for this thread
   * 
   * @param client ClientInfo
   */
  public void setCurrentUser(ClientInfo client) {
  }

  /**
   * Map a client IP address to a domain
   * 
   * @param clientIP InetAddress
   * @return String
   */
  protected final String mapClientAddressToDomain(InetAddress clientIP) {

    // Check if there are any domain mappings

    if (m_securityConfig.hasDomainMappings() == false)
      return null;

    // Convert the client IP address to an integer value

    int clientAddr = IPAddress.asInteger(clientIP);
    
    for (DomainMapping domainMap : m_securityConfig.getDomainMappings()) {
      
      if (domainMap.isMemberOfDomain(clientAddr)) {
        
        // DEBUG

        if (Debug.EnableInfo && hasDebug())
          Debug.println("Mapped client IP " + clientIP + " to domain " + domainMap.getDomain());

        return domainMap.getDomain();
      }
    }

    // DEBUG

    if (Debug.EnableInfo && hasDebug())
      Debug.println("Failed to map client IP " + clientIP + " to a domain");

    // No domain mapping for the client address

    return null;
  }

}