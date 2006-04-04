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
package org.alfresco.benchmark.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.benchmark.dataloader.DataLoaderComponent;
import org.alfresco.benchmark.dataloader.LoadedData;
import org.alfresco.benchmark.dataprovider.ContentData;
import org.alfresco.benchmark.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.dataprovider.PropertyProfile;
import org.alfresco.benchmark.dataprovider.RepositoryProfile;
import org.alfresco.benchmark.dataprovider.PropertyProfile.PropertyType;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class AlfrescoUtils
{
    private static ApplicationContext applicationContext;    
    
    private static List<PropertyProfile> contentPropertyProfiles;
    
    private static Map<String, String> testCaseOutputLocation = new HashMap<String, String>(5);
    private static Map<String, NodeRef> testCaseFolder = new HashMap<String, NodeRef>(5);
    private static Map<String, List<NodeRef>> testCaseFolders = new HashMap<String, List<NodeRef>>(5);
    private static Map<String, List<NodeRef>> testCaseContent = new HashMap<String, List<NodeRef>>(5);
    
    private static List<String> availableUsers;
    
    public static String OUTPUT_FOLDER = "./data/output/";
    
    // Model constants
    public static QName DC_PUBLISHER = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "publisher");
    public static QName DC_CONTRIBUTER = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "contributer");
    public static QName DC_TYPE = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "type");
    public static QName DC_IDENTIFIER = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "identifier");
    public static QName DC_DCSOURCE = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "dcsource");
    public static QName DC_COVERAGE = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "coverage");
    public static QName DC_RIGHTS = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "rights");
    public static QName DC_SUBJECT = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "subject");
    public static QName DC_AUTHOR = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "author");
    
    public static synchronized ApplicationContext getApplicationContext()
    {
        if (applicationContext == null)
        {
            applicationContext = new ClassPathXmlApplicationContext("classpath:alfresco/application-context.xml");
        }
        
        return applicationContext;
    }
    
    public static synchronized String getOutputFileLocation(TestCase testCase)
    {
        String location = testCaseOutputLocation.get(testCase.getName());
        if (location == null)
        {
            location = OUTPUT_FOLDER + "testCase_" + testCase.getName() + "_" + System.currentTimeMillis() + ".csv";
            testCaseOutputLocation.put(testCase.getName(), location);
        }
        return location;
    }
    
    public static synchronized NodeRef getTestCaseRootFolder(
            DataLoaderComponent dataLoaderComponent,
            NodeService nodeService, 
            RepositoryProfile repositoryProfile, 
            TestCase testCase)
    {
        NodeRef result = testCaseFolder.get(testCase.getName());
        if (result == null)
        {
            LoadedData loadedData = dataLoaderComponent.loadData(repositoryProfile);
            
            List<NodeRef> folders = new ArrayList<NodeRef>(50);
            List<NodeRef> content = new ArrayList<NodeRef>(200);
            getFolderAndContentLists(nodeService, folders, content, loadedData.getRootFolder());
            
            testCaseFolder.put(testCase.getName(), loadedData.getRootFolder());
            testCaseFolders.put(testCase.getName(), folders);
            testCaseContent.put(testCase.getName(), content);
            result = loadedData.getRootFolder();
        }
        return result;
    }
   
    public static synchronized NodeRef getRandomFolder(TestCase testCase)
    {
        List<NodeRef> folders = testCaseFolders.get(testCase.getName());
        if (folders == null)
        {
            throw new RuntimeException("The test case folders list has not been populated");
        }
        int randValue = RandUtils.rand.nextInt(folders.size());
        return folders.get(randValue);
    }
    
    public static synchronized NodeRef getRandomContent(TestCase testCase)
    {
        List<NodeRef> content = testCaseContent.get(testCase.getName());
        if (content == null)
        {
            throw new RuntimeException("The test case content list has not been populated");
        }
        int randValue = RandUtils.rand.nextInt(content.size());
        return content.get(randValue);
    }
    
    private static void getFolderAndContentLists(NodeService nodeService, List<NodeRef> folders, List<NodeRef> content, NodeRef folder)
    {
        folders.add(folder);
        
        for (ChildAssociationRef childAssoc : nodeService.getChildAssocs(folder))
        {
            NodeRef childNodeRef = childAssoc.getChildRef();
            if (nodeService.getType(childNodeRef).toString().contains("folder") == true)
            {
                getFolderAndContentLists(nodeService, folders, content, childNodeRef);
            }
            else if (nodeService.getType(childNodeRef).toString().contains("content") == true)
            {
                content.add(childNodeRef);
            }
        }         
    }
    
    public static NodeRef getCompanyHomeNodeRef(SearchService searchService, StoreRef storeRef)
    {
        // Get the company home node
        ResultSet rs = searchService.query(storeRef, SearchService.LANGUAGE_XPATH, "/app:company_home");
        return rs.getNodeRef(0);
    }
    
    public static synchronized List<PropertyProfile> getContentPropertyProfiles()
    {
        if (contentPropertyProfiles == null)
        {
            // Prepare the property profile data
            contentPropertyProfiles = new ArrayList<PropertyProfile>();
            
            // cm:content properties
            PropertyProfile name = new PropertyProfile();
            name.setPropertyName(ContentModel.PROP_NAME.toString());
            name.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(name);
            PropertyProfile content = new PropertyProfile();
            content.setPropertyName(ContentModel.PROP_CONTENT.toString());
            content.setPropertyType(PropertyType.CONTENT);
            contentPropertyProfiles.add(content);
            
            // cm:dublincore properties
            PropertyProfile publisher = new PropertyProfile();
            publisher.setPropertyName(AlfrescoUtils.DC_PUBLISHER.toString());
            publisher.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(publisher);
            PropertyProfile contributer = new PropertyProfile();
            contributer.setPropertyName(AlfrescoUtils.DC_CONTRIBUTER.toString());
            contributer.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(contributer);
            PropertyProfile type = new PropertyProfile();
            type.setPropertyName(AlfrescoUtils.DC_TYPE.toString());
            type.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(type);
            PropertyProfile identifier = new PropertyProfile();
            identifier.setPropertyName(AlfrescoUtils.DC_IDENTIFIER.toString());
            identifier.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(identifier);
            PropertyProfile dcsource = new PropertyProfile();
            dcsource.setPropertyName(AlfrescoUtils.DC_DCSOURCE.toString());
            dcsource.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(dcsource);
            PropertyProfile coverage = new PropertyProfile();
            coverage.setPropertyName(AlfrescoUtils.DC_COVERAGE.toString());
            coverage.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(coverage);
            PropertyProfile rights = new PropertyProfile();
            rights.setPropertyName(AlfrescoUtils.DC_RIGHTS.toString());
            rights.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(rights);
            PropertyProfile subject = new PropertyProfile();
            subject.setPropertyName(AlfrescoUtils.DC_SUBJECT.toString());
            subject.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(subject);
            PropertyProfile author = new PropertyProfile();
            author.setPropertyName(AlfrescoUtils.DC_AUTHOR.toString());
            author.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(author);
            PropertyProfile title = new PropertyProfile();
            title.setPropertyName(ContentModel.PROP_TITLE.toString());
            title.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(title);
            PropertyProfile description = new PropertyProfile();
            description.setPropertyName(ContentModel.PROP_DESCRIPTION.toString());
            description.setPropertyType(PropertyType.TEXT);
            contentPropertyProfiles.add(description);
        }
        
        return contentPropertyProfiles;
    }
    
    public static NodeRef createFolderNode(DataProviderComponent dataProviderComponent, NodeService nodeService, RepositoryProfile repositoryProfile, NodeRef folderNodeRef)
    {
        return createFolderNode(dataProviderComponent, nodeService, repositoryProfile, folderNodeRef, null);
    }
        
    public static NodeRef createFolderNode(DataProviderComponent dataProviderComponent, NodeService nodeService, RepositoryProfile repositoryProfile, NodeRef folderNodeRef, String nameValue)
    {
        // Get folder property data
        List<PropertyProfile> folderPropertyProfiles = new ArrayList<PropertyProfile>();
        
        if (nameValue == null)
        {
            PropertyProfile name = new PropertyProfile();
            name.setPropertyName(ContentModel.PROP_NAME.toString());
            name.setPropertyType(PropertyType.TEXT);
            folderPropertyProfiles.add(name);
            Map<String, Object> propertyValues = dataProviderComponent.getPropertyData(
                    repositoryProfile, 
                    folderPropertyProfiles);
            
            nameValue = (String)propertyValues.get(ContentModel.PROP_NAME.toString());
        }
        Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>();
        folderProps.put(ContentModel.PROP_NAME, nameValue);
        return nodeService.createNode(
                folderNodeRef, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.APP_MODEL_1_0_URI, nameValue),
                ContentModel.TYPE_FOLDER,
                folderProps).getChildRef();
    }
    
    public static NodeRef createContentNode(DataProviderComponent dataProviderComponent, NodeService nodeService, ContentService contentService, RepositoryProfile repositoryProfile, NodeRef folderNodeRef)
    {        
        // Get the test data                    
        Map<String, Object> propertyValues = dataProviderComponent.getPropertyData(
                                                                        repositoryProfile, 
                                                                        AlfrescoUtils.getContentPropertyProfiles());

        return createContentNode(nodeService, contentService, propertyValues, folderNodeRef);   
    }
    
    public static NodeRef createContentNode(NodeService nodeService, ContentService contentService, Map<String, Object> propertyValues, NodeRef folderNodeRef)
    {
        // Create a new node at the root of the store
        ContentData contentData = (ContentData)propertyValues.get(ContentModel.PROP_CONTENT.toString());
        String name = contentData.getName();
        
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(ContentModel.PROP_NAME, (Serializable)name);
        NodeRef newNodeRef = nodeService.createNode(
                folderNodeRef, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                ContentModel.TYPE_CONTENT,
                properties).getChildRef();
        
        // Add the dublin core aspect and properties
        Map<QName, Serializable> dublinCoreProps = new HashMap<QName, Serializable>(8);
        dublinCoreProps.put(AlfrescoUtils.DC_PUBLISHER, (Serializable)propertyValues.get(AlfrescoUtils.DC_PUBLISHER.toString()));
        dublinCoreProps.put(AlfrescoUtils.DC_CONTRIBUTER, (Serializable)propertyValues.get(AlfrescoUtils.DC_CONTRIBUTER.toString()));
        dublinCoreProps.put(AlfrescoUtils.DC_TYPE, (Serializable)propertyValues.get(AlfrescoUtils.DC_TYPE.toString()));
        dublinCoreProps.put(AlfrescoUtils.DC_IDENTIFIER, (Serializable)propertyValues.get(AlfrescoUtils.DC_IDENTIFIER.toString()));
        dublinCoreProps.put(AlfrescoUtils.DC_DCSOURCE, (Serializable)propertyValues.get(AlfrescoUtils.DC_DCSOURCE.toString()));
        dublinCoreProps.put(AlfrescoUtils.DC_COVERAGE, (Serializable)propertyValues.get(AlfrescoUtils.DC_COVERAGE.toString()));
        dublinCoreProps.put(AlfrescoUtils.DC_RIGHTS, (Serializable)propertyValues.get(AlfrescoUtils.DC_RIGHTS.toString()));
        dublinCoreProps.put(AlfrescoUtils.DC_SUBJECT, (Serializable)propertyValues.get(AlfrescoUtils.DC_SUBJECT.toString()));
        dublinCoreProps.put(ContentModel.PROP_TITLE, (Serializable)propertyValues.get(ContentModel.PROP_TITLE.toString()));
        dublinCoreProps.put(ContentModel.PROP_DESCRIPTION, (Serializable)propertyValues.get(ContentModel.PROP_DESCRIPTION.toString()));
        nodeService.addAspect(newNodeRef, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "dublincore"), dublinCoreProps);
        
        // Set the content
        ContentWriter contentWriter = contentService.getWriter(newNodeRef, ContentModel.PROP_CONTENT, true);                    
        contentWriter.setEncoding(contentData.getEncoding());
        contentWriter.setMimetype(contentData.getMimetype());
        contentWriter.putContent(contentData.getFile());

        return newNodeRef;
    }
    
    private static Object mutex = new Object();
    
    public static List<String> prepairUsers(
            DataLoaderComponent dataLoaderComponent, 
            PersonService personService, 
            NodeService nodeService,
            int numberOfUsers)
    {
        synchronized (mutex)
        {
            if (availableUsers == null)
            {
                // Create the list and populate from the repository (exclude 'admin')
                availableUsers = new ArrayList<String>();
                Set<NodeRef> people = personService.getAllPeople();
                for (NodeRef person : people)
                {
                    String currentUserName = (String)nodeService.getProperty(person, ContentModel.PROP_USERNAME);
                    if (currentUserName.startsWith("bm") == true)
                    {
                        availableUsers.add(currentUserName);
                    }
                }                
            }
            
            int numberOfAvilableUsers = availableUsers.size();
            if (numberOfAvilableUsers < numberOfUsers)
            {
                // Create some new users and add them to the available list
                List<String> newUsers = dataLoaderComponent.createUsers(numberOfUsers - numberOfAvilableUsers);
                availableUsers.addAll(newUsers);
            }
            
            return availableUsers;
        }
    }
    
    public static String getUserName()
    {
        synchronized (mutex)
        {
            String userName = null;
            
            try
            {
                if (availableUsers == null || availableUsers.size() == 0)                    
                {
                    throw new RuntimeException("Run out of users, increase number of test users available");
                }
                
                int randIndex = RandUtils.rand.nextInt(availableUsers.size());
                userName = availableUsers.get(randIndex);
                availableUsers.remove(randIndex);                            
            }
            catch (Throwable exception)
            {
                exception.printStackTrace();
            }
            
            return userName;
        }
    }
    
    public static void releaseUserName(String userName)
    {
        synchronized (mutex)
        {
            if (availableUsers != null)
            {
                availableUsers.add(userName);
            }
        }
    }

    
}
