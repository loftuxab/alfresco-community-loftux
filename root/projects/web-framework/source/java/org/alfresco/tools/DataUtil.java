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
package org.alfresco.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A collection of useful static methods for working with input and output
 * streams.
 * 
 * @author muzquiano
 */
public class DataUtil
{
    private static int BUFFER_SIZE = 1024;

    /**
     * Copy stream.
     * 
     * @param input
     *            the input
     * @param output
     *            the output
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void copyStream(InputStream input, OutputStream output)
            throws IOException
    {
        copyStream(input, output, true);
    }

    /**
     * Copy stream.
     * 
     * @param in
     *            the in
     * @param out
     *            the out
     * @param closeStreams
     *            the close streams
     * 
     * @return the int
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static int copyStream(final InputStream in, final OutputStream out, final boolean closeStreams)
        throws IOException
    {
        try
        {
            int byteCount = 0;
            final byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        }
        finally
        {
            if (closeStreams)
            {
                try
                {
                    in.close();
                }
                catch (IOException ex)
                {
                }
                try
                {
                    out.close();
                }
                catch (IOException ex)
                {
                }
            }
        }
    }

    /**
     * To byte array.
     * 
     * @param input
     *            the input
     * 
     * @return the byte[]
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static byte[] toByteArray(InputStream input) throws IOException
    {
        return toByteArray(input, true);
    }

    /**
     * To byte array.
     * 
     * @param input
     *            the input
     * @param closeStream
     *            the close stream
     * 
     * @return the byte[]
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static byte[] toByteArray(InputStream input, boolean closeStream)
            throws IOException
    {
        return copyToByteArray(input, closeStream).toByteArray();
    }

    /**
     * Copy to byte array.
     * 
     * @param input
     *            the input
     * @param closeInputStream
     *            the close input stream
     * 
     * @return the byte array output stream
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static ByteArrayOutputStream copyToByteArray(InputStream input, boolean closeInputStream)
        throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(BUFFER_SIZE);
        
        copyStream(input, buffer, closeInputStream);
        
        return buffer;
    }

    /**
     * Copy to string.
     * 
     * @param input     the input
     * @param closeInputStream  the close input stream
     * 
     * @return the string
     * 
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    public static String copyToString(InputStream input, String encoding, boolean closeInputStream)
        throws IOException
    {
        ByteArrayOutputStream baos = copyToByteArray(input, closeInputStream);
        return encoding != null ? new String(baos.toByteArray(), encoding) : new String(baos.toByteArray());
    }
}
