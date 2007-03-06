<?php

require_once("Alfresco/Service/Session.php");
require_once("Alfresco/Service/SpacesStore.php");
require_once("Alfresco/Service/Node.php");
require_once("Alfresco/Service/Version.php");

function alfArticleSave(&$article, &$user, &$text, &$summary, $minor, $watch, $sectionanchor, &$flags)
{
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
	
	$_SESSION["title"] = $article->getTitle()->getText();	
	$_SESSION["description"] = $summary;
	$_SESSION["lastVersionUrl"] = $url;
	
	return true;
}

class ExternalStoreAlfresco 
{
	function fetchFromURL($url) 
	{
		$session = Session::create("admin", "admin");
		$version = $this->urlToVersion($session, $url);		
		return $version->cm_content->content;
	}

	function &store($store, $data) 
	{
		$session = Session::create("admin", "admin");
		$store = new SpacesStore($session);
		
		$results = $session->query($store, 'PATH:"app:company_home/cm:wiki"');
	    $space = $results[0];
		
		$url = $_SESSION["lastVersionUrl"];
		$node = null;
		if ($url != null)
		{
			$node = $this->urlToNode($session, $url);	
		}
		else
		{
			$node = $space->createChild("cm_content", "cm_contains", "cm_".$_SESSION["title"]);
			$node->cm_name = $_SESSION["title"];
		
			//$node->addAspect("cm_titled");
			//$node->cm_title = $_SESSION["title"];
			//$node->cm_description = $_SESSION["lastVersionUrl"];
		
			$node->addAspect("cm_versionable");
			$node->cm_initialVersion = false;
			$node->cm_autoVersion = false;
		}
		
		$contentData = new ContentData("text/plain", "UTF-8");
		$contentData->content = $data;
		$node->cm_content = $contentData;
		
		$session->save();
		
		$description = $_SESSION["description"];
		if ($description == null)
		{
			$description = "";
		}
		
		// Create the version
		$version = $node->createVersion($description);
		
		$result = "alfresco://".$store->scheme."/".$store->address."/".$node->id."/".$version->store->scheme."/".$version->store->address."/".$version->id;		
		return $result;		
	}
	
	function urlToNode($session, $url)
	{
		$values = explode("/", substr($url, 11));		
		$store  = new Store($session, $values[1], $values[0]);
		return Node::create($session, $store, $values[2]);	
	}
	
	function urlToVersion($session, $url)
	{
		$values = explode("/", substr($url, 11));		
		$store  = new Store($session, $values[4], $values[3]);
		return new Version($session, $store, $values[5]);	
	}
}

?>
