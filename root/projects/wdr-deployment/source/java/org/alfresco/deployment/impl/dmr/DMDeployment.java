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

import java.io.IOException;

import org.alfresco.deployment.impl.server.Deployment;
import org.alfresco.service.cmr.model.FileInfo;

/**
 *
 *
 * @author Mark Rogers
 */
public class DMDeployment extends Deployment 
{
    /**
     * 
     */
    private static final long serialVersionUID = 1072135017772640386L;
    private FileInfo rootNode;
    
    public DMDeployment(String ticket, String targetName, String storeName,
            int version, FileInfo rootNode) throws IOException
            
    {
        super(ticket, targetName, storeName, version);
        this.setRootNode(rootNode);
    }

    public void setRootNode(FileInfo rootNode)
    {
        this.rootNode = rootNode;
    }

    public FileInfo getRootNode()
    {
        return rootNode;
    }
    

}
