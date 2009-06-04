package org.alfresco.deployment.impl.fsr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ValidateCommand implements Runnable
{
	public FileSystemDeploymentTarget target;
	
    /**
     * The logger for this target
     */
    private static Log logger = LogFactory.getLog(ValidateCommand.class);
	
	public ValidateCommand(FileSystemDeploymentTarget target)
	{
		this.target = target;
	}

	public void run() 
	{
      	try 
    	{
    		synchronized (target)
    		{
    			// Now we hold the lock for the target - which will prevent new deployments beginning.
    			if(target.isBusy())
    			{
    				// do no validation there is a deployment in progress
    				logger.warn("target is busy. Not validating target:" + target.getName());
    			}
    			else
    			{
    				logger.info("Validation starting for target:" + target.getName() );
    				target.validate();
    				logger.info("Validation finished");
    			}
    		}
    	}
    	catch (Exception e)
    	{
    		logger.error("Unable to validate", e);
    	}
	}
}
