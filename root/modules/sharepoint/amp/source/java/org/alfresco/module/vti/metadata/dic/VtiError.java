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

package org.alfresco.module.vti.metadata.dic;

/**
 * @author Michael Shavnev
 * @author Dmitry Lazurkin
 *
 */
public enum VtiError
{
    WRITE_ERROR (0x0002000C, "Write error on file."),
    CANNOT_RENAME_DEST_EXISTS (0x00020019, "Cannot rename : destination already exists."),
    FILE_ALREADY_EXISTS (0x00090002, "A file already exsits."),
    V_BAD_URL (0x00090005, "The provided URL is invalid."),
    V_URL_NOT_FOUND (0x00090006, "There is no file with URL in this Web."),
    PRIMARY_PARENT_NOT_EXIST (0x00090007, "The folder that would hold URL does not exist on the server."),
    FOLDER_ALREADY_EXISTS (0x0009000D, "A folder already exisits."),
    V_DOC_CHECKED_OUT (0x0009000E, "The file is checked out or locked for editing."),
    V_DOC_NOT_CHECKED_OUT (0x0009000F, "The file is not checked out."),
    SOME_FILES_AUTO_CHECKEDOUT (0x0009001E, "Some files have been automatically checked out from the source control repository."),
    V_URL_DIR_NOT_FOUND (0x00090007, "The folder does not exist. Please create the folder and then retry the operation."),
    V_DOC_IS_LOCKED (0x00090040, "The specified file is currently in use."),
    V_FILE_OPEN_FOR_WRITE (0x00020002, "The file cannot be opened for writing."),
    V_REMOVE_FILE (0x00020007, "The file could not be removed. "),
    V_REMOVE_DIRECTORY (0x00020004, "The directory could not be removed."),

    V_OWSSVR_ERRORACCESSDENIED (0x001E0002, "Access denied."),
    V_OWSSVR_ERRORSERVERINCAPABLE (0x001E0006, "The server does not support this capability. ");

    private final String messagePattern;
    private final int errorCode;

    VtiError(int errorCode, String messagePattern)
    {
        this.errorCode = errorCode;
        this.messagePattern = messagePattern;
    }

    public String getMessagePattern()
    {
        return messagePattern;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

}
