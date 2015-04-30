/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.alfresco.service.cmr.repository.CropSourceOptions;
import org.alfresco.service.cmr.repository.PagedSourceOptions;
import org.alfresco.service.cmr.repository.TemporalSourceOptions;
import org.alfresco.service.cmr.repository.TransformationOptionLimits;
import org.alfresco.service.cmr.repository.TransformationOptionPair;
import org.alfresco.service.cmr.repository.TransformationSourceOptions;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * Setup Jackson for TransformationOptions related classes.
 * 
 * @author Matt Ward
 */
public class TransformationOptionsModule extends SimpleModule
{
    public TransformationOptionsModule()
    {
        super("TransformationOptionsModule", new Version(0, 0, 1, null));
    }
    
    @Override
    public void setupModule(SetupContext ctx)
    {
        ctx.setMixInAnnotations(TransformationOptionPair.class,   TransformationOptionPairMixin.class);
        ctx.setMixInAnnotations(TransformationOptionLimits.class, TransformationOptionLimitsMixin.class);
        
        ctx.setMixInAnnotations(TransformationSourceOptions.class, TransformationSourceOptionsMixin.class);
        ctx.setMixInAnnotations(CropSourceOptions.class,           CropSourceOptionsMixin.class);
        ctx.setMixInAnnotations(PagedSourceOptions.class,          PagedSourceOptionsMixin.class);
        ctx.setMixInAnnotations(TemporalSourceOptions.class,       TemporalSourceOptionsMixin.class);
    }
}
