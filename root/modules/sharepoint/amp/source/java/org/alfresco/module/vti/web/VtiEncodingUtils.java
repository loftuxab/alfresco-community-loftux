/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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

package org.alfresco.module.vti.web;

import java.util.HashMap;
import java.util.Map;

/**
* <p>VtiEncodingUtils is used for encoding strings to specific FrontPage extension format.</p>   
* 
* @author Stas Sokolovsky
*/
public class VtiEncodingUtils
{
    
    private static Map<Character, String> encodingMap = new HashMap<Character, String>();
    
    /**
     * <p>Encode string to specific FrontPage extension format. </p> 
     *
     * @param original original string 
     */
    public static String encode(String original)
    {
        String result = original;
        try
        {
            String transformedString = new String(original.getBytes("UTF-8"), "ISO-8859-1");
            StringBuffer resultBuffer = new StringBuffer();
            
            for (int i = 0; i < transformedString.length(); i++)
            {
                String specialCharacter = null;
                if ((specialCharacter = encodingMap.get(transformedString.charAt(i))) != null) {
                    resultBuffer.append(specialCharacter);
                } else if ((int)transformedString.charAt(i) < 128) {
                    resultBuffer.append(Character.valueOf(transformedString.charAt(i)));
                } else {
                    addCharacter(transformedString.charAt(i), resultBuffer);
                }
            }
            result = resultBuffer.toString();
        }
        catch (Exception e)
        {
            // ignore
        }
        return result;
    }

    private static void addCharacter(char character, StringBuffer resultBuffer)
    {
        resultBuffer.append("&#");
        resultBuffer.append((int) (character));
        resultBuffer.append(';');
    }

    static
    {
        encodingMap.put('=', "&#61;");
        encodingMap.put('{', "&#123;");
        encodingMap.put('}', "&#125;");
        encodingMap.put('&', "&#38;");
        encodingMap.put(';', "&#59;");
        encodingMap.put('\'', "&#39;");
    }    
      
}
