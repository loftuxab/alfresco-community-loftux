package org.alfresco.repo.service;

import java.util.Collection;

import org.alfresco.service.ServiceDescriptor;
import org.alfresco.service.ServiceException;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;


/**
 * Service Descriptor.
 *  
 * @author David Caruana
 */
public class BeanServiceDescriptor
    implements ServiceDescriptor, InitializingBean, BeanFactoryAware
{
    // Container Bean Factory
    private BeanFactory beanFactory;
    
    // Namespace Service
    private NamespaceService namespaceService = null;
    
    // Namespace
    private String namespace = null; 
    
    // Service Name
    private String name = null;
    private QName qName;
    
    // Service interface class
    private Class serviceInterface = null;

    // Service Description
    private String description = null;
    
    // Service Implementation Name
    private String implementationName = null;
    
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }
    
    /**
     * Sets the namespace service
     * 
     * @param namespaceService  the namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    /**
     * Sets the service namespace
     * 
     * @param namespace  the namespace
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }
    
    /**
     * Sets the service name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Sets the service interface
     */
    public void setInterface(Class serviceInterface)
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
    public void setImplementation(String implementationName)
    {
        this.implementationName = implementationName;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
        throws Exception
    {
        ParameterCheck.mandatory("Namespace Service", namespaceService);
        ParameterCheck.mandatory("Name", name);
        ParameterCheck.mandatory("Interface", serviceInterface);
        ParameterCheck.mandatory("Implementation", implementationName);

        if (namespace != null)
        {
            if (!namespaceService.getURIs().contains(namespace))
            {
                throw new ServiceException("Namespace URI " + namespace + " is not defined for service " + name);
            }
        }
        
        // Create QName version of service name
        qName = QName.createQName((namespace == null) ? NamespaceService.DEFAULT_URI : namespace, name);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceDescriptor#getQualifiedName()
     */
    public QName getQualifiedName()
    {
        return qName;
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceDescriptor#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceDescriptor#getInterface()
     */
    public Class getInterface()
    {
        return serviceInterface;
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceDescriptor#getSupportedStoreProtocols()
     */
    public Collection<String> getSupportedStoreProtocols()
    {
        Collection<String> stores = null;
        Object implementation = beanFactory.getBean(implementationName);
        if (implementation instanceof StoreRedirector)
        {
            stores = ((StoreRedirector)implementation).getSupportedStoreProtocols();
        }
        return stores;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.StoreRedirector#getSupportedStores()
     */
    public Collection<StoreRef> getSupportedStores()
    {
        Collection<StoreRef> stores = null;
        Object implementation = beanFactory.getBean(implementationName);
        if (implementation instanceof StoreRedirector)
        {
            stores = ((StoreRedirector)implementation).getSupportedStores();
        }
        return stores;
    }

    /**
     * @return  the implementation bean name
     */
    /*package*/ String getImplementation()
    {
        return implementationName;
    }
   
}
