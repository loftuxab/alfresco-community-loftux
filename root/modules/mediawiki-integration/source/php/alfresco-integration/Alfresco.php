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
 
 	// Debug parameter
	$alfDebug = true;
	 
	// Configuration parameters
	$alfURL = "http://localhost:8080/alfresco/api";
	$alfUser = "admin";
	$alfPassword = "admin";
	 
	// Create the repository object
	$alfRepository = new Repository($alfURL);
	 
	// Get the ticket for the current session
	$alfTicket = null;
	if (isset($_SESSION["alfTicket"]) == true)
	{
		$alfTicket = $_SESSION["alfTicket"];
	}
	else
	{
		// Authenticate
		$alfTicket = $alfRepository->authenticate($alfUser, $alfPassword);	
		$_SESSION["alfTicket"] = $alfTicket;
	}
	 
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
	 
	// Create an alfresco session that can be used
	$alfSession = $alfRepository->createSession($alfTicket);
	 
	// Create a reference to the media wiki node
	$alfMediaWikiNode = $alfSession->getNodeFromString($alfWikiSpaceNodeRef);	
	 
	 // Include the alfresco extensions classes
	require_once("extensions/alfresco-integration/includes/AlfrescoHooks.php");
	require_once("extensions/alfresco-integration/includes/ExternalStoreAlfresco.php");
	
	// Configure in some external stores
	$wgDefaultExternalStore = array("alfresco://localhost:8080/alfresco/api");
	$wgExternalStores = array("alfresco");
 
?>
