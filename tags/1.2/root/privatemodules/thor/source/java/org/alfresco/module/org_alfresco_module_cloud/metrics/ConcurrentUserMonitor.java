package org.alfresco.module.org_alfresco_module_cloud.metrics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.alfresco.module.org_alfresco_module_cloud.webscripts.PublicApiCredentials;
import org.alfresco.module.org_alfresco_module_cloud.webscripts.TenantBasicHTTPAuthenticatorFactory;
import org.alfresco.module.org_alfresco_module_cloud.webscripts.TenantBasicHTTPAuthenticatorFactory.TenantBasicHttpAuthenticator;
import org.alfresco.module.org_alfresco_module_cloud.webscripts.TenantCredentials;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.web.auth.AuthenticationListener;
import org.alfresco.repo.web.auth.BasicAuthCredentials;
import org.alfresco.repo.web.auth.TicketCredentials;
import org.alfresco.repo.web.auth.WebCredentials;
import org.alfresco.repo.web.scripts.servlet.BasicHttpAuthenticatorFactory.BasicHttpAuthenticator;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ConcurrentUserMonitor. This class counts the number of users to have a logged in within the last 5 minutes. The
 * counts are exposed via JMX.
 * 
 * Implements {@link AuthenticationEventListener} to receive authentication events from {@link BasicHttpAuthenticator}
 * or {@link TenantBasicHttpAuthenticator}. On successful authentication, the credentials are added to a credential 
 * type specific hazelcast map, which evicts entries after 5 minutes. This period can altered by modifying the 
 * property, alfresco.conccurrentusers.timeToLive. The size of this map indicates the number of users who have logged
 * in using a particular credential type.
 * 
 * @author Alex Miller
 * @since Cloud Sprint 5
 */
public class ConcurrentUserMonitor implements AuthenticationListener, ConcurrentUserMonitorMXBean
{
    private static Log logger = LogFactory.getLog(TenantBasicHTTPAuthenticatorFactory.class);

    @SuppressWarnings("rawtypes")
    private static final Set<Class> DEFAULT_TRACKED_CREDENTIALS = new HashSet<Class>(Arrays.asList(new Class[] {
        BasicAuthCredentials.class, PublicApiCredentials.class, TicketCredentials.class
    }));

    private AtomicLong failureCount = new AtomicLong();
    
    @SuppressWarnings("rawtypes")
    private Set<Class> trackedCredentials = DEFAULT_TRACKED_CREDENTIALS;

    @SuppressWarnings("rawtypes")
    private Map<Class, SimpleCache> localStores = new HashMap<Class, SimpleCache>();    
    @SuppressWarnings("rawtypes")
    private Map<Class, SimpleCache> clusterStores = new HashMap<Class, SimpleCache>();

    
    /**
     * Update the set of {@link WebCredentials} types for which a count should be maintained.
     */
    @SuppressWarnings("rawtypes")
    public void setTrackedCredentials(Set<Class<? extends WebCredentials>> trackedCredentials)
    {
        ParameterCheck.mandatoryCollection("trackedCredentials", trackedCredentials);
        this.trackedCredentials = new HashSet<Class>(trackedCredentials);
    }
        
    /**
     * Set the local stores, for tracking locally authenticated users.
     */
    public void setLocalStores(@SuppressWarnings("rawtypes") Map<Class, SimpleCache> localStores)
    {
        this.localStores.clear();
        this.localStores.putAll(localStores);
    }
    
    /**
     * Set the clustered stores, for tracking authenticated users across the cluster.
     */
    public void setClusterStores(@SuppressWarnings("rawtypes") Map<Class, SimpleCache> clusterStores)
    {
        this.clusterStores.clear();
        this.clusterStores.putAll(clusterStores);
    }

    /**
     * Receive an userAuthenticated event and add the credentials to the relevant hazelcast map.
     */
    @Override
    public void userAuthenticated(WebCredentials credentials)
    {
        if (credentials instanceof TenantCredentials)
        {
            credentials = ((TenantCredentials)credentials).getOriginalCredentials();
        }
        
        Class<? extends WebCredentials> credentialsClass = credentials.getClass();
        if (trackedCredentials.contains(credentialsClass))
        {
            SimpleCache clusterCredentialsStore = clusterStores.get(credentialsClass);
            clusterCredentialsStore.put(credentials, credentials);
            SimpleCache localCredentailsStore = localStores.get(credentialsClass);
            if (localCredentailsStore != null)
            {
                localCredentailsStore.put(credentials, credentials);
            }
        }
    }

    /**
     * Receive notification of an authentication failure.
     */
    @Override
    public void authenticationFailed(WebCredentials credentials)
    {
        failureCount.incrementAndGet();
    }
    
    @Override
    public void authenticationFailed(WebCredentials credentials, Exception ex)
    {
        failureCount.incrementAndGet();
    }

    /**
     * @return A list of {@link UserCount} objects for each tracked credential type, with the number of user who logged in 
     *           with a particular credential type, in the last 5 minutes. 
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List<UserCount> getConcurrentUserCounts()
    {
        List<UserCount> results = new LinkedList<UserCount>();
        for (Class trackedCredentailsClass : trackedCredentials)
        {
            // TODO: should SimpleCache support size() directly? 
            long count = clusterStores.get(trackedCredentailsClass).getKeys().size();
            results.add(new UserCount(trackedCredentailsClass.getSimpleName(), count));
        }
        return results;
    }

    /**
     * @return A list of {@link UserCount} objects for each tracked credential type, with the number of user who logged in 
     *           with a particular credential type, in the last 5 minutes. 
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List<UserCount> getLocalConcurrentUserCounts()
    {
        List<UserCount> results = new LinkedList<UserCount>();
        for (Class trackedCredentailsClass : localStores.keySet())
        {
            long count = localStores.get(trackedCredentailsClass).getKeys().size();
            results.add(new UserCount(trackedCredentailsClass.getSimpleName(), count));
        }
        return results;
    }
}
