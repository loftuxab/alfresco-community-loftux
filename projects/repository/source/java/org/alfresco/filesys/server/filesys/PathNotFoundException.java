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
package org.alfresco.filesys.server.filesys;

import java.io.IOException;

/**
 * Path Not Found Exception Class
 * <p>
 * Indicates that the upper part of a path does not exist, as opposed to the file/folder at the end
 * of the path.
 */
public class PathNotFoundException extends IOException
{
    private static final long serialVersionUID = 4050768191053378616L;

    /**
     * Class constructor.
     */
    public PathNotFoundException()
    {
        super();
    }

    /**
     * Class constructor.
     * 
     * @param s java.lang.String
     */
    public PathNotFoundException(String s)
    {
        super(s);
    }
}
