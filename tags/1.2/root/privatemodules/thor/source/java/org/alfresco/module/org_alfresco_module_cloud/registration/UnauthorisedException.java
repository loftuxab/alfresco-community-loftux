package org.alfresco.module.org_alfresco_module_cloud.registration;

import org.alfresco.error.AlfrescoRuntimeException;

public class UnauthorisedException extends AlfrescoRuntimeException 
{
	private static final long serialVersionUID = 890460159540030233L;

	public UnauthorisedException(String msgId)
    {
        super(msgId);
    }

    public UnauthorisedException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
    }

    public UnauthorisedException(String msgId, Throwable cause)
    {
        super(msgId, cause);
    }

    public UnauthorisedException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
    }

}
