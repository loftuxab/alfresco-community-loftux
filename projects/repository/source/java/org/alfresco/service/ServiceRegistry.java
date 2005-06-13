package org.alfresco.service;

import java.util.Collection;

import javax.transaction.UserTransaction;

import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


/**
 * Registry of Repository Services.
 * 
 * Provides access to the public services of the Repository as well
 * descriptions of those services.
 * 
 * @author David Caruana
 */
public interface ServiceRegistry
{
    // Service Registry
    static final String SERVICE_REGISTRY = "AlfServiceRegistry";
    
    // Core Services
    static final QName USER_TRANSACTION = QName.createQName(NamespaceService.ALFRESCO_URI, "UserTransaction");
    static final QName REGISTRY_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "ServiceRegistry");
    static final QName NAMESPACE_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "NamespaceService");
    static final QName DICTIONARY_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "DictionaryService");
    static final QName NODE_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "NodeService");
    static final QName CONTENT_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "ContentService");
    static final QName MIMETYPE_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "MimetypeService");
    static final QName SEARCH_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "SearchService");
    static final QName CATEGORY_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "CategoryService");
    static final QName COPY_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "CopyService");
    static final QName LOCK_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "LockService");
    static final QName VERSION_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "VersionService");
    static final QName COCI_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "CheckoutCheckinService");
    static final QName RULE_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RuleService");

    
    /**
     * Get Services provided by Repository
     *
     * @return  list of provided Services
     */
    Collection<QName> getServices();
    

    /**
     * Is Service Provided?
     * 
     * @param serviceName  name of service to test provision of
     * @return  true => provided, false => not provided
     */
    boolean isServiceProvided(QName service);
    

    /**
     * Get Service Meta Data
     *
     * @param serviceName  name of service to retrieve meta data for
     * @return  the service meta data
     */
    ServiceDescriptor getServiceDescriptor(QName service);
    

    /** 
     * Get Service Interface
     *
     * @param serviceName  name of service to retrieve
     * @return  the service interface
     */  
    Object getService(QName service);
    
    /**
     * @return  the user transaction
     */
    UserTransaction getUserTransaction();

    
    /**
     * @return  the node service (or null, if one is not provided)
     */
    NodeService getNodeService();
    

    /**
     * @return  the content service (or null, if one is not provided)
     */
    ContentService getContentService();
    
    /**
     * @return  the mimetype service (or null, if one is not provided)
     */
    MimetypeService getMimetypeService();

    /**
     * @return  the search service (or null, if one is not provided)
     */
    SearchService getSearchService();
    

    /**
     * @return  the version service (or null, if one is not provided)
     */
    VersionService getVersionService();
    

    /**
     * @return  the lock service (or null, if one is not provided)
     */
    LockService getLockService();


    /**
     * @return  the dictionary service (or null, if one is not provided)
     */
    DictionaryService getDictionaryService();
 
    /**
     * @return  the copy service (or null, if one is not provided)
     */
    CopyService getCopyService();
    
    /**
     * @return  the checkout / checkin service (or null, if one is not provided)
     */
    CheckOutCheckInService getCheckOutCheckInService();   
}
