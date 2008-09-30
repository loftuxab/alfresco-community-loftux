package org.alfresco.deployment.impl.server;

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

import junit.framework.TestCase;

public class TargetTest extends TestCase 
{
	/**
	 * @param name
	 */
	public TargetTest(String name) 
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
	
	public void testGetFileForPath()
	{

	}
	
	public void testGetListing()
	{

	}
	
	public void testConstructor()
	{
		String name="testTarget";
		String root="hellmouth";
		String metadata="metadata";
		String user="Master";	
		String password="vampire";
		
		Target t = new Target(name, root, metadata, null, user, password);
		
		assertTrue("name not equal", t.getName().equals(name));
		assertTrue("root not equal", t.getRootDirectory().equals(root));
		assertTrue("meta not equal", t.getMetaDataDirectory().equals(metadata));
		assertTrue("user not equal", t.getUser().equals(user));
		assertTrue("password not equal", t.getPassword().equals(password));
			
	}
	
	public void testAutoFix()
	{
	
	}
	
	public void testValidateMetaData()
	{
	
	}
	
	/**
	 * Test 
	 */
	
	public void testPostCommit()
	{
		List<FSDeploymentRunnable> runnables = new ArrayList<FSDeploymentRunnable>(); 
		
		FSRunnableTester tester = new FSRunnableTester();
		
		
		String name="testTarget";
		String root="hellmouth";
		String metadata="metadata";
		String user="Master";	
		String password="vampire";
		
		Target t = new Target(name, root, metadata, runnables, user, password);
		Deployment deployment = null;
		try {
			deployment = new Deployment(t, ".");
		} catch (IOException e) {
			e.printStackTrace();
			fail("unable to create deployment");
		}
		
		// should do nothing
		t.runPostCommit(deployment);
		
		runnables.add(tester);
		t.runPostCommit(deployment);
		
		assertNotNull("init method not called", tester.getDeployment());
		assertTrue("run called", tester.isRunCalled());
		

		
	}
	
	private class FSRunnableTester implements FSDeploymentRunnable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5780190885270319744L;
		
		Deployment deployment;
		boolean runCalled = false;
		
		public void init(Deployment deployment) 
		{
			this.deployment = deployment;	
		}

		public void run() 
		{
			System.out.println("called run");
			runCalled = true;
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
