/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.metadata;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.LinkedList;
import java.util.List;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;

public class StandardIOContentFilterRegistry implements IOContentFilterRegistry
{

    protected List<ContentFilter> inputFilters = new LinkedList<ContentFilter>();

    protected List<ContentFilter> outputFilters = new LinkedList<ContentFilter>();

    @Override
    public ContentFilter getInputFilter(String mimeType, PushbackInputStream pis, NodeRef nodeRef) throws IOException
    {
        for(ContentFilter filter : inputFilters)
        {
            if(filter.appliesToInput(mimeType, pis, nodeRef))
            {
                return filter;
            }
        }
        return null;
    }

    @Override
    public ContentFilter getOutputFilter(NodeRef nodeRef, ContentReader contentReader)
    {
        for(ContentFilter filter : outputFilters)
        {
            if(filter.appliesToOutput(nodeRef, contentReader))
            {
                return filter;
            }
        }
        return null;
    }

    @Override
    public void registerInputFilter(ContentFilter contentFilter)
    {
        if(contentFilter != null)
        {
            inputFilters.add(contentFilter);
        }
    }

    @Override
    public void registerOutputFilter(ContentFilter contentFilter)
    {
        if(contentFilter != null)
        {
            outputFilters.add(contentFilter);
        }
    }

    @Override
    public int getMaxInputPreviewByteCount()
    {
        int result = 0;
        for(ContentFilter filter : inputFilters)
        {
            result = Math.max(result,  filter.getMaxInputPreviewByteCount());
        }
        return result;
    }

}
