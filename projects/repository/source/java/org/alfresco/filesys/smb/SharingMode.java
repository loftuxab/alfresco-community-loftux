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
package org.alfresco.filesys.smb;

/**
 * File Sharing Mode Class
 */
public class SharingMode
{

    // File sharing mode constants

    public final static int NOSHARING = 0x0000;
    public final static int READ = 0x0001;
    public final static int WRITE = 0x0002;
    public final static int DELETE = 0x0004;

    public final static int READWRITE = READ + WRITE;
}
