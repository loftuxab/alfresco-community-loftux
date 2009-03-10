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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Implementation of a Fake HttpServletResponse object which can be used to trap output from dispatched objects into a
 * buffer and then deal with results at a later time. Useful for page caching or nested components where the output
 * stream must be committed to real response at a undetermined point in the future.
 * 
 * @author muzquiano
 */
public class FakeHttpServletResponse extends HttpServletResponseWrapper
{
    public FakeHttpServletResponse(HttpServletResponse wrapped)
    {
        this(wrapped, false);
    }

    public FakeHttpServletResponse(HttpServletResponse wrapped, boolean fakeOutputStream)
    {
        super(wrapped);
        if (!fakeOutputStream)
        {
            this.content = new ByteArrayOutputStream(1024);
            this.outputStream = new DelegatingServletOutputStream(this.content);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
     */
    @Override
    public void setCharacterEncoding(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding()
    {
        return this.characterEncoding;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getOutputStream()
     */
    @Override
    public ServletOutputStream getOutputStream()
    {
        return this.outputStream;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getWriter()
     */
    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException
    {
        if (this.writer == null)
        {
            Writer targetWriter;
            if (this.outputStream != null)
            {
                // create a writer wrapping the underlying content output stream
                targetWriter = this.characterEncoding != null ? new OutputStreamWriter(this.content,
                        this.characterEncoding) : new OutputStreamWriter(this.content);
            }
            else
            {
                // create a "fake" or dummy writer as we have no output stream
                targetWriter = new Writer()
                {
                    @Override
                    public void write(char[] cbuf, int off, int len) throws IOException
                    {
                    }

                    @Override
                    public void flush() throws IOException
                    {
                    }

                    @Override
                    public void close() throws IOException
                    {
                    }
                };
            }

            this.writer = new PrintWriter(targetWriter, true);
        }

        return this.writer;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#flushBuffer()
     */
    @Override
    public void flushBuffer()
    {
        if (this.writer != null)
        {
            this.writer.flush();
        }

        if (this.outputStream != null)
        {
            try
            {
                this.outputStream.flush();
            }
            catch (IOException ex)
            {
                throw new IllegalStateException("Could not flush OutputStream: " + ex.getMessage());
            }
        }

        this.committed = true;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
     */
    @Override
    public void sendError(int newStatus, String newErrorMessage) throws IOException
    {
        if (this.committed)
        {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }

        this.status = newStatus;
        this.errorMessage = newErrorMessage;
        this.committed = true;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    @Override
    public void sendError(int newStatus) throws IOException
    {
        if (this.committed)
        {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }

        this.status = newStatus;
        this.committed = true;
    }

    /**
     * Gets the error message.
     * 
     * @return the error message
     */
    public String getErrorMessage()
    {

        return this.errorMessage;

    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
     */
    @Override
    public void sendRedirect(String url) throws IOException
    {
        if (this.committed)
        {
            throw new IllegalStateException("Cannot send redirect - response is already committed");
        }

        this.redirectedUrl = url;
        this.committed = true;
    }

    /**
     * Gets the redirected url.
     * 
     * @return the redirected url
     */
    public String getRedirectedUrl()
    {
        return this.redirectedUrl;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    @Override
    public void setStatus(int status)
    {
        this.status = status;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
     */
    @Override
    public void setStatus(int status, String errorMessage)
    {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the status.
     * 
     * @return the status
     */
    public int getStatus()
    {
        return this.status;
    }

    /**
     * Gets the content as byte array.
     * 
     * @return the content as byte array
     */
    public byte[] getContentAsByteArray()
    {
        flushBuffer();
        return this.content.toByteArray();
    }

    /**
     * Gets the content as string.
     * 
     * @return the content as string
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     */
    public String getContentAsString() throws UnsupportedEncodingException
    {
        flushBuffer();
        return this.characterEncoding != null ? this.content.toString(this.characterEncoding) : this.content.toString();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setContentLength(int)
     */
    @Override
    public void setContentLength(int contentLength)
    {
        this.contentLength = contentLength;
    }

    /**
     * Gets the content length.
     * 
     * @return the content length
     */
    public int getContentLength()
    {
        return this.contentLength;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
     */
    @Override
    public void setContentType(String contentType)
    {
        this.contentType = contentType;

        if (contentType != null)
        {
            int charsetIndex = contentType.toLowerCase().indexOf(FakeHttpServletResponse.CHARSET_PREFIX);
            if (charsetIndex != -1)
            {
                String encoding = contentType.substring(charsetIndex + FakeHttpServletResponse.CHARSET_PREFIX.length());
                setCharacterEncoding(encoding);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getContentType()
     */
    @Override
    public String getContentType()
    {
        return this.contentType;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setBufferSize(int)
     */
    @Override
    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getBufferSize()
     */
    @Override
    public int getBufferSize()
    {
        return this.bufferSize;
    }

    /**
     * Sets the committed.
     * 
     * @param committed
     *            the new committed
     */
    public void setCommitted(boolean committed)
    {
        this.committed = committed;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#isCommitted()
     */
    @Override
    public boolean isCommitted()
    {
        return this.committed;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#resetBuffer()
     */
    @Override
    public void resetBuffer()
    {
        if (this.committed)
        {
            throw new IllegalStateException("Cannot reset buffer - response is already committed");
        }
        if (this.content != null)
        {
            this.content.reset();
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#reset()
     */
    @Override
    public void reset()
    {
        resetBuffer();
        this.characterEncoding = null;
        this.contentLength = 0;
        this.contentType = null;
        this.locale = null;
        this.cookies.clear();
        this.headers.clear();
        this.status = HttpServletResponse.SC_OK;
        this.errorMessage = null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getLocale()
     */
    @Override
    public Locale getLocale()
    {
        return this.locale;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
     */
    @Override
    public void addCookie(Cookie cookie)
    {
        this.cookies.add(cookie);
    }

    /**
     * Gets the cookies.
     * 
     * @return the cookies
     */
    public Cookie[] getCookies()
    {
        return (Cookie[]) this.cookies.toArray(new Cookie[this.cookies.size()]);
    }

    /**
     * Gets the cookie.
     * 
     * @param name
     *            the name
     * @return the cookie
     */
    public Cookie getCookie(String name)
    {
        for (Iterator it = this.cookies.iterator(); it.hasNext();)
        {
            Cookie cookie = (Cookie) it.next();
            if (name.equals(cookie.getName()))
            {
                return cookie;
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
     */
    @Override
    public String encodeUrl(String url)
    {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
     */
    @Override
    public String encodeURL(String url)
    {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
     */
    @Override
    public String encodeRedirectUrl(String url)
    {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
     */
    @Override
    public String encodeRedirectURL(String url)
    {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void addHeader(String name, String value)
    {
        doAddHeader(name, value);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void setHeader(String name, String value)
    {
        this.headers.put(name, value);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
     */
    @Override
    public void addDateHeader(String name, long value)
    {
        doAddHeader(name, new Long(value));
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
     */
    @Override
    public void setDateHeader(String name, long value)
    {
        this.headers.put(name, new Long(value));
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
     */
    @Override
    public void addIntHeader(String name, int value)
    {
        doAddHeader(name, new Integer(value));
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
     */
    @Override
    public void setIntHeader(String name, int value)
    {
        this.headers.put(name, new Integer(value));
    }

    /**
     * Do add header.
     * 
     * @param name
     *            the name
     * @param value
     *            the value
     */
    private void doAddHeader(String name, Object value)
    {
        Object oldValue = this.headers.get(name);

        if (oldValue instanceof List)
        {
            List list = (List) oldValue;
            list.add(value);
        }
        else if (oldValue != null)
        {
            List list = new LinkedList();
            list.add(oldValue);
            list.add(value);
            this.headers.put(name, list);
        }
        else
        {
            this.headers.put(name, value);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
     */
    @Override
    public boolean containsHeader(String name)
    {
        return this.headers.containsKey(name);
    }

    /**
     * Gets the header names.
     * 
     * @return the header names
     */
    public Set getHeaderNames()
    {
        return this.headers.keySet();
    }

    /**
     * Gets the header.
     * 
     * @param name
     *            the name
     * @return the header
     */
    public Object getHeader(String name)
    {
        return this.headers.get(name);
    }

    /**
     * Gets the headers.
     * 
     * @param name
     *            the name
     * @return the headers
     */
    public List getHeaders(String name)
    {
        Object value = this.headers.get(name);

        if (value instanceof List)
        {
            return (List) value;
        }
        else if (value != null)
        {
            return Collections.singletonList(value);
        }
        else
        {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Sets the forwarded url.
     * 
     * @param forwardedUrl
     *            the new forwarded url
     */
    public void setForwardedUrl(String forwardedUrl)
    {
        this.forwardedUrl = forwardedUrl;
    }

    /**
     * Gets the forwarded url.
     * 
     * @return the forwarded url
     */
    public String getForwardedUrl()
    {
        return this.forwardedUrl;
    }

    /**
     * Sets the included url.
     * 
     * @param includedUrl
     *            the new included url
     */
    public void setIncludedUrl(String includedUrl)
    {
        this.includedUrl = includedUrl;
    }

    /**
     * Gets the included url.
     * 
     * @return the included url
     */
    public String getIncludedUrl()
    {
        return this.includedUrl;
    }

    /** The Constant CHARSET_PREFIX. */
    private static final String CHARSET_PREFIX = "charset=";

    /** The character encoding. */
    private String characterEncoding = "UTF-8";

    /** The content. */
    private ByteArrayOutputStream content;

    /** The output stream. */
    private DelegatingServletOutputStream outputStream;

    /** The writer. */
    private PrintWriter writer;

    /** The content length. */
    private int contentLength = 0;

    /** The content type. */
    private String contentType;

    /** The buffer size. */
    private int bufferSize = 4096;

    /** The committed. */
    private boolean committed;

    /** The locale. */
    private Locale locale = Locale.getDefault();

    /** The cookies. */
    private final List cookies = new ArrayList(24);

    /** The headers. */
    private final Map headers = new HashMap(24, 1.0f);

    /** The status. */
    private int status = HttpServletResponse.SC_OK;

    /** The error message. */
    private String errorMessage;

    /** The redirected url. */
    private String redirectedUrl;

    /** The forwarded url. */
    private String forwardedUrl;

    /** The included url. */
    private String includedUrl;

    /**
     * The Class DelegatingServletOutputStream.
     */
    public class DelegatingServletOutputStream extends ServletOutputStream
    {

        /**
         * Instantiates a new delegating servlet output stream.
         * 
         * @param targetStream
         *            the target stream
         */
        public DelegatingServletOutputStream(OutputStream targetStream)
        {
            super();
            this.proxy = targetStream;
        }

        /**
         * Gets the target stream.
         * 
         * @return the target stream
         */
        public OutputStream getTargetStream()
        {
            return this.proxy;
        }

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(int b) throws IOException
        {
            this.proxy.write(b);
        }

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#flush()
         */
        @Override
        public void flush() throws IOException
        {
            super.flush();
            this.proxy.flush();
        }

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#close()
         */
        @Override
        public void close() throws IOException
        {
            super.close();
            this.proxy.close();
        }

        /** The proxy. */
        private final OutputStream proxy;
    }

}
