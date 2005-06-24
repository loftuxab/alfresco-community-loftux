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
package org.alfresco.filesys.locking;

import java.io.IOException;

/**
 * Lock Conflict Exception Class
 * <p>
 * Thrown when a lock request overlaps with an existing lock on a file.
 */
public class LockConflictException extends IOException
{

    // Serializable version id

    private static final long serialVersionUID = 0;

    /**
     * Class constructor.
     */
    public LockConflictException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public LockConflictException(String s)
    {
        super(s);
    }
}
