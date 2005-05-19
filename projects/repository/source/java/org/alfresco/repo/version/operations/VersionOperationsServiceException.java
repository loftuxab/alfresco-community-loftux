/**
 * Created on May 16, 2005
 */
package org.alfresco.repo.version.operations;

/**
 * Version opertaions service exception class
 * 
 * @author Roy Wetherall
 */
public class VersionOperationsServiceException extends RuntimeException 
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3258410621186618417L;

	/**
	 * Constructor
	 */
	public VersionOperationsServiceException() 
	{
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message  the error message
	 */
	public VersionOperationsServiceException(String message) 
	{
		super(message);
	}
}
