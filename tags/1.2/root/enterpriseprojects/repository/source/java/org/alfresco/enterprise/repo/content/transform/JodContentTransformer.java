/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.transform;

import java.io.File;

import net.sf.jooreports.converter.DocumentFormat;

import org.alfresco.enterprise.repo.content.JodConverter;
import org.alfresco.repo.content.transform.ContentTransformerWorker;
import org.alfresco.repo.content.transform.OOoContentTransformerHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.springframework.beans.factory.InitializingBean;

/**
 * Makes use of the {@link http://code.google.com/p/jodconverter/} library and an installed
 * OpenOffice application to perform OpenOffice-driven conversions.
 * 
 * @author Neil McErlean
 */
public class JodContentTransformer extends OOoContentTransformerHelper implements ContentTransformerWorker, InitializingBean
{
    private static Log logger = LogFactory.getLog(JodContentTransformer.class);

    private JodConverter jodconverter;

    public void setJodConverter(JodConverter jodc)
    {
        this.jodconverter = jodc;
    }

    @Override
    protected Log getLogger()
    {
        return logger;
    }
    
    @Override
    protected String getTempFilePrefix()
    {
        return "JodContentTransformer";
    }
    
    @Override
    public boolean isAvailable()
    {
    	return jodconverter.isAvailable();
    }

    @Override
    protected void convert(File tempFromFile, DocumentFormat sourceFormat, File tempToFile,
            DocumentFormat targetFormat)
    {
        OfficeDocumentConverter converter = new OfficeDocumentConverter(jodconverter.getOfficeManager());
        converter.convert(tempFromFile, tempToFile);
    }
}
