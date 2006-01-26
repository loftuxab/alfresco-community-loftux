/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.webservice.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.alfresco.webservice.content.Content;

/**
 * @author Roy Wetherall
 */
public class ContentUtils
{   
    /**
     * Convert an input stream to a byte array
     * 
     * @param inputStream   the input stream
     * @return              the byte array
     * @throws Exception
     */
    public static byte[] convertToByteArray(InputStream inputStream) throws Exception
    {
        byte[] result = null;
        
        if (inputStream.available() > 0)
        {
            result = new byte[inputStream.available()];        
            inputStream.read(result);;
        }     
        
        return result;
    }
    
    /**
     * Get the content from the download servlet as a string
     * 
     * @param content   the content object
     * @return          the content as a string
     */
    public static String getContentAsString(Content content)
    {
        // Get the url and the ticket
        String ticket = AuthenticationUtils.getCurrentTicket();
        String strUrl = content.getUrl() + "?ticket=" + ticket;
        
        StringBuilder readContent = new StringBuilder();
        try
        {
            // Connect to donwload servlet            
            URL url = new URL(strUrl);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            int read = is.read();
            while (read != -1)
            {
               readContent.append((char)read);
               read = is.read();
            }
        }
        catch (Exception exception)
        {
            throw new WebServiceException("Unable to get content as string.", exception);
        }
        
        // return content as a string
        return readContent.toString();
    }
}
