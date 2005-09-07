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

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

import org.alfresco.jcr.session.SessionContext;
import org.alfresco.service.cmr.repository.NodeRef;


/**
 * Alfresco Implementation of a JCR Node
 * 
 * @author David Caruana
 */
public class NodeImpl extends ItemImpl implements Node
{
    /** Session Context */
    private SessionContext context;
    
    /** Node Reference to wrap */
    private NodeRef nodeRef;

    
    /**
     * Construct
     * 
     * @param context  session context
     * @param nodeRef  node reference to wrap
     */
    public NodeImpl(SessionContext context, NodeRef nodeRef)
    {
        this.context = context;
        this.nodeRef = nodeRef;
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.Node#addNode(java.lang.String)
     */
    public Node addNode(String relPath) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#addNode(java.lang.String, java.lang.String)
     */
    public Node addNode(String relPath, String primaryNodeTypeName) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#orderBefore(java.lang.String, java.lang.String)
     */
    public void orderBefore(String srcChildRelPath, String destChildRelPath) throws UnsupportedRepositoryOperationException, VersionException, ConstraintViolationException, ItemNotFoundException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Value)
     */
    public Property setProperty(String name, Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Value, int)
     */
    public Property setProperty(String name, Value value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Value[])
     */
    public Property setProperty(String name, Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Value[], int)
     */
    public Property setProperty(String name, Value[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, java.lang.String[])
     */
    public Property setProperty(String name, String[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, java.lang.String[], int)
     */
    public Property setProperty(String name, String[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, java.lang.String)
     */
    public Property setProperty(String name, String value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, java.lang.String, int)
     */
    public Property setProperty(String name, String value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, java.io.InputStream)
     */
    public Property setProperty(String name, InputStream value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, boolean)
     */
    public Property setProperty(String name, boolean value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, double)
     */
    public Property setProperty(String name, double value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, long)
     */
    public Property setProperty(String name, long value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, java.util.Calendar)
     */
    public Property setProperty(String name, Calendar value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#setProperty(java.lang.String, javax.jcr.Node)
     */
    public Property setProperty(String name, Node value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getNode(java.lang.String)
     */
    public Node getNode(String relPath) throws PathNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getNodes()
     */
    public NodeIterator getNodes() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getNodes(java.lang.String)
     */
    public NodeIterator getNodes(String namePattern) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getProperty(java.lang.String)
     */
    public Property getProperty(String relPath) throws PathNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getProperties()
     */
    public PropertyIterator getProperties() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getProperties(java.lang.String)
     */
    public PropertyIterator getProperties(String namePattern) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getPrimaryItem()
     */
    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getUUID()
     */
    public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getIndex()
     */
    public int getIndex() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getReferences()
     */
    public PropertyIterator getReferences() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#hasNode(java.lang.String)
     */
    public boolean hasNode(String relPath) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#hasProperty(java.lang.String)
     */
    public boolean hasProperty(String relPath) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#hasNodes()
     */
    public boolean hasNodes() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#hasProperties()
     */
    public boolean hasProperties() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getPrimaryNodeType()
     */
    public NodeType getPrimaryNodeType() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getMixinNodeTypes()
     */
    public NodeType[] getMixinNodeTypes() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#isNodeType(java.lang.String)
     */
    public boolean isNodeType(String nodeTypeName) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#addMixin(java.lang.String)
     */
    public void addMixin(String mixinName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#removeMixin(java.lang.String)
     */
    public void removeMixin(String mixinName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#canAddMixin(java.lang.String)
     */
    public boolean canAddMixin(String mixinName) throws NoSuchNodeTypeException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getDefinition()
     */
    public NodeDefinition getDefinition() throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#checkin()
     */
    public Version checkin() throws VersionException, UnsupportedRepositoryOperationException, InvalidItemStateException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#checkout()
     */
    public void checkout() throws UnsupportedRepositoryOperationException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#doneMerge(javax.jcr.version.Version)
     */
    public void doneMerge(Version version) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#cancelMerge(javax.jcr.version.Version)
     */
    public void cancelMerge(Version version) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#update(java.lang.String)
     */
    public void update(String srcWorkspaceName) throws NoSuchWorkspaceException, AccessDeniedException, LockException, InvalidItemStateException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#merge(java.lang.String, boolean)
     */
    public NodeIterator merge(String srcWorkspace, boolean bestEffort) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getCorrespondingNodePath(java.lang.String)
     */
    public String getCorrespondingNodePath(String workspaceName) throws ItemNotFoundException, NoSuchWorkspaceException, AccessDeniedException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#isCheckedOut()
     */
    public boolean isCheckedOut() throws RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#restore(java.lang.String, boolean)
     */
    public void restore(String versionName, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#restore(javax.jcr.version.Version, boolean)
     */
    public void restore(Version version, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#restore(javax.jcr.version.Version, java.lang.String, boolean)
     */
    public void restore(Version version, String relPath, boolean removeExisting) throws PathNotFoundException, ItemExistsException, VersionException, ConstraintViolationException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#restoreByLabel(java.lang.String, boolean)
     */
    public void restoreByLabel(String versionLabel, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getVersionHistory()
     */
    public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getBaseVersion()
     */
    public Version getBaseVersion() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#lock(boolean, boolean)
     */
    public Lock lock(boolean isDeep, boolean isSessionScoped) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getLock()
     */
    public Lock getLock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#unlock()
     */
    public void unlock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#holdsLock()
     */
    public boolean holdsLock() throws RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#isLocked()
     */
    public boolean isLocked() throws RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

}
