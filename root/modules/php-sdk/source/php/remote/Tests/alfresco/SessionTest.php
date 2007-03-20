<?php
/*
 * Copyright (C) 2005 Alfresco, Inc.
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
 * http://www.alfresco.com/legal/licensing"
 */
 
require_once('BaseTest.php');
require_once('../Alfresco/Service/Session.php');

class SessionTest extends BaseTest
{	
	public function testInvalidCredentials()
	{
		try
		{
			$invalidSession = Session::create(BaseTest::USERNAME, "badPassword", BaseTest::URL);
			$this->fail("An exception should have been raised since we tried to login with invalid credentials.");
		}
		catch (Exception $exception)
		{
			// Do nothing since we where expecting the exception
		}
	}
	
	public function testSessionDetails()
	{
		$session = $this->getSession();
		
		$this->assertEquals(BaseTest::USERNAME, $session->userName);
		$this->assertEquals(BaseTest::URL, $session->repositoryURL);
		$this->assertNotNull($session->ticket);
		
		$sessionDetails = $session->sessionDetails;
		$this->assertNotNull($sessionDetails);
		$this->assertEquals(BaseTest::USERNAME, $sessionDetails->userName);
		$this->assertEquals(BaseTest::URL, $sessionDetails->repositoryURL);
		$this->assertEquals($session->ticket, $sessionDetails->ticket);
		
		$newSession = Session::createFromSessionDetails($sessionDetails);
		$this->assertNotNull($newSession);
		$this->assertEquals(BaseTest::USERNAME, $newSession->userName);
		$this->assertEquals(BaseTest::URL, $newSession->repositoryURL);
		$this->assertEquals($session->ticket, $newSession->ticket);
		$this->assertNotNull($newSession->sessionDetails);		
	}
	
	public function testStores()
	{
		$stores = $this->getSession()->stores;
		$this->assertNotNull($stores);
		$this->assertTrue(count($stores) > 1);
		
		$foundSpacesStore = false;
		foreach ($stores as $store)
		{
			if ($store->address == "SpacesStore")
			{
				$foundSpacesStore = true;
			}
		}
		
		if ($foundSpacesStore == false)
		{
			$this->fail("The spaces store was not found when querying the stores of a perticular session.");
		}
	}
	
	public function testQuery()
	{
		$nodes = $this->getSession()->query($this->getStore(), 'TEXT:"Alfresco"');
		$this->assertNotNull($nodes);
		// TODO we don't know how many results to expect!
		// TODO maybe some additional tests to ensure the nodes are correctly formed
		
		$nodes2 = $this->getSession()->query($this->getStore(), 'PATH:"app:company_home"');
		$this->assertNotNull($nodes2);
		$this->assertTrue(1 == count($nodes2));
		
		$nodes3 = $this->getSession()->query($this->getStore(), 'PATH:"app:junk"');
		$this->assertNotNull($nodes3);
		$this->assertTrue(0 == count($nodes3));
	}
	
	public function testClear()
	{
		// We've just a load of queries so the node cache should have some stuff in it
		$this->getSession()->clear();
		
		// Do another query
		$nodes = $this->getSession()->query($this->getStore(), 'PATH:"app:company_home"');
		
		// Get a propery value to ensure the node can be populated
		$this->assertEquals("Company Home", $nodes[0]->cm_name);
	}

}

?>

