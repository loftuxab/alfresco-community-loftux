/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.metadata;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.content.metadata.AbstractMappingMetadataExtracter;
import org.alfresco.repo.content.metadata.OpenOfficeMetadataWorker;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.util.PropertyCheck;

/**
 * Extracts values from Open Office documents into the following:
 * <pre>
 *   <b>author:</b>                 --      cm:author
 *   <b>title:</b>                  --      cm:title
 *   <b>description:</b>            --      cm:description
 * </pre>
 * 
 * @author Neil McErlean
 */
public class JodConverterMetadataExtracter extends AbstractMappingMetadataExtracter implements OpenOfficeMetadataWorker
{
    private OpenOfficeMetadataWorker worker;
    private static final Set<String> typedEmptySet = Collections.emptySet();
    
    public JodConverterMetadataExtracter()
    {
        this(typedEmptySet);
    }
    
    public JodConverterMetadataExtracter(Set<String> supportedMimetypes)
    {
        super(supportedMimetypes);
    }
    
    public void setWorker(OpenOfficeMetadataWorker worker)
    {
        this.worker = worker;
    }
    
    @Override
    public synchronized void init()
    {
        PropertyCheck.mandatory("JodConverterMetadataExtracter", "worker", worker);
        
        // Base initialization
        super.init();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConnected()
    {
        return worker.isConnected();
    }

    /**
     * Perform the default check, but also check if the OpenOffice connection is good.
     */
    @Override
    public boolean isSupported(String sourceMimetype)
    {
        if (!isConnected())
        {
            return false;
        }
        return super.isSupported(sourceMimetype);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Serializable> extractRaw(ContentReader reader) throws Throwable
    {
        Map<String, Serializable> rawProperties = newRawMap();
        Map<String, Serializable> result = this.worker.extractRaw(reader);
        for (Map.Entry<String, Serializable> entry : result.entrySet())
        {
            putRawValue(entry.getKey(), entry.getValue(), rawProperties);
        }
        return rawProperties;
    }
}
