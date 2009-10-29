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

import java.math.BigInteger;
import java.util.LinkedList;

import org.alfresco.repo.cmis.ws.CmisFaultType;
import org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType;
import org.alfresco.repo.cmis.ws.CmisRepositoryEntryType;
import org.alfresco.repo.cmis.ws.CmisRepositoryInfoType;
import org.alfresco.repo.cmis.ws.CmisTypeContainer;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypePolicyDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypeRelationshipDefinitionType;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.GetRepositories;
import org.alfresco.repo.cmis.ws.GetRepositoryInfo;
import org.alfresco.repo.cmis.ws.GetRepositoryInfoResponse;
import org.alfresco.repo.cmis.ws.GetTypeChildren;
import org.alfresco.repo.cmis.ws.GetTypeChildrenResponse;
import org.alfresco.repo.cmis.ws.GetTypeDefinition;
import org.alfresco.repo.cmis.ws.GetTypeDescendants;
import org.alfresco.repo.cmis.ws.RepositoryServicePortBindingStub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Repository Service
 * 
 * @author Dmitry Velichkevich
 */
public class CmisRepositoryServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisRepositoryServiceClient.class);

    private static final String WRONG_TYPE_ID = "Wrong TypeId Parameter";

    private static final String INVALID_REPOSITORY_ID = "Wrong Repository Id";

    private static final String PROPERTY_DEFINITIONS_NOT_RETURNED_MESSAGE_PATTERN = "Property Definitions for \"%s\" Type Definitions was not returned in request with returnPropertyDefinitions=TRUE";
    private static final String PROPERTY_DEFINITIONS_RETURNED_MESSAGE_PATTERN = "Property Definitions for \"%s\" Type Definitions was not returned in request with returnPropertyDefinitions=FALSE";
    private static final String INVALID_EXCEPTION_MESSAGE = "Invalid exception was thrown. Expected: invalidArgument, was: ";
    private static final String BASE_DOCUMENT_TYPE_NOT_FOUND_MESSAGE = "Base Document type definition was not found";

    public CmisRepositoryServiceClient()
    {
    }

    public CmisRepositoryServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
    }

    /**
     * Invokes all methods in Repository Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        RepositoryServicePortBindingStub repositoryService = getServicesFactory().getRepositoryService(getProxyUrl() + getService().getPath());

        CmisRepositoryEntryType[] repositories = repositoryService.getRepositories(new GetRepositories());

        GetRepositoryInfo getRepositoryInfo = new GetRepositoryInfo();
        getRepositoryInfo.setRepositoryId(repositories[0].getId());
        repositoryService.getRepositoryInfo(getRepositoryInfo);

        String typeId = repositoryService.getTypeDescendants(new GetTypeDescendants(repositories[0].getId(), null, BigInteger.valueOf(-1), true))[0].getType().getId();
        repositoryService.getTypeChildren(new GetTypeChildren(repositories[0].getId(), typeId, true, BigInteger.ZERO, BigInteger.ZERO)).getType()[0].getId();

        repositoryService.getTypeDefinition(new GetTypeDefinition(repositories[0].getId(), typeId));
    }

    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
        AbstractServiceClient client = (CmisRepositoryServiceClient) applicationContext.getBean("cmisRepositoryServiceClient");
        try
        {
            client.initialize();
            client.invoke();
            client.release();
        }
        catch (Exception e)
        {
            LOGGER.error("Some error occured during client running. Exception message: " + e.getMessage());
        }
    }

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        super.onTearDown();
    }

    public void testGetRepositories()
    {
        CmisRepositoryEntryType[] repositories = null;
        try
        {
            LOGGER.info("[RepositoryService->getRepositories]");
            repositories = getServicesFactory().getRepositoryService().getRepositories(new GetRepositories());
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("GetRepositories response is NULL", repositories);
        assertTrue("GetRepositories response is empty", repositories.length > 0);
        assertNotNull("GetRepositories response is empty", repositories[0]);
    }

    public void testGetRepositoryInfo()
    {
        GetRepositoryInfoResponse getInfo = null;
        try
        {
            LOGGER.info("[RepositoryService->getRepositoryInfo]");
            getInfo = getServicesFactory().getRepositoryService().getRepositoryInfo(new GetRepositoryInfo(getAndAssertRepositoryId()));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("GetRepositoryInfo response is NULL", getInfo);
        CmisRepositoryInfoType repositoryInfo = getInfo.getRepositoryInfo();
        assertEquals("Repository id is not valid", getAndAssertRepositoryId(), repositoryInfo.getRepositoryId());
        assertNotNull("Repository name is NULL", repositoryInfo.getRepositoryName());
        assertNotNull("Repository relationship is NULL", repositoryInfo.getRepositoryRelationship());
        assertNotNull("Repository description is NULL", repositoryInfo.getRepositoryDescription());
        assertNotNull("Repository product name is NULL", repositoryInfo.getProductName());
        assertNotNull("Repository vendor name is NULL", repositoryInfo.getVendorName());
        assertNotNull("Repository product version is NULL", repositoryInfo.getProductVersion());
        assertNotNull("Repository root folder id is NULL", repositoryInfo.getRootFolderId());
        assertFalse("Repository root folder id is empty", "".equals(repositoryInfo.getRootFolderId()));
        // FIXME: uncomment this when changeToken concept will be resolved
        // assertNotNull(repositoryInfo.getLatestChangeToken());
        assertNotNull("Repository version supported is NULL", repositoryInfo.getCmisVersionSupported());
        assertNotNull("Repository thin client URI is NULL", repositoryInfo.getThinClientURI());
        // FIXME: uncomment this when changesIncomplete retrieving API will be added
        // assertNotNull(repositoryInfo.getChangesIncomplete());
        // FIXME: uncomment this when aclCapability will be added
        // assertNotNull(repositoryInfo.getAclCapability());

        CmisRepositoryCapabilitiesType capabilities = repositoryInfo.getCapabilities();
        assertNotNull("Repository capabilities are NULL", capabilities);
        assertNotNull("Repository capabilityACL is NULL", capabilities.getCapabilityACL());
        assertNotNull("Repository capabilityChanges is NULL", capabilities.getCapabilityChanges());
        // FIXME: uncomment this when capabilityChangesOnType capability will be added
        // assertNotNull(capabilities.getCapabilityChangesOnType());
        assertNotNull("Repository capabilityContentStreamUpdatability is NULL", capabilities.getCapabilityContentStreamUpdatability());
        assertNotNull("Repository capabilityJoin is NULL", capabilities.getCapabilityJoin());
        assertNotNull("Repository capabilityQuery is NULL", capabilities.getCapabilityQuery());
        assertNotNull("Repository capabilityRenditions is NULL", capabilities.getCapabilityRenditions());
    }

    public void testGetTypeChildren()
    {
        getAndAssertTypeChildren(null, true, 0, 0);
    }

    public void testGetTypeChildrenWithTypeIdInNotSetAndSetState()
    {
        GetTypeChildrenResponse response = getAndAssertTypeChildren(null, false, 0, 0);
        assertEquals("Invalid type amount was returned", 4, response.getType().length);
        String typeId = getBaseDocumentTypeId(response);
        assertNotNull(BASE_DOCUMENT_TYPE_NOT_FOUND_MESSAGE, typeId);
        response = getAndAssertTypeChildren(typeId, false, 10, 0);
    }

    public void testGetTypeChildrenPagination()
    {
        GetTypeChildrenResponse response = getAndAssertTypeChildren(null, false, 3, 0);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is NULL", response.isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", response.isHasMoreItems() && (3 == response.getType().length));

        response = getAndAssertTypeChildren(null, false, 0, 0);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is NULL", response.isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", !response.isHasMoreItems() && (4 == response.getType().length));
        String typeId = null;
        for (CmisTypeDefinitionType typeDef : response.getType())
        {
            if (!(typeDef instanceof CmisTypePolicyDefinitionType) && !(typeDef instanceof CmisTypeRelationshipDefinitionType))
            {
                typeId = typeDef.getId();
                break;
            }
        }
        assertNotNull("Type id is NULL", typeId);

        response = getAndAssertTypeChildren(null, false, 0, 1);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is NULL", response.isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", !response.isHasMoreItems() && (3 == response.getType().length));

        response = getAndAssertTypeChildren(null, false, 2, 1);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is NULL", response.isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", response.isHasMoreItems() && (2 == response.getType().length));

        response = getAndAssertTypeChildren(typeId, false, 10, 0);
        assertNotNull("GetTypeChildren response property 'hasMoreItems' is NULL", response.isHasMoreItems());
        assertTrue("GetTypeChildren response property 'hasMoreItems' is invalid", (response.isHasMoreItems() && (10 == response.getType().length)) || (!response.isHasMoreItems() && (response.getType().length <= 10)));
    }

    public void testGetTypeChildrenWithPropertyDefinitions() throws Exception
    {
        validateAllTypesOnValidProperties(getAndAssertTypeChildren(null, true, 0, 0), true);
        validateAllTypesOnValidProperties(getAndAssertTypeChildren(null, false, 0, 0), false);
    }

    public void testGetTypeChildrenWrongTypeId()
    {
        try
        {
            LOGGER.info("[RepositoryService->getTypeChildren]");
            getServicesFactory().getRepositoryService().getTypeChildren(
                    new GetTypeChildren(getAndAssertRepositoryId(), "Wrong Type id", false, BigInteger.valueOf(10), BigInteger.valueOf(0)));
            fail("No Exception was thrown during getting type children for wrong typeI");
        }
        catch (Exception e)
        {
            assertTrue("Invalid exception was thrown during getting type children for wrong typeId", e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.invalidArgument));
        }
    }

    private void validateAllTypesOnValidProperties(GetTypeChildrenResponse response, boolean expectedConditionValue)
    {
        for (CmisTypeDefinitionType typeDef : response.getType())
        {
            if (expectedConditionValue != propertiesDefinitionIsValid(typeDef, expectedConditionValue))
            {
                fail(String.format(((expectedConditionValue) ? (PROPERTY_DEFINITIONS_NOT_RETURNED_MESSAGE_PATTERN) : (PROPERTY_DEFINITIONS_RETURNED_MESSAGE_PATTERN)), typeDef
                        .getId()));
            }
        }
    }

    private boolean propertiesDefinitionIsValid(CmisTypeDefinitionType typeDef, boolean trueExpected)
    {
        // FIXME: remove this condition checking when policy type definitions will be corrected
        if (typeDef instanceof CmisTypePolicyDefinitionType)
        {
            return trueExpected;
        }
        if (((null == typeDef.getPropertyBooleanDefinition()) || (typeDef.getPropertyBooleanDefinition().length < 1))
                && ((null == typeDef.getPropertyDateTimeDefinition()) || (typeDef.getPropertyDateTimeDefinition().length < 1))
                && ((null == typeDef.getPropertyDecimalDefinition()) || (typeDef.getPropertyDecimalDefinition().length < 1))
                && ((null == typeDef.getPropertyHtmlDefinition()) || (typeDef.getPropertyHtmlDefinition().length < 1))
                && ((null == typeDef.getPropertyIdDefinition()) || (typeDef.getPropertyIdDefinition().length < 1))
                && ((null == typeDef.getPropertyIntegerDefinition()) || (typeDef.getPropertyIntegerDefinition().length < 1))
                && ((null == typeDef.getPropertyStringDefinition()) || (typeDef.getPropertyStringDefinition().length < 1))
                && ((null == typeDef.getPropertyUriDefinition()) || (typeDef.getPropertyUriDefinition().length < 1))
                && ((null == typeDef.getPropertyXhtmlDefinition()) || (typeDef.getPropertyXhtmlDefinition().length < 1))
                && ((null == typeDef.getPropertyXmlDefinition()) || (typeDef.getPropertyXmlDefinition().length < 1)))
        {
            return false;
        }
        return true;
    }

    public void testGetTypeDescendants() throws Exception
    {
        getAndAssertTypeDescendants(null, -1, false);
    }

    public void testGetTypeDescendantsWithPropertyDefinitions() throws Exception
    {
        validateAllTypeContainersOnValidPropertiesDefinitions(getAndAssertTypeDescendants(null, -1, true), true);
        validateAllTypeContainersOnValidPropertiesDefinitions(getAndAssertTypeDescendants(null, -1, false), false);
    }

    private void validateAllTypeContainersOnValidPropertiesDefinitions(CmisTypeContainer[] rootContainers, boolean expectedConditionValue)
    {
        LinkedList<CmisTypeContainer> containerList = new LinkedList<CmisTypeContainer>();
        addContainers(containerList, rootContainers);
        for (CmisTypeContainer container = containerList.getFirst(); !containerList.isEmpty(); containerList.removeFirst(), container = (!containerList.isEmpty()) ? (containerList
                .getFirst()) : (null))
        {
            if (null == container)
            {
                continue;
            }

            assertNotNull("Invalid Type Descendants response: one of the Type Containers' Type is undefined", container.getType());
            if (expectedConditionValue != propertiesDefinitionIsValid(container.getType(), expectedConditionValue))
            {
                fail(String.format(((expectedConditionValue) ? (PROPERTY_DEFINITIONS_NOT_RETURNED_MESSAGE_PATTERN) : (PROPERTY_DEFINITIONS_RETURNED_MESSAGE_PATTERN)), container
                        .getType().getId()));
            }

            if (null != container.getChildren())
            {
                addContainers(containerList, container.getChildren());
            }
        }
    }

    public void testGetTypeDescendantsDepthing() throws Exception
    {
        long allTypesAmount = calculateTypesAmount(getAndAssertTypeDescendants(null, -1, false));
        String typeId = getBaseDocumentTypeId(getAndAssertTypeChildren(null, false, 0, 0));
        assertNotNull(typeId);
        long documentTypesAmount = calculateTypesAmount(getAndAssertTypeDescendants(typeId, -1, false));
        long toConcreateDepthDocumentTypesAmount = calculateTypesAmount(getAndAssertTypeDescendants(typeId, 2, false));

        assertTrue(allTypesAmount > 0);
        assertTrue(documentTypesAmount > 0);
        assertTrue(toConcreateDepthDocumentTypesAmount > 0);
        assertTrue(documentTypesAmount < allTypesAmount);
        assertTrue(toConcreateDepthDocumentTypesAmount < allTypesAmount);
        assertTrue(toConcreateDepthDocumentTypesAmount < documentTypesAmount);
    }

    private long calculateTypesAmount(CmisTypeContainer[] rootContainers)
    {
        LinkedList<CmisTypeContainer> containerList = new LinkedList<CmisTypeContainer>();
        addContainers(containerList, rootContainers);
        long result = 0;
        for (CmisTypeContainer container = containerList.getFirst(); !containerList.isEmpty(); containerList.removeFirst(), container = (!containerList.isEmpty()) ? (containerList
                .getFirst()) : (null))
        {
            if (null == container)
            {
                continue;
            }

            assertNotNull("Invalid Type Descendants response: one of the Type Containers' Type is undefined", container.getType());
            result++;

            if (null != container.getChildren())
            {
                addContainers(containerList, container.getChildren());
            }
        }
        return result;
    }

    public void testGetTypeDescendantsWithTypeIdNotSetAndSetParameterState() throws Exception
    {
        String typeId = getBaseDocumentTypeId(getAndAssertTypeChildren(null, true, 0, 0));
        assertNotNull("Base document type id is NULL");
        CmisTypeContainer[] documentTypes = getAndAssertTypeDescendants(typeId, -1, true);

        LinkedList<CmisTypeContainer> typesList = new LinkedList<CmisTypeContainer>();
        addContainers(typesList, documentTypes);
        for (CmisTypeContainer container = typesList.getFirst(); !typesList.isEmpty(); typesList.removeFirst(), container = (!typesList.isEmpty()) ? (typesList.getFirst())
                : (null))
        {
            if (null == container)
            {
                continue;
            }

            CmisTypeDefinitionType type = container.getType();
            assertNotNull("One of returned type definition type is NULL", type);
            if (!typeId.equals(type.getBaseTypeId().getValue()))
            {
                fail("Type Children Response with concreate TypeId contains odd Type Definition. Expected: \"" + typeId + "\", actual: \"" + type.getBaseTypeId() + "\"");
            }

            if (null != container.getChildren())
            {
                addContainers(typesList, container.getChildren());
            }
        }
    }

    public void testGetTypeDescendantsWithWrongTypeIdParameter() throws Exception
    {
        try
        {
            LOGGER.info("[RepositoryService->getTypeDescendants]");
            getServicesFactory().getRepositoryService().getTypeDescendants(new GetTypeDescendants(getAndAssertRepositoryId(), WRONG_TYPE_ID, BigInteger.valueOf(-1), false));
            fail("Get Type Descendants service has processed Invalid TypeId as valid TypeId");
        }
        catch (Exception e)
        {
            if (!(e instanceof CmisFaultType) || (null == ((CmisFaultType) e).getType()) || (null == ((CmisFaultType) e).getType().getValue()))
            {
                fail(INVALID_EXCEPTION_MESSAGE + e.toString());
            }
            assertTrue((INVALID_EXCEPTION_MESSAGE + ((CmisFaultType) e).getType().getValue()), EnumServiceException.invalidArgument.getValue().equals(
                    ((CmisFaultType) e).getType().getValue()));
        }
    }

    public void testGetTypeDefinitionWithWrongTypeId()
    {
        try
        {
            LOGGER.info("[RepositoryService->getTypeDefinition]");
            getServicesFactory().getRepositoryService().getTypeDefinition(new GetTypeDefinition(getAndAssertRepositoryId(), WRONG_TYPE_ID));
            fail("Get Type Definition service has processed Invalid TypeId as valid TypeId");
        }
        catch (Exception e)
        {
            if (!(e instanceof CmisFaultType) || (null == ((CmisFaultType) e).getType()) || (null == ((CmisFaultType) e).getType().getValue()))
            {
                fail(INVALID_EXCEPTION_MESSAGE + e.toString());
            }
            assertTrue((INVALID_EXCEPTION_MESSAGE + ((CmisFaultType) e).getType().getValue()), EnumServiceException.invalidArgument.getValue().equals(
                    ((CmisFaultType) e).getType().getValue()));
        }
    }

    public void testGetTypeDefinition() throws Exception
    {
        CmisTypeContainer[] getTypesResponse = getAndAssertTypeDescendants(null, 1, true);
        getAndAssertTypeDefinition(getTypesResponse[0].getType().getId());
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        try
        {
            LOGGER.info("[RepositoryService->getRepositoryInfo]");
            getServicesFactory().getRepositoryService().getRepositoryInfo(new GetRepositoryInfo(INVALID_REPOSITORY_ID));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
        }

        try
        {
            LOGGER.info("[RepositoryService->getTypeChildren]");
            getServicesFactory().getRepositoryService().getTypeChildren(new GetTypeChildren(INVALID_REPOSITORY_ID, null, false, BigInteger.valueOf(10), BigInteger.valueOf(0)));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
        }

        try
        {
            LOGGER.info("[RepositoryService->getTypeDescendants]");
            getServicesFactory().getRepositoryService().getTypeDescendants(new GetTypeDescendants(INVALID_REPOSITORY_ID, null, BigInteger.valueOf(1), true));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
        }

        try
        {
            CmisTypeContainer[] getTypesResponse = getAndAssertTypeDescendants(null, 1, true);
            LOGGER.info("[RepositoryService->getTypeDefinition]");
            getServicesFactory().getRepositoryService().getTypeDefinition(new GetTypeDefinition(INVALID_REPOSITORY_ID, getTypesResponse[0].getType().getId()));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
        }
    }
}
