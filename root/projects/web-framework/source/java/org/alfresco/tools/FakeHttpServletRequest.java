package org.alfresco.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class FakeHttpServletRequest implements HttpServletRequest
{
    public String getAuthType()
    {
        return null;
    }

    public Cookie[] getCookies()
    {
        return null;
    }

    public long getDateHeader(String name)
    {
        return -1;
    }

    public String getHeader(String name)
    {
        return null;
    }

    public Enumeration getHeaders(String name)
    {
        return new Vector().elements();
    }

    public Enumeration getHeaderNames()
    {
        return new Vector().elements();
    }

    public int getIntHeader(String name)
    {
        return -1;
    }

    public String getMethod()
    {
        return "GET";
    }

    public String getPathInfo()
    {
        return null;
    }

    public String getPathTranslated()
    {
        return null;
    }

    public String getContextPath()
    {
        return "";
    }

    public String getQueryString()
    {
        return null;
    }

    public String getRemoteUser()
    {
        return null;
    }

    public boolean isUserInRole(String role)
    {
        return false;
    }

    public Principal getUserPrincipal()
    {
        return null;
    }

    public String getRequestedSessionId()
    {
        return null;
    }

    public String getRequestURI()
    {
        return "/";
    }

    public StringBuffer getRequestURL()
    {
        return new StringBuffer("http://localhost/");
    }

    public String getServletPath()
    {
        return "";
    }

    public HttpSession getSession(boolean create)
    {
        if (!create)
        {
            return null;
        }
        return new FakeHttpSession();
    }

    public HttpSession getSession()
    {
        return null;
    }

    public boolean isRequestedSessionIdValid()
    {
        return false;
    }

    public boolean isRequestedSessionIdFromCookie()
    {
        return false;
    }

    public boolean isRequestedSessionIdFromURL()
    {
        return false;
    }

    public boolean isRequestedSessionIdFromUrl()
    {
        return false;
    }

    public Object getAttribute(String name)
    {
        return this.attributes.get(name);
    }

    public Enumeration getAttributeNames()
    {
        return Collections.enumeration(this.attributes.keySet());
    }

    public String getCharacterEncoding()
    {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding)
            throws UnsupportedEncodingException
    {
        this.characterEncoding = characterEncoding;
    }

    public int getContentLength()
    {
        return 0;
    }

    public String getContentType()
    {
        return "text/plain";
    }

    public ServletInputStream getInputStream() throws IOException
    {
        return new ServletInputStream()
        {
            @Override
            public int read() throws IOException
            {
                return -1;
            }
        };
    }

    public String getLocalAddr()
    {
        return "127.0.0.1";
    }

    public String getLocalName()
    {
        return "localhost";
    }

    public int getLocalPort()
    {
        return 80;
    }

    public String getParameter(String name)
    {
        return null;
    }

    public Enumeration getParameterNames()
    {
        return new Vector().elements();
    }

    public String[] getParameterValues(String name)
    {
        return null;
    }

    public Map getParameterMap()
    {
        return Collections.EMPTY_MAP;
    }

    public String getProtocol()
    {
        return "HTTP/1.1";
    }

    public String getScheme()
    {
        return "http";
    }

    public String getServerName()
    {
        return "localhost";
    }

    public int getServerPort()
    {
        return 80;
    }

    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new StringReader(""));
    }

    public String getRemoteAddr()
    {
        return "localhost";
    }

    public String getRemoteHost()
    {
        return "localhost";
    }

    public int getRemotePort()
    {
        return 80;
    }

    public void setAttribute(String name, Object o)
    {
        this.attributes.put(name, o);
    }

    public void removeAttribute(String name)
    {
        this.attributes.remove(name);
    }

    public Locale getLocale()
    {
        return Locale.getDefault();
    }

    public Enumeration getLocales()
    {
        return Collections.enumeration(Arrays.asList(new Locale[] { Locale.getDefault() }));
    }

    public boolean isSecure()
    {
        return false;
    }

    public RequestDispatcher getRequestDispatcher(String path)
    {
        return new RequestDispatcher()
        {
            public void include(ServletRequest request, ServletResponse response)
                    throws ServletException, IOException
            {
            }

            public void forward(ServletRequest request, ServletResponse response)
                    throws ServletException, IOException
            {
            }
        };
    }

    public String getRealPath(String path)
    {
        return null;
    }

    private String characterEncoding = null;
    private Map attributes = new HashMap();
}
