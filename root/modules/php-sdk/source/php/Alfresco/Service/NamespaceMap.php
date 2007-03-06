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

class NamespaceMap
{
	public static $namespaceMap = array(
		"d" => "http://www.alfresco.org/model/dictionary/1.0",
		"sys" => "http://www.alfresco.org/model/system/1.0",
		"cm" => "http://www.alfresco.org/model/content/1.0",
		"app" => "http://www.alfresco.org/model/application/1.0",
		"bpm" => "http://www.alfresco.org/model/bpm/1.0");
	
	/**
	 * Tests whether this is the short name
	 */
	public static function isShortName($shortName)
	{
		$result = false;
		$charCount = count_chars($shortName, 1);
		$char = ord("_");
		if (array_key_exists($char, $charCount) == true)
		{
			$result = true;
		}
		return $result;	
	}
	
	public static function getFullName($shortName)
	{		
		$result = null;
		if (NamespaceMap::isShortName($shortName) == true)
		{
			list($prefix, $name) = NamespaceMap::splitShortName($shortName);							
			$url = NamespaceMap::$namespaceMap[$prefix];
			if ($url != null)
			{
				$result = "{".$url."}".$name;
			}
		}
		return $result;
	}	 
	
	private static function splitShortName($shortName)
	{
		$parts = explode("_", $shortName);
		$index = 0;
		$remainder = "";
		foreach($parts as $part)
		{
			if ($index > 0)
			{
				if ($index > 1)
				{
					// Convert the _'s to -'s
					$remainder .= "-";	
				}
				$remainder .= $part;
			}
			$index++;
		}	
		return array($parts[0], $remainder);	
	}   
}

?>
