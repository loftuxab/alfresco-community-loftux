package org.alfresco.filesys.smb.dcerpc;

/**
 * DCE Buffer Exception Class
 */
public class DCEBufferException extends Exception
{
    private static final long serialVersionUID = 3833460725724494132L;

    /**
     * Class constructor
     */
    public DCEBufferException()
    {
        super();
    }

    /**
     * Class constructor
     * 
     * @param str String
     */
    public DCEBufferException(String str)
    {
        super(str);
    }
}
