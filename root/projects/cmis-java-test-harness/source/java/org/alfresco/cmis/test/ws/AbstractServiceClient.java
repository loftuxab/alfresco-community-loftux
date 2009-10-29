/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.cmis.test.ws;

import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.alfresco.repo.cmis.ws.CmisContentStreamType;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisPropertyId;
import org.alfresco.repo.cmis.ws.CmisPropertyString;
import org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType;
import org.alfresco.repo.cmis.ws.CmisRepositoryEntryType;
import org.alfresco.repo.cmis.ws.CmisRepositoryInfoType;
import org.alfresco.repo.cmis.ws.CmisTypeContainer;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypeDocumentDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypeRelationshipDefinitionType;
import org.alfresco.repo.cmis.ws.CreateDocument;
import org.alfresco.repo.cmis.ws.CreateDocumentResponse;
import org.alfresco.repo.cmis.ws.CreateFolder;
import org.alfresco.repo.cmis.ws.CreateFolderResponse;
import org.alfresco.repo.cmis.ws.CreateRelationship;
import org.alfresco.repo.cmis.ws.CreateRelationshipResponse;
import org.alfresco.repo.cmis.ws.DeleteTree;
import org.alfresco.repo.cmis.ws.DeleteTreeResponse;
import org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds;
import org.alfresco.repo.cmis.ws.EnumContentStreamAllowed;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumUnfileObject;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetChildren;
import org.alfresco.repo.cmis.ws.GetChildrenResponse;
import org.alfresco.repo.cmis.ws.GetProperties;
import org.alfresco.repo.cmis.ws.GetRepositories;
import org.alfresco.repo.cmis.ws.GetRepositoryInfo;
import org.alfresco.repo.cmis.ws.GetRepositoryInfoResponse;
import org.alfresco.repo.cmis.ws.GetTypeDefinition;
import org.alfresco.repo.cmis.ws.GetTypeDefinitionResponse;
import org.alfresco.repo.cmis.ws.GetTypeDescendants;
import org.alfresco.repo.cmis.ws.ObjectServicePortBindingStub;
import org.alfresco.repo.cmis.ws.RepositoryServicePortBindingStub;
import org.alfresco.repo.webservice.authentication.AuthenticationFault;
import org.alfresco.repo.webservice.authentication.AuthenticationResult;
import org.alfresco.repo.webservice.authentication.AuthenticationServiceLocator;
import org.alfresco.repo.webservice.authentication.AuthenticationServiceSoapBindingStub;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.ws.axis.security.WSDoAllSender;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Base class for all services clients, provides authentication to clients
 * 
 * @author Mike Shavnev
 */
public abstract class AbstractServiceClient extends AbstractDependencyInjectionSpringContextTests implements CallbackHandler
{
    public static final int TIMEOUT = 60000;

    protected static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    protected static final String TEST_CONTENT = "Test Document content entry. This Document was created during test execution...";
    protected static final String TEST_FOLDER_NAME = "CMIS Tests";
    protected static final String TEST_FILE_NAME = "test.txt";
    protected static final String ENCODING = "UTF-8";

    protected static final String PROP_NAME = "cmis:Name";
    protected static final String PROP_OBJECT_ID = "cmis:ObjectId";
    protected static final String PROP_OBJECT_TYPE_ID = "cmis:ObjectTypeId";
    protected static final String PROP_CREATION_DATE = "cmis:CreationDate";
    protected static final String PROP_SOURCE_OBJECT_ID = "cmis:SourceObjectId";
    protected static final String PROP_TARGET_OBJECT_ID = "cmis:TargetObjectId";
    protected static final String PROP_BASE_TYPE = "cmis:BaseTypeId";

    private static final int MAXIMUM_FOLDERS_AMOUNT = 4;
    private static final String TEST_FOLDER_NAME_PATTERN = "Test Folder(%d.%d)";
    private static final String TEST_DOCUMENT_NAME_PATTERN = "Test Document(%d.%d).txt";

    private AbstractService abstractService;

    private String proxyUrl;
    private String serverUrl;

    private String username;
    private String password;

    private String ticket;
    private SimpleProvider engineConfiguration;
    private AuthenticationServiceSoapBindingStub authenticationService;

    private CmisServicesFactory servicesFactory;

    private static String repositoryId;
    private static String rootFolderId;
    private static String documentTypeId;
    private static String folderTypeId;
    private static String relationshipTypeId;
    private static CmisTypeDefinitionType relationshipSourceType;
    private static CmisTypeDefinitionType relationshipTargetType;
    private static CmisRepositoryCapabilitiesType capabilities;
    private static boolean contentStreamAllowed = false;
    private static boolean versioningAllowed = false;

    public AbstractServiceClient()
    {
        super();
    }

    public AbstractServiceClient(AbstractService abstractService)
    {
        this.abstractService = abstractService;
    }

    public AbstractService getService()
    {
        return abstractService;
    }

    public void setService(AbstractService abstractService)
    {
        this.abstractService = abstractService;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    public String getProxyUrl()
    {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl)
    {
        this.proxyUrl = proxyUrl;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public CmisServicesFactory getServicesFactory()
    {
        return servicesFactory;
    }

    public void setServicesFactory(CmisServicesFactory servicesFactory)
    {
        this.servicesFactory = servicesFactory;
    }

    /**
     * Gets Axis engine configuration with WS Security configured
     * 
     * @return EngineConfiguration
     */
    public EngineConfiguration getEngineConfiguration()
    {
        if (engineConfiguration == null)
        {
            WSDoAllSender wsDoAllSender = new WSDoAllSender()
            {
                private static final long serialVersionUID = 3313512765705136489L;

                @Override
                public WSPasswordCallback getPassword(String username, int doAction, String clsProp, String refProp, RequestData reqData) throws WSSecurityException
                {
                    WSPasswordCallback passwordCallback = null;
                    try
                    {
                        passwordCallback = super.getPassword(username, doAction, clsProp, refProp, reqData);
                    }
                    catch (WSSecurityException e)
                    {
                        passwordCallback = new WSPasswordCallback(username, WSPasswordCallback.USERNAME_TOKEN);
                        try
                        {
                            CallbackHandler callbackHandler = (CallbackHandler) getOption(refProp);
                            callbackHandler.handle(new Callback[] { passwordCallback });
                        }
                        catch (Exception e2)
                        {
                            throw new WSSecurityException("WSHandler: password callback failed", e);
                        }
                    }
                    return passwordCallback;
                }
            };

            wsDoAllSender.setOption(WSHandlerConstants.ACTION, WSConstants.USERNAME_TOKEN_LN + " " + WSConstants.TIMESTAMP_TOKEN_LN);
            wsDoAllSender.setOption(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
            wsDoAllSender.setOption(WSHandlerConstants.PW_CALLBACK_REF, this);
            wsDoAllSender.setOption(WSHandlerConstants.USER, username);

            engineConfiguration = new SimpleProvider();
            engineConfiguration.deployTransport(new QName("", "http"), new HTTPSender());
            engineConfiguration.setGlobalRequest(wsDoAllSender);
        }
        return engineConfiguration;
    }

    public void setAuthenticationService(AbstractService authenticationService)
    {
        AuthenticationServiceLocator locator = new AuthenticationServiceLocator();
        locator.setAuthenticationServiceEndpointAddress(serverUrl + authenticationService.getPath());
        try
        {
            this.authenticationService = (AuthenticationServiceSoapBindingStub) locator.getAuthenticationService();
            this.authenticationService.setTimeout(TIMEOUT);
        }
        catch (ServiceException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts session for original clients
     * 
     * @throws AuthenticationFault
     * @throws RemoteException
     */
    public void startSession() throws AuthenticationFault, RemoteException
    {
        AuthenticationResult result = authenticationService.startSession(username, password);
        ticket = result.getTicket();
    }

    /**
     * Ends session for original clients
     * 
     * @throws AuthenticationFault
     * @throws RemoteException
     */
    public void endSession() throws AuthenticationFault, RemoteException
    {
        authenticationService.endSession(ticket);
        ticket = null;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
    {
        for (int i = 0; i < callbacks.length; i++)
        {
            if (callbacks[i] instanceof WSPasswordCallback)
            {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                if (ticket != null)
                {
                    pc.setPassword(ticket);
                }
                else
                {
                    pc.setPassword(password);
                }
            }
            else
            {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }

    @Override
    protected String[] getConfigLocations()
    {
        setAutowireMode(AUTOWIRE_BY_NAME);
        setDependencyCheck(false);

        return new String[] { "classpath:cmis-context.xml" };
    }

    public abstract void initialize() throws Exception;

    public abstract void invoke() throws Exception;

    public abstract void release() throws Exception;

    protected String createAndAssertDocument(String documentName, String documentTypeId, String folderId, String content, EnumVersioningState initialVersion) throws Exception
    {
        ObjectServicePortBindingStub objectService = getServicesFactory().getObjectService();

        if (documentTypeId == null)
        {
            documentTypeId = getAndAssertDocumentTypeId();
        }

        CmisPropertiesType cmisPropertiesType = new CmisPropertiesType();
        CmisPropertyString cmisPropertyString = new CmisPropertyString();
        cmisPropertyString.setPdid(PROP_NAME);
        cmisPropertyString.setValue(new String[] { documentName });
        CmisPropertyId idProperty = new CmisPropertyId();
        idProperty.setPdid(PROP_OBJECT_TYPE_ID);
        idProperty.setValue(new String[] {documentTypeId});

        cmisPropertiesType.setPropertyString(new CmisPropertyString[] { cmisPropertyString });
        cmisPropertiesType.setPropertyId(new CmisPropertyId[] { idProperty });

        CmisContentStreamType contentStream = null;
        if (isContentStreamAllowed())
        {
            contentStream = new CmisContentStreamType(BigInteger.valueOf(content == null ? TEST_CONTENT.length() : content.length()), MIMETYPE_TEXT_PLAIN, documentName, null,
                    content == null ? TEST_CONTENT.getBytes(ENCODING) : content.getBytes(ENCODING), null);
        }

        CreateDocumentResponse createDocument = objectService.createDocument(new CreateDocument(getAndAssertRepositoryId(), cmisPropertiesType, folderId,
                contentStream, initialVersion, null, null, null));

        assertNotNull(createDocument);
        assertNotNull(createDocument.getObjectId());

        return createDocument.getObjectId();
    }

    protected String createAndAssertFolder(String folderName, String folderTypeId, String folderId) throws Exception
    {
        if (folderTypeId == null)
        {
            folderTypeId = getAndAssertFolderTypeId();
        }

        ObjectServicePortBindingStub objectService = getServicesFactory().getObjectService();

        CmisPropertiesType cmisPropertiesType = new CmisPropertiesType();
        CmisPropertyString cmisPropertyString = new CmisPropertyString();
        cmisPropertyString.setPdid(PROP_NAME);
        cmisPropertyString.setValue(new String[] { folderName });
        CmisPropertyId idProperty = new CmisPropertyId();
        idProperty.setPdid(PROP_OBJECT_TYPE_ID);
        idProperty.setValue(new String[] {folderTypeId});

        cmisPropertiesType.setPropertyString(new CmisPropertyString[] { cmisPropertyString });
        cmisPropertiesType.setPropertyId(new CmisPropertyId[] { idProperty });

        CreateFolderResponse createFolder = objectService.createFolder(new CreateFolder(getAndAssertRepositoryId(), cmisPropertiesType, folderId, null, null, null));

        assertNotNull(createFolder);
        assertNotNull(createFolder.getObjectId());

        return createFolder.getObjectId();
    }

    protected List<String> createAndAssertObjectsTree(String rootFolderId, EnumVersioningState documentsInitialVersion, EnumTypesOfFileableObjects objectsToReturn,
            int numberOfFinishLayer, int depth, int minLayerSize, int maxLayerSize) throws Exception
    {
        List<String> result = new LinkedList<String>();

        if ((depth <= 0) || (maxLayerSize < 1) || (minLayerSize > maxLayerSize))
        {
            return result;
        }

        documentsInitialVersion = (documentsInitialVersion == null) ? (EnumVersioningState.major) : (documentsInitialVersion);

        LinkedList<String> layerFolders = new LinkedList<String>();
        layerFolders.add(rootFolderId);

        SecureRandom decideIncliner = new SecureRandom();

        for (int layerNumber = 0; layerNumber < depth; layerNumber++)
        {
            int requiredFolderContainer = ((layerNumber < (depth - 1)) && (layerFolders.size() > 0)) ? (Math.abs(decideIncliner.nextInt()) % layerFolders.size()) : (-1);
            int currentFolderIndex = 0;

            List<String> currentLayerFolders = new LinkedList<String>();

            while (!layerFolders.isEmpty())
            {
                String folderId = layerFolders.getFirst();
                layerFolders.removeFirst();

                int seed = (maxLayerSize > MAXIMUM_FOLDERS_AMOUNT) ? (MAXIMUM_FOLDERS_AMOUNT) : (maxLayerSize);
                int foldersAmount = Math.abs(decideIncliner.nextInt()) % seed;
                foldersAmount = ((foldersAmount == 0) && (currentFolderIndex++ == requiredFolderContainer)) ? (1) : (foldersAmount);

                seed = maxLayerSize - foldersAmount;
                int documentsAmount = (seed == 0) ? (0) : (Math.abs(decideIncliner.nextInt()) % seed);

                if ((documentsAmount + foldersAmount) < minLayerSize)
                {
                    foldersAmount = (foldersAmount == 0) ? (1) : (foldersAmount);
                    seed = minLayerSize - foldersAmount - documentsAmount;
                    documentsAmount += (seed > 0) ? (seed) : (0);
                }

                for (int i = 0; i < foldersAmount; i++)
                {
                    String newFolderId = createAndAssertFolder(String.format(TEST_FOLDER_NAME_PATTERN, layerNumber, i), getAndAssertFolderTypeId(), folderId);

                    if (newFolderId != null)
                    {
                        if ((EnumTypesOfFileableObjects.FOLDERS == objectsToReturn) || (EnumTypesOfFileableObjects.BOTH == objectsToReturn) &&
                               ((numberOfFinishLayer < 0) ^ (layerNumber < numberOfFinishLayer)))
                        {
                            result.add(newFolderId);
                        }

                        currentLayerFolders.add(newFolderId);
                    }
                }

                for (int i = 0; i < documentsAmount; i++)
                {
                    String newDocumentId = createAndAssertDocument(String.format(TEST_DOCUMENT_NAME_PATTERN, layerNumber, i), getAndAssertDocumentTypeId(), folderId, null,
                            documentsInitialVersion);

                    if ((newDocumentId != null)
                            && ((EnumTypesOfFileableObjects.DOCUMENTS == objectsToReturn) || (EnumTypesOfFileableObjects.BOTH == objectsToReturn)) && ((numberOfFinishLayer < 0) || (layerNumber < numberOfFinishLayer)))
                    {
                        result.add(newDocumentId);
                    }
                }
            }

            layerFolders.addAll(currentLayerFolders);
        }

        return result;
    }

    protected List<String> deleteAndAssertTree(String rootFolderId, EnumUnfileObject multifilledObjectsBehaviour, boolean notDeletedMustAppear) throws Exception
    {
        ObjectServicePortBindingStub objectService = getServicesFactory().getObjectService();

        DeleteTreeResponse undeletedObjects = objectService.deleteTree(new DeleteTree(getAndAssertRepositoryId(), rootFolderId, multifilledObjectsBehaviour, notDeletedMustAppear));

        assertNotNull(undeletedObjects);

        if (notDeletedMustAppear)
        {
            assertNotNull(undeletedObjects.getFailedToDelete());
            String[] undeletedObjectsIds = undeletedObjects.getFailedToDelete();
            assertTrue(undeletedObjectsIds.length > 0);

            List<String> result = new LinkedList<String>();

            for (String objectId : undeletedObjectsIds)
            {
                assertNotNull(objectId);

                if (objectId != null)
                {
                    result.add(objectId);
                }
            }

            return result;
        }

        if ((undeletedObjects.getFailedToDelete() != null) && (undeletedObjects.getFailedToDelete() != null))
        {
            assertFalse(undeletedObjects.getFailedToDelete().length > 0);
        }

        return null;
    }

    protected String getStringProperty(String documentId, String property) throws Exception
    {
        return getStringProperty(getServicesFactory().getObjectService().getProperties(
                new GetProperties(getAndAssertRepositoryId(), documentId, "*", false, EnumIncludeRelationships.none, false)).getObject().getProperties(),
                property);
    }

    protected String getStringProperty(CmisPropertiesType cmisProperties, String property) throws Exception
    {
        for (int i = 0; cmisProperties != null && i < cmisProperties.getPropertyString().length; i++)
        {
            if ((null != property) && property.equals(cmisProperties.getPropertyString()[i].getPdid()))
            {
                return cmisProperties.getPropertyString()[i].getValue()[0];
            }
        }
        return null;
    }

    protected Boolean getBooleanProperty(String documentId, String property) throws Exception
    {
        return getBooleanProperty(getServicesFactory().getObjectService().getProperties(
                new GetProperties(getAndAssertRepositoryId(), documentId, "*", false, EnumIncludeRelationships.none, false)).getObject().getProperties(),
                property);
    }

    protected Boolean getBooleanProperty(CmisPropertiesType cmisProperties, String property) throws Exception
    {
        for (int i = 0; cmisProperties != null && i < cmisProperties.getPropertyBoolean().length; i++)
        {
            if ((null != property) && property.equals(cmisProperties.getPropertyBoolean()[i].getPdid()))
            {
                return cmisProperties.getPropertyBoolean()[i].getValue()[0];
            }
        }
        return null;
    }

    protected String getIdProperty(String documentId, String property) throws Exception
    {
        return getIdProperty(getServicesFactory().getObjectService().getProperties(
                new GetProperties(getAndAssertRepositoryId(), documentId, "*", false, EnumIncludeRelationships.none, false)).getObject().getProperties(),
                property);
    }

    protected String getIdProperty(CmisPropertiesType cmisProperties, String property) throws Exception
    {
        for (int i = 0; cmisProperties != null && i < cmisProperties.getPropertyId().length; i++)
        {
            if ((null != property) && property.equals(cmisProperties.getPropertyId()[i].getPdid()))
            {
                return cmisProperties.getPropertyId()[i].getValue()[0];
            }
        }
        return null;
    }

    protected boolean isDocumentInFolder(String documentId, String folderId) throws Exception
    {
        boolean found = false;
        GetChildrenResponse childrenResponse = getServicesFactory().getNavigationService().getChildren(
                new GetChildren(getAndAssertRepositoryId(), folderId, "*", false, EnumIncludeRelationships.none, false, false, null, null, ""));

        for (int i = 0; childrenResponse.getObject() != null && !found && i < childrenResponse.getObject().length; i++)
        {
            CmisObjectType cmisObjectType = childrenResponse.getObject()[i];
            for (int j = 0; !found && j < cmisObjectType.getProperties().getPropertyId().length; j++)
            {
                found = PROP_OBJECT_ID.equals(cmisObjectType.getProperties().getPropertyId()[j].getPdid())
                        && documentId.equals(cmisObjectType.getProperties().getPropertyId()[j].getValue(0));
            }
        }
        return found;
    }

    protected String createAndAssertRelationship(String sourceId, String targetId) throws Exception
    {
        if (sourceId == null)
        {
            CmisTypeDefinitionType sourceType = getAndAssertRelationshipSourceType();

            if (sourceType != null && EnumBaseObjectTypeIds.value2.equals(sourceType.getBaseTypeId()))
            {
                sourceId = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, sourceType.getId(), getAndAssertRootFolderId());
                assertNotNull("Source object was not created", sourceId);
            }
            else
            {
                sourceId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, sourceType == null ? getAndAssertDocumentTypeId() : sourceType.getId(),
                        getAndAssertRootFolderId(), null, EnumVersioningState.major);
                assertNotNull("Source object was not created", sourceId);
            }

        }
        if (targetId == null)
        {
            CmisTypeDefinitionType targetType = getAndAssertRelationshipTargetType();

            if (targetType != null && EnumBaseObjectTypeIds.value2.equals(targetType.getBaseTypeId()))
            {
                targetId = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, targetType.getId(), getAndAssertRootFolderId());
                assertNotNull("Target object was not created", targetId);
            }
            else
            {
                targetId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, targetType == null ? getAndAssertDocumentTypeId() : targetType.getId(),
                        getAndAssertRootFolderId(), "Test content" + System.currentTimeMillis(), null);
                assertNotNull("Target object was not created", targetId);
            }
        }
        CmisPropertiesType cmisPropertiesType = new CmisPropertiesType();
        CmisPropertyId idProperty = new CmisPropertyId();
        idProperty.setPdid(PROP_OBJECT_TYPE_ID);
        idProperty.setValue(new String[] {getAndAssertRelationshipTypeId()});
        cmisPropertiesType.setPropertyId(new CmisPropertyId[] { idProperty });

        CreateRelationshipResponse response = getServicesFactory().getObjectService().createRelationship(
                new CreateRelationship(getAndAssertRepositoryId(), cmisPropertiesType, sourceId, targetId, null, null, null));
        assertTrue("Relationship was not created", response != null && response.getObjectId() != null);
        return response.getObjectId();
    }

    private String getTypeId(EnumBaseObjectTypeIds type) throws Exception
    {
        if ((null == type) || (null == type.getValue()) || "".equals(type.getValue()))
        {
            return null;
        }

        String typeId = type.getValue();
        RepositoryServicePortBindingStub repositoryService = getServicesFactory().getRepositoryService();
        CmisTypeContainer[] response = repositoryService.getTypeDescendants(new GetTypeDescendants(getAndAssertRepositoryId(), type.getValue(), BigInteger.valueOf(-1), null));
        assertNotNull("Type Descendants response is empty", response);
        if ((null != response) && (response.length > 0))
        {
            AbstractConditionCalculator calculator = new AbstractConditionCalculator();
            if (EnumBaseObjectTypeIds._value1.equals(typeId))
            {
                calculator = new BestDocumentTypeConditionCalculator();
            }
            else
            {
                if (EnumBaseObjectTypeIds._value2.equals(typeId))
                {
                    calculator = new ValidFolderTypeConditionCalculator();
                }
            }
            typeId = enumerateAndAssertTypesHierarchy(response, calculator, !(calculator instanceof BestDocumentTypeConditionCalculator));
            if (calculator instanceof BestDocumentTypeConditionCalculator)
            {
                GetTypeDefinitionResponse typeDef = repositoryService.getTypeDefinition(new GetTypeDefinition(getAndAssertRepositoryId(), typeId));
                assertNotNull("Type Definition response is empty", typeDef);
                CmisTypeDocumentDefinitionType documentType = (CmisTypeDocumentDefinitionType) typeDef.getType();
                assertNotNull("Invalid Type Definition Response: Type Definition is undefined", documentType);

                contentStreamAllowed = EnumContentStreamAllowed.allowed.equals(documentType.getContentStreamAllowed())
                        || EnumContentStreamAllowed.required.equals(documentType.getContentStreamAllowed());
                versioningAllowed = documentType.isVersionable();
            }
        }

        return typeId;
    }

    private String enumerateAndAssertTypesHierarchy(CmisTypeContainer[] rootContainers, AbstractConditionCalculator calculator, boolean firstIsValid)
    {
        if ((null == rootContainers) || (null == calculator) || (rootContainers.length < 1))
        {
            return null;
        }

        LinkedList<CmisTypeContainer> typesList = new LinkedList<CmisTypeContainer>();
        addContainers(typesList, rootContainers);
        CmisTypeDefinitionType bestType = null;
        for (CmisTypeContainer currentContainer = typesList.getFirst(); !typesList.isEmpty(); typesList.removeFirst(), currentContainer = (!typesList.isEmpty()) ? (typesList
                .getFirst()) : (null))
        {
            if (null == currentContainer)
            {
                continue;
            }

            CmisTypeDefinitionType typeDef = currentContainer.getType();
            assertNotNull("Invalid TypeContainer: parent type is undefined", typeDef);
            if (null != typeDef)
            {
                if (calculator.calculate(bestType, typeDef))
                {
                    if (firstIsValid)
                    {
                        return typeDef.getId();
                    }
                    else
                    {
                        bestType = typeDef;
                    }
                }
            }
            if (currentContainer.getChildren() != null)
            {
                addContainers(typesList, currentContainer.getChildren());
            }
        }

        return (null != bestType) ? (bestType.getId()):(null);
    }

    protected void addContainers(LinkedList<CmisTypeContainer> typesList, CmisTypeContainer[] currentContainers)
    {
        for (CmisTypeContainer container : currentContainers)
        {
            assertNotNull("Invalid Type Descendants response: one of the Type Containers is undefined", container);
            if (null != container)
            {
                typesList.addLast(container);
            }
        }
    }

    protected String getAndAssertRepositoryId()
    {
        if (repositoryId == null)
        {
            CmisRepositoryEntryType[] repositoriesResponse = null;
            try
            {
                repositoriesResponse = getServicesFactory().getRepositoryService().getRepositories(new GetRepositories());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull("No repositories found", repositoriesResponse);
            assertTrue("No repositories found", (repositoriesResponse.length > 0));
            CmisRepositoryEntryType repositoryEntryType = repositoriesResponse[0];
            assertNotNull("Repository entry is NULL", repositoryEntryType);
            repositoryId = repositoryEntryType.getId();
            assertNotNull("Repository Id is NULL", repositoryId);
        }
        return repositoryId;
    }

    protected CmisRepositoryInfoType getAndAssertRepositoryInfo()
    {
        GetRepositoryInfoResponse cmisRepositoryInfo = null;
        try
        {
            cmisRepositoryInfo = getServicesFactory().getRepositoryService().getRepositoryInfo(new GetRepositoryInfo(getAndAssertRepositoryId()));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        assertNotNull("Repository Info is NULL", cmisRepositoryInfo);
        assertNotNull("Repository Info is NULL", cmisRepositoryInfo.getRepositoryInfo());
        return cmisRepositoryInfo.getRepositoryInfo();

    }

    protected CmisPropertiesType fillProperties(String name, String type)
    {
        CmisPropertiesType properties = new CmisPropertiesType();
        if (name != null)
        {
            CmisPropertyString cmisPropertyName = new CmisPropertyString();
            cmisPropertyName.setPdid(PROP_NAME);
            cmisPropertyName.setValue(new String[] { name });
            properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        }
        if (type != null)
        {
            CmisPropertyId idProperty = new CmisPropertyId();
            idProperty.setPdid(PROP_OBJECT_TYPE_ID);
            idProperty.setValue(new String[] {type});
            properties.setPropertyId(new CmisPropertyId[] { idProperty });
        }
        return properties;
    }

    protected String getAndAssertRootFolderId()
    {
        if (rootFolderId == null)
        {
            rootFolderId = getAndAssertRepositoryInfo().getRootFolderId();
            assertNotNull("Root Folder Id is NULL", rootFolderId);
        }
        return rootFolderId;
    }

    protected CmisRepositoryCapabilitiesType getAndAssertCapabilities()
    {
        if (capabilities == null)
        {
            capabilities = getAndAssertRepositoryInfo().getCapabilities();
            assertNotNull("Capabilities are NULL", capabilities);
        }
        return capabilities;
    }

    protected String getAndAssertDocumentTypeId()
    {
        if (documentTypeId == null)
        {
            try
            {
                documentTypeId = getTypeId(EnumBaseObjectTypeIds.value1);
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull("Document type id is NULL", documentTypeId);
        }
        return documentTypeId;
    }

    protected String getAndAssertFolderTypeId()
    {
        if (folderTypeId == null)
        {
            try
            {
                folderTypeId = getTypeId(EnumBaseObjectTypeIds.value2);
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull("Folder type id is NULL", folderTypeId);
        }
        return folderTypeId;
    }

    protected String getAndAssertRelationshipTypeId()
    {
        if (relationshipTypeId == null)
        {
            try
            {
                relationshipTypeId = getTypeId(EnumBaseObjectTypeIds.value3);
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull("Relationship type id is NULL", relationshipTypeId);
        }
        return relationshipTypeId;
    }

    protected CmisTypeDefinitionType getAndAssertRelationshipSourceType()
    {
        if (relationshipSourceType == null)
        {
            CmisTypeRelationshipDefinitionType relationshipDefinitionType = null;
            try
            {
                relationshipDefinitionType = (CmisTypeRelationshipDefinitionType) getServicesFactory().getRepositoryService().getTypeDefinition(
                        new GetTypeDefinition(getAndAssertRepositoryId(), getAndAssertRelationshipTypeId())).getType();
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull("Relationship type definition is NULL", relationshipDefinitionType);
            if (relationshipDefinitionType.getAllowedSourceTypes() != null && relationshipDefinitionType.getAllowedSourceTypes().length > 0)
            {
                CmisTypeDefinitionType sourceType = null;
                try
                {
                    sourceType = getServicesFactory().getRepositoryService().getTypeDefinition(
                            new GetTypeDefinition(getAndAssertRepositoryId(), relationshipDefinitionType.getAllowedSourceTypes()[0])).getType();
                }
                catch (Exception e)
                {
                    fail(e.getMessage());
                }
                assertNotNull("Relationship source type definition is NULL", sourceType);
                relationshipSourceType = sourceType;
            }
        }
        return relationshipSourceType;
    }

    protected CmisTypeDefinitionType getAndAssertRelationshipTargetType()
    {
        if (relationshipTargetType == null)
        {
            CmisTypeRelationshipDefinitionType relationshipDefinitionType = null;
            try
            {
                relationshipDefinitionType = (CmisTypeRelationshipDefinitionType) getServicesFactory().getRepositoryService().getTypeDefinition(
                        new GetTypeDefinition(getAndAssertRepositoryId(), getAndAssertRelationshipTypeId())).getType();
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull("Relationship type definition is NULL", relationshipDefinitionType);
            if (relationshipDefinitionType.getAllowedTargetTypes() != null && relationshipDefinitionType.getAllowedTargetTypes().length > 0)
            {
                CmisTypeDefinitionType targetType = null;
                try
                {
                    targetType = getServicesFactory().getRepositoryService().getTypeDefinition(
                            new GetTypeDefinition(getAndAssertRepositoryId(), relationshipDefinitionType.getAllowedTargetTypes()[0])).getType();
                }
                catch (Exception e)
                {
                    fail(e.getMessage());
                }
                assertNotNull("Relationship terget type definition is NULL", targetType);
                relationshipTargetType = targetType;
            }
        }
        return relationshipTargetType;
    }

    protected boolean isVersioningAllowed()
    {
        if (documentTypeId == null)
        {
            getAndAssertDocumentTypeId();
        }
        return versioningAllowed;
    }

    protected boolean isContentStreamAllowed()
    {
        if (documentTypeId == null)
        {
            getAndAssertDocumentTypeId();
        }
        return contentStreamAllowed;
    }
    

    private static class AbstractConditionCalculator {
        public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType) {
            return enumeratedType.isCreatable();
        }
    }

    private static class ValidFolderTypeConditionCalculator extends AbstractConditionCalculator {
        @Override
        public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
        {
            return enumeratedType.isCreatable(); //TODO: enumeratedType.isFileable() when dictionary will be corrected  
        }
    }

    private static class BestDocumentTypeConditionCalculator extends AbstractConditionCalculator {
        @Override
        public boolean calculate(CmisTypeDefinitionType currentType, CmisTypeDefinitionType enumeratedType)
        {
            return currentType == null
            || (enumeratedType.isCreatable() && !currentType.isCreatable())
            || (currentType.isCreatable() && enumeratedType.isCreatable() && enumeratedType.isFileable() && !currentType.isFileable())
            || (currentType.isCreatable() && enumeratedType.isCreatable() && enumeratedType.isFileable() && currentType.isFileable()
                    && ((CmisTypeDocumentDefinitionType) enumeratedType).isVersionable() && !((CmisTypeDocumentDefinitionType)currentType).isVersionable())
            || (currentType.isCreatable() && enumeratedType.isCreatable() && enumeratedType.isFileable() && currentType.isFileable()
                    && !EnumContentStreamAllowed.notallowed.equals(((CmisTypeDocumentDefinitionType) enumeratedType).getContentStreamAllowed()) && EnumContentStreamAllowed.notallowed
                    .equals(((CmisTypeDocumentDefinitionType)currentType).getContentStreamAllowed()))
            || (currentType.isCreatable() && enumeratedType.isCreatable() && enumeratedType.isFileable() && currentType.isFileable()
                    && ((CmisTypeDocumentDefinitionType) enumeratedType).isVersionable() && ((CmisTypeDocumentDefinitionType)currentType).isVersionable()
                    && !EnumContentStreamAllowed.notallowed.equals(((CmisTypeDocumentDefinitionType) enumeratedType).getContentStreamAllowed())
                    && !EnumContentStreamAllowed.notallowed.equals(((CmisTypeDocumentDefinitionType)currentType).getContentStreamAllowed()) && enumeratedType.isQueryable() && !currentType.isQueryable());
        }
    }
}
