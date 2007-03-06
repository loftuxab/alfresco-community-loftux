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
 
require_once ('BaseTest.php');
require_once ('../Alfresco/Service/Version.php');

class VersionTest extends BaseTest 
{
	public function testVersion() 
	{
		// First create a new content node
		$node = $this->createContentNode("origional content");
		$node->addAspect("cm_titled");
		$node->cm_title = "origional title";
		$node->cm_description = "origional description";
		$this->getSession()->save();
		
		// Try and version the content
		$version = $node->createVersion();
		
		// Do some checks!
		$this->assertTrue($node->hasAspect("cm_versionable"));
		$this->assertEquals("1.0", $node->cm_versionLabel);
		
		// Make some more modifications 
		$node->title = "changed title";
		$node->description = "changed description";
		$node->cm_content->content = "changed content";
		$this->getSession()->save();
		
		// Check that we can still retireve the versioned content and property values
		$this->assertEquals("origional title", $version->cm_title);
		$this->assertEquals("origional description", $version->cm_description);	
		echo "content: ".$version->cm_content->content."<br>";
		echo "content: ".$node->cm_content->content."<br>";
		
		$s = new Store($this->getSession(), "lightWeightVersionStore", "versionStore");
		$v = new Version($this->getSession(), $s, "1e0d08cf-cb34-11db-9eb5-3569d0dd9f0d");
		echo "content: ".$v->cm_content->content."<br>";
	}

	
}
?>

