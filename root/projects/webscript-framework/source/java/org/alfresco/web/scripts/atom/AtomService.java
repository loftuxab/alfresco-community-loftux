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

import java.io.StringReader;
import java.util.Map;

import javax.xml.namespace.QName;

import org.alfresco.web.scripts.Format;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Content.Type;

/**
 * Atom Model
 * 
 * @author davidc
 */
public class AtomService
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
    public Feed createFeed()
    {
        return abderaService.createFeed();
    }
    
    /**
     * Construct an empty Entry
     * 
     * @return  entry
     */
    public Entry createEntry()
    {
        return abderaService.createEntry();
    }
    
    /**
     * Gets pre-configured Atom Extensions (QNames)
     * 
     * @return  map of QNames by alias
     */
    public Map<String, QName> getNames()
    {
        return abderaService.getNames();
    }
    
    /**
     * Creates a QName
     * 
     * @param uri
     * @param localName
     * @return  qname
     */
    public QName createQName(String uri, String localName)
    {
        return new QName(uri, localName);
    }
    
    /**
     * Establish mimetype of atom content
     * 
     * @param content  atom content
     * @return  mimetype (or null, if it could not be established)
     */
    public String toMimeType(Entry entry)
    {
        if (entry == null || entry.getContentElement() == null)
        {
            return null;
        }
        
        Content content = entry.getContentElement();
        String mimetype = (content.getMimeType() == null) ? null : content.getMimeType().toString();
        if (mimetype == null)
        {
            Content.Type type = content.getContentType();
            if (type != null)
            {
                if (type == Type.HTML)
                {
                    mimetype = Format.HTML.mimetype();
                }
                else if (type == Type.XHTML)
                {
                    mimetype = Format.XHTML.mimetype();
                }
                else if (type == Type.TEXT)
                {
                    mimetype = Format.TEXT.mimetype();
                }
            }
        }
        return mimetype;
    }

    /**
     * Parse an Atom element
     * 
     * @param entry
     * @return
     */
    public Element toAtom(org.springframework.extensions.surf.util.Content atom)
    {
        return abderaService.parse(atom.getInputStream(), null);
    }

    /**
     * Parse an Atom element
     * 
     * @param entry
     * @return
     */
    public Element toAtom(String atom)
    {
        return abderaService.parse(new StringReader(atom), null);
    }
    
    /**
     * Parse an Atom Service
     * 
     * @param entry
     * @return
     */
    public Service toService(org.springframework.extensions.surf.util.Content entry)
    {
        return abderaService.parseService(entry.getInputStream(), null);
    }

    /**
     * Parse an Atom Service
     * 
     * @param entry
     * @return
     */
    public Service toService(String entry)
    {
        return abderaService.parseService(new StringReader(entry), null);
    }
    
    /**
     * Parse an Atom Entry
     * 
     * @param entry
     * @return
     */
    public Entry toEntry(org.springframework.extensions.surf.util.Content entry)
    {
        return abderaService.parseEntry(entry.getInputStream(), null);
    }

    /**
     * Parse an Atom Entry
     * 
     * @param entry
     * @return
     */
    public Entry toEntry(String entry)
    {
        return abderaService.parseEntry(new StringReader(entry), null);
    }

    /**
     * Parse an Atom Feed
     * 
     * @param feed
     * @return
     */
    public Feed toFeed(org.springframework.extensions.surf.util.Content feed)
    {
        return abderaService.parseFeed(feed.getInputStream(), null);
    }

    /**
     * Parse an Atom Feed
     * 
     * @param feed
     * @return
     */
    public Feed toFeed(String feed)
    {
        return abderaService.parseFeed(new StringReader(feed), null);
    }
    
}
