/*
 * Copyright (C) 2009-2009 Alfresco Software Limited.
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
package org.alfresco.deployment.impl.dmr;

import org.alfresco.deployment.FileType;
import org.alfresco.deployment.impl.server.DeployedFile;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * 
 *
 * @author Mark Rogers
 */
public class DMDeployedFile extends DeployedFile
{
    /**
     * 
     */
    private static final long serialVersionUID = -5032978596418068099L;

    private NodeRef destNodeRef;
    
    private String encoding;
    
    private String mimeType;
    
    public DMDeployedFile(FileType type, 
            String preLocation, 
            String path,
            String guid, 
            boolean create,
            NodeRef destNodeRef,
            String encoding,
            String mimeType)
    {
        super(type, preLocation, path, guid, create);
        this.destNodeRef = destNodeRef;
        this.encoding = encoding;
        this.mimeType = mimeType;    
    }

    
    public DMDeployedFile(FileType type, 
            String preLocation, 
            String path,
            String guid, 
            boolean create)
    {
        super(type, preLocation, path, guid, create);
        // TODO Auto-generated constructor stub
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setDestNodeRef(NodeRef destNodeRef)
    {
        this.destNodeRef = destNodeRef;
    }

    public NodeRef getDestNodeRef()
    {
        return destNodeRef;
    }

}
