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
*  File    AVMHostMatch.java
*----------------------------------------------------------------------------*/

package org.alfresco.catalina.host;
import java.util.regex.Matcher;

public class AVMHostMatch
{
    /** @exclude */
    protected AVMHost host_;

    /** @exclude */
    protected Matcher match_;

    public AVMHost getHost()  { return host_; }
    public Matcher getMatch() { return match_;}

    public AVMHostMatch( AVMHost host, Matcher match)
    {
        host_  = host;
        match_ = match;
    }
}
