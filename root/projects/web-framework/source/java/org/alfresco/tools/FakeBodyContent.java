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

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

/**
 * Implementation of the JSP BodyContent abstract class.  BodyContent 
 * extends JspWriter to allow access to the underlying buffer.
 * 
 * The buffer can be cleared, converted to a string, 
 * or read through a Reader.  It also has the notion of an enclosed
 * writer, which is in essence a parent BodyContent.
 * 
 * Finally, it has a writeOut method which allows for efficiently 
 * writing its contents to its parent (or another writer).
 * 
 * @author muzquiano
 */
public class FakeBodyContent extends BodyContent
{
    static int DEFAULT_BUFFER_SIZE = 1024;

    public FakeBodyContent(JspWriter encl)
    {
        super(encl);
        this.enclosingWriter = encl;
        if (bufferSize == JspWriter.DEFAULT_BUFFER)
            bufferSize = DEFAULT_BUFFER_SIZE;
        else if (bufferSize == JspWriter.UNBOUNDED_BUFFER)
        {
            bufferSize = DEFAULT_BUFFER_SIZE;
            unbounded = true;
        }
        buffer = new char[bufferSize];
        index = 0;
    }

    /**
     * Write the String s to the buffer
     */
    public void write(String s) throws IOException
    {
        // prevent NPEs
        if (s == null)
            s = "null";

        if (bufferSize == 0)
        {
            // write straight to the Writer contained in the parent JspWriter
            // not actually sure what good this does, but this case never happens anyways
            out.write(s);
        }
        else
        {
            if (s.length() == 0)
            {
                return;
            }

            // make sure there is enough room
            if (index + s.length() >= bufferSize)
            {
                growBuffer(index + s.length());
            }

            // copy the characters from s into the buffer
            s.getChars(0, s.length(), buffer, index);
            index += s.length();
        }
    }

    /**
     * Write the character represented by the integer i to the buffer
     */
    public void write(int i) throws IOException
    {
        if (bufferSize == 0)
        {
            // write straight to the Writer contained in the parent JspWriter
            // not actually sure what good this does, but this case never happens anyways
            out.write(i);
        }
        else
        {
            // make sure there is enough room
            if (index >= bufferSize)
            {
                growBuffer(bufferSize + 1);
            }

            buffer[index++] = (char) i;
        }
    }

    /**
     * Write an array of characters to the buffer
     */
    public void write(char c[], int off, int len) throws IOException
    {
        if (bufferSize == 0)
        {
            // write straight to the Writer contained in the parent JspWriter
            // not actually sure what good this does, but this case never happens anyways
            out.write(c, off, len);
        }
        else
        {
            int end = off + len;
            // make sure the offset and length parameters are valid
            if ((off < 0) || (off > c.length) || (len < 0) || (end > c.length) || ((end) < 0))
            {
                throw new IndexOutOfBoundsException();
            }
            else if (len == 0)
            {
                return;
            }

            // make sure there is enough space
            if (index + len >= bufferSize)
            {
                growBuffer(index + len);
            }
            System.arraycopy(c, off, buffer, index, len);
            index += len;
        }
    }

    /**
     * Ensure that at least minLength bytes are available in the buffer total.
     */
    private void growBuffer(int minLength)
    {
        // grow by a factor of two, or minLength, whichever is greater
        int newLength = Math.max(minLength, bufferSize * 2);
        char newBuf[] = new char[newLength];
        System.arraycopy(buffer, 0, newBuf, 0, index);
        buffer = newBuf;
        bufferSize = newBuf.length;
    }

    public void print(char c) throws IOException
    {
        write((int) c);
    }

    public void print(double d) throws IOException
    {
        write(Double.toString(d));
    }

    public void print(boolean b) throws IOException
    {
        write(new Boolean(b).toString());
    }

    public void print(long l) throws IOException
    {
        write(Long.toString(l));
    }

    public void print(float f) throws IOException
    {
        write(Float.toString(f));
    }

    public void print(int i) throws IOException
    {
        write(Integer.toString(i));
    }

    public void print(Object o) throws IOException
    {
        write(o.toString());
    }

    public void print(char c[]) throws IOException
    {
        write(c, 0, c.length);
    }

    public void print(String s) throws IOException
    {
        write(s);
    }

    public void println() throws IOException
    {
        newLine();
    }

    public void println(String s) throws IOException
    {
        print(s);
        println();
    }

    public void println(char c) throws IOException
    {
        print(c);
        println();
    }

    public void println(char c[]) throws IOException
    {
        print(c);
        println();
    }

    public void println(long l) throws IOException
    {
        print(l);
        println();
    }

    public void println(int i) throws IOException
    {
        print(i);
        println();
    }

    public void println(double d) throws IOException
    {
        print(d);
        println();
    }

    public void println(float f) throws IOException
    {
        print(f);
        println();
    }

    public void println(boolean b) throws IOException
    {
        print(b);
        println();
    }

    public void println(Object o) throws IOException
    {
        print(o);
        println();
    }

    /**
     * Close is a no-op in BodyContent
     */
    public void close() throws IOException
    {
    }

    /**
     * Flush is a no-op in BodyContent, since you have to explicitly write its contents to the enclosing writer.
     */
    public void flush() throws IOException
    {
    }

    /**
     * Return remaining size in the buffer.  This shouldn't be a concern, since this implementation always
     * grows the buffer.
     */
    public int getRemaining()
    {
        return bufferSize - index;
    }

    /**
     * Clear the contents of the buffer, unless it was flushed
     */
    public void clear() throws IOException
    {
        if (flushed)
            throw new IOException("Can't clear flushed buffer");
        clearBuffer();
    }

    /**
     * Clear the contents of the buffer
     */
    public void clearBuffer() throws IOException
    {
        if (bufferSize == 0)
            throw new IllegalStateException("No buffer set");
        index = 0;
    }

    static String lineSeparator = System.getProperty("line.separator");

    /**
     * Add a newline to the buffer
     */
    public void newLine() throws IOException
    {
        write(lineSeparator);
    }

    /**
     * Return the value of this BodyContent as a Reader.
     * Note: this is after evaluation!!  There are no scriptlets,
     * etc in this stream.
     *
     * @returns the value of this BodyContent as a Reader
     */
    public Reader getReader()
    {
        if (flushed)
            throw new IllegalStateException(
                    "The stream has already been flushed");
        return new CharArrayReader(buffer, 0, index);
    }

    /**
     * Return the value of the BodyContent as a String.
     * Note: this is after evaluation!!  There are no scriptlets,
     * etc in this stream.
     *
     * @returns the value of the BodyContent as a String
     */
    public String getString()
    {
        if (flushed)
            throw new IllegalStateException(
                    "The stream has already been flushed");
        return new String(buffer, 0, index);
    }

    /**
     * Write the contents of this BodyContent into a Writer.
     * Subclasses are likely to do interesting things with the
     * implementation so some things are extra efficient.
     *
     * @param out The writer into which to place the contents of
     * this body evaluation
     */
    public void writeOut(Writer out) throws IOException
    {
        if (flushed)
            throw new IllegalStateException(
                    "The stream has already been flushed");
        out.write(buffer, 0, index);
    }

    /**
     * Get the enclosing JspWriter
     *
     * @returns the enclosing JspWriter passed at construction time
     */
    public JspWriter getEnclosingWriter()
    {
        return enclosingWriter;
    }

    protected void setEnclosingWriter(JspWriter encl)
    {
        this.enclosingWriter = encl;
    }

    /**
     * private fields
     */

    private JspWriter enclosingWriter;
    private PrintWriter out;
    private char buffer[];
    private int index;
    private boolean flushed = false;
    public boolean unbounded = false;
}
