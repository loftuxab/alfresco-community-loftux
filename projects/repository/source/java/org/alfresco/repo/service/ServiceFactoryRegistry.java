package org.alfresco.repo.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.lock.LockService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.version.VersionService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;


/**
 * Implementation of a Service Registry based on the definition of
 * Services contained within a Spring Bean Factory.
 * 
 * @author David Caruana
 */
/*package*/ class ServiceFactoryRegistry
    implements BeanFactoryAware, BeanFactoryPostProcessor, ServiceRegistry
{
    // Bean Factory within which the registry lives
    private BeanFactory factory = null;
    
    // Map of Service QNames to implementations within the factory
    private Map<QName, String> services = new HashMap<QName, String>();

    // Pre-defined core services
    private static final QName NODE_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "nodeService");
    private static final QName CONTENT_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "contentService");
    private static final QName VERSION_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "versionService");
    private static final QName LOCK_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "lockService");
    private static final QName DICTIONARY_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "dictionaryService");
    
    
    
     /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        String[] beans = beanFactory.getBeanDefinitionNames(ServiceFactory.class);
        for (String bean : beans)
        {
            BeanDefinition def = (BeanDefinition)beanFactory.getBeanDefinition(bean);
            if (!def.isAbstract())
            {
                ServiceDescriptor descriptor = (ServiceDescriptor)beanFactory.getBean(BeanFactory.FACTORY_BEAN_PREFIX + bean);
                services.put(descriptor.getName(), bean);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.factory = beanFactory;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getServices()
     */
    public Collection<QName> getServices()
    {
        return Collections.unmodifiableSet(services.keySet());
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#isServiceProvided(org.alfresco.repo.ref.QName)
     */
    public boolean isServiceProvided(QName service)
    {
        return services.containsKey(service);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getServiceDescriptor(org.alfresco.repo.ref.QName)
     */
    public ServiceDescriptor getServiceDescriptor(QName service)
    {
        ServiceDescriptor descriptor = null;
        String bean = services.get(service);
        if (bean != null)
        {
            descriptor = (ServiceDescriptor)factory.getBean(BeanFactory.FACTORY_BEAN_PREFIX + bean);
        }
        return descriptor;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getService(org.alfresco.repo.ref.QName)
     */
    public Object getService(QName service)
    {
        Object theService = null;
        String bean = services.get(service);
        if (bean != null)
        {
            theService = factory.getBean(bean);
        }
        return theService;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getNodeService()
     */
    public NodeService getNodeService()
    {
        return (NodeService)getService(NODE_SERVICE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getContentService()
     */
    public ContentService getContentService()
    {
        return (ContentService)getService(CONTENT_SERVICE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getVersionService()
     */
    public VersionService getVersionService()
    {
        return (VersionService)getService(VERSION_SERVICE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getLockService()
     */
    public LockService getLockService()
    {
        return (LockService)getService(LOCK_SERVICE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getDictionaryService()
     */
    public DictionaryService getDictionaryService()
    {
        return (DictionaryService)getService(DICTIONARY_SERVICE);
    }

}
