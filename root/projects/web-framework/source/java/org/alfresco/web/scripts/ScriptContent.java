package org.alfresco.web.scripts;

import java.io.InputStream;
import java.io.Reader;

public interface ScriptContent
{
    /**
     * Returns an input stream to the contents of the script
     * 
     * @return  the input stream
     */
    InputStream getInputStream();
    
    /**
     * Returns a reader to the contents of the script
     * 
     * @return  the reader
     */
    Reader getReader();

}
