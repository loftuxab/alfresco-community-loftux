package org.alfresco.repo.dictionary;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.util.cache.AbstractAsynchronouslyRefreshedCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Asynchronously refreshed cache for dictionary models.
 */
public class CompiledModelsCache extends AbstractAsynchronouslyRefreshedCache<DictionaryRegistry>
{
    private static Log logger = LogFactory.getLog(CompiledModelsCache.class);

    private DictionaryDAOImpl dictionaryDAO;
    private TenantService tenantService;

    @Override
    protected DictionaryRegistry buildCache(String tenantId)
    {
        if (tenantId == null)
        {
            tenantId = tenantService.getCurrentUserDomain();
        }

        final String finalTenant = tenantId;
        return AuthenticationUtil.runAs(new RunAsWork<DictionaryRegistry>()
        {
            public DictionaryRegistry doWork() throws Exception
            {
                return dictionaryDAO.initDictionaryRegistry(finalTenant);
            }
        }, tenantService.getDomainUser(AuthenticationUtil.getSystemUserName(), tenantId));
    }

    /**
     * @param tenantId the tenantId of cache that will be removed from live cache
     * @return removed DictionaryRegistry
     */
    public void remove(final String tenantId)
    {
        //TODO Should be reworked when ACE-2001 will be implemented
        liveLock.writeLock().lock();
        try
        {
            DictionaryRegistry dictionaryRegistry = live.get(tenantId);
            if (dictionaryRegistry != null)
            {
                live.remove(tenantId);
                dictionaryRegistry.remove();
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Removed dictionary register for tenant " + tenantId);
                }
            }
        }
        finally
        {
            liveLock.writeLock().unlock();
        }
    }

    /**
     * @param dictionaryDAO the dictionaryDAOImpl to set
     */
    public void setDictionaryDAO(DictionaryDAOImpl dictionaryDAO)
    {
        this.dictionaryDAO = dictionaryDAO;
    }

    /**
     * @param tenantService the tenantService to set
     */
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }
}
