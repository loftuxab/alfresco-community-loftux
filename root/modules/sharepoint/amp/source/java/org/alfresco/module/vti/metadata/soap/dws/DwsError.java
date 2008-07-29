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
package org.alfresco.module.vti.metadata.soap.dws;

/**
 * @author PavelYur
 *
 */
public enum DwsError
{
    NO_ACCESS (3),               // The user does not have sufficient rights
    CONFLICT (4),                // Another user modified the specified item
    ITEM_NOT_FOUND (5),          // Could not find the specified item
    LIST_NOT_FOUND (7),          // The specified list does not exist
    TOO_MANY_ITEMS (8),          // The specified list contains more than 99 items
    FOLDER_NOT_FOUND (10),       // The parent folder does not exist
    WEB_CONTAINS_SUBWEB (11),    // The document workspace contains subsites
    ALREADY_EXISTS (13),         // The specified URL already exists
    QUOTA_EXCEEDED (14);         // This operation exceeds the user's quota
    
    private final int value;
    
    DwsError(int value) 
     {
         this.value = value;
     }
     
     public int toInt()
     {
         return value;
     }
}
