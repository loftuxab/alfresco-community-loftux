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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagInfo;

import org.alfresco.tools.Array;
import org.alfresco.web.site.parser.IParser;
import org.alfresco.web.site.parser.ITagletHandler;

/**
 * @author muzquiano
 */
public class PageTokenizer implements ITagletHandler
{
    private static final long serialVersionUID = 1144161358406531996L;

    public PageTokenizer()
    {
        tags = new Array();
        tagnames = new Array();
        bodytags = new Array();
        //		recycleBC = new Array();
        recycleTags = new Hashtable();
        tokens = new TokenStream();
        reader = tokens.getTokenStreamReader();
        accumBuffer = new char[1024];
        index = 0;
        inAccum = false;
        skipTo = null;
        backTag = null;
    }

    /**
     * Give the TagletHandler access to the IParser interface, so it can report error information with line numbers.
     */
    public void setParser(IParser parser)
    {
        this.parser = parser;
    }

    /**
     * Give the TagletHandler access to the PageContext
     */
    public void setPageContext(PageContext pc)
    {
        this.pc = pc;
        out = pc.getOut();
    }

    /**
     * Called before parsing of the page begins.  Has access to the PageContext.
     */
    public void startPage() throws JspException
    {
        pc.setAttribute("TOKEN_STREAM", tokens);
    }

    /**
     * Called after parsing of the page concludes.  Has access to the PageContext.
     */
    public void endPage() throws JspException
    {
        pc.removeAttribute("TOKEN_STREAM");
        if (!tagnames.isEmpty())
            throw new JspException(
                    "Reached EOF before " + tagnames.back() + " tag was closed.");
    }

    /**
     * Report an error on the page.
     */
    public void pageError(JspWriter out)
    {
        // don't need to do anything here
    }

    /**
     * Notification that a character should be printed.
     */
    public void doPrint(int c, int lastReturn) throws JspException
    {
        if (index >= accumBuffer.length)
        {
            flushAccum(lastReturn);
        }
        accumBuffer[index] = (char) c;
        index++;
        inAccum = true;
    }

    /**
     * Notification that a byte ArrayList should be printed.
     */
    public void doPrint(char data[], int lastReturn) throws JspException
    {
        int len = data.length;
        if (len > accumBuffer.length)
        {
            // for big data, just put the a token in the stream directly
            // be sure to flush first
            flushAccum(lastReturn);
            char copy[] = new char[len];
            System.arraycopy(data, 0, copy, 0, len);
            Token tok = new Token(Token.PRINT, copy);
            callPrint(tok, lastReturn);
        }
        else
        {
            // for small data, try to fill the buffer
            int i = 0;
            while (i < len)
            {
                int end = Math.min(index + len, accumBuffer.length);
                int size = end - index;
                System.arraycopy(data, i, accumBuffer, index, size);
                i += size;
                index += size;
                if (index >= accumBuffer.length)
                    flushAccum(lastReturn);
            }
        }
        inAccum = true;
    }

    /**
     * Notification that a byte ArrayList should be printed.
     */
    public void doPrint(char data[], int off, int len, int lastReturn)
            throws JspException
    {
        if (len > accumBuffer.length)
        {
            // for big data, just put the a token in the stream directly
            // be sure to flush first
            flushAccum(lastReturn);
            char copy[] = new char[len];
            System.arraycopy(data, off, copy, 0, len);
            Token tok = new Token(Token.PRINT, copy);
            callPrint(tok, lastReturn);
        }
        else
        {
            // for small data, try to fill the buffer
            int i = off;
            while (i < len)
            {
                int end = Math.min(index + len - i, accumBuffer.length);
                int size = end - index;
                System.arraycopy(data, i, accumBuffer, index, size);
                i += size;
                index += size;
                if (index >= accumBuffer.length)
                    flushAccum(lastReturn);
            }
        }
        inAccum = true;
    }

    /**
     * Notification that a String should be printed.
     */
    public void doPrint(String s, int lastReturn) throws JspException
    {
        int len = s.length();
        if (len > accumBuffer.length)
        {
            // for big data, just put the a token in the stream directly
            // be sure to flush first
            flushAccum(lastReturn);
            char copy[] = new char[len];
            s.getChars(0, s.length(), copy, 0);
            Token tok = new Token(Token.PRINT, copy);
            callPrint(tok, lastReturn);
        }
        else
        {
            // for small data, try to fill the buffer
            int i = 0;
            while (i < len)
            {

                int end = Math.min(index + len - i, accumBuffer.length);
                int size = end - index;
                s.getChars(i, i + size, accumBuffer, index);
                i += size;
                index += size;
                if (index >= accumBuffer.length)
                    flushAccum(lastReturn);
            }
        }
        inAccum = true;
    }

    /**
     * Flush out any unfinished operations
     */
    public void flush()
    {
        try
        {
            flushAccum(Tag.EVAL_PAGE);
        }
        catch (JspException e)
        {
        }
    }

    /**
     * Flush the accumulation buffer, if there is one.  In general, the characters from many doPrint() operations
     * will be aggregated until another operation occurs or a buffer limit is reached.
     */
    public void flushAccum(int lastReturn) throws JspException
    {
        if (!inAccum || index == 0)
            return;
        char copy[] = new char[index];
        System.arraycopy(accumBuffer, 0, copy, 0, index);
        index = 0;
        inAccum = false;
        Token tok = new Token(Token.PRINT, copy);
        tokens.putToken(tok);
        reader.nextToken();
        callPrint(tok, lastReturn);
    }

    public void callPrint(Token tok, int lastReturn) throws JspException
    {
        switch (lastReturn)
        {
            case Tag.SKIP_BODY:
            case Tag.SKIP_PAGE:
                return;
            default:
                try
                {
                    out.print((char[]) tok.getData());
                }
                catch (IOException e)
                {
                    throw new JspExceptionWrapper(e);
                }
                break;
        }
    }

    /**
     * Initialize a new tag.  Called when a tag is first encountered.  Responsible for creating the tag, setting
     * the parent and PageContext.
     */
    public void doInitTag(TagInfo info, TagData td, String tagName,
            int lastReturn) throws JspException
    {
        flushAccum(lastReturn);
        Token tok = new Token(Token.INIT_TAG, tagName, info.getTagClassName());
        tokens.putToken(tok);
        reader.nextToken();
        callInitTag(tok, lastReturn);
    }

    public void callInitTag(Token tok, int lastReturn) throws JspException
    {
        String name = tok.getTagName();
        String className = (String) tok.getData();
        Tag tag = getTag(className);
        tag.setPageContext(pc);
        tag.setParent(backTag);

        // REVIEW: keep separate back tag
        tags.pushBack(backTag);
        backTag = tag;
        tagnames.pushBack(name);
    }

    /**
     * Set an attribute on the tag.  Called for each attribute encountered in the tag.
     */
    public void doTagAttribute(TagInfo info, TagData td, String attrName,
            String attrValue, int lastReturn) throws JspException
    {
        flushAccum(lastReturn);
        Method method;
        try
        {
            method = backTag.getClass().getMethod(
                    "set" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1),
                    new Class[] { String.class });
        }
        catch (NoSuchMethodException nsme)
        {
            throw new JspExceptionWrapper(nsme);
        }
        Token tok = new Token(Token.TAG_ATTRIBUTE, method,
                new Object[] { attrValue });
        tokens.putToken(tok);
        reader.nextToken();
        callTagAttribute(tok, lastReturn);
    }

    public void callTagAttribute(Token tok, int lastReturn) throws JspException
    {
        switch (lastReturn)
        {
            case Tag.SKIP_BODY:
            case Tag.SKIP_PAGE:
                return;
            default:
                try
                {
                    tok.getMethod().invoke(backTag, (Object[]) tok.getData());
                }
                catch (IllegalAccessException iae)
                {
                    throw new JspExceptionWrapper(iae);
                }
                catch (InvocationTargetException ite)
                {
                    throw new JspExceptionWrapper(ite);
                }
                break;
        }
    }

    /**
     * Start tag execution.  Called after all attributes are set.
     */
    public int doStartTag(TagInfo info, TagData td, int lastReturn)
            throws JspException
    {
        flushAccum(lastReturn);
        Token tok = new Token(Token.START_TAG);
        tokens.putToken(tok);
        reader.nextToken();
        return callStartTag(tok, lastReturn);
    }

    public int callStartTag(Token tok, int lastReturn) throws JspException
    {
        switch (lastReturn)
        {
            case Tag.SKIP_BODY:
            case Tag.SKIP_PAGE:
                return lastReturn;
            default:
                int ret = -1;
                ret = backTag.doStartTag();
                if (ret == Tag.SKIP_BODY)
                    skipTo = backTag;
                return ret;
        }
    }

    /**
     * Start the body of a tag.  Called after doStartTag().  May not do anything if the tag is not a BodyTag.
     */
    public void doStartBody(TagInfo info, TagData td, int lastReturn)
            throws JspException
    {
        if (!(backTag instanceof BodyTag))
            return;
        flushAccum(lastReturn);
        Token tok = new Token(Token.START_BODY);
        tokens.putToken(tok);
        reader.nextToken();
        callStartBody(tok, lastReturn);
        reader.mark();
    }

    public void callStartBody(Token tok, int lastReturn) throws JspException
    {
        switch (lastReturn)
        {
            case Tag.SKIP_BODY:
                return;
            case Tag.EVAL_BODY_INCLUDE:
                break;
            case BodyTag.EVAL_BODY_TAG:
                bodytags.pushBack(backTag);
                BodyContent bc = ((JspPageContextImpl) pc).pushBody();
                ((BodyTag) backTag).setBodyContent(bc);
                ((BodyTag) backTag).doInitBody();
                break;
        }
    }

    /**
     * End the body of a tag.  Called after the body of a tag, but before the end.  May return BodyTag.EVAL_BODY_TAG, 
     * which causes the body to be re-executed (followed by doEndBody()).
     */
    public int doEndBody(TagInfo info, TagData td, String tagname,
            int lastReturn) throws JspException
    {
        if (!(backTag instanceof BodyTag))
            return lastReturn;
        flushAccum(lastReturn);
        Token tok = new Token(Token.END_BODY, tagname);
        tokens.putToken(tok);
        reader.nextToken();
        int ret = callEndBody(tok, lastReturn);
        if (ret == BodyTag.EVAL_BODY_TAG)
        {
            reader.reset();
            reader.mark();
            ret = execute(reader, lastReturn);
        }
        else
            reader.popMark();
        return ret;
    }

    public int callEndBody(Token tok, int lastReturn) throws JspException
    {
        switch (lastReturn)
        {
            case Tag.SKIP_BODY:
            case Tag.SKIP_PAGE:
                return lastReturn;
            default:
                String tagname = (String) tagnames.back();
                if (!tok.getTagName().equals(tagname))
                    throw new JspException(
                            "Unbalanced tags.  Expecting " + tagname + " but got " + tok.getTagName() + ".");
                if (!bodytags.isEmpty() && backTag.equals((BodyTag) bodytags.back()))
                {
                    int ret = Tag.EVAL_PAGE;
                    ret = ((BodyTag) backTag).doAfterBody();
                    if (ret == BodyTag.EVAL_BODY_TAG)
                    {
                        return BodyTag.EVAL_BODY_TAG;
                    }
                    ((JspPageContextImpl) pc).popBody();
                    bodytags.popBack();
                    return Tag.EVAL_PAGE;
                }
                return Tag.EVAL_PAGE;
        }
    }

    /**
     * End a tag.  Called after every other tag command.
     */
    public int doEndTag(TagInfo info, TagData td, String tagname, int lastReturn)
            throws JspException
    {
        flushAccum(lastReturn);
        Token tok = new Token(Token.END_TAG, tagname);
        tokens.putToken(tok);
        reader.nextToken();
        return callEndTag(tok, lastReturn);
    }

    public int callEndTag(Token tok, int lastReturn) throws JspException
    {
        String tagname = (String) tagnames.popBack();
        if (!tok.getTagName().equals(tagname))
            throw new JspException(
                    "Unbalanced tags.  Expecting " + tagname + " but got " + tok.getTagName() + ".");
        switch (lastReturn)
        {
            case Tag.SKIP_PAGE:
                return lastReturn;
            case Tag.SKIP_BODY:
                if (!backTag.equals(skipTo))
                {
                    releaseTag(backTag);
                    if (tags.isEmpty())
                        backTag = null;
                    else
                        backTag = (Tag) tags.popBack();
                    return lastReturn;
                }
            default:
                int ret = Tag.EVAL_PAGE;
                ret = backTag.doEndTag();
                releaseTag(backTag);
                if (tags.isEmpty())
                    backTag = null;
                else
                    backTag = (Tag) tags.popBack();
                return ret;
        }
    }

    /**
     * Return the token stream created while parsing this document.
     */
    public TokenStream getTokens() throws JspException
    {
        // make sure any pending operations are recorded in the token stream.
        flushAccum(0);
        return tokens;
    }

    /**
     * This is the publically callable execute method.  It takes a TokenStream produced by a previous run of
     * PageTokenizer and re-executes it.
     */
    public int execute(TokenStreamReader ts) throws JspException
    {
        // should call startPage and endPage here.
        startPage();
        int ret = execute(ts, Tag.EVAL_PAGE);
        endPage();
        return ret;
    }

    /**
     * This method should only be called internally
     */
    private int execute(TokenStreamReader ts, int lastReturn)
            throws JspException
    {
        int ret = lastReturn;
        while (ts.hasMoreTokens())
        {
            Token tok = ts.nextToken();
            switch (tok.getCommand())
            {
                case Token.PRINT:
                    callPrint(tok, ret);
                    break;
                case Token.INIT_TAG:
                    callInitTag(tok, ret);
                    break;
                case Token.TAG_ATTRIBUTE:
                    callTagAttribute(tok, ret);
                    break;
                case Token.START_TAG:
                    ret = callStartTag(tok, ret);
                    break;
                case Token.START_BODY:
                    callStartBody(tok, ret);
                    ts.mark();
                    break;
                case Token.END_BODY:
                    ret = callEndBody(tok, ret);
                    if (ret == BodyTag.EVAL_BODY_TAG)
                    {
                        ts.reset();
                        ts.mark();
                    }
                    else
                        ts.popMark();
                    break;
                case Token.END_TAG:
                    ret = callEndTag(tok, ret);
                    break;
                default:
                    throw new JspException("Invalid token in stream");
            }
        }
        return ret;
    }

    private Tag getTag(String className) throws JspException
    {
        Tag tag = (Tag) recycleTags.get(className);
        if (tag == null)
        {
            try
            {
                tag = (Tag) Class.forName(className).newInstance();
            }
            catch (ClassNotFoundException cnfe)
            {
                throw new JspExceptionWrapper(cnfe);
            }
            catch (InstantiationException ie)
            {
                throw new JspExceptionWrapper(ie);
            }
            catch (IllegalAccessException iae)
            {
                throw new JspExceptionWrapper(iae);
            }
        }
        else
        {
            recycleTags.remove(className);
        }
        return tag;
    }

    private void releaseTag(Tag tag)
    {
        tag.release();
        recycleTags.put(tag.getClass().getName(), tag);
    }

    public JspWriter getOut()
    {
        return out;
    }

    public Tag getBackTag()
    {
        return backTag;
    }

    private char accumBuffer[];
    private int index;
    private boolean inAccum;
    private Tag skipTo;
    private TokenStream tokens;
    private TokenStreamReader reader;
    private PageContext pc;
    private Array tags;
    private Tag backTag;
    private Array tagnames;
    private Array bodytags;
    private JspWriter out;
    private IParser parser;

    // object pooling
    //	private Array recycleBC;
    private Hashtable recycleTags;

}
