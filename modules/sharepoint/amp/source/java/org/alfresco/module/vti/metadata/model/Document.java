package org.alfresco.module.vti.metadata.model;

import java.io.InputStream;

/**
 * <p>Represents single MS Office file with content and meta-information</p>
 * 
 * @author PavelYur 
 */
public class Document extends DocMetaInfo
{

    // content stream
    private InputStream inputStream;
    
    /**
     * Default constructor
     */
    public Document()
    {
        super(false);
    }

    /**
     * @return file content
     */
    public InputStream getInputStream()
    {        
        return inputStream;
    }

    /**
     * @param inputStream the inputStream to set
     */
    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

}
