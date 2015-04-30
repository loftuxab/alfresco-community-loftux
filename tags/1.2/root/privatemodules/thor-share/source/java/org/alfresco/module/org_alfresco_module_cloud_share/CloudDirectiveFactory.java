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
package org.alfresco.module.org_alfresco_module_cloud_share;


import org.springframework.extensions.directives.DefaultDirectiveFactory;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;

public class CloudDirectiveFactory extends DefaultDirectiveFactory
{
    @Override
    public ChecksumResourceDirective createChecksumResourceDirective(String directiveName,
            ModelObject object, ExtensibilityModel extensibilityModel, RequestContext context) 
    {
        ChecksumResourceDirective d = new ChecksumResourceDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }

    @Override
    public MessagesDependencyDirective createMessagesDependencyDirective(String directiveName,
            ModelObject object, ExtensibilityModel extensibilityModel, RequestContext context) 
    {
        MessagesDependencyDirective d = new MessagesDependencyDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }

    @Override
    public CssDependencyDirective createCssDependencyDirective(String directiveName,
            ModelObject object, ExtensibilityModel extensibilityModel, RequestContext context) 
    {
        CssDependencyDirective d = new CssDependencyDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }

    @Override
    public JavaScriptDependencyDirective createJavaScriptDependencyDirective(String directiveName,
            ModelObject object, ExtensibilityModel extensibilityModel, RequestContext context) 
    {
        JavaScriptDependencyDirective d = new JavaScriptDependencyDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
}
