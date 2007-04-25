<?php
	
	// Tests the global values set my the script/template engine, both those added by the
	// engine and those added from the model
	
	// Check the repository value
	assertNotNull($_REPOSITORY, "_REPOSITORY was found to be null");  
	$newSession = $_REPOSITORY->createSession();
	assertNotNull($newSession, "newSession was found to be null");
	
	// Check the session value
	assertNotNull($_SESSION, "_SESSION was found to be null");
	assertNotNull($_SESSION->stores);
	assertNotNull($_SESSION->ticket);
	
	// Check the node value set in the model and passed through
	assertNotNull($testNode, "testNode was found to be null");
	assertEquals($nodeId, $testNode->id);
	
	// Check the store ref value passed through
	assertNotNull($testStore, "testStore was found to be null");
	assertEquals($storeId, $testStore->address);
	
	// Check the other values set in the model
	assertEquals("testString", $testString);
	assertEquals(1.0, $testNumber);
		  
?>
