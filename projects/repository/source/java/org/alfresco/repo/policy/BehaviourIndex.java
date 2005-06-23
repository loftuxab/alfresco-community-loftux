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
package org.alfresco.repo.policy;

import java.util.Collection;


/**
 * Index of Bound Behaviours.
 * 
 * @author David Caruana
 *
 * @param <B>  the type of Binding.
 */
/*package*/ interface BehaviourIndex<B extends BehaviourBinding>
{
    /**
     * Gets all bound behaviours
     * 
     * @return  the bound behaviours
     */
    public Collection<BehaviourDefinition> getAll();
    
    /**
     * Gets all bound behaviours for the specified binding.
     * 
     * Note: The index may use any algorithm for determining which behaviours
     *       are returned for the binding e.g. based on hierarchical binding
     *       
     * @param binding  the binding
     * @return  the associated behaviours
     */
    public Collection<BehaviourDefinition> find(B binding);

    /**
     * Add a Behaviour Change Observer.
     * 
     * @param observer  the observer
     */
    public void addChangeObserver(BehaviourChangeObserver<B> observer);
}
