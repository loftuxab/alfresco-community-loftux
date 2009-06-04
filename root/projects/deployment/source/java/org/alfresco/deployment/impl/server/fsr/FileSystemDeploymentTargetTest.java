package org.alfresco.deployment.impl.server.fsr;

/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.deployment.FSDeploymentRunnable;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.impl.server.Deployment;

import junit.framework.TestCase;

public class FileSystemDeploymentTargetTest extends TestCase 
{
	/**
	 * @param name
	 */
	public FileSystemDeploymentTargetTest(String name) 
	{
		super(name);
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testAutoFix()
	{
	
	}
	
	public void testValidateMetaData()
	{
	
	}
	
//	/**
//	 * Test postCommitCallback
//	 * the exception should be swallowed and not thrown.
//	 */
//	
//	public void testPostCommit()
//	{
//		List<FSDeploymentRunnable> runnables = new ArrayList<FSDeploymentRunnable>(); 
//		
//		FSRunnableTester tester = new FSRunnableTester();
//		tester.throwException = true;
//		
//		String name="testTarget";
//		String root="hellmouth";
//		String metadata="metadata";
//		String user="Master";	
//		String password="vampire";
//		
//		Target t = new Target(name, root, metadata, null, runnables, user, password);
//		Deployment deployment = null;
//		try {
//			deployment = new Deployment(t, ".");
//		} catch (IOException e) {
//			e.printStackTrace();
//			fail("unable to create deployment");
//		}
//		
//		// should do nothing
//		t.runPostCommit(deployment);
//		
//		runnables.add(tester);
//		t.runPostCommit(deployment);
//		
//		assertNotNull("init method not called", tester.getDeployment());
//		assertTrue("run called", tester.isRunCalled());
//		
//	}
//	
//	
//	/**
//	 * Test the prepare callback 
//	 */
//	public void testPrepare()
//	{
//		List<FSDeploymentRunnable> runnables = new ArrayList<FSDeploymentRunnable>(); 
//		
//		FSRunnableTester tester = new FSRunnableTester();
//		
//		
//		String name="testTarget";
//		String root="hellmouth";
//		String metadata="metadata";
//		String user="Master";	
//		String password="vampire";
//		
//		Target t = new Target(name, root, metadata, runnables, null, user, password);
//		Deployment deployment = null;
//		try {
//			deployment = new Deployment(t, ".");
//		} catch (IOException e) {
//			e.printStackTrace();
//			fail("unable to create deployment");
//		}
//		
//		// should do nothing
//		t.runPrepare(deployment);
//		
//		// add the tester
//		runnables.add(tester);
//		t.runPrepare(deployment);
//		
//		assertNotNull("init method not called", tester.getDeployment());
//		assertTrue("run called", tester.isRunCalled());
//		
//		// set the tester to throw and exception - this should not be swallowed
//		tester.throwException = true;
//		
//		try{
//			t.runPrepare(deployment);
//			fail("exception not thrown");
//		} catch (DeploymentException de) {
//		
//		}
//	}
	
	/**
	 * Test class for 
	 */
	private class FSRunnableTester implements FSDeploymentRunnable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5780190885270319744L;
		
		Deployment deployment;
		boolean runCalled = false;
		boolean throwException;
		
		
		public void init(Deployment deployment) 
		{
			this.deployment = deployment;	
		}

		public void run() 
		{
			System.out.println("called run");
			runCalled = true;
			
			if(throwException)
			{
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
	}
}
