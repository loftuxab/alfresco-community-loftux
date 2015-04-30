package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.StandardWebdavService;

public class ServerRootResponder extends HttpServlet
{

    private static final long serialVersionUID = 4207220180323049L;

    protected AuthenticationService authenticationService;
    
    protected Logger logger = Logger.getLogger(this.getClass());
    
    @Override
    public void init() throws ServletException
    {
        super.init();
        logger.debug("ServerRootResponder.init");
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if (context == null)
        {
            return;
        }
        ServiceRegistry serviceRegistry = (ServiceRegistry)context.getBean(ServiceRegistry.SERVICE_REGISTRY);
        authenticationService = serviceRegistry.getAuthenticationService();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("ServerRootResponder.service METHOD="+request.getMethod());
        if(request.getMethod().equals("PROPFIND"))
        {
            doPropfind(request, response);
        }
        else
        {
            super.service(request, response);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String currentUser = null;
        try
        {
            currentUser = authenticationService.getCurrentUserName();
        }
        catch(Exception e)
        {
            currentUser = "NOT_AUTHENTICATED";
        }
        logger.debug("ServerRootResponder.doOptions authUser="+currentUser);
        ServiceFilter.appendResponseHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doPropfind(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String currentUser = null;
        try
        {
            currentUser = authenticationService.getCurrentUserName();
        }
        catch(Exception e)
        {
            currentUser = "NOT_AUTHENTICATED";
        }
        logger.debug("ServerRootResponder.doPropfind authUser="+currentUser);
        ServiceFilter.appendResponseHeaders(response);
        response.setStatus(207);
        // evaluate depth parameter
        int depth = 0;
        String depthHeaderParameter = request.getHeader("Depth");
        if (depthHeaderParameter != null)
        {
            try
            {
                depth = Integer.parseInt(depthHeaderParameter);
                if (depth < 0)
                {
                    depth = 0;
                }
                if (depth > 1)
                {
                    depth = 1;
                }
            }
            catch (NumberFormatException nfe)
            {
                depth = 0;
            }
        }
        String protocol = request.isSecure() ? "https://" : "http://";
        int defaultPort = request.isSecure() ? 443 : 80;
        String portString = (request.getLocalPort() != defaultPort) ? ":" + Integer.toString(request.getLocalPort()) : "";
        String serverUrl = protocol + request.getServerName() + portString;
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        response.setStatus(StandardWebdavService.SC_MULTI_STATUS);
        out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        out.println("<D:multistatus xmlns:D=\"DAV:\" xmlns:Office=\"urn:schemas-microsoft-com:office:office\" xmlns:Repl=\"http://schemas.microsoft.com/repl/\" xmlns:Z=\"urn:schemas-microsoft-com:\">");
        out.println("  <D:response>");
        out.println("    <D:href>" + serverUrl + "</D:href>");
        out.println("    <D:propstat>");
        out.println("      <D:prop>");
        out.println("        <D:displayname></D:displayname>");
        out.println("        <D:lockdiscovery/>");
        out.println("        <D:supportedlock/>");
        out.println("        <D:isFolder>t</D:isFolder>");
        out.println("        <D:iscollection>1</D:iscollection>");
        out.println("        <D:ishidden>0</D:ishidden>");
        out.println("        <D:getcontenttype>application/octet-stream</D:getcontenttype>");
        out.println("        <D:getcontentlength>0</D:getcontentlength>");
        out.println("        <D:resourcetype><D:collection/></D:resourcetype>");
        out.println("        <Repl:authoritative-directory>t</Repl:authoritative-directory>");
        out.println("        <D:getlastmodified>2009-04-03T12:48:40Z</D:getlastmodified>");
        out.println("        <D:creationdate>2009-04-03T12:48:40Z</D:creationdate>");
        out.println("        <Repl:repl-uid>rid:{5A000D79-1C51-41AE-9953-62BF3AA27C79}</Repl:repl-uid>");
        out.println("        <Repl:resourcetag>rt:5A000D79-1C51-41AE-9953-62BF3AA27C79@00000000000</Repl:resourcetag>");
        out.println("        <D:getetag>&quot;{5A000D79-1C51-41AE-9953-62BF3AA27C79},0&quot;</D:getetag>");
        out.println("      </D:prop>");
        out.println("      <D:status>HTTP/1.1 200 OK</D:status>");
        out.println("    </D:propstat>");
        out.println("  </D:response>");
        if (depth > 0)
        {
            out.println("  <D:response>");
            out.println("    <D:href>" + serverUrl + request.getContextPath() + "</D:href>");
            out.println("    <D:propstat>");
            out.println("      <D:prop>");
            out.println("        <D:displayname></D:displayname>");
            out.println("        <D:lockdiscovery/>");
            out.println("        <D:supportedlock/>");
            out.println("        <D:isFolder>t</D:isFolder>");
            out.println("        <D:iscollection>1</D:iscollection>");
            out.println("        <D:ishidden>0</D:ishidden>");
            out.println("        <D:getcontenttype>application/octet-stream</D:getcontenttype>");
            out.println("        <D:getcontentlength>0</D:getcontentlength>");
            out.println("        <D:resourcetype><D:collection/></D:resourcetype>");
            out.println("        <Repl:authoritative-directory>t</Repl:authoritative-directory>");
            out.println("        <D:getlastmodified>2009-04-03T12:47:48Z</D:getlastmodified>");
            out.println("        <D:creationdate>2009-04-03T12:43:12Z</D:creationdate>");
            out.println("        <Repl:repl-uid>rid:{5A000D79-1C51-41AE-9953-62BF3AA27C79}</Repl:repl-uid>");
            out.println("        <Repl:resourcetag>rt:5A000D79-1C51-41AE-9953-62BF3AA27C79@00000000000</Repl:resourcetag>");
            out.println("        <D:getetag>&quot;{5A000D79-1C51-41AE-9953-62BF3AA27C79},0&quot;</D:getetag>");
            out.println("      </D:prop>");
            out.println("      <D:status>HTTP/1.1 200 OK</D:status>");
            out.println("    </D:propstat>");
            out.println("  </D:response>");
        }
        out.println("</D:multistatus>");
    }

}
