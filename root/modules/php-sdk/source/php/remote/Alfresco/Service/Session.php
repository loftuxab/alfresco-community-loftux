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
 
require_once 'Store.php';
require_once 'SessionDetails.php';
require_once 'Node.php';
require_once 'WebService/WebServiceFactory.php';

class Session extends BaseObject
{
	public $authenticationService;
	public $repositoryService;
	public $contentService;

	private $_sessionDetails;
	private $_stores;
	
	private $nodeCache;
	private $idCount = 0;

    /**
     * Constructor
     * 
     * @param userName the user name
     * @param ticket the currenlty authenticated users ticket
     */
	private function __construct($userName, $ticket, $repositoryURL, $authenticationService=null)
	{
		$this->_sessionDetails = new SessionDetails($ticket, $userName, $repositoryURL);
		$this->authenticationService = $authenticationService;
		$this->nodeCache = array();

		// Get the other service's
		if ($authenticationService == null)
		{
			$this->authenticationService = WebServiceFactory::getAuthenticationService($this->_sessionDetails->repositoryURL);
		}
		$this->repositoryService = WebServiceFactory::getRepositoryService($this->_sessionDetails->repositoryURL, $this->_sessionDetails->ticket);
		$this->contentService = WebServiceFactory::getContentService($this->_sessionDetails->repositoryURL, $this->_sessionDetails->ticket);
	}

	public static function create($userName, $password, $repositoryURL = "http://localhost:8080/alfresco/api")
	{
		// TODO need to handle exception here ...

		$authenticationService = WebServiceFactory :: getAuthenticationService($repositoryURL);
		$result = $authenticationService->startSession(array (
			"username" => $userName,
			"password" => $password
		));
		return new Session($result->startSessionReturn->username, $result->startSessionReturn->ticket, $repositoryURL, $authenticationService);
	}
	
	public static function createFromSessionDetails($sessionDetails)
	{
		return new Session($sessionDetails->userName, $sessionDetails->ticket, $sessionDetails->repositoryURL);	
	}	
	
	public function createStore($address, $scheme="workspace")
	{
		// Create the store
		$result = $this->repositoryService->createStore(array(
													"scheme" => $scheme,
													"address" => $address));
		$store = new Store($this, $result->createStoreReturn->address, $result->createStoreReturn->scheme);											
		
		// Add to the cached list if its been populated
		if (isset($this->_stores) == true)
		{
			$this->_stores[] = $store;
		}	
		
		// Return the newly created store
		return $store;
	}

	public function close()
	{
		$this->authenticationService->endSession(array (
			"ticket" => $this->_sessionDetails->ticket
		));
	}

	public function save($debug=false)
	{
		// Build the update statements from the node cache
		$statements = array();
		foreach ($this->nodeCache as $node)
		{
			$node->onBeforeSave($statements);
		}
		
		if ($debug == true)
		{
			var_dump($statements);
			echo ("<br><br>");
		}
		
		if (count($statements) > 0)
		{
			// Make the web service call
			$result = $this->repositoryService->update(array("statements" => $statements));
			//var_dump($result);
					
			// Update the state of the updated nodes
			foreach ($this->nodeCache as $node)
			{
				$node->onAfterSave($this->getIdMap($result));
			}
		}
	}
	
	/**
	 * Clears the current session by emptying the node cache.
	 * 
	 * WARNING:  all unsaved changes will be lost when clearing the session.
	 */
	public function clear()
	{
		// Clear the node cache
		$this->nodeCache = array();	
	}
	
	private function getIdMap($result)
	{
		$return = array();
		$statements = $result->updateReturn;
		if (is_array($statements) == true)
		{
			foreach ($statements as $statement)
			{
				if ($statement->statement == "create")
				{
					$id = $statement->sourceId;
					$uuid = $statement->destination->uuid;
					$return[$id] = $uuid;
				}
			}	
		}	
		else
		{
			if ($statements->statement == "create")
				{
					$id = $statements->sourceId;
					$uuid = $statements->destination->uuid;
					$return[$id] = $uuid;
				}	
		}	
		return $return;	
	}
	
	public function query($store, $query, $language='lucene')
	{
		// TODO need to support paged queries
		$result = $this->repositoryService->query(array(
					"store" => $store->__toArray(),
					"query" => array(
						"language" => $language,
						"statement" => $query),
					"includeMetaData" => false));					
				
		// TODO for now do nothing with the score and the returned data		   	
		$resultSet = $result->queryReturn->resultSet;		
		return $this->resultSetToNodes($this, $store, $resultSet);
	}
	
	/** Read only property accessors */
    
    public function getSessionDetails()
    {
    	return $this->_sessionDetails;
    }
    
	public function getUserName()
	{
		return $this->_sessionDetails->userName;
	}

	public function getTicket()
	{
		return $this->_sessionDetails->ticket;
	}

	public function getRepositoryURL()
	{
		return $this->_sessionDetails->repositoryURL;
	}

	public function getStores()
	{
		if (isset ($this->_stores) == false)
		{
			$this->_stores = array ();
			$results = $this->repositoryService->getStores();

			foreach ($results->getStoresReturn as $result)
			{
				$this->_stores[] = new Store($this, $result->address, $result->scheme);
			}
		}

		return $this->_stores;
	}
	
	/** Want these methods to be package scope some hoe! **/
	
	/**
	 * Adds a new node to the session.
	 */
	public function addNode($node)
	{
		$this->nodeCache[$node->__toString()] = $node;
	}
	
	public function getNode($store, $id)
	{		
		$result = null;
		$nodeRef = $store->scheme . "://" . $store->address . "/" . $id;
		if (array_key_exists($nodeRef, $this->nodeCache) == true)
		{
			$result = $this->nodeCache[$nodeRef];
		}
		return $result;
	}
	
	public function nextSessionId()
	{
		$sessionId = "session".$this->_ticket.$this->idCount;
		$this->idCount ++;
		return $sessionId;
	}
}
?>