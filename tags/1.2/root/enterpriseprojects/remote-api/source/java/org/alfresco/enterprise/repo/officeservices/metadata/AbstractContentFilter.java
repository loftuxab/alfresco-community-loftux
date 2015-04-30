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
import java.util.List;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

public abstract class AbstractContentFilter implements ContentFilter
{
    
    protected boolean inputFilter = false;
    
    protected boolean outputFilter = false;
    
    protected List<String> restrictedToMimetypes;
    
    protected List<String> restrictedToContentTypes;
    
    protected boolean includeRestrictedContentTypeChildren;

    protected NodeService nodeService;

    protected IOContentFilterRegistry registry;
    
    private static Logger logger = Logger.getLogger(AbstractContentFilter.class);

    @Override
    public int getMaxInputPreviewByteCount()
    {
        return 0;
    }
    
    @Override
    public boolean appliesToInput(String mimeType, PushbackInputStream pis, NodeRef nodeRef) throws IOException
    {
        if( (mimeType != null) && (restrictedToMimetypes != null) )
        {
            if(!restrictedToMimetypes.contains(mimeType))
            {
                return false;
            }
        }
        if( (nodeRef != null) && (restrictedToContentTypes != null) )
        {
            if(nodeService == null)
            {
                logger.error("If a ContentFilter is configured with a restrictedToContentTypes list, you need to provide the NodeService");
                return false;
            }
            QName nodeType = nodeService.getType(nodeRef);
            if(!restrictedToContentTypes.contains(nodeType.toString()))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean appliesToOutput(NodeRef nodeRef, ContentReader contentReader)
    {
        if(restrictedToMimetypes != null)
        {
            if(contentReader == null)
            {
                return false;
            }
            String contentMimeType = contentReader.getMimetype();
            if(contentMimeType == null)
            {
                return false;
            }
            if(!restrictedToMimetypes.contains(contentMimeType))
            {
                return false;
            }
        }
        if(restrictedToContentTypes != null)
        {
            if(nodeService == null)
            {
                logger.error("If a ContentFilter is configured with a restrictedToContentTypes list, you need to provide the NodeService");
                return false;
            }
            QName nodeType = nodeService.getType(nodeRef);
            if(!restrictedToContentTypes.contains(nodeType.toString()))
            {
                return false;
            }
        }
        return true;
    }

    public final void register()
    {
        if(registry == null)
        {
            logger.error("AbstractContentOutputFilter cannot be registered without an IOContentFilterRegistry.");
        }
        if(outputFilter)
        {
            registry.registerOutputFilter(this);
        }
        if(inputFilter)
        {
            registry.registerInputFilter(this);
        }
    }

    public boolean isInputFilter()
    {
        return inputFilter;
    }

    public void setInputFilter(boolean inputFilter)
    {
        this.inputFilter = inputFilter;
    }

    public boolean isOutputFilter()
    {
        return outputFilter;
    }

    public void setOutputFilter(boolean outputFilter)
    {
        this.outputFilter = outputFilter;
    }

    public List<String> getRestrictedToMimetypes()
    {
        return restrictedToMimetypes;
    }

    public void setRestrictedToMimetypes(List<String> restrictedToMimetypes)
    {
        this.restrictedToMimetypes = restrictedToMimetypes;
    }

    public List<String> getRestrictedToContentTypes()
    {
        return restrictedToContentTypes;
    }

    public void setRestrictedToContentTypes(List<String> restrictedToContentTypes)
    {
        this.restrictedToContentTypes = restrictedToContentTypes;
    }

    public boolean isIncludeRestrictedContentTypeChildren()
    {
        return includeRestrictedContentTypeChildren;
    }

    public void setIncludeRestrictedContentTypeChildren(boolean includeRestrictedContentTypeChildren)
    {
        this.includeRestrictedContentTypeChildren = includeRestrictedContentTypeChildren;
    }

    public NodeService getNodeService()
    {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public IOContentFilterRegistry getRegistry()
    {
        return registry;
    }

    public void setRegistry(IOContentFilterRegistry ioContentFilterRegistry)
    {
        this.registry = ioContentFilterRegistry;
    }

}
