/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *
 * Created on 27-Jun-2005
 */
package org.alfresco.repo.importer;

import java.io.File;
import java.io.FileFilter;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Interface to load files and directories into the hub.
 * All will be created as new - there is no detection if a file exists or has changed etc..
 * 
 * @author andyh
 */
public interface FileImporter
{
    /**
     * Load a file or directory into the repository
     * 
     * @param container - the node into which to insert the file or directory
     * @param file - the start point for the import
     * @param recurse - if the start point is a directoty then recurse
     * @throws FileImporterException
     */
    public void loadFile(NodeRef container, File file, boolean recurse) throws FileImporterException;
    
    /**
     * Load all files or directories that match the file filter in the given directory
     * 
     * @param container
     * @param file
     * @param filter
     * @param recurse
     * @throws FileImporterException
     */
    public void loadFile(NodeRef container, File file, FileFilter filter, boolean recurse) throws FileImporterException;
    
    
    /**
     * Load a single file or directory without any recursion
     * 
     * @param container
     * @param file
     * @throws FileImporterException
     */
    public void loadFile(NodeRef container, File file) throws FileImporterException;
    
}
