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
package org.alfresco.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @since 2.1
 * @author Derek Hulley
 */
public abstract class AbstractCharactersetFinder implements CharactersetFinder
{
    private static Log logger = LogFactory.getLog(AbstractCharactersetFinder.class);
    private static boolean isDebugEnabled = logger.isDebugEnabled();
    
    /**
     * {@inheritDoc}
     * <p>
     * The input stream is checked to ensure that it supports marks, after which
     * a buffer is extracted, leaving the stream in its original state.
     */
    public final Charset detectCharset(InputStream is)
    {
        // Only support marking streams
        if (!is.markSupported())
        {
            throw new IllegalArgumentException("The InputStream must support marks");
        }
        try
        {
            int bufferSize = getBestBufferSize();
            if (bufferSize < 0)
            {
                throw new RuntimeException("The required buffer size may not be negative: " + bufferSize);
            }
            // Mark the stream for just a few more than we actually will need
            is.mark(bufferSize);
            // Create a buffer to hold the data
            byte[] buffer = new byte[bufferSize];
            // Fill it
            int read = is.read(buffer);
            // Create an appropriately sized buffer
            if (read > -1 && read < buffer.length)
            {
                byte[] copyBuffer = new byte[read];
                System.arraycopy(buffer, 0, copyBuffer, 0, read);
                buffer = copyBuffer;
            }
            // Detect
            return detectCharset(buffer);
        }
        catch (IOException e)
        {
            // Attempt a reset
            throw new AlfrescoRuntimeException("IOException while attempting to detect charset encoding.", e);
        }
        finally
        {
            try { is.reset(); } catch (Throwable ee) {}
        }
    }
    
    public final Charset detectCharset(byte[] buffer)
    {
        try
        {
            Charset charset = detectCharsetImpl(buffer);
            // Done
            if (isDebugEnabled)
            {
                if (charset == null)
                {
                    // Read a few characters for debug purposes
                    logger.debug("\n" +
                            "Failed to identify stream character set: \n" +
                            "   Guessed 'chars': " + buffer);
                }
                else
                {
                    // Read a few characters for debug purposes
                    logger.debug("\n" +
                            "Identified character set from stream:\n" +
                            "   Charset:        " + charset + "\n" +
                            "   Detected chars: " + new String(buffer, charset.name()));
                }
            }
            return charset;
        }
        catch (Throwable e)
        {
            logger.error("IOException while attempting to detect charset encoding.", e);
            return null;
        }
    }

    /**
     * Some implementations may only require a few bytes to do detect the stream type,
     * whilst others may be more efficient with larger buffers.  In either case, the
     * number of bytes actually present in the buffer cannot be enforced.
     * 
     * @return              Returns the maximum desired size of the buffer passed
     *                      to the {@link CharactersetFinder#detectCharset(byte[])} method.
     */
    protected abstract int getBestBufferSize();
    
    /**
     * Worker method for implementations to override.  All exceptions will be reported and
     * absorbed and <tt>null</tt> returned.
     * 
     * @param buffer            the buffer of data no bigger than the requested
     *                          {@linkplain #getBestBufferSize() best buffer size}
     * @return                  Returns the charset or <tt>null</tt> if an accurate conclusion
     *                          is not possible
     * @throws Exception        Any exception, checked or not
     */
    protected abstract Charset detectCharsetImpl(byte[] buffer) throws Exception;
}
