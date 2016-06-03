package org.alfresco.service.cmr.repository.datatype;

import org.alfresco.api.AlfrescoPublicApi;     

/**
 * Base Exception of Type Converter Exceptions.
 * 
 * @author David Caruana
 */
@AlfrescoPublicApi
public class TypeConversionException extends RuntimeException
{
    private static final long serialVersionUID = 3257008761007847733L;

    public TypeConversionException(String msg)
    {
       super(msg);
    }
    
    public TypeConversionException(String msg, Throwable cause)
    {
       super(msg, cause);
    }

}
