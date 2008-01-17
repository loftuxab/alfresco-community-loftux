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
 
 // Register the various event hooks
$wgHooks['ArticleSave'][] = 'alfArticleSave';
$wgHooks['TitleMoveComplete'][] = 'alfTitleMoveComplete';

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
	
	// Store the details of the article in the session
	$_SESSION["title"] = ExternalStoreAlfresco::getTitle($article->getTitle());	
	$_SESSION["description"] = $summary;
	$_SESSION["lastVersionUrl"] = $url;
	
	// Returning true ensures that the document is saved
	return true;
}

function alfTitleMoveComplete(&$title, &$newtitle, &$user, $pageid, $redirid)
{
	//$logger = new Logger("integration.mediawiki.ExternalStoreAlfresco");
	
	//if ($logger->isDebugEnabled() == true)
	//{
	//	$logger->debug("Handling title move event");
	//	$logger->debug(	  "title=".ExternalStoreAlfresco::getTitle($title).
	//				    "; newTitle=".ExternalStoreAlfresco::getTitle($newtitle).
	//					"; user=".$user->getName().
	//					"; pageid=".$pageid.		// is page_id on page table
	//					"; redirid=".$redirid);
	//}
	
	// Do summert :D
}
 
?>
