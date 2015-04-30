package org.alfresco.enterprise.repo.officeservices.metadata;

public class ContentFilterProcessingResult
{

    public static final ContentFilterProcessingResult MODIFIED = new ContentFilterProcessingResult(true, null);

    public static final ContentFilterProcessingResult UNMODIFIED = new ContentFilterProcessingResult(false, null);
    
    private boolean modified;
    
    private ContentPostProcessor postProcessor;
    
    public ContentFilterProcessingResult(boolean modified, ContentPostProcessor postProcessor)
    {
        this.modified = modified;
        this.postProcessor = postProcessor;
    }

    public boolean isModified()
    {
        return modified;
    }

    public ContentPostProcessor getPostProcessor()
    {
        return postProcessor;
    }

}
