package org.alfresco.repo.service;

import java.util.Collection;

import org.alfresco.repo.ref.QName;


/**
 * A Service Description.
 * 
 * @author David Caruana
 *
 */
public interface ServiceDescriptor
{
    /**
     * @return  the name of the service
     */
    public QName getName();
    
    /**
     * @return  a description of the service
     */
    public String getDescription();

    /**
     * @return  the service interface
     */
    public Class getInterface();

    /**
     * @return  the store types supported by the service (or null, if not applicable)
     */
    public Collection<String> getSupportedStores();
}
