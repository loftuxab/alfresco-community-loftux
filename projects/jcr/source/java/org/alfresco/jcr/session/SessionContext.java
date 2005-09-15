/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jcr.session;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespacePrefixResolver;


/**
 * Alfresco Session Context
 * 
 * @author David Caruana
 */
public interface SessionContext
{
    /**
     * Get the Service Registry
     * 
     * @return  the service registry
     */
    public ServiceRegistry getServiceRegistry();
    
    /**
     * Get the Authentication Service
     * 
     * @return  the authentication service
     */
    public AuthenticationService getAuthenticationService();

    /**
     * Get the Namespace Resolver for this session
     * 
     * @return  the namespace resolver
     */
    public NamespacePrefixResolver getNamespaceResolver();
    
    /**
     * Get the authenticated ticket for this session
     * 
     * @return  the ticket
     */
    public String getTicket();

}
