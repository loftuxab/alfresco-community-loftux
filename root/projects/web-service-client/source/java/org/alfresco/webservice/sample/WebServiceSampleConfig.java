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
package org.alfresco.webservice.sample;

import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.types.StoreEnum;

/**
 * @author Roy Wetherall
 */
public interface WebServiceSampleConfig
{
    /** Admin user name and password used to connect to the repository */
    static final String USERNAME = "admin";
    static final String PASSWORD = "admin";
    
    /** The store used throughout the samples */
    static final Store STORE = new Store(StoreEnum.workspace, "SpacesStore");
}
