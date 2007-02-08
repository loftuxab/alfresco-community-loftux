/*-----------------------------------------------------------------------------
*  Copyright 2006 Alfresco Inc.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    AVMResourceBinding.java
*----------------------------------------------------------------------------*/


package org.alfresco.catalina.host;
import java.util.regex.Matcher;

/**
*   Inteface for classes that use data collected from the 
*   AVMHost's reverseProxyBinding to calculate parameters
*   for resource lookup within AVMService (e.g.: the version
*   number and virtual repository name).
*/
public interface AVMResourceBinding
{
    /**
    *  Fetch the name of the virtual repository indicated by
    *  data obtained when the reverseProxyBinding 
    *  regex was matched within the AVMUrlValve.
    */
    public String getRepositoryName(Matcher match);

    /**
    *  Fetch the version of the resource indicated by
    *  data obtained when the reverseProxyBinding 
    *  regex was matched within the AVMUrlValve.
    *  <p>
    *  Note: "-1" corresponds to the HEAD version.
    */
    public String getVersion(Matcher match);
}
