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
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

import org.alfresco.tools.Array;
import org.alfresco.tools.ArrayIterator;

/**
 * @author muzquiano
 */
public class JspWriterImpl extends JspWriter
{
    static int DEFAULT_BUFFER_SIZE = 10240;

    public JspWriterImpl(PrintWriter out, int bufSize, boolean autoFlush)
    {
        super(bufSize, autoFlush);
        if (bufferSize == JspWriter.DEFAULT_BUFFER)
            bufferSize = DEFAULT_BUFFER_SIZE;
        writers = new Array();
        pw = out;
        this.out = new TagBodyContentImpl(this);
    }

    public JspWriterImpl(JspWriter out, int bufSize, boolean autoFlush)
    {
        super(bufSize, autoFlush);
        if (bufferSize == JspWriter.DEFAULT_BUFFER)
            bufferSize = DEFAULT_BUFFER_SIZE;
        writers = new Array();
        this.out = out;
    }

    public void write(String s) throws IOException
    {
        checkOpen();
        out.write(s);
    }

    public void write(int i) throws IOException
    {
        checkOpen();
        out.write(i);
    }

    public void write(char c[], int off, int len) throws IOException
    {
        checkOpen();
        out.write(c, off, len);
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

    public void close() throws IOException
    {
        checkOpen();
        flush();
        Writer top = (Writer) writers.front();
        top.close();
        out = null;
    }

    public void flush() throws IOException
    {
        out.flush();
        if (out instanceof BodyContent)
        {
            if (!writers.isEmpty())
            {
                ((BodyContent) out).writeOut(((BodyContent) out).getEnclosingWriter());
                ((BodyContent) out).clear();
            }
            else if (pw != null)
            {
                ((BodyContent) out).writeOut(pw);
                ((BodyContent) out).clear();
                pw.flush();
            }
        }
    }

    protected void flushBuffer() throws IOException
    {
    }

    protected void flushAll() throws IOException
    {
        if (!writers.isEmpty())
        {
            for (ArrayIterator iter = writers.end(); !iter.atBegin(); iter.retreat())
            {
                JspWriter cur = (JspWriter) iter.get();
                if (cur instanceof BodyContent)
                {
                    ((BodyContent) cur).writeOut(((BodyContent) cur).getEnclosingWriter());
                    ((BodyContent) cur).clear();
                }
                else
                {
                    cur.flush();
                }
            }
        }
        out.flush();
    }

    public int getRemaining()
    {
        return out.getRemaining();
    }

    public void clear() throws IOException
    {
        if (flushed)
            throw new IOException("Can't clear flushed buffer");
        clearBuffer();
    }

    public void clearBuffer() throws IOException
    {
        checkOpen();
        out.clear();
        for (ArrayIterator iter = writers.begin(); !iter.atEnd(); iter.advance())
            ((JspWriter) iter.get()).clear();
    }

    protected BodyContent pushWriter()
    {
        writers.pushBack(out);
        out = new TagBodyContentImpl(out);
        return (BodyContent) out;
    }

    protected JspWriter popWriter()
    {
        out = (JspWriter) writers.popBack();
        return out;
    }

    static String lineSeparator = System.getProperty("line.separator");

    public void newLine() throws IOException
    {
        write(lineSeparator);
    }

    private void checkOpen() throws IOException
    {
        if (out == null)
            throw new IOException("Stream closed");
    }

    private JspWriter out;
    private PrintWriter pw;
    private Array writers;
    private int index;
    private boolean flushed = false;
}
