package org.alfresco.deployment.impl.fsr;

/**
 * Used by the commit method
 */

/* package scope */abstract class CommitThread extends Thread 
{	
	private Exception exception;
	
	private boolean stop = false;
	
	public boolean isFinish()
	{
		return stop;
	}
	
	/** 
	 * Called to stop this thread
	 */
	public void setFinish() 
	{
		stop = true;
	}
	
	public Exception getException()
	{
		return exception;
	}
	
	public void setException(Exception e)
	{
		this.exception = e;
	}
}


