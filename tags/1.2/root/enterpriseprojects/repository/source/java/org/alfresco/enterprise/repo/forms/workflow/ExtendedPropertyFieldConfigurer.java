/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.forms.workflow;

import java.util.List;

import org.alfresco.repo.forms.processor.workflow.ExtendedPropertyFieldProcessor;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.InitializingBean;

/**
 * Configurer for the {@link ExtendedPropertyFieldProcessor}.
 * 
 * @author Frederik Heremans
 */
public class ExtendedPropertyFieldConfigurer implements InitializingBean
{
    private NamespaceService namespaceService;
    private List<String> escapedProperties;
    private ExtendedPropertyFieldProcessor extendedPropertyFieldProcessor;
    
    public void setEscapedProperties(List<String> escapedProperties)
    {
        this.escapedProperties = escapedProperties;
    }
    
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    public void setExtendedPropertyFieldProcessor(
                ExtendedPropertyFieldProcessor extendedPropertyFieldProcessor)
    {
        this.extendedPropertyFieldProcessor = extendedPropertyFieldProcessor;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if(escapedProperties != null)
        {
            for(String propQname : escapedProperties) 
            {
                extendedPropertyFieldProcessor.addEscapedPropertyName(
                            QName.createQName(propQname, namespaceService));
            }
        }
    }
}
