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

import java.io.Serializable;
import java.util.Collection;



/**
 * Version history interface.
 * 
 * Collects the versions that make-up a version history.
 * 
 * @author Roy Wetherall
 */
public interface VersionHistory extends Serializable
{
    /**
     * Gets the root (or initial) version of the version history.
     * 
     * @return  the root version
     */
    public Version getRootVersion();
    
    /**
     * Gets a collection containing all the versions within the
     * version history.
     * <p>
     * The order of the versions is not guarenteed.
     * 
     * @return  collection containing all the versions
     */
    public Collection<Version> getAllVersions();

    /**
     * Gets the predecessor of a specified version
     * 
     * @param version  the version object
     * @return         the predeceeding version, null if root version
     */
    public Version getPredecessor(Version version);

    /**
     * Gets the succeeding versions of a specified version.
     * 
     * @param version  the version object
     * @return         a collection containing the succeeding version, empty is none
     */
    public Collection<Version> getSuccessors(Version version);
    
    /**
     * Gets a version with a specified version label.  The version label is guarenteed 
     * unique within the version history.
     * 
     * @param versionLabel                   the version label
     * @return                               the version object
     * @throws VersionDoesNotExistException  indicates requested version does not exisit
     */
    public Version getVersion(String versionLabel);

}
