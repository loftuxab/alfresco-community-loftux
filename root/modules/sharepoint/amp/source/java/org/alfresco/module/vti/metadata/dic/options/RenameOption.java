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
 * Define the behaviors of a rename operation.
 * 
 * @author Michael Shavnev
 *
 */
public enum RenameOption 
{
    /**
     * The client MUST send "none" if it does not want to specify any of the options given 
     * by a RENAME-OPTION-VAL.
     */
    none,

    /**
     * Creates the parent directory if it does not already exist.
     */
    createdir,
    
    /**
     * Requests that servers, implementing <b>link fixup</b>, fix the linked files other than those moved.
     */
    findbacklinks,
    
    /**
     * Do not perform link fixup on links in moved documents.
     */
    nochangeall,
    
    /**
     * Simulates the move of a directory rather than a file.
     */
    patchprefix;

    
    public static EnumSet<RenameOption> getOptions(String stringValues)
    {
        EnumSet<RenameOption> enumSet = null;
        
        if (stringValues == null || stringValues.trim().length() == 0) 
        {
            enumSet = EnumSet.of(RenameOption.none);
        } 
        else 
        {
            String[] values = stringValues.split(",");
            enumSet = EnumSet.noneOf(RenameOption.class);
            
            for (String value : values)
            {
                enumSet.add(valueOf(value));
            }
        }

        return enumSet;
    }
    
    
}
