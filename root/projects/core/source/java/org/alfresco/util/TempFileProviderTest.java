/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.util;

import java.io.File;

import junit.framework.TestCase;

/**
 * @see org.alfresco.util.TempFileProvider
 * 
 * @author Derek Hulley
 */
public class TempFileProviderTest extends TestCase
{
    public void testTempDir() throws Exception
    {
        File tempDir = TempFileProvider.getTempDir();
        assertTrue("Not a directory", tempDir.isDirectory());
        File tempDirParent = tempDir.getParentFile();
        
        // create a temp file
        File tempFile = File.createTempFile("AAAA", ".tmp");
        File tempFileParent = tempFile.getParentFile();
        
        // they should be equal
        assertEquals("Our temp dir not subdirectory system temp directory",
                tempFileParent, tempDirParent);
    }
    
    public void testTempFile() throws Exception
    {
        File tempFile = TempFileProvider.createTempFile("AAAA", ".tmp");
        File tempFileParent = tempFile.getParentFile();
        File tempDir = TempFileProvider.getTempDir();
        assertEquals("Temp file not located in our temp directory",
                tempDir, tempFileParent);
    }
}
