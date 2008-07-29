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
 *
 * @author Michael Shavnev
 *
 *
 */
public enum VtiProperty
{
    // FOLDER & FILES
    FILE_THICKETDIR ("vti_thicketdir"),
    FILE_TIMECREATED ("vti_timecreated"),
    FILE_TIMELASTMODIFIED ("vti_timelastmodified"),
    FILE_TIMELASTWRITTEN  ("vti_timelastwritten"),

    // FOLDER
    FOLDER_DIRLATESTSTAMP ("vti_dirlateststamp"),
    FOLDER_HASSUBDIRS     ("vti_hassubdirs"),
    FOLDER_ISBROWSABLE    ("vti_isbrowsable"),
    FOLDER_ISCHILDWEB     ("vti_ischildweb"),
    FOLDER_ISEXECUTABLE   ("vti_isexecutable"),
    FOLDER_ISSCRIPTABLE   ("vti_isscriptable"),

    /**
     * Specifies which of several supported base List types is used for the List
     * associated with this folder.
     */
    FOLDER_LISTBASETYPE   ("vti_listbasetype"),

    // FILE
    FILE_TITLE ("vti_title"),
    FILE_FILESIZE ("vti_filesize"),
    FILE_METATAGS  ("vti_metatags"),
    FILE_SOURCECONTROLCHECKEDOUTBY ("vti_sourcecontrolcheckedoutby"),
    FILE_SOURCECONTROLTIMECHECKEDOUT ("vti_sourcecontroltimecheckedout"),
    FILE_THICKETSUPPORTINGFILE ("vti_thicketsupportingfile"),
    FILE_SOURCECONTROLLOCKEXPIRES ("vti_sourcecontrollockexpires"),
    FILE_SOURCECONTROLCOOKIE ("vti_sourcecontrolcookie"),
    FILE_SOURCECONTROLVERSION ("vti_sourcecontrolversion"),
    // SERVICE

    SERVICE_CASESENSITIVEURLS ("vti_casesensitiveurls"),
    SERVICE_LONGFILENAMES ("vti_longfilenames"),
    SERVICE_SHOWHIDDENPAGES ("vti_showhiddenpages"),
    SERVICE_TITLE ("vti_title"),
    SERVICE_WELCOMENAMES ("vti_welcomenames"),
    SERVICE_USERNAME ("vti_username"),
    SERVICE_SERVERTZ ("vti_servertz"),
    SERVICE_SOURCECONTROLSYSTEM ("vti_sourcecontrolsystem"),
    SERVICE_SOURCECONTROLVERSION ("vti_sourcecontrolversion"),
    SERVICE_DOCLIBWEBVIEWENABLED ("vti_doclibwebviewenabled");

    private final String value;

    VtiProperty(String value)
    {
        this.value = value;
    }

    public String toString()
    {
        return value;
    }
}
