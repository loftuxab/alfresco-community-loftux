/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.util;

import java.util.Date;

/**
 * Utility helper class providing helper methods for the conversion of values.
 *  
 * @author Kevin Roast
 */
public final class Conversion
{
   /**
    * Private constructor
    */
   private Conversion()
   {
   }
   
   /**
    * Convert a Date to an XML format date of the form <pre>yyyy-mm-ddThh:mm:ss</pre>
    * 
    * @param date    Non-null Date value to convert
    * 
    * @return XML format date string
    */
   public static String dateToXmlDate(Date date)
   {
      StringBuilder buf = new StringBuilder(20);

      // perform year conversion by hand to guarentee universal format
      String month = Integer.toString(date.getMonth() + 1);
      String day = Integer.toString(date.getDate());
      String hour = Integer.toString(date.getHours());
      String minute = Integer.toString(date.getMinutes());
      String second = Integer.toString(date.getSeconds());
      
      buf.append(date.getYear() + 1900)
         .append('-');
      
      if (month.length() == 1)
      {
         buf.append('0');
      }
      buf.append(month)
         .append('-');
      
      if (day.length() == 1)
      {
         buf.append('0');
      }
      buf.append(day)
         .append('T');
      
      if (hour.length() == 1)
      {
         buf.append('0');
      }
      buf.append(hour)
         .append(":");
      
      if (minute.length() == 1)
      {
         buf.append('0');
      }
      buf.append(minute)
      .append(":");

      if (second.length() == 1)
      {
         buf.append('0');
      }
      buf.append(second);
      
      return buf.toString();
   }
   
   /**
    * Convert an XML format date of the form <pre>yyyy-mm-ddThh:mm:ss</pre> to a Date object
    * 
    * @param xml
    * 
    * @return Date object or null if no XML date was provided
    */
   public static Date dateFromXmlDate(String xml)
   {
      if (xml == null || xml.length() != 19)
      {
         return null;
      }
      
      int year    = Integer.parseInt(xml.substring(0, 4));
      int month   = Integer.parseInt(xml.substring(5, 7));
      int day     = Integer.parseInt(xml.substring(8, 10));
      int hours   = Integer.parseInt(xml.substring(11, 13));
      int mins    = Integer.parseInt(xml.substring(14, 16));
      int secs    = Integer.parseInt(xml.substring(17, 19));
      
      Date date = new Date(year - 1900, month - 1, day, hours, mins, secs);
      
      return date;
   }
}
