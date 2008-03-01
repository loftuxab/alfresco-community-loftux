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
package org.alfresco.web.scripts.atom;

import java.util.Map;

import javax.xml.namespace.QName;

import org.alfresco.util.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

/**
 * Atom Model
 * 
 * @author davidc
 */
public class AtomModel
{
    private AbderaService abderaService;
    
    /**
     * Sets the Abdera Service
     * 
     * @param abderaService
     */
    public void setAbderaService(AbderaService abderaService)
    {
       this.abderaService = abderaService; 
    }
    
    /**
     * Construct an empty Feed
     * 
     * @return  feed
     */
    public Feed newFeed()
    {
        return abderaService.newFeed();
    }
    
    /**
     * Construct an empty Entry
     * 
     * @return  entry
     */
    public Entry newEntry()
    {
        return abderaService.newEntry();
    }
    
    /**
     * Gets pre-configured Atom Extensions (QNames)
     * 
     * @return  map of QNames by alias
     */
    public Map<String, QName> getQNames()
    {
        return abderaService.getQNameExtensions();
    }
    

//    TODO: To consider... 
// 
//    public Entry parseEntry(Content content)
//    {
//        // TODO:
//        return null;
//    }
//    
//    public Feed parseFeed(Content content)
//    {
//        // TODO:
//        return null;
//    }
    
}
