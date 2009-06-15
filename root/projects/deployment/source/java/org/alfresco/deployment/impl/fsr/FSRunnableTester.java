package org.alfresco.deployment.impl.fsr;

import org.alfresco.deployment.FSDeploymentRunnable;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.impl.server.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test class for testing FSRunnables.
 * 
 *  Sets a boolean flag to say that its been called.
 *  
 *  Can throw an exception when told to do so.
 */
public class FSRunnableTester implements FSDeploymentRunnable
{
	private static Log logger = LogFactory.getLog(FSRunnableTester.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5780190885270319744L;
	
	Deployment deployment;
	private boolean runCalled = false;
	private boolean throwException;
	
	
	public void init(Deployment deployment) 
	{
		this.deployment = deployment;	
	}

	public void run() 
	{
		logger.debug("called run");
		setRunCalled(true);
		
		if(isThrowException())
		{
			logger.debug("throwing exception");
			throw new DeploymentException("test exception");
		}
	}
	
	public Deployment getDeployment()
	{
		return deployment;
	}
	
	public boolean isRunCalled()
	{
		return runCalled;
	}

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	public boolean isThrowException() {
		return throwException;
	}

	public void setRunCalled(boolean runCalled) {
		this.runCalled = runCalled;
	}
}
