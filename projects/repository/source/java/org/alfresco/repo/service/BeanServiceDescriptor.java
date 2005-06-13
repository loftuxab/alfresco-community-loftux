package org.alfresco.repo.service;

import java.util.Collection;
import java.util.Properties;

import org.alfresco.service.ServiceDescriptor;
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
    
    // Namespace Prefixes
    private Properties namespacePrefixes = null; 
    
    // Service Name
    private String name = null;
    private QName qName;
    
    // Service interface class
    private Class serviceInterface = null;

    // Service Description
    private String description = null;
    
    
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
     * Sets the namespaces that this factory will use to resolve names
     * 
     * @param namespacePrefixes  the namespace prefixes
     */
    public void setNamespacePrefixes(Properties namespacePrefixes)
    {
        this.namespacePrefixes = namespacePrefixes;
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
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
        throws Exception
    {
        ParameterCheck.mandatory("Namespace Service", namespaceService);
        ParameterCheck.mandatory("Service Name", name);
        ParameterCheck.mandatory("Service Interface", serviceInterface);

        // TODO: Construct local namespace prefix resolver

        // Create QName version of service name
        qName = QName.createQName(name, namespaceService);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceDescriptor#getName()
     */
    public String getName()
    {
        return name;
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
        Object implementation = beanFactory.getBean(name);
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
        Object implementation = beanFactory.getBean(name);
        if (implementation instanceof StoreRedirector)
        {
            stores = ((StoreRedirector)implementation).getSupportedStores();
        }
        return stores;
    }

    
}
