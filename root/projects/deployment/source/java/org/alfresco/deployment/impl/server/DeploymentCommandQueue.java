package org.alfresco.deployment.impl.server;

public interface DeploymentCommandQueue 
{
	/**
	 *  Queue a command object for execution. 
	 */
	public void queueCommand(Runnable command);
	
	/**
	 * Get a command from the queue
	 * @return the command or null 
	 */
	public Runnable pollCommand();

}
