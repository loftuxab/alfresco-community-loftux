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

import java.io.InputStream;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.writer.Writer;


/**
 * Abdera (Atom Processing) Service
 * 
 * @author davidc
 */
public interface AbderaService
{
    // Known Atom Writers
    public static final String DEFAULT_WRITER = "default";
    public static final String ATOM_WRITER = "prettyxml";
    public static final String JSON_WRITER = "json";
    
    /**
     * Gets Abdera
     * 
     * @return  abdera
     */
    public Abdera getAbdera();

    /**
     * Gets Atom Parser
     * 
     * @return  atom parser
     */
    public Parser getParser();

    /**
     * Construct an Atom Feed
     * 
     * @return  feed
     */
    public Feed newFeed();

    /**
     * Construct an Atom Entry
     * 
     * @return  entry
     */
    public Entry newEntry();

    /**
     * Parse Atom Feed
     * @param doc  document to parse
     * @param base  (optional) base path for relative references
     * @return  feed
     */
    public Feed parseFeed(InputStream doc, String base);
    
    /**
     * Parse Atom Entry
     * @param doc  document to parse
     * @param base  (optional) base path for relative references
     * @return  entry
     */
    public Entry parseEntry(InputStream doc, String base);
    
    /**
     * Gets an Atom Writer
     * 
     * @param writer  writer name
     * @return  writer (or null, if it doesn't exist)
     */
    public Writer getWriter(String writer);

    /**
     * Gets registered QName extensions
     *  
     * @return  map of QNames by Alias
     */
    public Map<String, QName> getQNameExtensions();

}
