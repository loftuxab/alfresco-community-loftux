/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */

package org.alfresco.deployment;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * This is the interface that is implemented for the transport between client 
 * and server of the File System Receiver (FSR)
 * 
 * The methods in this interface are concerned with the transport of OutputStreams
 * 
 * @author britt
 */
public interface DeploymentReceiverTransport extends DeploymentReceiverService 
{
    /**
     * Get an OutputStream token.
     * @param ticket
     * @param path
     * @param guid
     * @return the token to identify this file.
     */
    public String getSendToken(String ticket, String path, String guid, String encoding, String mimeType, Set<String>aspects, Map<String, Serializable> props);
    
    /**
     * Tell the deployment receiver that a send is finished.
     * @param ticket 
     * @param outputToken
     */
    public void finishSend(String ticket, String outputToken);
    
    /**
     * Write a block of bytes to a file that is being sent.
     * @param outputToken
     * @param data
     * @param offset
     */
    public void write(String ticket, String outputToken, byte[] data, int offset, int count);
    
    // Management part of the interface below   
    
    /**
     * Shut down the Deployment Receiver.
     * @param user
     * @param password
     */
    public void shutDown(String user, char[] password);
    
//    /**
//     * Get the target status for all targets
//     */
//    public TargetStatus[] getTargetStatus(String user, String password);
//    
//    /**
//     * Get the target status
//     */
//    public TargetStatus getTargetStatus(String user, String password, String targetName);
    
}
