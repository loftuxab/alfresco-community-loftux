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
package org.alfresco.repo.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Type Definition
 * 
 * @author David Caruana
 *
 */
public class M2Type extends M2Class
{
    private List<String> mandatoryAspects = new ArrayList<String>();
    
    /*package*/ M2Type()
    {
        super();
    }
    
   
    public void addMandatoryAspect(String name)
    {
        mandatoryAspects.add(name);
    }
    
    
    public void removeMandatoryAspect(String name)
    {
        mandatoryAspects.remove(name);
    }
    

    public List<String> getMandatoryAspects()
    {
        return Collections.unmodifiableList(mandatoryAspects);
    }    

}
