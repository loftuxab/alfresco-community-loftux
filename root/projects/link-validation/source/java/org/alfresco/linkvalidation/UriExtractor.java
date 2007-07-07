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
*  File    UriExtractor.java
*----------------------------------------------------------------------------*/

package org.alfresco.linkvalidation;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;
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
*  Given an HREF, extracts a unique and sorted list of URIs from 
*  "A" and "IMG" nodes, and forcing all hostnames and protocols 
*  to lower-case (this makes map-based caching more effective).  
*  Unlike the "legacy" htmlparser.org standard of stripping the 
*  URI scheme away from "mailto:*"  and "javascript:*" links 
*  (an no others), this extractor preserves them (thereby making
*  it possible to deal with URIs in a uniform way downstream).
*/
public class UriExtractor 
{
    Parser parser_;
    static OrFilter A_OR_IMG = new OrFilter( new TagNameFilter("A"), 
                                             new TagNameFilter("IMG"));

    public UriExtractor() { parser_ = new Parser(); }

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

    public Set<String> extractURIs() throws ParserException 
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

        Set<String> hrefs = new TreeSet<String>();

        SimpleNodeIterator iter = node_list.elements();
        while (iter.hasMoreNodes() )
        {
            Tag node = (Tag) iter.nextNode();
            if  ( "A".equals( node.getTagName() ) )
            {
                // Oddly, there's a special case for mailto and javascript
                // links that needs to be handled.   The LinkTag strips these
                // schemes away when you call getLink().
                //
                // See: http://htmlparser.sourceforge.net
                //            /javadoc/org/htmlparser/tags
                //            /LinkTag.html#getLink()
                // 
                //       "This string has had the "mailto:" and "javascript:" 
                //       protocol stripped off the front (if those predicates 
                //       return true) but not for other protocols. Don't ask 
                //       me why, it's a legacy thing.
                //
                // Fortunately, the source is online:
                // http://htmlparser.cvs.sourceforge.net/htmlparser/htmlparser 
                //       /src/org/htmlparser/tags/LinkTag.java
                // 
                // What isn't clear in the docs becomes obvious in the source:
                // the function that's *really* wanted is extractLink()
                // (and getLink() is nothing more than a decoy).  
                //
                // You can't even reliably call getLink() then isMailLink()
                // because the logic there is somewhat busted and assumes
                // "mailtoxxxxxxxxxxxx:foo@example.com" is a mail link.

                LinkTag tag = (LinkTag) node;   // and so it goes...

                hrefs.add( lowercase_hostname( tag.extractLink() ) );
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
    String lowercase_hostname( String raw_uri )
    {
        // Incoming URIs will look like this:  "http://mooCow.com"
        //                                or:  "http://mooCow.com/"
        //                                or:  "http://mooCow.com:999"
        //                                or:  "http://mooCow.com:999/..."
        //                                or:  "mailto:alice@example.com"
        //                                or:   ... ? 
        // 
        // Therefore, just look for ".*://.../" 
        //                      or  ".*://...."
        // and lower case that frag.


        if ( raw_uri == null ) { return raw_uri; }

        int colon = raw_uri.indexOf(':');
        if (colon < 0 ) { return raw_uri; }

        int length = raw_uri.length();
        if ( length <= colon + 2 ) { return raw_uri;}
        if ( raw_uri.charAt( colon + 1 ) != '/') { return raw_uri;}
        if ( raw_uri.charAt( colon + 2 ) != '/') { return raw_uri;}

        int eohost =  raw_uri.indexOf('/', colon + 3);
        if ( eohost < 0 ) { eohost = length; }

        return raw_uri.substring(0, eohost).toLowerCase() +
               raw_uri.substring(eohost,length);
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

        UriExtractor     linkExtractor = new UriExtractor();

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
        Set<String> uris = null;
        try { uris = linkExtractor.extractURIs(); }
        catch (Exception e) { e.printStackTrace(); }

        for (String uri : uris)
        {
            System.out.println( uri );
        }
    }
}
