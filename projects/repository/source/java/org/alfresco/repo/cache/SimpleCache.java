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
package org.alfresco.repo.cache;

import java.io.Serializable;

/**
 * Basic caching interface.
 * <p>
 * All implementations <b>must</b> be thread-safe.  Additionally, the use of the
 * <tt>Serializable</tt> for both keys and values ensures that the underlying
 * cache implementations can support both clustered caches as well as persistent
 * caches.
 * 
 * @author Derek Hulley
 */
public interface SimpleCache
{
    public boolean contains(Serializable key);
    
    public Serializable get(Serializable key);
    
    public void put(Serializable key, Serializable value);
    
    public void remove(Serializable key);
}
