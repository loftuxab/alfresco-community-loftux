package org.alfresco.repo.tenant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Thread local to maintain tenant context for threads.
 * 
 * @author janv
 * @since Thor
 */
public class TenantContextHolder
{
    private static Log logger = LogFactory.getLog(TenantContextHolder.class);
    
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
    
    public static String setTenantDomain(String tenantDomain)
    {
        String currentTenantDomain = getTenantDomain();
        
        if (tenantDomain == null)
        {
            clearTenantDomain();
            return currentTenantDomain;
        }
        
        // force lower-case
        tenantDomain = tenantDomain.toLowerCase();
        
        if (tenantDomain.equals(currentTenantDomain))
        {
            return currentTenantDomain;
        }
        
        contextHolder.set(tenantDomain);
        
        if (logger.isTraceEnabled())
        {
            logger.trace("Set tenant: "+tenantDomain);
        }
        
        return currentTenantDomain;
    }
    
    public static String getTenantDomain() 
    {
        return (String)contextHolder.get();
    }
    
    public static void clearTenantDomain() 
    {
        if (logger.isTraceEnabled())
        {
            String tenantDomain = getTenantDomain();
            if (! TenantService.DEFAULT_DOMAIN.equals(tenantDomain))
            {
                logger.trace("Clear tenant domain (was: "+getTenantDomain()+")");
            }
        }
        
        contextHolder.remove();
    }
}
