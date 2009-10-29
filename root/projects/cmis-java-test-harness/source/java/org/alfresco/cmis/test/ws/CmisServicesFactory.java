/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.cmis.test.ws;

import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.alfresco.repo.cmis.ws.DiscoveryServiceLocator;
import org.alfresco.repo.cmis.ws.DiscoveryServicePortBindingStub;
import org.alfresco.repo.cmis.ws.MultiFilingServiceLocator;
import org.alfresco.repo.cmis.ws.MultiFilingServicePortBindingStub;
import org.alfresco.repo.cmis.ws.NavigationServiceLocator;
import org.alfresco.repo.cmis.ws.NavigationServicePortBindingStub;
import org.alfresco.repo.cmis.ws.ObjectServiceLocator;
import org.alfresco.repo.cmis.ws.ObjectServicePortBindingStub;
import org.alfresco.repo.cmis.ws.RelationshipServiceLocator;
import org.alfresco.repo.cmis.ws.RelationshipServicePortBindingStub;
import org.alfresco.repo.cmis.ws.RepositoryServiceLocator;
import org.alfresco.repo.cmis.ws.RepositoryServicePortBindingStub;
import org.alfresco.repo.cmis.ws.VersioningServiceLocator;
import org.alfresco.repo.cmis.ws.VersioningServicePortBindingStub;
import org.apache.axis.client.Stub;

/**
 * This factory controls CMIS services callers creation and configuration. Also it introduce some cache functionality to improve performance and clarity
 * 
 * @author Dmitry Velichkevich
 */
public class CmisServicesFactory
{
    private static final int TIMEOUT = 60000;

    private AbstractServiceClient repositoryServiceDescriptor;
    private AbstractServiceClient discoveryServiceDescriptor;
    private AbstractServiceClient objectServiceDescriptor;
    private AbstractServiceClient navigationServiceDescriptor;
    private AbstractServiceClient multiFilingServiceDescriptor;
    private AbstractServiceClient versioningServiceDescriptor;
    private AbstractServiceClient relationshipServiceDescriptor;

    private Map<String, Stub> servicesCache = new HashMap<String, Stub>();

    public CmisServicesFactory()
    {
    }

    public AbstractServiceClient getRepositoryServiceDescriptor()
    {
        return repositoryServiceDescriptor;
    }

    public void setRepositoryServiceDescriptor(AbstractServiceClient repositoryServiceDescriptor)
    {
        this.repositoryServiceDescriptor = repositoryServiceDescriptor;
    }

    public AbstractServiceClient getNavigationServiceDescriptor()
    {
        return navigationServiceDescriptor;
    }

    public void setNavigationServiceDescriptor(AbstractServiceClient navigationServiceDescriptor)
    {
        this.navigationServiceDescriptor = navigationServiceDescriptor;
    }

    public AbstractServiceClient getObjectServiceDescriptor()
    {
        return objectServiceDescriptor;
    }

    public void setObjectServiceDescriptor(AbstractServiceClient objectServiceDescriptor)
    {
        this.objectServiceDescriptor = objectServiceDescriptor;
    }

    public AbstractServiceClient getMultiFilingServiceDescriptor()
    {
        return multiFilingServiceDescriptor;
    }

    public void setMultiFilingServiceDescriptor(AbstractServiceClient multiFilingServiceDescriptor)
    {
        this.multiFilingServiceDescriptor = multiFilingServiceDescriptor;
    }

    public AbstractServiceClient getVersioningServiceDescriptor()
    {
        return versioningServiceDescriptor;
    }

    public void setVersioningServiceDescriptor(AbstractServiceClient versioningServiceDescriptor)
    {
        this.versioningServiceDescriptor = versioningServiceDescriptor;
    }

    public AbstractServiceClient getRelationshipServiceDescriptor()
    {
        return relationshipServiceDescriptor;
    }

    public void setRelationshipServiceDescriptor(AbstractServiceClient relationshipServiceDescriptor)
    {
        this.relationshipServiceDescriptor = relationshipServiceDescriptor;
    }

    public AbstractServiceClient getDiscoveryServiceDescriptor()
    {
        return discoveryServiceDescriptor;
    }

    public void setDiscoveryServiceDescriptor(AbstractServiceClient discoveryServiceDescriptor)
    {
        this.discoveryServiceDescriptor = discoveryServiceDescriptor;
    }

    /**
     * Gets port for Repository Service with the default configured access URL
     * 
     * @return - RepositoryServicePortBindingStub
     * @throws ServiceException
     */
    public RepositoryServicePortBindingStub getRepositoryService() throws ServiceException
    {
        return getRepositoryService(repositoryServiceDescriptor.getServerUrl() + repositoryServiceDescriptor.getService().getPath());
    }

    /**
     * Gets port for Repository Service
     * 
     * @param address - address where service resides
     * @return - RepositoryServicePortBindingStub
     * @throws ServiceException
     */
    public RepositoryServicePortBindingStub getRepositoryService(String address) throws ServiceException
    {
        RepositoryServicePortBindingStub result = ((address != null) && (servicesCache.get(address) != null)) ? ((RepositoryServicePortBindingStub) servicesCache.get(address))
                : (null);

        if (result == null)
        {
            RepositoryServiceLocator locator = new RepositoryServiceLocator(repositoryServiceDescriptor.getEngineConfiguration());
            locator.setRepositoryServicePortEndpointAddress(address);
            result = (RepositoryServicePortBindingStub) locator.getRepositoryServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);

            servicesCache.put(address, result);
        }

        return result;
    }

    /**
     * Gets port for Discovery Service with the default configured access URL
     * 
     * @return - DiscoveryServicePortBindingStub
     * @throws ServiceException
     */
    public DiscoveryServicePortBindingStub getDiscoveryService() throws ServiceException
    {
        return getDiscoveryService(discoveryServiceDescriptor.getServerUrl() + discoveryServiceDescriptor.getService().getPath());
    }

    /**
     * Gets port for Discovery Service
     * 
     * @param address - address where service resides
     * @return DiscoveryServicePortBindingStub
     * @throws ServiceException
     */
    public DiscoveryServicePortBindingStub getDiscoveryService(String address) throws ServiceException
    {
        DiscoveryServicePortBindingStub result = ((address != null) && (servicesCache.get(address) != null)) ? ((DiscoveryServicePortBindingStub) servicesCache.get(address))
                : (null);

        if (result == null)
        {
            DiscoveryServiceLocator locator = new DiscoveryServiceLocator(discoveryServiceDescriptor.getEngineConfiguration());
            locator.setDiscoveryServicePortEndpointAddress(address);
            result = (DiscoveryServicePortBindingStub) locator.getDiscoveryServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);

            servicesCache.put(address, result);
        }

        return result;
    }

    /**
     * Gets port for Object Service with the default configured access URL
     * 
     * @return - ObjectServicePortBindingStub
     * @throws ServiceException
     */
    public ObjectServicePortBindingStub getObjectService() throws ServiceException
    {
        return getObjectService(objectServiceDescriptor.getServerUrl() + objectServiceDescriptor.getService().getPath());
    }

    /**
     * Gets port for Object Service
     * 
     * @param address - address where service resides
     * @return - ObjectServicePortBindingStub
     * @throws ServiceException
     */
    public ObjectServicePortBindingStub getObjectService(String address) throws ServiceException
    {
        ObjectServicePortBindingStub result = ((address != null) && (servicesCache.get(address) != null)) ? ((ObjectServicePortBindingStub) servicesCache.get(address)) : (null);

        if (result == null)
        {
            ObjectServiceLocator locator = new ObjectServiceLocator(objectServiceDescriptor.getEngineConfiguration());
            locator.setObjectServicePortEndpointAddress(address);
            result = (ObjectServicePortBindingStub) locator.getObjectServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);

            servicesCache.put(address, result);
        }

        return result;
    }

    /**
     * Gets port for Navigation Service with the default configured access URL
     * 
     * @return - NavigationServicePortBindingStub
     * @throws ServiceException
     */
    public NavigationServicePortBindingStub getNavigationService() throws ServiceException
    {
        return getNavigationService(navigationServiceDescriptor.getServerUrl() + navigationServiceDescriptor.getService().getPath());
    }

    /**
     * Gets port for Navigation Service
     * 
     * @param address - address where service resides
     * @return - RepositoryServicePortBindingStub
     * @throws ServiceException
     */
    public NavigationServicePortBindingStub getNavigationService(String address) throws ServiceException
    {
        NavigationServicePortBindingStub result = ((address != null) && (servicesCache.get(address) != null)) ? ((NavigationServicePortBindingStub) servicesCache.get(address))
                : (null);

        if (result == null)
        {
            NavigationServiceLocator locator = new NavigationServiceLocator(navigationServiceDescriptor.getEngineConfiguration());
            locator.setNavigationServicePortEndpointAddress(address);
            result = (NavigationServicePortBindingStub) locator.getNavigationServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);

            servicesCache.put(address, result);
        }

        return result;
    }

    /**
     * Gets port for MultiFiling Service with the default configured access URL
     * 
     * @return - MultiFilingServicePortBindingStub
     * @throws ServiceException
     */
    public MultiFilingServicePortBindingStub getMultiFilingServicePort() throws ServiceException
    {
        return getMultiFilingServicePort(multiFilingServiceDescriptor.getServerUrl() + multiFilingServiceDescriptor.getService().getPath());
    }

    /**
     * Gets port for MultiFiling Service
     * 
     * @param address - address where service resides
     * @return - MultiFilingServicePortBindingStub
     * @throws ServiceException
     */
    public MultiFilingServicePortBindingStub getMultiFilingServicePort(String address) throws ServiceException
    {
        MultiFilingServicePortBindingStub result = ((address != null) && (servicesCache.get(address) != null)) ? ((MultiFilingServicePortBindingStub) servicesCache.get(address))
                : (null);

        if (result == null)
        {
            MultiFilingServiceLocator locator = new MultiFilingServiceLocator(multiFilingServiceDescriptor.getEngineConfiguration());
            locator.setMultiFilingServicePortEndpointAddress(address);
            result = (MultiFilingServicePortBindingStub) locator.getMultiFilingServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);

            servicesCache.put(address, result);
        }

        return result;
    }

    /**
     * Gets port for Versioning Service with the default configured access URL
     * 
     * @return - VersioningServicePortBindingStub
     * @throws ServiceException
     */
    public VersioningServicePortBindingStub getVersioningService() throws ServiceException
    {
        return getVersioningService(versioningServiceDescriptor.getServerUrl() + versioningServiceDescriptor.getService().getPath());
    }

    /**
     * Gets port for Versioning Service
     * 
     * @param address - address where service resides
     * @return - VersioningServicePortBindingStub
     * @throws ServiceException
     */
    public VersioningServicePortBindingStub getVersioningService(String address) throws ServiceException
    {
        VersioningServicePortBindingStub result = ((address != null) && (servicesCache.get(address) != null)) ? ((VersioningServicePortBindingStub) servicesCache.get(address))
                : (null);

        if (result == null)
        {
            VersioningServiceLocator locator = new VersioningServiceLocator(versioningServiceDescriptor.getEngineConfiguration());
            locator.setVersioningServicePortEndpointAddress(address);
            result = (VersioningServicePortBindingStub) locator.getVersioningServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);

            servicesCache.put(address, result);
        }

        return result;
    }

    /**
     * Gets port for Relationship Service with the default configured access URL
     * 
     * @return - RelationshipServicePortBindingStub
     * @throws ServiceException
     */
    public RelationshipServicePortBindingStub getRelationshipService() throws ServiceException
    {
        return getRelationshipService(relationshipServiceDescriptor.getServerUrl() + relationshipServiceDescriptor.getService().getPath());
    }
    
    /**
     * Gets port for Relationship Service
     * 
     * @param address - address where service resides
     * @return - RelationshipServicePortBindingStub
     * @throws ServiceException
     */
    public RelationshipServicePortBindingStub getRelationshipService(String address) throws ServiceException
    {
        RelationshipServicePortBindingStub result = ((address != null) && (servicesCache.get(address) != null)) ? ((RelationshipServicePortBindingStub) servicesCache.get(address))
                : (null);
        
        if (result == null)
        {
            RelationshipServiceLocator locator = new RelationshipServiceLocator(relationshipServiceDescriptor.getEngineConfiguration());
            locator.setRelationshipServicePortEndpointAddress(address);
            result = (RelationshipServicePortBindingStub) locator.getRelationshipServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            
            servicesCache.put(address, result);
        }
        
        return result;
    }
}
