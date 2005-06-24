package org.alfresco.filesys.smb.dcerpc;

/**
 * DCE/RPC Exception Class
 */
public class DCEException extends Exception
{
    private static final long serialVersionUID = 3258688788954625072L;

    /**
     * Class constructor
     * 
     * @param str String
     */
    public DCEException(String str)
    {
        super(str);
    }
}
