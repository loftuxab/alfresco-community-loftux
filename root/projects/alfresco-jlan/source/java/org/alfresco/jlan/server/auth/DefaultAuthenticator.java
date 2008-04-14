/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.jlan.server.auth;

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.core.SharedDevice;

/**
 * <p>Default authenticator class.
 *
 * <p>The default authenticator implementation enables user level security mode and allows
 * any user to connect to the server.
 *
 * @author gkspencer
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
