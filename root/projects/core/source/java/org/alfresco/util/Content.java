package org.alfresco.util;

import java.io.IOException;
import java.io.InputStream;

public interface Content
{
    public String getContent() throws IOException;
    
    public String getMimetype();
    
    public String getEncoding();
    
    public long getSize();

    public InputStream getInputStream();
}
