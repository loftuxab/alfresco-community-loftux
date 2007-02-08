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
*  File    JNDIConstants.java
*----------------------------------------------------------------------------*/

package org.alfresco.config;

/**
*  Constants to create proper JNDI names for the directories
*  that contain www content.
*  <p>
*  Ultimately, the constants in this fill will go away entirely.
*  This is a stop-gap until we have support multiple virtual AVMHost
*  instances, and a full Spring config (with associated sync to virt server).
*  
*/
public final class JNDIConstants 
{
    /**
    *  Directory used for virtualized web content.
    *  Typically, this directory is a transparent overlay 
    *  on a shared staging area.
    */
    public final static String  DIR_DEFAULT_WWW     = "www";

    /**
    *  Directory in which virtualized webapps reside (e.g.: "ROOT").
    */
    public final static String  DIR_DEFAULT_APPBASE  = "avm_webapps";
}
