package org.alfresco.enterprise.repo.officeservices;

import junit.framework.TestCase;

public class OfficeServicesPauseSystemTest extends TestCase
{

	public void testHelloWorld() throws Exception
	{
		System.out.println("------------------------------ Enterprise System Build Test - Pause ------------------------------");
		System.out.println("Waiting for 5 minutes....");
		Thread.sleep(5 * 60 * 1000);
		System.out.println("Continuing.");
	}
	
}
