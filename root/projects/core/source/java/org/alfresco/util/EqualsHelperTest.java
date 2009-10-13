/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

/**
 * @see EqualsHelper
 * 
 * @author Derek Hulley
 * @since 3.1SP2
 */
public class EqualsHelperTest extends TestCase
{
    private File fileOne;
    private File fileTwo;
    
    @Override
    public void setUp() throws Exception
    {
        fileOne = TempFileProvider.createTempFile(getName(), "-one.txt");
        fileTwo = TempFileProvider.createTempFile(getName(), "-two.txt");
        
        OutputStream osOne = new FileOutputStream(fileOne);
        osOne.write("1234567890 - ONE".getBytes("UTF-8"));
        osOne.close();
        
        OutputStream osTwo = new FileOutputStream(fileTwo);
        osTwo.write("1234567890 - TWO".getBytes("UTF-8"));
        osTwo.close();
    }
    
    public void testStreamsNotEqual() throws Exception
    {
        InputStream isLeft = new FileInputStream(fileOne);
        InputStream isRight = new FileInputStream(fileTwo);
        boolean equal = EqualsHelper.binaryStreamEquals(isLeft, isRight);
        assertFalse("Should not be the same", equal);
    }
    
    public void testStreamsEqual() throws Exception
    {
        InputStream isLeft = new FileInputStream(fileOne);
        InputStream isRight = new FileInputStream(fileOne);
        boolean equal = EqualsHelper.binaryStreamEquals(isLeft, isRight);
        assertTrue("Should be the same", equal);
    }
}
