package org.alfresco.repo.service;

import java.util.Collection;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.QName;
import org.alfresco.util.ParameterCheck;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * Factory for defining a public Repository Service.
 * 
 * @author David Caruana
 *
 */
/*package*/ class ServiceFactory 
    implements FactoryBean, BeanNameAware, InitializingBean, ServiceDescriptor
{
    // Namespace Service
    private NamespaceService namespaceService;
    
    // Service Name
    private String name = null;
    
    // Service Description
    private String description = null;
    
    // Service interface class
    private Class serviceInterface = null;
    
    // Service implementation
    private Object implementation = null;
    
    
    /**
     * Sets the namespace service
     * 
     * @param namespaceService
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    /**
     * Sets the service interface
     */
    public void setServiceInterface(Class serviceInterface)
    {
        this.serviceInterface = serviceInterface;
    }

    /**
     * Sets the service descrition
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * Sets the service implementation
     */
    public void setImplementation(Object implementation)
    {
        this.implementation = implementation;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
        throws ServiceException
    {
        ParameterCheck.mandatory("Service Interface", serviceInterface);
        ParameterCheck.mandatory("Implementation", implementation);
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject()
    {
        return implementation;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType()
    {
        return serviceInterface;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceDescriptor#getName()
     */
    public QName getName()
    {
        return QName.createQName(name, namespaceService);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceDescriptor#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceDescriptor#getInterface()
     */
    public Class getInterface()
    {
        return serviceInterface;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceDescriptor#getSupportedStores()
     */
    public Collection<String> getSupportedStores()
    {
        Collection<String> stores = null;
        if (implementation instanceof StoreRedirector)
        {
            stores = ((StoreRedirector)implementation).getSupportedStores();
        }
        return stores;
    }

}
