package org.alfresco.repo.service;

import org.alfresco.repo.ref.QName;


/**
 * A Service Description.
 * 
 * @author David Caruana
 *
 */
public interface ServiceDescriptor extends StoreRedirector
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
}
