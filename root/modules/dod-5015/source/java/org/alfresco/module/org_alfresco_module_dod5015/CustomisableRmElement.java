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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

/**
 * This enum gives the set of allowed customisable types (aspects) where a custom
 * property can be defined.
 */
public enum CustomisableRmElement
{
    RECORD_SERIES   ("rmc:customRecordSeriesProperties"),
    RECORD_CATEGORY ("rmc:customRecordCategoryProperties"),
    RECORD_FOLDER   ("rmc:customRecordFolderProperties"),
    RECORD          ("rmc:customRecordProperties");

    private final String aspectName;
    private CustomisableRmElement(String aspectName)
    {
        this.aspectName = aspectName;
    }
    
    public static CustomisableRmElement getEnumFor(String elementName)
    {
        if ("recordSeries".equalsIgnoreCase(elementName))
        {
            return RECORD_SERIES;
        }
        else if ("recordCategory".equalsIgnoreCase(elementName))
        {
            return RECORD_CATEGORY;
        }
        else if ("recordFolder".equalsIgnoreCase(elementName))
        {
            return RECORD_FOLDER;
        }
        else if ("record".equalsIgnoreCase(elementName))
        {
            return RECORD;
        }
        else
        {
            return null;
        }
    }
    
    public String getCorrespondingAspect()
    {
        return aspectName;
    }
}