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
 
require_once("AlfrescoConfig.php"); 
require_once("Alfresco/Service/Session.php");
require_once("Alfresco/Service/SpacesStore.php");
require_once("Alfresco/Service/Node.php");
require_once("Alfresco/Service/Version.php");

function debug($message)
{
	global $alfDebug;
	
	if ($alfDebug == true)
	{
		$handle = fopen("c:\\work\\mediawiki-debug.txt", "a");
		fwrite($handle, date("G:i:s d/m/Y")." - ".$message."\n");
		fclose($handle);
	}
}

/**
 * Hook function called before content is saved.  At this point we can extract information about the article
 * and store it on the session to be used later.
 */
function alfArticleSave(&$article, &$user, &$text, &$summary, $minor, $watch, $sectionanchor, &$flags)
{
	// Execute a query to get the previous versions URL, we can use this later when we save the content
	// and want to update the version history.
	$url = null;
	$fieldName = "old_text";
	$revision = Revision::newFromId($article->mLatest);
	if (isset($revision) == true)
	{
		$dbw =& $article->getDB();
		$row = $dbw->selectRow( 'text',
					array( 'old_text', 'old_flags' ),
					array( 'old_id' => $revision->getTextId() ),
					"ExternalStoreAlfresco::alfArticleSave");
		$url = $row->$fieldName;
	}
	
	// Sort out the namespace of this article so we can figure out what the title is
	$title = $article->getTitle()->getText();
	$ns = $article->getTitle()->getNamespace();
	if ($ns != NS_MAIN)
	{
		// lookup the display name of the namespace
		$title = Namespace::getCanonicalName($ns)." - ".$title;
	}
	
	// Store the details of the article in the session
	$_SESSION["title"] = $title;	
	$_SESSION["description"] = $summary;
	$_SESSION["lastVersionUrl"] = $url;
	
	// Returning true ensures that the document is saved
	return true;
}

/**
 * External Alfresco content store.
 * 
 * This store retrieves and stores content from MediWiki into a space in a given Alfresco repository.
 */
class ExternalStoreAlfresco 
{
	/**
	 * Fetch the content from the Alfresco repository.
	 * 
	 * @param	$url	the URL to the alfresco content
	 */
	function fetchFromURL($url) 
	{
		$session = $this->getSession();
		$version = $this->urlToVersion($session, $url);		
		return $version->cm_content->content;
	}

	/**
	 * Stores the provided content in the Alfresco repository
	 * 
	 * @param	$store	the external store
	 * @param	$data	the content
	 */
	function &store($store, $data) 
	{
		$session = $this->getSession();
	    $space = $this->getWikiSpace($session);
		
		$url = $_SESSION["lastVersionUrl"];
		$node = null;
		
		$isNormalText = (strpos($url, 'alfresco://') === false);
		
		if ($url != null && $isNormalText == false)
		{
			$node = $this->urlToNode($session, $url);	
		}
		else
		{
			$node = $space->createChild("cm_content", "cm_contains", "cm_".$_SESSION["title"]);
			$node->cm_name = $_SESSION["title"];
		
			$node->addAspect("cm_versionable");
			$node->cm_initialVersion = false;
			$node->cm_autoVersion = false;
		}
		
		// Set the content and save
		$node->setContent("cm_content", "text/plain", "UTF-8", $data);		
		$session->save();
		
		$description = $_SESSION["description"];
		if ($description == null)
		{
			$description = "";
		}
		
		// Create the version
		$version = $node->createVersion($description);
		
		$result = "alfresco://".$node->store->scheme."/".$node->store->address."/".$node->id."/".$version->store->scheme."/".$version->store->address."/".$version->id;		
		return $result;		
	}
	
	/**
	 * Get the session to the Alfresco respoitory
	 */
	function getSession()
	{
		global $alfURL, $alfUser, $alfPassword;		
		
		$repository = new Repository($alfURL);
		$ticket = $repository->authenticate($alfUser, $alfPassword);
		return $repository->createSession($ticket);
	}
	
	/**
	 * Get the store
	 */
	function getStore($session)
	{
		global $alfWikiStore;		
		debug("Getting store ". $alfWikiStore);
		$store = $session->getStoreFromString($alfWikiStore);
		debug("Store found - ".$store->scheme." ".$store->address);
		return $store;
	}
	
	/**
	 * Get the wiki space
	 */
	function getWikiSpace($session)
	{
		global $alfWikiSpace;
		$results = $session->query($this->getStore($session), 'PATH:"'.$alfWikiSpace.'"');
	    return $results[0];
	}
	
	/**
	 * Convert the url to the the node it relates to
	 */
	function urlToNode($session, $url)
	{
		$values = explode("/", substr($url, 11));		
		$store  = $this->getStore($session); 
		return $session->getNode($store, $values[2]);	
	}
	
	/**
	 * Convert the url to the version it relates to
	 */
	function urlToVersion($session, $url)
	{
		$values = explode("/", substr($url, 11));		
		$store  = $session->getStore($values[4], $values[3]);
		return new Version($session, $store, $values[5]);	
	}
}

?>
