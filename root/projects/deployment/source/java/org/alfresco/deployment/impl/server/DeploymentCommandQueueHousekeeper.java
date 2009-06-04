package org.alfresco.deployment.impl.server;
import org.alfresco.deployment.impl.server.Housekeeper;

public class DeploymentCommandQueueHousekeeper implements Housekeeper 
{
    /**
     * How many commands to process per "tick",  too many and you may block out other processes.
     */
    private int maxCommandsPerTick = 2;
    
    public void init()
    {
    	
    }
    
	public void poll() {
		
		for(int i = 0; i < getMaxCommandsPerTick(); i++) 
		{	
			Runnable command = commandQueue.pollCommand();
		
			if(command != null)
			{
				command.run();
			}
			else
			{
				break;
			}
		}
	}

	public void setMaxCommandsPerTick(int maxCommandsPerTick) {
		this.maxCommandsPerTick = maxCommandsPerTick;
	}

	public int getMaxCommandsPerTick() {
		return maxCommandsPerTick;
	}
	
	private DeploymentCommandQueue commandQueue;
	
	public void setCommandQueue(DeploymentCommandQueue commandQueue) 
	{
		this.commandQueue = commandQueue;
	}

	public DeploymentCommandQueue getCommandQueue() 
	{
		return commandQueue;
	}
}
