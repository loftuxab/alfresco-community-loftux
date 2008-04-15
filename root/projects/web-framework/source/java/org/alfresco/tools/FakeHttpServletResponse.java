/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
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

/**
 * Implementation of a Fake HttpServletResponse object which can be used
 * to trap output from dispatched objects into a buffer and then deal
 * with results at a later time.  Useful for page caching or nested
 * components where the output stream must be committed to real response
 * at a undetermined point in the future.
 * 
 * @author Uzquiano
 *
 */
public class FakeHttpServletResponse implements HttpServletResponse
{
    public void setCharacterEncoding(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }

    public String getCharacterEncoding()
    {
        return characterEncoding;
    }

    public ServletOutputStream getOutputStream()
    {
        return outputStream;
    }

    public PrintWriter getWriter() throws UnsupportedEncodingException
    {
        if (writer == null)
        {
            Writer targetWriter = (characterEncoding != null ? new OutputStreamWriter(
                    content, characterEncoding) : new OutputStreamWriter(
                    content));

            writer = new PrintWriter(targetWriter, true);
        }

        return writer;
    }

    public void flushBuffer()
    {
        if (writer != null)
        {
            writer.flush();
        }

        if (outputStream != null)
        {
            try
            {
                outputStream.flush();
            }
            catch (IOException ex)
            {
                throw new IllegalStateException(
                        "Could not flush OutputStream: " + ex.getMessage());
            }
        }

        committed = true;
    }

    public void sendError(int newStatus, String newErrorMessage)
            throws IOException
    {
        if (committed)
        {
            throw new IllegalStateException(
                    "Cannot set error status - response is already committed");
        }

        status = newStatus;
        errorMessage = newErrorMessage;
        committed = true;
    }

    public void sendError(int newStatus) throws IOException
    {
        if (committed)
        {
            throw new IllegalStateException(
                    "Cannot set error status - response is already committed");
        }

        status = newStatus;
        committed = true;
    }

    public String getErrorMessage()
    {

        return errorMessage;

    }

    public void sendRedirect(String url) throws IOException
    {
        if (committed)
        {
            throw new IllegalStateException(
                    "Cannot send redirect - response is already committed");
        }

        redirectedUrl = url;
        committed = true;
    }

    public String getRedirectedUrl()
    {
        return redirectedUrl;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public void setStatus(int status, String errorMessage)
    {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public int getStatus()
    {
        return status;
    }

    public byte[] getContentAsByteArray()
    {
        flushBuffer();
        return content.toByteArray();
    }

    public String getContentAsString() throws UnsupportedEncodingException
    {
        flushBuffer();
        return (characterEncoding != null) ? content.toString(characterEncoding) : content.toString();
    }

    public void setContentLength(int contentLength)
    {
        this.contentLength = contentLength;
    }

    public int getContentLength()
    {
        return contentLength;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;

        if (contentType != null)
        {
            int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
            if (charsetIndex != -1)
            {
                String encoding = contentType.substring(charsetIndex + CHARSET_PREFIX.length());
                setCharacterEncoding(encoding);
            }
        }
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    public int getBufferSize()
    {
        return bufferSize;
    }

    public void setCommitted(boolean committed)
    {
        this.committed = committed;
    }

    public boolean isCommitted()
    {
        return committed;
    }

    public void resetBuffer()
    {
        if (committed)
        {
            throw new IllegalStateException(
                    "Cannot reset buffer - response is already committed");
        }
        content.reset();
    }

    public void reset()
    {
        resetBuffer();
        characterEncoding = null;
        contentLength = 0;
        contentType = null;
        locale = null;
        cookies.clear();
        headers.clear();
        status = HttpServletResponse.SC_OK;
        errorMessage = null;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void addCookie(Cookie cookie)
    {
        cookies.add(cookie);
    }

    public Cookie[] getCookies()
    {
        return (Cookie[]) cookies.toArray(new Cookie[cookies.size()]);
    }

    public Cookie getCookie(String name)
    {
        for (Iterator it = cookies.iterator(); it.hasNext();)
        {
            Cookie cookie = (Cookie) it.next();
            if (name.equals(cookie.getName()))
            {
                return cookie;
            }
        }

        return null;
    }

    public String encodeUrl(String url)
    {
        return url;
    }

    public String encodeURL(String url)
    {
        return url;
    }

    public String encodeRedirectUrl(String url)
    {
        return url;
    }

    public String encodeRedirectURL(String url)
    {
        return url;
    }

    public void addHeader(String name, String value)
    {
        doAddHeader(name, value);
    }

    public void setHeader(String name, String value)
    {
        headers.put(name, value);
    }

    public void addDateHeader(String name, long value)
    {
        doAddHeader(name, new Long(value));
    }

    public void setDateHeader(String name, long value)
    {
        headers.put(name, new Long(value));
    }

    public void addIntHeader(String name, int value)
    {
        doAddHeader(name, new Integer(value));
    }

    public void setIntHeader(String name, int value)
    {
        headers.put(name, new Integer(value));
    }

    private void doAddHeader(String name, Object value)
    {
        Object oldValue = headers.get(name);

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
            headers.put(name, list);
        }
        else
        {
            headers.put(name, value);
        }
    }

    public boolean containsHeader(String name)
    {
        return headers.containsKey(name);
    }

    public Set getHeaderNames()
    {
        return headers.keySet();
    }

    public Object getHeader(String name)
    {
        return headers.get(name);
    }

    public List getHeaders(String name)
    {
        Object value = headers.get(name);

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

    public void setForwardedUrl(String forwardedUrl)
    {
        this.forwardedUrl = forwardedUrl;
    }

    public String getForwardedUrl()
    {
        return forwardedUrl;
    }

    public void setIncludedUrl(String includedUrl)
    {
        this.includedUrl = includedUrl;
    }

    public String getIncludedUrl()
    {
        return includedUrl;
    }

    private static final String CHARSET_PREFIX = "charset=";
    private String characterEncoding = "ISO-8859-1";

    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private final DelegatingServletOutputStream outputStream = new DelegatingServletOutputStream(
            this.content);

    private PrintWriter writer;
    private int contentLength = 0;
    private String contentType;
    private int bufferSize = 4096;
    private boolean committed;
    private Locale locale = Locale.getDefault();
    private final List cookies = new ArrayList();
    private final Map headers = new HashMap();
    private int status = HttpServletResponse.SC_OK;
    private String errorMessage;
    private String redirectedUrl;
    private String forwardedUrl;
    private String includedUrl;

    public class DelegatingServletOutputStream extends ServletOutputStream
    {
        public DelegatingServletOutputStream(OutputStream targetStream)
        {
            super();
            this.proxy = targetStream;
        }

        public OutputStream getTargetStream()
        {
            return proxy;
        }

        public void write(int b) throws IOException
        {
            proxy.write(b);
        }

        public void flush() throws IOException
        {
            super.flush();
            proxy.flush();
        }

        public void close() throws IOException
        {
            super.close();
            proxy.close();
        }

        private final OutputStream proxy;
    }

}
