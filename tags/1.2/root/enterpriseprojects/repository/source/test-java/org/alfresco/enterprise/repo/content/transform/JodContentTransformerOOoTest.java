/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.transform;

import static org.junit.Assert.assertNotNull;

import org.alfresco.enterprise.repo.content.AbstractJodConverterBasedTest;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.thumbnail.ThumbnailDefinition;
import org.alfresco.repo.thumbnail.ThumbnailRegistry;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * 
 * @author Neil McErlean
 * @since 3.2 SP1
 */
public class JodContentTransformerOOoTest extends AbstractJodConverterBasedTest
{
    private static Log log = LogFactory.getLog(JodContentTransformerOOoTest.class);

    /**
     * This test method tests the built-in thumbnail transformations - all for a Word source document.
     * This will include transformations doc-pdf-png and doc-pdf-swf. ALF-2070
     */
    @Test
    public void thumbnailTransformationsUsingJodConverter()
    {
    	// If OpenOffice is not available then we will ignore this test (by passing it).
    	// This is because not all the build servers have OOo installed.
    	if (!isOpenOfficeAvailable())
    	{
    		System.out.println("Did not run " + this.getClass().getSimpleName() + ".thumbnailTransformationsUsingJodConverter" +
    				" because OOo is not available.");
    		return;
    	}
    	
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    ThumbnailRegistry thumbnailRegistry = thumbnailService.getThumbnailRegistry();
                    for (ThumbnailDefinition thumbDef : thumbnailRegistry.getThumbnailDefinitions())
                    {
                    	if (log.isDebugEnabled())
                    	{
                    		log.debug("Testing thumbnail definition " + thumbDef.getName());
                    	}
                    	
                        NodeRef thumbnail = thumbnailService.createThumbnail(contentNodeRef, ContentModel.PROP_CONTENT,
							        thumbDef.getMimetype(), thumbDef.getTransformationOptions(), thumbDef.getName());
                        
                        assertNotNull("Thumbnail was unexpectedly null.", thumbnail);
                    }
                    
                    return null;
                }
            });
    }
}
