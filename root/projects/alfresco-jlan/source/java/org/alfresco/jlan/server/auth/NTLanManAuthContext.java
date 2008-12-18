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

import java.util.Random;

import org.alfresco.jlan.util.DataPacker;
import org.alfresco.jlan.util.HexDump;

/**
 * NTLM1/LanMan CIFS Authentication Context Class
 * 
 * <p>Holds the challenge sent to the client during the negotiate phase that is used to verify the
 * hashed password in the session setup phase.
 *
 * @author gkspencer
 */
public class NTLanManAuthContext extends ChallengeAuthContext {

  // Random number generator used to generate challenge

  private static Random m_random = new Random(System.currentTimeMillis());

  /**
   * Class constructor
   */
  public NTLanManAuthContext() {

    // Generate a new challenge key, pack the key and return

    m_challenge = new byte[8];
    DataPacker.putIntelLong(m_random.nextLong(), m_challenge, 0);
  }

  /**
   * Class constructor
   * 
   * @param challenge byte[]
   */
  public NTLanManAuthContext(byte[] challenge) {
    m_challenge = challenge;
  }

  /**
   * Return the CIFS authentication context as a string
   * 
   * @return String
   */
  public String toString() {

    StringBuffer str = new StringBuffer();

    str.append("[NTLM,Challenge=");
    str.append(HexDump.hexString(m_challenge));
    str.append("]");

    return str.toString();
  }
}
