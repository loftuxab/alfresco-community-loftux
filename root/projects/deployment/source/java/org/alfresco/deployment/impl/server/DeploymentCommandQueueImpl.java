package org.alfresco.deployment.impl.server;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DeploymentCommandQueueImpl implements DeploymentCommandQueue
{
	/**
	 * The command queue for this engine
	 */
    private ConcurrentLinkedQueue<Runnable> commandQueue = new ConcurrentLinkedQueue<Runnable>();

	/**
	 *  Queue a command object for execution. 
	 */
	public void queueCommand(Runnable command)
	{
		commandQueue.add(command);
	}

	public Runnable pollCommand() {
		return commandQueue.poll();
	}

}
