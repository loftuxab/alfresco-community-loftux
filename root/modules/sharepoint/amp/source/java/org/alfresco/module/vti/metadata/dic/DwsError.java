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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of standard errors that may be returned while working with DwsService methods.</p>
 * 
 * @author PavelYur
 */
public enum DwsError
{
    /**
     * The user does not have sufficient rights
     */
    NO_ACCESS (3),               
    
    /**
     * Another user modified the specified item
     */
    CONFLICT (4),                
    
    /**
     * Could not find the specified item
     */
    ITEM_NOT_FOUND (5),          
    
    /**
     *  The specified list does not exist
     */
    LIST_NOT_FOUND (7),          
    
    /**
     * The specified list contains more than 99 items
     */
    TOO_MANY_ITEMS (8),          
    
    /**
     *  The parent folder does not exist
     */
    FOLDER_NOT_FOUND (10),       
    
    /**
     * The document workspace contains subsites
     */
    WEB_CONTAINS_SUBWEB (11),    
    
    /**
     * The specified URL already exists
     */
    ALREADY_EXISTS (13),         
    
    /**
     * This operation exceeds the user's quota
     */
    QUOTA_EXCEEDED (14);             
    
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
