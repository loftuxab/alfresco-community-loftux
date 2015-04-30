/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * An implementation of the {@link DatabaseMBean} interface exposing database metadata.
 * 
 * @author dward
 */
public class Database implements DatabaseMBean
{

    /** The database major version. */
    private final int databaseMajorVersion;

    /** The database minor version. */
    private final int databaseMinorVersion;

    /** The database product name. */
    private final String databaseProductName;

    /** The database product version. */
    private final String databaseProductVersion;

    /** The driver major version. */
    private final int driverMajorVersion;

    /** The driver minor version. */
    private final int driverMinorVersion;

    /** The driver name. */
    private final String driverName;

    /** The driver version. */
    private final String driverVersion;

    /** The JDBC major version. */
    private final int jdbcMajorVersion;

    /** The JDBC minor version. */
    private final int jdbcMinorVersion;

    /** Does it store lower case identifiers? */
    private final boolean storesLowerCaseIdentifiers;

    /** Does it store lower case quoted identifiers? */
    private final boolean storesLowerCaseQuotedIdentifiers;

    /** Does it store mixed case identifiers? */
    private final boolean storesMixedCaseIdentifiers;

    /** Does it store mixed case quoted identifiers? */
    private final boolean storesMixedCaseQuotedIdentifiers;

    /** Does it store upper case identifiers? */
    private final boolean storesUpperCaseIdentifiers;

    /** Does it store upper case quoted identifiers? */
    private final boolean storesUpperCaseQuotedIdentifiers;

    /** The URL. */
    private final String url;

    /** The user name. */
    private final String userName;

    /**
     * Creates a new instance.
     * 
     * @param dataSource
     *            the data source
     * @throws SQLException
     *             a SQL exception
     */
    public Database(DataSource dataSource) throws SQLException
    {
        Connection con = null;
        try
        {
            con = dataSource.getConnection();
            DatabaseMetaData dbmd = con.getMetaData();

            this.databaseMajorVersion = dbmd.getDatabaseMajorVersion();
            this.databaseMinorVersion = dbmd.getDatabaseMinorVersion();
            this.databaseProductName = dbmd.getDatabaseProductName();
            this.databaseProductVersion = dbmd.getDatabaseProductVersion();
            this.driverMajorVersion = dbmd.getDriverMajorVersion();
            this.driverMinorVersion = dbmd.getDriverMinorVersion();
            this.driverName = dbmd.getDriverName();
            this.driverVersion = dbmd.getDriverVersion();
            this.jdbcMajorVersion = dbmd.getJDBCMajorVersion();
            this.jdbcMinorVersion = dbmd.getJDBCMinorVersion();
            this.storesLowerCaseIdentifiers = dbmd.storesLowerCaseIdentifiers();
            this.storesLowerCaseQuotedIdentifiers = dbmd.storesLowerCaseQuotedIdentifiers();
            this.storesMixedCaseIdentifiers = dbmd.storesMixedCaseIdentifiers();
            this.storesMixedCaseQuotedIdentifiers = dbmd.storesMixedCaseQuotedIdentifiers();
            this.storesUpperCaseIdentifiers = dbmd.storesUpperCaseIdentifiers();
            this.storesUpperCaseQuotedIdentifiers = dbmd.storesUpperCaseQuotedIdentifiers();
            this.url = dbmd.getURL();
            this.userName = dbmd.getUserName();
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                }
            }

        }

    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getDatabaseMajorVersion()
     */
    public int getDatabaseMajorVersion()
    {
        return this.databaseMajorVersion;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getDatabaseMinorVersion()
     */
    public int getDatabaseMinorVersion()
    {
        return this.databaseMinorVersion;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getDatabaseProductName()
     */
    public String getDatabaseProductName()
    {
        return this.databaseProductName;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getDatabaseProductVersion()
     */
    public String getDatabaseProductVersion()
    {
        return this.databaseProductVersion;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getDriverMajorVersion()
     */
    public int getDriverMajorVersion()
    {
        return this.driverMajorVersion;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getDriverMinorVersion()
     */
    public int getDriverMinorVersion()
    {
        return this.driverMinorVersion;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getDriverName()
     */
    public String getDriverName()
    {
        return this.driverName;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getDriverVersion()
     */
    public String getDriverVersion()
    {
        return this.driverVersion;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getJDBCMajorVersion()
     */
    public int getJDBCMajorVersion()
    {
        return this.jdbcMajorVersion;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getJDBCMinorVersion()
     */
    public int getJDBCMinorVersion()
    {
        return this.jdbcMinorVersion;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getStoresLowerCaseIdentifiers()
     */
    public boolean getStoresLowerCaseIdentifiers()
    {
        return this.storesLowerCaseIdentifiers;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getStoresLowerCaseQuotedIdentifiers()
     */
    public boolean getStoresLowerCaseQuotedIdentifiers()
    {
        return this.storesLowerCaseQuotedIdentifiers;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getStoresMixedCaseIdentifiers()
     */
    public boolean getStoresMixedCaseIdentifiers()
    {
        return this.storesMixedCaseIdentifiers;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getStoresMixedCaseQuotedIdentifiers()
     */
    public boolean getStoresMixedCaseQuotedIdentifiers()
    {
        return this.storesMixedCaseQuotedIdentifiers;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getStoresUpperCaseIdentifiers()
     */
    public boolean getStoresUpperCaseIdentifiers()
    {
        return this.storesUpperCaseIdentifiers;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getStoresUpperCaseQuotedIdentifiers()
     */
    public boolean getStoresUpperCaseQuotedIdentifiers()
    {
        return this.storesUpperCaseQuotedIdentifiers;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getURL()
     */
    public String getURL()
    {
        return this.url;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.DatabaseMBean#getUserName()
     */
    public String getUserName()
    {
        return this.userName;
    }

}
