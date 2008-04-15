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
package org.alfresco.web.site.parser;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagInfo;

/**
 * The interface that a handler must implement to be used with TagletParser.  TagletParser will call the 
 * various handler methods in response to different parts of the document being parsed.  The class implementing
 * ITagletHandler is assumed to be stateful, and is only accessed by one thread at a time.  Output should go to the
 * out defined on the PageContext object provided.
 * 
 * @author muzquiano
 */
public interface ITagletHandler
{
    /**
     * Give the TagletHandler access to the IParser interface, so it can report error information with line numbers.
     */
    public void setParser(IParser parser);

    /**
     * Give the TagletHandler access to the PageContext
     */
    public void setPageContext(PageContext pc);

    /**
     * Called before parsing of the page begins.  Has access to the PageContext.
     */
    public void startPage() throws JspException;

    /**
     * Called after parsing of the page concludes.  Has access to the PageContext.
     */
    public void endPage() throws JspException;

    /**
     * Notification that a character should be printed.
     */
    public void doPrint(int c, int lastReturn) throws JspException;

    /**
     * Notification that a byte array should be printed.
     */
    public void doPrint(char data[], int lastReturn) throws JspException;

    /**
     * Notification that a byte array should be printed.
     */
    public void doPrint(char data[], int off, int len, int lastReturn)
            throws JspException;

    /**
     * Notification that a String should be printed.
     */
    public void doPrint(String s, int lastReturn) throws JspException;

    /**
     * Flush out any unfinished operations
     */
    public void flush();

    /**
     * Flush the accumulation buffer, if there is one.  In general, the characters from many doPrint() operations
     * will be aggregated until another operation occurs or a buffer limit is reached.
     */
    public void flushAccum(int lastReturn) throws JspException;

    /**
     * Initialize a new tag.  Called when a tag is first encountered.  Responsible for creating the tag, setting
     * the parent and PageContext.
     */
    public void doInitTag(TagInfo info, TagData td, String tagName,
            int lastReturn) throws JspException;

    /**
     * Set an attribute on the tag.  Called for each attribute encountered in the tag.
     */
    public void doTagAttribute(TagInfo info, TagData td, String attrName,
            String attrValue, int lastReturn) throws JspException;

    /**
     * Start tag execution.  Called after all attributes are set.
     */
    public int doStartTag(TagInfo info, TagData td, int lastReturn)
            throws JspException;

    /**
     * Start the body of a tag.  Called after doStartTag().  May not do anything if the tag is not a BodyTag.
     */
    public void doStartBody(TagInfo info, TagData td, int lastReturn)
            throws JspException;

    /**
     * End the body of a tag.  Called after the body of a tag, but before the end.  May return BodyTag.EVAL_BODY_TAG, 
     * which causes the body to be re-executed (followed by doEndBody()).
     */
    public int doEndBody(TagInfo info, TagData td, String tagname,
            int lastReturn) throws JspException;

    /**
     * End a tag.  Called after every other tag command.
     */
    public int doEndTag(TagInfo info, TagData td, String tagname, int lastReturn)
            throws JspException;

    /**
     * Report an error on the page.
     */
    public void pageError(JspWriter out);
}
