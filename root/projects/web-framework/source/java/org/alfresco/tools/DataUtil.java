/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
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
    public static int BUFFER_SIZE = 4 * 1024;

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException
    {
        copyStream(input, output, true);
    }

    public static int copyStream(InputStream in, OutputStream out,
            boolean closeStreams) throws IOException
    {
        try
        {
            int byteCount = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
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

    /*
     * public static void copyStream(InputStream input, OutputStream output,
     * boolean closeStreams) throws IOException { try { byte[] buffer = new
     * byte[BUFFER_SIZE]; int numBytesRead;
     * 
     * while ((numBytesRead = input.read(buffer)) != -1) { output.write(buffer,
     * 0, numBytesRead); } } finally { if (closeStreams) { closeStream(input);
     * closeStream(output); } } }
     */

    public static byte[] toByteArray(InputStream input) throws IOException
    {
        return toByteArray(input, true);
    }

    public static byte[] toByteArray(InputStream input, boolean closeStream)
            throws IOException
    {
        return copyToByteArray(input, closeStream).toByteArray();
    }

    public static String toString(InputStream input) throws IOException
    {
        return copyToByteArray(input, true).toString();
    }

    public static void closeStream(InputStream input)
    {
        try
        {
            if (input != null)
            {
                input.close();
            }
        }
        catch (IOException ignored)
        {
        }
    }

    public static void closeStream(OutputStream output)
    {
        try
        {
            if (output != null)
            {
                output.close();
            }
        }
        catch (IOException ignored)
        {
        }
    }

    public static ByteArrayOutputStream copyToByteArray(InputStream input,
            boolean closeInputStream) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try
        {
            copyStream(input, buffer, closeInputStream);
        }
        finally
        {
            closeStream(buffer);
        }

        return buffer;
    }

    public static String copyToString(InputStream input,
            boolean closeInputStream) throws IOException
    {
        ByteArrayOutputStream baos = copyToByteArray(input, closeInputStream);
        return new String(baos.toByteArray());
    }

}
