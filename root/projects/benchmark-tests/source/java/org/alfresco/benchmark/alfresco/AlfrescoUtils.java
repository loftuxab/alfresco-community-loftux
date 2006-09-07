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
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
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
    
    private static NodeRef rootTestDataFolder;
    
    /**
     * Get a list of the available test data root folders
     * 
     * @param searchService
     * @param nodeService
     * @return
     */
    public static synchronized NodeRef getRootTestDataFolder(SearchService searchService, NodeService nodeService)
    {
        if (rootTestDataFolder == null)
        {
            NodeRef companyHome = getCompanyHomeNodeRef(searchService, storeRef);
            
            List<ChildAssociationRef> assocs = nodeService.getChildAssocs(companyHome, RegexQNamePattern.MATCH_ALL, new RegexQNamePattern(NamespaceService.APP_MODEL_1_0_URI, AlfrescoDataLoaderComponentImpl.BENCHMARK_OBJECT_PREFIX + ".*"));
            if (assocs.size() == 0)
            {
                throw new RuntimeException("ERROR:  There is no test data available to execute the benchmark tests against.  Please execute the load-benchmark-data ant task.");
            }
            
            // Get the folder node ref
            rootTestDataFolder = assocs.get(0).getChildRef();
            
            if (assocs.size() > 1)
            {
                System.out.println("WARNING:  More that one root test data folder has been found.  Using '" + nodeService.getProperty(rootTestDataFolder, ContentModel.PROP_NAME) + "'");
            }                
        }
        
        return rootTestDataFolder;
    }
    
    private static RepositoryProfile repositoryProfile;
    
    public static RepositoryProfile getRepositoryProfile(
            final AuthenticationComponent authenticationComponent, 
            final SearchService searchService, 
            final NodeService nodeService)
    {
        if (repositoryProfile == null)
        {
            AuthenticationUtil.runAs(new RunAsWork<Object>()
            {
                public Object doWork() throws Exception
                {
                    NodeRef rootNodeRef = getRootTestDataFolder(searchService, nodeService);
                    String repositoryProfileValue = (String)nodeService.getProperty(rootNodeRef, QName.createQName("http://www.alfresco.org/model/benchmark/1.0", "repositoryProfile"));
                    repositoryProfile = new RepositoryProfile(repositoryProfileValue);
                    return null;
                }
                
            }, AuthenticationUtil.getSystemUserName());
        }
        return repositoryProfile;
    }
    
    /**
     * Get the company home node reference
     * 
     * @param searchService
     * @param storeRef
     * @return
     */
    public static NodeRef getCompanyHomeNodeRef(SearchService searchService, StoreRef storeRef)
    {
        // Get the company home node
        ResultSet rs = searchService.query(storeRef, SearchService.LANGUAGE_XPATH, "/app:company_home");
        return rs.getNodeRef(0);
    }
    
    /**
     * Get a list of the property profiles for a content node
     * 
     * @return
     */
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
    
    /**
     * Create a folder node
     * ]
     * @param nodeService
     * @param folderNodeRef
     * @return
     */
    public static NodeRef createFolderNode(NodeService nodeService, NodeRef folderNodeRef)
    {
        return createFolderNode(nodeService, folderNodeRef, null);
    }
        
    /**
     * Create a folder node
     * 
     * @param nodeService
     * @param folderNodeRef
     * @param nameValue
     * @return
     */
    public static NodeRef createFolderNode(NodeService nodeService, NodeRef folderNodeRef, String nameValue)
    {
        // Get folder property data
        List<PropertyProfile> folderPropertyProfiles = new ArrayList<PropertyProfile>();
        
        if (nameValue == null)
        {
            folderPropertyProfiles.add(PropertyProfile.createSmallTextProperty(ContentModel.PROP_NAME.toString()));
            Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(
                    folderPropertyProfiles);
            
            nameValue = (String)propertyValues.get(ContentModel.PROP_NAME.toString());
        }
        
        Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>();
        folderProps.put(ContentModel.PROP_NAME, nameValue);
        return nodeService.createNode(
                folderNodeRef, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, nameValue),
                ContentModel.TYPE_FOLDER,
                folderProps).getChildRef();
    }
    
    public static NodeRef createContentNode(NodeService nodeService, ContentService contentService, NodeRef folderNodeRef)
    {
        return createContentNode(nodeService, contentService, folderNodeRef, null);
    }
    
    /**
     * Create a content node
     * 
     * @param nodeService
     * @param contentService
     * @param folderNodeRef
     * @return
     */
    public static NodeRef createContentNode(NodeService nodeService, ContentService contentService, NodeRef folderNodeRef, String fileName)
    {        
        // Get the test data                    
        Map<String, Object> propertyValues = DataProviderComponent.getInstance().getPropertyData(
                                                                        AlfrescoUtils.getContentPropertyProfiles());

        return createContentNode(nodeService, contentService, propertyValues, folderNodeRef, true, fileName);   
    }
    
    /**
     * Create a content node
     * 
     * @param nodeService
     * @param contentService
     * @param propertyValues
     * @param folderNodeRef
     * @return
     */
    public static NodeRef createContentNode(NodeService nodeService, ContentService contentService, Map<String, Object> propertyValues, NodeRef folderNodeRef)
    {
        return createContentNode(nodeService, contentService, propertyValues, folderNodeRef, true, null);
    }
    
    /**
     * Create a content node
     * 
     * @param nodeService
     * @param contentService
     * @param propertyValues
     * @param folderNodeRef
     * @param addAspect
     * @return
     */
    public static NodeRef createContentNode(NodeService nodeService, ContentService contentService, Map<String, Object> propertyValues, NodeRef folderNodeRef, boolean addAspect)
    {
        return createContentNode(nodeService, contentService, propertyValues, folderNodeRef, true, null);
    }
    
    /**
     * Create a content node
     * 
     * @param nodeService
     * @param contentService
     * @param propertyValues
     * @param folderNodeRef
     * @param addAspect
     * @param contentName
     * @return
     */
    public static NodeRef createContentNode(NodeService nodeService, ContentService contentService, Map<String, Object> propertyValues, NodeRef folderNodeRef, boolean addAspect, String contentName)
    {
        // Create a new node at the root of the store
        ContentData contentData = (ContentData)propertyValues.get(ContentModel.PROP_CONTENT.toString());
        
        String name = null;
        if (contentName == null)
        {
            name = contentData.getName();
        }
        else
        {
            name = contentName;
        }
        
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
    
    /**
     * Prepare the users ready for the benchmark tests.
     * 
     * @param dataLoaderComponent
     * @param personService
     * @param nodeService
     * @param numberOfUsers
     * @return
     */
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
    
    /**
     * Get a user that can be used with a benchmark test.  This removes a user from the available user list, releaseUserName must be called 
     * to ensure the tests do not run out of avilable users.
     * 
     * @return
     */
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
    
    /**
     * Releases a user name once the benchmark test has finished with it.
     * 
     * @param userName
     */
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
