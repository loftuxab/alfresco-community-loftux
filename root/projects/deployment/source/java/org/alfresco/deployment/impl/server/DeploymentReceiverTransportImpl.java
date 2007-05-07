/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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

package org.alfresco.deployment.impl.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.deployment.DeploymentReceiverService;
import org.alfresco.deployment.DeploymentReceiverTransport;
import org.alfresco.deployment.FileDescriptor;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.util.GUID;

/**
 * The server side implementation the remote interface.
 * @author britt
 */
public class DeploymentReceiverTransportImpl implements
        DeploymentReceiverTransport
{
    /**
     * The DeploymentReceiverService.
     */
    private DeploymentReceiverService fService;

    /**
     * The table of OutputStreams.
     */
    private Map<String, OutputStream> fOutputs;
    
    public DeploymentReceiverTransportImpl()
    {
        fOutputs = new HashMap<String, OutputStream>();
    }
    
    public void setDeploymentReceiverService(DeploymentReceiverService service)
    {
        fService = service;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverTransport#finishSend(java.lang.String, java.lang.String)
     */
    public void finishSend(String ticket, String outputToken)
    {
        OutputStream out = fOutputs.get(outputToken);
        if (out == null)
        {
            fService.abort(ticket);
            throw new DeploymentException("Invalid output token. Aborted.");
        }
        fOutputs.remove(outputToken);
        fService.finishSend(ticket, out);
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverTransport#getSendToken(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getSendToken(String ticket, String path, String guid)
    {
        OutputStream out = fService.send(ticket, path, guid);
        String handle = GUID.generate();
        synchronized (this)
        {
            fOutputs.put(handle, out);
        }
        return handle;
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverTransport#write(java.lang.String, java.lang.String, byte[], int, int)
     */
    public void write(String ticket, String outputToken, byte[] data,
                      int offset, int count)
    {
        OutputStream out = fOutputs.get(outputToken);
        if (out == null)
        {
            throw new DeploymentException("Invalid output stream token.");
        }
        try
        {
            out.write(data, offset, count);
        }
        catch (IOException e)
        {
            fService.abort(ticket);
            throw new DeploymentException("Failed write. Aborted.");
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#abort(java.lang.String)
     */
    public void abort(String ticket)
    {
        fService.abort(ticket);
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#begin(java.lang.String, java.lang.String, java.lang.String)
     */
    public String begin(String target, String user, String password)
    {
        return fService.begin(target, user, password);
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#commit(java.lang.String)
     */
    public void commit(String ticket)
    {
        fService.commit(ticket);
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#delete(java.lang.String, java.lang.String)
     */
    public void delete(String ticket, String path)
    {
        fService.delete(ticket, path);
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#finishSend(java.lang.String, java.io.OutputStream)
     */
    public void finishSend(String token, OutputStream out)
    {
        throw new DeploymentException("Forbidden call.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#getListing(java.lang.String, java.lang.String)
     */
    public List<FileDescriptor> getListing(String ticket, String path)
    {
        return fService.getListing(ticket, path);
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#mkdir(java.lang.String, java.lang.String, java.lang.String)
     */
    public void mkdir(String ticket, String path, String guid)
    {
        fService.mkdir(ticket, path, guid);
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#send(java.lang.String, java.lang.String, java.lang.String)
     */
    public OutputStream send(String token, String path, String guid)
    {
        throw new DeploymentException("Forbidden call.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#shutDown(java.lang.String, java.lang.String)
     */
    public void shutDown(String user, String password)
    {
        fService.shutDown(user, password);
    }

    public void setGuid(String ticket, String path, String guid)
    {
        fService.setGuid(ticket, path, guid);
    }
}
