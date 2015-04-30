package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.StandardDispatcherService;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.protocol.VermeerRequest;

/**
 * 
 */
public class ServiceDispatcher extends StandardDispatcherService
{

    /** the version ID used for serialization */
    private static final long serialVersionUID = 5100357015031001729L;

    /** list of service names (as String objects) that are handled by this server */
    protected List<String> serviceNames = new ArrayList<String>();

    @Override
    public void init(ServletConfig servletConfig_p) throws ServletException
    {
        super.init(servletConfig_p);
        
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if(wac == null)
        {
            throw new ServletException("Error initializing Servlet. No WebApplicationContext available.");
        }
        
        AlfrescoVirtualFileSystem vfs = (AlfrescoVirtualFileSystem) wac.getBean("AosVirtualFileSystem");
        if(vfs == null)
        {
            throw new ServletException("Cannot find bean AosVirtualFileSystem in WebApplicationContext.");
        }
        ((AlfrescoVirtualFileSystem)vfs).prepare();
        
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
    public List<?> getServicePrefixes(VermeerRequest vermeerRequest)
    {
        return serviceNames;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.getWriter().print("<html><body>This is the Alfresco ServiceDispatcher</body></html>");
    }

    protected class ServiceDispatcherUserData implements UserData
    {

        @Override
        public String getUsername()
        {
            return "";
        }

    }

    protected UserData userData = new ServiceDispatcherUserData();

    @Override
    public UserData negotiateAuthentication(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException
    {
        return userData;
    }

    @Override
    public void invalidateAuthentication(UserData arg0, HttpServletRequest arg1, HttpServletResponse arg2) throws IOException
    {
        // nothing to do here
    }

    @Override
    public void requestAuthentication(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException
    {
        // nothing to do here
    }

}