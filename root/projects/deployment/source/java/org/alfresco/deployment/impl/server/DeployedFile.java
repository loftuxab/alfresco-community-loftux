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

import java.io.Serializable;

import org.alfresco.deployment.types.FileType;

/**
 * This is a record of a deployed file. It holds the pre-commit location
 * of a file, the final location of the file, and the GUID of the file.
 * @author britt
 */
public class DeployedFile implements Serializable
{
    private static final long serialVersionUID = -8500167211804636309L;

    private FileType fType;
    
    private String fPreLocation;
    
    private String fFinalPath;
    
    private String fGUID;
    
    public DeployedFile(FileType type,
                        String preLocation,
                        String finalPath,
                        String guid)
    {
        fType = type;
        fPreLocation = preLocation;
        fFinalPath = finalPath;
        fGUID = guid;
    }

    /**
     * @return the FinalPath
     */
    public String getFinalPath()
    {
        return fFinalPath;
    }

    /**
     * @return the GUID
     */
    public String getGuid()
    {
        return fGUID;
    }
    
    /**
     * @return the PreLocation
     */
    public String getPreLocation()
    {
        return fPreLocation;
    }
    
    /**
     * @return the Type
     */
    public FileType getType()
    {
        return fType;
    }
}
