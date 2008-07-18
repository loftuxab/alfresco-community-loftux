package org.alfresco.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class URLUtil
{
    /**
     * Performs an unauthenticated connection and retrieves the content
     * 
     * @param url
     * @return
     */
    public static String get(String url)
    {
        String data = null;

        InputStream in = null;
        try 
        {
            URL u = new URL(url);
            in = u.openStream();
            
            data = DataUtil.copyToString(in, true);
        }
        catch (MalformedURLException mue)
        {
            mue.printStackTrace();
            // TODO: Handle
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            //TODO: FileNotFoundException caught here
        }
        finally
        {
            try 
            {
                in.close(); 
            } 
            catch(Exception e) { }
            
            //TODO
        }
        
        return data;
    }
}
