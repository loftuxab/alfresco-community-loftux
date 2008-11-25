/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.web.framework.render;

/**
 * Describes modes for rendering
 * 
 * @author muzquiano
 */
public enum RenderMode 
{
	VIEW("view"),
	EDIT("edit"),
	HELP("help"),
	ADMIN("admin");
	
	private final String mode;
	
	private RenderMode(String mode)
	{
		this.mode = mode;
	}
	
	@Override
	public String toString()
	{
		return this.mode;
	}

	// TODO: I understand from Kevin that this method is not
	// necessary.  However, when I tried to use his changes, they
	// broke web studio.  Apparently, the way he was doing it is
	// not equivalent to doing fromString as it is shown here since
	// it was always returning null.
	//
	// TODO: Investigate because I would like to use Kevin's method
	// however the primary goal is to get things working so this will
	// be left in for the moment
	//
	public static RenderMode fromString(String mode)
	{
		for(RenderMode e: values())
		{
			if(e.mode.equals(mode))
			{
				return e;
			}
		}
		return null;
	}
}
