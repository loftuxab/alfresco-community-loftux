/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.module.org_alfresco_module_cloud.repo.content.transform;

import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.repo.content.transform.ContentTransformerWorker;



public interface RemoteAlfrescoTransformerWorker extends ContentTransformerWorker
{
    /**
     * Are remote transformations enabled? Returns true if the repository should
     * attempt to delegate transformations to a remote transformation server or
     * return false if remote transformations should not be used.
     * 
     * @return boolean - true if remote transformations are enabled.
     */
    boolean isEnabled();
    
    /**
     * Sets whether remote transformations should be enabled.
     * 
     * @see #isEnabled()
     * @param enabled the enabled to set
     */
    void setEnabled(boolean enabled);
    
    /**
     * Use the HttpClientFactory to obtain an HttpClient instance.
     */
    void initHttpClient();
    
    /**
     * @return the username
     */
    String getUsername();

    /**
     * @param username the username to set
     */
    void setUsername(String username);

    /**
     * @return the password
     */
    String getPassword();

    /**
     * @param password the password to set
     */
    void setPassword(String password);

    /**
     * @return the httpClientFactory
     */
    HttpClientFactory getHttpClientFactory();
    
    /**
     * @param httpClientFactory the httpClientFactory to set
     */
    void setHttpClientFactory(HttpClientFactory httpClientFactory);
}