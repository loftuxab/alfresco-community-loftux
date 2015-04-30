/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_cloud_share;

import org.springframework.extensions.surf.extensibility.ExtensibilityModel;


/**
 * <p>This directive is used to address the problem of browsers caching stale i18n messages for the application. It modifies the 
 * WebScript URL requested to include an checksum generated from the WebScript results to ensure that when those results change
 * that the browser will be forced to request the updated version (because the browser cached version will be stored
 * against a different URL).</p>
 * 
 * @author David Draper
 */
public class MessagesDependencyDirective extends org.springframework.extensions.directives.MessagesDependencyDirective
{
    public MessagesDependencyDirective(String directiveName, ExtensibilityModel model) 
    {
        super(directiveName, model);
    }

    @Override
    protected String getToInsert(ProcessedDependency pd)
    {
        return pd.getToInsert().replace(TenantUtil.getTenantName(), TenantUtil.DEFAULT_TENANT_NAME);
    }
}
