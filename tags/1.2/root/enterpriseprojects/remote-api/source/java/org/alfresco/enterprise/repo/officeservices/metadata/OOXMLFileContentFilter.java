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
import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.PackageHelper;

public class OOXMLFileContentFilter extends AbstractContentFilter
{

    List<OOXMLFileProcessor> processors = null;
    
    public List<OOXMLFileProcessor> getProcessors()
    {
        return processors;
    }

    public void setProcessors(List<OOXMLFileProcessor> processors)
    {
        this.processors = processors;
    }

    @Override
    public int getMaxInputPreviewByteCount()
    {
        return 4;
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
        if(!POIXMLDocument.hasOOXMLHeader(pis))
        {
            return false;
        }
        for(OOXMLFileProcessor processor : processors)
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
        for(OOXMLFileProcessor processor : processors)
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
        
        // read in the OOXML package
        OPCPackage pkg = PackageHelper.open(in);
        
        // check if one of the modifiers is interested in modifying this file
        boolean haveInterestedProcessors = false;
        for(OOXMLFileProcessor processor : processors)
        {
            haveInterestedProcessors |= processor.appliesTo(pkg, nodeRef);
        }
        
        // no-one interested: write out the unmodified OLE file
        if(!haveInterestedProcessors)
        {
            if(stopOnNoOp || (out == null))
            {
                return ContentFilterProcessingResult.UNMODIFIED;
            }
            pkg.save(out);
            return ContentFilterProcessingResult.MODIFIED;
        }
        
        // run the set of modifiers on the OLE file system
        boolean hasModifications = false;
        ContentPostProcessor postProcessor = null;
        for(OOXMLFileProcessor processor : processors)
        {
            if(processor.appliesTo(pkg, nodeRef))
            {
                ContentFilterProcessingResult processorResult = processor.execute(pkg, nodeRef);
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
        if((!hasModifications && stopOnNoOp) || (out == null))
        {
            return new ContentFilterProcessingResult(false, postProcessor);
        }
        
        // write the modified OLE file system to the output stream
        pkg.save(out);
        return new ContentFilterProcessingResult(true, postProcessor);
    }

}
