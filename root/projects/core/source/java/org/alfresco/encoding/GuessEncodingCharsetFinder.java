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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import com.glaforge.i18n.io.CharsetToolkit;

/**
 * Uses the <a href="http://glaforge.free.fr/wiki/index.php?wiki=GuessEncoding">Guess Encoding</a>
 * library.
 * 
 * @since 2.1
 * @author Derek Hulley
 */
public class GuessEncodingCharsetFinder extends AbstractCharactersetFinder
{
    /** 8192 bytes */
    private static final int BUFFER_SIZE = 8192;
    /** Dummy charset to detect the default guess */
    private static final Charset DUMMY_CHARSET = new DummyCharset();

    /**
     * @return          Returns {@link #BUFFER_SIZE}
     */
    @Override
    protected int getBestBufferSize()
    {
        return BUFFER_SIZE;
    }
    
    @Override
    protected Charset detectCharsetImpl(byte[] buffer) throws Exception
    {
        CharsetToolkit charsetToolkit = new CharsetToolkit(buffer, DUMMY_CHARSET);
        charsetToolkit.setEnforce8Bit(true);            // Force the default instead of a guess
        Charset charset = charsetToolkit.guessEncoding();
        if (charset == DUMMY_CHARSET)
        {
            return null;
        }
        else
        {
            return charset;
        }
    }
    
    /**
     * A dummy charset to detect a default hit.
     */
    public static class DummyCharset extends Charset
    {
        DummyCharset()
        {
            super("dummy", new String[] {});
        }

        @Override
        public boolean contains(Charset cs)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public CharsetDecoder newDecoder()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public CharsetEncoder newEncoder()
        {
            throw new UnsupportedOperationException();
        }
        
    }
}
