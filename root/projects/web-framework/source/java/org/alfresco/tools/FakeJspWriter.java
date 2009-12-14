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
package org.alfresco.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;

import org.springframework.extensions.surf.util.StringBuilderWriter;

/**
 * akeJspWriter.
 */
public class FakeJspWriter extends JspWriter
{
    /** The root writer. */
    private Writer rootWriter = null;
    
    /** A print writer that wraps the root writer */
    private PrintWriter printWriter = null;

    /**
     * Instantiates a new fake jsp writer.
     */
    public FakeJspWriter() {
        super(0, false);
        
        rootWriter = new StringBuilderWriter(512);
        printWriter = new PrintWriter(rootWriter);
    }

    /**
     * Instantiates a new fake jsp writer.
     * 
     * @param myWriter
     *            the my writer
     */
    public FakeJspWriter(Writer myWriter) {
        super(0, false);

        this.rootWriter = myWriter;
        printWriter = new PrintWriter(myWriter);
    }
    
    /**
     * Instantiates a new fake jsp writer.
     * 
     * @param arg0
     *            the arg0
     * @param arg1
     *            the arg1
     */
    public FakeJspWriter(int arg0, boolean arg1) {
        super(arg0, arg1);

        rootWriter = new StringBuilderWriter(512);
        printWriter = new PrintWriter(rootWriter);        
    }

    /**
     * Gets the print writer.
     * 
     * @return the print writer
     */
    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    /**
     * Gets the root writer.
     * 
     * @return the root writer
     */
    public Writer getRootWriter() {
        return rootWriter;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#newLine()
     */
    @Override
    public void newLine() throws IOException {
        printWriter.println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(boolean)
     */
    @Override
    public void print(boolean arg0) throws IOException {
        printWriter.print(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(char)
     */
    @Override
    public void print(char arg0) throws IOException {
        printWriter.print(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(int)
     */
    @Override
    public void print(int arg0) throws IOException {
        printWriter.print(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(long)
     */
    @Override
    public void print(long arg0) throws IOException {
        printWriter.print(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(float)
     */
    @Override
    public void print(float arg0) throws IOException {
        printWriter.print(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(double)
     */
    @Override
    public void print(double arg0) throws IOException {
        printWriter.print(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(char[])
     */
    @Override
    public void print(char[] arg0) throws IOException {
        printWriter.print(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(java.lang.String)
     */
    @Override
    public void print(String arg0) throws IOException {
        printWriter.print(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(java.lang.Object)
     */
    @Override
    public void print(Object arg0) throws IOException {
        printWriter.print(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println()
     */
    @Override
    public void println() throws IOException {
        printWriter.println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(boolean)
     */
    @Override
    public void println(boolean arg0) throws IOException {
        printWriter.println(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(char)
     */
    @Override
    public void println(char arg0) throws IOException {
        printWriter.println(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(int)
     */
    @Override
    public void println(int arg0) throws IOException {
        printWriter.println(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(long)
     */
    @Override
    public void println(long arg0) throws IOException {
        printWriter.println(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(float)
     */
    @Override
    public void println(float arg0) throws IOException {
        printWriter.println(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(double)
     */
    @Override
    public void println(double arg0) throws IOException {
        printWriter.println(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(char[])
     */
    @Override
    public void println(char[] arg0) throws IOException {
        printWriter.println(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(java.lang.String)
     */
    @Override
    public void println(String arg0) throws IOException {
        printWriter.println(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(java.lang.Object)
     */
    @Override
    public void println(Object arg0) throws IOException {
        printWriter.println(arg0);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#clear()
     */
    @Override
    public void clear() throws IOException {
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#clearBuffer()
     */
    @Override
    public void clearBuffer() throws IOException {
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#flush()
     */
    @Override
    public void flush() throws IOException {
        printWriter.flush();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#close()
     */
    @Override
    public void close() throws IOException {
        printWriter.close();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#getRemaining()
     */
    @Override
    public int getRemaining() {
        return 0;
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        printWriter.write(cbuf, off, len);
    }

}