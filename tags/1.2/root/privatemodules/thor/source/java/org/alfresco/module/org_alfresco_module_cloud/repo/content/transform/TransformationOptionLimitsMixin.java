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

import org.alfresco.service.cmr.repository.TransformationOptionPair;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Provides Jackson mapping annotations for the TransformationOptionLimits class.
 * 
 * @author Matt Ward
 */
public abstract class TransformationOptionLimitsMixin
{
    @JsonProperty TransformationOptionPair time;
    @JsonProperty TransformationOptionPair kbytes;
    @JsonProperty TransformationOptionPair pages;
    
    @JsonIgnore abstract TransformationOptionPair getTimePair();
    @JsonIgnore abstract TransformationOptionPair getKBytesPair();
    @JsonIgnore abstract TransformationOptionPair getPagesPair();
    
    @JsonIgnore abstract long getTimeoutMs();
    @JsonIgnore abstract long getReadLimitTimeMs();
    @JsonIgnore abstract long getMaxSourceSizeKBytes();
    @JsonIgnore abstract long getReadLimitKBytes();
    @JsonIgnore abstract int getMaxPages();
    @JsonIgnore abstract int getPageLimit();
}
