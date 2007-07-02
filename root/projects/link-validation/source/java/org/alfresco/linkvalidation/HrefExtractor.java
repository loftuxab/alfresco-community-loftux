/*-----------------------------------------------------------------------------
*  Copyright 2007 Alfresco Inc.
*  
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*  
*  This program is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
*  for more details.
*  
*  You should have received a copy of the GNU General Public License along
*  with this program; if not, write to the Free Software Foundation, Inc.,
*  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  As a special
*  exception to the terms and conditions of version 2.0 of the GPL, you may
*  redistribute this Program in connection with Free/Libre and Open Source
*  Software ("FLOSS") applications as described in Alfresco's FLOSS exception.
*  You should have received a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    HrefExtractor.java
*----------------------------------------------------------------------------*/

package org.alfresco.linkvalidation;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

/**
*  Given an HREF, extracts links from "A" and "IMG" nodes,
*  and forcing all hostnames and protocols to lower-case
*  (this makes map-based caching more effective).
*/
public class HrefExtractor 
{
    Parser parser_;
    static OrFilter A_OR_IMG = new OrFilter( new TagNameFilter("A"), 
                                             new TagNameFilter("IMG"));

    public HrefExtractor() { parser_ = new Parser(); }

    public void setConnection (URLConnection connection)  
    {
        try { parser_.setConnection( connection); }
        catch (ParserException hpe) 
        { 
            // This is where the attempt is made to connect
            // to the remote server.  Unfortunately, ParserException
            // isn't particuarly informative, and hpe.getCause() 
            // returns null.   The good news is that when you call
            // other functions later on, more precice exceptions
            // get thrown.  Therefore, just swallow the uninformative
            // ParserException here.  Not ideal, but neither is
            // re-writing org.htmlparser.Parser.
        }
    }

    public List<String> extractHrefs() throws ParserException 
    {
        parser_.reset();
        NodeList node_list;
        
        try 
        { 
            node_list = parser_.extractAllNodesThatMatch( A_OR_IMG ); 
        }
        catch (EncodingChangeException e)
        {
            parser_.reset();
            node_list = parser_.extractAllNodesThatMatch( A_OR_IMG ); 
        }

        ArrayList<String> hrefs = new ArrayList<String>( node_list.size() );

        SimpleNodeIterator iter = node_list.elements();
        while (iter.hasMoreNodes() )
        {
            Tag node = (Tag) iter.nextNode();
            if  ( "A".equals( node.getTagName() ) )
            {
                hrefs.add( lowercase_hostname( ((LinkTag)node).getLink()));
            }
            else
            {
                hrefs.add( lowercase_hostname( ((ImageTag)node).getImageURL()));
            }
        }
        return hrefs;
    }

    /*-------------------------------------------------------------------------
    *   Ensure hostname & protocol are in lower-case.
    *------------------------------------------------------------------------*/
    String lowercase_hostname( String raw_url )
    {
        // Incoming URLs will look like this:  "http://mooCow.com"
        //                                or:  "http://mooCow.com/"
        //                                or:  "http://mooCow.com:999"
        //                                or:  "http://mooCow.com:999/..."

        if  (raw_url == null ) { return null; }

        int slash_1 = raw_url.indexOf('/');
        if ( slash_1 < 0) { return raw_url;}

        int slash_2 = raw_url.indexOf('/', slash_1 + 1);
        if ( slash_2 < 0) { return raw_url;}

        int end_hostport =  raw_url.indexOf('/', slash_2 + 1);

        int raw_url_length = raw_url.length();

        if ( end_hostport < 0) { end_hostport = raw_url_length; }

        return raw_url.substring(0, end_hostport).toLowerCase() +
               raw_url.substring(end_hostport, raw_url_length);
    }

    public static void main(String[] argv) 
    {
        if (argv.length < 0) 
        {
            System.err.println("Syntax Error : Specify an href to parse");
            System.exit(-1);
        }

        HttpURLConnection conn = null; 
        URL               url  = null;
        int               response_code =0;

        try 
        {
            url  = new URL( argv[0] );
            conn = (HttpURLConnection) url.openConnection(); 
        }
        catch (Exception e) { e.printStackTrace(); }

        HrefExtractor     linkExtractor = new HrefExtractor();

        conn.setConnectTimeout( 10000 );
        conn.setReadTimeout(    30000 );
        conn.setUseCaches(      false );

        try { response_code = conn.getResponseCode(); }
        catch (Exception e) 
        { 
            // Things that can go wrong:
            //     bad hostname:       java.net.UnknownHostException
            //     connection refused: java.net.ConnectException
            
            e.printStackTrace(); 
            System.exit(-1);
        }

        System.out.println("Response code: " + response_code );

        linkExtractor.setConnection( conn );
        List<String> hrefs = null;
        try { hrefs = linkExtractor.extractHrefs(); }
        catch (Exception e) { e.printStackTrace(); }

        for (String href: hrefs)
        {
            System.out.println( href );
        }
    }
}
