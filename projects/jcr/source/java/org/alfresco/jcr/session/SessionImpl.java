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
package org.alfresco.jcr.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.alfresco.jcr.item.ItemImpl;
import org.alfresco.jcr.item.ItemResolver;
import org.alfresco.jcr.item.NodeImpl;
import org.alfresco.jcr.proxy.JCRProxyFactory;
import org.alfresco.jcr.repository.RepositoryImpl;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/**
 * Alfresco Implementation of a JCR Session
 * 
 * @author David Caruana
 */
public class SessionImpl implements Session, SessionContext
{
    /** Parent Repository */ 
    private RepositoryImpl repository;
    
    /** Authenticated ticket */
    private String ticket;
    
    /** Session Attributes */
    private Map<String, Object> attributes;
    
    /** Workspace Store Reference */
    private StoreRef workspaceStore;
    
    /** Dynamic Namespace for this session */
    NamespacePrefixResolver namespaceResolver;
    
    private Session proxy = null;
    
    
    /**
     * Construct
     * 
     * @param repository  parent repository
     * @param ticket  authenticated ticket
     * @param workspaceName  workspace name
     * @param attributes  session attributes
     * @throws NoSuchWorkspaceException
     */
    public SessionImpl(RepositoryImpl repository, String ticket, String workspaceName, Map<String, Object> attributes)
        throws NoSuchWorkspaceException
    {
        this.repository = repository;
        this.ticket = ticket;
        this.attributes = (attributes == null) ? new HashMap<String, Object>() : attributes;
        this.namespaceResolver = new DynamicNamespacePrefixResolver(repository.getServiceRegistry().getNamespaceService());
        this.workspaceStore = getWorkspaceStore(workspaceName);
    }

    /**
     * Create proxied Session
     * 
     * @return  JCR Session
     */    
    public Session getProxy()
    {
        if (proxy == null)
        {
            proxy = (Session)JCRProxyFactory.create(this, Session.class, this); 
        }
        return proxy;
    }

    //
    // Session Context
    //
    
    /* (non-Javadoc)
     * @see org.alfresco.jcr.session.SessionContext#getServiceRegistry()
     */
    public ServiceRegistry getServiceRegistry()
    {
        return repository.getServiceRegistry();
    }

    /* (non-Javadoc)
     * @see org.alfresco.jcr.session.SessionContext#getAuthenticationService()
     */
    public AuthenticationService getAuthenticationService()
    {
        return repository.getAuthenticationService();
    }

    /* (non-Javadoc)
     * @see org.alfresco.jcr.session.SessionContext#getTicket()
     */
    public String getTicket()
    {
        return ticket;
    }

    /* (non-Javadoc)
     * @see org.alfresco.jcr.session.SessionContext#getNamespaceResolver()
     */
    public NamespacePrefixResolver getNamespaceResolver()
    {
        return namespaceResolver;
    }

    //
    // JCR Session
    //
    
    /* (non-Javadoc)
     * @see javax.jcr.Session#getRepository()
     */
    public Repository getRepository()
    {
        return repository;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getUserID()
     */
    public String getUserID()
    {
        return getAuthenticationService().getCurrentUserName();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getAttributeNames()
     */
    public String[] getAttributeNames()
    {
        String[] names = (String[]) attributes.keySet().toArray(new String[attributes.keySet().size()]);
        return names;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getWorkspace()
     */
    public Workspace getWorkspace()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#impersonate(javax.jcr.Credentials)
     */
    public Session impersonate(Credentials credentials) throws LoginException, RepositoryException
    {
        // TODO: Implement when impersonation permission added to Alfresco Repository
        throw new LoginException("Insufficient permission to impersonate");
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getRootNode()
     */
    public Node getRootNode() throws RepositoryException
    {
        NodeRef nodeRef = getServiceRegistry().getNodeService().getRootNode(workspaceStore);
        NodeImpl nodeImpl = new NodeImpl(this, nodeRef);
        return nodeImpl.getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getNodeByUUID(java.lang.String)
     */
    public Node getNodeByUUID(String uuid) throws ItemNotFoundException, RepositoryException
    {
        NodeRef nodeRef = new NodeRef(workspaceStore, uuid);
        boolean exists = getServiceRegistry().getNodeService().exists(nodeRef);
        if (exists == false)
        {
            throw new ItemNotFoundException();
        }
        NodeImpl nodeImpl = new NodeImpl(this, nodeRef);
        return nodeImpl.getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getItem(java.lang.String)
     */
    public Item getItem(String absPath) throws PathNotFoundException, RepositoryException
    {
        NodeRef nodeRef = getServiceRegistry().getNodeService().getRootNode(workspaceStore);
        ItemImpl itemImpl = ItemResolver.findItem(this, nodeRef, absPath);
        return itemImpl.getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#itemExists(java.lang.String)
     */
    public boolean itemExists(String absPath) throws RepositoryException
    {
        NodeRef nodeRef = getServiceRegistry().getNodeService().getRootNode(workspaceStore);
        return ItemResolver.itemExists(this, nodeRef, absPath);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#move(java.lang.String, java.lang.String)
     */
    public void move(String srcAbsPath, String destAbsPath) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#save()
     */
    public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#refresh(boolean)
     */
    public void refresh(boolean keepChanges) throws RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#hasPendingChanges()
     */
    public boolean hasPendingChanges() throws RepositoryException
    {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getValueFactory()
     */
    public ValueFactory getValueFactory() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#checkPermission(java.lang.String, java.lang.String)
     */
    public void checkPermission(String absPath, String actions) throws AccessControlException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getImportContentHandler(java.lang.String, int)
     */
    public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior) throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#importXML(java.lang.String, java.io.InputStream, int)
     */
    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior) throws IOException, PathNotFoundException, ItemExistsException, ConstraintViolationException, VersionException, InvalidSerializedDataException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#exportSystemView(java.lang.String, org.xml.sax.ContentHandler, boolean, boolean)
     */
    public void exportSystemView(String absPath, ContentHandler contentHandler, boolean skipBinary, boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#exportSystemView(java.lang.String, java.io.OutputStream, boolean, boolean)
     */
    public void exportSystemView(String absPath, OutputStream out, boolean skipBinary, boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#exportDocumentView(java.lang.String, org.xml.sax.ContentHandler, boolean, boolean)
     */
    public void exportDocumentView(String absPath, ContentHandler contentHandler, boolean skipBinary, boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#exportDocumentView(java.lang.String, java.io.OutputStream, boolean, boolean)
     */
    public void exportDocumentView(String absPath, OutputStream out, boolean skipBinary, boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#setNamespacePrefix(java.lang.String, java.lang.String)
     */
    public void setNamespacePrefix(String prefix, String uri) throws NamespaceException, RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getNamespacePrefixes()
     */
    public String[] getNamespacePrefixes() throws RepositoryException
    {
        Collection<String> prefixes = namespaceResolver.getPrefixes();
        return prefixes.toArray(new String[prefixes.size()]);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI(String prefix) throws NamespaceException, RepositoryException
    {
        String uri = namespaceResolver.getNamespaceURI(prefix);
        if (uri == null)
        {
            throw new NamespaceException("Prefix " + prefix + " is unknown.");
        }
        return uri;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getNamespacePrefix(java.lang.String)
     */
    public String getNamespacePrefix(String uri) throws NamespaceException, RepositoryException
    {
        Collection<String> prefixes = namespaceResolver.getPrefixes(uri);
        if (prefixes.size() == 0)
        {
            throw new NamespaceException("URI " + uri + " is unknown.");
        }
        // Return first prefix registered for uri
        return prefixes.iterator().next();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#logout()
     */
    public void logout()
    {
        getAuthenticationService().invalidateTicket(getTicket());
        ticket = null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#isLive()
     */
    public boolean isLive()
    {
        return ticket != null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#addLockToken(java.lang.String)
     */
    public void addLockToken(String lt)
    {
        // TODO: NOOP for level 1?
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#getLockTokens()
     */
    public String[] getLockTokens()
    {
        return new String[0];
    }

    /* (non-Javadoc)
     * @see javax.jcr.Session#removeLockToken(java.lang.String)
     */
    public void removeLockToken(String lt)
    {
        // TODO: NOOP for level 1?
    }


    /**
     * Gets the workspace store reference for the given workspace name
     * 
     * @param workspaceName  the workspace name
     * @return  the store reference
     * @throws NoSuchWorkspaceException
     */
    private StoreRef getWorkspaceStore(String workspaceName)
        throws NoSuchWorkspaceException
    {
        if (workspaceName == null)
        {
            // TODO: Provide a default "Null Workspace" as per JCR specification
            throw new NoSuchWorkspaceException("A default workspace could not be established.");
        }
        
        NodeService nodeService = getServiceRegistry().getNodeService();
        List<StoreRef> stores = nodeService.getStores();
        StoreRef workspace = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, workspaceName);
        if (stores.contains(workspace) == false)
        {
            throw new NoSuchWorkspaceException("Workspace " + workspaceName + " does not exist.");
        }
        return workspace;
    }

}