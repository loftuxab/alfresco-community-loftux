package org.alfresco.enterprise.repo.officeservices.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;

public interface ContentFilter
{

    int getMaxInputPreviewByteCount();
    
    boolean appliesToInput(String mimeType, PushbackInputStream pis, NodeRef nodeRef) throws IOException;

    boolean appliesToOutput(NodeRef nodeRef, ContentReader contentReader);

    ContentFilterProcessingResult process(NodeRef nodeRef, InputStream in, OutputStream out, boolean stopOnNoOp) throws IOException;

}
