/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.connector;

import java.io.Serializable;

import org.dom4j.Element;

/**
 * Describes a MediaWiki credential as received back from the MediaWiki
 * login service API.
 * 
 * @author muzquiano
 */
public class MediaWikiHeaders implements Serializable
{    
    private String lguserid;
    private String lgusername;
    private String lgtoken;
    private String cookieprefix;
    private String sessionid;
    
    /**
     * Instantiates a new media wiki headers.
     * 
     * @param loginElement the login element
     */
    protected MediaWikiHeaders(Element loginElement)
    {
        String result = (String) loginElement.attributeValue("result");
        if("Success".equalsIgnoreCase(result))
        {            
            lguserid = (String) loginElement.attributeValue("lguserid");
            lgusername = (String) loginElement.attributeValue("lgusername");
            lgtoken = (String) loginElement.attributeValue("lgtoken");
            cookieprefix = (String) loginElement.attributeValue("cookieprefix");
            sessionid = (String) loginElement.attributeValue("sessionid");
        }
    }
    
    /**
     * Gets the request properties.
     * 
     * @return the request properties
     */
    protected String getCookieString()
    {
        if(cookieprefix == null)
        {
            cookieprefix = "";
        }
        
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        
        if(lgusername != null)
        {
            if(!first)
            {
                builder.append(";");
            }
            builder.append(cookieprefix);
            builder.append("UserName");
            builder.append("=");
            builder.append(lgusername);
            first = false;
        }
        if(lguserid != null)
        {
            if(!first)
            {
                builder.append(";");
            }
            builder.append(cookieprefix);
            builder.append("UserID");
            builder.append("=");
            builder.append(lguserid);
            first = false;
        }
        if(lgtoken != null)
        {
            if(!first)
            {
                builder.append(";");
            }
            builder.append(cookieprefix);
            builder.append("Token");
            builder.append("=");
            builder.append(lgtoken);
            first = false;
        }
        if(sessionid != null)
        {
            if(!first)
            {
                builder.append(";");
            }
            builder.append(cookieprefix);
            builder.append("_session");
            builder.append("=");
            builder.append(sessionid);
            first = false;
        }
        
        return builder.toString();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getCookieString();
    }
}
