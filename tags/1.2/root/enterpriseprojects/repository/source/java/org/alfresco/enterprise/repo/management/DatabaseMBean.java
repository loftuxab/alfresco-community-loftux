/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.sql.DatabaseMetaData;

/**
 * A Management Interface exposing properties of {@link DatabaseMetaData} for monitoring.
 * 
 * @author dward
 */
public interface DatabaseMBean
{
    /**
     * Retrieves the major version number of the underlying database.
     * 
     * @return the database major version
     */
    public int getDatabaseMajorVersion();

    /**
     * Retrieves the minor version number of the underlying database.
     * 
     * @return the database minor version
     */
    public int getDatabaseMinorVersion();

    /**
     * Retrieves the name of this database product.
     * 
     * @return the database product name
     */
    public String getDatabaseProductName();

    /**
     * Retrieves the version number of this database product.
     * 
     * @return the database product version
     */
    public String getDatabaseProductVersion();

    /**
     * Retrieves the JDBC driver's major version number.
     * 
     * @return the driver major version
     */

    public int getDriverMajorVersion();

    /**
     * Retrieves the JDBC driver's minor version number.
     * 
     * @return the driver minor version
     */
    public int getDriverMinorVersion();

    /**
     * Retrieves the name of the JDBC driver.
     * 
     * @return the driver name
     */
    public String getDriverName();

    /**
     * Retrieves the version number of the JDBC driver as a String.
     * 
     * @return the driver version
     */
    public String getDriverVersion();

    /**
     * Retrieves the major JDBC version number for the driver.
     * 
     * @return the JDBC major version
     */
    public int getJDBCMajorVersion();

    /**
     * Retrieves the minor JDBC version number for the driver.
     * 
     * @return the JDBC minor version
     */
    public int getJDBCMinorVersion();

    /**
     * Retrieves the URL for this DBMS.
     * 
     * @return the URL
     */
    public String getURL();

    /**
     * Retrieves the user name as known to this database.
     * 
     * @return the user name
     */
    public String getUserName();

    /**
     * Determines whether this database treats mixed case unquoted SQL identifiers as case insensitive and stores them
     * in lower case.
     * 
     * @return true if it stores lower case identifiers
     */
    public boolean getStoresLowerCaseIdentifiers();

    /**
     * Determines whether this database treats mixed case quoted SQL identifiers as case insensitive and stores them in
     * lower case.
     * 
     * @return true if it stores lower case quoted identifiers
     */
    public boolean getStoresLowerCaseQuotedIdentifiers();

    /**
     * Determines whether this database treats mixed case unquoted SQL identifiers as case insensitive and stores them
     * in mixed case.
     * 
     * @return true if it stores mixed case identifiers
     */
    public boolean getStoresMixedCaseIdentifiers();

    /**
     * Determines whether this database treats mixed case quoted SQL identifiers as case insensitive and stores them in
     * mixed case.
     * 
     * @return true if it stores mixed case quoted identifiers
     */
    public boolean getStoresMixedCaseQuotedIdentifiers();

    /**
     * Determines whether this database treats mixed case unquoted SQL identifiers as case insensitive and stores them
     * in upper case.
     * 
     * @return true if it stores upper case identifiers
     */
    public boolean getStoresUpperCaseIdentifiers();

    /**
     * Determines whether this database treats mixed case quoted SQL identifiers as case insensitive and stores them in
     * upper case.
     * 
     * @return true if it stores upper case quoted identifiers
     */
    public boolean getStoresUpperCaseQuotedIdentifiers();
}
