/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud;

import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

public class InMemoryTestContext
{
    public static final String[] CONFIG_LOCATIONS = new String[] { "classpath:org/alfresco/module/org_alfresco_module_cloud/accounts/test-account-service-context.xml",
    		"classpath:alfresco/web-scripts-application-context.xml" };

    public static final String WEB_CONFIG_LOCATION = "classpath:org/alfresco/module/org_alfresco_module_cloud/accounts/test-account-web-service-context.xml";
    
    public static ApplicationContext getContext()
    {
        return ApplicationContextHelper.getApplicationContext(CONFIG_LOCATIONS);
    }

}
