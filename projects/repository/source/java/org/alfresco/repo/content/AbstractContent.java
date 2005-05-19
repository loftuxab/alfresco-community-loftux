package org.alfresco.repo.content;

import org.springframework.util.Assert;

/**
 * Provides basic information for <tt>Content</tt>.
 * 
 * @author Derek Hulley
 */
public class AbstractContent implements Content
{
    private String contentUrl;
    private String mimetype;
    private String encoding;

    /**
     * @param contentUrl the content URL
     */
    protected AbstractContent(String contentUrl)
    {
        Assert.hasText(contentUrl, "Invalid content URL");
        this.contentUrl = contentUrl;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("Content")
          .append("[ url=").append(contentUrl)
          .append(", mimetype=").append(mimetype)
          .append(", encoding=").append(encoding)
          .append("]");
        return sb.toString();
    }
    
    public String getContentUrl()
    {
        return contentUrl;
    }
    
    public String getMimetype()
    {
        return mimetype;
    }

    /**
     * @param mimetype the underlying content's mimetype - null if unknown
     */
    public void setMimetype(String mimetype)
    {
        this.mimetype = mimetype;
    }
    
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * @param encoding the underlying content's encoding - null if unknown
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
}
