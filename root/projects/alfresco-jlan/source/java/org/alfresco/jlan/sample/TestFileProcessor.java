/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
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

package org.alfresco.jlan.sample;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.cache.FileState;
import org.alfresco.jlan.server.filesys.loader.FileProcessor;
import org.alfresco.jlan.server.filesys.loader.FileSegment;


/**
 * Test File Processor Class
 *
 * @author gkspencer
 */
public class TestFileProcessor implements FileProcessor {

	/**
	 * Process a cached file just before it is to be stored.
	 * 
	 * @param context
	 * @param state
	 * @param segment
	 */
	public void processStoredFile(DiskDeviceContext context, FileState state, FileSegment segment) {
		try {
			Debug.println("## TestFileProcessor Storing file=" + state.getPath() + ", fid=" + state.getFileId() + ", cache=" + segment.getTemporaryFile());
		}
		catch (Exception ex) {
		}
	}

	/**
	 * Process a cached file just after being loaded.
	 *
	 * @param context
	 * @param state
	 * @param segment
	 */
	public void processLoadedFile(DiskDeviceContext context, FileState state, FileSegment segment) {
		try {
			Debug.println("## TestFileProcessor Loaded file=" + state.getPath() + ", fid=" + state.getFileId() + ", cache=" + segment.getTemporaryFile());
		}
		catch (Exception ex) {
		}
	}

}
