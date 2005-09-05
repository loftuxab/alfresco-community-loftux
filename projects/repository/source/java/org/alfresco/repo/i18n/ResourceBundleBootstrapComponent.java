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
package org.alfresco.repo.i18n;

import java.util.List;

/**
 * Resource bundle bootstrap component.
 * <p>
 * Provides a convenient way to make resource bundles available via Spring config.
 * 
 * @author Roy Wetherall
 */
public class ResourceBundleBootstrapComponent
{
    /**
     * Set the resource bundles to be registered.  This should be a list of resource
     * bundle base names whose content will be made available across the repository.
     * 
     * @param resourceBundles   the resource bundles
     */
    public void setResourceBundles(List<String> resourceBundles)
    {
        for (String resourceBundle : resourceBundles)
        {
            I18NUtil.registerResourceBundle(resourceBundle);
        }
    }
}
