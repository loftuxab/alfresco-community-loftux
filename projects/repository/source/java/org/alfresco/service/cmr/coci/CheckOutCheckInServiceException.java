/**
 * Created on May 16, 2005
 */
package org.alfresco.service.cmr.coci;

/**
 * Version opertaions service exception class
 * 
 * @author Roy Wetherall
 */
public class CheckOutCheckInServiceException extends RuntimeException 
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3258410621186618417L;

	/**
	 * Constructor
	 */
	public CheckOutCheckInServiceException() 
	{
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message  the error message
	 */
	public CheckOutCheckInServiceException(String message) 
	{
		super(message);
	}
}
