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
package org.alfresco.filesys.server.config;

/**
 * <p>
 * Indicates that one or more parameters in the server configuration are not valid.
 */
public class InvalidConfigurationException extends Exception
{
    private static final long serialVersionUID = 3257568390900887607L;

    /**
     * InvalidConfigurationException constructor.
     * 
     * @param s java.lang.String
     */
    public InvalidConfigurationException(String s)
    {
        super(s);
    }

    /**
     * InvalidConfigurationException constructor.
     * 
     * @param s java.lang.String
     * @param ex Exception
     */
    public InvalidConfigurationException(String s, Throwable ex)
    {
        super(s, ex);
    }
}