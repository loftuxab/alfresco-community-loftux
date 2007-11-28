package org.alfresco.jlan.server.auth;

/*
 * DefaultAuthenticator.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.core.SharedDevice;

/**
 * <p>Default authenticator class.
 *
 * <p>The default authenticator implementation enables user level security mode and allows
 * any user to connect to the server.
 */
public class DefaultAuthenticator extends CifsAuthenticator {

  /**
   * Class constructor
   */
  public DefaultAuthenticator() {
    setAccessMode(USER_MODE);
  }

  /**
   * Allow any user to access the server
   *
   * @param client   Client details.
   * @param share    Shared device the user is connecting to.
   * @param pwd      Share level password.
   * @param sess     Server session
   * @return int
   */
  public int authenticateShareConnect(ClientInfo client, SharedDevice share, String pwd, SrvSession sess) {
    return Writeable;
  }

  /**
   * Allow any user to access the server.
   *
   * @param client   Client details.
   * @param sess		 Server session
   * @param alg			 Encryption algorithm
   * @return int
   */
  public int authenticateUser(ClientInfo client, SrvSession sess, int alg) {
    return AUTH_ALLOW;
  }

  /**
   * The default authenticator does not use encrypted passwords.
   *
   * @param sess SrvSession
   * @return byte[]
   */
  public byte[] getChallengeKey(SrvSession sess) {
    return null;
  }
}