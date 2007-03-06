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
 
require_once('PHPUnit2/Framework/TestCase.php');
require_once('../Alfresco/Service/Session.php');
require_once('../Alfresco/Service/Store.php');

class BaseTest extends PHPUnit2_Framework_TestCase
{
   const USERNAME = "admin";
   const PASSWORD = "admin";
   const URL = "http://localhost:8080/alfresco/api";
  
   private static $_session; 
   private static $_companyHome;  
   
   protected function getStore()
   {
      return new Store($this->getSession(), "SpacesStore");	
   }
   
   protected function getSession()
   {
   	  if (self::$_session == null)
   	  {
   	     // Start the Alfresco session
	     self::$_session = Session::create(BaseTest::USERNAME, BaseTest::PASSWORD, BaseTest::URL);
   	  }
   	  return self::$_session;
   }
   
   protected function getCompanyHome()
   {
   	   if (self::$_companyHome == null)
   	   {
   	   	  $nodes = $this->getSession()->query($this->getStore(), 'PATH:"app:company_home"');
	           self::$_companyHome = $nodes[0]; 
   	   }
   	   return self::$_companyHome;
   }
   
   protected function createContentNode($content="Some simple content")
   {
   		// Create a new content node in the company home
		$fileName = "myDoc_" . time() . ".txt";
		$contentNode = $this->getCompanyHome()->createChild(
														"cm_content", 
														"cm_contains", 
														"app_" .$fileName);
		$contentNode->cm_name = $fileName;
		
		$contentData = new ContentData("text/plain", "UTF-8");
		$contentData->content = $content;
		$contentNode->cm_content = $contentData;
		
		// Save new content
		$this->getSession()->save();	
		
		return $contentNode;
   }
}
?>
