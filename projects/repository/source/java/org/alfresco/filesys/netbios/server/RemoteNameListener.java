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
package org.alfresco.filesys.netbios.server;

import java.net.InetAddress;

/**
 * NetBIOS remote name listener interface.
 */
public interface RemoteNameListener
{

    /**
     * Signal that a remote host has added a new NetBIOS name.
     * 
     * @param evt NetBIOSNameEvent
     * @param addr java.net.InetAddress
     */
    public void netbiosAddRemoteName(NetBIOSNameEvent evt, InetAddress addr);

    /**
     * Signal that a remote host has released a NetBIOS name.
     * 
     * @param evt NetBIOSNameEvent
     * @param addr java.net.InetAddress
     */
    public void netbiosReleaseRemoteName(NetBIOSNameEvent evt, InetAddress addr);
}