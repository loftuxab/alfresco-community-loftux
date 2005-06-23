/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.service.ServiceDescriptor;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;


/**
 * Implementation of a Service Registry based on the definition of
 * Services contained within a Spring Bean Factory.
 * 
 * @author David Caruana
 */
public class ServiceDescriptorRegistry
    implements BeanFactoryAware, BeanFactoryPostProcessor, ServiceRegistry
{
    // Bean Factory within which the registry lives
    private BeanFactory beanFactory = null;

    // Service Descriptor map
    private Map<QName, BeanServiceDescriptor> descriptors = new HashMap<QName, BeanServiceDescriptor>();


    /* (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        Map beans = beanFactory.getBeansOfType(BeanServiceDescriptor.class);
        for (Object bean : beans.values())
        {
            BeanServiceDescriptor descriptor = (BeanServiceDescriptor)bean;
            descriptors.put(descriptor.getQualifiedName(), descriptor);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getServices()
     */
    public Collection<QName> getServices()
    {
        return Collections.unmodifiableSet(descriptors.keySet());
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#isServiceProvided(org.alfresco.repo.ref.QName)
     */
    public boolean isServiceProvided(QName service)
    {
        return descriptors.containsKey(service);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getServiceDescriptor(org.alfresco.repo.ref.QName)
     */
    public ServiceDescriptor getServiceDescriptor(QName service)
    {
        return descriptors.get(service);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.service.ServiceRegistry#getService(org.alfresco.repo.ref.QName)
     */
    public Object getService(QName service)
    {
        Object serviceBean = null;
        BeanServiceDescriptor descriptor = descriptors.get(service);
        if (descriptor != null)
        {
            serviceBean = beanFactory.getBean(descriptor.getImplementation()); 
        }
        return serviceBean;
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
     * @see org.alfresco.service.ServiceRegistry#getMimetypeService()
     */
    public MimetypeService getMimetypeService()
    {
        return (MimetypeService)getService(MIMETYPE_SERVICE);
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

    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceRegistry#getSearchService()
     */
    public SearchService getSearchService()
    {
        return (SearchService)getService(SEARCH_SERVICE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceRegistry#getUserTransaction()
     */
    public UserTransaction getUserTransaction()
    {
        return (UserTransaction)getService(USER_TRANSACTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceRegistry#getCopyService()
     */
    public CopyService getCopyService()
    {
        return (CopyService)getService(COPY_SERVICE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceRegistry#getCheckOutCheckInService()
     */
    public CheckOutCheckInService getCheckOutCheckInService()
    {
        return (CheckOutCheckInService)getService(COCI_SERVICE);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceRegistry#getCategoryService()
     */
    public CategoryService getCategoryService()
    {
        return (CategoryService)getService(CATEGORY_SERVICE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.ServiceRegistry#getNamespaceService()
     */
    public NamespaceService getNamespaceService()
    {
        return (NamespaceService)getService(NAMESPACE_SERVICE);
    }
    
}
