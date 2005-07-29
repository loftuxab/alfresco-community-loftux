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
package org.alfresco.service;

import java.util.Collection;

import javax.transaction.UserTransaction;

import org.alfresco.repo.importer.ImporterService;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.configuration.ConfigurableService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.search.CategoryService;
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
    static final QName IMPORTER_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "ImporterService");
    static final QName CONFIGURABLE_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "ConfigurableService");
    
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
     * @return  the namespace service (or null, if one is not provided)
     */
    NamespaceService getNamespaceService();
    
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
    
    /**
     * @return  the category service (or null, if one is not provided)
     */
    CategoryService getCategoryService();
    
    /**
     * @return Returns the importer service or null if not present
     */
    ImporterService getImporterService();
    
    /**
     * @return the rule service (or null, if one is not provided)
     */
    RuleService getRuleService();
    
    /**
     * @return the configurable service (or null if one is not provided)
     */
    ConfigurableService getConfigurableService();
}
