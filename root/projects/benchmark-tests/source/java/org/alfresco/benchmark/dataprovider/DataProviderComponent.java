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
package org.alfresco.benchmark.dataprovider;

import java.util.List;
import java.util.Map;

/**
 * Provides test data used during benchmark tests
 * 
 * @author Roy Wetherall
 */
public interface DataProviderComponent
{    
    Map<String, Object> getPropertyData(RepositoryProfile repositoryProfile, List<PropertyProfile> propertyProfiles);    
}
