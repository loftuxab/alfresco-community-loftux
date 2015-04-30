package org.alfresco.module.org_alfresco_module_cloud.aos;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CloudAosFilter implements Filter
{
    
    private static ThreadLocal<Boolean> threadGuarded = new ThreadLocal<Boolean>();

    protected static Log logger = LogFactory.getLog("org.alfresco.webdav.protocol");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void destroy()
    {
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, final FilterChain chain)
                throws IOException, ServletException
    {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response= (HttpServletResponse) servletResponse;
        
        threadGuarded.set(Boolean.TRUE);
        try
        {
            // note: if no tenant specified (ie. "/") then run as system tenant (eg. to list networks)
            TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
            {
                public Void doWork() throws ServletException, IOException
                {
                    //TenantContextHolder.setTenantDomain(tenantDomain);
                    mainWork(chain, request, response);
                    return null;
                }
            }, TenantService.DEFAULT_DOMAIN);
        }
        finally
        {
            threadGuarded.set(Boolean.FALSE);
            TenantContextHolder.clearTenantDomain();
            AuthenticationUtil.clearCurrentSecurityContext();
        }

    }
    
    public static boolean isCurrentThreadGuarded()
    {
        return threadGuarded.get() != null && threadGuarded.get().booleanValue();
    }

    protected void mainWork(FilterChain chain, HttpServletRequest request,
                HttpServletResponse response) throws IOException, ServletException
    {
        chain.doFilter(request, response);
    }

}
