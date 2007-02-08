/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.i18n;

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
