package org.alfresco.deployment.impl.server.fsr;
import org.alfresco.deployment.impl.server.Housekeeper;

public class FileSystemReceiverHousekeeper implements Housekeeper 
{
	
    /**
     * The common bits of file system deployment
     */
    private FileSystemReceiverService fileSystemReceiverService;
 
    /**
     * How many commands to process per "tick",  too many and you may block out other processes.
     */
    private int maxCommandsPerTick = 2;
    
    public void init()
    {
    	
    }
	
    public void setFileSystemReceiverService(FileSystemReceiverService fileSystemReceiverService) 
	{
		this.fileSystemReceiverService = fileSystemReceiverService;
	}

	public FileSystemReceiverService getFileSystemReceiverService() 
	{
		return fileSystemReceiverService;
	}
	@Override
	public void poll() {
		
		for(int i = 0; i < getMaxCommandsPerTick(); i++) 
		{	
			Runnable command = fileSystemReceiverService.pollCommand();
		
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

}
