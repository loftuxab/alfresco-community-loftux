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
 * <p>This directive is used to convert resource URLs into resource URLs containing a checksum that uniquely 
 * matches the resource contents.</p>
 * 
 * @author David Draper
 */
public class ChecksumResourceDirective extends org.springframework.extensions.directives.ChecksumResourceDirective
{
    public ChecksumResourceDirective(String directiveName, ExtensibilityModel extensibilityModel) {
        super(directiveName, extensibilityModel);
    }

    @Override
    protected String getToInsert(ProcessedDependency pd)
    {
        return pd.getToInsert().replace(TenantUtil.getTenantName(), TenantUtil.DEFAULT_TENANT_NAME);
    }
}
