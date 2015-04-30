package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.GetWebCollectionWebDescription;
import com.xaldon.officeservices.GetWebWebDescription;
import com.xaldon.officeservices.StandardWebsService;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.WebsGetContentTypeContentType;
import com.xaldon.officeservices.WebsGetContentTypesContentType;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.protocol.SimpleSoapParser;
import com.xaldon.officeservices.protocol.SoapParameter;

public class WebsService extends StandardWebsService
{

    private static final long serialVersionUID = -4811811494131468815L;

    protected AlfrescoVirtualFileSystem vfs;
    
    protected AuthenticationService authenticationService;

    /** list of service names (as String objects) that are handled by this server */
    protected List<String> serviceNames = new ArrayList<String>();

    public void init(ServletConfig servletConfig_p) throws ServletException
    {
        super.init(servletConfig_p);
        
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if(wac == null)
        {
            throw new ServletException("Error initializing Servlet. No WebApplicationContext available.");
        }
        
        vfs = (AlfrescoVirtualFileSystem) wac.getBean("AosVirtualFileSystem");
        if(vfs == null)
        {
            throw new ServletException("Cannot find bean AosVirtualFileSystem in WebApplicationContext.");
        }
        ((AlfrescoVirtualFileSystem)vfs).prepare();
        authenticationService = (AuthenticationService) wac.getBean("AuthenticationService");
        if(authenticationService == null)
        {
            throw new ServletException("Cannot find bean AuthenticationService in WebApplicationContext.");
        }
        
        if(vfs.getSitePathOverwrite() != null)
        {
            serviceNames.add(vfs.getSitePathOverwrite().equals("/") ? "" : vfs.getSitePathOverwrite());
        }
        else
        {
            serviceNames.add(servletConfig_p.getServletContext().getContextPath() + Const.DEFAULT_SITE_PATH_IN_CONTEXT);
        }
    }

    @Override
    public List<?> getServicePrefixes(SimpleSoapParser parser, HttpServletRequest request)
    {
        return serviceNames;
    }
    
    protected String getServerUrl(HttpServletRequest request)
    {
        String protocol = request.isSecure() ? "https://" : "http://";
        int defaultPort = request.isSecure() ? 443 : 80;
        String portString = (request.getLocalPort() != defaultPort) ? ":" + Integer.toString(request.getLocalPort()) : "";
        return protocol + request.getServerName() + portString;
    }

    @Override
    public List<?> getWebCollection(SimpleSoapParser parser, HttpServletRequest request)
    {
        String serverUrl = getServerUrl(request);
        List<Object> webCollection = new ArrayList<Object>(serviceNames.size()-1);
        for(int i = 0;  i <  serviceNames.size()-1;  i++)
        {
            String servicePrefix = (String)serviceNames.get(i);
            webCollection.add(new GetWebCollectionWebDescription("Alfresco",serverUrl+servicePrefix));
        }
        return webCollection;
    }

    @Override
    protected GetWebWebDescription getWebResult(UserData userData, String webUrl, SimpleSoapParser parser, HttpServletRequest request)
    {
        String serverUrl = getServerUrl(request);
        for(int i = 0;  i <  serviceNames.size()-1;  i++)
        {
            String servicePrefix = (String)serviceNames.get(i);
            String webUrlForService = serverUrl+servicePrefix;
            if(webUrl.equals(webUrlForService))
            {
                return new GetWebWebDescription("Alfresco",serverUrl+servicePrefix,"Alfresco",GetWebWebDescription.LCID_ENUS,"");
            }
        }
        return null;
    }

    // Authentication

    @Override
    public UserData negotiateAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        return new AuthenticationServiceUserData(authenticationService);
    }

    @Override
    public void requestAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // not required
    }

    @Override
    public void invalidateAuthentication(UserData userData, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // not required
    }

    @Override
    public WebsGetContentTypesContentType[] getContentTypes(SimpleSoapParser parser, HttpServletRequest request)
    {
        // custom content types are not yet supported and will be added later
        return null;
    }

    @Override
    protected WebsGetContentTypeContentType getContentType(UserData userData, String contentTypeId, HttpServletRequest request) throws AuthenticationRequiredException
    {
        // custom content types are not yet supported and will be added later
        return null;
    }

    @Override
    protected boolean updateContentTypeXmlDocument(UserData userData, String contentTypeId, SoapParameter newDocument, HttpServletRequest request) throws AuthenticationRequiredException
    {
        // custom content types are not yet supported and will be added later
        return false;
    }

}
