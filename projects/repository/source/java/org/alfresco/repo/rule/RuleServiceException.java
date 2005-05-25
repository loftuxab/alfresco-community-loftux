package org.alfresco.repo.rule;

/**
 * Rule Service Exception Class
 * 
 * @author Roy Wetherall
 */
public class RuleServiceException extends RuntimeException 
{
	/**
	 * Serial version UID 
	 */
	private static final long serialVersionUID = 3257571685241467958L;

	/**
	 * Constructor
	 */
	public RuleServiceException() 
	{
		super();
	}

	/**
	 * Construtor
	 * 
	 * @param message 	the message string
	 */
	public RuleServiceException(String message) 
	{
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message	the message string
	 * @param source	the source exception
	 */
	public RuleServiceException(String message, Throwable source) 
	{
		super(message, source);
	}

	/**
	 * Constructor
	 * 
	 * @param source	the source exception
	 */
	public RuleServiceException(Throwable source) 
	{
		super(source);
	}
}
