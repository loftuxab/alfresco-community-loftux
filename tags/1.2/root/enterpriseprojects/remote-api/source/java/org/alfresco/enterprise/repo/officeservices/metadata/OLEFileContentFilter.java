/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.List;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.io.IOUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class OLEFileContentFilter extends AbstractContentFilter
{

    List<OLEFileProcessor> processors = null;
    
    public List<OLEFileProcessor> getProcessors()
    {
        return processors;
    }

    public void setProcessors(List<OLEFileProcessor> processors)
    {
        this.processors = processors;
    }

    @Override
    public int getMaxInputPreviewByteCount()
    {
        return 8;
    }

    @Override
    public boolean appliesToInput(String mimeType, PushbackInputStream pis, NodeRef nodeRef) throws IOException
    {
        if( (processors == null) || (processors.size() == 0) )
        {
            return false;
        }
        if(!super.appliesToInput(mimeType, pis, nodeRef))
        {
            return false;
        }
        if(!POIFSFileSystem.hasPOIFSHeader(pis))
        {
            return false;
        }
        for(OLEFileProcessor processor : processors)
        {
            if(processor.appliesTo(nodeRef))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean appliesToOutput(NodeRef nodeRef, ContentReader contentReader)
    {
        if(!super.appliesToOutput(nodeRef, contentReader))
        {
            return false;
        }
        if( (processors == null) || (processors.size() == 0) )
        {
            return false;
        }
        for(OLEFileProcessor processor : processors)
        {
            if(processor.appliesTo(nodeRef))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public ContentFilterProcessingResult process(NodeRef nodeRef, InputStream in, OutputStream out, boolean stopOnNoOp) throws IOException
    {
        // if there are no modifiers registered, we just copy the stream
        if( (processors == null) || (processors.size() == 0) )
        {
            if(stopOnNoOp || (out == null))
            {
                return ContentFilterProcessingResult.UNMODIFIED;
            }
            IOUtils.copy(in, out);
            return ContentFilterProcessingResult.MODIFIED;
        }
        
        // read in the ole file system
        POIFSFileSystem oleFS = new POIFSFileSystem(in);
        
        // check if one of the modifiers is interested in modifying this file
        boolean haveInterestedModifiers = false;
        for(OLEFileProcessor processor : processors)
        {
            haveInterestedModifiers |= processor.appliesTo(oleFS, nodeRef);
        }
        
        // no-one interested: write out the unmodified OLE file
        if(!haveInterestedModifiers)
        {
            if(stopOnNoOp || (out == null))
            {
                return ContentFilterProcessingResult.UNMODIFIED;
            }
            oleFS.writeFilesystem(out);
            return ContentFilterProcessingResult.MODIFIED;
        }
        
        // run the set of modifiers on the OLE file system
        boolean hasModifications = false;
        ContentPostProcessor postProcessor = null;
        for(OLEFileProcessor processor : processors)
        {
            if(processor.appliesTo(oleFS, nodeRef))
            {
                ContentFilterProcessingResult processorResult = processor.execute(oleFS, nodeRef);
                hasModifications |= processorResult.isModified();
                if(processorResult.getPostProcessor() != null)
                {
                    if(postProcessor == null)
                    {
                        postProcessor = processorResult.getPostProcessor();
                    }
                    else
                    {
                        if(postProcessor instanceof ChainingContentPostProcessor)
                        {
                            ((ChainingContentPostProcessor)postProcessor).add(processorResult.getPostProcessor());
                        }
                        else
                        {
                            ChainingContentPostProcessor chain = new ChainingContentPostProcessor();
                            chain.add(postProcessor);
                            chain.add(processorResult.getPostProcessor());
                            postProcessor = chain;
                        }
                    }
                }
            }
        }
        
        // drop out if OLE file system has not been modified and we are requested to stop on no-op
        if( (!hasModifications && stopOnNoOp) || (out == null))
        {
            return new ContentFilterProcessingResult(false, postProcessor);
        }
        
        // write the modified OLE file system to the output stream
        oleFS.writeFilesystem(out);
        return new ContentFilterProcessingResult(true, postProcessor);
    }

}
