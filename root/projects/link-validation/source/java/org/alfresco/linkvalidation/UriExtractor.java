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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.Pattern;
import java.util.SortedMap;
import java.util.TreeMap;
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

//-----------------------------------------------------------------------------
/**
*  Given an HREF, extracts a unique and sorted map of URIs from
*  "A" and "IMG" nodes, and forcing all hostnames and protocols
*  to lower-case (this makes map-based caching more effective).
*  <p>
*  Browsers don't consider certain unescaped links like this as
*  broken, even though in a strict sense, they are.  For example:
*  <pre>
*       &lt;a href="mind the gap.html"&gt;click me&lt;/a&gt;
*  </pre>
*
*  Browsers will treat this as if your web page contained:
*  <pre>
*       &lt;a href="mind%20the%20gap.html"&gt;click me&lt;/a&gt;
*  </pre>
*
*  The goal of link validation is to similate what would
*  appear broken to a broser, not to enforce o
*  RFC 2396 (see: http://www.ietf.org/rfc/rfc2396.txt), or
*  RFC 3987 (see: http://www.ietf.org/rfc/rfc3987.txt).
*
*  Therefore, the UriExtractor cannonicalizes parsed links so
*  they're corrected in the same way browsers correct them.
*  <p>
*
*  Also, unlike the "legacy" htmlparser.org standard of stripping
*  the URI scheme away from "mailto:*"  and "javascript:*" links
*  (an no others), this extractor preserves them (thereby making
*  it possible to deal with URIs in a uniform way downstream).
*/
//-----------------------------------------------------------------------------
public class UriExtractor
{
    Parser parser_;
    static OrFilter A_OR_IMG = new OrFilter( new TagNameFilter("A"),
                                             new TagNameFilter("IMG"));

    //-------------------------------------------------------------------------
    /** Lookup table to see if a char is hex */
    //-------------------------------------------------------------------------
    static final int Is_hex[] =
    {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    //-------------------------------------------------------------------------
    /** Lookup table to xlate a hex char to dec */
    //-------------------------------------------------------------------------
    static final int Hex_to_dec[] =
    {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0,
        0,10,11,12,13,14,15, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0,10,11,12,13,14,15, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    //-------------------------------------------------------------------------
    /**
    *  Recognizes schema-qualified URIs in "generic syntax";
    *  See http://en.wikipedia.org/wiki/URI_scheme   <br>.
    */
    //-------------------------------------------------------------------------
    static final Pattern Url_pattern =             // ... earning my keep... ;)
        Pattern.compile( "^([^:/?#]+)://"   +      // protocol
                         "(?:([^@]+)@)?"    +      // user info
                         "([a-zA-Z0-9.-]+)" +      // host
                         "(?::([0-9]+))?"   +      // port
                         "([^?#]*)?"        +      // path
                         "(?:\\?([^#]*))?"  +      // query
                         "(?:#(.*))?$" );          // fragment

    //-------------------------------------------------------------------------
    /** Return an unencoded version of 'str' if 'str' is %HH-encoded         */
    //-------------------------------------------------------------------------
    static String Unhex( String str )
    {
        if ( str == null ) { return str; }          // nothing to do
        if (str.indexOf('%') < 0) { return str; }   // nothing to unhex

        int length = str.length();
        byte [] dec = new byte[ length ];
        byte [] enc = str.getBytes();

        int j=0;
        for (int i=0; i<length; i++, j++)
        {
            int x1, x2;
            if ( (enc[i] == '%') && ( i + 2 < length ) &&
                 (Is_hex[(x1=enc[i+1])] + Is_hex[(x2=enc[i+2])]) == 2
               )
            {
                dec[j] = (byte)(((Hex_to_dec[x1]) << 4) + Hex_to_dec[x2]);
                i+=2;
            }
            else { dec[j] = enc[i]; }
        }
        return new String( dec, 0, j );   // dec might be smaller than enc
    }


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

    //-------------------------------------------------------------------------
    /** Return sorted unique cannonicalized map of the URIs in a web page.   */
    //-------------------------------------------------------------------------
    public SortedMap<String,Boolean> extractURIs() throws ParserException
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

        SortedMap<String,Boolean> hrefs = new TreeMap<String,Boolean>();

        SimpleNodeIterator iter = node_list.elements();
        while (iter.hasMoreNodes() )
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

            Tag      node           = (Tag) iter.nextNode();
            boolean  is_well_formed = true;
            String   link;

            if  ( "A".equals( node.getTagName() ) )
            {
                link = ((LinkTag) node).extractLink();
            }
            else
            {
                link = ((ImageTag)node).getImageURL();
            }

            // Now for some pyrotechnics...
            //
            //     Browsers are much more forgiving of broken links
            //     than the letter-of-the-law.  For for example the
            //     following "should" not work in a browser, but does:
            //
            //        <a href="mind the gap.html">works in a browser!</a>
            //
            //     What the user ought to have done was something like:
            //
            //        <a href="mind%20the%20gap.html">works in a browser!</a>
            //
            //     Browsers will even tolerate a mix of encoded & unencoded:
            //
            //        <a href="mind%20the gap.html">works in a browser!</a>
            //
            //     However, no browser will let you %HH encode the protocol
            //     or the chars in the hostname.  For example, the following
            //     are treated as "broken" by browsers:
            //
            //        <a href="%68ttp://google.com/">works in a browser!</a>
            //        <a href="http://googl%65.com/">works in a browser!</a>
            //
            //     Because the goal of link validation is to approximate what
            //     a *browser* thinks is broken, an attempt is made to do the
            //     following:
            //
            //      [1]  Parse the link as if it were a URI in "generic syntax".
            //           See:  http://en.wikipedia.org/wiki/URI_scheme
            //
            //      [2]  If [1], worked, unhex only the segments that browsers
            //           do then reconstruct the URI based on that.
            //
            //      [3]  If [2] worked, reconstruct the link using the URI
            //           toASCIIString() function.   Note [2] is required
            //           because the URI class takes *unencoded* strings
            //           as inputs, but the web page might have used either
            //           the encoded form or unencoded.
            //
            //      Otherwise, don't manipulate the link fetched.
            //
            // Got that?

            Matcher matcher = Url_pattern.matcher( link );
            if ( matcher.matches() )
            {
               try
               {
                   int port = -1;
                   String port_str =  matcher.group(4);
                   if ( port_str != null )
                   {
                       port = Integer.parseInt(port_str);
                   }
                   URI u = new URI(
                            (matcher.group(1)).toLowerCase() ,  // scheme
                            matcher.group(2),                   // userinfo
                            (matcher.group(3)).toLowerCase(),   // hostname
                            port,                               // port
                            Unhex( matcher.group(5) ),          // path
                            Unhex( matcher.group(6) ),          // query
                            Unhex( matcher.group(7) ) );        // frag

                   link = u.toASCIIString();
               }
               catch (Exception e) { is_well_formed = false; }
            }
            hrefs.put( lowercase_hostname( link ) , is_well_formed );
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
        SortedMap<String,Boolean> uris = null;
        try { uris = linkExtractor.extractURIs(); }
        catch (Exception e) { e.printStackTrace(); }

        for (String uri : uris.keySet())
        {
            System.out.println( uri );
        }
    }
}
