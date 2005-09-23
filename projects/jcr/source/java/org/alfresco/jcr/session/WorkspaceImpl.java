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
import java.util.ArrayList;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.ItemExistsException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.QueryManager;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;

import org.alfresco.jcr.query.QueryManagerImpl;
import org.alfresco.jcr.util.JCRProxyFactory;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.xml.sax.ContentHandler;

/**
 * Alfresco implementation of a JCR Workspace
 * 
 * @author David Caruana
 */
public class WorkspaceImpl implements Workspace
{
    
    private SessionImpl session;
    private Workspace proxy = null;
    private QueryManagerImpl queryManager = null;
    
    /**
     * Construct
     * 
     * @param session  the session
     */
    public WorkspaceImpl(SessionImpl session)
    {
        this.session = session;   
    }
    
    /**
     * Get proxied JCR Workspace
     * 
     * @return  proxied JCR Workspace
     */
    public Workspace getProxy()
    {
        if (proxy == null)
        {
            proxy = (Workspace)JCRProxyFactory.create(this, Workspace.class, session);
        }
        return proxy;
    }
    
    
    /* (non-Javadoc)
     * @see javax.jcr.Workspace#getSession()
     */
    public Session getSession()
    {
        return session.getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#getName()
     */
    public String getName()
    {
        return session.getWorkspaceStore().getIdentifier();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#copy(java.lang.String, java.lang.String)
     */
    public void copy(String srcAbsPath, String destAbsPath) throws ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#copy(java.lang.String, java.lang.String, java.lang.String)
     */
    public void copy(String srcWorkspace, String srcAbsPath, String destAbsPath) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#clone(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public void clone(String srcWorkspace, String srcAbsPath, String destAbsPath, boolean removeExisting) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#move(java.lang.String, java.lang.String)
     */
    public void move(String srcAbsPath, String destAbsPath) throws ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#restore(javax.jcr.version.Version[], boolean)
     */
    public void restore(Version[] versions, boolean removeExisting) throws ItemExistsException, UnsupportedRepositoryOperationException, VersionException, LockException, InvalidItemStateException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#getQueryManager()
     */
    public QueryManager getQueryManager() throws RepositoryException
    {
        if (queryManager == null)
        {
            queryManager = new QueryManagerImpl(session);
        }
        return queryManager;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#getNamespaceRegistry()
     */
    public NamespaceRegistry getNamespaceRegistry() throws RepositoryException
    {
        return session.getRepositoryImpl().getNamespaceRegistry();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#getNodeTypeManager()
     */
    public NodeTypeManager getNodeTypeManager() throws RepositoryException
    {
        return session.getTypeManager();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#getObservationManager()
     */
    public ObservationManager getObservationManager() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#getAccessibleWorkspaceNames()
     */
    public String[] getAccessibleWorkspaceNames() throws RepositoryException
    {
        NodeService nodeService = session.getRepositoryImpl().getServiceRegistry().getNodeService();
        List<StoreRef> storeRefs = nodeService.getStores();
        List<String> workspaceStores = new ArrayList<String>();
        for (StoreRef storeRef : storeRefs)
        {
            if (storeRef.getProtocol().equals(StoreRef.PROTOCOL_WORKSPACE))
            {
                workspaceStores.add(storeRef.getIdentifier());
            }
        }
        return workspaceStores.toArray(new String[workspaceStores.size()]);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#getImportContentHandler(java.lang.String, int)
     */
    public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior) throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, AccessDeniedException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Workspace#importXML(java.lang.String, java.io.InputStream, int)
     */
    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior) throws IOException, PathNotFoundException, ItemExistsException, ConstraintViolationException, InvalidSerializedDataException, LockException, AccessDeniedException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

}
