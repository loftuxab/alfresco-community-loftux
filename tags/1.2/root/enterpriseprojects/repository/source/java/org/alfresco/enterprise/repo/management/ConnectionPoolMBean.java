/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

/**
 * A Management Interface exposing properties of a BasicDataSource connection pool.
 * 
 * @author dward
 */
public interface ConnectionPoolMBean
{
    /**
     * Returns the default transaction isolation state of returned connections.
     * 
     * @return the default transaction isolation
     */
    public int getDefaultTransactionIsolation();

    /**
     * Returns the jdbc driver class name.
     * 
     * @return the driver class name
     */
    public String getDriverClassName();

    /**
     * Returns the initial size of the connection pool.
     * 
     * @return the initial size
     */
    public int getInitialSize();

    /**
     * Returns the maximum number of active connections that can be allocated at the same time.
     * 
     * @return the maximum number of active connections
     */
    public int getMaxActive();

    /**
     * Returns the maximum number of connections that can remain idle in the pool.
     * 
     * @return the maximum number of idle connections
     */
    public int getMaxIdle();

    /**
     * Returns the minimum number of idle connections in the pool.
     * 
     * @return the minimum number of idle connections
     */
    public int getMinIdle();

    /**
     * The current number of active connections that have been allocated from this data source.
     * 
     * @return the number of active connections
     */
    public int getNumActive();

    /**
     * The current number of idle connections that are waiting to be allocated from this data source.
     * 
     * @return the number of idle connections
     */
    public int getNumIdle();

    /**
     * Returns the JDBC connection URL property.
     * 
     * @return the URL
     */
    public String getUrl();

    /**
     * Returns the JDBC connection user name property.
     * 
     * @return the user name
     */
    public String getUsername();
    
    /**
     * See </b>http://jakarta.apache.org/commons/dbcp/configuration.html<b> for a full description of properties.
     */
    public int getMaxWait();
    
    /**
     * See </b>http://jakarta.apache.org/commons/dbcp/configuration.html<b> for a full description of properties.
     */
    public String getValidationQuery();
    
    /**
     * See </b>http://jakarta.apache.org/commons/dbcp/configuration.html<b> for a full description of properties.
     */
    public int getTimeBetweenEvictionRunsMillis();
    
    /**
     * See <a href=http://jakarta.apache.org/commons/dbcp/configuration.html>the DBCP configuration page</a>
     * for a full description of properties.
     */
    public int getMinEvictableIdleTimeMillis();
    
    /**
     * See <a href=http://jakarta.apache.org/commons/dbcp/configuration.html>the DBCP configuration page</a>
     * for a full description of properties.
     */
    public boolean getTestOnBorrow();
    
    /**
     * See <a href=http://jakarta.apache.org/commons/dbcp/configuration.html>the DBCP configuration page</a>
     * for a full description of properties.
     */
    public boolean getTestOnReturn();
    
    /**
     * See <a href=http://jakarta.apache.org/commons/dbcp/configuration.html>the DBCP configuration page</a>
     * for a full description of properties.
     */
    public boolean getTestWhileIdle();
    
    /**
     * See <a href=http://jakarta.apache.org/commons/dbcp/configuration.html>the DBCP configuration page</a>
     * for a full description of properties.
     */
    public boolean getRemoveAbandoned();
    
    /**
     * See <a href=http://jakarta.apache.org/commons/dbcp/configuration.html>the DBCP configuration page</a>
     * for a full description of properties.
     */
    public int getRemoveAbandonedTimeout();
}
