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
package org.alfresco.web.site.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class ProxyServlet extends BaseServlet {
	/**
	 * Serialization UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Key for redirect location header.
	 */
    private static final String STRING_LOCATION_HEADER = "Location";
    /**
     * Key for content length header.
     */
    private static final String STRING_CONTENT_LENGTH_HEADER = "Content-Length";
    /**
     * Key for content type header.
     */
    private static final String STRING_CONTENT_TYPE_HEADER_NAME = "Content-Type";

    /**
     * Key for content length header.
     */
    private static final String STRING_CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    /**
     * Key for set cookie header.
     */
    private static final String STRING_SET_COOKIE_HEADER_NAME = "Set-Cookie";
    /**
     * Key for set cookie 2 header.
     */
    private static final String STRING_SET_COOKIE2_HEADER_NAME = "Set-Cookie2";
    /**
     * Key for cookie header
     */
    private static final String STRING_COOKIE_HEADER_NAME = "Cookie";
    /**
     * Key for cookie header
     */
    private static final String STRING_HOST_HEADER_NAME = "Host";
    /**
     * The content type for HTML data.
     */
    private static final String STRING_CONTENT_TYPE_HTML = "text/html";
    /**
     * The content type for HTML data.
     */
    private static final String STRING_CONTENT_TYPE_MULTIPART = "text/html";
    /**
     * The directory to use to temporarily store uploaded files
     */
    private static final File FILE_UPLOAD_TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    
    // Proxy host params
    /**
     * The host to which we are proxying requests
     */
	private String stringProxyHost;
	/**
	 * The port on the proxy host to which we are proxying requests. Default value is 80.
	 */
	private int intProxyPort = 80;
	/**
	 * The (optional) path on the proxy host to wihch we are proxying requests. Default value is "".
	 */
	private String stringProxyPath = "";
	/**
	 * The maximum size for uploaded files in bytes. Default value is 5MB.
	 */
	private int intMaxFileUploadSize = 5 * 1024 * 1024;
	
	/**
	 * Initialize the <code>ProxyServlet</code>
	 * @param servletConfig The Servlet configuration passed in by the servlet conatiner
	 */
	public void init(ServletConfig servletConfig) 
	{
		/*
		// Get the proxy host
		String stringProxyHostNew = servletConfig.getInitParameter("proxyHost");
		if(stringProxyHostNew == null || stringProxyHostNew.length() == 0) { 
			throw new IllegalArgumentException("Proxy host not set, please set init-param 'proxyHost' in web.xml");
		}
		this.setProxyHost(stringProxyHostNew);
		// Get the proxy port if specified
		String stringProxyPortNew = servletConfig.getInitParameter("proxyPort");
		if(stringProxyPortNew != null && stringProxyPortNew.length() > 0) {
			this.setProxyPort(Integer.parseInt(stringProxyPortNew));
		}
		// Get the proxy path if specified
		String stringProxyPathNew = servletConfig.getInitParameter("remotePath");
		if(stringProxyPathNew != null && stringProxyPathNew.length() > 0) {
			this.setProxyPath(stringProxyPathNew);
		}
		// Get the maximum file upload size if specified
		String stringMaxFileUploadSize = servletConfig.getInitParameter("maxFileUploadSize");
		if(stringMaxFileUploadSize != null && stringMaxFileUploadSize.length() > 0) {
			this.setMaxFileUploadSize(Integer.parseInt(stringMaxFileUploadSize));
		}
		*/
	}
	
	/**
	 * Performs an HTTP GET request
	 * @param httpServletRequest The {@link HttpServletRequest} object passed
	 *                            in by the servlet engine representing the
	 *                            client request to be proxied
	 * @param httpServletResponse The {@link HttpServletResponse} object by which
	 *                             we can send a proxied response to the client 
	 */
	public void doGet (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    		throws IOException, ServletException 
    {
		// Create a GET request
		GetMethod getMethodProxyRequest = new GetMethod(this.getProxyURL(httpServletRequest));
		// Forward the request headers
		setProxyRequestHeaders(httpServletRequest, getMethodProxyRequest);
    	// Execute the proxy request
		this.executeProxyRequest(getMethodProxyRequest, httpServletRequest, httpServletResponse);
	}
	
	/**
	 * Performs an HTTP POST request
	 * @param httpServletRequest The {@link HttpServletRequest} object passed
	 *                            in by the servlet engine representing the
	 *                            client request to be proxied
	 * @param httpServletResponse The {@link HttpServletResponse} object by which
	 *                             we can send a proxied response to the client 
	 */
	public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
        	throws IOException, ServletException 
    {
    	// Create a standard POST request
    	PostMethod postMethodProxyRequest = new PostMethod(this.getProxyURL(httpServletRequest));
		// Forward the request headers
		setProxyRequestHeaders(httpServletRequest, postMethodProxyRequest);
    	// Check if this is a mulitpart (file upload) POST
    	if(ServletFileUpload.isMultipartContent(httpServletRequest)) {
    		this.handleMultipartPost(postMethodProxyRequest, httpServletRequest);
    	} else {
    		this.handleStandardPost(postMethodProxyRequest, httpServletRequest);
    	}
    	// Execute the proxy request
    	this.executeProxyRequest(postMethodProxyRequest, httpServletRequest, httpServletResponse);
    }
	
	/**
	 * Sets up the given {@link PostMethod} to send the same multipart POST
	 * data as was sent in the given {@link HttpServletRequest}
	 * @param postMethodProxyRequest The {@link PostMethod} that we are
	 *                                configuring to send a multipart POST request
	 * @param httpServletRequest The {@link HttpServletRequest} that contains
	 *                            the mutlipart POST data to be sent via the {@link PostMethod}
	 */
    @SuppressWarnings("unchecked")
	private void handleMultipartPost(PostMethod postMethodProxyRequest, HttpServletRequest httpServletRequest)
    		throws ServletException 
    {
    	// Create a factory for disk-based file items
    	DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
    	// Set factory constraints
    	diskFileItemFactory.setSizeThreshold(this.getMaxFileUploadSize());
    	diskFileItemFactory.setRepository(FILE_UPLOAD_TEMP_DIRECTORY);
    	// Create a new file upload handler
    	ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
    	// Parse the request
    	try {
    		// Get the multipart items as a list
    		List<FileItem> listFileItems = (List<FileItem>) servletFileUpload.parseRequest(httpServletRequest);
    		// Create a list to hold all of the parts
    		List<Part> listParts = new ArrayList<Part>();
    		// Iterate the multipart items list
    		for(FileItem fileItemCurrent : listFileItems) {
    			// If the current item is a form field, then create a string part
    			if (fileItemCurrent.isFormField()) {
    				StringPart stringPart = new StringPart(
    						fileItemCurrent.getFieldName(), // The field name
    						fileItemCurrent.getString()     // The field value
    				);
    				// Add the part to the list
    				listParts.add(stringPart);
    			} else {
    				// The item is a file upload, so we create a FilePart
    				FilePart filePart = new FilePart(
    						fileItemCurrent.getFieldName(),    // The field name
    						new ByteArrayPartSource(
    								fileItemCurrent.getName(), // The uploaded file name
    								fileItemCurrent.get()      // The uploaded file contents
    						)
    				);
    				// Add the part to the list
    				listParts.add(filePart);
    			}
    		}
    		MultipartRequestEntity multipartRequestEntity = new MultipartRequestEntity(
																listParts.toArray(new Part[] {}),
																postMethodProxyRequest.getParams()
															);
    		postMethodProxyRequest.setRequestEntity(multipartRequestEntity);
    		// The current content-type header (received from the client) IS of
    		// type "multipart/form-data", but the content-type header also
    		// contains the chunk boundary string of the chunks. Currently, this
    		// header is using the boundary of the client request, since we
    		// blindly copied all headers from the client request to the proxy
    		// request. However, we are creating a new request with a new chunk
    		// boundary string, so it is necessary that we re-set the
    		// content-type string to reflect the new chunk boundary string
    		postMethodProxyRequest.setRequestHeader(STRING_CONTENT_TYPE_HEADER_NAME, multipartRequestEntity.getContentType());
    	} catch (FileUploadException fileUploadException) {
    		throw new ServletException(fileUploadException);
    	}
    }
    
	/**
	 * Sets up the given {@link PostMethod} to send the same standard POST
	 * data as was sent in the given {@link HttpServletRequest}
	 * @param postMethodProxyRequest The {@link PostMethod} that we are
	 *                                configuring to send a standard POST request
	 * @param httpServletRequest The {@link HttpServletRequest} that contains
	 *                            the POST data to be sent via the {@link PostMethod}
	 */    
    @SuppressWarnings("unchecked")
	private void handleStandardPost(PostMethod postMethodProxyRequest, HttpServletRequest httpServletRequest) 
    {
		// Get the client POST data as a Map
		Map<String, String[]> mapPostParameters = (Map<String,String[]>) httpServletRequest.getParameterMap();
		// Create a List to hold the NameValuePairs to be passed to the PostMethod
		List<NameValuePair> listNameValuePairs = new ArrayList<NameValuePair>();
		// Iterate the parameter names
		for(String stringParameterName : mapPostParameters.keySet()) {
			// Iterate the values for each parameter name
			String[] stringArrayParameterValues = mapPostParameters.get(stringParameterName);
			for(String stringParamterValue : stringArrayParameterValues) {
				// Create a NameValuePair and store in list
				NameValuePair nameValuePair = new NameValuePair(stringParameterName, stringParamterValue);
				listNameValuePairs.add(nameValuePair);
			}
		}
		// Set the proxy request POST data 
		postMethodProxyRequest.setRequestBody(listNameValuePairs.toArray(new NameValuePair[] { }));
    }
    
    /**
     * Executes the {@link HttpMethod} passed in and sends the proxy response
     * back to the client via the given {@link HttpServletResponse}
     * @param httpMethodProxyRequest An object representing the proxy request to be made
     * @param httpServletResponse An object by which we can send the proxied
     *                             response back to the client
     * @throws IOException Can be thrown by the {@link HttpClient}.executeMethod
     * @throws ServletException Can be thrown to indicate that another error has occurred
     */
    private void executeProxyRequest(
    		HttpMethod httpMethodProxyRequest,
    		HttpServletRequest httpServletRequest,
    		HttpServletResponse httpServletResponse)
    			throws IOException, ServletException 
    {
		// Create a default HttpClient
		HttpClient httpClient = new HttpClient();
		httpMethodProxyRequest.setFollowRedirects(false);

		// populate credentials
		populateCredentials(httpClient, httpMethodProxyRequest, httpServletRequest, httpServletResponse);				
		
		// Execute the request
		int intProxyResponseCode = httpClient.executeMethod(httpMethodProxyRequest);

		// Check if the proxy response is a redirect
		// The following code is adapted from org.tigris.noodle.filters.CheckForRedirect
		// Hooray for open source software
		if(intProxyResponseCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
				&& intProxyResponseCode < HttpServletResponse.SC_NOT_MODIFIED /* 304 */) {
			String strStatusCode = Integer.toString(intProxyResponseCode);
			String location = httpMethodProxyRequest.getResponseHeader(STRING_LOCATION_HEADER).getValue();
			if(location == null) {
					throw new ServletException("Recieved status code: " + strStatusCode 
							+ " but no " +  STRING_LOCATION_HEADER + " header was found in the response");
			}
			// Modify the redirect to go to this proxy servlet rather that the proxied host
			String stringMyHostName = httpServletRequest.getServerName();
			if(httpServletRequest.getServerPort() != 80) {
				stringMyHostName += ":" + httpServletRequest.getServerPort();
			}
			stringMyHostName += httpServletRequest.getContextPath();
			httpServletResponse.sendRedirect(location.replace(getProxyHostAndPort() + this.getProxyPath(), stringMyHostName));
			return;
		} else if(intProxyResponseCode == HttpServletResponse.SC_NOT_MODIFIED) {
			// 304 needs special handling.  See:
			// http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
			// We get a 304 whenever passed an 'If-Modified-Since'
			// header and the data on disk has not changed; server
			// responds w/ a 304 saying I'm not going to send the
			// body because the file has not changed.
			httpServletResponse.setIntHeader(STRING_CONTENT_LENGTH_HEADER, 0);
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		
		// Pass the response code back to the client
		httpServletResponse.setStatus(intProxyResponseCode);
		
		// If the response code is a 401, then hand back any authentication
		System.out.println("INT PROXY RESPONSE CODE: " + intProxyResponseCode);
		if(intProxyResponseCode == HttpServletResponse.SC_UNAUTHORIZED)
		{
			Header wwwAuthHeader = httpMethodProxyRequest.getResponseHeader("WWW-Authenticate");
			if(wwwAuthHeader != null)
			{
				String wwwAuthHeaderValue = wwwAuthHeader.getValue();
				//System.out.println("wwwAuthHeaderValue: " + wwwAuthHeaderValue);
				httpServletResponse.setHeader("WWW-Authenticate", wwwAuthHeaderValue); 
			}
		}
		
		// Handle content type
		// The following code is adapted from org.tigris.noodle.filters.HandleContentType
		// Hooray for open source software
        String contentType = STRING_CONTENT_TYPE_HTML;
        // Check if the proxy response set the content type
        Header headerContentType = httpMethodProxyRequest.getResponseHeader(STRING_CONTENT_TYPE_HEADER_NAME);
        if (headerContentType != null) {
            contentType = headerContentType.getValue();
        }
        // Pass the content type on to the client response
        httpServletResponse.setContentType(contentType);
        if (!contentType.startsWith(STRING_CONTENT_TYPE_HTML)) {
            // Propagate content-length if present so that IE can deal
            // with binaries.
        	Header headerContentLength = httpMethodProxyRequest.getResponseHeader(STRING_CONTENT_LENGTH_HEADER_NAME);
            if (headerContentLength != null) {
            	httpServletResponse.setHeader(STRING_CONTENT_LENGTH_HEADER_NAME, headerContentLength.getValue());
            }
        }
        
        // Pass set cookie headers back to the client
        // The following code is adapted from org.tigris.noodle.filters.CopyCookies
		// Hooray for open source software
        Header[] headerArraySetCookies = httpMethodProxyRequest.getResponseHeaders(STRING_SET_COOKIE_HEADER_NAME);
        Header[] headerArraySetCookies2 = httpMethodProxyRequest.getResponseHeaders(STRING_SET_COOKIE2_HEADER_NAME);
        try {
        	for(Header header : headerArraySetCookies) {
        		parseCookie(httpServletResponse, header.getValue());
        	}
        	for(Header header : headerArraySetCookies2) {
        		parseCookie(httpServletResponse, header.getValue());
        	}
        } catch (Exception exception) {
        	System.out.println("Unable to pass set cookie back to client");
        }
        
        // Send the content to the client
        InputStream inputStreamProxyResponse = httpMethodProxyRequest.getResponseBodyAsStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStreamProxyResponse);
        OutputStream outputStreamClientResponse = httpServletResponse.getOutputStream();
        int intNextByte;
        while ( ( intNextByte = bufferedInputStream.read() ) != -1 ) {
        	outputStreamClientResponse.write(intNextByte);
        }
    }
    
    public String getServletInfo() {
        return "HTTP Proxy Servlet";
    }

    /**
     * Retreives all of the headers from the servlet request and sets them on
     * the proxy request
     * 
     * @param httpServletRequest The request object representing the client's
     *                            request to the servlet engine
     * @param httpMethodProxyRequest The request that we are about to send to
     *                                the proxy host
     */
    @SuppressWarnings("unchecked")
	private void setProxyRequestHeaders(HttpServletRequest httpServletRequest, HttpMethod httpMethodProxyRequest) 
    {
    	// Get an Enumeration of all of the header names sent by the client
		Enumeration enumerationOfHeaderNames = httpServletRequest.getHeaderNames();
		while(enumerationOfHeaderNames.hasMoreElements()) {
			String stringHeaderName = (String) enumerationOfHeaderNames.nextElement();
			if(stringHeaderName.equalsIgnoreCase(STRING_CONTENT_LENGTH_HEADER_NAME))
				continue;
			// As per the Java Servlet API 2.5 documentation:
			//		Some headers, such as Accept-Language can be sent by clients
			//		as several headers each with a different value rather than
			//		sending the header as a comma separated list.
			// Thus, we get an Enumeration of the header values sent by the client
			Enumeration enumerationOfHeaderValues = httpServletRequest.getHeaders(stringHeaderName);
			while(enumerationOfHeaderValues.hasMoreElements()) {
				String stringHeaderValue = (String) enumerationOfHeaderValues.nextElement();
				// In case the proxy host is running multiple virtual servers,
				// rewrite the Host header to ensure that we get content from
				// the correct virtual server
				if(stringHeaderName.equalsIgnoreCase(STRING_HOST_HEADER_NAME)){
					stringHeaderValue = getProxyHostAndPort();
				}
				Header header = new Header(stringHeaderName, stringHeaderValue);
				// Set the same header on the proxy request
				httpMethodProxyRequest.setRequestHeader(header);
			}
		}
    }
    

    /**
     * Parse and copy over a cookie.
     * The following code is adapted from org.tigris.noodle.filters.CopyCookies
     */
    private static void parseCookie (HttpServletResponse res, String line)
        throws Exception 
    {
        StringTokenizer stringTokenizer = new StringTokenizer(line, ";,", true );
        String stringExpires = "";
        boolean booleanInExpires = false;
        Vector<String> vectorStringCookieData = new Vector<String>();
        Vector<Cookie> vestorCookieJar = new Vector<Cookie>();
        while( stringTokenizer.hasMoreElements() ) {
            String stringNextToken = stringTokenizer.nextToken();
            stringNextToken = stringNextToken.trim();

            if (stringNextToken.equals(";")) {
                continue;
            }

            // the end of the cookie
            if (stringNextToken.equals(",") && !booleanInExpires) {
                parseCookieData (vestorCookieJar, vectorStringCookieData);
                continue;
            }
            
            // regular tokens
            if (booleanInExpires == false && ! stringNextToken.equals (",") &&
                ! stringNextToken.startsWith("expires"))
            {
                vectorStringCookieData.addElement(stringNextToken);
            }

            // handle expires
            if (booleanInExpires == true && ! stringNextToken.equals (","))
            {
                stringExpires += ", " + stringNextToken;
                vectorStringCookieData.addElement(stringExpires);
                stringExpires = "";
                booleanInExpires = false;
            }
            if (stringNextToken.startsWith("expires"))
            {
                stringExpires = stringNextToken;
                booleanInExpires = true;
            }
        }
        // need to call one more time
        parseCookieData (vestorCookieJar, vectorStringCookieData);
        for (Enumeration<Cookie> e = vestorCookieJar.elements(); e.hasMoreElements(); )
        {
            res.addCookie(e.nextElement());
        }
    }

    /**
     * The following code is adapted from org.tigris.noodle.filters.CopyCookies
     */
    private static void parseCookieData(Vector<Cookie> vectorCookieJar, Vector<String> vectorStringCookieData)
    		throws Exception 
    {
    	SimpleDateFormat cookieDate = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zz", Locale.US );
    	Date dateExpires = null;
    	String stringPath = null;
        String stringDomain = null;
        String var = null;
        String varVal = null;
        for (Enumeration<String> e = vectorStringCookieData.elements();e.hasMoreElements() ; ) {
            String tok = e.nextElement();
            int equals_pos = tok.indexOf('=');
            if (equals_pos > 0) {
                String name = tok.substring(0, equals_pos);
                String value = tok.substring(equals_pos + 1);
                if (name.equals("expires")) {
                    dateExpires = cookieDate.parse(value);
                }
                else if (name.equals("path")) {
                    stringPath = value;                        
                }    
                else if (name.equals("domain")) {
                    stringDomain = value;                        
                }
                else {
                    var = name;
                    varVal = value;                        
                }
            }
        }
        Cookie cookie = new Cookie(var, varVal);
        if (dateExpires != null)
            cookie.setMaxAge(new Long(dateExpires.getTime()).intValue());
        if (stringDomain != null)
            cookie.setDomain(stringDomain);
        if (stringPath != null)
            cookie.setPath(stringPath);
        vectorCookieJar.addElement(cookie);
        vectorStringCookieData.removeAllElements();
    }
    
	// Accessors
    private String getProxyURL(HttpServletRequest httpServletRequest) 
    {
    	/*
		// Set the protocol to HTTP
		String stringProxyURL = "http://" + this.getProxyHost();
		// Check if we are proxying to a non-standard port
		if(this.getProxyPort() != 80) {
			stringProxyURL += ":" + this.getProxyPort();
		}
		// Check if we are proxying to a path other that the documnet root
		if(!this.getProxyPath().equalsIgnoreCase("")){
			stringProxyURL += this.getProxyPath();
		}
		// Handle the path given to the servlet
		stringProxyURL += httpServletRequest.getPathInfo();
		// Handle the query string
		if(httpServletRequest.getQueryString() != null) {
			stringProxyURL += "?" + httpServletRequest.getQueryString();
		}
		*/
    	
    	//String stringProxyURL = "http://localhost:8080/alfresco/service/ui/mytasks";
    	String stringProxyURL = (String) httpServletRequest.getParameter("endpoint");

    	// TODO: Strip out endpoint
    	if(httpServletRequest.getQueryString() != null) {
			stringProxyURL += "?" + httpServletRequest.getQueryString();
    	}
		return stringProxyURL;
    }
    
    private String getProxyHostAndPort() {
    	if(this.getProxyPort() == 80) {
    		return this.stringProxyHost;
    	} else {
    		return this.stringProxyHost + ":" + this.getProxyPort();
    	}
	}
    
	private String getProxyHost() {
		return this.stringProxyHost;
	}
	private void setProxyHost(String stringProxyHostNew) {
		this.stringProxyHost = stringProxyHostNew;
	}
	private int getProxyPort() {
		return this.intProxyPort;
	}
	private void setProxyPort(int intProxyPortNew) {
		this.intProxyPort = intProxyPortNew;
	}
	private String getProxyPath() {
		return this.stringProxyPath;
	}
	private void setProxyPath(String stringProxyPathNew) {
		this.stringProxyPath = stringProxyPathNew;
	}
	private int getMaxFileUploadSize() {
		return this.intMaxFileUploadSize;
	}
	private void setMaxFileUploadSize(int intMaxFileUploadSizeNew) {
		this.intMaxFileUploadSize = intMaxFileUploadSizeNew;
	}
	
	public void populateCredentials(HttpClient httpClient, HttpMethod httpMethodProxyRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
	{
		// basic authentication
		String u = httpServletRequest.getParameter("u");
		String pw = httpServletRequest.getParameter("pw");
		if(u != null && pw != null)
		{
			httpClient.getParams().setAuthenticationPreemptive(true);
			Credentials defaultcreds = new UsernamePasswordCredentials(u, pw);
			httpClient.getState().setCredentials(new AuthScope("localhost", 8080, AuthScope.ANY_REALM), defaultcreds);
		}
		
		// alfresco ticket
		String ticket = httpServletRequest.getParameter("ticket");
		if(ticket != null)
			httpMethodProxyRequest.getParams().setParameter("ticket", ticket);
	}
}
