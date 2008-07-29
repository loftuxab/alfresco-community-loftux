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
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.module.vti.metadata.dic.options;

import java.util.EnumSet;

/**
 * Used to define the behavior of file upload operations.
 * 
 * @author Michael Shavnev
 */
public enum PutOption
{    
    /**
     * The client MUST send "none" if it does not want to specify any of the options given 
     * by a RENAME-OPTION-VAL.
     */
    none,
    
    /**
     * Specified, the server does all the needed checking to ensure that all the files can be 
     * updated before changing the first one
     */
    atomic,
    
    /**
     * Used to support long-term checkout operations
     */
    checkin,
    
    /**
     * Valid only if checkin is specified. Notifies the source control of the new content (checkin),
     * but keeps the document checked out.
     */
    checkout,
    
    /**
     * The parent directory is created if it does not exist.
     */
    createdir,
    
    /**
     * Uses the date and time the document was last modified to determine whether the 
     * item has been concurrently modified by another user. This flag is used to prevent 
     * race conditions where two users could edit the same data
     */
    edit,
    
    /**
     * Acts as though versioning is enabled, even if it is not
     */
    forceversions,
    
    /**
     * Requests that metadata be returned for <b>thicket</b> supporting files.
     */
    listthickets,
    
    /**
     * Preserves information about who created the file and when.
     */
    migrationsemantics,
    
    /**
     * Does not add the document to source control.
     */
    noadd,
    
    /**
     * Uses the date and time the document was last modified, as specified in the inbound metainfo, rather than the extent of time on the server.
     */
    overwrite,
    
    /**
     * Specifies that the associated file is a thicket supporting file. 
     */
    thicket;
    
    
    public static EnumSet<PutOption> getOptions(String stringValues)
    {
        EnumSet<PutOption> enumSet = null;
        
        if (stringValues == null || stringValues.trim().length() == 0) 
        {
            enumSet = EnumSet.of(PutOption.none);
        } 
        else 
        {
            String[] values = stringValues.split(",");
            enumSet = EnumSet.noneOf(PutOption.class);
            
            for (String value : values)
            {
                enumSet.add(valueOf(value));
            }
        }

        return enumSet;
    }
    
    
}