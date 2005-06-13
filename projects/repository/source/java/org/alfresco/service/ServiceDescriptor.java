package org.alfresco.service;

import java.util.Collection;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;


/**
 * A Service Description.
 * 
 * @author David Caruana
 *
 */
public interface ServiceDescriptor
{
    /**
     * @return  the qualified name of the service
     */
    public QName getQualifiedName();
    
    /**
     * @return  a description of the service
     */
    public String getDescription();

    /**
     * @return  the service interface
     */
    public Class getInterface();

    /**
     * @return the names of the protocols supported
     */
    public Collection<String> getSupportedStoreProtocols();
    
    /**
     * @return the Store Refs of the stores supported
     */
    public Collection<StoreRef> getSupportedStores();
}
