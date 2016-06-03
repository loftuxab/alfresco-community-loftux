/*
 * #%L
 * Alfresco greenmail implementation
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
/*
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been used and modified. Original file can be found on http://foedus.sourceforge.net
 */
package com.icegreen.greenmail.foedus.util;

import java.io.*;


public class StringBufferResource
        implements Resource {
    StringWriter _currentWriter;
    StringBuffer _contentBuffer;

    public StringBufferResource() {
    }

    public StringBufferResource(String initalValue) {
        _contentBuffer = new StringBuffer(initalValue);
    }

    public Writer getWriter()
            throws IOException {
        _currentWriter = new StringWriter();

        return _currentWriter;
    }

    public InputStream getInputStream()
            throws IOException {
        closeInput();

        return new ByteArrayInputStream(_contentBuffer.toString().getBytes());
    }

    public Reader getReader()
            throws IOException {
        closeInput();

        return new StringReader(_contentBuffer.toString());
    }

    private void closeInput()
            throws IOException {
        if (_currentWriter != null) {
            _contentBuffer = _currentWriter.getBuffer();
            _currentWriter = null;
        }

        if (_contentBuffer == null)
            throw new IOException("No content has been written");
    }

    public long getSize() {

        return _contentBuffer.toString().length();
    }

    public String getAsString() throws IOException {
        closeInput();
        return _contentBuffer.toString();
    }

    public void delete() {
        _contentBuffer = null;
        _currentWriter = null;
    }

}