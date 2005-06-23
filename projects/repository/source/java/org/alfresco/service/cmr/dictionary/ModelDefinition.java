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
package org.alfresco.service.cmr.dictionary;

import java.util.Date;

import org.alfresco.service.namespace.QName;


/**
 * Read-only definition of a Model.
 * 
 * @author David Caruana
 */
public interface ModelDefinition
{
    /**
     * @return the model name
     */
    public QName getName();
    
    /**
     * @return the model description
     */
    public String getDescription();
    
    /**
     * @return the model author
     */
    public String getAuthor();
    
    /**
     * @return the date when the model was published
     */
    public Date getPublishedDate();
    
    /**
     * @return the model version
     */
    public String getVersion();

}
