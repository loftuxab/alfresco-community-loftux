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
package org.alfresco.error;

/**
 * Runtime exception thrown by Alfresco code
 * 
 * @author gavinc
 */
public class AlfrescoRuntimeException extends RuntimeException
{
   private static final long serialVersionUID = 3834594313622859827L;

   public AlfrescoRuntimeException(String msg)
    {
        super(msg);
    }

    public AlfrescoRuntimeException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
