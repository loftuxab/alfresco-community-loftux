/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

import org.alfresco.service.namespace.QName;


/**
 * Content Model Constants
 */
public interface CloudModel
{
    // Namespace
    static final String CLOUD_MODEL_1_0_URI = "http://www.alfresco.org/model/cloud/1.0";
    
    // Aspects
    
    static final QName ASPECT_ACCOUNTS = QName.createQName(CLOUD_MODEL_1_0_URI, "accounts");
    static final QName ASPECT_EXTERNAL_PERSON = QName.createQName(CLOUD_MODEL_1_0_URI, "personExternal"); // eg. see cloud-usage-quota-context.xml
    static final QName ASPECT_NETWORK_ADMIN = QName.createQName(CLOUD_MODEL_1_0_URI, "networkAdmin");
    
    
    // Properties
    
    static final QName PROP_HOME_ACCOUNT = QName.createQName(CLOUD_MODEL_1_0_URI, "homeAccountId");
    static final QName PROP_SECONDARY_ACCOUNTS = QName.createQName(CLOUD_MODEL_1_0_URI, "secondaryAccountIds");
    static final QName PROP_DEFAULT_ACCOUNT = QName.createQName(CLOUD_MODEL_1_0_URI, "defaultAccountId");
    
    static final QName PROP_EXTERNAL_PERSON = QName.createQName(CLOUD_MODEL_1_0_URI, "tenantSrcDst"); // marker property only (used when creating the person)
    
}
