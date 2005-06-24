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
 * NetBIOS name query listener interface.
 */
public interface QueryNameListener
{

    /**
     * Signal that a NetBIOS name query has been received, for the specified local NetBIOS name.
     * 
     * @param evt Local NetBIOS name details.
     * @param addr IP address of the remote node that sent the name query request.
     */
    public void netbiosNameQuery(NetBIOSNameEvent evt, InetAddress addr);
}