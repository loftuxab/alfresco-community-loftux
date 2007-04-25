/*-----------------------------------------------------------------------------
*  Copyright 2007 Alfresco Inc.
*  
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*  
*  This program is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
*  for more details.
*  
*  You should have received a copy of the GNU General Public License along
*  with this program; if not, write to the Free Software Foundation, Inc.,
*  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  As a special
*  exception to the terms and conditions of version 2.0 of the GPL, you may
*  redistribute this Program in connection with Free/Libre and Open Source
*  Software ("FLOSS") applications as described in Alfresco's FLOSS exception.
*  You should have received a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    MD5.java
*----------------------------------------------------------------------------*/

package org.alfresco.util;  
import java.security.*; 

/**
*  The MD5 utility class computes the MD5 digest (aka: "hash") of a block 
*  of data; an MD5 digest is a 32-char ASCII string.
*
*  The synchronized/static function "Digest" is useful for situations where 
*  lock contention in the application is not expected to be an issue.
*
*  The unsynchronized/non-static method "digest" is useful in a 
*  multi-threaded program that wanted to avoid locking by creating
*  an MD5 object for exclusive use by a single thread.
*
*
* <pre>
*  EXAMPLE 1:  Static usage
*
*      import org..alfresco.util.MD5;
*      String x = MD5.Digest("hello".getBytes());
*
*
*  EXAMPLE 2:  Per-thread non-static usage
*
*      import org..alfresco.util.MD5;
*      MD5 md5 = new MD5();
*      ...
*      String x = md5.digest("hello".getBytes());
*
* </pre>
*/
public class MD5
{
    private static final byte[] ToHex_ = 
    { '0','1','2','3','4','5','6','7',
      '8','9','a','b','c','d','e','f'
    };

    private MessageDigest         md5_ = null;

    static private MessageDigest  Md5_; 
    static
    {
        try { Md5_ = MessageDigest.getInstance("MD5");}  // MD5 is supported
        catch ( NoSuchAlgorithmException e ) {};         // safe to swallow
    };

    /**
    *  Constructor for use with the unsynchronized/non-static method 
    *  "digest" method.   Note that the "digest" function is not
    *  thread-safe, so if you want to use it, every thread must create
    *  its own MD5 instance.   If you don't want to bother & are willing
    *  to deal with the potential for lock contention, use the synchronized
    *  static "Digest" function instead of creating an instance via this
    *  constructor.
    */
    public MD5()
    {
        try { md5_ = MessageDigest.getInstance("MD5");}  // MD5 is supported
        catch ( NoSuchAlgorithmException e ) {};         // safe to swallow
    }
  
    /**
    *   Thread-safe static digest (hashing) function.
    *
    *   If you want to avoid lock contention, create an instance of MD5
    *   per-thead, anc call the unsynchronized method 'digest' instead. 
    */
    public static synchronized String Digest(byte[] dataToHash)
    {
        Md5_.update(dataToHash, 0, dataToHash.length);
        return HexStringFromBytes( Md5_.digest() );
    }

    /**
    *  Non-threadsafe MD5 digest (hashing) function
    */
    public String digest(byte[] dataToHash)
    {
        md5_.update(dataToHash, 0, dataToHash.length);
        return HexStringFromBytes( md5_.digest() );
    }
 
    private static String HexStringFromBytes(byte[] b)
    {
        byte [] hex_bytes = new byte[  b.length * 2 ];
        int i=0,j=0;

        for (i=0; i < b.length; i++)
        {
            hex_bytes[j]   = ToHex_[ ( b[i] & 0x000000F0 ) >> 4 ] ;
            hex_bytes[j+1] = ToHex_[   b[i] & 0x0000000F ];
            j+=2;
        }
        return new String( hex_bytes );
    }
} 
