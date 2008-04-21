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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/*
 * Useful for generating non-typed GUIDs.
 * 
 * This code borrows concepts from RandomGUID. RandomGUID was written by Marc A.
 * Mnich and is available here: www.JavaExchange.com
 * 
 * @author muzquiano
 */
public class ObjectGUID
{
    public String valueBeforeMD5 = "";
    public String valueAfterMD5 = "";
    private static Random myRand;
    private static SecureRandom mySecureRand;
    private static String s_id;

    static
    {
        mySecureRand = new SecureRandom();
        long secureInitializer = mySecureRand.nextLong();
        myRand = new Random(secureInitializer);
        try
        {
            s_id = InetAddress.getLocalHost().toString();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }

    }

    public ObjectGUID()
    {
        getRandomGUID(false);
    }

    public ObjectGUID(boolean secure)
    {
        getRandomGUID(secure);
    }

    private void getRandomGUID(boolean secure)
    {
        MessageDigest md5 = null;
        StringBuilder sbValueBeforeMD5 = new StringBuilder();

        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println("Error: " + e);
        }

        try
        {
            long time = System.currentTimeMillis();
            long rand = 0;

            if (secure)
            {
                rand = mySecureRand.nextLong();
            }
            else
            {
                rand = myRand.nextLong();
            }

            // StringBuilder
            sbValueBeforeMD5.append(s_id);
            sbValueBeforeMD5.append(":");
            sbValueBeforeMD5.append(Long.toString(time));
            sbValueBeforeMD5.append(":");
            sbValueBeforeMD5.append(Long.toString(rand));

            valueBeforeMD5 = sbValueBeforeMD5.toString();
            md5.update(valueBeforeMD5.getBytes());

            byte[] array = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < array.length; ++j)
            {
                int b = array[j] & 0xFF;
                if (b < 0x10)
                    sb.append('0');
                sb.append(Integer.toHexString(b));
            }

            valueAfterMD5 = sb.toString();

        }
        catch (Exception e)
        {
            System.out.println("Error:" + e);
        }
    }

    // truncate the string output to be a bit shorter
    public String toString()
    {
        /*
        String raw = valueAfterMD5.toUpperCase();
        StringBuilder sb = new StringBuilder();
        sb.append(raw.substring(0, 8));
        sb.append("-");
        sb.append(raw.substring(8, 12));
        sb.append("-");
        sb.append(raw.substring(20));
        */
        
        String raw = valueAfterMD5.toLowerCase();
        StringBuilder sb = new StringBuilder();
        sb.append(raw.substring(0,6));
        sb.append(raw.substring(8,12));

        return sb.toString();
    }
}
