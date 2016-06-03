
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
