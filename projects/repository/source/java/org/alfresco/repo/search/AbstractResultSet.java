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
package org.alfresco.repo.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;

public abstract class AbstractResultSet implements ResultSet
{

    private Path[] propertyPaths;
    
    public AbstractResultSet(Path[] propertyPaths)
    {
        super();
        this.propertyPaths = propertyPaths;
    }

    public Path[] getPropertyPaths()
    {
        return propertyPaths;
    }

  
    public float getScore(int n)
    {
        // All have equal weight by default
        return 1.0f;
    }

    public void close()
    {
        // default to do nothing
    }

    public List<NodeRef> getNodeRefs()
    {
        ArrayList<NodeRef> nodeRefs = new ArrayList<NodeRef>(length());
        for(ResultSetRow row: this)
        {
            nodeRefs.add(row.getNodeRef());
        }
        return nodeRefs;
    }

    public List<ChildAssociationRef> getChildAssocRefs()
    {
        ArrayList<ChildAssociationRef> cars = new ArrayList<ChildAssociationRef>(length());
        for(ResultSetRow row: this)
        {
            cars.add(row.getChildAssocRef());
        }
        return cars;
    }

  

}
