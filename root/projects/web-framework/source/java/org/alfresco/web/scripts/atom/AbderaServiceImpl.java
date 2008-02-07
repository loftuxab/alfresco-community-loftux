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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.alfresco.web.scripts.WebScriptException;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.writer.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;


/**
 * Abdera Service Implementation
 * 
 * @author davidc
 */
public class AbderaServiceImpl implements AbderaService, InitializingBean
{
    // Logger
    private static final Log logger = LogFactory.getLog(AbderaServiceImpl.class);
    
    private Abdera abdera;
    private Parser parser;
    private List<String> writerNames;
    private Map<String,Writer> writers;
    private Map<String, String> qNamesAsString;
    private Map<String, QName> qNames;
    
    /**
     * Set QNames
     * 
     * @param qNamesAsString  map of {namespaceuri}localname by alias
     */
    public void setQnames(Map<String, String> qNamesAsString)
    {
        this.qNamesAsString = qNamesAsString;
    }
    
    /**
     * Set available Writer names
     * 
     * @param writerNames  list of writer names
     */
    public void setWriters(List<String> writerNames)
    {
        this.writerNames = writerNames;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
        throws Exception
    {
        // construct Abdera Service
        abdera = new Abdera();
        parser = abdera.getParser();
        // TODO: parser options
        
        // construct writers
        writers = new HashMap<String, Writer>(writerNames == null ? 1 : writerNames.size() +1);
        writers.put(AbderaService.DEFAULT_WRITER, abdera.getWriter());
        if (writerNames != null)
        {
            for (String writerName : writerNames)
            {
                Writer writer = abdera.getWriterFactory().getWriter(writerName);
                if (writer == null)
                {
                    throw new WebScriptException("Failed to register Atom writer '" + writerName + "'; does not exist.");
                }
                writers.put(writerName, writer);
            }
        }

        // construct QNames
        Map<String, QName> buildQNames = new HashMap<String, QName>();
        if (qNamesAsString != null)
        {
            for (Map.Entry<String, String> entry : qNamesAsString.entrySet())
            {
                String alias = entry.getKey();
                QName qName = QName.valueOf(entry.getValue());
                buildQNames.put(alias, qName);
                
                if (logger.isDebugEnabled())
                    logger.debug("Registered QName '" + qName + "' as '" + alias + "'");
            }
        }
        qNames = Collections.unmodifiableMap(buildQNames);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.atom.AbderaService#getAbdera()
     */
    public Abdera getAbdera()
    {
        return abdera;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.atom.AbderaService#getParser()
     */
    public Parser getParser()
    {
        return parser;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.atom.AbderaService#getQNameExtensions()
     */
    public Map<String, QName> getQNameExtensions()
    {
        return qNames;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.atom.AbderaService#newEntry()
     */
    public Entry newEntry()
    {
        return abdera.newEntry();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.atom.AbderaService#newFeed()
     */
    public Feed newFeed()
    {
        return abdera.newFeed();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.atom.AbderaService#parseEntry(java.io.InputStream, java.lang.String)
     */
    public Entry parseEntry(InputStream doc, String base)
    {
        Document<Element> entryDoc;
        if (base != null && base.length() > 0)
        {
            entryDoc = parser.parse(doc, base);
        }
        else
        {
            entryDoc = parser.parse(doc);
        }

        Element root = entryDoc.getRoot();
        if (!Entry.class.isAssignableFrom(root.getClass()))
        {
            throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Expected Atom Entry, but recieved " + root.getClass());
        }
        
        return (Entry)root;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.atom.AbderaService#parseFeed(java.io.InputStream, java.lang.String)
     */
    public Feed parseFeed(InputStream doc, String base)
    {
        Document<Element> feedDoc;
        if (base != null && base.length() > 0)
        {
            feedDoc = parser.parse(doc, base);
        }
        else
        {
            feedDoc = parser.parse(doc);
        }
        
        Element root = feedDoc.getRoot();
        if (!Entry.class.isAssignableFrom(root.getClass()))
        {
            throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Expected Atom Feed, but recieved " + root.getClass());
        }
        
        return (Feed)root;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.atom.AbderaService#getWriter(java.lang.String)
     */
    public Writer getWriter(String name)
    {
        return writers.get(name);
    }
    
}
