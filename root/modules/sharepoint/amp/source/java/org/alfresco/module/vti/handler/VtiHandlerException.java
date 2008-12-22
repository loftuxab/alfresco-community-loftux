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
package org.alfresco.module.vti.handler;

/**
 * Exception for SOAP services and VTI methods
 * 
 * @author EugeneZh
 */
public class VtiHandlerException extends RuntimeException
{

    public static final String NOT_FOUND = "Not found";
    public static final String NOT_PERMISSIONS = "Does not permissions";
    public static final String ALREADY_EXISTS = "Already exists";
    public static final String DOESNOT_EXIST = "Doesn't exist";
    public static final String URL_NOT_FOUND = "Url not found";
    public static final String OWSSVR_ERRORACCESSDENIED = "Owssvr access denied";
    public static final String BAD_URL = "Bad Url";
    public static final String UNDEFINED = "Undefined";
    public static final String DOC_NOT_CHECKED_OUT = "File not checked out";
    public static final String DOC_CHECKED_OUT = "File checked out";
    public static final String PRIMARY_PARENT_NOT_EXIST = "Folder does not exist";
    public static final String FOLDER_ALREADY_EXISTS = "Folder already exisits";
    public static final String FILE_ALREADY_EXISTS = "File already exsits";
    public static final String REMOVE_DIRECTORY = "Directory could not be removed";
    public static final String FILE_OPEN_FOR_WRITE = "Can't open for writing";
    public static final String REMOVE_FILE = "File could not be removed";
    public static final String URL_DIR_NOT_FOUND = "Folder does not exist";
    public static final String HAS_ILLEGAL_CHARACTERS = "The name contains characters that are not permitted";

    private static final long serialVersionUID = 1L;

    /**
     * Create exception with specified message
     * 
     * @param message the specified message
     */
    public VtiHandlerException(String message)
    {
        super(message);
    }

    /**
     * Create exception with specified message ant throwable object
     * 
     * @param message the specified message
     * @param throwable
     */
    public VtiHandlerException(String message, Throwable throwable)
    {
        super(message, throwable);
    }

    /**
     * Create exception with specified throwable object
     * 
     * @param throwable
     */
    public VtiHandlerException(Throwable throwable)
    {
        super(throwable);
    }
}
