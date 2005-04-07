package com.activiti.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * Base test class providing Hibernate sessions
 * 
 * @author Derek Hulley
 */
public abstract class BaseSpringTest extends AbstractTransactionalDataSourceSpringContextTests
{
    /** protected so that it gets populated if autowiring is done by variable name **/
    protected SessionFactory sessionFactory;
    
    public BaseSpringTest()
    {
    }
    
    /**
     * Setter present for in case autowiring is done by type
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * @return Returns the existing session attached to the thread.
     *      A new session will <b>not</b> be created.
     */
    protected Session getSession()
    {
        return SessionFactoryUtils.getSession(sessionFactory, false);
    }
    
    /**
     * Forces the session to flush to the database (without commiting) and clear the
     * cache.  This ensures that all reads against the session are fresh instances,
     * which gives the assurance that the DB read/write operations occur correctly.
     */
    protected void flushAndClear()
    {
        getSession().flush();
        getSession().clear();
    }

    protected String[] getConfigLocations()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Getting config locations");
        }
        return new String[] { "classpath:applicationContext.xml" };
    }
}
