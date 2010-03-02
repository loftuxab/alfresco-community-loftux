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
