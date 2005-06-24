package org.alfresco.filesys.netbios;

/**
 * Name Template Exception Class
 * <p>
 * Thrown when a NetBIOS name template contains invalid characters or is too long.
 */
public class NameTemplateException extends Exception
{
    private static final long serialVersionUID = 3256439188231762230L;

    /**
     * Default constructor.
     */
    public NameTemplateException()
    {
        super();
    }

    /**
     * Class constructor
     * 
     * @param s java.lang.String
     */
    public NameTemplateException(String s)
    {
        super(s);
    }
}
