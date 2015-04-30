/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.rest.authentication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_cloud.webscripts.TenantBasicHTTPAuthenticatorFactory;
import org.alfresco.repo.web.filter.beans.DependencyInjectedFilter;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import ucar.nc2.util.net.EasyX509TrustManager;


/**
 * Gateway based Authentication Filter
 * 
 * This filter, if configured with a URI to an external gateway, will invoke
 * the gateway to determine if the request should proceed or not.
 * 
 * If the gateway returns success, any headers returned from the gateway on
 * its response are applied to the originating request, and the original
 * request is continues. Otherwise, a failure response from the gateway is
 * streamed directly to the response of the originating request.
 */
public class GatewayAuthenticationFilter implements DependencyInjectedFilter, InitializingBean
{
    // Logger
    private static final Log logger = LogFactory.getLog(GatewayAuthenticationFilter.class);
    
    private Set<String> outboundHeaders = null;
    private Set<String> inboundHeaders = null;
    
    private int bufferSize = 2048;
    private String gatewayProtocol = "https";
    private String gatewayHost;
    private int gatewayPort = 80;
    private String filterPrefix = "/publicapi";
    private int connectTimeout = 10000;  // 10 seconds
    private int readTimeout = 120000;  // 120 seconds
    private boolean httpTcpNodelay = true;
    private boolean httpConnectionStalecheck = true;
    
    /**
     * @param gatewayProtocol
     */
    public void setGatewayProtocol(String gatewayProtocol)
    {
        this.gatewayProtocol = gatewayProtocol == null ? null : gatewayProtocol.toLowerCase(Locale.ENGLISH).trim();
    }

    /**
     * @param gatewayHost
     */
    public void setGatewayHost(String gatewayHost)
    {
        this.gatewayHost = gatewayHost == null ? null : gatewayHost.toLowerCase(Locale.ENGLISH).trim();
    }

    /**
     * @param gatewayPort
     */
    public void setGatewayPort(int gatewayPort)
    {
        this.gatewayPort = gatewayPort;
    }

    /**
     * @param inboundHeaders
     */
    public void setOutboundHeaders(Set<String> outboundHeaders)
    {
        if (outboundHeaders != null)
        {
            Set<String> trimmed = new HashSet<String>();
            for (String value : outboundHeaders)
            {
                trimmed.add(value.toLowerCase(Locale.ENGLISH).trim());
            }
            outboundHeaders = trimmed;
        }
        
        this.outboundHeaders = outboundHeaders;
    }
    
    public void setInboundHeaders(Set<String> inboundHeaders)
    {
        if (inboundHeaders != null)
        {
            Set<String> trimmed = new HashSet<String>();
            for (String value : inboundHeaders)
            {
                trimmed.add(value.toLowerCase(Locale.ENGLISH).trim());
            }
            inboundHeaders = trimmed;
        }
        
        this.inboundHeaders = inboundHeaders;
    }
    
    /**
     * Set the filter path this filter is mapped to  e.g. /publicapi
     * 
     * @param filterPrefix
     */
    public void setFilterPrefix(String filterPrefix)
    {
        this.filterPrefix = filterPrefix;
    }

    /**
     * Sets the buffer size for streaming reponses from gateway to Alfresco
     * 
     * @param bufferSize
     */
    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }
    
    /**
     * @param connectTimeout
     */
    public void setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }
    
    /**
     * @param readTimeout
     */
    public void setReadTimeout(int readTimeout)
    {
        this.readTimeout = readTimeout;
    }

    /**
     * @param httpTcpNodelay
     */
    public void setHttpTcpNodelay(boolean httpTcpNodelay)
    {
        this.httpTcpNodelay = httpTcpNodelay;
    }

    /**
     * @param httpConnectionStalecheck
     */
    public void setHttpConnectionStalecheck(boolean httpConnectionStalecheck)
    {
        this.httpConnectionStalecheck = httpConnectionStalecheck;
    }
    

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if (outboundHeaders == null || outboundHeaders.isEmpty())
        {
            Set<String> headers = new HashSet<String>();
            headers.add("Authorization");
            headers.add("key");
            setOutboundHeaders(headers);
        }

        if (inboundHeaders == null || inboundHeaders.isEmpty())
        {
            Set<String> headers = new HashSet<String>();
            headers.add(TenantBasicHTTPAuthenticatorFactory.DEFAULT_AUTHENTICATOR_KEY_HEADER);
            headers.add(TenantBasicHTTPAuthenticatorFactory.DEFAULT_REMOTE_USER_HEADER);
            setInboundHeaders(headers);
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(this.gatewayHost == null ? "Gateway filter disabled" : "Gateway " + this.gatewayHost + " enabled");
            logger.debug("Outbound Headers: " + outboundHeaders);
            logger.debug("Inbound Headers: " + inboundHeaders);
        }
    }
    
    @Override
    public void doFilter(ServletContext context, ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException
    {
        // clear trusted authentication headers
        ModifiableHeadersHttpServletRequest httpReq = new ModifiableHeadersHttpServletRequest((HttpServletRequest)req);
        HttpServletResponse httpRes = (HttpServletResponse)res;

        // if gateway is not configured, just pass-through
        if (gatewayHost == null || gatewayHost.length() == 0)
        {
            chain.doFilter(httpReq, res);
            return;
        }
        
        // setup http client for gateway reqest
        HttpClient client = createHttpClient();
        HttpMethod method = createGatewayMethod(httpReq);
        
        try
        {
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Authenticating via gateway ");
                msg.append(method.getName()).append(" ");
                msg.append(client.getHostConfiguration().getProtocol().getScheme()).append("://");
                msg.append(client.getHostConfiguration().getHost()).append(":");
                msg.append(client.getHostConfiguration().getPort()).append(" ");
                msg.append(method.getURI().toString());
                Header[] headers = method.getRequestHeaders();
                for (Header header : headers)
                {
                    msg.append(" ").append(header.getName()).append("=").append(header.getValue());
                }
                logger.debug(msg);
            }
            
            // proxy request to gateway
            int responseCode = client.executeMethod(method);
            
            if (logger.isDebugEnabled())
                logger.debug("Gateway response code: " + responseCode);
            
            if (responseCode == HttpServletResponse.SC_OK)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Continuing Alfresco request...");
                
                // success from gateway, copy response X- headers to request, and continue with request
                Header[] headers = method.getResponseHeaders();
                for (Header header : headers)
                {
                    if (inboundHeaders.contains(header.getName().toLowerCase(Locale.ENGLISH)))
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Adding header " + header.getName() + " with value " + header.getValue() + " to request");
                        
                        httpReq.setHeader(header.getName(), header.getValue());
                    }
                }
                method.releaseConnection();
                chain.doFilter(httpReq, httpRes);
            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.debug("Aborting Alfresco request...");
                
                // gateway deny, respond with gateway response
                httpRes.setStatus(responseCode);
                Header[] headers = method.getResponseHeaders();
                for (Header header : headers)
                {
                    httpRes.setHeader(header.getName(), header.getValue());
                }
                copyStream(method.getResponseBodyAsStream(), httpRes.getOutputStream());
            }
        }
        catch (ConnectTimeoutException ex)
        {
            if (logger.isDebugEnabled())
                logger.debug("Caught connection timeout " + ex.toString());
            
            httpRes.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
        }
        catch (SocketTimeoutException ex)
        {
            if (logger.isDebugEnabled())
                logger.debug("Caught socket timeout " + ex.toString());
            
            httpRes.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
        }
        catch (UnknownHostException ex)
        {
            if (logger.isDebugEnabled())
                logger.debug("Caught Unknown host " + ex.toString());

            httpRes.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        catch (ConnectException ex)
        {
            if (logger.isDebugEnabled())
                logger.debug("Caught connect error " + ex.toString());
            
            httpRes.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        catch (IOException ex)
        {
            if (logger.isDebugEnabled())
                logger.debug("Caught IO error " + ex.toString());
            
            httpRes.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        finally
        {
            // final tidy up
            method.releaseConnection();
        }
    }

    @SuppressWarnings("deprecation")
    private HttpClient createHttpClient()
    {
        // TODO: consider per-thread HttpClient (as implemented in RemoteClient)
        HttpClient client = new HttpClient();
        if (gatewayProtocol.equals("https"))
        {
            client.getHostConfiguration().setHost(gatewayHost, gatewayPort, new Protocol(gatewayProtocol, new EasySSLProtocolSocketFactory(), gatewayPort));
        }
        else
        {
            client.getHostConfiguration().setHost(gatewayHost, gatewayPort, gatewayProtocol);
        }
        HttpClientParams params = client.getParams();
        params.setBooleanParameter("http.tcp.nodelay", httpTcpNodelay);
        params.setBooleanParameter("http.connection.stalecheck", httpConnectionStalecheck);
        params.setIntParameter("http.connection.timeout", connectTimeout);
        params.setIntParameter("http.socket.timeout", readTimeout);
        return client;
    }

    private HttpMethod createGatewayMethod(HttpServletRequest request)
    {
        HttpMethod method = null;
        String contextPrefix = request.getContextPath() + filterPrefix;
        String uri = request.getRequestURI().substring(contextPrefix.length());
        
        String requestMethod = request.getMethod();
        if (requestMethod.equalsIgnoreCase("get"))
        {
            method = new GetMethod(uri);
        }
        else if (requestMethod.equalsIgnoreCase("put"))
        {
            method = new PutMethod(uri);
        }
        else if (requestMethod.equalsIgnoreCase("post"))
        {
            method = new PostMethod(uri);
        }
        else if (requestMethod.equalsIgnoreCase("delete"))
        {
            method = new DeleteMethod(uri);
        }
        else if (requestMethod.equalsIgnoreCase("head"))
        {
            method = new HeadMethod(uri);
        }
        else if (requestMethod.equalsIgnoreCase("options"))
        {
            method = new OptionsMethod(uri);
        }
        else
        {
            // default to get via gateway
            method = new GetMethod(uri);
        }

        // add query string
        String queryString = request.getQueryString();
        method.setQueryString(queryString);

        // no redirect processing
        method.setFollowRedirects(false);

        // copy originating request headers
        @SuppressWarnings("unchecked")
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements())
        {
            String key = headers.nextElement();
            if (outboundHeaders.contains(key.toLowerCase(Locale.ENGLISH)))
            {
                method.setRequestHeader(key, request.getHeader(key));
            }
        }
        
        return method;
    }
    
    private void copyStream(InputStream in, OutputStream out)
        throws IOException
    {
        if (in == null)
        {
            return;
        }
        
        try
        {
            final byte[] buffer = new byte[bufferSize];
            int read = in.read(buffer);
            while (read != -1)
            {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
        }
        finally
        {
            try
            {
                try
                {
                    in.close();
                }
                finally
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
            }
        }
    }
    
    /**
     * Request Wrapper that allows request headers to be excluded, and for custom headers to be set. 
     */
    private static class ModifiableHeadersHttpServletRequest extends HttpServletRequestWrapper
    {
        private List<String> excludeHeaders = new ArrayList<String>();
        private Map<String, String> setHeaders;
        
        public ModifiableHeadersHttpServletRequest(HttpServletRequest request)
        {
            super(request);
            this.excludeHeaders.add(TenantBasicHTTPAuthenticatorFactory.DEFAULT_AUTHENTICATOR_KEY_HEADER);
            this.excludeHeaders.add(TenantBasicHTTPAuthenticatorFactory.DEFAULT_REMOTE_USER_HEADER);
        }

        /**
         * Set a header value
         * 
         * @param name
         * @param value
         */
        public void setHeader(String name, String value)
        {
            if (setHeaders == null)
            {
                setHeaders = new HashMap<String, String>();
            }
            setHeaders.put(name, value);
            excludeHeaders.remove(name);
        }

        @Override
        public String getHeader(String name)
        {
            if (includeHeader(name))
            {
                if (setHeaders != null && setHeaders.containsKey(name))
                {
                    return setHeaders.get(name);
                }
                return super.getHeader(name);
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Enumeration<String> getHeaderNames()
        {
            List<String> headerNames = new ArrayList<String>();
            for (Enumeration<String> e = super.getHeaderNames(); e.hasMoreElements() ;)
            {
                String name = e.nextElement();
                if (includeHeader(name))
                {
                    headerNames.add(name);
                }
            }
            if (setHeaders != null)
            {
                headerNames.addAll(setHeaders.keySet());
            }
            return Collections.enumeration(headerNames);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Enumeration<String> getHeaders(String name)
        {
            if (includeHeader(name))
            {
                if (setHeaders != null && setHeaders.containsKey(name))
                {
                    List<String> values = new ArrayList<String>();
                    values.add(setHeaders.get(name));
                    return Collections.enumeration(values);
                }
                return super.getHeaders(name);
            }
            return Collections.enumeration(Collections.EMPTY_LIST);
        }

        @Override
        public long getDateHeader(String name)
        {
            if (includeHeader(name))
            {
                return super.getIntHeader(name);
            }
            return -1;
        }
        
        @Override
        public int getIntHeader(String name)
        {
            if (includeHeader(name))
            {
                return super.getIntHeader(name);
            }
            return -1;
        }
        
        private boolean includeHeader(String name)
        {
            if (excludeHeaders == null)
            {
                return true;
            }
            if (!excludeHeaders.contains(name))
            {
                return true;
            }
            if (setHeaders == null)
            {
                return false;
            }
            if (setHeaders.containsKey(name))
            {
                return true;
            }
            return false;
        }
    }
    
    
    /**
     * EasySSLProtocolSocketFactory can be used to create SSL Sockets that accept self-signed certificates.
     * 
     * Taken from:
     * 
     * http://svn.apache.org/viewvc/httpcomponents/oac.hc3x/trunk/src/contrib/org/apache/commons/httpclient/contrib/ssl/EasySSLProtocolSocketFactory.java
     */
    public static class EasySSLProtocolSocketFactory implements SecureProtocolSocketFactory
    {
        private SSLContext sslcontext = null;

        public EasySSLProtocolSocketFactory()
        {
            super();
        }

        private SSLContext createEasySSLContext()
        {
            try
            {
                SSLContext context = SSLContext.getInstance("SSL");
                context.init(null, new TrustManager[] {new EasyX509TrustManager(null)}, null);
                return context;
            }
            catch (Exception e)
            {
                throw new HttpClientError(e.toString());
            }
        }

        private SSLContext getSSLContext()
        {
            if (this.sslcontext == null)
            {
                this.sslcontext = createEasySSLContext();
            }
            return this.sslcontext;
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
            throws IOException, UnknownHostException
        {
            return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
        }

        @Override
        public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort, final HttpConnectionParams params)
            throws IOException, UnknownHostException, ConnectTimeoutException
        {
            if (params == null)
            {
                throw new IllegalArgumentException("Parameters may not be null");
            }
            int timeout = params.getConnectionTimeout();
            SocketFactory socketfactory = getSSLContext().getSocketFactory();
            if (timeout == 0)
            {
                return socketfactory.createSocket(host, port, localAddress, localPort);
            } else
            {
                Socket socket = socketfactory.createSocket();
                SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
                SocketAddress remoteaddr = new InetSocketAddress(host, port);
                socket.bind(localaddr);
                socket.connect(remoteaddr, timeout);
                return socket;
            }
        }

        @Override
        public Socket createSocket(String host, int port)
            throws IOException, UnknownHostException
        {
            return getSSLContext().getSocketFactory().createSocket(host, port);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException, UnknownHostException
        {
            return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        public boolean equals(Object obj)
        {
            return ((obj != null) && obj.getClass().equals(EasySSLProtocolSocketFactory.class));
        }

        public int hashCode()
        {
            return EasySSLProtocolSocketFactory.class.hashCode();
        }
    }

}
