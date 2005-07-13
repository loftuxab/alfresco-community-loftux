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
package org.alfresco.service.cmr.repository;

import org.alfresco.service.namespace.QName;


/**
 * Thrown a <b>property</b> is not valid or not set.
 * 
 * @author Derek Hulley
 * 
 * @deprecated Used to be thrown before when mandatory properties were not
 *      present where required.  This has been superceded by integrity testing.
 */
@Deprecated
public class PropertyException extends RuntimeException
{
    private static final long serialVersionUID = 3976734787505631540L;

    private QName propertyRef;
    
    public PropertyException(QName propertyRef)
    {
        this(null, propertyRef);
    }

    public PropertyException(String msg, QName propertyRef)
    {
        super(msg);
        this.propertyRef = propertyRef;
    }

    /**
     * @return Returns the offending property reference
     */
    public QName getPropertyRef()
    {
        return propertyRef;
    }
}
