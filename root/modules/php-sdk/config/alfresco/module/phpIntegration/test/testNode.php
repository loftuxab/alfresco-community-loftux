<?php

	// Get the test nodes
	$node = $_ALF_MODEL["testNode"];
	assertNotNull($node, "testNode model value was found to be null");
	$folder = $_ALF_MODEL["testFolder"];
	assertNotNull($folder, "testFolder model value was found to be null");
		
	// Get all properties
	$properties = $node->properties;
	assertNotNull($properties);	
	echo "Properties:\n";
	foreach ($properties as $fullName=>$value)
	{
		echo "   - ".$fullName."=>".$value."\n";	
	}
	assertEquals("testNode.txt", $properties["{http://www.alfresco.org/model/content/1.0}name"]);
	assertEquals("Roy Wetherall", $properties["{http://www.alfresco.org/model/content/1.0}author"]);
	// TODO checks on nodeRefs, dates, etc ....
	
	// Check the dynamic property read
	assertEquals("testNode.txt", $node->cm_name);
	assertEquals("Roy Wetherall", $node->cm_author);
	assertNull($node->cm_junk);
	assertNull($node->junk);
	assertNull($node->cm_title);	
	
	// Check aspects and hasAspect
	$aspects = $node->aspects;
	assertNotNull($aspects);
	echo "Aspects: \n";
	foreach ($aspects as $aspect)
	{
		echo "   - ".$aspect."\n";
	}
	assertTrue($node->hasAspect("{http://www.alfresco.org/model/content/1.0}versionable"));
	assertTrue($node->hasAspect("{http://www.alfresco.org/model/content/1.0}classifiable"));
	assertTrue($node->hasAspect("cm_versionable"));
	assertTrue($node->hasAspect("cm_classifiable"));
	assertFalse($node->hasAspect("{http://www.alfresco.org/model/content/1.0}titled"));
	assertFalse($node->hasAspect("cm_titled"));
	
	// Check children
	$children = $folder->children;
	assertNotNull($children, "children was not expected to be null");
	assertEquals(2, count($children));
	echo "Children: \n";
	foreach ($children as $childAssoc)
	{
		assertNotNull($childAssoc->parent, "the parent of the association was unexpectedly null");
		assertNotNull($childAssoc->child, "the child of the association was unexpectedly null");
		assertEquals($folder->id, $childAssoc->parent->id);
		assertEquals("{http://www.alfresco.org/model/content/1.0}contains", $childAssoc->type);
		echo "   - type:".$childAssoc->type."; parent:".$childAssoc->parent->id."; child:".$childAssoc->child->id."; isPrimary=".$childAssoc->isPrimary."\n";
	}
	
	// Check parents
	$parents = $node->parents;
	assertNotNull($parents, "parents was not expected to be null");
	assertEquals(1, count($parents));
	echo "Parents: \n";
	foreach ($parents as $childAssoc)
	{
		assertNotNull($childAssoc->parent, "the parent of the association was unexpectedly null");
		assertNotNull($childAssoc->child, "the child of the association was unexpectedly null");
		assertEquals($node->id, $childAssoc->child->id);
		assertEquals("{http://www.alfresco.org/model/content/1.0}contains", $childAssoc->type);
		echo "   - type:".$childAssoc->type."; parent:".$childAssoc->parent->id."; child:".$childAssoc->child->id."; isPrimary=".$childAssoc->isPrimary."\n";	
	}
	
	// Check associations
	$associations = $node->associations;
	assertNotNull($associations, "associations was not expected to be null");
	assertEquals(1, count($associations));
	echo "Associations: \n";
	foreach ($associations as $assoc)
	{
		assertNotNull($assoc->from, "the from of the association was unexpectedly null");
		assertNotNull($assoc->to, "the to of the association was unexpectedly null");
		assertEquals($node->id, $assoc->from->id);
		assertEquals("{http://www.alfresco.org/model/content/1.0}references", $assoc->type);
		echo "   - type:".$assoc->type."; from:".$assoc->from->id."; to:".$assoc->to->id."\n";
	}
	
	// Check content property
	$contentData = $node->cm_content;
	assertNotNull($contentData, "contentData was unexpectedly null");
	assertEquals("UTF-8", $contentData->encoding);
	assertEquals("text/plain", $contentData->mimetype);	
	assertNotNull($contentData->url , "url was unexpectedly null");
	assertNotNull($contentData->guestUrl, "guestUrl was unexpectedly null");
	echo "Content Data: \n";
	echo "   - url: ".$contentData->url."\n";
	echo "   - guest Url: ".$contentData->guestUrl."\n";
	
	
?>