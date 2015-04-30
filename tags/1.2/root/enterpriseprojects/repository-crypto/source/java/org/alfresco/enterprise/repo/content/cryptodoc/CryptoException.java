package org.alfresco.enterprise.repo.content.cryptodoc;

import org.alfresco.error.AlfrescoRuntimeException;

public class CryptoException extends AlfrescoRuntimeException 
{
	
	private static final long serialVersionUID = 1L;
	
	public CryptoException(String message)
	{
		super(message);
	}
	
	public CryptoException(String message, Throwable t)
	{
		super(message, t);
	}

}
