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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * UTF-8 URL character decoder. Based on an optimized and modified version of the JDK
 * source for the class java.net.URLDecoder.
 * 
 * @author kevinr
 */
public final class URLDecoder
{
    /**
     * Decodes a <code>application/x-www-form-urlencoded</code> string using a specific 
     * encoding scheme.
     * <p>
     * UTF-8 encoding is used to determine what characters are represented by any
     * consecutive sequences of the form "<code>%<i>xy</i></code>".
     * <p>
     * The '+' plus sign is NOT converted to space on the assumption that a sensible
     * URLEncoder class {@link URLEncoder} has been used!
     *
     * @param s the non-null <code>String</code> to decode
     * 
     * @return the decoded <code>String</code>
     */
    public static String decode(final String s) 
    {
        final int len = s.length();
        StringBuilder sb = null;
        int i = 0;
        
        char c;
        byte[] bytes = null;
        while (i < len)
        {
            c = s.charAt(i);
            if (c == '%')
            {
                /*
                 * Starting with this instance of %, process all
                 * consecutive substrings of the form %xy. Each
                 * substring %xy will yield a byte. Convert all
                 * consecutive  bytes obtained this way to whatever
                 * character(s) they represent in the provided
                 * encoding.
                 */
                try
                {
                    if (sb == null)
                    {
                        final String soFar = s.substring(0, i);
                        sb = new StringBuilder(len + 16);
                        sb.append(soFar);
                    }
                    
                    // (numChars-i)/3 is an upper bound for the number
                    // of remaining bytes
                    if (bytes == null)
                    {
                        bytes = new byte[(len-i)/3];
                    }
                    int pos = 0;
                    
                    while ( ((i+2) < len) && (c=='%') )
                    {
                        bytes[pos++] = (byte)Integer.parseInt(s.substring(i+1,i+3),16);
                        i += 3;
                        if (i < len)
                        {
                            c = s.charAt(i);
                        }
                    }
                    
                    // A trailing, incomplete byte encoding such as
                    // "%x" will cause an exception to be thrown
                    if ((i < len) && (c=='%'))
                    {
                        throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
                    }
                    
                    sb.append(new String(bytes, 0, pos, "UTF-8"));
                }
                catch (UnsupportedEncodingException encErr)
                {
                    // this should not happen on any currently support JVMs!
                    throw new IllegalStateException("URLDecoder: Unable to generate UTF-8 String");
                }
                catch (NumberFormatException numErr)
                {
                    throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - " 
                            + numErr.getMessage());
                }
            }
            else
            {
                i++;
                if (sb != null)
                {
                    sb.append(c);
                }
            }
        }
        
        return (sb != null ? sb.toString() : s);
    }
}
