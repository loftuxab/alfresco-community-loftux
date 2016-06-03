
package org.alfresco.test.properties;

/**
 * Properties used for QA tests.
 * 
 * @author Meenal Bhave
 * @since 1.0
 */
public class QATestSettings
{
    private final String uniqueTestRunName;
    
    private final String adminUsername;

    private final String adminPassword;
    
    private final String defaultUser;

    private final String defaultPassword;

    private final String domainFree;

    private final String domainPremium;

    private final String domainHybrid;

    private int solrRetryCount;

    private long solrWaitTime;

    public QATestSettings(final String uniqueTestRunName, final String adminUsername, final String adminPassword, final String defaultUser, final String defaultPassword,
            final String domainFree, final String domainPremium, final String domainHybrid, final long solrWaitTime, final int solrRetryCount)
    {
        this.uniqueTestRunName = uniqueTestRunName;
        
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;

        this.defaultUser = defaultUser;
        this.defaultPassword = defaultPassword;

        this.domainFree = domainFree;
        this.domainPremium = domainPremium;
        this.domainHybrid = domainHybrid;
        
        this.solrWaitTime = solrWaitTime;
        this.solrRetryCount = solrRetryCount;        
    }
    
    public String getQATestSettings()
    {        
        return "Test Properties set as: Admin Username: " + adminUsername
         + " Admin Password: " + adminPassword 
         + " Default Password: " + defaultPassword
         + " uniqueTestRunName: " + uniqueTestRunName
         + " solr Wait Time: " + solrWaitTime;
    }

    public String getUniqueTestRunName()
    {
        return uniqueTestRunName;
    }
    
    public String getAdminUsername()
    {
        return adminUsername;
    }

    public String getAdminPassword()
    {
        return adminPassword;
    }

    public String getDefaultUser()
    {
        return defaultUser;
    }
    
    public String getDefaultPassword()
    {
        return defaultPassword;
    }

    public String getDomainFree()
    {
        return domainFree;
    }

    public String getDomainPremium()
    {
        return domainPremium;
    }

    public String getDomainHybrid()
    {
        return domainHybrid;
    }

    public int getSolrRetryCount()
    {
        return solrRetryCount;
    }
    
    public long getSolrWaitTime()
    {
        return solrWaitTime;
    }
}