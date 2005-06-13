/**
 * Created on Mar 31, 2005
 */
package org.alfresco.repo.version.common.counter;

import javax.sql.DataSource;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.BaseSpringTest;

/**
 * @author Roy Wetherall
 */
public class VersionCounterDaoServiceTest extends BaseSpringTest
{
    /**
     * Test store id's
     */
    private final static String STORE_ID_1 = "test1_" + System.currentTimeMillis();
    private final static String STORE_ID_2 = "test2_" + System.currentTimeMillis();
    private static final String STORE_NONE = "test3_" + System.currentTimeMillis();;
    
    /**
     * Version counter DAO service
     */
    private VersionCounterDaoService counter = null;
    
    /**
     * Datasource object
     */
    private DataSource dataSource = null;
        
    /**
     * Set the version counter DAO service
     * 
     * @param counter 
     *          the version counter DAO service
     */
    public void setCounter(VersionCounterDaoService counter)
    {
        this.counter = counter;
    }   
    
    /**
     * Set the datasource
     * 
     * @param dataSource
     *          a data source
     */
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
    
    /**
     * Test nextVersionNumber
     */
    public void testNextVersionNumber()
    {
        // Create the store references
        StoreRef store1 = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, VersionCounterDaoServiceTest.STORE_ID_1);
        StoreRef store2 = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, VersionCounterDaoServiceTest.STORE_ID_2);
        
        int store1Version0 = this.counter.nextVersionNumber(store1);
        assertEquals(store1Version0, 1);
        
        int store1Version1 = this.counter.nextVersionNumber(store1);
        assertEquals(store1Version1, 2);
        
        int store2Version0 = this.counter.nextVersionNumber(store2);
        assertEquals(store2Version0, 1);
        
        int store1Version2 = this.counter.nextVersionNumber(store1);
        assertEquals(store1Version2, 3);
        
        int store2Version1 = this.counter.nextVersionNumber(store2);
        assertEquals(store2Version1, 2);
        
        int store1Current = this.counter.currentVersionNumber(store1);
        assertEquals(store1Current, 3);
        
        int store2Current = this.counter.currentVersionNumber(store2);
        assertEquals(store2Current, 2);
        
        StoreRef storeNone = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, VersionCounterDaoServiceTest.STORE_NONE);
        int storeNoneCurrent = this.counter.currentVersionNumber(storeNone);
        assertEquals(storeNoneCurrent, 0);
        
        // Need to clean-up since the version counter works in its own transaction
        this.counter.resetVersionNumber(store1);
        this.counter.resetVersionNumber(store2);
    }

}
