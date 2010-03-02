/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
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
