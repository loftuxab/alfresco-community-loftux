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
package org.alfresco.benchmark.alfresco;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.benchmark.framework.BenchmarkUtils;
import org.alfresco.benchmark.framework.DataLoaderComponent;
import org.alfresco.benchmark.framework.dataprovider.ContentData;
import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile;
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;
import org.alfresco.benchmark.framework.dataprovider.PropertyProfile.PropertyType;
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
import org.alfresco.service.namespace.RegexQNamePattern;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Roy Wetherall
 */
public class AlfrescoUtils
{
    private static ApplicationContext applicationContext;    
    
    private static List<PropertyProfile> contentPropertyProfiles;
    
    //private static Map<String, NodeRef> testCaseFolder = new HashMap<String, NodeRef>(5);
    //private static Map<String, List<NodeRef>> testCaseFolders = new HashMap<String, List<NodeRef>>(5);
    //private static Map<String, List<NodeRef>> testCaseContent = new HashMap<String, List<NodeRef>>(5);
    
    private static List<String> availableUsers;    
    
    public static StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
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
    
    private static List<String> folders;
    private static List<String> content;
    
    @SuppressWarnings("unchecked")
    public static synchronized NodeRef getRandomFolder()
    {
        try
        {
            if (folders == null)
            {
                folders = (List<String>)new ObjectInputStream(new FileInputStream(BenchmarkUtils.getOutputFileLocation() + File.separator + "alf_loaded_folders.bin")).readObject();            
            }
            
            int size = folders.size();
            int rand = BenchmarkUtils.rand.nextInt(size);
            return new NodeRef(folders.get(rand));
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Unable to get random folder path", exception);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static synchronized NodeRef getRandomContent()
    {
        try
        {
            if (content == null)
            {
                content = (List<String>)new ObjectInputStream(new FileInputStream(BenchmarkUtils.getOutputFileLocation() + File.separator + "alf_loaded_content.bin")).readObject();            
            }
            
            int size = content.size();
            int rand = BenchmarkUtils.rand.nextInt(size);
            return new NodeRef(content.get(rand));
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Unable to get random content path", exception);
        }
    } 
    
    
    private static List<NodeRef> rootFolders;
    
    public static synchronized List<NodeRef> getRootFolders(SearchService searchService, NodeService nodeService)
    {
        if (rootFolders == null)
        {
            NodeRef companyHome = getCompanyHomeNodeRef(searchService, storeRef);
            
            List<ChildAssociationRef> assocs = nodeService.getChildAssocs(companyHome, RegexQNamePattern.MATCH_ALL, new RegexQNamePattern(NamespaceService.APP_MODEL_1_0_URI, AlfrescoDataLoaderComponentImpl.BENCHMARK_OBJECT_PREFIX + ".*"));
            if (assocs.size() == 0)
            {
                throw new RuntimeException("There is no benchmark data loaded in to the repository.");
            }
            rootFolders = new ArrayList<NodeRef>(assocs.size());
            for (ChildAssociationRef assoc : assocs)
            {
                rootFolders.add(assoc.getChildRef());
            }
        }
        return rootFolders;
    }
   
//    public static NodeRef getRandomFolder(SearchService searchService, NodeService nodeService)
//    {       
//        List<NodeRef> folders = new ArrayList<NodeRef>(); 
//        getRandomFolder(nodeService, getRootFolders(searchService, nodeService), folders);
//        NodeRef folder = folders.get(BenchmarkUtils.rand.nextInt(folders.size()));
//        return folder;
//    }
//    
//    private static void getRandomFolder(NodeService nodeService, List<NodeRef> folders, List<NodeRef> result)
//    {
//        int randIndex = BenchmarkUtils.rand.nextInt(folders.size());
//        NodeRef folder = folders.get(randIndex);
//        result.add(folder);
//
//        // Get the sub-folders of the folder
//        List<ChildAssociationRef> assocs = nodeService.getChildAssocs(folder);
//        List<NodeRef> subFolders = new ArrayList<NodeRef>(assocs.size());
//        for (ChildAssociationRef assoc : assocs)
//        {
//            NodeRef subFolder = assoc.getChildRef();
//            
//            if (nodeService.getType(subFolder).getLocalName().equals("folder") == true)
//            {
//                subFolders.add(subFolder);
//            }
//        }
//        
//        if (subFolders.size() != 0)
//        {
//            getRandomFolder(nodeService, subFolders, result);
//        }
//    }
//    
//    public static synchronized NodeRef getRandomContent(SearchService searchService, NodeService nodeService)
//    {
//        List<NodeRef> contentList = new ArrayList<NodeRef>();
//        
//        while (contentList.size() == 0)
//        {
//            NodeRef folder = getRandomFolder(searchService, nodeService);
//            
//            for (ChildAssociationRef assoc : nodeService.getChildAssocs(folder))
//            {
//                NodeRef child = assoc.getChildRef();
//                if (nodeService.getType(child).getLocalName().equals("content") == true)
//                {
//                    contentList.add(child);
//                }
//            }
//        }
//        
//        NodeRef content = contentList.get(BenchmarkUtils.rand.nextInt(contentList.size())); 
//        return content;
//    }
    
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
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(ContentModel.PROP_NAME.toString()));
            contentPropertyProfiles.add(new PropertyProfile(ContentModel.PROP_CONTENT.toString(), PropertyType.CONTENT));
            
            // cm:dublincore properties
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(AlfrescoUtils.DC_PUBLISHER.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(AlfrescoUtils.DC_CONTRIBUTER.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(AlfrescoUtils.DC_TYPE.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(AlfrescoUtils.DC_IDENTIFIER.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(AlfrescoUtils.DC_DCSOURCE.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(AlfrescoUtils.DC_COVERAGE.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(AlfrescoUtils.DC_RIGHTS.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(AlfrescoUtils.DC_SUBJECT.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(AlfrescoUtils.DC_AUTHOR.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(ContentModel.PROP_TITLE.toString()));
            contentPropertyProfiles.add(PropertyProfile.createSmallTextProperty(ContentModel.PROP_DESCRIPTION.toString()));
        }
        
        return contentPropertyProfiles;
    }
    
    public static NodeRef createFolderNode(NodeService nodeService, RepositoryProfile repositoryProfile, NodeRef folderNodeRef)
    {
        return createFolderNode(nodeService, repositoryProfile, folderNodeRef, null);
    }
        
    public static NodeRef createFolderNode(NodeService nodeService, RepositoryProfile repositoryProfile, NodeRef folderNodeRef, String nameValue)
    {
        // Get folder property data
        List<PropertyProfile> folderPropertyProfiles = new ArrayList<PropertyProfile>();
        
        if (nameValue == null)
        {
            folderPropertyProfiles.add(PropertyProfile.createSmallTextProperty(ContentModel.PROP_NAME.toString()));
            Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(
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
    
    public static NodeRef createContentNode(NodeService nodeService, ContentService contentService, RepositoryProfile repositoryProfile, NodeRef folderNodeRef)
    {        
        // Get the test data                    
        Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(
                                                                        repositoryProfile, 
                                                                        AlfrescoUtils.getContentPropertyProfiles());

        return createContentNode(nodeService, contentService, propertyValues, folderNodeRef);   
    }
    
    public static NodeRef createContentNode(NodeService nodeService, ContentService contentService, Map<String, Object> propertyValues, NodeRef folderNodeRef)
    {
        return createContentNode(nodeService, contentService, propertyValues, folderNodeRef, true);
    }
    
    public static NodeRef createContentNode(NodeService nodeService, ContentService contentService, Map<String, Object> propertyValues, NodeRef folderNodeRef, boolean addAspect)
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
        
        if (addAspect == true)
        {
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
        }
        
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
                
                int randIndex = BenchmarkUtils.rand.nextInt(availableUsers.size());
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
