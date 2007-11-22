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
 
	/**
	 * Basic Tutorial Two - Company Home
	 * 
	 * In the tutorial we will get a reference to the company home node.
	 * 
	 * A discusion of this tutorial can be found at http://wiki.alfresco.com/wiki/PHP_Tutorial_Two
	 * 
	 * Note: any changes to this file should be uploaded to the wiki
	 */ 
  
	// Include the required Alfresco PHP API objects  
	if (isset($_SERVER["ALF_AVAILABLE"]) == false)
    {
	   require_once "Alfresco/Service/Repository.php";
	   require_once "Alfresco/Service/Session.php";
	   require_once "Alfresco/Service/SpacesStore.php";
    }

	// Specify the connection details
	$repositoryUrl = "http://localhost:8080/alfresco/api";
	$userName = "admin";
	$password = "admin"; 
	
	// Authenticate the user and create a session
	$repository = new Repository($repositoryUrl);
	$ticket = $repository->authenticate($userName, $password);
	$session = $repository->createSession($ticket);
	
	// Create a reference to the 'SpacesStore'
	$spacesStore = new SpacesStore($session);
	
	// Get the company home node
	$companyHome = $spacesStore->companyHome;	 
?>

<html>

<head>
	<title>Basic Tutorial Two - Company Home</title>
</head>

<body>
    <big>Basic Tutorial Two - Company Home</big>
	<p>Found the "<?php echo $companyHome->cm_name ?>" node with node reference <?php echo $companyHome->__toString() ?></p>
</body>

</html>
