package org.alfresco.tools;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class EncodingUtil
{
    public static String DEFAULT_ENCODING = "utf-8";
    
    public static String encode(String input)
    {
        return encode(input, DEFAULT_ENCODING);
    }
    
    public static String encode(String input, String encoding)
    {
        String output = null;
        try 
        {
            output = URLEncoder.encode(input, encoding);
        }
        catch(Exception ex) { }
        return output;       
    }
    
    public static String decode(String input)
    {
        return decode(input, DEFAULT_ENCODING);
    }

    public static String decode(String input, String encoding)
    {
        String output = null;
        try 
        {
            output = URLDecoder.decode(input, encoding);
        }
        catch(Exception ex) { }
        return output;       
    }

}
