package org.alfresco.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.springframework.util.FileCopyUtils;


/**
 * Input Stream based Content
 */
public class InputStreamContent implements Content, Serializable
{
    private static final long serialVersionUID = -7729633986840536282L;

    /**
     * Constructor
     * 
     * @param stream    content input stream
     * @param mimetype  content mimetype
     */
    public InputStreamContent(InputStream stream, String mimetype, String encoding)
    {
        this.stream = stream;
        this.mimetype = mimetype;
        this.encoding = encoding;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.jscript.ScriptNode.ScriptContent#getContent()
     */
    public String getContent()
        throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileCopyUtils.copy(stream, os);  // both streams are closed
        byte[] bytes = os.toByteArray();
        // get the encoding for the string
        String encoding = getEncoding();
        // create the string from the byte[] using encoding if necessary
        String content = (encoding == null) ? new String(bytes) : new String(bytes, encoding);
        // done
        return content;
    }
    
    public InputStream getInputStream()
    {
        return stream;
    }
    
    public long getSize()
    {
        return -1;
    }
    
    public String getMimetype()
    {
        return mimetype;
    }
    
    public String getEncoding()
    {
        return encoding;
    }

    
    private InputStream stream;
    
    private String mimetype;
    
    private String encoding;
}