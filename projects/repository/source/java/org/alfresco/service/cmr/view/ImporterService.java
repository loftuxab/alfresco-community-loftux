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
package org.alfresco.service.cmr.view;

import java.io.InputStream;
import java.util.Properties;




/**
 * Importer Service.  Entry point for importing xml data sources into the Repository.
 * 
 * @author David Caruana
 *
 */
public interface ImporterService
{

    /**
     * Import a Repository view into the specified location
     * 
     * @param inputStream  input stream containing the xml view to parse
     * @param location  the location to import under
     * @param configuration  property values used for binding property place holders in import stream
     * @param progress  progress monitor (optional)
     */
    public void importView(InputStream inputStream, Location location, Properties configuration, ImporterProgress progress)
        throws ImporterException;

}
