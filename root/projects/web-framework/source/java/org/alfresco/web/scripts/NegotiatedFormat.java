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
package org.alfresco.web.scripts;


/**
 * Map between media type and format
 *  
 * @author davidc
 */
public class NegotiatedFormat
{
    private MediaType mediaType;
    private String format;
    
    /**
     * Construct
     * 
     * @param mediaType
     * @param format
     */
    public NegotiatedFormat(MediaType mediaType, String format)
    {
        this.mediaType = mediaType;
        this.format = format;
    }

    /**
     * @return  media type
     */
    public MediaType getMediaType()
    {
        return mediaType;
    }
    
    /**
     * @return  format
     */
    public String getFormat()
    {
        return format;
    }
    
    /**
     * Negotiate Format - given a list of accepted media types, return the format that's
     * most suitable
     * 
     * @param accept  comma-seperated list of accepted media types
     * @param negotiatedFormats  list of available formats
     * @return  most suitable format (or null, if none)
     */
    public static String negotiateFormat(String accept, NegotiatedFormat[] negotiatedFormats)
    {
        String format = null;
        float match = 0.0f;
        String[] acceptTypes = accept.split(",");
        for (String acceptType : acceptTypes)
        {
            MediaType acceptMediaType = new MediaType(acceptType);
            for (NegotiatedFormat negotiatedFormat : negotiatedFormats)
            {
                float negotiatedMatch = negotiatedFormat.getMediaType().compare(acceptMediaType);
                if (negotiatedMatch > match)
                {
                    match = negotiatedMatch;
                    format = negotiatedFormat.getFormat();
                }
            }
        }
        return format;
    }
    
}