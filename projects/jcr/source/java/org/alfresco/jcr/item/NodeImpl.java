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
package org.alfresco.jcr.item;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
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

import org.alfresco.jcr.dictionary.NodeTypeImpl;
import org.alfresco.jcr.proxy.JCRProxyFactory;
import org.alfresco.jcr.session.SessionImpl;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.Path.Element;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;


/**
 * Alfresco Implementation of a JCR Node
 * 
 * @author David Caruana
 */
public class NodeImpl extends ItemImpl implements Node
{
    /** Node Reference to wrap */
    private NodeRef nodeRef;

    /** Proxy */
    private Node proxy = null;
    
    
    /**
     * Construct
     * 
     * @param context  session context
     * @param nodeRef  node reference to wrap
     */
    public NodeImpl(SessionImpl context, NodeRef nodeRef)
    {
        super(context);
        this.nodeRef = nodeRef;
    }

    /**
     * Get Node Proxy
     * 
     * @param nodeImpl
     * @return
     */
    @Override
    public Node getProxy()
    {
        if (proxy == null)
        {
            proxy = (Node)JCRProxyFactory.create(this, Node.class, session); 
        }
        return proxy;
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
        NodeImpl nodeImpl = ItemResolver.findNode(session, nodeRef, relPath);
        return nodeImpl.getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getNodes()
     */
    public NodeIterator getNodes() throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef);        
        NodeIterator iterator = new ChildAssocNodeIteratorImpl(session, childAssocs);
        return iterator;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getNodes(java.lang.String)
     */
    public NodeIterator getNodes(String namePattern) throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        JCRPatternMatch match = new JCRPatternMatch(namePattern, session.getNamespaceResolver());
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef, match);        
        NodeIterator iterator = new ChildAssocNodeIteratorImpl(session, childAssocs);
        return iterator;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getProperty(java.lang.String)
     */
    public Property getProperty(String relPath) throws PathNotFoundException, RepositoryException
    {
        JCRPath path = new JCRPath(relPath);
        if (path.size() == 1)
        {
            QName propertyName = QName.createQName(relPath, session.getNamespaceResolver());
            return PropertyResolver.createProperty(this, propertyName).getProxy();
        }

        ItemImpl itemImpl = ItemResolver.findItem(session, nodeRef, relPath);
        if (itemImpl == null || !(itemImpl instanceof PropertyImpl))
        {
            throw new PathNotFoundException("Property path " + relPath + " not found from node " + nodeRef);
        }
        return ((PropertyImpl)itemImpl).getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getProperties()
     */
    public PropertyIterator getProperties() throws RepositoryException
    {
        List<PropertyImpl> properties = PropertyResolver.createProperties(this, null);
        PropertyIterator iterator = new PropertyListIterator(properties);
        return iterator;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getProperties(java.lang.String)
     */
    public PropertyIterator getProperties(String namePattern) throws RepositoryException
    {
        JCRPatternMatch match = new JCRPatternMatch(namePattern, session.getNamespaceResolver());
        List<PropertyImpl> properties = PropertyResolver.createProperties(this, match);
        PropertyIterator iterator = new PropertyListIterator(properties);
        return iterator;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getPrimaryItem()
     */
    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException
    {
        // Note: Alfresco does not support the notion of primary item
        throw new ItemNotFoundException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getUUID()
     */
    public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        return nodeRef.getId();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getIndex()
     */
    public int getIndex() throws RepositoryException
    {
        int index = 1;
        String name = getName();
        if (name != null)
        {
            // TODO: Look at more efficient approach
            SearchService searchService = session.getServiceRegistry().getSearchService();
            List<NodeRef> siblings = searchService.selectNodes(nodeRef, "../" + name, null, session.getNamespaceResolver(), false);
            for (NodeRef sibling : siblings)
            {
                if (sibling.equals(nodeRef))
                {
                    break;
                }
                index++;
            }
        }        
        return index;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getReferences()
     */
    public PropertyIterator getReferences() throws RepositoryException
    {
        // Note: Lookup for references not supported for now
        return new PropertyListIterator(new ArrayList<PropertyImpl>());
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#hasNode(java.lang.String)
     */
    public boolean hasNode(String relPath) throws RepositoryException
    {
        return ItemResolver.nodeExists(session, nodeRef, relPath);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#hasProperty(java.lang.String)
     */
    public boolean hasProperty(String relPath) throws RepositoryException
    {
        JCRPath path = new JCRPath(relPath);
        if (path.size() == 1)
        {
            QName propertyName = QName.createQName(relPath, session.getNamespaceResolver());
            return PropertyResolver.hasProperty(this, propertyName);
        }

        return ItemResolver.itemExists(session, nodeRef, relPath);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#hasNodes()
     */
    public boolean hasNodes() throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef);        
        return childAssocs.size() > 0;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#hasProperties()
     */
    public boolean hasProperties() throws RepositoryException
    {
        // Note: nt:base has a mandatory primaryType property for which we don't have security access control
        return true;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getPrimaryNodeType()
     */
    public NodeType getPrimaryNodeType() throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        QName type = nodeService.getType(nodeRef);
        DictionaryService dictionaryService = session.getServiceRegistry().getDictionaryService();
        ClassDefinition classDefinition = dictionaryService.getClass(type);
        return new NodeTypeImpl(session, classDefinition).getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#getMixinNodeTypes()
     */
    public NodeType[] getMixinNodeTypes() throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        DictionaryService dictionaryService = session.getServiceRegistry().getDictionaryService();
        
        // Add aspects defined by node
        Set<QName> aspects = nodeService.getAspects(nodeRef);
        NodeType[] nodeTypes = new NodeType[aspects.size() + 1];
        int i = 0;
        for (QName aspect : aspects)
        {
            ClassDefinition classDefinition = dictionaryService.getClass(aspect);
            nodeTypes[i++] = new NodeTypeImpl(session, classDefinition).getProxy(); 
        }
        
        // Add mix:referenceable aspect (to represent the Alfresco sys:referenceable aspect)
        ClassDefinition classDefinition = dictionaryService.getClass(NodeTypeImpl.MIX_REFERENCEABLE);
        nodeTypes[i] = new NodeTypeImpl(session, classDefinition).getProxy();
        
        return nodeTypes;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Node#isNodeType(java.lang.String)
     */
    public boolean isNodeType(String nodeTypeName) throws RepositoryException
    {
        QName name = QName.createQName(nodeTypeName, session.getNamespaceResolver());
        
        // is it one of standard types
        if (name.equals(NodeTypeImpl.MIX_REFERENCEABLE) || name.equals(NodeTypeImpl.NT_BASE))
        {
            return true;
        }

        // determine via class hierarchy
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        DictionaryService dictionaryService = session.getServiceRegistry().getDictionaryService();

        // first, check the type
        QName type = nodeService.getType(nodeRef);
        if (dictionaryService.isSubClass(name, type))
        {
            return true;
        }
        
        // second, check the aspects
        Set<QName> aspects = nodeService.getAspects(nodeRef);
        for (QName aspect : aspects)
        {
            if (dictionaryService.isSubClass(name, aspect))
            {
                return true;
            }
        }

        // no, its definitely not of the specified type
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

    /* (non-Javadoc)
     * @see javax.jcr.Item#getName()
     */
    public String getName() throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        ChildAssociationRef parentAssoc = nodeService.getPrimaryParent(nodeRef);
        QName childName = parentAssoc.getQName();
        return (childName == null) ? "" : childName.toPrefixString(session.getNamespaceResolver());
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#isNode()
     */
    public boolean isNode()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getParent()
     */
    public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        ChildAssociationRef parentAssoc = nodeService.getPrimaryParent(nodeRef);
        if (parentAssoc == null || parentAssoc.getParentRef() == null)
        {
            // TODO: Distinguish between ItemNotFound and AccessDenied
            throw new ItemNotFoundException("Parent of node " + nodeRef + " does not exist.");
        }
        NodeImpl nodeImpl = new NodeImpl(session, parentAssoc.getParentRef());
        return nodeImpl.getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getPath()
     */
    public String getPath() throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        SearchService searchService = session.getServiceRegistry().getSearchService();
        Path path = nodeService.getPath(nodeRef);
        
        // Add indexes for same name siblings
        // TODO: Look at more efficient approach
        for (int i = path.size() - 1; i >= 0; i--)
        {
            Path.Element pathElement = path.get(i);
            if (i > 0 && pathElement instanceof Path.ChildAssocElement)
            {
                int index = 1;
                String searchPath = path.subPath(i).toPrefixString(session.getNamespaceResolver());
                List<NodeRef> siblings = searchService.selectNodes(nodeRef, searchPath, null, session.getNamespaceResolver(), false);
                if (siblings.size() > 1)
                {
                    ChildAssociationRef childAssoc = ((Path.ChildAssocElement)pathElement).getRef();
                    NodeRef childRef = childAssoc.getChildRef();
                    for (NodeRef sibling : siblings)
                    {
                        if (sibling.equals(childRef))
                        {
                            childAssoc.setNthSibling(index);
                            break;
                        }
                        index++;
                    }
                }
            }
        }
        
        return path.toPrefixString(session.getNamespaceResolver());
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getDepth()
     */
    public int getDepth() throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        Path path = nodeService.getPath(nodeRef);
        // Note: Root is at depth 0
        return path.size() -1;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getAncestor(int)
     */
    public Item getAncestor(int depth) throws ItemNotFoundException, AccessDeniedException, RepositoryException
    {
        // Retrieve primary parent path for node
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        Path path = nodeService.getPath(nodeRef);
        if (depth < 0 || depth > (path.size() - 1))
        {
            throw new ItemNotFoundException("Ancestor at depth " + depth + " not found for node " + nodeRef);
        }

        // Extract path element at requested depth
        Element element = path.get(depth);
        if (!(element instanceof Path.ChildAssocElement))
        {
            throw new RepositoryException("Path element at depth " + depth + " is not a node");
        }
        Path.ChildAssocElement childAssocElement = (Path.ChildAssocElement)element;
        
        // Create node
        NodeRef ancestorNodeRef = childAssocElement.getRef().getChildRef();
        NodeImpl nodeImpl = new NodeImpl(session, ancestorNodeRef);
        return nodeImpl.getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#isSame(javax.jcr.Item)
     */
    public boolean isSame(Item otherItem) throws RepositoryException
    {
        return getProxy().equals(otherItem);
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.Item#accept(javax.jcr.ItemVisitor)
     */
    public void accept(ItemVisitor visitor) throws RepositoryException
    {
        visitor.visit(getProxy());
    }
    
    /**
     * Gets the Alfresco Node Reference
     * 
     * @return  the node reference
     */
    /*package*/ NodeRef getNodeRef()
    {
        return nodeRef;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof NodeImpl))
        {
            return false;
        }
        NodeImpl other = (NodeImpl)obj;
        return this.nodeRef.equals(other.nodeRef);
    }

    @Override
    public int hashCode()
    {
        return nodeRef.hashCode();
    }
    
}
