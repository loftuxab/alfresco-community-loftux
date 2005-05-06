/**
 * Created on Mar 31, 2005
 */
package org.alfresco.repo.version.common.counter.db;

import javax.sql.DataSource;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.version.common.counter.VersionCounterDaoService;

/**
 * Version counter Db DAO service implemtation.
 * 
 * @author Roy Wetherall
 */
public class DbVersionCounterDaoServiceImpl implements VersionCounterDaoService
{
    // TODO these queries need to moved to a query register at some point
    
    /**
     * SQL - create table
     */
    private final static String SQL_CREATE_TABLE = 
        "create table if not exists version_counter" +
        "(" +
        "    store_protocol varchar(255)," +
        "    store_id varchar(255)," +
        "    current_version int" +
        ")";
    
    /**
     * SQL - select current version
     */
    private final static String SQL_SELECT = 
        "select " +
        "   current_version " +
        "from " +
        "   version_counter " +
        "where " +
        "   store_protocol = ? and store_id = ?";
    
    /**
     * SQL - update current version
     */
    private final static String SQL_UPDATE = 
        "update " +
        "   version_counter " +
        "set " +
        "   current_version = ? " +
        "where " +
        "   store_protocol = ? and store_id=?";
    
    /**
     * SQL - insert current version
     */
    private final static String SQL_INSERT = 
        "insert into version_counter (store_protocol, store_id, current_version) " +
        "values (?, ?, 1)";
    
    /**
     * SQL - delete version counter data
     */
    private final static String SQL_DELETE = 
        "delete from " +
        "   version_counter " +
        "where " +
        "   store_protocol = ? and store_id =?";
    
    /*
     * The data source
     */
    private DataSource dataSource = null;
    
    /*
     * Set the datasource
     * 
     * @param dataSource the datasource
     */
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
    
    /**
     * Initialises the version counter by ensuring that the required table has been created
     */
    public void initialise()
    {
        JdbcTemplate template = new JdbcTemplate(this.dataSource);
        template.execute(DbVersionCounterDaoServiceImpl.SQL_CREATE_TABLE);
    }
    
    /**
     * Get the next available version number for the specified store.
     * 
     * @param storeRef  the version store id
     * @return          the next version number
     */
    public synchronized int nextVersionNumber(StoreRef storeRef)
    {
        int currentVersion = 0;
        JdbcTemplate template = new JdbcTemplate(this.dataSource);   
                
        try
        {
            // Get the next version number from the database
            currentVersion = template.queryForInt(
                DbVersionCounterDaoServiceImpl.SQL_SELECT, 
                new Object[]{storeRef.getProtocol(), storeRef.getIdentifier()});
        }
        catch (IncorrectResultSizeDataAccessException exception)
        {
            // Ignore since the version will be 0 and this will cause a row for the
            // unknown store to be inserted
        }
        
        if (currentVersion == 0)
        {
            // Since we did not have an entry for the specified store id, insert one indicating the
            // next version number
            template.update(
                    DbVersionCounterDaoServiceImpl.SQL_INSERT, 
                    new Object[]{storeRef.getProtocol(), storeRef.getIdentifier()});
        }
        else
        {        
            // Update the next version number
            template.update(
                    DbVersionCounterDaoServiceImpl.SQL_UPDATE,
                    new Object[]{currentVersion+1, storeRef.getProtocol(), storeRef.getIdentifier()});
        }
        
        return currentVersion+1;
    }
    
    /**
     * Gets the current version number for the specified store.
     * 
     * @param storeRef  the store reference
     * @return          the current version number, zero if no version yet allocated.
     */
    public int currentVersionNumber(StoreRef storeRef)
    {
        int currentVersion = 0;
        
        JdbcTemplate template = new JdbcTemplate(this.dataSource);   
        
        try
        {
            // Get the next version number from the database
            currentVersion = template.queryForInt(
                DbVersionCounterDaoServiceImpl.SQL_SELECT, 
                new Object[]{storeRef.getProtocol(), storeRef.getIdentifier()});
        }
        catch (IncorrectResultSizeDataAccessException exception)
        {
            // Ignore since the version will be 0 indicating that there is not yet a counter
            // for this store
        }
        
        return currentVersion;
    }
    
    /**
     * Resets the version number for a the specified store.
     * 
     * WARNING: calling this method will completely reset the current 
     * version count for the specified store and cannot be undone.  
     *
     * @param storeRef  the store reference
     */
    public synchronized void resetVersionNumber(StoreRef storeRef)
    {
        // The version number is reset by removing the version counter entry from the table
        JdbcTemplate template = new JdbcTemplate(this.dataSource);
        template.update(SQL_DELETE, new Object[]{storeRef.getProtocol(), storeRef.getIdentifier()});
    }

}
