<?php

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
 * http://www.alfresco.com/legal/licensing"
 */	 
 	// Start the session
 	session_start();
 
 	// Create the log
 	$alfLog = new Logger();
 
 	// Debug parameter
	$alfDebug = true;
	$wgShowExceptionDetails = true;
	
	// Disable the cookie check
	$wgDisableCookieCheck = true;
	 
	// Include the alfresco extensions classes
	require_once("extensions/alfresco-integration/includes/AlfrescoHooks.php");
	require_once("extensions/alfresco-integration/includes/ExternalStoreAlfresco.php");	
	require_once("extensions/alfresco-integration/includes/AuthAlfresco.php");
	
	// Configure in some external stores
	$wgDefaultExternalStore = array("alfresco://localhost:8080/alfresco/api");
	$wgExternalStores = array("alfresco");
	
	// Configure in the Alfresco authentication
	$alfLog->debug("Alfresco - Setting authentication implementation");
	$wgAuth = new AuthAlfresco();	
	
	// Get the current mediaWiki space nodeReference
	$alfWikiSpaceNodeRef = null;
	if (isset($_REQUEST["mediaWikiSpace"]) == true)
	{
	 	$alfWikiSpaceNodeRef = $_REQUEST["mediaWikiSpace"];		
	 	$_SESSION["mediaWikiSpace"] = $alfWikiSpaceNodeRef;
	}
	else if (isset($_SESSION["mediaWikiSpace"]) == true)
	{
	 	$alfWikiSpaceNodeRef = $_SESSION["mediaWikiSpace"];	
	}
	else
	{
	 	// Error!!!!!
	 	throw new Exception("WikiSpace not present");
	}
	
	// Create the repository object
	$alfURL = "http://localhost:8080/alfresco/api";
	$alfRepository = new Repository($alfURL);
	
	// Set the other global values
	$alfTicket = null;
	$alfSession = null;
	$alfMediaWikiNode = null;
	$doLogin = false;
	
	// Check the request to see if we are being provided the ticket
	if (isset($_REQUEST["alfTicket"]) == true && isset($_REQUEST["alfUser"]) == true)
	{
		// Get the passes ticket and user name
		$passedTicket = $_REQUEST["alfTicket"];
		$passedUser = $_REQUEST["alfUser"];
		$doLogin = true;
		
		// Get the ticket from the request
		$alfTicket = $passedTicket;
	}	
	else if (isset($_SESSION["alfTicket"]) == true)
	{
		// Get the ticket out of the session
		$alfTicket = $_SESSION["alfTicket"];
	} 
	
	// Set global values if we have a ticket
	if ($alfTicket != null)
	{	
		// Create an alfresco session that can be used
		$alfSession = $alfRepository->createSession($alfTicket);
	 
		// Create a reference to the media wiki node
		$alfMediaWikiNode = $alfSession->getNodeFromString($alfWikiSpaceNodeRef);
	}
	
	// Set the configuration values
	eval(MediaWikiSpace::getEvaluationString($alfRepository, $alfWikiSpaceNodeRef));

	// Make sure the setup is not included later
	define(MW_NO_SETUP, true);
	require_once("Setup.php");
	
	// Check to see if we should be doing an 'auto' login
	if ($doLogin == true)
	{
		// Authenticate the mediawiki user
		$u = User::newFromName($passedUser);
		if (is_null($u) == false && User::isUsableName($u->getName()) == true)
		{
			// Check to see if the user already exists
			if (0 == $u->getID()) 
			{
				if ($wgAuth->autoCreate() == true && $wgAuth->userExists($u->getName()) == true) 
				{
					if ($wgAuth->authenticate($u->getName(), $passedTicket) == true) 
					{
						// Initialise the user
						$u->addToDatabase();
						$wgAuth->initUser( $u );
						$u->saveSettings();
					} 	
					else
					{
						// Can't authenticate the user based on the credentials provided
						$u = null;
					}
				}
				else
				{
					// Unable to auto create the user in media wiki
					$u = null;
				}
			}
			else
			{
				// Load the users details
				$u->load();	
			}
		}
		
		// Assuming we have found a user check the ticket
		if ($u != null && $u->checkPassword($passedTicket) == true) 
		{
			$wgAuth->updateUser( $u );
			$wgUser = $u;
			$wgUser->setCookies();
		}
	}
?>
