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

package org.alfresco.module.vti.handler.alfresco;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Dmitry Lazurkin
 *
 */
public class VtiUtils
{
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private static final SimpleDateFormat versionDateFormat = new SimpleDateFormat("M/d/yyyy h:mm a", Locale.ENGLISH);

    static
    {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        versionDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Convert FrontPageExtension version string to alfresco version label.
     * For FrontPageExtension version string minor number is optional, but for alfresco version label it's required
     *
     * @param docVersion FrontPageExtension version string
     * @return alfresco version label
     */
    public static String toAlfrescoVersionLabel(String docVersion)
    {
        if (docVersion.indexOf(".") == -1)
        {
            docVersion += ".0"; // add minor number to version label
        }

        return docVersion;
    }

    /**
     * Convert FrontPageExtension lock timeout to Alfresco lock timeout.
     * FrontPageExtension timeout is number of minutes, but Alfresco timeout is number of seconds.
     *
     * @param timeout FrontPageExtension lock timeout
     * @return Alfresco lock timeout
     */
    public static int toAlfrescoLockTimeout(int timeout)
    {
        return timeout * 60;
    }

    public static String formatDate(Date date)
    {
        return dateFormat.format(date);
    }

    public static String formatVersionDate(Date date)
    {
        return versionDateFormat.format(date);
    }

    public static boolean compare(Date date, String dateString)
    {
        return dateString.replaceAll("-0000", "+0000").equals(dateFormat.format(date));
    }

}
