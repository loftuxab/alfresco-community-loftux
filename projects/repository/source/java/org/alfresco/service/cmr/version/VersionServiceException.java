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
package org.alfresco.service.cmr.version;

/**
 * Version service exception class.
 * 
 * @author Roy Wetherall
 */
public class VersionServiceException extends RuntimeException
{     
    private static final long serialVersionUID = 3544671772030349881L;

    public VersionServiceException()
    {
        super();
    }

    public VersionServiceException(String message)
    {
        super(message);
    }

    public VersionServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public VersionServiceException(Throwable cause)
    {
        super(cause);
    }
}
