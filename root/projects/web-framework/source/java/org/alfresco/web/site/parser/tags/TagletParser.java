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
package org.alfresco.web.site.parser.tags;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.alfresco.web.site.FilterContext;
import org.alfresco.web.site.parser.IParser;
import org.alfresco.web.site.parser.ITagletHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author muzquiano
 */
public class TagletParser implements IParser
{
    public TagletParser()
    {
        // set up special jsp tag library
        TagLibraryInfoImpl taglib = new TagLibraryInfoImpl("jsp", "", null,
                null, "jsp", null);
        namespaces.put("jsp", taglib);
        TagAttributeInfo attributes[] = new TagAttributeInfo[1];
        attributes[0] = TagAttributeInfoHelper.newTagAttributeInfo("page",
                true, "", true);
        /*
         TagInfo includetag = new TagInfo("include", "org.alfresco.tags.JspIncludeTag", "empty", "jsp:include tag", taglib, null, attributes);
         taglib.putTag(includetag);
         */

        // set up dummy taglib for img
        TagLibraryInfoImpl imglib = new TagLibraryInfoImpl("img", "", null,
                null, "img", null);
        namespaces.put("img", imglib);

        // set up a dummy taglib for script
        TagLibraryInfoImpl scriptlib = new TagLibraryInfoImpl("script", "",
                null, null, "script", null);
        namespaces.put("script", scriptlib);

        // set up a dummy taglib for link (stylesheets)
        TagLibraryInfoImpl linklib = new TagLibraryInfoImpl("link", "", null,
                null, "link", null);
        namespaces.put("link", linklib);

        // set up a dummy taglib for body (background)
        TagLibraryInfoImpl bodylib = new TagLibraryInfoImpl("body", "", null,
                null, "body", null);
        namespaces.put("body", bodylib);

        // set up a dummy taglib for input (type=image src="")
        TagLibraryInfoImpl inputlib = new TagLibraryInfoImpl("input", "", null,
                null, "input", null);
        namespaces.put("input", inputlib);

        // set up a dummy taglib for table (background)
        TagLibraryInfoImpl tablelib = new TagLibraryInfoImpl("table", "", null,
                null, "table", null);
        namespaces.put("table", tablelib);

        // set up a dummy taglib for anchors
        TagLibraryInfoImpl alib = new TagLibraryInfoImpl("a", "", null, null,
                "a", null);
        namespaces.put("a", alib);

        // set up a dummy taglib for PARAM tags (for Flash, among others)
        TagLibraryInfoImpl paramlib = new TagLibraryInfoImpl("param", "", null,
                null, "param", null);
        namespaces.put("param", paramlib);

        // set up a dummy taglib for EMBED tags (for Flash, among others)
        TagLibraryInfoImpl embedlib = new TagLibraryInfoImpl("embed", "", null,
                null, "embed", null);
        namespaces.put("embed", embedlib);
    }

    /**
     * This imports all of the Tag Definitions from a standard TLD file
     * It informs the parser about how to work with tags of this type.
     * @param prefix
     * @param tagurl
     * @throws Exception
     */
    public void importNamespace(String prefix, String tagUrl, String tldXml)
            throws Exception
    {
        prefix = prefix.toLowerCase();
        if (namespaces.containsKey(prefix))
            return;

        Document doc = parse(tldXml);

        Element root = doc.getDocumentElement();
        NodeList nl = root.getElementsByTagName("shortname");
        String shortname = "";
        if (nl.getLength() > 0)
        {
            shortname = ((Text) nl.item(0).getChildNodes().item(0)).getData().trim();
        }
        TagLibraryInfoImpl taglib = new TagLibraryInfoImpl(prefix, tagUrl,
                null, null, shortname, null);
        namespaces.put(prefix, taglib);
        nl = root.getElementsByTagName("tag");
        for (int i = 0; i < nl.getLength(); i++)
        {
            Element el = (Element) nl.item(i);
            String name = ((Text) el.getElementsByTagName("name").item(0).getFirstChild()).getData().trim().toLowerCase();
            String tagclass = ((Text) el.getElementsByTagName("tagclass").item(
                    0).getFirstChild()).getData().trim();

            // get TagExtraInfo if it exists
            NodeList tlist = el.getElementsByTagName("teiclass");
            TagExtraInfo tei = null;
            if (tlist.getLength() > 0)
            {
                String teiclass = ((Text) tlist.item(0).getFirstChild()).getData().trim();
                try
                {
                    tei = (TagExtraInfo) Class.forName(teiclass).newInstance();
                }
                catch (Exception e)
                {
                    tei = null;
                }
            }
            String bodycontent = ((Text) el.getElementsByTagName("bodycontent").item(
                    0).getFirstChild()).getData().trim();

            // get info if it exists
            String info = "";
            tlist = el.getElementsByTagName("info");
            if (tlist.getLength() > 0)
            {
                info = ((Text) tlist.item(0).getFirstChild()).getData().trim();
            }
            NodeList attList = el.getElementsByTagName("attribute");
            TagAttributeInfo attributeInfo[] = new TagAttributeInfo[attList.getLength()];
            for (int j = 0; j < attList.getLength(); j++)
            {
                Element attEl = (Element) attList.item(j);
                String attname = ((Text) attEl.getElementsByTagName("name").item(
                        0).getFirstChild()).getData().trim().toLowerCase();
                String required = ((Text) attEl.getElementsByTagName("required").item(
                        0).getFirstChild()).getData().trim();
                attributeInfo[j] = TagAttributeInfoHelper.newTagAttributeInfo(
                        attname, required.equals("true"), "", true);
            }
            TagInfo taginfo = new TagInfo(name, tagclass, bodycontent, info,
                    null, tei, attributeInfo);
            taglib.putTag(taginfo);
        }
    }

    /**
     * Workhorse function for parsing tags.
     * @param cxt
     * @param handler
     * @param is
     * @param os
     * @return
     */
    public TokenStream parseTaglets(FilterContext cxt, ITagletHandler handler,
            InputStream is, OutputStream os)
    {
        try
        {
            tags = new ArrayList();
            tagnames = new ArrayList();
            bodytags = new ArrayList();
            this.cxt = cxt;
            this.out = new JspWriterImpl(new PrintWriter(os),
                    JspWriter.DEFAULT_BUFFER, true);
            // make the reader buffered
            this.in = new BufferedReader(new InputStreamReader(is));
            this.pcxt = new JspPageContextImpl(cxt, out);
            this.pt = handler;
            //this.line = new char[255];
            this.line = new char[4096];

            pt.setParser(this);
            pt.setPageContext(pcxt);

            // Parse the input

            initParser(in);
            pt.startPage();
            lastReturn = Tag.EVAL_PAGE;
            inJsp = false;
            boolean done = false;
            while (!done)
            {
                if (echoToChar(in, pt, "<", false) == -1)
                    break;

                origLinePos = col;

                readNext(in);

                // skip jsp
                if (nextChar == '%')
                {
                    inJsp = true;
                    pt.doPrint('<', lastReturn);
                    readNext(in);

                    // Special case for <%@ include file %> tags
                    if (nextChar == '@')
                    {
                        pt.doPrint("%", lastReturn);
                        readNext(in);
                        pt.doPrint("@", lastReturn);
                        echoWS(in, pt, true);
                        String command = readToWS(in, false);
                        pt.doPrint(command, lastReturn);
                        echoWS(in, pt, true);

                        while (nextChar != '%' && nextChar != -1)
                        {
                            String attname = readToChar(in, "= \t\n", false);
                            pt.doPrint(attname, lastReturn);
                            echoWS(in, pt, true);
                            if (nextChar != '=')
                            {
                                continue;
                            }
                            pt.doPrint('=', lastReturn);
                            readNext(in);
                            echoWS(in, pt, true);
                            String attvalue;
                            if (nextChar == '"')
                            {
                                readNext(in);
                                attvalue = readToChar(in, "\"", true, false);
                                readNext(in);
                            }
                            else
                            {
                                attvalue = readToChar(in, " \n>", false, false);
                            }
                            pt.doPrint('\"' + attvalue + '\"', lastReturn);
                            echoWS(in, pt, true);

                            // TODO: Support for includes?
                            // How could this be used?
                            /*
                             if(command.equals("include") && attname.equals("file")) 
                             {
                             boolean b_rootPage = false;
                             
                             //
                             // get the base and parent URL for this include
                             // the 'base url' is the URL of the top most JSP that kicked off all of these includes - it is the one to which all run-time references are matched
                             // the 'parent url' ist he URL of the parent JSP that is including this one.  it is the compile-time reference.
                             //
                             String currentBaseUrl = (String) cxt.getRequest().getAttribute(ContentServer.CS_INCLUDE_BASE_URL);
                             String currentParentUrl = (String) cxt.getRequest().getAttribute(ContentServer.CS_INCLUDE_PARENT_URL);
                             
                             //
                             // if the current base url is null, then we're the first jsp include
                             // so claim the request attribute
                             //
                             if(currentBaseUrl == null || "".equals(currentBaseUrl)) {
                             b_rootPage = true;
                             cxt.getRequest().setAttribute(ContentServer.CS_INCLUDE_BASE_URL, cacheInfo.getUrl());
                             }
                             
                             //
                             // set ourselves as the current parent url
                             //
                             cxt.getRequest().setAttribute(ContentServer.CS_INCLUDE_PARENT_URL, cacheInfo.getUrl());
                             
                             //
                             // load the subcontent using 'compile-time' option
                             //
                             String url = cxt.getServerInstance().loadContent(cxt, attvalue, true);
                             if(url == null)
                             url = "";
                             
                             //
                             // now that the content has loaded, restore the previous parent url
                             //
                             if(currentParentUrl != null)
                             cxt.getRequest().setAttribute(ContentServer.CS_INCLUDE_PARENT_URL, currentParentUrl);
                             else
                             cxt.getRequest().setAttribute(ContentServer.CS_INCLUDE_PARENT_URL, "");
                             
                             //
                             // if we were the root page, then remove our base url request attribute
                             //
                             if(b_rootPage)
                             cxt.getRequest().setAttribute("INCLUDE_BASE_URL", "");

                             //
                             // report the dependency to the cache
                             //
                             if(cacheInfo != null) {	
                             if(url != null && !"".equals(url)) {
                             cacheInfo.addIncluded(url.substring(cxt.getServerInstance().getCache().getVirtualDir().length()));
                             } else {
                             // not good, we couldn't cache the included jsp
                             cxt.getServerInstance().getServerStats().log("Unable to process include of file '" + attvalue + "'.  As a result, the '" + attvalue + "' file wasn't cached.  This will result in incorrect page output.  Most likely, this file isn't present inside of Content Management.");
                             throw new SAXException("Error while processing include of file '" + attvalue + "' - the file could not be found.");
                             }
                             }
                             }
                             */
                        } // while nextChar != '%'
                        if (nextChar == -1)
                            throw new SAXException("Parse error: Expecting %>");
                        readNext(in);
                        if (nextChar != '>')
                            throw new SAXException("Parse error: Expecting %>");
                        readNext(in);
                        pt.doPrint("%>", lastReturn);
                    } // if nextChar == '@'
                    else
                    {
                        do
                        {
                            pt.doPrint("%", lastReturn);
                            if (echoToChar(in, pt, "%", false) == -1)
                                throw new SAXException(
                                        "Parse error: Expecting %>");
                            readNext(in);
                        }
                        while (nextChar != '>');
                        readNext(in);
                        pt.doPrint("%>", lastReturn);
                    } // nextChar != '@'
                    inJsp = false;
                    continue;
                }

                // skip whitespace
                echoWS(in, pt, true);
                String name = readToChar(in, "> \t\n", false);
                int pos = name.indexOf(':');
                TagLibraryInfo taglib = null;
                if (pos != -1)
                {
                    String prefix;
                    if (name.charAt(0) == '/')
                        prefix = name.substring(1, pos);
                    else
                        prefix = name.substring(0, pos);
                    taglib = (TagLibraryInfo) namespaces.get(prefix.toLowerCase());
                }
                else
                {
                    taglib = (TagLibraryInfo) namespaces.get(name.toLowerCase());
                }
                if (name.length() > 0 && name.charAt(0) == '/')
                {
                    // skip unknown tags or anything inside a script
                    if (taglib == null || fInScript)
                    {
                        pt.doPrint('<', lastReturn);
                        pt.doPrint(name, lastReturn);
                        echoToChar(in, pt, ">", false, false);
                        pt.doPrint('>', lastReturn);

                        // special case for scripts
                        if (name.equalsIgnoreCase("/script"))
                            fInScript = false;
                    }
                    else
                    {
                        readToChar(in, ">", false);
                        endElement(name.substring(1));
                    }
                    readNext(in);
                }
                else
                {
                    // skip unknown tags or anything inside a script
                    if (taglib == null || fInScript)
                    {
                        pt.doPrint('<', lastReturn);
                        pt.doPrint(name, lastReturn);
                        echoToChar(in, pt, ">", false, false);
                        if (nextChar == -1)
                            throw new SAXException(
                                    "Parse error.  Reached EOF inside tag '" + name + "'.");
                        readNext(in);
                        pt.doPrint('>', lastReturn);

                        // special case for scripts
                        if (name.equalsIgnoreCase("script"))
                            fInScript = true;

                        continue;
                    }
                    JspAttributeList attrs = new JspAttributeList();
                    if (skipWS(in, false) == -1)
                        throw new SAXException(
                                "Parse error.  Reached EOF inside tag '" + name + "'.");
                    boolean hasEnding = false;
                    if (name.endsWith("/"))
                    {
                        hasEnding = true;
                        name = name.substring(0, name.length() - 1);
                    }
                    while (nextChar != '>')
                    {
                        switch (nextChar)
                        {
                            case -1:
                                // shouldn't ever hit the end of the file inside a tag
                                throw new SAXException(
                                        "Parse error.  Reached EOF inside tag '" + name + "'.");
                            case '/':
                                readNext(in);
                                if (nextChar != '>')
                                    throw new SAXException(
                                            "Parse error.  Expected '>' to follow '/'.");
                                hasEnding = true;
                                break;
                            case '?':
                                readNext(in);
                                if (nextChar != '>')
                                    throw new SAXException(
                                            "Parse error. Expected '>' to follow '?'.");
                                hasEnding = false;
                                break;
                            case '<':
                                readNext(in);
                                if (nextChar != '%')
                                {
                                    throw new SAXException(
                                            "Parse error. New tag cannot be opened here unless it is jsp.  Last attribute name was '" + last_attname + "' with value '" + last_attvalue + "'");
                                }
                                String jspvalue = "<" + readToChar(in, ">",
                                        true) + ">";
                                readNext(in);
                                attrs.putAttribute("___jsp" + jspvalue,
                                        jspvalue);
                                if (skipWS(in, false) == -1)
                                    throw new SAXException(
                                            "Parse error.  Reached EOF inside tag '" + name + "'.");
                                break;
                            default:
                                String attname = readToChar(in, "= \t\n", false);
                                last_attname = attname; //debugging
                                if (skipWS(in, true) == -1)
                                    throw new SAXException(
                                            "Parse error.  Reached EOF inside tag '" + name + "'.");
                                if (nextChar != '=')
                                {
                                    attrs.putAttribute(attname, "");
                                    continue;
                                }
                                readNext(in);
                                if (skipWS(in, true) == -1)
                                    throw new SAXException(
                                            "Parse error.  Reached EOF inside tag '" + name + "'.");
                                String attvalue;
                                switch (nextChar)
                                {
                                    case '/':
                                    case '>':
                                        throw new SAXException(
                                                "Parse error. Missing attribute value for attribute " + attname);
                                    case '"':
                                        readNext(in);
                                        attvalue = readToChar(in, "\"", true,
                                                false);
                                        last_attvalue = attvalue; //debugging
                                        if (nextChar == -1)
                                            throw new SAXException(
                                                    "Parse error.  Reached EOF inside tag '" + name + "'.");
                                        readNext(in);
                                        break;
                                    default:
                                        attvalue = readToChar(in, " \n>",
                                                false, false);
                                        last_attvalue = attvalue; //debugging
                                        break;
                                }
                                attrs.putAttribute(attname, attvalue);
                                if (skipWS(in, false) == -1)
                                    throw new SAXException(
                                            "Parse error.  Reached EOF inside tag '" + name + "'.");
                        }
                    }
                    startElement(name, attrs);
                    if (hasEnding)
                    {
                        endElement(name);
                    }
                    readNext(in);
                }
            }
            Exception error = pcxt.getException();
            pt.flush();
            pt.endPage();
            out.flush();
            if (error != null)
                throw error;
            if (pt instanceof PageTokenizer)
                return ((PageTokenizer) pt).getTokens();
            else
                return null;
        }
        catch (Exception e)
        {
            pt.pageError(out);
            if (e instanceof JspExceptionWrapper)
                e = ((JspExceptionWrapper) e).getWrappedException();
            StringBuffer buf = new StringBuffer();
            buf.append(e.getMessage());
            buf.append(": (");
            buf.append(row);
            buf.append(",");
            buf.append(col);
            buf.append(")\n");
            buf.append(line, 0, col < line.length ? col : line.length);
            buf.append("\n");
            for (int i = 0; i + 1 < (col < line.length ? col : line.length); i++)
            {
                if (line[i] == '\t')
                    buf.append("\t");
                else
                    buf.append('-');
            }
            buf.append('^');
            buf.append('\n');

            try
            {
                out.flush();
                PrintWriter pout = new PrintWriter(os);
                pout.write(buf.toString());
                e.printStackTrace(pout);
                pout.flush();
            }
            catch (IOException ioe)
            {
            }
        }
        return null;
    }

    private boolean fInQuote;
    private boolean fInJsp;
    private boolean fInScript;
    private int nextChar;
    private int row;
    private int col;
    private char[] line;
    private int origLinePos;

    private void initParser(Reader in) throws IOException
    {
        fInQuote = false;
        fInJsp = false;
        fInScript = false;
        row = 1;
        col = 0;
        line[0] = ' ';
        readNext(in);
    }

    private int readNext(Reader in) throws IOException
    {
        int ret = nextChar;
        col++;
        if (col <= line.length)
            line[col - 1] = (char) ret;
        if (nextChar == '\n')
        {
            row++;
            col = 0;
        }
        try
        {
            nextChar = in.read();
        }
        catch (IOException e)
        {
            nextChar = -1;
        }
        if (nextChar == '"')
            fInQuote = !fInQuote;
        return ret;
    }

    private int echoToChar(Reader in, ITagletHandler th, String c,
            boolean ignoreQuotes) throws IOException, JspException
    {
        while (true)
        {
            if (nextChar == -1)
                return -1;
            if ((!fInQuote || ignoreQuotes) && c.indexOf(nextChar) != -1)
                break;
            th.doPrint(nextChar, lastReturn);
            readNext(in);
        }
        return nextChar;
    }

    private int echoToChar(Reader in, ITagletHandler th, String c,
            boolean ignoreQuotes, boolean ignoreJsp) throws IOException,
            JspException
    {
        fInJsp = false;
        while (true)
        {
            if (nextChar == -1)
                break;
            if ((!fInQuote || ignoreQuotes) && c.indexOf(nextChar) != -1 && (!fInJsp || ignoreJsp))
                break;
            th.doPrint(nextChar, lastReturn);
            char last = (char) nextChar;
            readNext(in);
            if (last == '<' && nextChar == '%')
            {
                fInJsp = true;
                th.doPrint(nextChar, lastReturn);
                readNext(in);
            }
            else if (last == '%' && nextChar == '>' && fInJsp)
            {
                fInJsp = false;
                th.doPrint(nextChar, lastReturn);
                readNext(in);
            }
        }
        return nextChar;
    }

    private int echoWS(Reader in, ITagletHandler th, boolean ignoreQuotes)
            throws IOException, JspException
    {
        while (true)
        {
            if (nextChar == -1)
                return -1;
            if ((!fInQuote || ignoreQuotes) && (nextChar == ' ' || nextChar == '\n' || nextChar == '\t' || nextChar == '\r'))
                th.doPrint(nextChar, lastReturn);
            else
                break;
            readNext(in);
        }
        return nextChar;
    }

    private int skipWS(Reader in, boolean ignoreQuotes) throws IOException
    {
        while (true)
        {
            if (nextChar == -1)
                return -1;
            if ((fInQuote && !ignoreQuotes) || (nextChar != ' ' && nextChar != '\n' && nextChar != '\t' && nextChar != '\r'))
                break;
            readNext(in);
        }
        return nextChar;
    }

    private StringBuffer readToChar_buffer;

    private String readToChar(Reader in, String c, boolean ignoreQuotes)
            throws IOException
    {
        if (readToChar_buffer == null)
            readToChar_buffer = new StringBuffer();
        readToChar_buffer.setLength(0);
        while (true)
        {
            if (nextChar == -1)
                break;
            if ((!fInQuote || ignoreQuotes) && c.indexOf(nextChar) != -1)
                break;
            readToChar_buffer.append((char) nextChar);
            readNext(in);
        }
        return readToChar_buffer.toString();
    }

    private String readToChar(Reader in, String c, boolean ignoreQuotes,
            boolean ignoreJsp) throws IOException
    {
        fInJsp = false;
        if (readToChar_buffer == null)
            readToChar_buffer = new StringBuffer();
        readToChar_buffer.setLength(0);
        while (true)
        {
            if (nextChar == -1)
                break;
            if ((!fInQuote || ignoreQuotes) && c.indexOf(nextChar) != -1 && (!fInJsp || ignoreJsp))
                break;
            readToChar_buffer.append((char) nextChar);
            char last = (char) nextChar;
            readNext(in);
            if (last == '<' && nextChar == '%')
            {
                fInJsp = true;
                readToChar_buffer.append((char) nextChar);
                readNext(in);
            }
            else if (last == '%' && nextChar == '>' && fInJsp)
            {
                fInJsp = false;
                readToChar_buffer.append((char) nextChar);
                readNext(in);
            }
        }
        return readToChar_buffer.toString();
    }

    private String readToWS(Reader in, boolean ignoreQuotes) throws IOException
    {
        StringBuffer buf = new StringBuffer();
        while (true)
        {
            if (nextChar == -1)
                break;
            if ((!fInQuote || ignoreQuotes) && (nextChar == ' ' || nextChar == '\n' || nextChar == '\t'))
                break;
            buf.append((char) nextChar);
            readNext(in);
        }
        return buf.toString();
    }

    public void startElement(String name, AttributeList attrs)
            throws JspException
    {
        int pos = name.indexOf(':');
        TagInfo handler = null;
        if (pos != -1)
        {
            String prefix = name.substring(0, pos);
            TagLibraryInfo taglib = (TagLibraryInfo) namespaces.get(prefix.toLowerCase());
            if (taglib != null)
            {
                String tagname = name.substring(pos + 1);
                handler = taglib.getTag(tagname.toLowerCase());
            }
        }
        if (handler != null)
        {
            try
            {
                // initialize the tag
                pt.doInitTag(handler, null, name, lastReturn);

                // add attributes
                TagAttributeInfo tagattrs[] = handler.getAttributes();
                for (int i = 0; i < tagattrs.length; i++)
                {
                    String attrname = tagattrs[i].getName();
                    if (attrname.startsWith("___jsp"))
                        continue;
                    String attrvalue = attrs.getValue(attrname);
                    if (attrvalue == null && tagattrs[i].isRequired())
                        throw new JspException(
                                "Required attribute '" + attrname + "' is missing");
                    if (attrvalue != null)
                        pt.doTagAttribute(handler, null, attrname, attrvalue,
                                lastReturn);
                }

                lastReturn = pt.doStartTag(handler, null, lastReturn);
                pt.doStartBody(handler, null, lastReturn);
            }
            catch (JspException je)
            {
                throw je;
            }
            catch (Exception e)
            {
                throw new JspExceptionWrapper(e);
            }
        }
        else
        {
            // our custom handlers for HTML elements
            // currently defined: img, script, link, body

            //
            // first, add ourselves as the parent page
            //
            //String currentParentUrl = (String) cxt.getRequest().getAttribute(ContentServer.CS_INCLUDE_PARENT_URL);
            //String currentUrl = null;
            //if(cacheInfo != null)
            //	currentUrl = cacheInfo.getUrl();
            //if(currentUrl != null)
            //	cxt.getRequest().setAttribute(ContentServer.CS_INCLUDE_PARENT_URL, currentUrl);

            if (name.equalsIgnoreCase("img"))
            {
                // special case for image tags
                pt.doPrint("<IMG", lastReturn);
                if (attrs != null)
                {
                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        String val = attrs.getValue(i);
                        if (attrs.getName(i).startsWith("___jsp"))
                            pt.doPrint(val, lastReturn);
                        else if (attrs.getName(i).equalsIgnoreCase("src") && val.indexOf("<%") == -1 && val.indexOf("$") == -1)
                        {
                            if (val.trim().length() == 0)
                            {
                                // shouldn't try to load blank image
                                pt.doPrint(
                                        attrs.getName(i) + "=\"about:blank\"",
                                        lastReturn);
                            }
                            else
                            {
                                // should we perform a cache substitution for the url?
                                // check if the url is external, if not, then redirect it to the cached version
                                String url = val;
                                // TODO: Handle better
                                if (url != null)
                                    pt.doPrint(
                                            attrs.getName(i) + "=\"" + url + "\"",
                                            lastReturn);
                            }
                        }
                        else
                            pt.doPrint(attrs.getName(i) + "=\"" + val + "\"",
                                    lastReturn);
                    }
                }
                pt.doPrint('>', lastReturn);
            }
            else if (name.equalsIgnoreCase("script"))
            {
                // special case for script tags
                pt.doPrint("<SCRIPT", lastReturn);
                if (attrs != null)
                {
                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        String val = attrs.getValue(i);
                        if (attrs.getName(i).startsWith("___jsp"))
                            pt.doPrint(val, lastReturn);
                        else if (attrs.getName(i).equalsIgnoreCase("src") && val.indexOf("<%") == -1 && val.indexOf("$") == -1)
                        {
                            // substitute cached version -- only if there is no jsp included though.
                            if (val.trim().length() == 0)
                            {
                                // shouldn't try to load blank image
                                pt.doPrint(attrs.getName(i) + "=\"\"",
                                        lastReturn);
                            }
                            else
                            {
                                // should we perform a cache substitution for the url?
                                // check if the url is external, if not, then redirect it to the cached version
                                String url = val;
                                // TODO: Handle better
                                if (url != null)
                                    pt.doPrint(
                                            attrs.getName(i) + "=\"" + url + "\"",
                                            lastReturn);
                            }
                        }
                        else
                            pt.doPrint(attrs.getName(i) + "=\"" + val + "\"",
                                    lastReturn);
                    }
                }
                pt.doPrint('>', lastReturn);
            }
            else if (name.equalsIgnoreCase("link"))
            {
                // special case for link tags
                pt.doPrint("<LINK", lastReturn);
                if (attrs != null)
                {
                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        String val = attrs.getValue(i);
                        if (attrs.getName(i).startsWith("___jsp"))
                            pt.doPrint(val, lastReturn);
                        else if (attrs.getName(i).equalsIgnoreCase("href") && val.indexOf("<%") == -1 && val.indexOf("$") == -1)
                        {
                            // substitute cached version -- only if there is no jsp included though.
                            if (val.trim().length() == 0)
                            {
                                // shouldn't try to load blank image
                                pt.doPrint(attrs.getName(i) + "=\"\"",
                                        lastReturn);
                            }
                            else
                            {
                                // should we perform a cache substitution for the url?
                                // check if the url is external, if not, then redirect it to the cached version
                                String url = val;
                                // TODO: Handle better
                                if (url != null)
                                    pt.doPrint(
                                            attrs.getName(i) + "=\"" + url + "\"",
                                            lastReturn);
                            }
                        }
                        else
                            pt.doPrint(attrs.getName(i) + "=\"" + val + "\"",
                                    lastReturn);
                    }
                }
                pt.doPrint('>', lastReturn);
            }
            else if (name.equalsIgnoreCase("body"))
            {
                // special case for link tags
                pt.doPrint("<BODY", lastReturn);
                if (attrs != null)
                {
                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        String val = attrs.getValue(i);
                        if (attrs.getName(i).startsWith("___jsp"))
                            pt.doPrint(val, lastReturn);
                        else if (attrs.getName(i).equalsIgnoreCase("background") && val.indexOf("<%") == -1 && val.indexOf("$") == -1)
                        {
                            // substitute cached version -- only if there is no jsp included though.
                            if (val.trim().length() == 0)
                            {
                                // shouldn't try to load blank image
                                pt.doPrint(attrs.getName(i) + "=\"\"",
                                        lastReturn);
                            }
                            else
                            {
                                // should we perform a cache substitution for the url?
                                // check if the url is external, if not, then redirect it to the cached version
                                String url = val;
                                // TODO: Handle better
                                if (url != null)
                                    pt.doPrint(
                                            attrs.getName(i) + "=\"" + url + "\"",
                                            lastReturn);
                            }
                        }
                        else
                            pt.doPrint(attrs.getName(i) + "=\"" + val + "\"",
                                    lastReturn);
                    }
                }
                pt.doPrint('>', lastReturn);
            }
            else if (name.equalsIgnoreCase("input"))
            {
                // special case for input tags of type image (src=)
                pt.doPrint("<INPUT", lastReturn);
                if (attrs != null)
                {
                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        String val = attrs.getValue(i);
                        if (attrs.getName(i).startsWith("___jsp"))
                            pt.doPrint(val, lastReturn);
                        else if (attrs.getName(i).equalsIgnoreCase("src") && val.indexOf("<%") == -1 && val.indexOf("$") == -1)
                        {
                            // substitute cached version -- only if there is no jsp included though.
                            if (val.trim().length() == 0)
                            {
                                // shouldn't try to load blank image
                                pt.doPrint(attrs.getName(i) + "=\"\"",
                                        lastReturn);
                            }
                            else
                            {
                                // should we perform a cache substitution for the url?
                                // check if the url is external, if not, then redirect it to the cached version
                                String url = val;
                                // TODO: Handle better
                                if (url != null)
                                    pt.doPrint(
                                            attrs.getName(i) + "=\"" + url + "\"",
                                            lastReturn);
                            }
                        }
                        else
                            pt.doPrint(attrs.getName(i) + "=\"" + val + "\"",
                                    lastReturn);
                    }
                }
                pt.doPrint('>', lastReturn);
            }
            else if (name.equalsIgnoreCase("table"))
            {
                // special case for table tags (background)
                pt.doPrint("<TABLE", lastReturn);
                if (attrs != null)
                {
                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        String val = attrs.getValue(i);
                        if (attrs.getName(i).startsWith("___jsp"))
                            pt.doPrint(val, lastReturn);
                        else if (attrs.getName(i).equalsIgnoreCase("background") && val.indexOf("<%") == -1 && val.indexOf("$") == -1)
                        {
                            // substitute cached version -- only if there is no jsp included though.
                            if (val.trim().length() == 0)
                            {
                                // shouldn't try to load blank image
                                pt.doPrint(attrs.getName(i) + "=\"\"",
                                        lastReturn);
                            }
                            else
                            {
                                // should we perform a cache substitution for the url?
                                // check if the url is external, if not, then redirect it to the cached version
                                String url = val;
                                // TODO: Handle better
                                if (url != null)
                                    pt.doPrint(
                                            attrs.getName(i) + "=\"" + url + "\"",
                                            lastReturn);
                            }
                        }
                        else
                            pt.doPrint(attrs.getName(i) + "=\"" + val + "\"",
                                    lastReturn);
                    }
                }
                pt.doPrint('>', lastReturn);
            }
            else if (name.equalsIgnoreCase("a"))
            {
                // special case for anchors
                // if they have elected to enforce special anchor processing
                //				String enforcedTarget = cxt.getServerInstance().getServerSettings().getEnforcedAnchorDestination();
                //				boolean b_enforcedTarget = (enforcedTarget != null && !"".equals(enforcedTarget));
                boolean b_enforcedTarget = false;

                pt.doPrint("<A", lastReturn);
                if (attrs != null)
                {
                    boolean b_targetWritten = false;

                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        String val = attrs.getValue(i);
                        String atName = attrs.getName(i);

                        if ("target".equalsIgnoreCase(atName))
                        {
                            if (b_enforcedTarget)
                            {
                                b_targetWritten = true;
                            }
                        }

                        if (attrs.getName(i).startsWith("___jsp"))
                        {
                            pt.doPrint(val, lastReturn);
                        }
                        else
                        {
                            pt.doPrint(atName + "=\"" + val + "\"", lastReturn);
                        }
                    }

                    // if we're using enforced targets, then write the target tag
                    if (b_enforcedTarget && !b_targetWritten)
                    {
                        /*
                         pt.doPrint(' ', lastReturn);
                         pt.doPrint("target=\"" + enforcedTarget + "\"", lastReturn);
                         */
                    }
                }
                pt.doPrint('>', lastReturn);
            }
            else if (name.equalsIgnoreCase("param"))
            {
                // special case for PARAM tags
                pt.doPrint("<PARAM", lastReturn);
                if (attrs != null)
                {
                    boolean b_movieTag = false;
                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        String atVal = attrs.getValue(i);
                        String atName = attrs.getName(i);

                        if ("name".equalsIgnoreCase(atName))
                        {
                            if ("movie".equalsIgnoreCase(atVal))
                                b_movieTag = true;
                        }

                        if ("value".equalsIgnoreCase(atName))
                        {
                            if (b_movieTag)
                            {
                                // TODO: need to stream from cached location?
                            }
                        }

                        if (attrs.getName(i).startsWith("___jsp"))
                        {
                            pt.doPrint(atVal, lastReturn);
                        }
                        else
                        {
                            pt.doPrint(atName + "=\"" + atVal + "\"",
                                    lastReturn);
                        }
                    }
                }
                pt.doPrint('>', lastReturn);
            }
            else if (name.equalsIgnoreCase("embed"))
            {
                // special case for EMBED tags
                pt.doPrint("<EMBED", lastReturn);
                if (attrs != null)
                {
                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        String atVal = attrs.getValue(i);
                        String atName = attrs.getName(i);

                        if ("src".equalsIgnoreCase(atName))
                        {
                            // TODO: Change location of src?
                        }

                        if (attrs.getName(i).startsWith("___jsp"))
                        {
                            pt.doPrint(atVal, lastReturn);
                        }
                        else
                        {
                            pt.doPrint(atName + "=\"" + atVal + "\"",
                                    lastReturn);
                        }
                    }
                }
                pt.doPrint('>', lastReturn);
            }
            else
            {
                pt.doPrint("<" + name, lastReturn);
                if (attrs != null)
                {
                    for (int i = 0; i < attrs.getLength(); i++)
                    {
                        pt.doPrint(' ', lastReturn);
                        if (attrs.getName(i).startsWith("___jsp"))
                            pt.doPrint(attrs.getValue(i), lastReturn);
                        else
                            pt.doPrint(
                                    attrs.getName(i) + "=\"" + attrs.getValue(i) + "\"",
                                    lastReturn);
                    }
                }
                pt.doPrint('>', lastReturn);
            }

            // remove ourserlves as the parent page and restore the previous parent page
            //			if(currentParentUrl != null)
            //				cxt.getRequest().setAttribute(ContentServer.CS_INCLUDE_PARENT_URL, currentParentUrl);
            //			else
            //				cxt.getRequest().setAttribute(ContentServer.CS_INCLUDE_PARENT_URL, "");
        }
    }

    public void endElement(String name) throws JspException
    {
        int pos = name.indexOf(':');
        TagInfo handler = null;
        if (pos != -1)
        {
            String prefix = name.substring(0, pos);
            TagLibraryInfo taglib = (TagLibraryInfo) namespaces.get(prefix.toLowerCase());
            if (taglib != null)
            {
                String tagname = name.substring(pos + 1);
                handler = taglib.getTag(tagname.toLowerCase());
            }
        }
        if (handler != null)
        {
            lastReturn = pt.doEndBody(handler, null, name, lastReturn);
            lastReturn = pt.doEndTag(handler, null, name, lastReturn);
        }
        else
        {
            pt.doPrint("</" + name + ">", lastReturn);
        }
    }

    public void characters(char buf[], int offset, int len) throws JspException
    {
        String s = new String(buf, offset, len);
        try
        {
            out.write(buf, offset, len);
        }
        catch (IOException e)
        {
            throw new JspExceptionWrapper(e);
        }
    }

    public void error(SAXParseException e) throws SAXParseException
    {
        // Ignore errors -- document may not be well-formed
    }

    public TagLibraryInfo getNameSpace(String name)
    {
        return (TagLibraryInfo) namespaces.get(name);
    }

    public int getRow()
    {
        return row;
    }

    public int getColumn()
    {
        return col;
    }

    public String getLine()
    {
        return escapeString(new String(line, 0, col));
    }

    private String escapeString(String str)
    {
        StringTokenizer tok = new StringTokenizer(str, "\r\n\t\"\0%", true);
        StringBuffer ret = new StringBuffer();
        while (tok.hasMoreElements())
        {
            String token = tok.nextToken();
            if (token.length() == 1)
            {
                switch (token.charAt(0))
                {
                    case '\r':
                        ret.append("\\r");
                        break;
                    case '\n':
                        ret.append("\\n");
                        break;
                    case '\t':
                        ret.append("\\t");
                        break;
                    case '"':
                        ret.append("\\\"");
                        break;
                    case '\0':
                        ret.append("\\0");
                        break;
                    case '%':
                        ret.append("\\037");
                        break;
                }
            }
            else
            {
                ret.append(token);
            }
        }
        return ret.toString();
    }

    public static Document parse(String xml)
    {
        Document doc = null;
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);

            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

            InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
            doc = documentBuilder.parse(inputStream);
        }
        catch (Exception ex)
        {
        }
        return doc;
    }

    private JspWriter out;
    private Reader in;
    private ITagletHandler pt;
    private ArrayList outputStack;
    private ArrayList tags;
    private ArrayList tagnames;
    private ArrayList bodytags;
    private PageContext pcxt;
    private FilterContext cxt;
    private int lastReturn;
    private boolean inJsp;
    private static Hashtable namespaces = new Hashtable();
    private static boolean initted = false;
    //private CacheInfo cacheInfo;
    private String last_attname = "";
    private String last_attvalue = "";
}
