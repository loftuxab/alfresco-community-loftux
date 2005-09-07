/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jcr.node;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;


/**
 * Alfresco Implementation of an Item
 * 
 * @author David Caruana
 */
public class ItemImpl implements Item
{

    /* (non-Javadoc)
     * @see javax.jcr.Item#getPath()
     */
    public String getPath() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getName()
     */
    public String getName() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getAncestor(int)
     */
    public Item getAncestor(int depth) throws ItemNotFoundException, AccessDeniedException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getParent()
     */
    public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getDepth()
     */
    public int getDepth() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getSession()
     */
    public Session getSession() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#isNode()
     */
    public boolean isNode()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#isNew()
     */
    public boolean isNew()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#isModified()
     */
    public boolean isModified()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#isSame(javax.jcr.Item)
     */
    public boolean isSame(Item otherItem) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#accept(javax.jcr.ItemVisitor)
     */
    public void accept(ItemVisitor visitor) throws RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#save()
     */
    public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#refresh(boolean)
     */
    public void refresh(boolean keepChanges) throws InvalidItemStateException, RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#remove()
     */
    public void remove() throws VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

}
