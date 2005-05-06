package org.alfresco.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Base test class providing Hibernate sessions
 * 
 * @author Derek Hulley
 */
public abstract class BaseHibernateTest extends BaseSpringTest
{
    private static final Log logger = LogFactory.getLog(BaseHibernateTest.class);
    
    private SessionFactory sessionFactory;

    private Session session;

    public BaseHibernateTest()
    {
    }

    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
        // construct a session at the same time
        this.session = sessionFactory.openSession();
    }

    /**
     * @return Returns a <code>Session</code> that is <b>separate</b> from that provided
     *      automatically to the test and related beans
     */
    protected Session getSession()
    {
        return session;
    }

    public void afterPropertiesSet() throws Exception
    {
        throw new UnsupportedOperationException();
    }
}
