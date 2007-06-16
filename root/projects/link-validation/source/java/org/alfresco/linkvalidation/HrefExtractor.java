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
*  Given an HREF, extracts links from "A" and "IMG" nodes.
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
        catch (ParserException hpe) { /* Oh well */  }
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
                hrefs.add( ((LinkTag)node).getLink() );
            }
            else
            {
                hrefs.add( ((ImageTag)node).getImageURL() );
            }
        }
        return hrefs;
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
