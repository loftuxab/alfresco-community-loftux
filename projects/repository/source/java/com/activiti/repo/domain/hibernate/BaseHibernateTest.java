package com.activiti.repo.domain.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * Base test class providing Hibernate sessions
 * 
 * @author derekh
 */
public abstract class BaseHibernateTest extends
        AbstractTransactionalDataSourceSpringContextTests implements
        InitializingBean {
    private SessionFactory sessionFactory;
    private Session session;

    public BaseHibernateTest() {
    }

    protected String[] getConfigLocations() {
        return new String[] { "classpath:applicationContext.xml"};
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        // construct a session at the same time
        this.session = sessionFactory.openSession();
    }

    protected Session getSession() {
        return session;
    }

    public void afterPropertiesSet() throws Exception {
        throw new UnsupportedOperationException();
    }
}
