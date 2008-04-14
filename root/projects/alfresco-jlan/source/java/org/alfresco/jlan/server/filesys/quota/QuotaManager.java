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

package org.alfresco.jlan.server.filesys.quota;

import java.io.IOException;

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.DiskInterface;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.TreeConnection;


/**
 * Quota Manager Interface
 * 
 * <p>Provides the basic interface for filesystem disk quota management.
 *
 * @author gkspencer
 */
public interface QuotaManager {

	/**
	 * Start the quota manager.
	 * 
	 * @param disk DiskInterface
	 * @param ctx DiskDeviceContext
	 * @exception QuotaManagerException
	 */
	public void startManager(DiskInterface disk, DiskDeviceContext ctx)
		throws QuotaManagerException;

	/**
	 * Stop the quota manager
	 * 
	 * @param disk DiskInterface
	 * @param ctx DiskDeviceContext
	 * @exception QuotaManagerException
	 */
	public void stopManager(DiskInterface disk, DiskDeviceContext ctx)
		throws QuotaManagerException;
		
	/**
	 * Allocate space on the filesystem.
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param file NetworkFile
	 * @param alloc long
	 * @return long
	 * @exception IOException
	 */
	public long allocateSpace(SrvSession sess, TreeConnection tree, NetworkFile file, long alloc)
		throws IOException;
		
	/**
	 * Release space to the free space for the filesystem.
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @param fid int
	 * @param path String
	 * @param alloc long
	 * @exception IOException
	 */
	public void releaseSpace(SrvSession sess, TreeConnection tree, int fid, String path, long alloc)
		throws IOException;
		
	/**
	 * Return the free space available in bytes
	 * 
	 * @return long
	 */
	public long getAvailableFreeSpace();
	
	/**
	 * Return the free space available to the specified user/session
	 *
	 * @param sess SrvSession
	 * @param tree TreeConnection
	 * @return long
	 */
	public long getUserFreeSpace(SrvSession sess, TreeConnection tree);
}
