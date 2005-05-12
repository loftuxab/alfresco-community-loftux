/**
 * Created on May 10, 2005
 */
package org.alfresco.repo.node.operations;

/**
 * Nodes operations service exception class.
 * 
 * @author Roy Wetherall
 */
public class NodeOperationsServiceException extends RuntimeException 
{
	/**
	 * Serial version UID 
	 */
	private static final long serialVersionUID = 3256727273112614964L;

	/**
	 * Constructor
	 */
	public NodeOperationsServiceException() 
	{
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message  the error message
	 */
	public NodeOperationsServiceException(String message) 
	{
		super(message);
	}
}
